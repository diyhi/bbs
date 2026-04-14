package cms.service.frontend.impl;

import cms.component.*;
import cms.component.fileSystem.FileComponent;
import cms.component.filterWord.SensitiveWordFilterComponent;
import cms.component.follow.FollowCacheManager;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.lucene.QuestionLuceneComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.payment.PaymentComponent;
import cms.component.question.AnswerCacheManager;
import cms.component.question.QuestionCacheManager;
import cms.component.question.QuestionComponent;
import cms.component.setting.SettingCacheManager;
import cms.component.setting.SettingComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.topic.TopicComponent;
import cms.component.user.*;
import cms.config.BusinessException;
import cms.dto.HtmlProcessingResult;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.frontendModule.AppendQuestionDTO;
import cms.dto.frontendModule.QuestionDTO;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.frontendModule.*;
import cms.model.message.Remind;
import cms.model.payment.PaymentLog;
import cms.model.question.*;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.model.user.UserDynamic;
import cms.model.vote.VoteOption;
import cms.model.vote.VoteTheme;
import cms.repository.message.RemindRepository;
import cms.repository.question.QuestionIndexRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.QuestionClientService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 前台问题服务
 */
@Service
public class QuestionClientServiceImpl implements QuestionClientService {
    private static final Logger logger = LogManager.getLogger(QuestionClientServiceImpl.class);


    @Resource
    UserCacheManager userCacheManager;
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    QuestionTagRepository questionTagRepository;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource StaffCacheManager staffCacheManager;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource AnswerCacheManager answerCacheManager;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource
    QuestionComponent questionComponent;
    @Resource
    QuestionLuceneComponent questionLuceneComponent;
    @Resource CaptchaCacheManager captchaCacheManager;
    @Resource
    UserRepository userRepository;
    @Resource
    SensitiveWordFilterComponent sensitiveWordFilterComponent;
    @Resource
    SettingComponent settingComponent;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    RemindRepository remindRepository;
    @Resource
    QuestionIndexRepository questionIndexRepository;
    @Resource FollowCacheManager followCacheManager;
    @Resource
    UserDynamicComponent userDynamicComponent;
    @Resource
    UserDynamicConfig userDynamicConfig;
    @Resource PointComponent pointComponent;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    TopicComponent topicComponent;

    @Resource RemindCacheManager remindCacheManager;
    @Resource SettingCacheManager settingCacheManager;


    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {}


    /**
     * 文件上传配置
     * @param success 是否校验通过
     * @param message 错误消息
     * @param formats 允许的后缀列表
     * @param maxSize 最大限制 (KB)
     */
    private record FileUploadConfig(
            boolean success,
            String message,
            List<String> formats,
            long maxSize
    ) {
        public static QuestionClientServiceImpl.FileUploadConfig ok(List<String> formats, long maxSize) {
            return new QuestionClientServiceImpl.FileUploadConfig(true, null, formats, maxSize);
        }
        public static QuestionClientServiceImpl.FileUploadConfig fail(String message) {
            return new QuestionClientServiceImpl.FileUploadConfig(false, message, null, 0);
        }
    }



