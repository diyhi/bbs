package cms.service.topic.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.lucene.TopicLuceneComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.payment.PaymentComponent;
import cms.component.redEnvelope.RedEnvelopeCacheManager;
import cms.component.redEnvelope.RedEnvelopeComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.topic.CommentComponent;
import cms.component.topic.TopicCacheManager;
import cms.component.topic.TopicComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleComponent;
import cms.config.BusinessException;
import cms.dto.*;
import cms.dto.topic.HideTagType;
import cms.dto.topic.ImageInfo;
import cms.dto.topic.TopicRequest;
import cms.model.message.Remind;
import cms.model.payment.PaymentLog;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.thumbnail.Thumbnail;
import cms.model.topic.*;
import cms.model.user.User;
import cms.model.user.UserGrade;
import cms.model.vote.VoteOption;
import cms.model.vote.VoteRecord;
import cms.model.vote.VoteTheme;
import cms.repository.message.RemindRepository;
import cms.repository.redEnvelope.RedEnvelopeRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.thumbnail.ThumbnailRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TagRepository;
import cms.repository.topic.TopicIndexRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserGradeRepository;
import cms.repository.user.UserRepository;
import cms.service.topic.TopicService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 话题标签服务
 */
@Service
public class TopicServiceImpl implements TopicService {
    private static final Logger logger = LogManager.getLogger(TopicServiceImpl.class);

    @Resource
    TagRepository tagRepository;
    @Resource
    TopicRepository topicRepository;
    @Resource SettingRepository settingRepository;
    @Resource JsonComponent jsonComponent;
    @Resource TextFilterComponent textFilterComponent;
    @Resource FileComponent fileComponent;
    @Resource
    TopicComponent topicComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource StaffCacheManager staffCacheManager;
    @Resource
    UserGradeRepository userGradeRepository;
    @Resource
    CommentComponent commentComponent;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    CommentRepository commentRepository;
    @Resource
    RedEnvelopeRepository redEnvelopeRepository;
    @Resource
    UserRepository userRepository;
    @Resource TopicCacheManager topicCacheManager;
    @Resource
    TopicIndexRepository topicIndexRepository;
    @Resource
    RedEnvelopeComponent redEnvelopeComponent;
    @Resource
    PaymentComponent paymentComponent;
    @Resource RedEnvelopeCacheManager redEnvelopeCacheManager;
    @Resource
    ThumbnailRepository thumbnailRepository;

    @Resource
    TopicLuceneComponent topicLuceneComponent;
    @Resource
    RemindRepository remindRepository;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;
    @Resource RemindCacheManager remindCacheManager;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {
    }


