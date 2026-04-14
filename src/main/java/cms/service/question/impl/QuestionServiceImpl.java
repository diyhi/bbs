package cms.service.question.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.lucene.QuestionLuceneComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.payment.PaymentComponent;
import cms.component.question.AnswerComponent;
import cms.component.question.QuestionCacheManager;
import cms.component.question.QuestionComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.user.PointComponent;
import cms.component.user.PointLogConfig;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleComponent;
import cms.config.BusinessException;
import cms.dto.*;
import cms.dto.question.AppendQuestionRequest;
import cms.dto.question.QuestionRequest;
import cms.model.message.Remind;
import cms.model.payment.PaymentLog;
import cms.model.question.*;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.model.vote.VoteOption;
import cms.model.vote.VoteRecord;
import cms.model.vote.VoteTheme;
import cms.repository.message.RemindRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionIndexRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.question.QuestionService;
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
 * 问题服务
 */
@Service
public class QuestionServiceImpl implements QuestionService {
    private static final Logger logger = LogManager.getLogger(QuestionServiceImpl.class);


    @Resource
    QuestionTagRepository questionTagRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    UserCacheManager userCacheManager;
    @Resource
    StaffCacheManager staffCacheManager;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    AnswerComponent answerComponent;
    @Resource
    AnswerRepository answerRepository;
    @Resource
    QuestionIndexRepository questionIndexRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    QuestionCacheManager questionCacheManager;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource
    PointComponent pointComponent;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    QuestionComponent questionComponent;
    @Resource
    QuestionLuceneComponent questionLuceneComponent;
    @Resource
    RemindRepository remindRepository;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;
    @Resource RemindCacheManager remindCacheManager;


    private final List<Integer> voteMaxChoices = Arrays.asList(-1,1);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {
    }