    /**
     * 获取问题分页
     * @param page 页码
     * @param questionTagId 标签Id
     * @param filterCondition 过滤条件
     * @param request 请求信息
     * @return
     */
    public PageView<Question> getQuestionPage(int page, Long questionTagId, Integer filterCondition, HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigQuestionPage configQuestionPage) {
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if (configQuestionPage.getMaxResult() != null && configQuestionPage.getMaxResult() > 0) {
                maxResult = configQuestionPage.getMaxResult();
            }
            int pageCount = 10;// 页码显示总数
            int sort = 1;//排序

            //标签Id
            if(!configQuestionPage.isTag_transferPrameter()){
                questionTagId = configQuestionPage.getTagId();
            }
            //排序
            if(configQuestionPage.getSort() != null && configQuestionPage.getSort()  >0){
                sort = configQuestionPage.getSort() ;
            }
            //条件过滤
            if(!configQuestionPage.isFilterCondition_transferPrameter()){
                filterCondition = configQuestionPage.getFilterCondition();
            }

            //页码显示总数
            if(configQuestionPage.getPageCount() != null && configQuestionPage.getPageCount() >0){
                pageCount = configQuestionPage.getPageCount();
            }

            PageForm pageForm = new PageForm();
            pageForm.setPage(page);

            //调用分页算法代码
            PageView<Question> pageView = new PageView<Question>(maxResult,pageForm.getPage(),pageCount);
            //当前页
            int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

            //执行查询
            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();
            //标签Id
            if(questionTagId != null && questionTagId >0L){

                jpql.append(" and exists(select q.questionId from QuestionTagAssociation q where q.questionTagId = ?"+ (params.size()+1)+" and q.questionId=o.id) ");
                params.add(questionTagId);//加上查询参数
            }

            if(filterCondition != null){
                if(filterCondition.equals(20)){//未解决：20
                    jpql.append(" and o.adoptionAnswerId=?"+ (params.size()+1));
                    params.add(0L);
                }else if(filterCondition.equals(30)){//已解决：30
                    jpql.append(" and o.adoptionAnswerId>?"+ (params.size()+1));
                    params.add(0L);
                }else if(filterCondition.equals(40)){//积分悬赏：40
                    jpql.append(" and o.point>?"+ (params.size()+1));
                    params.add(0L);
                }else if(filterCondition.equals(50)){//现金悬赏：50
                    jpql.append(" and o.amount>?"+ (params.size()+1));
                    params.add(new BigDecimal("0"));
                }
            }

            jpql.append(" and o.status=?"+ (params.size()+1));
            params.add(20);


            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

            orderby.put("sort", "desc");//优先级   大-->小
            //排行依据
            if(sort == 1){
                orderby.put("id", "desc");//发布时间排序   新-->旧
            }else if(sort == 2){
                orderby.put("id", "asc");//发布时间排序  旧-->新
            }else if(sort == 3){
                orderby.put("lastAnswerTime", "desc");//最后回答时间排序   新-->旧
            }else if(sort == 4){
                orderby.put("lastAnswerTime", "asc");//最后回答时间排序  旧-->新
            }



            //删除第一个and
            String jpql_str = StringUtils.difference(" and", jpql.toString());
            QueryResult<Question> qr = questionRepository.getScrollData(Question.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);


            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(Question question : qr.getResultlist()){
                    question.setIp(null);//IP地址不显示
                    question.setContent(null);//问题内容不显示
                    question.setMarkdownContent(null);//问题Markdown内容不显示
                    if(question.getPostTime().equals(question.getLastAnswerTime())){//如果发贴时间等于回复时间，则不显示回复时间
                        question.setLastAnswerTime(null);
                    }
                    question.setViewTotal(question.getViewTotal()+questionComponent.readLocalView(question.getId()));

                    if(!question.getIsStaff()){//会员
                        User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
                        if(user != null){
                            question.setAccount(user.getAccount());
                            question.setNickname(user.getNickname());
                            question.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                            question.setAvatarName(user.getAvatarName());

                            List<String> userRoleNameList = userRoleComponent.queryUserRoleName(user.getUserName(),request);
                            if(userRoleNameList != null && userRoleNameList.size() >0){
                                question.setUserRoleNameList(userRoleNameList);//用户角色名称集合
                            }

                            if(user.getCancelAccountTime() != -1L){//账号已注销
                                question.setUserInfoStatus(-30);
                            }
                        }

                    }else{
                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(question.getUserName());
                        if(sysUsers != null){
                            question.setNickname(sysUsers.getNickname());
                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                question.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                question.setAvatarName(sysUsers.getAvatarName());
                            }
                        }
                        question.setAccount(question.getUserName());//员工用户名和账号是同一个
                    }
                }