    /**
     * 获取话题列表
     * @param page 页码
     * @param visible 是否可见
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Topic> getTopicList(int page, Boolean visible, String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        if(visible != null && !visible){
            jpql.append(" and o.status>?"+ (params.size()+1));
            params.add(100);
        }else{
            jpql.append(" and o.status<?"+ (params.size()+1));
            params.add(100);
        }
        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Topic> pageView = new PageView<Topic>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Tag> tagList = tagRepository.findAllTag();
            if(tagList != null && tagList.size() >0){
                for(Topic topic : qr.getResultlist()){
                    for(Tag tag : tagList){
                        if(topic.getTagId().equals(tag.getId())){
                            topic.setTagName(tag.getName());
                            break;
                        }
                    }

                }
            }


            for(Topic topic : qr.getResultlist()){
                if(topic.getIsStaff()){//如果为员工

                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(topic.getUserName());
                    if(sysUsers != null){
                        topic.setNickname(sysUsers.getNickname());
                        if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                            topic.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                            topic.setAvatarName(sysUsers.getAvatarName());
                        }
                    }


                    topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
                }else{
//					User user = userService.findUserByUserName(topic.getUserName());
                    User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                    if(user != null){
                        topic.setAccount(user.getAccount());
                        topic.setNickname(user.getNickname());
                        if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                            topic.setAvatarPath(fileServerAddress+user.getAvatarPath());
                            topic.setAvatarName(user.getAvatarName());
                        }
                    }
                }
            }


        }

        pageView.setQueryResult(qr);
        return pageView;
    }


    /**
     * 获取话题明细
     * @param topicId 话题Id
     * @param commentId 评论Id
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getTopicDetail(Long topicId, Long commentId, Integer page, HttpServletRequest request){
        if(topicId == null || topicId <=0L){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        Topic topic = topicRepository.findById(topicId);
        if(topic == null){
            throw new BusinessException(Map.of("topicId", "话题不存在"));
        }

        Map<String,Object> returnValue = new LinkedHashMap<String,Object>();

        if(topic.getIp() != null && !topic.getIp().trim().isEmpty()){
            topic.setIpAddress(IpAddress.queryAddress(topic.getIp().trim()));
        }
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty()){
            //处理富文本路径
            topic.setContent(fileComponent.processRichTextFilePath(topic.getContent(),"topic"));
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty() && systemSetting.getFileSecureLinkSecret() != null && !systemSetting.getFileSecureLinkSecret().trim().isEmpty()){
            List<String> serverAddressList = fileComponent.fileServerAllAddress(request);
            //解析上传的文件完整路径名称
            Map<String,String> analysisFullFileNameMap = textFilterComponent.analysisFullFileName(topic.getContent(),"topic",serverAddressList);
            if(analysisFullFileNameMap != null && !analysisFullFileNameMap.isEmpty()){


                Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
                for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

                    newFullFileNameMap.put(entry.getKey(), WebUtil.getUrl(request)+ SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),-1L,systemSetting.getFileSecureLinkSecret()));
                }

                topic.setContent(textFilterComponent.processFullFileName(topic.getContent(),"topic",newFullFileNameMap,serverAddressList));

            }
        }
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty()){

            //处理视频播放器标签
            String content = textFilterComponent.processVideoPlayer(WebUtil.getUrl(request),topic.getContent(),-1L,systemSetting.getFileSecureLinkSecret());
            topic.setContent(content);
        }

        if(topic.getIsStaff() == false){//会员
            User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            if(user != null){
                topic.setAccount(user.getAccount());
                topic.setNickname(user.getNickname());
                topic.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                topic.setAvatarName(user.getAvatarName());

                List<String> userRoleNameList = userRoleComponent.queryUserRoleName(user.getUserName());
                if(userRoleNameList != null && !userRoleNameList.isEmpty()){
                    topic.setUserRoleNameList(userRoleNameList);//用户角色名称集合
                }
            }

        }else{
            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(topic.getUserName());
            if(sysUsers != null){
                topic.setNickname(sysUsers.getNickname());
                if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                    topic.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                    topic.setAvatarName(sysUsers.getAvatarName());
                }
            }
            topic.setAccount(topic.getUserName());//员工用户名和账号是同一个

        }

        List<String> topic_roleNameList = userRoleComponent.queryAllowViewTopicRoleName(topic.getTagId());
        if(topic_roleNameList != null && !topic_roleNameList.isEmpty()){
            topic.setAllowRoleViewList(topic_roleNameList);
        }

        Tag tag = tagRepository.findById(topic.getTagId());
        if(tag != null){
            topic.setTagName(tag.getName());
        }

        if(topic.getGiveRedEnvelopeId() != null && !topic.getGiveRedEnvelopeId().isEmpty()){//红包
            GiveRedEnvelope giveRedEnvelope = redEnvelopeRepository.findById(topic.getGiveRedEnvelopeId());
            returnValue.put("giveRedEnvelope", giveRedEnvelope);

        }


        returnValue.put("topic", topic);

        returnValue.put("availableTag", commentComponent.availableTag());

        returnValue.put("supportEditor", systemSetting.getSupportEditor());



        PageForm pageForm = new PageForm();
        pageForm.setPage(page);

        if(commentId != null && commentId >0L && page == null){
            Long row = commentRepository.findRowByCommentId(commentId,topicId);
            if(row != null && row >0L){

                int _page = Integer.parseInt(String.valueOf(row))/settingRepository.findSystemSetting_cache().getBackstagePageNumber();
                if(Integer.parseInt(String.valueOf(row))%settingRepository.findSystemSetting_cache().getBackstagePageNumber() >0){//余数大于0要加1
                    _page = _page+1;
                }
                pageForm.setPage(_page);
            }
        }





        //评论
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        PageView<Comment> pageView = new PageView<Comment>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);




        //当前页
        int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();


        jpql.append(" o.topicId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
        params.add(topicId);//设置o.parentId=?2参数


        orderby.put("postTime", "asc");//根据sort字段降序排序
        QueryResult<Comment> qr = commentRepository.getScrollData(Comment.class,firstindex, pageView.getMaxresult(),jpql.toString(),params.toArray(),orderby);


        List<Long> commentIdList = new ArrayList<Long>();
        List<Comment> commentList = qr.getResultlist();

        //引用修改Id集合
        List<Long> quoteUpdateIdList = new ArrayList<Long>();
        //重新查询Id
        List<Long> query_quoteUpdateIdList = new ArrayList<Long>();
        //新引用集合
        Map<Long,String> new_quoteList = new HashMap<Long,String>();//key:自定义评论Id value:自定义评论内容(文本)

        if(commentList != null && !commentList.isEmpty()){
            for(Comment comment : commentList){
                if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
                    //处理富文本路径
                    comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
                }

                if(comment.getQuoteUpdateId() != null && comment.getQuoteUpdateId().length() >1){
                    String[] quoteUpdateId_arr = comment.getQuoteUpdateId().split(",");
                    for(String quoteUpdateId : quoteUpdateId_arr){
                        if(quoteUpdateId != null && !quoteUpdateId.trim().isEmpty()){
                            Long l = Long.parseLong(quoteUpdateId);
                            if(!quoteUpdateIdList.contains(l)){
                                quoteUpdateIdList.add(l);
                            }
                        }
                    }

                }


                if(comment.getIp() != null && !comment.getIp().trim().isEmpty()){
                    comment.setIpAddress(IpAddress.queryAddress(comment.getIp()));
                }
            }

            A:for(Long quoteUpdateId : quoteUpdateIdList){
                for(Comment comment : commentList){
                    if(comment.getId().equals(quoteUpdateId)){
                        new_quoteList.put(comment.getId(), textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent())));
                        continue A;
                    }
                }
                query_quoteUpdateIdList.add(quoteUpdateId);
            }
        }




        if(!query_quoteUpdateIdList.isEmpty()){
            List<Comment> quote_commentList = commentRepository.findByCommentIdList(query_quoteUpdateIdList);
            if(quote_commentList != null && !quote_commentList.isEmpty()){
                for(Comment comment : quote_commentList){
                    new_quoteList.put(comment.getId(), textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent())));
                }
            }
        }

        Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
        if(commentList != null && !commentList.isEmpty()){
            for(Comment comment : commentList){
                if(!comment.getIsStaff()){//会员
                    User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
                    if(user != null){
                        comment.setAccount(user.getAccount());
                        comment.setNickname(user.getNickname());
                        comment.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                        comment.setAvatarName(user.getAvatarName());
                        userRoleNameMap.put(comment.getUserName(), null);
                    }

                }else{
                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(comment.getUserName());
                    if(sysUsers != null){
                        comment.setNickname(sysUsers.getNickname());
                        if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                            comment.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                            comment.setAvatarName(sysUsers.getAvatarName());
                        }
                    }
                    comment.setAccount(comment.getUserName());//员工用户名和账号是同一个

                }
                commentIdList.add(comment.getId());
                if(comment.getQuote() != null && !comment.getQuote().trim().isEmpty()){
                    //旧引用
                    List<Quote> quoteList = jsonComponent.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
                    if(quoteList != null && !quoteList.isEmpty()){
                        for(Quote quote :quoteList){
                            if(new_quoteList.containsKey(quote.getCommentId())){
                                quote.setContent(new_quoteList.get(quote.getCommentId()));
                            }

                            if(!quote.getIsStaff()){//会员
                                User user = userCacheManager.query_cache_findUserByUserName(quote.getUserName());
                                if(user != null){
                                    quote.setAccount(user.getAccount());
                                    quote.setNickname(user.getNickname());
                                    quote.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                    quote.setAvatarName(user.getAvatarName());
                                    userRoleNameMap.put(quote.getUserName(), null);
                                }

                            }else{
                                SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(quote.getUserName());
                                if(sysUsers != null){
                                    quote.setNickname(sysUsers.getNickname());
                                    if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                        quote.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                        quote.setAvatarName(sysUsers.getAvatarName());
                                    }
                                }

                                quote.setAccount(quote.getUserName());//员工用户名和账号是同一个

                            }


                        }
                    }
                    comment.setQuoteList(quoteList);
                }


            }
        }




        if(!commentIdList.isEmpty()){
            List<Reply> replyList = commentRepository.findReplyByCommentId(commentIdList);
            if(replyList != null && !replyList.isEmpty()){
                for(Comment comment : commentList){
                    for(Reply reply : replyList){
                        if(reply.getIsStaff() == false){//会员
                            User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
                            if(user != null){
                                reply.setAccount(user.getAccount());
                                reply.setNickname(user.getNickname());
                                reply.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                reply.setAvatarName(user.getAvatarName());
                                userRoleNameMap.put(reply.getUserName(), null);
                            }

                        }else{
                            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(reply.getUserName());
                            if(sysUsers != null){
                                reply.setNickname(sysUsers.getNickname());
                                if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                    reply.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                    reply.setAvatarName(sysUsers.getAvatarName());
                                }
                            }
                            reply.setAccount(reply.getUserName());//员工用户名和账号是同一个

                        }
                        if(reply.getFriendUserName() != null && !reply.getFriendUserName().trim().isEmpty()){
                            if(reply.getIsFriendStaff() == false){//会员
                                User user = userCacheManager.query_cache_findUserByUserName(reply.getFriendUserName());
                                if(user != null){
                                    reply.setFriendAccount(user.getAccount());
                                    reply.setFriendNickname(user.getNickname());
                                    reply.setFriendAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                    reply.setFriendAvatarName(user.getAvatarName());
                                }

                            }else{
                                SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(reply.getFriendUserName());
                                if(sysUsers != null){
                                    reply.setFriendNickname(sysUsers.getNickname());
                                    if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                        reply.setFriendAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                        reply.setFriendAvatarName(sysUsers.getAvatarName());
                                    }
                                }
                                reply.setFriendAccount(reply.getFriendUserName());//员工用户名和账号是同一个

                            }
                        }


                        if(comment.getId().equals(reply.getCommentId())){

                            reply.setContent(reply.getContent());
                            comment.addReply(reply);
                        }
                    }

                    //回复排序
                    commentComponent.replySort(comment.getReplyList());
                }
            }
        }

        if(!userRoleNameMap.isEmpty()){
            for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey());
                entry.setValue(roleNameList);
            }
        }

        if(commentList != null && !commentList.isEmpty()){
            for(Comment comment : commentList){
                //用户角色名称集合
                for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                    if(entry.getKey().equals(comment.getUserName())){
                        List<String> roleNameList = entry.getValue();
                        if(roleNameList != null && !roleNameList.isEmpty()){
                            comment.setUserRoleNameList(roleNameList);
                        }
                        break;
                    }
                }
                if(comment.getReplyList() != null && !comment.getReplyList().isEmpty()){
                    for(Reply reply : comment.getReplyList()){
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(reply.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && !roleNameList.isEmpty()){
                                    reply.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }
                }
                if(comment.getQuoteList() != null && !comment.getQuoteList().isEmpty()){
                    for(Quote quote : comment.getQuoteList()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(quote.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && !roleNameList.isEmpty()){
                                    quote.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }


                    }

                }
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        returnValue.put("pageView", pageView);


        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        return returnValue;
    }

    /**
     * 获取添加话题界面信息
     * @return
     */
    public Map<String,Object> getAddTopicViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
        returnValue.put("userGradeList", userGradeList);
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 添加话题
     * @param topicRequest 话题表单
     * @param request 请求信息
     */
    public void addTopic(TopicRequest topicRequest,HttpServletRequest request){
        validateInput(topicRequest);

        Map<String, String> errors = new HashMap<>();
        Topic topic = new Topic();
        VoteTheme voteTheme = null;
        List<String> optionTextList = new ArrayList<String>();
        LocalDateTime voteEndDate = null;
        LocalDateTime voteCreateDate = LocalDateTime.now();

        Tag tag = tagRepository.findById(topicRequest.getTagId());
        if(tag != null && tag.getChildNodeNumber()==0){
            topic.setTagId(tag.getId());
            topic.setTagName(tag.getName());
            topic.setTagIdGroup(tag.getParentIdGroup()+tag.getId()+",");
        }else{
            errors.put("tagId", "标签不存在");
        }


        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }


        // 处理内容过滤和文件路径
        TopicServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, topicRequest.getContent(), topicRequest.getIsMarkdown(), topicRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
        //图片地址
        List<ImageInfo> imageList = new ArrayList<ImageInfo>();
        //校正隐藏标签
        String validValue =  textFilterComponent.correctionHiddenTag(value,userGradeList);

        //解析隐藏标签
        Map<Integer,Object> analysisHiddenTagMap = textFilterComponent.analysisHiddenTag(validValue);
        for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
            //管理员账户不能发表'余额购买'话题
            if(entry.getKey().equals(HideTagType.AMOUNT.getName())){
                throw new BusinessException(Map.of("content", "管理员账户不能发表'余额购买'话题"));
                //break;
            }
        }



        //删除隐藏标签
        String new_content = textFilterComponent.deleteHiddenTag(value);

        //不含标签内容
        String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(new_content));

        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        //不含标签内容
        String source_text = textFilterComponent.filterText(htmlFilterResult.content);
        //清除空格&nbsp;
        String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();

        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && !htmlFilterResult.htmlProcessingResult.isHasFile()
                && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                && !htmlFilterResult.htmlProcessingResult.isHasMap()
                && (source_text.trim().isEmpty() || source_trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }

        //非隐藏标签内图片
        List<String> other_imageNameList = textFilterComponent.readImageName(new_content,"topic");

        if(other_imageNameList != null && other_imageNameList.size() >0){
            for(int i=0; i<other_imageNameList.size(); i++){
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setName(FileUtil.getName(other_imageNameList.get(i)));
                imageInfo.setPath(FileUtil.getFullPath(other_imageNameList.get(i)));

                imageList.add(imageInfo);

            }
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }


        //摘要
        if(!trimSpace.isEmpty()){
            if(trimSpace.length() >180){
                topic.setSummary(trimSpace.substring(0, 180)+"..");
            }else{
                topic.setSummary(trimSpace+"..");
            }
        }
        topic.setTitle(topicRequest.getTitle());
        topic.setContent(validValue);
        topic.setAllow(topicRequest.getAllow());
        topic.setStatus(topicRequest.getStatus());
        topic.setEssence(topicRequest.getEssence());
        LocalDateTime d =  LocalDateTime.now();
        topic.setPostTime(d);
        topic.setLastReplyTime(d);
        topic.setImage(jsonComponent.toJSONString(imageList));
        topic.setSort(topicRequest.getSort());
        topic.setIp(IpAddress.getClientIpAddress(request));
        topic.setUserName(username);
        topic.setIsStaff(true);
        topic.setIsMarkdown(topicRequest.getIsMarkdown() == null ? false:topicRequest.getIsMarkdown());
        topic.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        topicRepository.saveTopic(topic,null,null,null,null,voteTheme);
        topicCacheManager.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态
        //更新索引
        topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),1));




        //上传文件编号
        String fileNumber = "a"+userId;


        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult().getImageNameList() != null && !htmlFilterResult.htmlProcessingResult().getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult().getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){

                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }

                    fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //音视频
        if(htmlFilterResult.htmlProcessingResult().getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult().getMediaNameList().isEmpty()){
            for(String mediaName :htmlFilterResult.htmlProcessingResult().getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //文件
        if(htmlFilterResult.htmlProcessingResult().getFileNameList() != null && !htmlFilterResult.htmlProcessingResult().getFileNameList().isEmpty()){
            for(String fileName :htmlFilterResult.htmlProcessingResult().getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }

        List<Thumbnail> thumbnailList = thumbnailRepository.findAllThumbnail_cache();
        if(thumbnailList != null && !thumbnailList.isEmpty()){
            //异步增加缩略图
            if(!imageList.isEmpty()){
                fileComponent.addThumbnail(thumbnailList,imageList);
            }
        }
    }


    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir, String userName,Boolean isStaff, String fileName, String fileServerAddress, MultipartFile file) {

        String number = topicComponent.generateFileNumber(userName, isStaff);

        Map<String,Object> returnValue = new HashMap<String,Object>();
        if (dir == null || number == null || number.trim().isEmpty()) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }
        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyy-MM-dd");
        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, date, number, returnValue);
        } else { // 本地系统
            return handleLocalUpload(dir, file, date, number, fileServerAddress, returnValue);
        }
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param date 日期
     * @param number 上传文件编号
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, String date, String number, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }

        List<String> formatList = getAllowFormats(dir,result);
        if (formatList == null || formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,date +"_"+dir+"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/topic/"+date+"/"+dir+"/"+newFileName,null);//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }

    /**
     * 根据文件类型获取允许上传的格式列表。
     */
    private List<String> getAllowFormats(String dir,Map<String, Object> result) {
        List<String> formatList = new ArrayList<>();
        switch (dir) {
            case "image":
                formatList = CommentedProperties.readRichTextAllowImageUploadFormat();
                break;
            case "media":
                formatList = CommentedProperties.readRichTextAllowVideoUploadFormat();
                break;
            case "file":
                formatList = CommentedProperties.readRichTextAllowFileUploadFormat();
                break;
            default:
                result.put("error", 1);
                result.put("message", "缺少dir参数");
        }
        return formatList;
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param date 日期
     * @param number  上传文件编号
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, String date, String number, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        List<String> formatList = getAllowFormats(dir,result);
        if (formatList == null || formatList.isEmpty()) {
            return result;
        }
        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }

        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
        String pathDir = "file"+File.separator+"topic"+File.separator + date +File.separator +dir+ File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,date +"_"+dir+"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/topic/"+date+"/"+dir+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }

    /**
     * 修改话题
     * @param topicRequest 话题表单
     * @param request 请求信息
     */
    public void editTopic(TopicRequest topicRequest,HttpServletRequest request){
        if(topicRequest.getStatus() == null){
            throw new BusinessException(Map.of("status", "话题状态不能为空"));
        }
        if(topicRequest.getTopicId() == null || topicRequest.getTopicId() <=0L){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }


        Topic topic = topicRepository.findById(topicRequest.getTopicId());
        if(topic == null){
            throw new BusinessException(Map.of("topicId", "话题Id不存在"));
        }
        //旧状态
        Integer old_status = topic.getStatus();
        String old_content = topic.getContent();
        String old_voteThemeId = topic.getVoteThemeId();
        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
        //旧图片地址
        List<ImageInfo> oldImageList = new ArrayList<ImageInfo>();
        if(topic.getImage() != null && !topic.getImage().trim().isEmpty()){
            oldImageList = jsonComponent.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
        }
        //图片地址
        List<ImageInfo> imageList = new ArrayList<ImageInfo>();
        VoteTheme voteTheme = null;
        List<VoteOption> voteOptionList = null;
        VoteTheme new_voteTheme = null;//新增投票主题
        VoteTheme update_voteTheme = null;//修改投票主题
        String deleteVoteThemeId = null;//删除投票
        List<VoteOption> add_voteOptionList = new ArrayList<VoteOption>();
        List<VoteOption> edit_voteOptionList = new ArrayList<VoteOption>();
        List<String> delete_voteOptionIdList = new ArrayList<String>();

        validateInput(topicRequest);


        Map<String, String> errors = new HashMap<>();

        Tag tag = tagRepository.findById(topicRequest.getTagId());
        if(tag != null && tag.getChildNodeNumber()==0){
            topic.setTagId(tag.getId());
            topic.setTagName(tag.getName());
            topic.setTagIdGroup(tag.getParentIdGroup()+tag.getId()+",");
        }else{
            errors.put("tagId", "标签不存在");
        }

        LocalDateTime voteCreateDate = topic.getPostTime();//投票主题创建时间
        LocalDateTime voteEndDate= null;//截止日期
        List<VoteOption> temp_voteOptionList = new ArrayList<VoteOption>();



        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        // 处理内容过滤和文件路径
        TopicServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, topicRequest.getContent(), topicRequest.getIsMarkdown(), topicRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
        //校正隐藏标签
        String validValue =  textFilterComponent.correctionHiddenTag(value,userGradeList);

        if(topic.getIsStaff()){//如果是员工
            //解析隐藏标签
            Map<Integer,Object> analysisHiddenTagMap = textFilterComponent.analysisHiddenTag(validValue);
            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                //管理员账户不能发表'余额购买'话题
                if(entry.getKey().equals(HideTagType.AMOUNT.getName())){
                    throw new BusinessException(Map.of("content", "管理员账户不能发表'余额购买'话题"));
                    //break;
                }
            }
        }

        //删除隐藏标签
        String new_content = textFilterComponent.deleteHiddenTag(value);


        //不含标签内容
        String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(new_content));


        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        //不含标签内容
        String source_text = textFilterComponent.filterText(htmlFilterResult.content);
        //清除空格&nbsp;
        String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();

        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && !htmlFilterResult.htmlProcessingResult.isHasFile()
                && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                && !htmlFilterResult.htmlProcessingResult.isHasMap()
                && (source_text.trim().isEmpty() || source_trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }


        //非隐藏标签内图片
        List<String> other_imageNameList = textFilterComponent.readImageName(new_content,"topic");


        if(other_imageNameList != null && other_imageNameList.size() >0){
            for(int i=0; i<other_imageNameList.size(); i++){
                ImageInfo imageInfo = new ImageInfo();
                imageInfo.setName(FileUtil.getName(other_imageNameList.get(i)));
                imageInfo.setPath(FileUtil.getFullPath(other_imageNameList.get(i)));

                imageList.add(imageInfo);

            }
        }



        //摘要
        if(!trimSpace.isEmpty()){
            if(trimSpace.length() >180){
                topic.setSummary(trimSpace.substring(0, 180)+"..");
            }else{
                topic.setSummary(trimSpace+"..");
            }
        }
        topic.setAllow(topicRequest.getAllow());
        topic.setEssence(topicRequest.getEssence());
        topic.setStatus(topicRequest.getStatus());
        topic.setTitle(topicRequest.getTitle());
        topic.setContent(validValue);
        topic.setSort(topicRequest.getSort());
        topic.setIsMarkdown(topicRequest.getIsMarkdown() == null ? false:topicRequest.getIsMarkdown());
        topic.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        topic.setImage(jsonComponent.toJSONString(imageList));
        topic.setLastUpdateTime(LocalDateTime.now());//最后修改时间

        int i = topicRepository.updateTopic(topic,new_voteTheme,update_voteTheme,deleteVoteThemeId,add_voteOptionList,edit_voteOptionList,delete_voteOptionIdList);


        //更新索引
        topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));

        if(i >0 && !old_status.equals(topicRequest.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            if(user != null){
                //修改用户动态话题状态
                userRepository.updateUserDynamicTopicStatus(user.getId(),topic.getUserName(),topic.getId(),topic.getStatus());
            }

        }


        //删除缓存
        topicCacheManager.deleteTopicCache(topic.getId());//删除话题缓存
        topicCacheManager.delete_cache_analysisHiddenTag(topic.getId());//删除解析隐藏标签缓存
        topicCacheManager.delete_cache_analysisFullFileName(topic.getId());//删除 解析上传的文件完整路径名称缓存
        topicCacheManager.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态




        ReadPathResult readPathResult = textFilterComponent.readPathName(old_content,"topic");


        //旧图片
        List<String> old_imageNameList = readPathResult.getImageNameList();

        if(old_imageNameList != null && !old_imageNameList.isEmpty()){

            Iterator<String> iter = old_imageNameList.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();

                for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                    if(imageName.equals("file/topic/"+new_imageName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(!old_imageNameList.isEmpty()){
                for(String imageName : old_imageNameList){

                    oldPathFileList.add(FileUtil.toSystemPath(imageName));

                }

            }
        }


        //旧影音
        List<String> old_mediaNameList = readPathResult.getMediaNameList();
        if(old_mediaNameList != null && !old_mediaNameList.isEmpty()){
            Iterator<String> iter = old_mediaNameList.iterator();
            while (iter.hasNext()) {
                String mediaName = iter.next();
                for(String new_mediaName : htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                    if(mediaName.equals("file/topic/"+new_mediaName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(!old_mediaNameList.isEmpty()){
                for(String mediaName : old_mediaNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(mediaName));

                }

            }
        }

        //旧文件
        List<String> old_fileNameList = readPathResult.getFileNameList();
        if(old_fileNameList != null && !old_fileNameList.isEmpty()){
            Iterator<String> iter = old_fileNameList.iterator();
            while (iter.hasNext()) {
                String fileName = iter.next();
                for(String new_fileName : htmlFilterResult.htmlProcessingResult.getFileNameList()){
                    if(fileName.equals("file/topic/"+new_fileName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(!old_fileNameList.isEmpty()){
                for(String fileName : old_fileNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(fileName));

                }

            }
        }




        List<Thumbnail> thumbnailList = thumbnailRepository.findAllThumbnail_cache();
        if(thumbnailList != null && !thumbnailList.isEmpty()){


            if(oldImageList != null && !oldImageList.isEmpty()){
                List<ImageInfo> deleteBeforeImageList = new ArrayList<ImageInfo>();
                A:for(ImageInfo oldImageInfo : oldImageList){
                    if(!imageList.isEmpty()){
                        for(ImageInfo imageInfo :imageList){
                            if(oldImageInfo.getName().equals(imageInfo.getName())){
                                continue A;
                            }
                        }
                    }
                    deleteBeforeImageList.add(oldImageInfo);
                }
                if(!deleteBeforeImageList.isEmpty()){
                    //删除旧缩略图
                    fileComponent.deleteThumbnail(thumbnailList,oldImageList);
                }
            }

            //异步增加缩略图
            if(!imageList.isEmpty()){
                fileComponent.addThumbnail(thumbnailList,imageList);
            }
        }

        //上传文件编号
        String fileNumber = topicComponent.generateFileNumber(topic.getUserName(), topic.getIsStaff());

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //删除音视频锁
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult.getMediaNameList().isEmpty()){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //删除文件锁
        if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && !htmlFilterResult.htmlProcessingResult.getFileNameList().isEmpty()){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }

        //删除旧路径文件
        if(!oldPathFileList.isEmpty()){
            for(String oldPathFile :oldPathFileList){
                //如果验证不是当前用户上传的文件，则不删除
                if(!topicComponent.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
                    continue;
                }


                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(oldPathFile);
                if(state != null && !state){

                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"topic"+File.separator, "");

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
                }
            }
        }
    }


    /**
     * 获取修改话题界面信息
     * @param topicId 话题Id
     * @return
     */
    public Map<String,Object> getEditTopicViewModel(Long topicId){
        if(topicId == null || topicId <=0L){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        Topic topic = topicRepository.findById(topicId);
        if(topic == null){
            throw new BusinessException(Map.of("topicId", "话题不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(topic.getContent() != null && !topic.getContent().trim().isEmpty()){
            //处理富文本路径
            topic.setContent(fileComponent.processRichTextFilePath(topic.getContent(),"topic"));
        }


        Tag tag = tagRepository.findById(topic.getTagId());
        if(tag != null){
            topic.setTagName(tag.getName());
        }


        returnValue.put("topic", topic);

        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();
        returnValue.put("userGradeList", userGradeList);
        return returnValue;
    }


    /**
     * 删除话题
     * @param topicId 话题Id集合
     */
    public void deleteTopic(Long[] topicId){
        if(topicId == null || topicId.length ==0){
            throw new BusinessException(Map.of("topicId", "请选择话题"));
        }

        List<Long> topicIdList = Arrays.stream(topicId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (topicIdList.isEmpty()) {
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }

        List<Topic> topicList = topicRepository.findByIdList(topicIdList);
        if(topicList == null || topicList.isEmpty()) {
            throw new BusinessException(Map.of("topicId", "话题不存在"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof SysUsers){
            userId =((SysUsers)principal).getUserId();
            username =((SysUsers)principal).getUserAccount();
        }


        for(Topic topic : topicList){
            if(topic.getStatus() < 100){//标记删除
                int i = topicRepository.markDelete(topic.getId());

                User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                if(i >0 && user != null){
                    //修改话题状态
                    userRepository.softDeleteUserDynamicByTopicId(user.getId(),topic.getUserName(),topic.getId());
                }
                //更新索引
                topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
                topicCacheManager.deleteTopicCache(topic.getId());//删除缓存
            }else{//物理删除
                GiveRedEnvelope giveRedEnvelope = null;
                String userName = null;
                BigDecimal amount = null;
                Object paymentLogObject = null;

                if(topic.getGiveRedEnvelopeId() != null && !topic.getGiveRedEnvelopeId().isEmpty()){
                    giveRedEnvelope = redEnvelopeRepository.findById(topic.getGiveRedEnvelopeId());
                    if(giveRedEnvelope != null && (giveRedEnvelope.getRefundAmount() == null || giveRedEnvelope.getRefundAmount().compareTo(new BigDecimal("0"))==0)){
                        User user = userCacheManager.query_cache_findUserById(giveRedEnvelope.getUserId());
                        BigDecimal refundAmount = redEnvelopeComponent.refundAmount(giveRedEnvelope);

                        if(user != null && refundAmount.compareTo(new BigDecimal("0")) >0){

                            PaymentLog paymentLog = new PaymentLog();
                            paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(user.getId()));//支付流水号
                            paymentLog.setPaymentModule(140);//支付模块 140.话题返还红包
                            paymentLog.setSourceParameterId(topic.getGiveRedEnvelopeId());//参数Id
                            paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                            paymentLog.setOperationUserName(username);//操作用户名称
                            paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                            paymentLog.setAmount(refundAmount);//金额
                            paymentLog.setInterfaceProduct(0);//接口产品
                            paymentLog.setUserName(user.getUserName());//用户名称
                            paymentLog.setTimes(LocalDateTime.now());
                            paymentLog.setSourceParameterId(giveRedEnvelope.getId());
                            //金额日志
                            paymentLogObject = paymentComponent.createPaymentLogObject(paymentLog);


                            userName = user.getUserName();
                            amount = refundAmount;
                        }

                    }


                }


                String fileNumber = topicComponent.generateFileNumber(topic.getUserName(), topic.getIsStaff());

                int i = topicRepository.deleteTopic(topic.getId(),giveRedEnvelope,userName,amount,paymentLogObject);

                if(i>0){
                    //根据话题Id删除用户动态(话题下的评论和回复也同时删除)
                    userRepository.deleteUserDynamicByTopicId(topic.getId());
                }

                topicCacheManager.deleteTopicCache(topic.getId());//删除缓存

                topicCacheManager.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态
                if(topic.getGiveRedEnvelopeId() != null && !topic.getGiveRedEnvelopeId().isEmpty()){
                    //删除缓存
                    redEnvelopeCacheManager.delete_cache_findById(topic.getGiveRedEnvelopeId());
                }

                //更新索引
                topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),3));
                ReadPathResult readPathResult = textFilterComponent.readPathName(topic.getContent(),"topic");



                List<String> filePathList = new ArrayList<String>();

                List<Thumbnail> thumbnailList = thumbnailRepository.findAllThumbnail_cache();
                if(thumbnailList != null && thumbnailList.size() >0){
                    List<ImageInfo> beforeImageList = null;
                    if(topic.getImage() != null && !topic.getImage().trim().isEmpty()){
                        beforeImageList = jsonComponent.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
                    }
                    if(beforeImageList != null && beforeImageList.size() >0){
                        //删除旧缩略图
                        fileComponent.deleteThumbnail(thumbnailList,beforeImageList);
                    }

                }

                //删除图片
                List<String> imageNameList = readPathResult.getImageNameList();
                for(String imageName :imageNameList){
                    filePathList.add(FileUtil.toSystemPath(imageName));

                }

                //删除影音
                List<String> mediaNameList = readPathResult.getMediaNameList();
                for(String mediaName :mediaNameList){
                    filePathList.add(FileUtil.toSystemPath(mediaName));
                }
                //删除文件
                List<String> fileNameList = readPathResult.getFileNameList();
                for(String fileName :fileNameList){
                    filePathList.add(FileUtil.toSystemPath(fileName));
                }

                for(String filePath :filePathList){


                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
                        continue;
                    }

                    //替换路径中的..号
                    filePath = FileUtil.toRelativePath(filePath);
                    //删除旧路径文件
                    Boolean state = fileComponent.deleteFile(filePath);
                    if(state != null && !state){
                        //替换指定的字符，只替换第一次出现的
                        filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"topic"+File.separator, "");
                        //创建删除失败文件
                        fileComponent.failedStateFile("file"+ File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
                    }
                }

                //清空目录
                Boolean state = fileComponent.removeDirectory("file"+File.separator+"comment"+File.separator+topic.getId()+File.separator);
                if(state != null && !state){
                    //创建删除失败目录文件
                    fileComponent.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+"#"+topic.getId());
                }

            }


        }
    }

    /**
     * 还原话题
     * @param topicId 话题Id集合
     */
    public void reductionTopic(Long[] topicId){
        if(topicId == null || topicId.length ==0){
            throw new BusinessException(Map.of("topicId", "请选择话题"));
        }

        List<Long> topicIdList = Arrays.stream(topicId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (topicIdList.isEmpty()) {
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }

        List<Topic> topicList = topicRepository.findByIdList(topicIdList);
        if(topicList == null || topicList.isEmpty()) {
            throw new BusinessException(Map.of("topicId", "话题不存在"));
        }


        int i = topicRepository.reductionTopic(topicList);

        for(Topic topic :topicList){

            User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            if(i >0 && user != null){
                //修改话题状态
                userRepository.reductionUserDynamicByTopicId(user.getId(),topic.getUserName(),topic.getId());
            }

            //更新索引
            topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));
            topicCacheManager.deleteTopicCache(topic.getId());//删除缓存
        }
    }


    /**
     * 审核话题
     * @param topicId 话题Id
     */
    public void auditTopic(Long topicId){
        if(topicId == null || topicId <=0L){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        int i = topicRepository.updateTopicStatus(topicId, 20);

        Topic topic = topicCacheManager.queryTopicCache(topicId);
        if(i >0 && topic != null){
            User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            if(user != null){
                //修改话题状态
                userRepository.updateUserDynamicTopicStatus(user.getId(),topic.getUserName(),topic.getId(),20);
            }
        }

        //更新索引
        topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topicId),2));
        topicCacheManager.deleteTopicCache(topicId);//删除缓存
    }

    /**
     * 获取话题搜索
     * @param page 页码
     * @param dataSource 数据源
     * @param keyword 关键词
     * @param tagId 标签Id
     * @param tagName 标签名称
     * @param account 账号
     * @param start_postTime 起始发贴时间
     * @param end_postTime 结束发贴时间
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Topic> getSearch(int page, Integer dataSource,String keyword,String tagId,String tagName,String account,
                                     String start_postTime,String end_postTime,String fileServerAddress){

        if(dataSource == null){//如果数据源为空，则默认为lucene方式查询
            dataSource = 1;
        }
        //错误
        Map<String,String> errors = new HashMap<String,String>();

        String _keyword = null;//关键词
        Long _tagId = null;//标签
        String _userName = null;//用户名
        LocalDateTime _start_postTime = null;//发表时间 起始
        LocalDateTime _end_postTime= null;//发表时间	结束


        if(keyword != null && !keyword.trim().isEmpty()){
            _keyword = keyword.trim();
        }
        //标签
        if(tagId != null && !tagId.trim().isEmpty()){
            boolean tagId_verification = Verification.isPositiveInteger(tagId.trim());//正整数
            if(tagId_verification){
                _tagId = Long.parseLong(tagId.trim());
            }else{
                errors.put("tagId", "请选择标签");
            }
        }
        //账号
        if(account != null && !account.trim().isEmpty()){
            User user = userRepository.findUserByAccount(account.trim());
            if(user != null){
                _userName = user.getUserName();
            }else{
                _userName = account.trim();
            }
        }
        //起始发表时间
        if(start_postTime != null && !start_postTime.trim().isEmpty()){
            boolean start_postTimeVerification = Verification.isTime_minute(start_postTime.trim());
            if(start_postTimeVerification){
                _start_postTime = LocalDateTime.parse(start_postTime.trim(), FORMATTER);
            }else{
                errors.put("start_postTime", "请填写正确的日期");
            }
        }
        //结束发表时间
        if(end_postTime != null && !end_postTime.trim().isEmpty()){
            boolean end_postTimeVerification = Verification.isTime_minute(end_postTime.trim());
            if(end_postTimeVerification){
                _end_postTime = LocalDateTime.parse(end_postTime.trim(), FORMATTER);
            }else{
                errors.put("end_postTime", "请填写正确的日期");
            }
        }

        if(_start_postTime != null && _end_postTime != null){
            if(_start_postTime.isAfter(_end_postTime)){
                // 起始时间比结束时间大
                errors.put("start_postTime", "起始时间不能比结束时间大");
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        //调用分页算法代码
        PageView<Topic> pageView = new PageView<Topic>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);

        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();

        if(dataSource.equals(1)){//lucene索引
            QueryResult<Topic> qr = topicLuceneComponent.findIndexByCondition(pageView.getCurrentpage(), pageView.getMaxresult(), _keyword, _tagId, _userName, _start_postTime, _end_postTime, null, 1);

            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                List<Long> topicIdList =  new ArrayList<Long>();//话题Id集合

                List<Topic> new_topicList = new ArrayList<Topic>();
                for(Topic topic : qr.getResultlist()){
                    topicIdList.add(topic.getId());
                }
                if(topicIdList != null && topicIdList.size() >0){
                    List<Topic> topicList = topicRepository.findByIdList(topicIdList);
                    if(topicList != null && topicList.size() >0){
                        for(Topic old_t : qr.getResultlist()){
                            for(Topic pi : topicList){
                                if(pi.getId().equals(old_t.getId())){
                                    pi.setTitle(old_t.getTitle());
                                    pi.setContent(old_t.getContent());


                                    if(pi.getIsStaff()){//如果为员工
                                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(pi.getUserName());
                                        if(sysUsers != null){
                                            pi.setNickname(sysUsers.getNickname());
                                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                                pi.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                                                pi.setAvatarName(sysUsers.getAvatarName());
                                            }
                                        }
                                        pi.setAccount(pi.getUserName());//员工用户名和账号是同一个
                                    }else{
                                        User user = userCacheManager.query_cache_findUserByUserName(pi.getUserName());
                                        if(user != null){
                                            pi.setAccount(user.getAccount());
                                            pi.setNickname(user.getNickname());
                                            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                                                pi.setAvatarPath(fileServerAddress+user.getAvatarPath());
                                                pi.setAvatarName(user.getAvatarName());
                                            }
                                        }
                                    }
                                    new_topicList.add(pi);
                                    break;
                                }
                            }
                        }
                    }
                }

                QueryResult<Topic> _qr = new QueryResult<Topic>();
                //把查询结果设进去
                _qr.setResultlist(new_topicList);
                _qr.setTotalrecord(qr.getTotalrecord());
                pageView.setQueryResult(_qr);
            }
        }else{//数据库
            String param = "";//sql参数
            List<Object> paramValue = new ArrayList<Object>();//sql参数值

            if(_keyword != null){//标题
                param += " and (o.title like ?"+(paramValue.size()+1)+" escape '/' ";
                paramValue.add("%"+ cms.utils.StringUtil.escapeSQLLike(_keyword)+"%");
                //内容
                param += " or o.content like ?"+(paramValue.size()+1)+" escape '/' )";
                paramValue.add("%"+cms.utils.StringUtil.escapeSQLLike(_keyword)+"%");
            }
            if(_tagId != null && _tagId >0){//标签
                Tag tag = tagRepository.findById(_tagId);
                if(tag != null){
                    param += " and o.tagIdGroup like ?"+(paramValue.size()+1)+" escape '/' ";
                    paramValue.add(cms.utils.StringUtil.escapeSQLLike(tag.getParentIdGroup()+_tagId)+",%");
                }
            }

            if(_userName != null && !"".equals(_userName)){//用户
                param += " and o.userName =?"+(paramValue.size()+1);
                paramValue.add(_userName);
            }
            if(_start_postTime != null){//起始发表时间
                param += " and o.postTime >= ?"+(paramValue.size()+1);

                paramValue.add(_start_postTime);
            }
            if(_end_postTime != null){//结束发表时间
                param += " and o.postTime <= ?"+(paramValue.size()+1);
                paramValue.add(_end_postTime);
            }
            //删除第一个and
            param = StringUtils.difference(" and", param);


            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
            orderby.put("id", "desc");//排序
            QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class,firstindex, pageView.getMaxresult(), param, paramValue.toArray(),orderby);

            if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(Topic t :qr.getResultlist()){
                    if(t.getTitle() != null && !t.getTitle().trim().isEmpty()){
                        //转义
                        t.setTitle(HtmlEscape.escape(t.getTitle()));
                    }
                    if(t.getContent() != null && !t.getContent().trim().isEmpty()){
                        t.setContent(textFilterComponent.filterText(t.getContent()));
                        if(t.getContent().length() > 190){

                            t.setContent(t.getContent().substring(0, 190));
                        }
                    }


                    if(t.getIsStaff()){//如果为员工
                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(t.getUserName());
                        if(sysUsers != null){
                            t.setNickname(sysUsers.getNickname());
                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                t.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                                t.setAvatarName(sysUsers.getAvatarName());
                            }
                        }
                        t.setAccount(t.getUserName());//员工用户名和账号是同一个
                    }else{
                        User user = userCacheManager.query_cache_findUserByUserName(t.getUserName());
                        if(user != null){
                            t.setAccount(user.getAccount());
                            t.setNickname(user.getNickname());
                            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                                t.setAvatarPath(fileServerAddress+user.getAvatarPath());
                                t.setAvatarName(user.getAvatarName());
                            }
                        }
                    }
                }
            }

            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
        }



        if(pageView.getRecords() != null && pageView.getRecords().size() >0){
            List<Tag> tagList = tagRepository.findAllTag_cache();
            if(tagList != null && tagList.size() >0){
                for(Topic t :pageView.getRecords()){
                    for(Tag tag :tagList){
                        if(t.getTagId().equals(tag.getId())){
                            t.setTagName(tag.getName());
                            break;
                        }

                    }

                }
            }

        }

        return pageView;
    }


    /**
     * 获取全部待审核话题
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Topic> getPendingTopics(int page,String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.status=?"+ (params.size()+1));
        params.add(10);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Topic> pageView = new PageView<Topic>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Tag> tagList = tagRepository.findAllTag();
            if(tagList != null && tagList.size() >0){
                for(Topic topic : qr.getResultlist()){
                    for(Tag tag : tagList){
                        if(topic.getTagId().equals(tag.getId())){
                            topic.setTagName(tag.getName());
                            break;
                        }
                    }

                }
            }
            for(Topic topic : qr.getResultlist()){

                if(topic.getIsStaff()){//如果为员工
                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(topic.getUserName());
                    if(sysUsers != null){
                        topic.setNickname(sysUsers.getNickname());
                        if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                            topic.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                            topic.setAvatarName(sysUsers.getAvatarName());
                        }
                    }
                    topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
                }else{
                    User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                    if(user != null){
                        topic.setAccount(user.getAccount());
                        topic.setNickname(user.getNickname());
                        if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                            topic.setAvatarPath(fileServerAddress+user.getAvatarPath());
                            topic.setAvatarName(user.getAvatarName());
                        }
                    }
                }
            }
        }

        pageView.setQueryResult(qr);


        return pageView;
    }

    /**
     * 获取全部待审核评论
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Comment> getAllPendingComments(int page,String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.status=?"+ (params.size()+1));
        params.add(10);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Comment> pageView = new PageView<Comment>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Comment> qr = commentRepository.getScrollData(Comment.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            for(Comment o :qr.getResultlist()){
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!topicIdList.contains(o.getTopicId())){
                    topicIdList.add(o.getTopicId());
                }
            }
            List<Topic> topicList = topicRepository.findTitleByIdList(topicIdList);
            if(topicList != null && topicList.size() >0){
                for(Comment o :qr.getResultlist()){

                    if(o.getIsStaff()){//如果为员工
                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(o.getUserName());
                        if(sysUsers != null){
                            o.setNickname(sysUsers.getNickname());
                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                o.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                                o.setAvatarName(sysUsers.getAvatarName());
                            }
                        }
                        o.setAccount(o.getUserName());//员工用户名和账号是同一个
                    }else{
                        User user = userCacheManager.query_cache_findUserByUserName(o.getUserName());
                        if(user != null){
                            o.setAccount(user.getAccount());
                            o.setNickname(user.getNickname());
                            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                                o.setAvatarPath(fileServerAddress+user.getAvatarPath());
                                o.setAvatarName(user.getAvatarName());
                            }
                        }
                    }
                    for(Topic topic : topicList){
                        if(topic.getId().equals(o.getTopicId())){
                            o.setTopicTitle(topic.getTitle());
                            break;
                        }
                    }

                }
            }

        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取全部待审核回复
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Reply> getAllPendingReplies(int page,String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.status=?"+ (params.size()+1));
        params.add(10);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Reply> pageView = new PageView<Reply>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Reply> qr = commentRepository.getScrollData(Reply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            for(Reply o :qr.getResultlist()){
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!topicIdList.contains(o.getTopicId())){
                    topicIdList.add(o.getTopicId());
                }




                if(o.getIsStaff()){//如果为员工
                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(o.getUserName());
                    if(sysUsers != null){
                        o.setNickname(sysUsers.getNickname());
                        if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                            o.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                            o.setAvatarName(sysUsers.getAvatarName());
                        }
                    }
                    o.setAccount(o.getUserName());//员工用户名和账号是同一个
                }else{
                    User user = userCacheManager.query_cache_findUserByUserName(o.getUserName());
                    if(user != null){
                        o.setAccount(user.getAccount());
                        o.setNickname(user.getNickname());
                        if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                            o.setAvatarPath(fileServerAddress+user.getAvatarPath());
                            o.setAvatarName(user.getAvatarName());
                        }
                    }
                }

            }
            List<Topic> topicList = topicRepository.findTitleByIdList(topicIdList);
            if(topicList != null && topicList.size() >0){
                for(Reply o :qr.getResultlist()){
                    for(Topic topic : topicList){
                        if(topic.getId().equals(o.getTopicId())){
                            o.setTopicTitle(topic.getTitle());
                            break;
                        }
                    }

                }
            }

        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取话题取消隐藏列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> getUnhiddenTopics(int page,Long topicId,String fileServerAddress){
        if(topicId == null || topicId <=0L){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<TopicUnhide> pageView = new PageView<TopicUnhide>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        QueryResult<TopicUnhide> qr = topicRepository.findTopicUnhidePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(TopicUnhide o :qr.getResultlist()){

                User user = userCacheManager.query_cache_findUserByUserName(o.getUserName());
                if(user != null){
                    o.setAccount(user.getAccount());
                    o.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        o.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        o.setAvatarName(user.getAvatarName());
                    }
                }


            }

        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        Topic topic = topicCacheManager.queryTopicCache(topicId);
        if(topic != null){
            returnValue.put("currentTopic", topic);
        }

        returnValue.put("pageView", pageView);

        return returnValue;
    }

    /**
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @return HtmlFilterResult Html过滤结果
     */
    private TopicServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {
        //过滤标签
        content = textFilterComponent.filterTag(request,content);
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"topic",null);
        return new TopicServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);
    }

    /**
     * 校验表单字段
     * @param topicRequest 话题表单
     */
    private void validateInput(TopicRequest topicRequest) {
        Map<String, String> errors = new HashMap<>();

        if(topicRequest.getTagId() == null || topicRequest.getTagId() <=0L){
            errors.put("tagId", "标签不能为空");
        }

        if(topicRequest.getTitle() != null && !topicRequest.getTitle().trim().isEmpty()){
            if(topicRequest.getTitle().length() >150){
                errors.put("title", "不能大于150个字符");
            }
        }else{
            errors.put("title", "标题不能为空");
        }

        if(topicRequest.getSort() != null){
            if(topicRequest.getSort() >99999999){
                errors.put("sort", "不能超过8位数字");
            }
        }else{
            errors.put("sort", "排序不能为空");
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }
    }
}