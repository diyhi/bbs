package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.topic.TopicCacheManager;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.fulltext.SearchResult;
import cms.dto.topic.ImageInfo;
import cms.dto.user.ResourceEnum;
import cms.model.frontendModule.ConfigHotSearchTerm;
import cms.model.frontendModule.FrontendApi;
import cms.model.question.Question;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.topic.Tag;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.question.QuestionRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TagRepository;
import cms.repository.topic.TopicRepository;
import cms.service.frontend.SearchClientService;
import cms.utils.HtmlEscape;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;


/**
 * 前台搜索服务
 */
@Service
public class SearchClientServiceImpl implements SearchClientService {
    private static final Logger logger = LogManager.getLogger(SearchClientServiceImpl.class);

    @Resource
    JsonComponent jsonComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    TagRepository tagRepository;
    @Resource
    TopicRepository topicRepository;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource StaffCacheManager staffCacheManager;
    @Resource UserCacheManager userCacheManager;
    @Resource
    SearcherManager questionSearcherManager;
    @Resource SearcherManager topicSearcherManager;
    @Resource
    Analyzer analyzer;
    @Resource TopicCacheManager topicCacheManager;
    @Resource
    TextFilterComponent textFilterComponent;


    /**
     * 获取热门搜索词
     * @param request 请求信息
     * @return
     */
    public List<String> getHotSearchWords(HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigHotSearchTerm configHotSearchTerm) {
            List<String> searchWordList = new ArrayList<String>();
            if(configHotSearchTerm.getSearchTermList() != null && !configHotSearchTerm.getSearchTermList().isEmpty()){
                searchWordList.addAll(configHotSearchTerm.getSearchTermList());
            }
            return searchWordList;
        }
        return null;
    }

    /**
     * 获取搜索
     * @param page 页码
     * @param keyword 关键词
     * @param request 请求信息
     * @return
     */
    public PageView<SearchResult> getSearch(int page, String keyword, HttpServletRequest request){
        if((keyword == null || keyword.trim().isEmpty())){
            throw new BusinessException(Map.of("message", "搜索关键词不能为空"));
        }

        PageView<SearchResult> pageView = new PageView<SearchResult>(settingRepository.findSystemSetting_cache().getForestagePageNumber(), page, 10);



        QueryResult<SearchResult> qr = this.findIndexByCondition(pageView.getCurrentpage(),pageView.getMaxresult(), keyword.trim(),20, 1,false);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList =  new ArrayList<Long>();//话题Id集合
            Map<Long,List<String>> tagRoleNameMap = new HashMap<Long,List<String>>();//标签角色名称 key:标签Id 角色名称集合
            Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
            Map<Long,Boolean> userViewPermissionMap = new HashMap<Long,Boolean>();//用户如果对话题项是否有查看权限  key:标签Id value:是否有查看权限
            List<Long> questionIdList =  new ArrayList<Long>();//问题Id集合


            for(SearchResult searchResult : qr.getResultlist()){
                if(searchResult.getIndexModule().equals(10)){//话题模块
                    topicIdList.add(searchResult.getTopic().getId());
                }else if(searchResult.getIndexModule().equals(20)){//问题模块
                    questionIdList.add(searchResult.getQuestion().getId());
                }
            }

            List<Topic> topicList = null;
            if(topicIdList.size() >0){
                topicList = topicRepository.findByIdList(topicIdList);
            }
            List<Question> questionList = null;
            //问题
            if(questionIdList.size() >0){
                questionList = questionRepository.findByIdList(questionIdList);
            }

            Iterator<SearchResult> iter = qr.getResultlist().iterator();
            A:while (iter.hasNext()) {
                SearchResult searchResult = iter.next();
                if(searchResult.getIndexModule().equals(10)){//话题模块
                    if(topicIdList.size() >0){
                        Topic old_topic = searchResult.getTopic();
                        for(Topic t : topicList){
                            if(old_topic.getId().equals(t.getId())){
                                t.setTitle(old_topic.getTitle());
                                t.setContent(old_topic.getContent());
                                t.setIp(null);//IP不显示
                                t.setMarkdownContent("");//Markdown内容不显示
                                if(t.getPostTime().equals(t.getLastReplyTime())){//如果发贴时间等于回复时间，则不显示回复时间
                                    t.setLastReplyTime(null);
                                }
                                if(!t.getIsStaff()){//会员
                                    User user = userCacheManager.query_cache_findUserByUserName(t.getUserName());
                                    t.setAccount(user.getAccount());
                                    t.setNickname(user.getNickname());
                                    t.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                    t.setAvatarName(user.getAvatarName());


                                    if(user.getCancelAccountTime() != -1L){//账号已注销
                                        t.setUserInfoStatus(-30);
                                    }

                                    userRoleNameMap.put(t.getUserName(), null);

                                }else{
                                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(t.getUserName());
                                    if(sysUsers != null){
                                        t.setNickname(sysUsers.getNickname());
                                        if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
                                            t.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                            t.setAvatarName(sysUsers.getAvatarName());
                                        }
                                    }
                                    t.setAccount(t.getUserName());//员工用户名和账号是同一个
                                }
                                searchResult.setTopic(t);
                                continue A;
                            }
                        }
                    }
                }else if(searchResult.getIndexModule().equals(20)){//问题模块
                    if(questionList != null && questionList.size() >0){
                        Question old_question = searchResult.getQuestion();
                        for(Question t : questionList){
                            if(old_question.getId().equals(t.getId())){
                                t.setTitle(old_question.getTitle());
                                t.setContent(old_question.getContent());
                                t.setIp(null);//IP不显示
                                t.setMarkdownContent("");//Markdown内容不显示
                                if(t.getPostTime().equals(t.getLastAnswerTime())){//如果发贴时间等于回复时间，则不显示回复时间
                                    t.setLastAnswerTime(null);
                                }
                                if(!t.getIsStaff()){//会员
                                    User user = userCacheManager.query_cache_findUserByUserName(t.getUserName());
                                    t.setAccount(user.getAccount());
                                    t.setNickname(user.getNickname());
                                    t.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                    t.setAvatarName(user.getAvatarName());

                                    if(user.getCancelAccountTime() != -1L){//账号已注销
                                        t.setUserInfoStatus(-30);
                                    }

                                    userRoleNameMap.put(t.getUserName(), null);

                                }else{
                                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(t.getUserName());
                                    if(sysUsers != null){
                                        t.setNickname(sysUsers.getNickname());
                                        if(sysUsers.getAvatarName() != null && !"".equals(sysUsers.getAvatarName().trim())){
                                            t.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                            t.setAvatarName(sysUsers.getAvatarName());
                                        }
                                    }
                                    t.setAccount(t.getUserName());//员工用户名和账号是同一个
                                }
                                searchResult.setQuestion(t);
                                continue A;
                            }
                        }
                    }
                }

                //如果SQL查询不存在则删除本条记录
                iter.remove();

            }

            List<Tag> tagList = tagRepository.findAllTag_cache();
            if(tagList != null && tagList.size() >0 && topicIdList != null && topicIdList.size() >0){
                for(Topic t : topicList){
                    for(Tag tag :tagList){
                        if(t.getTagId().equals(tag.getId())){
                            t.setTagName(tag.getName());
                            tagRoleNameMap.put(t.getTagId(), null);
                            userViewPermissionMap.put(t.getTagId(), null);

                            break;
                        }

                    }
                }
            }
            if(tagRoleNameMap.size() >0){
                for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                    List<String> roleNameList = userRoleComponent.queryAllowViewTopicRoleName(entry.getKey(),request);
                    entry.setValue(roleNameList);
                }
            }

            if(userRoleNameMap.size() >0){
                for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                    List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey(),request);
                    entry.setValue(roleNameList);
                }
            }
            if(userViewPermissionMap.size()>0){
                for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                    //是否有当前功能操作权限
                    boolean flag = userRoleComponent.isPermission(ResourceEnum._1001000,entry.getKey());
                    entry.setValue(flag);
                }
            }

            for(SearchResult searchResult : qr.getResultlist()){
                if(searchResult.getIndexModule().equals(10)){//话题模块
                    Topic topic = searchResult.getTopic();
                    topic.setMarkdownContent("");//Markdown内容不显示
                    //话题允许查看的角色名称集合
                    for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                        if(entry.getKey().equals(topic.getTagId())){
                            List<String> roleNameList = entry.getValue();
                            if(roleNameList != null && roleNameList.size() >0){
                                topic.setAllowRoleViewList(roleNameList);
                            }
                            break;
                        }

                    }
                    if(!topic.getIsStaff()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(topic.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && roleNameList.size() >0){
                                    topic.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }

                    //用户如果对话题项无查看权限，则不显示摘要和图片
                    for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                        if(entry.getKey().equals(topic.getTagId())){
                            if(entry.getValue() != null && !entry.getValue()){
                                topic.setImage(null);
                                topic.setImageInfoList(new ArrayList<ImageInfo>());
                                topic.setSummary("");
                                topic.setContent("");

                            }
                            break;
                        }

                    }

                    //非正常状态用户清除显示数据
                    if(topic.getUserInfoStatus() <0){
                        topic.setUserName(null);
                        topic.setAccount(null);
                        topic.setNickname(null);
                        topic.setAvatarPath(null);
                        topic.setAvatarName(null);
                        topic.setUserRoleNameList(new ArrayList<String>());
                    }
                }else if(searchResult.getIndexModule().equals(20)){//问题模块
                    Question question = searchResult.getQuestion();
                    question.setMarkdownContent("");//Markdown内容不显示
                    if(!question.getIsStaff()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(question.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && roleNameList.size() >0){
                                    question.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }
                    //非正常状态用户清除显示数据
                    if(question.getUserInfoStatus() <0){
                        question.setUserName(null);
                        question.setAccount(null);
                        question.setNickname(null);
                        question.setAvatarPath(null);
                        question.setAvatarName(null);
                        question.setUserRoleNameList(new ArrayList<String>());
                    }
                }
            }

        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 根据条件查询索引
     * @param firstIndex 开始索引
     * @param maxResult 需要获取的记录数
     * @param keyword 关键词
     * @param status 状态
     * @param sortCondition 排序条件
     * @param isHide 是否显示隐藏标签内容
     * @return
     */
    private QueryResult<SearchResult> findIndexByCondition(int firstIndex, int maxResult,String keyword,Integer status,int sortCondition,boolean isHide){
        QueryResult<SearchResult> qr = new QueryResult<SearchResult>();
        //存储符合条件的记录
        List<SearchResult> searchResultList = new ArrayList<SearchResult>();


        IndexSearcher searcher_topic =  null;
        IndexSearcher searcher_question =  null;
        IndexSearcher indexSearcher = null;
        try {
            searcher_topic = topicSearcherManager.acquire();
            searcher_question = questionSearcherManager.acquire();

            MultiReader multiReader = new MultiReader(searcher_topic.getIndexReader(),searcher_question.getIndexReader());
            indexSearcher = new IndexSearcher(multiReader);

            //要搜索的字段
            //	String[] fieldName = {"name","sellprice","createdate"}; //"path"字段不查询
            // BooleanClause.Occur[]数组,它表示多个条件之间的关系,
            // BooleanClause.Occur.MUST表示 and,
            // BooleanClause.Occur.MUST_NOT表示not,
            // BooleanClause.Occur.SHOULD表示or.
            BooleanQuery.Builder query = new BooleanQuery.Builder();//组合查询

            if(keyword != null && !keyword.trim().isEmpty()){
                BooleanClause.Occur[] clauses = { BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD };
                Query keyword_parser = MultiFieldQueryParser.parse(new String[] {QueryParser.escape(keyword), QueryParser.escape(keyword)}, new String[] {"title", "content"}, clauses,analyzer);
                query.add(keyword_parser,BooleanClause.Occur.MUST);
            }


            if(status != null){
                //是否可见 精确查询
                Query status_query = IntPoint.newExactQuery("status", status);
                query.add(status_query,BooleanClause.Occur.MUST);
            }

            //分页起始索引
            int startIndex = firstIndex <= 1? 0 : (firstIndex-1) * maxResult;
            //分页结束索引
            int endIndex = startIndex+maxResult;

            //排序
            SortField[] sortFields = new SortField[2];
            //type对应的值分别为：
            //SortField.Type.SCORE 按相关度(积分)排序
            //SortField.Type.DOC 按文档排序
            //SortField.Type.AUTO 域的值为int、long、float都有效
            //SortField.Type.STRING 域按STRING排序
            //SortField.Type.FLOAT
            //SortField.Type.LONG
            //SortField.Type.DOUBLE
            //SortField.Type.SHORT
            //SortField.Type.CUSTOM 通过比较器排序
            //SortField.Type.BYTE
            sortFields[0] = new SortField("title",SortField.Type.SCORE);
            if(sortCondition == 1){
                sortFields[1] = new SortField("postTime",SortField.Type.LONG,true);//false升序，true降序 //发布时间排序   新-->旧
            }else if(sortCondition == 2){
                sortFields[1] = new SortField("postTime",SortField.Type.LONG,false);//false升序，true降序//发布时间排序  旧-->新
            }else{
                //和1相同
                sortFields[1] = new SortField("postTime",SortField.Type.LONG,true);//false升序，true降序 //发布时间排序   新-->旧
            }
            Sort sort = new Sort(sortFields);


            //高亮设置
            SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter("<B>","</B>");//设定高亮显示的格式，也就是对高亮显示的词组加上前缀后缀

            Highlighter highlighter = new Highlighter(simpleHtmlFormatter,new QueryScorer(query.build()));
            highlighter.setTextFragmenter(new SimpleFragmenter(190));//设置每次返回的字符数.想必大家在使用搜索引擎的时候也没有一并把全部数据展示出来吧，当然这里也是设定只展示部分数据




            TopDocs topDocs = indexSearcher.search(query.build() ,endIndex,sort);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            StoredFields storedFields = indexSearcher.storedFields();
            //查询所得记录
            for (int i = startIndex;i < endIndex && i<topDocs.totalHits.value(); i++) {

                Document targetDoc = storedFields.document(scoreDocs[i].doc); //根据文档编号取出相应的文档
                String _indexModule = targetDoc.get("indexModule");
                String _id = targetDoc.get("id");
                String _title = targetDoc.get("title");
                String _content = targetDoc.get("content");

                Long id = Long.parseLong(_id);
                String title = "";
                String content = "";

                if (_title != null && !_title.isEmpty()) {
                    //执行转义
                    _title = HtmlEscape.escape(_title);
                    TokenStream tokenStream = analyzer.tokenStream("title",new StringReader(_title));
                    String highLightText = highlighter.getBestFragment(tokenStream, _title);//高亮显示
                    if(highLightText != null && !highLightText.isEmpty()){
                        title = highLightText;
                    }else{
                        title = _title;
                    }
                }


                if (_content != null && !_content.isEmpty()) {
                    if(isHide){//如果显示隐藏内容
                        _content = textFilterComponent.filterText(_content);
                    }else{
                        if(_indexModule == null || "10".equals(_indexModule)){//话题模块
                            Topic topic = topicCacheManager.queryTopicCache(Long.parseLong(_id));//查询缓存
                            if(topic != null){
                                _content = textFilterComponent.filterHideText(topic.getContent());
                            }
                        }else{
                            _content = textFilterComponent.filterHideText(_content);
                        }
                    }

                    TokenStream tokenStream = analyzer.tokenStream("content",new StringReader(_content));
                    String highLightText = highlighter.getBestFragment(tokenStream, _content);
                    //高亮显示
                    if(highLightText != null && !highLightText.isEmpty()){
                        content = highLightText;
                    }else{
                        if(_content.length() >190){
                            content = _content.substring(0, 190);
                        }else{
                            content = _content;
                        }

                    }
                }

                if(_indexModule != null && "20".equals(_indexModule)){//问题模块
                    SearchResult searchResult = new SearchResult();
                    searchResult.setIndexModule(20);
                    Question question = new Question();
                    question.setId(id);
                    question.setTitle(title);
                    question.setContent(content);
                    searchResult.setQuestion(question);
                    searchResultList.add(searchResult);

                }else{//话题模块
                    SearchResult searchResult = new SearchResult();
                    searchResult.setIndexModule(10);
                    Topic topic = new Topic();
                    topic.setId(id);
                    topic.setTitle(title);
                    topic.setContent(content);
                    searchResult.setTopic(topic);
                    searchResultList.add(searchResult);
                }

            }
            //把查询结果设进去
            qr.setResultlist(searchResultList);
            qr.setTotalrecord(topDocs.totalHits.value());

        } catch (CorruptIndexException e) {
            //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("根据条件查询索引",e);
            }
        } catch (IOException e) {
            //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("根据条件查询索引",e);
            }
        }catch (ParseException e) {
            //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("根据条件查询索引",e);
            }
        }catch (InvalidTokenOffsetsException e) {
            //	e.printStackTrace();
            if (logger.isErrorEnabled()) {
                logger.error("根据条件查询索引",e);
            }
        }finally{
            //关闭资源
            try {
                topicSearcherManager.release(searcher_topic);

            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据条件查询索引关闭topicSearcher资源错误",e);
                }
            }

            try {
                questionSearcherManager.release(searcher_question);
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("根据条件查询索引关闭questionSearcher资源错误",e);
                }
            }

        }
        return qr;
    }
}