                List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag_cache();
                List<String> languageFormExtensionCodeList = null;
                SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
                if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
                    languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
                }



                if(questionTagList != null && questionTagList.size() >0){
                    for(Question question : qr.getResultlist()){
                        List<QuestionTagAssociation> questionTagAssociationList = questionCacheManager.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
                        if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                            for(QuestionTag questionTag : questionTagList){
                                if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
                                    if(questionTag.getMultiLanguageExtension() != null && !questionTag.getMultiLanguageExtension().trim().isEmpty()){
                                        Map<String,String> multiLanguageExtensionMap = jsonComponent.toObject(questionTag.getMultiLanguageExtension(), HashMap.class);
                                        if(multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() >0){
                                            questionTag.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
                                        }
                                    }
                                }
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
                //非正常状态用户清除显示数据
                for(Question question : qr.getResultlist()){
                    if(question.getUserInfoStatus() <0){
                        question.setUserName(null);
                        question.setAccount(null);
                        question.setNickname(null);
                        question.setAvatarPath(null);
                        question.setAvatarName(null);
                        question.setUserRoleNameList(new ArrayList<String>());
                        question.setTitle("");
                        question.setSummary("");
                        question.setContent("");
                        question.setMarkdownContent("");
                        question.getAppendQuestionItemList().clear();
                    }
                }
            }


            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
            return pageView;
        }
        return null;
    }
    /**
     * 获取相似问题
     * @param questionId 问题Id
     * @return
     */
    public List<Question> getSimilarQuestion(Long questionId,HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigSimilarQuestion configSimilarQuestion) {
            int maxResult = 10;
            //每页显示记录数
            if(configSimilarQuestion.getMaxResult() != null && configSimilarQuestion.getMaxResult() >0){
                maxResult = configSimilarQuestion.getMaxResult();
            }

            if(questionId != null && questionId > 0L){
                return questionLuceneComponent.findLikeQuestion(maxResult,questionId,20);
            }
        }
        return null;
    }
    /**
     * 获取问题明细
     * @param questionId 问题Id
     * @param request 请求信息
     */
    public Question getQuestionDetail(Long questionId, HttpServletRequest request){
        if(questionId == null || questionId <= 0L){
            return null;
        }
        Question question = questionCacheManager.query_cache_findById(questionId);//查询缓存
        if(question == null){
            return null;
        }

        String ip = IpAddress.getClientIpAddress(request);

        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        if(ip != null){
            questionComponent.addView(questionId, ip);
        }
        question.setViewTotal(question.getViewTotal()+questionComponent.readLocalView(question.getId()));

        if(question.getStatus() >20){//20:已发布
            return null;

        }else{
            if(question.getStatus().equals(10) ){
                if(accessUser == null){
                    return null;
                }else{
                    if(!accessUser.getUserName().equals(question.getUserName())){
                        return null;
                    }
                }
            }
        }
        //只有中国地区简体中文语言才显示IP归属地
        if(accessUser != null && systemSetting.isShowIpAddress() && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
            question.setIpAddress(IpAddress.queryProvinceAddress(question.getIp()));
        }
        question.setIp(null);//IP地址不显示
        question.setMarkdownContent(null);//问题Markdown内容不显示

        if(question.getContent() != null && !question.getContent().trim().isEmpty()){
            //处理富文本路径
            question.setContent(fileComponent.processRichTextFilePath(question.getContent(),"question"));
        }

        List<String> languageFormExtensionCodeList = null;
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
        }

        List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag_cache();

        if(questionTagList != null && questionTagList.size() >0){
            List<QuestionTagAssociation> questionTagAssociationList = questionCacheManager.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
            if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                for(QuestionTag questionTag : questionTagList){
                    if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
                        if(questionTag.getMultiLanguageExtension() != null && !questionTag.getMultiLanguageExtension().trim().isEmpty()){
                            Map<String,String> multiLanguageExtensionMap = jsonComponent.toObject(questionTag.getMultiLanguageExtension(), HashMap.class);
                            if(multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() >0){
                                questionTag.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
                            }
                        }
                    }
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

        //删除最后一个逗号
        String _appendContent = StringUtils.substringBeforeLast(question.getAppendContent(), ",");//从右往左截取到相等的字符,保留左边的

        List<AppendQuestionItem> appendQuestionItemList = jsonComponent.toGenericObject(_appendContent+"]", new TypeReference< List<AppendQuestionItem> >(){});
        if(appendQuestionItemList != null && appendQuestionItemList.size() >0){
            for(AppendQuestionItem appendQuestionItem : appendQuestionItemList){
                if(appendQuestionItem.getContent() != null && !appendQuestionItem.getContent().trim().isEmpty()){
                    //处理富文本路径
                    appendQuestionItem.setContent(fileComponent.processRichTextFilePath(appendQuestionItem.getContent(),"question"));
                }
            }
        }



        question.setAppendQuestionItemList(appendQuestionItemList);


        if(!question.getIsStaff()){//会员
            User user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            if(user != null){
                question.setAccount(user.getAccount());
                question.setNickname(user.getNickname());
                question.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                question.setAvatarName(user.getAvatarName());

                List<String> userRoleNameList = userRoleComponent.queryUserRoleName(user.getUserName(),request);
                if(userRoleNameList != null && userRoleNameList.size() >0){
                    question.setUserRoleNameList(userRoleNameList);//用户角色名称集合
                }

                if(user.getCancelAccountTime() != -1L){//账号已注销
                    question.setUserInfoStatus(-30);
                }
            }

        }else{
            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(question.getUserName());
            if(sysUsers != null){
                question.setNickname(sysUsers.getNickname());
                if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                    question.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                    question.setAvatarName(sysUsers.getAvatarName());
                }
            }
            question.setAccount(question.getUserName());//员工用户名和账号是同一个
        }

        //非正常状态用户清除显示数据
        if(question.getUserInfoStatus() <0){
            question.setUserName(null);
            question.setAccount(null);
            question.setNickname(null);
            question.setAvatarPath(null);
            question.setAvatarName(null);
            question.setUserRoleNameList(new ArrayList<String>());
            question.setTitle("");
            question.setContent("");
            question.setMarkdownContent("");
            question.getAppendQuestionItemList().clear();
        }

        return question;
    }

    /**
     * 获取添加问题界面信息
     * @return
     */
    public Map<String,Object> getAddQuestionViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.question_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());//是否有验证码
            }
            User user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(user != null){
                returnValue.put("maxDeposit",user.getDeposit());//用户共有预存款
                returnValue.put("maxPoint",user.getPoint());//用户共有积分
            }
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //如果全局不允许提交问题
        if(!systemSetting.isAllowQuestion()){
            returnValue.put("allowQuestion",false);//不允许提交问题
        }else{
            returnValue.put("allowQuestion",true);//允许提交问题
        }
        returnValue.put("questionRewardPointMin",systemSetting.getQuestionRewardPointMin());
        returnValue.put("questionRewardPointMax",systemSetting.getQuestionRewardPointMax());
        returnValue.put("questionRewardAmountMin",systemSetting.getQuestionRewardAmountMin());
        returnValue.put("questionRewardAmountMax",systemSetting.getQuestionRewardAmountMax());
        returnValue.put("maxVoteOptions", systemSetting.getQuestionMaxVoteOptions());//发起投票选项数量

        returnValue.put("maxQuestionTagQuantity", systemSetting.getMaxQuestionTagQuantity());//提交问题最多可选择标签数量

        returnValue.put("availableTag", questionComponent.availableTag());//问题编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 添加问题
     * @param questionDTO 问题表单
     * @param request 请求信息
     */
    public void addQuestion(QuestionDTO questionDTO, HttpServletRequest request){

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10002000,null);//如果无权限设置403状态码

        AccessUser accessUser = AccessUserThreadLocal.get();
        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("question", "只读模式不允许提交数据"));
        }

        //验证码
        boolean isCaptcha = captchaComponent.question_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(questionDTO.getCaptchaKey(), questionDTO.getCaptchaValue(), errors);
        }

        User user = userRepository.findUserByUserName(accessUser.getUserName());//查询用户数据
        if(user != null){

            if(questionDTO.getAmount() != null){
                if(questionDTO.getAmount().compareTo(BigDecimal.ZERO) <0){
                    errors.put("amount","不能小于0");
                }else if(questionDTO.getAmount().compareTo(new BigDecimal("999999999")) >0){
                    errors.put("amount", "不能大于999999999");
                }else if(questionDTO.getAmount().compareTo(user.getDeposit()) >0){
                    errors.put("amount", "不能大于账户预存款");
                }

                if(systemSetting.getQuestionRewardAmountMax() != null ){
                    if(systemSetting.getQuestionRewardAmountMax().compareTo(new BigDecimal("0")) ==0){
                        errors.put("amount","不允许悬赏金额");

                    }else if(systemSetting.getQuestionRewardAmountMax().compareTo(new BigDecimal("0")) >0){
                        if(questionDTO.getAmount().compareTo(systemSetting.getQuestionRewardAmountMin()) <0){
                            errors.put("amount","不能小于悬赏金额下限");
                        }
                        if(questionDTO.getAmount().compareTo(systemSetting.getQuestionRewardAmountMax()) >0){
                            errors.put("amount","不能大于悬赏金额上限");
                        }
                    }
                }else{
                    if(questionDTO.getAmount().compareTo(systemSetting.getQuestionRewardAmountMin()) <0){
                        errors.put("amount","不能小于悬赏金额下限");
                    }
                }
            }

            if(questionDTO.getPoint() != null){
                if(questionDTO.getPoint() <0L){
                    errors.put("point","不能小于0");
                }else if(questionDTO.getPoint() >99999999){
                    errors.put("point", "不能超过8位数字");
                }else if(questionDTO.getPoint() > user.getPoint()){
                    errors.put("point","不能大于账户积分");
                }
                if(systemSetting.getQuestionRewardPointMax() != null ){
                    if(systemSetting.getQuestionRewardPointMax().equals(0L)){
                        errors.put("point","不允许悬赏积分");

                    }else if(systemSetting.getQuestionRewardPointMax() >0L){
                        if(questionDTO.getPoint() < systemSetting.getQuestionRewardPointMin()){
                            errors.put("point","不能小于悬赏积分下限");
                        }
                        if(questionDTO.getPoint() > systemSetting.getQuestionRewardPointMax()){
                            errors.put("point","不能大于悬赏积分上限");
                        }
                    }
                }else{
                    if(questionDTO.getPoint() < systemSetting.getQuestionRewardPointMin()){
                        errors.put("point","不能小于悬赏积分下限");
                    }
                }
            }
        }

        //如果全局不允许提交问题
        if(!systemSetting.isAllowQuestion()){
            errors.put("question", "不允许提交问题");
        }

        //如果实名用户才允许提交问题
        if(systemSetting.isRealNameUserAllowQuestion()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("question", "实名用户才允许提交问题");
            }

        }


        Question question = new Question();
        LocalDateTime time = LocalDateTime.now();
        VoteTheme voteTheme = null;
        List<String> optionTextList = new ArrayList<String>();
        LocalDateTime voteEndDate = null;

        //前台发表话题审核状态
        if(systemSetting.getQuestion_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            question.setStatus(10);//10.待审核
        }else if(systemSetting.getQuestion_review().equals(30)){
            //是否有当前功能操作权限
            boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._10006000,null);
            if(flag_permission){
                question.setStatus(20);//20.已发布
            }else{
                question.setStatus(10);//10.待审核
            }
        }else{
            question.setStatus(20);//20.已发布
        }

        LinkedHashSet <QuestionTagAssociation> questionTagAssociationList = new LinkedHashSet<QuestionTagAssociation>();

        if(questionDTO.getTagId() != null && questionDTO.getTagId().length >0){
            if(questionDTO.getTagId().length > systemSetting.getMaxQuestionTagQuantity()){
                errors.put("tagId", "标签不能超过"+systemSetting.getMaxQuestionTagQuantity()+"个");
            }

            List<QuestionTag> questionTagList =  questionTagRepository.findAllQuestionTag_cache();
            for(Long id :questionDTO.getTagId()){
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
            question.setQuestionTagAssociationList(new ArrayList<QuestionTagAssociation>(questionTagAssociationList));
        }else{
            errors.put("tagId", "标签不能为空");
        }
        if(questionTagAssociationList.size() == 0){
            errors.put("tagId", "标签不能为空");
        }

        if(questionDTO.getTitle() != null && !questionDTO.getTitle().trim().isEmpty()){
            if(systemSetting.isAllowFilterWord()){
                String wordReplace = "";
                if(systemSetting.getFilterWordReplace() != null){
                    wordReplace = systemSetting.getFilterWordReplace();
                }
                questionDTO.setTitle(sensitiveWordFilterComponent.filterSensitiveWord(questionDTO.getTitle(), wordReplace));
            }

            question.setTitle(questionDTO.getTitle());
            if(questionDTO.getTitle().length() >150){
                errors.put("title", "不能大于150个字符");
            }
        }else{
            errors.put("title", "标题不能为空");
        }


        if (questionDTO.getIsMarkdown() != null && questionDTO.getIsMarkdown()) {
            if(systemSetting.getSupportEditor().equals(10)){
                errors.put("question", "不支持此编辑器");
            }
        } else {
            if(systemSetting.getSupportEditor().equals(20)){
                errors.put("question", "不支持此编辑器");
            }
        }


        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }


        EditorTag editorTag = settingComponent.readQuestionEditorTag();
        // 处理内容过滤和文件路径
        QuestionClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, questionDTO.getContent(), questionDTO.getIsMarkdown(), questionDTO.getMarkdownContent(),editorTag);

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "问题内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();
        //风控文本
        String riskControlText = value;
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
            throw new BusinessException(Map.of("content", "问题内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        //摘要
        if(!trimSpace.isEmpty()){
            if(systemSetting.isAllowFilterWord()){
                String wordReplace = "";
                if(systemSetting.getFilterWordReplace() != null){
                    wordReplace = systemSetting.getFilterWordReplace();
                }
                trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
            }
            if(trimSpace.length() >180){
                question.setSummary(trimSpace.substring(0, 180)+"..");
            }else{
                question.setSummary(trimSpace+"..");
            }
        }

        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            value = sensitiveWordFilterComponent.filterSensitiveWord(value, wordReplace);
        }




        question.setPostTime(time);
        question.setLastAnswerTime(time);
        question.setAmount(Optional.ofNullable(questionDTO.getAmount())
                .orElse(new BigDecimal("0.00")));// 如果 getAmount() 为空，则取默认值 new BigDecimal("0.00")
        question.setPoint(Optional.ofNullable(questionDTO.getPoint())
                .orElse(0L));// 如果 getPoint() 为空，则取 0L
        question.setIp(IpAddress.getClientIpAddress(request));
        question.setUserName(accessUser.getUserName());
        question.setIsStaff(false);
        question.setContent(value);
        question.setIsMarkdown(questionDTO.getIsMarkdown() == null ? false:questionDTO.getIsMarkdown());
        question.setMarkdownContent(htmlFilterResult.formatterMarkdown);


        //用户悬赏积分日志
        PointLog reward_pointLog = null;
        if(questionDTO.getPoint() != null && questionDTO.getPoint() >0L){
            reward_pointLog = new PointLog();
            reward_pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
            reward_pointLog.setModule(1000);//1000.悬赏积分
            //	reward_pointLog.setParameterId(question.getId());//参数Id
            reward_pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
            reward_pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
            reward_pointLog.setPointState(2);//2:账户支出
            reward_pointLog.setPoint(questionDTO.getPoint());//积分
            reward_pointLog.setUserName(accessUser.getUserName());//用户名称
            reward_pointLog.setRemark("");
            reward_pointLog.setTimes(time);
        }

        //用户悬赏金额日志
        PaymentLog reward_paymentLog = null;

        if(questionDTO.getAmount() != null && questionDTO.getAmount().compareTo(new BigDecimal("0")) >0){
            reward_paymentLog = new PaymentLog();
            reward_paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(accessUser.getUserId()));//支付流水号
            reward_paymentLog.setPaymentModule(90);//支付模块 90.悬赏金额
            reward_paymentLog.setSourceParameterId(String.valueOf(question.getId()));//参数Id
            reward_paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
            reward_paymentLog.setOperationUserName(accessUser.getUserName());//操作用户名称  0:系统  1: 员工  2:会员
            reward_paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
            reward_paymentLog.setAmount(questionDTO.getAmount());//金额
            reward_paymentLog.setInterfaceProduct(0);//接口产品
            reward_paymentLog.setUserName(accessUser.getUserName());//用户名称
            reward_paymentLog.setTimes(time);

        }

        try {
            //保存问题
            questionRepository.saveQuestion(question,new ArrayList<QuestionTagAssociation>(questionTagAssociationList),questionDTO.getPoint(),reward_pointLog,questionDTO.getAmount(),reward_paymentLog,voteTheme);
        } catch (Exception e) {
            throw new BusinessException(Map.of ("question", "提交问题错误")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),1));

        PointLog pointLog = new PointLog();
        pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
        pointLog.setModule(700);//700.问题
        pointLog.setParameterId(question.getId());//参数Id
        pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
        pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

        pointLog.setPoint(systemSetting.getQuestion_rewardPoint());//积分
        pointLog.setUserName(accessUser.getUserName());//用户名称
        pointLog.setRemark("");

        //增加用户积分
        userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getQuestion_rewardPoint(), pointComponent.createPointLogObject(pointLog));


        //用户动态
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
        userDynamic.setUserName(accessUser.getUserName());
        userDynamic.setModule(500);//模块 500.问题
        userDynamic.setQuestionId(question.getId());
        userDynamic.setPostTime(question.getPostTime());
        userDynamic.setStatus(question.getStatus());
        userDynamic.setFunctionIdGroup(","+question.getId()+",");
        Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
        userRepository.saveUserDynamic(new_userDynamic);



        if(htmlFilterResult.htmlProcessingResult.getMentionUserNameList() != null && !htmlFilterResult.htmlProcessingResult.getMentionUserNameList().isEmpty()){
            int count = 0;
            for(String mentionUserName : htmlFilterResult.htmlProcessingResult.getMentionUserNameList()){
                count++;
                if(systemSetting.getAllowMentionMaxNumber() != null && count > systemSetting.getAllowMentionMaxNumber()){
                    break;
                }
                User _user = userCacheManager.query_cache_findUserByUserName(mentionUserName);
                if(_user != null && !mentionUserName.equals(accessUser.getUserName())){//不发提醒给自己
                    //提醒楼主
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(230);// 230:别人在问题提到我
                    remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setQuestionId(question.getId());//问题Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());


                }
            }
        }

        //删除缓存
        userCacheManager.delete_cache_findUserById(accessUser.getUserId());
        userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());
        followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());
        //异步执行会员卡赠送任务(长期任务类型)
        membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(accessUser.getUserName());


        String fileNumber = "b"+ accessUser.getUserId();

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){

                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }

                    fileComponent.deleteLock("file"+ File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

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
        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("question", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("question", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("question", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }


    /**
     * 获取追加问题界面信息
     * @return
     */
    public Map<String,Object> getAppendQuestionViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.question_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
            }
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //如果全局不允许提交问题
        if(!systemSetting.isAllowQuestion()){
            returnValue.put("allowQuestion",false);//不允许提交问题
        }else{
            returnValue.put("allowQuestion",true);//允许提交问题
        }

        returnValue.put("availableTag", questionComponent.availableTag());//问题编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 追加问题
     * @param appendQuestionDTO 追加问题表单
     * @param request 请求信息
     */
    public void appendQuestion(AppendQuestionDTO appendQuestionDTO, HttpServletRequest request){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10003000,null);//如果无权限设置403状态码

        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("question", "只读模式不允许提交数据"));
        }


        //验证码
        boolean isCaptcha = captchaComponent.question_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(appendQuestionDTO.getCaptchaKey(), appendQuestionDTO.getCaptchaValue(), errors);
        }

        //如果全局不允许提交问题
        if(!systemSetting.isAllowQuestion()){
            errors.put("question", "不允许提交问题");
        }

        //如果实名用户才允许提交问题
        if(systemSetting.isRealNameUserAllowQuestion()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("question", "实名用户才允许提交问题");
            }
        }

        if(appendQuestionDTO.getQuestionId() == null || appendQuestionDTO.getQuestionId() <=0L){
            throw new BusinessException(Map.of("question", "问题Id不能为空"));
        }
        Question question = questionRepository.findById(appendQuestionDTO.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("question", "问题不存在"));
        }
        if(!question.getUserName().equals(accessUser.getUserName())){
            errors.put("question", "不是提交该问题的用户不允许追加提问");
        }

        if (appendQuestionDTO.getIsMarkdown() != null && appendQuestionDTO.getIsMarkdown()) {
            if(systemSetting.getSupportEditor().equals(10)){
                errors.put("question", "不支持此编辑器");
            }
        } else {
            if(systemSetting.getSupportEditor().equals(20)){
                errors.put("question", "不支持此编辑器");
            }
        }


        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        EditorTag editorTag = settingComponent.readQuestionEditorTag();
        // 处理内容过滤和文件路径
        QuestionClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, appendQuestionDTO.getContent(), appendQuestionDTO.getIsMarkdown(), appendQuestionDTO.getMarkdownContent(),editorTag);

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "问题内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();
        //风控文本
        String riskControlText = value;

        //不含标签内容
        String source_text = textFilterComponent.filterText(htmlFilterResult.content);
        //清除空格&nbsp;
        String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();

        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && !htmlFilterResult.htmlProcessingResult.isHasFile()
                && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                && !htmlFilterResult.htmlProcessingResult.isHasMap()
                && (source_text.trim().isEmpty() || source_trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "追加内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            value = sensitiveWordFilterComponent.filterSensitiveWord(value, wordReplace);
        }
        if(value.isEmpty()){
            throw new BusinessException(Map.of("content", "追加内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        AppendQuestionItem appendQuestionItem = new AppendQuestionItem();

        appendQuestionItem.setId(UUIDUtil.getUUID32());
        appendQuestionItem.setContent(value);
        appendQuestionItem.setIsMarkdown(appendQuestionDTO.getIsMarkdown() == null ? false:appendQuestionDTO.getIsMarkdown());
        appendQuestionItem.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        appendQuestionItem.setPostTime(LocalDateTime.now());
        String appendContent_json = jsonComponent.toJSONString(appendQuestionItem);


        //追加问题
        questionRepository.saveAppendQuestion(appendQuestionDTO.getQuestionId(), appendContent_json+",");
        //更新索引
        questionIndexRepository.addQuestionIndex(new QuestionIndex(String.valueOf(question.getId()),2));


        if(htmlFilterResult.htmlProcessingResult.getMentionUserNameList() != null && !htmlFilterResult.htmlProcessingResult.getMentionUserNameList().isEmpty()){
            int count = 0;
            for(String mentionUserName : htmlFilterResult.htmlProcessingResult.getMentionUserNameList()){
                count++;
                if(systemSetting.getAllowMentionMaxNumber() != null && count > systemSetting.getAllowMentionMaxNumber()){
                    break;
                }
                User _user = userCacheManager.query_cache_findUserByUserName(mentionUserName);
                if(_user != null && !mentionUserName.equals(accessUser.getUserName())){//不发提醒给自己
                    //提醒楼主
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(230);// 230:别人在问题提到我
                    remind.setSendTimeFormat(new Date().getTime());//发送时间格式化
                    remind.setQuestionId(question.getId());//问题Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());


                }
            }
        }

        //删除缓存
        questionCacheManager.delete_cache_findById(appendQuestionDTO.getQuestionId());//删除问题缓存

        String fileNumber = "b"+ accessUser.getUserId();

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                if(imageName != null && !imageName.trim().isEmpty()){

                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }

                    fileComponent.deleteLock("file"+ File.separator+"question"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

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
        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("question", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("question", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("question", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }

    /**
     * 文件上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param dir: 上传类型，分别为image、file、media
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> uploadFile(String dir, String fileName, MultipartFile file, String fileServerAddress){
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(systemSetting.getCloseSite().equals(2)){
            returnValue.put("error", 1);
            returnValue.put("message", "只读模式不允许提交数据");
            return returnValue;
        }

        //如果全局不允许提交问题
        if(!systemSetting.isAllowQuestion()){
            returnValue.put("error", 1);
            returnValue.put("message", "不允许提交问题");
            return returnValue;
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        //如果实名用户才允许提交问题
        if(systemSetting.isRealNameUserAllowQuestion()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                returnValue.put("error", 1);
                returnValue.put("message","实名用户才允许提交问题");
                return returnValue;
            }
        }
        //上传文件编号
        String number = "b"+accessUser.getUserId();

        if (dir == null || number.trim().isEmpty()) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }
        QuestionClientServiceImpl.FileUploadConfig config = validateUpload(dir, accessUser.getUserName());
        if (!config.success()) {
            returnValue.put("error", 1);
            returnValue.put("message", config.message());
            return returnValue;
        }

        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyy-MM-dd");
        int fileSystem = fileComponent.getFileSystem();

        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, date, number,config, returnValue);
        } else { // 本地系统
            // 本地系统增加大小校验
            if (file != null && file.getSize() / 1024 > config.maxSize()) {
                returnValue.put("error", 1);
                returnValue.put("message", "文件超出允许上传大小");
                return returnValue;
            }
            return handleLocalUpload(dir, file, date, number, config,fileServerAddress, returnValue);
        }

    }


    /**
     * 获取我的问题
     * @param page 页码
     * @return
     */
    public PageView<Question> getQuestionList(int page){
        //调用分页算法代码
        PageView<Question> pageView = new PageView<Question>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(accessUser.getUserName());

        jpql.append(" and o.status<?"+ (params.size()+1));
        params.add(100);

        jpql.append(" and o.isStaff=?"+ (params.size()+1));
        params.add(false);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        QueryResult<Question> qr = questionRepository.getScrollData(Question.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Question o :qr.getResultlist()){
                o.setIp(null);//IP地址不显示
            }

            List<String> languageFormExtensionCodeList = null;
            if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
                languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            }
            List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag_cache();

            if(questionTagList != null && questionTagList.size() >0){
                for(Question question : qr.getResultlist()){
                    List<QuestionTagAssociation> questionTagAssociationList = questionCacheManager.query_cache_findQuestionTagAssociationByQuestionId(question.getId());
                    if(questionTagAssociationList != null && questionTagAssociationList.size() >0){
                        for(QuestionTag questionTag : questionTagList){
                            if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
                                if(questionTag.getMultiLanguageExtension() != null && !questionTag.getMultiLanguageExtension().trim().isEmpty()){
                                    Map<String,String> multiLanguageExtensionMap = jsonComponent.toObject(questionTag.getMultiLanguageExtension(), HashMap.class);
                                    if(multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() >0){
                                        questionTag.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
                                    }
                                }
                            }
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

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }


    /**
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @param editorTag 编辑器标签
     * @return HtmlFilterResult Html过滤结果
     */
    private QuestionClientServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent,EditorTag editorTag) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content,editorTag);
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"question",editorTag);
        return new QuestionClientServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);

    }


    /**
     * 校验上传参数
     * @param dir 目录
     * @param userName 用户名称
     * @return
     */
    private QuestionClientServiceImpl.FileUploadConfig validateUpload(String dir, String userName) {
        EditorTag editor = settingComponent.readQuestionEditorTag();
        if (editor == null) return QuestionClientServiceImpl.FileUploadConfig.fail("读取配置失败");

        // 根据 dir 定义不同的规则
        return switch (dir) {
            case "image" -> {
                if (!editor.isImage()) yield QuestionClientServiceImpl.FileUploadConfig.fail("当前图片类型不允许上传");
                if (!userRoleComponent.isPermission(ResourceEnum._2002000, null))
                    yield QuestionClientServiceImpl.FileUploadConfig.fail("权限不足");
                yield QuestionClientServiceImpl.FileUploadConfig.ok(editor.getImageFormat(), editor.getImageSize());
            }
            default -> QuestionClientServiceImpl.FileUploadConfig.fail("缺少dir参数");
        };
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param date 日期
     * @param number 上传文件编号
     * @param fileUploadConfig 文件上传配置
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, String date, String number, QuestionClientServiceImpl.FileUploadConfig fileUploadConfig, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),fileUploadConfig.formats())){
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
        String presigne = fileComponent.createPresigned("file/question/"+date+"/"+dir+"/"+newFileName, fileUploadConfig.maxSize());//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }




    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param date 日期
     * @param number  上传文件编号
     * @param fileUploadConfig 文件上传配置
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, String date, String number, QuestionClientServiceImpl.FileUploadConfig fileUploadConfig, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();

        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),fileUploadConfig.formats())){
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

}