    /**
     * 获取问题列表
     *
     * @param page              页码
     * @param visible           是否可见
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Question> getQuestionList(int page, Boolean visible, String fileServerAddress) {
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        if (visible != null && !visible) {
            jpql.append(" and o.status>?" + (params.size() + 1));
            params.add(100);
        } else {
            jpql.append(" and o.status<?" + (params.size() + 1));
            params.add(100);
        }
        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Question> pageView = new PageView<Question>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);
        //当前页
        int firstindex = (page - 1) * pageView.getMaxresult();
        //排序
        LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Question> qr = questionRepository.getScrollData(Question.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(), orderby);

        if (qr != null && qr.getResultlist() != null && qr.getResultlist().size() > 0) {
            List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();

            if (questionTagList != null && questionTagList.size() > 0) {
                for (Question question : qr.getResultlist()) {
                    List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
                    if (questionTagAssociationList != null && questionTagAssociationList.size() > 0) {
                        for (QuestionTag questionTag : questionTagList) {
                            for (QuestionTagAssociation questionTagAssociation : questionTagAssociationList) {
                                if (questionTagAssociation.getQuestionTagId().equals(questionTag.getId())) {
                                    questionTagAssociation.setQuestionTagName(questionTag.getName());
                                    question.addQuestionTagAssociation(questionTagAssociation);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            for (Question question : qr.getResultlist()) {

                if (question.getIsStaff()) {//如果为员工
                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(question.getUserName());
                    if (sysUsers != null) {
                        question.setNickname(sysUsers.getNickname());
                        if (sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()) {
                            question.setAvatarPath(fileServerAddress + sysUsers.getAvatarPath());
                            question.setAvatarName(sysUsers.getAvatarName());
                        }
                    }
                    question.setAccount(question.getUserName());//员工用户名和账号是同一个
                } else {
                    User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
                    if (user != null) {
                        question.setAccount(user.getAccount());
                        question.setNickname(user.getNickname());
                        if (user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()) {
                            question.setAvatarPath(fileServerAddress + user.getAvatarPath());
                            question.setAvatarName(user.getAvatarName());
                        }
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取问题明细
     *
     * @param questionId 问题Id
     * @param answerId   答案Id
     * @param page       页码
     * @param request    请求信息
     * @return
     */
    public Map<String, Object> getQuestionDetail(Long questionId, Long answerId, Integer page, HttpServletRequest request) {
        if (questionId == null || questionId <= 0L) {
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        Question question = questionRepository.findById(questionId);
        if (question == null) {
            throw new BusinessException(Map.of("questionId", "问题不存在"));
        }

        Map<String, Object> returnValue = new LinkedHashMap<String, Object>();
        if (question.getIp() != null && !question.getIp().trim().isEmpty()) {
            question.setIpAddress(IpAddress.queryAddress(question.getIp().trim()));
        }
        if (question.getContent() != null && !question.getContent().trim().isEmpty()) {
            //处理富文本路径
            question.setContent(fileComponent.processRichTextFilePath(question.getContent(), "question"));
        }

        //删除最后一个逗号
        String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

        List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent + "]", new TypeReference<List<AppendQuestionItem>>() {
        });
        if (appendQuestionItemList != null && appendQuestionItemList.size() > 0) {
            for (AppendQuestionItem appendQuestionItem : appendQuestionItemList) {
                if (appendQuestionItem.getContent() != null && !appendQuestionItem.getContent().trim().isEmpty()) {
                    //处理富文本路径
                    appendQuestionItem.setContent(fileComponent.processRichTextFilePath(appendQuestionItem.getContent(), "question"));
                }
            }

        }

        question.setAppendQuestionItemList(appendQuestionItemList);


        if (question.getIsStaff() == false) {//会员
            User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            if (user != null) {
                question.setAccount(user.getAccount());
                question.setNickname(user.getNickname());
                question.setAvatarPath(fileComponent.fileServerAddress(request) + user.getAvatarPath());
                question.setAvatarName(user.getAvatarName());

                List<String> userRoleNameList = userRoleComponent.queryUserRoleName(user.getUserName());
                if (userRoleNameList != null && userRoleNameList.size() > 0) {
                    question.setUserRoleNameList(userRoleNameList);//用户角色名称集合
                }
            }

        } else {
            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(question.getUserName());
            if (sysUsers != null) {
                question.setNickname(sysUsers.getNickname());
                if (sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()) {
                    question.setAvatarPath(fileComponent.fileServerAddress(request) + sysUsers.getAvatarPath());
                    question.setAvatarName(sysUsers.getAvatarName());
                }
            }
            question.setAccount(question.getUserName());//员工用户名和账号是同一个

        }
        List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();

        if (questionTagList != null && questionTagList.size() > 0) {
            List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
            if (questionTagAssociationList != null && questionTagAssociationList.size() > 0) {
                for (QuestionTag questionTag : questionTagList) {
                    for (QuestionTagAssociation questionTagAssociation : questionTagAssociationList) {
                        if (questionTagAssociation.getQuestionTagId().equals(questionTag.getId())) {
                            questionTagAssociation.setQuestionTagName(questionTag.getName());
                            question.addQuestionTagAssociation(questionTagAssociation);
                            break;
                        }
                    }
                }
            }
            question.setQuestionTagAssociationList(questionTagAssociationList);
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        returnValue.put("question", question);
        returnValue.put("availableTag", answerComponent.availableTag());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());



        PageForm pageForm = new PageForm();
        pageForm.setPage(page);

        if (answerId != null && answerId > 0L && page == null) {
            Long row = answerRepository.findRowByAnswerId(answerId, questionId);
            if (row != null && row > 0L) {

                int _page = Integer.parseInt(String.valueOf(row)) / settingRepository.findSystemSetting_cache().getBackstagePageNumber();
                if (Integer.parseInt(String.valueOf(row)) % settingRepository.findSystemSetting_cache().getBackstagePageNumber() > 0) {//余数大于0要加1

                    _page = _page + 1;
                }
                pageForm.setPage(_page);

            }
        }

        //答案
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        PageView<Answer> pageView = new PageView<Answer>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), pageForm.getPage(), 10);

        //当前页
        int firstindex = (pageForm.getPage() - 1) * pageView.getMaxresult();
        //排序
        LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();

        if (questionId != null && questionId > 0L) {
            jpql.append(" o.questionId=?" + (params.size() + 1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
            params.add(questionId);//设置o.parentId=?2参数
        }

        orderby.put("postTime", "asc");//根据sort字段降序排序
        QueryResult<Answer> qr = answerRepository.getScrollData(Answer.class, firstindex, pageView.getMaxresult(), jpql.toString(), params.toArray(), orderby);


        List<Long> answerIdList = new ArrayList<Long>();
        List<Answer> answerList = qr.getResultlist();


        if (answerList != null && answerList.size() > 0) {
            for (Answer answer : answerList) {
                answerIdList.add(answer.getId());
                if (answer.getContent() != null && !"".equals(answer.getContent().trim())) {
                    //处理富文本路径
                    answer.setContent(fileComponent.processRichTextFilePath(answer.getContent(), "answer"));
                }

            }
        }

        Map<String, List<String>> userRoleNameMap = new HashMap<String, List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
        if (answerList != null && answerList.size() > 0) {
            for (Answer answer : answerList) {
                if (answer.getIsStaff() == false) {//会员
                    User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
                    if (user != null) {
                        answer.setAccount(user.getAccount());
                        answer.setNickname(user.getNickname());
                        answer.setAvatarPath(fileComponent.fileServerAddress(request) + user.getAvatarPath());
                        answer.setAvatarName(user.getAvatarName());
                        userRoleNameMap.put(answer.getUserName(), null);
                    }
                } else {
                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(answer.getUserName());
                    if (sysUsers != null) {
                        answer.setNickname(sysUsers.getNickname());
                        if (sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()) {
                            answer.setAvatarPath(fileComponent.fileServerAddress(request) + sysUsers.getAvatarPath());
                            answer.setAvatarName(sysUsers.getAvatarName());
                        }
                    }
                    answer.setAccount(answer.getUserName());//员工用户名和账号是同一个

                }

                if (answer.getIp() != null && !answer.getIp().trim().isEmpty()) {
                    answer.setIpAddress(IpAddress.queryAddress(answer.getIp()));
                }
            }

        }
        if (answerIdList.size() > 0) {
            List<AnswerReply> answerReplyList = answerRepository.findReplyByAnswerId(answerIdList);
            if (answerReplyList != null && answerReplyList.size() > 0) {
                for (Answer answer : answerList) {
                    for (AnswerReply answerReply : answerReplyList) {
                        if (answerReply.getIsStaff() == false) {//会员
                            User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                            if (user != null) {
                                answerReply.setAccount(user.getAccount());
                                answerReply.setNickname(user.getNickname());
                                answerReply.setAvatarPath(fileComponent.fileServerAddress(request) + user.getAvatarPath());
                                answerReply.setAvatarName(user.getAvatarName());
                                userRoleNameMap.put(answerReply.getUserName(), null);
                            }

                        } else {
                            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(answerReply.getUserName());
                            if (sysUsers != null) {
                                answerReply.setNickname(sysUsers.getNickname());
                                if (sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()) {
                                    answerReply.setAvatarPath(fileComponent.fileServerAddress(request) + sysUsers.getAvatarPath());
                                    answerReply.setAvatarName(sysUsers.getAvatarName());
                                }
                            }
                            answerReply.setAccount(answerReply.getUserName());//员工用户名和账号是同一个

                        }
                        if (answerReply.getFriendUserName() != null && !answerReply.getFriendUserName().trim().isEmpty()) {
                            if (answerReply.getIsFriendStaff() == false) {//会员
                                User user = userCacheManager.query_cache_findUserByUserName(answerReply.getFriendUserName());
                                if (user != null) {
                                    answerReply.setFriendAccount(user.getAccount());
                                    answerReply.setFriendNickname(user.getNickname());
                                    answerReply.setFriendAvatarPath(fileComponent.fileServerAddress(request) + user.getAvatarPath());
                                    answerReply.setFriendAvatarName(user.getAvatarName());
                                }

                            } else {
                                SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(answerReply.getFriendUserName());
                                if (sysUsers != null) {
                                    answerReply.setFriendNickname(sysUsers.getNickname());
                                    if (sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()) {
                                        answerReply.setFriendAvatarPath(fileComponent.fileServerAddress(request) + sysUsers.getAvatarPath());
                                        answerReply.setFriendAvatarName(sysUsers.getAvatarName());
                                    }
                                }
                                answerReply.setFriendAccount(answerReply.getFriendUserName());//员工用户名和账号是同一个

                            }
                        }
                        if (answer.getId().equals(answerReply.getAnswerId())) {
                            answerReply.setContent(answerReply.getContent());
                            answer.addAnswerReply(answerReply);
                        }
                    }
                    //回复排序
                    answerComponent.replySort(answer.getAnswerReplyList());
                }
            }
        }
        if (userRoleNameMap != null && userRoleNameMap.size() > 0) {
            for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey());
                entry.setValue(roleNameList);
            }
        }
        if (answerList != null && answerList.size() > 0) {
            for (Answer answer : answerList) {
                //用户角色名称集合
                for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                    if (entry.getKey().equals(answer.getUserName())) {
                        List<String> roleNameList = entry.getValue();
                        if (roleNameList != null && roleNameList.size() > 0) {
                            answer.setUserRoleNameList(roleNameList);
                        }
                        break;
                    }
                }

                if (answer.getAnswerReplyList() != null && answer.getAnswerReplyList().size() > 0) {
                    for (AnswerReply reply : answer.getAnswerReplyList()) {
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if (entry.getKey().equals(reply.getUserName())) {
                                List<String> roleNameList = entry.getValue();
                                if (roleNameList != null && roleNameList.size() > 0) {
                                    reply.setUserRoleNameList(roleNameList);
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

        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (obj instanceof UserDetails) {
            username = ((UserDetails) obj).getUsername();
        }
        returnValue.put("userName", username);

        return returnValue;
    }

    /**
     * 获取问题添加界面信息
     * @return
     */
    public Map<String, Object> getAddQuestionViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 添加问题
     * @param questionRequest 问题表单
     * @param request 请求信息
     */
    public void addQuestion(QuestionRequest questionRequest, HttpServletRequest request){

        Map<String, String> errors = new HashMap<>();
        validateInput(questionRequest,errors);
        Question question = new Question();
        VoteTheme voteTheme = null;
        List<String> optionTextList = new ArrayList<String>();
        LocalDateTime voteEndDate = null;
        LocalDateTime voteCreateDate = LocalDateTime.now();


        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }


        LinkedHashSet <QuestionTagAssociation> questionTagAssociationList = new LinkedHashSet<QuestionTagAssociation>();
        if(questionRequest.getTagId() != null && questionRequest.getTagId().length >0){
            List<QuestionTag> questionTagList =  questionTagRepository.findAllQuestionTag();
            for(Long id :questionRequest.getTagId()){
                if(id != null && id >0L){
                    QuestionTagAssociation questionTagAssociation = new QuestionTagAssociation();
                    questionTagAssociation.setQuestionTagId(id);
                    for(QuestionTag questionTag : questionTagList){
                        if(questionTag.getId().equals(id) && questionTag.getChildNodeNumber().equals(0)){
                            questionTagAssociation.setQuestionTagName(questionTag.getName());
                            questionTagAssociationList.add(questionTagAssociation);
                            break;
                        }

                    }
                }
            }
        }else{
            errors.put("tagId", "标签不能为空");
        }



        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        // 处理内容过滤和文件路径
        QuestionServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, questionRequest.getContent(), questionRequest.getIsMarkdown(), questionRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        //不含标签内容
        String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(value));

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



        //摘要
        if(!trimSpace.isEmpty()){
            if(trimSpace.length() >180){
                question.setSummary(trimSpace.substring(0, 180)+"..");
            }else{
                question.setSummary(trimSpace+"..");
            }
        }

        question.setTitle(questionRequest.getTitle());
        question.setAllow(questionRequest.getAllow());
        question.setStatus(questionRequest.getStatus());
        LocalDateTime d = LocalDateTime.now();
        question.setPostTime(d);
        question.setLastAnswerTime(d);
        question.setIp(IpAddress.getClientIpAddress(request));
        question.setUserName(username);
        question.setIsStaff(true);
        question.setContent(value);
        question.setSort(questionRequest.getSort());
        question.setIsMarkdown(questionRequest.getIsMarkdown() == null ? false:questionRequest.getIsMarkdown());
        question.setMarkdownContent(htmlFilterResult.formatterMarkdown);

        questionRepository.saveQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList),null,null,null,null,voteTheme);

        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),1));



        //上传文件编号
        String fileNumber = "a"+userId;


        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){

                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }

                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //音视频
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult.getMediaNameList().isEmpty()){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //文件
        if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && !htmlFilterResult.htmlProcessingResult.getFileNameList().isEmpty()){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
    }

    /**
     * 获取修改问题界面信息
     * @param questionId 问题Id
     * @return
     */
    public Map<String, Object> getEditQuestionViewModel(Long questionId){
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
        Question question = questionRepository.findById(questionId);
        if(question == null){
            throw new BusinessException(Map.of("questionId", "问题不存在"));
        }

        if(question.getContent() != null && !question.getContent().trim().isEmpty()){
            //处理富文本路径
            question.setContent(fileComponent.processRichTextFilePath(question.getContent(),"question"));
        }





        List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();
        if(questionTagList != null && questionTagList.size() >0){

            List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
            if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                for(QuestionTag questionTag : questionTagList){
                    for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
                        if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
                            questionTagAssociation.setQuestionTagName(questionTag.getName());
                            question.addQuestionTagAssociation(questionTagAssociation);
                            break;
                        }
                    }
                }
            }

        }

        User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
        if(user != null){
            returnValue.put("maxDeposit",user.getDeposit());//允许使用的预存款
            returnValue.put("maxPoint",user.getPoint());//允许使用的积分
        }
        returnValue.put("question", question);
        return returnValue;
    }

    /**
     * 修改问题
     * @param questionRequest 问题表单
     * @param request 请求信息
     */
    public void editQuestion(QuestionRequest questionRequest, HttpServletRequest request){
        if(questionRequest.getStatus() == null){
            throw new BusinessException(Map.of("status", "问题状态不能为空"));
        }
        if(questionRequest.getQuestionId() == null || questionRequest.getQuestionId() <=0L){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }

        Question question = questionRepository.findById(questionRequest.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("question", "问题不存在"));
        }
        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
        Integer old_status = question.getStatus();
        String old_voteThemeId = question.getVoteThemeId();
        String old_content = question.getContent();

        VoteTheme voteTheme = null;
        List<VoteOption> voteOptionList = null;
        VoteTheme new_voteTheme = null;//新增投票主题
        VoteTheme update_voteTheme = null;//修改投票主题
        String deleteVoteThemeId = null;//删除投票
        List<VoteOption> add_voteOptionList = new ArrayList<VoteOption>();
        List<VoteOption> edit_voteOptionList = new ArrayList<VoteOption>();
        List<String> delete_voteOptionIdList = new ArrayList<String>();


        Map<String, String> errors = new HashMap<>();
        validateInput(questionRequest,errors);





        String username = "";//用户名称
        String userId = "";//用户Id
        Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof SysUsers){
            userId =((SysUsers)principal).getUserId();
            username =((SysUsers)principal).getUserAccount();
        }
        LinkedHashSet <QuestionTagAssociation> questionTagAssociationList = new LinkedHashSet<QuestionTagAssociation>();
        if(questionRequest.getTagId() != null && questionRequest.getTagId().length >0){
            List<QuestionTag> questionTagList =  questionTagRepository.findAllQuestionTag();
            for(Long id :questionRequest.getTagId()){
                if(id != null && id >0L){
                    QuestionTagAssociation questionTagAssociation = new QuestionTagAssociation();
                    questionTagAssociation.setQuestionTagId(id);
                    for(QuestionTag questionTag : questionTagList){
                        if(questionTag.getId().equals(id) && questionTag.getChildNodeNumber().equals(0)){
                            questionTagAssociation.setQuestionTagName(questionTag.getName());
                            questionTagAssociationList.add(questionTagAssociation);
                            break;
                        }

                    }
                }
            }
        }else{
            errors.put("tagId", "标签不能为空");
        }

        User user = null;
        if(!question.getIsStaff()){
            user = userRepository.findUserByUserName(question.getUserName());//查询用户数据
            if(question.getAdoptionAnswerId().equals(0L)){//未采纳答案的问题才允许修改赏金
                if(questionRequest.getPoint() != null){
                    if(questionRequest.getPoint() <0L){
                        errors.put("point","不能小于0");
                    }else if(questionRequest.getPoint() >99999999){
                        errors.put("point", "不能超过8位数字");
                    }else if(questionRequest.getPoint() > user.getPoint()){
                        errors.put("point","不能大于账户积分");
                    }
                }

                if(user != null && questionRequest.getAmount() != null){
                    if(questionRequest.getAmount().compareTo(BigDecimal.ZERO) <0){
                        errors.put("amount","不能小于0");
                    }else if(questionRequest.getAmount().compareTo(new BigDecimal("999999999")) >0){
                        errors.put("amount", "不能大于999999999");
                    }else if(questionRequest.getAmount().compareTo(user.getDeposit()) >0){
                        errors.put("amount", "不能大于账户预存款");
                    }
                }
            }
        }

        LocalDateTime voteCreateDate = question.getPostTime();//投票主题创建时间
        LocalDateTime voteEndDate= null;//截止日期
        List<VoteOption> temp_voteOptionList = new ArrayList<VoteOption>();




        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        // 处理内容过滤和文件路径
        QuestionServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, questionRequest.getContent(), questionRequest.getIsMarkdown(), questionRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        //不含标签内容
        String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(value));

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

        //摘要
        if(!trimSpace.isEmpty()){
            if(trimSpace.length() >180){
                question.setSummary(trimSpace.substring(0, 180)+"..");
            }else{
                question.setSummary(trimSpace+"..");
            }
        }



        //变更积分符号
        boolean changePointSymbol = true;//true：加号  false：减号
        //变更积分
        Long changePoint = 0L;
        //变更金额符号
        boolean changeAmountSymbol = true;//true：加号  false：减号
        //变更金额
        BigDecimal changeAmount = new BigDecimal("0");


        if(questionRequest.getPoint() != null && questionRequest.getPoint() > question.getPoint()){
            changePoint = questionRequest.getPoint() - question.getPoint();
        }
        if(questionRequest.getPoint() != null && questionRequest.getPoint() < question.getPoint()){
            changePointSymbol = false;
            changePoint = question.getPoint() - questionRequest.getPoint();
        }

        if(questionRequest.getAmount() != null && questionRequest.getAmount().compareTo(question.getAmount()) > 0){
            changeAmount = questionRequest.getAmount().subtract(question.getAmount());
        }
        if(questionRequest.getAmount() != null && questionRequest.getAmount().compareTo(question.getAmount()) < 0){
            changeAmountSymbol = false;
            changeAmount = question.getAmount().subtract(questionRequest.getAmount());
        }

        //用户悬赏积分日志
        Object pointLogObject = null;
        //用户悬赏金额日志
        Object paymentLogObject = null;

        LocalDateTime time =  LocalDateTime.now();


        if(!question.getIsStaff() && user != null && changePoint>0L){//如果有变更积分
            question.setPoint(questionRequest.getPoint());

            if(!question.getIsStaff()){
                PointLog reward_pointLog = new PointLog();
                reward_pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
                reward_pointLog.setModule(1200);//1200.调整赏金
                reward_pointLog.setParameterId(question.getId());//参数Id
                reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                reward_pointLog.setOperationUserName(username);//操作用户名称
                reward_pointLog.setPointState(changePointSymbol ? 2 :1);//2:账户支出
                reward_pointLog.setPoint(changePoint);//积分
                reward_pointLog.setUserName(user.getUserName());//用户名称
                reward_pointLog.setRemark("");
                reward_pointLog.setTimes(time);
                pointLogObject = pointComponent.createPointLogObject(reward_pointLog);
            }
        }
        if(!question.getIsStaff() && user != null && changeAmount.compareTo(new BigDecimal("0")) >0){//如果有变更金额
            question.setAmount(questionRequest.getAmount());

            PaymentLog reward_paymentLog = new PaymentLog();
            reward_paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(user.getId()));//支付流水号
            reward_paymentLog.setPaymentModule(110);//支付模块 110.调整赏金
            reward_paymentLog.setSourceParameterId(String.valueOf(question.getId()));//参数Id
            reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
            reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
            reward_paymentLog.setAmountState(changeAmountSymbol ? 2 :1);//金额状态  1:账户存入  2:账户支出
            reward_paymentLog.setAmount(changeAmount);//金额
            reward_paymentLog.setInterfaceProduct(0);//接口产品
            reward_paymentLog.setUserName(user.getUserName());//用户名称
            reward_paymentLog.setTimes(time);
            paymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);

        }

        question.setTitle(questionRequest.getTitle());
        question.setAllow(questionRequest.getAllow());
        question.setStatus(questionRequest.getStatus());
        question.setContent(value);
        question.setSort(questionRequest.getSort());
        question.setIsMarkdown(questionRequest.getIsMarkdown() == null ? false:questionRequest.getIsMarkdown());
        question.setMarkdownContent(htmlFilterResult.formatterMarkdown);



        question.setLastUpdateTime(LocalDateTime.now());//最后修改时间
        int i = questionRepository.updateQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList),
                changePointSymbol,changePoint,changeAmountSymbol, changeAmount,pointLogObject,paymentLogObject,new_voteTheme,update_voteTheme,deleteVoteThemeId,add_voteOptionList,edit_voteOptionList,delete_voteOptionIdList);
        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));

        if(i >0 && !old_status.equals(questionRequest.getStatus())){
            if(user != null){
                //修改用户动态问题状态
                userRepository.updateUserDynamicQuestionStatus(user.getId(),question.getUserName(),question.getId(),question.getStatus());
            }

        }



        //删除缓存
        questionCacheManager.delete_cache_findById(question.getId());//删除问题缓存
        questionCacheManager.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
        if(user != null){
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());
            //异步执行会员卡赠送任务(长期任务类型)
            membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

        }


        ReadPathResult readPathResult = textFilterComponent.readPathName(old_content,"question");
        //旧图片
        List<String> old_imageNameList = readPathResult.getImageNameList();

        if(old_imageNameList != null && old_imageNameList.size() >0){

            Iterator<String> iter = old_imageNameList.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();

                for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                    if(imageName.equals("file/question/"+new_imageName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_imageNameList.size() >0){
                for(String imageName : old_imageNameList){

                    oldPathFileList.add(FileUtil.toSystemPath(imageName));

                }

            }
        }

        //旧影音
        List<String> old_mediaNameList = readPathResult.getMediaNameList();
        if(old_mediaNameList != null && old_mediaNameList.size() >0){
            Iterator<String> iter = old_mediaNameList.iterator();
            while (iter.hasNext()) {
                String mediaName = iter.next();
                for(String new_mediaName : htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                    if(mediaName.equals("file/question/"+new_mediaName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_mediaNameList.size() >0){
                for(String mediaName : old_mediaNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(mediaName));

                }

            }
        }

        //旧文件
        List<String> old_fileNameList = readPathResult.getFileNameList();
        if(old_fileNameList != null && old_fileNameList.size() >0){
            Iterator<String> iter = old_fileNameList.iterator();
            while (iter.hasNext()) {
                String fileName = iter.next();
                for(String new_fileName : htmlFilterResult.htmlProcessingResult.getFileNameList()){
                    if(fileName.equals("file/question/"+new_fileName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_fileNameList.size() >0){
                for(String fileName : old_fileNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(fileName));

                }

            }
        }



        //上传文件编号
        String fileNumber = questionComponent.generateFileNumber(question.getUserName(), question.getIsStaff());

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //删除音视频锁
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult.getMediaNameList().isEmpty()){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //删除文件锁
        if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && !htmlFilterResult.htmlProcessingResult.getFileNameList().isEmpty()){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }

        //删除旧路径文件
        if(oldPathFileList.size() >0){
            for(String oldPathFile :oldPathFileList){
                //如果验证不是当前用户上传的文件，则不删除
                if(!questionComponent.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
                    continue;
                }


                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(oldPathFile);
                if(state != null && !state){

                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"question"+File.separator, "");

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
                }
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

        String number = questionComponent.generateFileNumber(userName, isStaff);

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
        String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;
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
        String presigne = fileComponent.createPresigned("file/question/"+date+"/"+dir+"/"+newFileName,null);//创建预签名


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
        String pathDir = "file"+File.separator+"question"+File.separator + date +File.separator +dir+ File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"question"+File.separator+"lock"+File.separator;

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
        result.put("url", fileServerAddress+"file/question/"+date+"/"+dir+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }

    /**
     * 获取问题追加界面信息
     * @param questionId 问题Id
     * @return
     */
    public Map<String, Object> getAddAdditionalQuestionViewModel(Long questionId){
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
        Question question = questionRepository.findById(questionId);
        if(question == null){
            throw new BusinessException(Map.of("questionId", "问题不存在"));
        }
        returnValue.put("question", question);
        return returnValue;
    }




    /**
     * 获取修改追加问题界面信息
     * @param questionId 问题Id
     * @param appendQuestionItemId 追加问题Id
     * @return
     */
    public Map<String, Object> getEditAdditionalQuestionViewModel(Long questionId,String appendQuestionItemId){
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
        Question question = questionRepository.findById(questionId);
        if(question == null){
            throw new BusinessException(Map.of("questionId", "问题不存在"));
        }
        //删除最后一个逗号
        String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

        List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
        if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
            for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
                if(appendQuestionItem.getId().equals(appendQuestionItemId)){



                    if(appendQuestionItem.getContent() != null && !appendQuestionItem.getContent().trim().isEmpty()){
                        //处理富文本路径
                        appendQuestionItem.setContent(fileComponent.processRichTextFilePath(appendQuestionItem.getContent(),"question"));
                    }


                    returnValue.put("appendQuestionItem", appendQuestionItem);
                    break;
                }
            }
        }

        returnValue.put("question", question);
        return returnValue;
    }


    /**
     * 追加问题
     * @param appendQuestionRequest 追加问题表单
     * @param request 请求信息
     */
    public void appendQuestion(AppendQuestionRequest appendQuestionRequest, HttpServletRequest request){
        if(appendQuestionRequest.getQuestionId() == null || appendQuestionRequest.getQuestionId() <=0){
            throw new BusinessException(Map.of("content", "问题Id不能为空"));
        }
        Question question = questionRepository.findById(appendQuestionRequest.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("content", "问题不存在"));
        }

        // 处理内容过滤和文件路径
        QuestionServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, appendQuestionRequest.getContent(), appendQuestionRequest.getIsMarkdown(), appendQuestionRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

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

        if(value == null || value.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "追加内容后不能为空"));
        }

        AppendQuestionItem appendQuestionItem = new AppendQuestionItem();
        appendQuestionItem.setId(UUIDUtil.getUUID32());
        appendQuestionItem.setContent(value.trim());
        appendQuestionItem.setIsMarkdown(appendQuestionRequest.getIsMarkdown() == null ? false:appendQuestionRequest.getIsMarkdown());
        appendQuestionItem.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        appendQuestionItem.setPostTime(LocalDateTime.now());
        String appendContent_json = jsonComponent.toJSONString(appendQuestionItem);

        //追加问题
        questionRepository.saveAppendQuestion(appendQuestionRequest.getQuestionId(), appendContent_json+",");
        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));

        //删除缓存
        questionCacheManager.delete_cache_findById(appendQuestionRequest.getQuestionId());//删除问题缓存

        //上传文件编号
        String fileNumber = questionComponent.generateFileNumber(question.getUserName(), question.getIsStaff());



        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){

                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }

                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //音视频
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult.getMediaNameList().isEmpty()){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //文件
        if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && !htmlFilterResult.htmlProcessingResult.getFileNameList().isEmpty()){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }
    }


    /**
     * 修改追加问题
     * @param appendQuestionRequest 追加问题表单
     * @param request 请求信息
     */
    public void editAppendQuestion(AppendQuestionRequest appendQuestionRequest, HttpServletRequest request){
        if(appendQuestionRequest.getQuestionId() == null || appendQuestionRequest.getQuestionId() <=0){
            throw new BusinessException(Map.of("content", "问题Id不能为空"));
        }
        Question question = questionRepository.findById(appendQuestionRequest.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("content", "问题不存在"));
        }
        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件
        String old_content = "";
        //删除最后一个逗号
        String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

        List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});

        boolean flag = false;
        if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
            for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
                if(appendQuestionItem.getId().equals(appendQuestionRequest.getAppendQuestionItemId())){
                    old_content = appendQuestionItem.getContent();
                    flag = true;
                    break;
                }
            }
        }
        if(!flag){
            throw new BusinessException(Map.of("question", "追加问题不存在"));
        }

        // 处理内容过滤和文件路径
        QuestionServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, appendQuestionRequest.getContent(), appendQuestionRequest.getIsMarkdown(), appendQuestionRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

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

        if(value == null || value.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "追加内容后不能为空"));
        }

        if(appendQuestionItemList.size() >0){
            for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
                if(appendQuestionItem.getId().equals(appendQuestionRequest.getAppendQuestionItemId())){
                    appendQuestionItem.setContent(value);
                    appendQuestionItem.setIsMarkdown(appendQuestionRequest.getIsMarkdown() == null ? false:appendQuestionRequest.getIsMarkdown());
                    appendQuestionItem.setMarkdownContent(htmlFilterResult.formatterMarkdown);
                }
            }
        }


        String appendContent_json = jsonComponent.toJSONString(appendQuestionItemList);
        //删除最后一个中括号
        appendContent_json = StringUtils.substringBeforeLast(appendContent_json, "]");//从右往左截取到相等的字符,保留左边的


        int i = questionRepository.updateAppendQuestion(appendQuestionRequest.getQuestionId(),appendContent_json+",");
        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));



        //删除缓存
        questionCacheManager.delete_cache_findById(question.getId());//删除问题缓存

        ReadPathResult readPathResult = textFilterComponent.readPathName(old_content,"question");
        //旧图片
        List<String> old_imageNameList = readPathResult.getImageNameList();

        if(old_imageNameList != null && old_imageNameList.size() >0){

            Iterator<String> iter = old_imageNameList.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();

                for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                    if(imageName.equals("file/question/"+new_imageName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_imageNameList.size() >0){
                for(String imageName : old_imageNameList){

                    oldPathFileList.add(FileUtil.toSystemPath(imageName));

                }

            }
        }

        //旧影音
        List<String> old_mediaNameList = readPathResult.getMediaNameList();
        if(old_mediaNameList != null && old_mediaNameList.size() >0){
            Iterator<String> iter = old_mediaNameList.iterator();
            while (iter.hasNext()) {
                String mediaName = iter.next();
                for(String new_mediaName : htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                    if(mediaName.equals("file/question/"+new_mediaName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_mediaNameList.size() >0){
                for(String mediaName : old_mediaNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(mediaName));

                }

            }
        }

        //旧文件
        List<String> old_fileNameList = readPathResult.getFileNameList();
        if(old_fileNameList != null && old_fileNameList.size() >0){
            Iterator<String> iter = old_fileNameList.iterator();
            while (iter.hasNext()) {
                String fileName = iter.next();
                for(String new_fileName : htmlFilterResult.htmlProcessingResult.getFileNameList()){
                    if(fileName.equals("file/question/"+new_fileName)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_fileNameList.size() >0){
                for(String fileName : old_fileNameList){
                    oldPathFileList.add(FileUtil.toSystemPath(fileName));

                }

            }
        }



        //上传文件编号
        String fileNumber = questionComponent.generateFileNumber(question.getUserName(), question.getIsStaff());

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                }
            }
        }
        //删除音视频锁
        if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult.getMediaNameList().isEmpty()){
            for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                if(mediaName != null && !mediaName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                }
            }
        }
        //删除文件锁
        if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && !htmlFilterResult.htmlProcessingResult.getFileNameList().isEmpty()){
            for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                if(fileName != null && !fileName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"question"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                }
            }
        }

        //删除旧路径文件
        if(oldPathFileList.size() >0){
            for(String oldPathFile :oldPathFileList){
                //如果验证不是当前用户上传的文件，则不删除
                if(!questionComponent.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
                    continue;
                }


                //替换路径中的..号
                oldPathFile = FileUtil.toRelativePath(oldPathFile);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(oldPathFile);
                if(state != null && !state){

                    //替换指定的字符，只替换第一次出现的
                    oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"question"+File.separator, "");

                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
                }
            }
        }
    }


    /**
     * 删除问题
     * @param questionId 问题Id集合
     */
    public void deleteQuestion(Long[] questionId){
        if(questionId == null || questionId.length ==0){
            throw new BusinessException(Map.of("questionId", "请选择问题"));
        }

        List<Long> questionIdList = Arrays.stream(questionId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (questionIdList.isEmpty()) {
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }

        List<Question> questionList = questionRepository.findByIdList(questionIdList);
        if(questionList == null || questionList.isEmpty()) {
            throw new BusinessException(Map.of("questionId", "问题不存在"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof SysUsers){
            userId =((SysUsers)principal).getUserId();
            username =((SysUsers)principal).getUserAccount();
        }

        for(Question question : questionList){
            User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            //悬赏金额
            BigDecimal rewardAmount = new BigDecimal("0.00");
            //悬赏积分
            Long rewardPoint = 0L;
            //用户悬赏积分日志
            Object pointLogObject = null;
            //用户悬赏金额日志
            Object paymentLogObject = null;

            if(question.getStatus() < 100){//标记删除
                int i = questionRepository.markDelete(question.getId());


                if(i >0 && user != null){
                    //修改问题状态
                    userRepository.softDeleteUserDynamicByQuestionId(user.getId(),question.getUserName(),question.getId());
                }
                //更新索引
                questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
                questionCacheManager.delete_cache_findById(question.getId());//删除缓存
                questionCacheManager.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
            }else{//物理删除
                if(question.getAdoptionAnswerId().equals(0L) && !question.getIsStaff()){//如果悬赏未采纳答案，则将赏金退还给提问用户

                    LocalDateTime time =  LocalDateTime.now();

                    if(user != null && question.getPoint() != null && question.getPoint() >0L){
                        rewardPoint = question.getPoint();
                        PointLog reward_pointLog = new PointLog();
                        reward_pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
                        reward_pointLog.setModule(1000);//1000.悬赏积分
                        reward_pointLog.setParameterId(question.getId());//参数Id
                        reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                        reward_pointLog.setOperationUserName(username);//操作用户名称
                        reward_pointLog.setPointState(1);//2:账户支出
                        reward_pointLog.setPoint(question.getPoint());//积分
                        reward_pointLog.setUserName(user.getUserName());//用户名称
                        reward_pointLog.setRemark("");
                        reward_pointLog.setTimes(time);
                        pointLogObject = pointComponent.createPointLogObject(reward_pointLog);
                    }



                    if(user != null && question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
                        rewardAmount = question.getAmount();
                        PaymentLog reward_paymentLog = new PaymentLog();
                        reward_paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(user.getId()));//支付流水号
                        reward_paymentLog.setPaymentModule(90);//支付模块 90.悬赏金额
                        reward_paymentLog.setSourceParameterId(String.valueOf(question.getId()));//参数Id
                        reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                        reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
                        reward_paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                        reward_paymentLog.setAmount(question.getAmount());//金额
                        reward_paymentLog.setInterfaceProduct(0);//接口产品
                        reward_paymentLog.setUserName(user.getUserName());//用户名称
                        reward_paymentLog.setTimes(time);
                        paymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);
                    }


                }

                String fileNumber = questionComponent.generateFileNumber(question.getUserName(), question.getIsStaff());
                try {
                    int i = questionRepository.deleteQuestion(question.getId(),question.getUserName(),rewardPoint,pointLogObject,rewardAmount,paymentLogObject);

                    if(i>0){
                        //根据问题Id删除用户动态(问题下的评论和回复也同时删除)
                        userRepository.deleteUserDynamicByQuestionId(question.getId());
                    }

                    questionCacheManager.delete_cache_findById(question.getId());//删除缓存
                    questionCacheManager.delete_cache_findQuestionTagAssociationByQuestionId(question.getId());//删除'根据问题Id查询问题标签关联'缓存
                    if(user != null){
                        userCacheManager.delete_cache_findUserById(user.getId());
                        userCacheManager.delete_cache_findUserByUserName(user.getUserName());
                        //异步执行会员卡赠送任务(长期任务类型)
                        membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

                    }

                    //更新索引
                    questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),3));


                    String questionContent = question.getContent();

                    //删除最后一个逗号
                    String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

                    List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
                    if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
                        for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
                            questionContent += appendQuestionItem.getContent();
                        }
                    }


                    ReadPathResult readPathResult = textFilterComponent.readPathName(questionContent,"question");

                    List<String> filePathList = new ArrayList<String>();

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
                        if(!questionComponent.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
                            continue;
                        }

                        //替换路径中的..号
                        filePath = FileUtil.toRelativePath(filePath);

                        //删除旧路径文件
                        Boolean state = fileComponent.deleteFile(filePath);
                        if(state != null && !state){
                            //替换指定的字符，只替换第一次出现的
                            filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"question"+File.separator, "");
                            //创建删除失败文件
                            fileComponent.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
                        }
                    }

                    //清空目录
                    Boolean state = fileComponent.removeDirectory("file"+File.separator+"answer"+File.separator+question.getId()+File.separator);
                    if(state != null && !state){
                        //创建删除失败目录文件
                        fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+question.getId());
                    }
                } catch (Exception e) {
                    throw new BusinessException(Map.of("answer", e.getMessage()));
                }

            }
        }
    }

    /**
     * 删除追加问题
     * @param questionId 问题Id集合
     */
    public void deleteAdditionalQuestion(Long questionId,String appendQuestionItemId) {
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        if(appendQuestionItemId == null || appendQuestionItemId.trim().isEmpty()){
            throw new BusinessException(Map.of("appendQuestionItemId", "追加问题Id不能为空"));
        }
        Question question = questionRepository.findById(questionId);
        if(question == null){
            throw new BusinessException(Map.of("questionId", "问题不存在"));
        }
        boolean flag = false;
        String old_content = "";
        //删除最后一个逗号
        String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

        List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});

        if(appendQuestionItemList != null && appendQuestionItemList.size() >0){

            Iterator<AppendQuestionItem> iter = appendQuestionItemList.iterator();
            while (iter.hasNext()) {
                AppendQuestionItem appendQuestionItem = iter.next();
                if(appendQuestionItem.getId().equals(appendQuestionItemId)){
                    old_content = appendQuestionItem.getContent();
                    flag = true;
                    iter.remove();
                    break;
                }
            }
        }




        String appendContent_json = jsonComponent.toJSONString(appendQuestionItemList);
        //删除最后一个中括号
        appendContent_json = StringUtils.substringBeforeLast(appendContent_json, "]");//从右往左截取到相等的字符,保留左边的

        if(appendQuestionItemList.size() >0){
            appendContent_json += ",";
        }
        if(flag){
            String fileNumber = questionComponent.generateFileNumber(question.getUserName(), question.getIsStaff());

            int i = questionRepository.updateAppendQuestion(questionId, appendContent_json);


            questionCacheManager.delete_cache_findById(question.getId());//删除缓存
            //更新索引
            questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
            ReadPathResult readPathResult = textFilterComponent.readPathName(old_content,"question");

            List<String> filePathList = new ArrayList<String>();

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
                if(!questionComponent.getFileNumber(FileUtil.getBaseName(filePath.trim())).equals(fileNumber)){
                    continue;
                }

                //替换路径中的..号
                filePath = FileUtil.toRelativePath(filePath);

                //删除旧路径文件
                Boolean state = fileComponent.deleteFile(filePath);
                if(state != null && !state){
                    //替换指定的字符，只替换第一次出现的
                    filePath = Strings.CS.replaceOnce(filePath, "file"+File.separator+"question"+File.separator, "");


                    //创建删除失败文件
                    fileComponent.failedStateFile("file"+File.separator+"question"+File.separator+"lock"+File.separator+FileUtil.toUnderline(filePath));
                }
            }

            //清空目录
            Boolean state_ = fileComponent.removeDirectory("file"+File.separator+"answer"+File.separator+question.getId()+File.separator);
            if(state_ != null && !state_){
                //创建删除失败目录文件
                fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+"#"+question.getId());
            }


        }
    }


    /**
     * 还原问题
     * @param questionId 问题Id集合
     */
    public void reductionQuestion(Long[] questionId){
        if(questionId == null || questionId.length ==0){
            throw new BusinessException(Map.of("questionId", "请选择问题"));
        }
        List<Question> questionList = questionRepository.findByIdList(Arrays.asList(questionId));
        if(questionList == null || questionList.size() ==0) {
            throw new BusinessException(Map.of("question", "问题不能为空"));
        }
        int i = questionRepository.reductionQuestion(questionList);

        for(Question question :questionList){

            User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            if(i >0 && user != null){
                //修改问题状态
                userRepository.reductionUserDynamicByQuestionId(user.getId(),question.getUserName(),question.getId());
            }

            //更新索引
            questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));
            questionCacheManager.delete_cache_findById(question.getId());//删除缓存
        }
    }

    /**
     * 审核问题
     * @param questionId 问题Id
     */
    public void auditQuestion(Long questionId){
        if(questionId == null || questionId <=0){
            throw new BusinessException(Map.of("questionId", "请选择问题"));
        }
        int i = questionRepository.updateQuestionStatus(questionId, 20);

        Question question = questionCacheManager.query_cache_findById(questionId);
        if(i >0 && question != null){
            User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            if(user != null){
                //修改问题状态
                userRepository.updateUserDynamicQuestionStatus(user.getId(),question.getUserName(),question.getId(),20);
            }
        }

        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(questionId),2));
        questionCacheManager.delete_cache_findById(questionId);//删除缓存
    }

    /**
     * 获取问题搜索
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
    public PageView<Question> getSearch(int page, Integer dataSource,String keyword,String tagId,String tagName,String account,
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
        PageView<Question> pageView = new PageView<Question>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();

        if(dataSource.equals(1)){//lucene索引

            QueryResult<Question> qr = questionLuceneComponent.findIndexByCondition(pageView.getCurrentpage(), pageView.getMaxresult(), _keyword, _tagId, _userName, _start_postTime, _end_postTime, null, 1);

            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                List<Long> questionIdList =  new ArrayList<Long>();//话题Id集合

                List<Question> new_questionList = new ArrayList<Question>();
                for(Question question : qr.getResultlist()){
                    questionIdList.add(question.getId());
                }
                if(questionIdList.size() >0){
                    List<Question> questionList = questionRepository.findByIdList(questionIdList);
                    if(questionList != null && questionList.size() >0){
                        for(Question old_t : qr.getResultlist()){
                            for(Question pi : questionList){
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

                                    new_questionList.add(pi);
                                    break;
                                }
                            }
                        }
                    }
                }

                QueryResult<Question> _qr = new QueryResult<Question>();
                //把查询结果设进去
                _qr.setResultlist(new_questionList);
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
                paramValue.add("%"+ cms.utils.StringUtil.escapeSQLLike(_keyword)+"%");
            }
            if(_tagId != null && _tagId >0){//标签
                param += " and exists(select q.questionId from QuestionTagAssociation q where q.questionTagId = ?"+(paramValue.size()+1)+" and q.questionId=o.id) ";
                paramValue.add(_tagId);
            }
            if(_userName != null && !_userName.trim().isEmpty()){//用户
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
            QueryResult<Question> qr = questionRepository.getScrollData(Question.class,firstindex, pageView.getMaxresult(), param, paramValue.toArray(),orderby);

            if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(Question t :qr.getResultlist()){
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
            List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();

            if(questionTagList != null && questionTagList.size() >0){
                for(Question question : pageView.getRecords()){
                    List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
                    if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                        for(QuestionTag questionTag : questionTagList){
                            for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
                                if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
                                    questionTagAssociation.setQuestionTagName(questionTag.getName());
                                    question.addQuestionTagAssociation(questionTagAssociation);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }
        return pageView;
    }

    /**
     * 获取全部待审核问题
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Question> getPendingQuestions(int page, String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.status=?"+ (params.size()+1));
        params.add(10);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Question> pageView = new PageView<Question>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Question> qr = questionRepository.getScrollData(Question.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();

            if(questionTagList != null && questionTagList.size() >0){
                for(Question question : qr.getResultlist()){
                    List<QuestionTagAssociation> questionTagAssociationList = questionRepository.findQuestionTagAssociationByQuestionId(question.getId());
                    if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                        for(QuestionTag questionTag : questionTagList){
                            for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
                                if(questionTagAssociation.getQuestionTagId().equals(questionTag.getId())){
                                    questionTagAssociation.setQuestionTagName(questionTag.getName());
                                    question.addQuestionTagAssociation(questionTagAssociation);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            for(Question question : qr.getResultlist()){

                if(question.getIsStaff()){//如果为员工
                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(question.getUserName());
                    if(sysUsers != null){
                        question.setNickname(sysUsers.getNickname());
                        if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                            question.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                            question.setAvatarName(sysUsers.getAvatarName());
                        }
                    }
                    question.setAccount(question.getUserName());//员工用户名和账号是同一个
                }else{
                    User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
                    if(user != null){
                        question.setAccount(user.getAccount());
                        question.setNickname(user.getNickname());
                        if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                            question.setAvatarPath(fileServerAddress+user.getAvatarPath());
                            question.setAvatarName(user.getAvatarName());
                        }
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取全部待审核答案
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Answer> getAllPendingAnswers(int page, String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.status=?"+ (params.size()+1));
        params.add(10);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Answer> pageView = new PageView<Answer>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Answer> qr = answerRepository.getScrollData(Answer.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> questionIdList = new ArrayList<Long>();
            for(Answer o :qr.getResultlist()){
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!questionIdList.contains(o.getQuestionId())){
                    questionIdList.add(o.getQuestionId());
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
            List<Question> questionList = questionRepository.findTitleByIdList(questionIdList);
            if(questionList != null && questionList.size() >0){
                for(Answer o :qr.getResultlist()){
                    for(Question question : questionList){
                        if(question.getId().equals(o.getQuestionId())){
                            o.setQuestionTitle(question.getTitle());
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
    public PageView<AnswerReply> getAllPendingReplies(int page, String fileServerAddress){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.status=?"+ (params.size()+1));
        params.add(10);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<AnswerReply> pageView = new PageView<AnswerReply>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<AnswerReply> qr = answerRepository.getScrollData(AnswerReply.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> questionIdList = new ArrayList<Long>();
            for(AnswerReply o :qr.getResultlist()){

                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!questionIdList.contains(o.getQuestionId())){
                    questionIdList.add(o.getQuestionId());
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
            List<Question> questionList = questionRepository.findTitleByIdList(questionIdList);
            if(questionList != null && questionList.size() >0){
                for(AnswerReply o :qr.getResultlist()){
                    for(Question question : questionList){
                        if(question.getId().equals(o.getQuestionId())){
                            o.setQuestionTitle(question.getTitle());
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
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @return HtmlFilterResult Html过滤结果
     */
    private QuestionServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content);
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"question",null);
        return new QuestionServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);
    }

    /**
     * 校验表单字段
     * @param questionRequest 问题表单
     * @param errors 错误信息
     */
    private void validateInput(QuestionRequest questionRequest,Map<String, String> errors) {
        if(questionRequest.getTitle() != null && !questionRequest.getTitle().trim().isEmpty()){
            if(questionRequest.getTitle().length() >150){
                errors.put("title", "不能大于150个字符");
            }
        }else{
            errors.put("title", "标题不能为空");
        }

        if(questionRequest.getSort() != null){
            if(questionRequest.getSort() >99999999){
                errors.put("sort", "不能超过8位数字");
            }
        }else{
            errors.put("sort", "排序不能为空");
        }
    }
}