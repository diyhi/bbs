package cms.service.frontend.impl;

import cms.component.*;
import cms.component.fileSystem.FileComponent;
import cms.component.filterWord.SensitiveWordFilterComponent;
import cms.component.follow.FollowCacheManager;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.payment.PaymentComponent;
import cms.component.question.AnswerCacheManager;
import cms.component.question.AnswerComponent;
import cms.component.question.QuestionCacheManager;
import cms.component.question.QuestionComponent;
import cms.component.setting.SettingCacheManager;
import cms.component.setting.SettingComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.user.*;
import cms.config.BusinessException;
import cms.dto.*;
import cms.dto.frontendModule.AnswerDTO;
import cms.dto.frontendModule.UserAnswerCountDTO;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.frontendModule.ConfigAnswerPage;
import cms.model.frontendModule.FrontendApi;
import cms.model.message.Remind;
import cms.model.payment.PaymentLog;
import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.model.user.UserDynamic;
import cms.repository.message.RemindRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.AnswerClientService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 前台答案服务
 */
@Service
public class AnswerClientServiceImpl implements AnswerClientService {
    private static final Logger logger = LogManager.getLogger(AnswerClientServiceImpl.class);

    @Resource
    UserCacheManager userCacheManager;
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    AnswerRepository answerRepository;
    @Resource
    QuestionRepository questionRepository;
    @Resource AnswerCacheManager answerCacheManager;
    @Resource StaffCacheManager staffCacheManager;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    AnswerComponent answerComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource QuestionCacheManager questionCacheManager;
    @Resource
    SettingComponent settingComponent;
    @Resource
    SensitiveWordFilterComponent sensitiveWordFilterComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    PointComponent pointComponent;
    @Resource
    UserDynamicConfig userDynamicConfig;
    @Resource
    UserDynamicComponent userDynamicComponent;
    @Resource PointLogConfig pointLogConfig;
    @Resource FollowCacheManager followCacheManager;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    RemindConfig remindConfig;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindRepository remindRepository;
    @Resource SettingCacheManager settingCacheManager;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    QuestionComponent questionComponent;
    @Resource CaptchaCacheManager captchaCacheManager;
    @Resource
    PaymentComponent paymentComponent;

    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {
    }
    //允许文件格式
    private record AllowFormat(List<String> formatList, long fileSize) {
    }

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
        public static AnswerClientServiceImpl.FileUploadConfig ok(List<String> formats, long maxSize) {
            return new AnswerClientServiceImpl.FileUploadConfig(true, null, formats, maxSize);
        }
        public static AnswerClientServiceImpl.FileUploadConfig fail(String message) {
            return new AnswerClientServiceImpl.FileUploadConfig(false, message, null, 0);
        }
    }


    /**
     * 获取答案分页
     * @param page 页码
     * @param questionId 问题Id
     * @param answerId 答案Id
     * @param request 请求信息
     * @return
     */
    public PageView<Answer> getAnswerPage(Integer page, Long questionId, Long answerId, HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigAnswerPage configAnswerPage) {
            int pageCount = 10;// 页码显示总数
            int sort = 1;//排序
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if (configAnswerPage.getMaxResult() != null && configAnswerPage.getMaxResult() > 0) {
                maxResult = configAnswerPage.getMaxResult();
            }
            //排序
            if (configAnswerPage.getSort() != null && configAnswerPage.getSort() > 0) {
                sort = configAnswerPage.getSort();
            }

            //页码显示总数
            if (configAnswerPage.getPageCount() != null && configAnswerPage.getPageCount() > 0) {
                pageCount = configAnswerPage.getPageCount();
            }

            AccessUser accessUser = AccessUserThreadLocal.get();

            PageForm pageForm = new PageForm();
            pageForm.setPage(page);

            if(answerId != null && answerId >0L && page == null){
                Integer row_sort = 1;
                if(sort == 1){
                    row_sort = 1;
                }else if(sort == 2){
                    row_sort = 2;
                }
                Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);//查询缓存
                if(answer != null && answer.getAdoption()){//如果答案为采纳，则显示第一页
                    pageForm.setPage(1);
                }else{
                    Long row = answerRepository.findRowByAnswerId(answerId,questionId,20,row_sort);
                    if(row != null && row >0L){

                        Integer _page = Integer.parseInt(String.valueOf(row))/maxResult;
                        if(Integer.parseInt(String.valueOf(row))%maxResult >0){//余数大于0要加1

                            _page = _page+1;
                        }


                        pageForm.setPage(_page);
                    }
                }
            }

            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();
            PageView<Answer> pageView = new PageView<Answer>(maxResult,pageForm.getPage(),pageCount);
            //当前页
            int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();


            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

            if(questionId != null && questionId >0L){
                jpql.append(" o.questionId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
                params.add(questionId);//设置o.parentId=?2参数
            }else{
                return pageView;
            }


            jpql.append(" and o.status=?"+ (params.size()+1));
            params.add(20);

            orderby.put("adoption", "desc");//采纳答案时间排序   新-->旧
            //排行依据
            if(sort == 1){
                orderby.put("postTime", "desc");//回答时间排序   新-->旧
            }else if(sort == 2){
                orderby.put("postTime", "asc");//回答时间排序  旧-->新
            }
            //根据sort字段降序排序
            QueryResult<Answer> qr = answerRepository.getScrollData(Answer.class,firstIndex, pageView.getMaxresult(),jpql.toString(),params.toArray(),orderby);


            List<Long> answerIdList = new ArrayList<Long>();
            List<Answer> answerList = qr.getResultlist();
            SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

            Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合
            if(answerList != null && answerList.size() >0){
                for(Answer answer : answerList){
                    if(answer.getContent() != null && !answer.getContent().trim().isEmpty()){
                        //处理富文本路径
                        answer.setContent(fileComponent.processRichTextFilePath(answer.getContent(),"answer"));
                    }
                    //只有中国地区简体中文语言才显示IP归属地
                    if(accessUser != null && systemSetting.isShowIpAddress() && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
                        answer.setIpAddress(IpAddress.queryProvinceAddress(answer.getIp()));
                    }
                    answer.setIp(null);//IP地址不显示
                    if(!answer.getIsStaff()){//会员
                        User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
                        if(user != null){
                            answer.setAccount(user.getAccount());
                            answer.setNickname(user.getNickname());
                            answer.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                            answer.setAvatarName(user.getAvatarName());
                            userRoleNameMap.put(answer.getUserName(), null);

                            if(user.getCancelAccountTime() != -1L){//账号已注销
                                answer.setUserInfoStatus(-30);
                            }
                        }
                    }else{
                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(answer.getUserName());
                        if(sysUsers != null){
                            answer.setNickname(sysUsers.getNickname());
                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                answer.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                answer.setAvatarName(sysUsers.getAvatarName());
                            }
                        }
                        answer.setAccount(answer.getUserName());//员工用户名和账号是同一个
                    }

                    //非正常状态用户清除显示数据
                    if(answer.getUserInfoStatus() <0){
                        answer.setUserName(null);
                        answer.setAccount(null);
                        answer.setNickname(null);
                        answer.setAvatarPath(null);
                        answer.setAvatarName(null);
                        answer.setUserRoleNameList(new ArrayList<String>());
                        answer.setContent("");
                        answer.setMarkdownContent("");
                    }
                }

            }

            if(userRoleNameMap.size() >0){
                for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                    List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey(),request);
                    entry.setValue(roleNameList);
                }
            }





            if(answerList != null && answerList.size() >0){
                for(Answer answer : answerList){
                    answerIdList.add(answer.getId());

                    if(!answer.getIsStaff()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(answer.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && roleNameList.size() >0){
                                    answer.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }
                }
            }

            if(answerIdList.size() >0){
                List<AnswerReply> replyList = answerRepository.findReplyByAnswerId(answerIdList,20);
                if(replyList != null && replyList.size() >0){
                    for(Answer answer : answerList){
                        for(AnswerReply answerReply : replyList){
                            if(answer.getId().equals(answerReply.getAnswerId())){
                                //只有中国地区简体中文语言才显示IP归属地
                                if(accessUser != null && systemSetting.isShowIpAddress() && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
                                    answerReply.setIpAddress(IpAddress.queryProvinceAddress(answerReply.getIp()));
                                }
                                answerReply.setIp(null);//IP地址不显示
                                if(!answerReply.getIsStaff()){//会员
                                    User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                                    if(user != null){
                                        answerReply.setAccount(user.getAccount());
                                        answerReply.setNickname(user.getNickname());
                                        answerReply.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                        answerReply.setAvatarName(user.getAvatarName());

                                        List<String> roleNameList = userRoleComponent.queryUserRoleName(answerReply.getUserName(),request);
                                        answerReply.setUserRoleNameList(roleNameList);

                                        if(user.getCancelAccountTime() != -1L){//账号已注销
                                            answerReply.setUserInfoStatus(-30);
                                        }
                                    }


                                }else{
                                    SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(answerReply.getUserName());
                                    if(sysUsers != null){
                                        answerReply.setNickname(sysUsers.getNickname());
                                        if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                            answerReply.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                            answerReply.setAvatarName(sysUsers.getAvatarName());
                                        }
                                    }
                                    answerReply.setAccount(answerReply.getUserName());//员工用户名和账号是同一个
                                }

                                if(answerReply.getFriendUserName() != null && !answerReply.getFriendUserName().trim().isEmpty()){
                                    if(answerReply.getIsFriendStaff() == false){//会员
                                        User user = userCacheManager.query_cache_findUserByUserName(answerReply.getFriendUserName());
                                        if(user != null && user.getState().equals(1) && user.getCancelAccountTime().equals(-1L)){
                                            answerReply.setFriendAccount(user.getAccount());
                                            answerReply.setFriendNickname(user.getNickname());
                                            answerReply.setFriendAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                            answerReply.setFriendAvatarName(user.getAvatarName());
                                        }

                                    }else{
                                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(answerReply.getFriendUserName());
                                        if(sysUsers != null){
                                            answerReply.setFriendNickname(sysUsers.getNickname());
                                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                                answerReply.setFriendAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                                answerReply.setFriendAvatarName(sysUsers.getAvatarName());
                                            }
                                        }
                                        answerReply.setFriendAccount(answerReply.getFriendUserName());//员工用户名和账号是同一个

                                    }
                                }


                                //非正常状态用户清除显示数据
                                if(answerReply.getUserInfoStatus() <0){
                                    answerReply.setUserName(null);
                                    answerReply.setAccount(null);
                                    answerReply.setNickname(null);
                                    answerReply.setAvatarPath(null);
                                    answerReply.setAvatarName(null);
                                    answerReply.setUserRoleNameList(new ArrayList<String>());
                                    answerReply.setContent("");
                                }

                                answer.addAnswerReply(answerReply);
                            }
                        }
                        //回复排序
                        answerComponent.replySort(answer.getAnswerReplyList());
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
     * 获取添加答案界面信息
     * @return
     */
    public Map<String,Object> getAddAnswerViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        if(accessUser != null){
            boolean captchaKey = captchaComponent.answer_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());//是否有验证码
            }
        }
        //如果全局不允许提交答案
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isAllowAnswer()){
            returnValue.put("allowAnswer",true);//允许提交答案
        }else{
            returnValue.put("allowAnswer",false);//不允许提交答案
        }

        returnValue.put("availableTag", answerComponent.availableTag());//答案编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }
    /**
     * 添加答案
     * @param answerDTO 答案表单
     * @param request 请求信息
     */
    public void addAnswer(AnswerDTO answerDTO, HttpServletRequest request){
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answer", "只读模式不允许提交数据"));
        }


        if(answerDTO.getQuestionId() == null || answerDTO.getQuestionId() <=0L){
            throw new BusinessException(Map.of("answer","问题Id不能为空"));
        }
        Question question = questionCacheManager.query_cache_findById(answerDTO.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answer","问题不存在"));
        }

        //是否全局允许回答
        if(!systemSetting.isAllowAnswer()){
            errors.put("answer", "禁止回答");
        }
        if(!question.isAllow()){
            errors.put("answer", "禁止回答");
        }
        if(!question.getStatus().equals(20)){//已发布
            errors.put("answer", "已发布的问题才允许回答");
        }

        //验证码
        boolean isCaptcha = captchaComponent.answer_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(answerDTO.getCaptchaKey(), answerDTO.getCaptchaValue(), errors);
        }

        Answer answer = new Answer();
        LocalDateTime postTime = LocalDateTime.now();

        //前台发表评论审核状态
        if(systemSetting.getAnswer_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            answer.setStatus(10);//10.待审核
        }else if(systemSetting.getAnswer_review().equals(30)){
            //是否有当前功能操作权限
            boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._10012000,null);
            if(flag_permission){
                answer.setStatus(20);//20.已发布
            }else{
                answer.setStatus(10);//10.待审核
            }
        }else{
            answer.setStatus(20);//20.已发布
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10007000,null);//如果无权限设置403状态码

        //如果实名用户才允许回答
        if(systemSetting.isRealNameUserAllowAnswer()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("answer", "实名用户才允许回答");
            }

        }

        if (answerDTO.getIsMarkdown() != null && answerDTO.getIsMarkdown()) {
            if(systemSetting.getSupportEditor().equals(10)){
                errors.put("answer", "不支持此编辑器");
            }
        } else {
            if(systemSetting.getSupportEditor().equals(20)){
                errors.put("answer", "不支持此编辑器");
            }
        }

        // 处理内容过滤和文件路径
        AnswerClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, answerDTO.getContent(), answerDTO.getIsMarkdown(), answerDTO.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            errors.put("content",  "内容不能为空");
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        //风控文本
        String riskControlText = value;


        //不含标签内容
        String text = textFilterComponent.filterText(htmlFilterResult.content);

        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();


        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            errors.put("content",  "内容不能为空");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            value = sensitiveWordFilterComponent.filterSensitiveWord(value, wordReplace);
        }


        answer.setPostTime(postTime);
        answer.setQuestionId(answerDTO.getQuestionId());
        answer.setContent(value);
        answer.setIsMarkdown(answerDTO.getIsMarkdown());
        answer.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        answer.setIsStaff(false);
        answer.setUserName(accessUser.getUserName());

        answer.setIp(IpAddress.getClientIpAddress(request));
        //保存答案
        answerRepository.saveAnswer(answer);


        PointLog pointLog = new PointLog();
        pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
        pointLog.setModule(800);//800.答案
        pointLog.setParameterId(answer.getId());//参数Id
        pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
        pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

        pointLog.setPoint(systemSetting.getAnswer_rewardPoint());//积分
        pointLog.setUserName(accessUser.getUserName());//用户名称
        pointLog.setRemark("");

        //增加用户积分
        userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getAnswer_rewardPoint(), pointComponent.createPointLogObject(pointLog));

        //用户动态
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
        userDynamic.setUserName(accessUser.getUserName());
        userDynamic.setModule(600);//模块 600.答案
        userDynamic.setQuestionId(question.getId());
        userDynamic.setAnswerId(answer.getId());
        userDynamic.setPostTime(postTime);
        userDynamic.setStatus(answer.getStatus());
        userDynamic.setFunctionIdGroup(","+question.getId()+","+answer.getId()+",");
        Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
        userRepository.saveUserDynamic(new_userDynamic);



        //删除缓存
        userCacheManager.delete_cache_findUserById(accessUser.getUserId());
        userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());
        questionCacheManager.delete_cache_findById(answerDTO.getQuestionId());
        answerCacheManager.delete_cache_answerCount(answer.getUserName());
        followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());

        //修改问题最后回答时间
        questionRepository.updateQuestionAnswerTime(answerDTO.getQuestionId(),postTime);


        if(!question.getIsStaff()){
            User _user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回答不发提醒给自己

                //提醒作者
                Remind remind = new Remind();
                remind.setId(remindConfig.createRemindId(_user.getId()));
                remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                remind.setTypeCode(120);//120:别人回答了我的问题
                remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                remind.setQuestionId(question.getId());//问题Id
                remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id

                Object remind_object = remindComponent.createRemindObject(remind);
                remindRepository.saveRemind(remind_object);

                //删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
            }
        }

        if(htmlFilterResult.htmlProcessingResult.getMentionUserNameList() != null && !htmlFilterResult.htmlProcessingResult.getMentionUserNameList().isEmpty()){
            int count = 0;
            for(String mentionUserName : htmlFilterResult.htmlProcessingResult.getMentionUserNameList()){
                count++;
                if(systemSetting.getAllowMentionMaxNumber() != null && count > systemSetting.getAllowMentionMaxNumber()){
                    break;
                }
                User _user = userCacheManager.query_cache_findUserByUserName(mentionUserName);
                if(_user != null && !mentionUserName.equals(accessUser.getUserName())){//不发提醒给自己
                    //提醒用户
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(240);// 240:别人在答案提到我
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setQuestionId(question.getId());//问题Id
                    remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());


                }
            }
        }


        //异步执行会员卡赠送任务(长期任务类型)
        membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(accessUser.getUserName());




        String fileNumber = "b"+ accessUser.getUserId();

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult().getImageNameList() != null && !htmlFilterResult.htmlProcessingResult().getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult().getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+ File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }
            }
        }
        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("answer", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }


    }
    /**
     * 获取修改答案界面信息
     * @param answerId 答案Id
     * @return
     */
    public Map<String,Object> getEditAnswerViewModel(Long answerId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AccessUser accessUser = AccessUserThreadLocal.get();

        if(accessUser != null){
            boolean captchaKey = captchaComponent.answer_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
            }
        }
        if(accessUser != null && answerId != null && answerId >0L){
            Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);//查询缓存
            if(answer != null && answer.getStatus() <100 && answer.getUserName().equals(accessUser.getUserName())){
                answer.setIp(null);//IP地址不显示


                if(answer.getContent() != null && !answer.getContent().trim().isEmpty()){
                    //处理富文本路径
                    answer.setContent(fileComponent.processRichTextFilePath(answer.getContent(),"answer"));
                }

                returnValue.put("answer",answer);
            }
        }


        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //如果全局不允许提交答案
        if(!systemSetting.isAllowAnswer()){
            returnValue.put("allowAnswer",false);//不允许提交答案
        }else{
            returnValue.put("allowAnswer",true);//允许提交答案
        }
        returnValue.put("availableTag", answerComponent.availableTag());//答案编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }
    /**
     * 修改答案
     * @param answerDTO 答案表单
     * @param request 请求信息
     */
    public void editAnswer(AnswerDTO answerDTO, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answer", "只读模式不允许提交数据"));
        }


        if(answerDTO.getAnswerId() == null || answerDTO.getAnswerId() <=0L){
            throw new BusinessException(Map.of("answer", "答案Id不能为空"));
        }

        //验证码
        boolean isCaptcha = captchaComponent.answer_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(answerDTO.getCaptchaKey(), answerDTO.getCaptchaValue(), errors);
        }

        Answer answer = answerRepository.findByAnswerId(answerDTO.getAnswerId());
        if(answer == null){
            throw new BusinessException(Map.of("answer", "答案不存在"));
        }
        Question question = questionRepository.findById(answer.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answer", "问题不存在"));
        }

        if(!answer.getUserName().equals(accessUser.getUserName())){
            errors.put("answer", "只允许修改自己发布的答案");//
        }
        if(answer.getAdoption()){
            errors.put("answer", "不允许修改已采纳的答案");
        }
        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10008000,null);//如果无权限设置403状态码


        //如果全局不允许提交答案
        if(!systemSetting.isAllowAnswer()){
            errors.put("answer", "禁止回答");
        }

        //如果实名用户才允许提交回答
        if(systemSetting.isRealNameUserAllowAnswer()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("answer", "实名用户才允许提交回答");
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        //旧状态
        Integer old_status = answer.getStatus();
        //旧内容
        String old_content = answer.getContent();

        if(answer.getStatus().equals(20)){//如果已发布，则重新执行发贴审核逻辑
            //前台发表答案审核状态
            if(systemSetting.getAnswer_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
                answer.setStatus(10);//10.待审核
            }else if(systemSetting.getAnswer_review().equals(30)){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._10012000,null);
                if(flag_permission){
                    answer.setStatus(20);//20.已发布
                }else{
                    answer.setStatus(10);//10.待审核
                }
            }else{
                answer.setStatus(20);//20.已发布
            }
        }

        // 处理内容过滤和文件路径
        AnswerClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, answerDTO.getContent(), answer.getIsMarkdown(), answerDTO.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            errors.put("content",  "内容不能为空");
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        //风控文本
        String riskControlText = value;


        //不含标签内容
        String text = textFilterComponent.filterText(htmlFilterResult.content);

        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();


        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            errors.put("content",  "内容不能为空");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            value = sensitiveWordFilterComponent.filterSensitiveWord(value, wordReplace);
        }

        answer.setContent(value);
        answer.setMarkdownContent(htmlFilterResult.formatterMarkdown);


        LocalDateTime time = LocalDateTime.now();
        int i = answerRepository.updateAnswer(answer.getId(),answer.getContent(),answer.getMarkdownContent(),answer.getStatus(),time,answer.getUserName());
        if(i ==0){
            throw new BusinessException(Map.of("answer", "修改答案失败")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }


        if(i >0 && answer.getStatus() < 100 && !old_status.equals(answer.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
            if(user != null){
                //修改答案状态
                userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus());
            }

        }

        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answer.getId());


        //上传文件编号
        String fileNumber = questionComponent.generateFileNumber(answer.getUserName(), answer.getIsStaff());

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }


        //旧图片名称
        List<String> old_ImageName = textFilterComponent.readImageName(old_content,"answer");
        if(old_ImageName != null && old_ImageName.size() >0){
            Iterator<String> iter = old_ImageName.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();//含有路径
                String old_name = FileUtil.getName(imageName);

                for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                    String new_name = FileUtil.getName(new_imageName);
                    if(old_name.equals(new_name)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_ImageName != null && old_ImageName.size() >0){
                for(String imagePath : old_ImageName){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
                        continue;
                    }
                    //替换路径中的..号
                    imagePath = FileUtil.toRelativePath(imagePath);
                    //替换路径分割符
                    imagePath = Strings.CS.replace(imagePath, "/", File.separator);

                    Boolean state = fileComponent.deleteFile(imagePath);
                    if(state != null && !state){
                        //替换指定的字符，只替换第一次出现的
                        imagePath = Strings.CS.replaceOnce(imagePath, "file"+File.separator+"answer"+File.separator, "");
                        imagePath = Strings.CS.replace(imagePath, File.separator, "_");//替换所有出现过的字符

                        //创建删除失败文件
                        fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+imagePath);

                    }
                }

            }
        }

        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("answer", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }
    /**
     * 删除答案
     * @param answerId 答案Id
     */
    public void deleteAnswer(Long answerId){
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answer", "只读模式不允许提交数据"));
        }


        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answer", "答案Id不能为空"));
        }

        Answer answer = answerRepository.findByAnswerId(answerId);
        if(answer == null){
            throw new BusinessException(Map.of("answer", "答案不存在"));
        }
        Question question = questionRepository.findById(answer.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answer", "问题不存在"));
        }
        if(!answer.getUserName().equals(accessUser.getUserName())){
            errors.put("answer", "只允许删除自己发布的答案");
        }

        if(answer.getStatus() > 100){
            errors.put("answer", "答案已删除");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10009000,null);//如果无权限设置403状态码
        Integer constant = 100;
        int i = answerRepository.markDeleteAnswer(answer.getId(),constant);
        if(i == 0){
            throw new BusinessException(Map.of("answer", "删除答案失败"));
        }

        if(i >0 && answer.getStatus() < 100){
            User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
            if(user != null){
                //修改答案状态
                userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus()+constant);
            }

        }
        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answer.getId());

    }
    /**
     * 采纳答案
     * @param answerId 答案Id
     */
    public void adoptionAnswer(Long answerId){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answer", "只读模式不允许提交数据"));
        }

        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answer", "答案Id不能为空"));
        }

        Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);
        if(answer == null){
            throw new BusinessException(Map.of("answer", "答案不存在"));
        }
        Question question = questionCacheManager.query_cache_findById(answer.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answer", "问题不存在"));
        }

        if(!question.getStatus().equals(20)){
            throw new BusinessException(Map.of("answer", "该问题不允许采纳答案"));
        }

        if(question.getAdoptionAnswerId() >0L){
            throw new BusinessException(Map.of("answer", "该问题已经采纳答案"));
        }

        if(!accessUser.getUserName().equals(question.getUserName())){
            throw new BusinessException(Map.of("answer", "不是提交该问题的用户不允许采纳答案"));
        }

        LocalDateTime time = LocalDateTime.now();

        User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());

        //是否更改采纳答案
        boolean changeAdoption = false;
        if(question.getAdoptionAnswerId() >0L){
            changeAdoption = true;

        }

        //回答用户获得积分
        Long point = 0L;

        //用户悬赏积分日志
        Object pointLogObject = null;
        if(user != null && answer.getIsStaff() ==false  && question.getPoint() != null && question.getPoint() >0L){
            point = question.getPoint();

            PointLog reward_pointLog = new PointLog();
            reward_pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
            reward_pointLog.setModule(1100);//1100.采纳答案
            reward_pointLog.setParameterId(answer.getId());//参数Id
            reward_pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
            reward_pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
            reward_pointLog.setPointState(1);//积分状态  1:账户存入  2:账户支出
            reward_pointLog.setPoint(question.getPoint());//积分
            reward_pointLog.setUserName(answer.getUserName());//用户名称
            reward_pointLog.setRemark("");
            reward_pointLog.setTimes(time);
            pointLogObject = pointComponent.createPointLogObject(reward_pointLog);
        }

        //用户悬赏金额日志
        Object paymentLogObject = null;
        //平台分成
        QuestionRewardPlatformShare questionRewardPlatformShare = null;
        //回答用户分成金额
        BigDecimal userNameShareAmount = new BigDecimal("0");
        //平台分成金额
        BigDecimal platformShareAmount = new BigDecimal("0");

        if(question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){

            if(!answer.getIsStaff()){//会员回答
                if(user != null){
                    Integer questionRewardPlatformShareProportion = settingRepository.findSystemSetting().getQuestionRewardPlatformShareProportion();//平台分成比例

                    if(questionRewardPlatformShareProportion >0){
                        //平台分成金额 = 总金额 * (平台分成比例 /100)
                        platformShareAmount = question.getAmount().multiply(new BigDecimal(String.valueOf(questionRewardPlatformShareProportion)).divide(new BigDecimal("100")));

                        userNameShareAmount = question.getAmount().subtract(platformShareAmount);
                    }else{
                        userNameShareAmount = question.getAmount();
                    }




                    PaymentLog reward_paymentLog = new PaymentLog();
                    String paymentRunningNumber = paymentComponent.createRunningNumber(user.getId());
                    reward_paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                    reward_paymentLog.setPaymentModule(100);//支付模块 100.采纳答案
                    reward_paymentLog.setSourceParameterId(String.valueOf(answer.getId()));//参数Id
                    reward_paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
                    reward_paymentLog.setOperationUserName(accessUser.getUserName());//操作用户名称  0:系统  1: 员工  2:会员
                    reward_paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                    reward_paymentLog.setAmount(userNameShareAmount);//金额
                    reward_paymentLog.setInterfaceProduct(0);//接口产品
                    reward_paymentLog.setUserName(answer.getUserName());//用户名称
                    reward_paymentLog.setTimes(time);
                    paymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);

                    if(questionRewardPlatformShareProportion >0){
                        //平台分成
                        questionRewardPlatformShare = new QuestionRewardPlatformShare();
                        questionRewardPlatformShare.setQuestionId(question.getId());//问题Id
                        questionRewardPlatformShare.setStaff(answer.getIsStaff());//分成用户是否为员工
                        questionRewardPlatformShare.setPostUserName(question.getUserName());//提问题的用户名称
                        questionRewardPlatformShare.setAnswerUserName(answer.getUserName());//回答问题的用户名称
                        questionRewardPlatformShare.setPlatformShareProportion(questionRewardPlatformShareProportion);//平台分成比例
                        questionRewardPlatformShare.setAnswerUserShareRunningNumber(paymentRunningNumber);//回答问题的用户分成流水号
                        questionRewardPlatformShare.setTotalAmount(question.getAmount());//总金额
                        questionRewardPlatformShare.setShareAmount(platformShareAmount);//平台分成金额
                        questionRewardPlatformShare.setAdoptionTime(time);//采纳时间
                    }
                }
            }else{//员工回答
                //收益归平台
                platformShareAmount = question.getAmount();

                //平台分成
                questionRewardPlatformShare = new QuestionRewardPlatformShare();
                questionRewardPlatformShare.setQuestionId(question.getId());//问题Id
                questionRewardPlatformShare.setStaff(answer.getIsStaff());//分成用户是否为员工
                questionRewardPlatformShare.setPostUserName(question.getUserName());//提问题的用户名称
                questionRewardPlatformShare.setAnswerUserName(answer.getUserName());//回答问题的用户名称
                questionRewardPlatformShare.setPlatformShareProportion(100);//平台分成比例
                //	questionRewardPlatformShare.setAnswerUserShareRunningNumber();//回答问题的用户分成流水号
                questionRewardPlatformShare.setTotalAmount(question.getAmount());//总金额
                questionRewardPlatformShare.setShareAmount(platformShareAmount);//平台分成金额
                questionRewardPlatformShare.setAdoptionTime(time);//采纳时间
            }

        }


        int i = answerRepository.updateAdoptionAnswer(answer.getQuestionId(), answerId,changeAdoption,null,null,null,null,
                answer.getUserName(),point,pointLogObject,userNameShareAmount,paymentLogObject,questionRewardPlatformShare);
        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answerId);
        questionCacheManager.delete_cache_findById(answer.getQuestionId());
        answerCacheManager.delete_cache_answerCount(answer.getUserName());

        if(user != null){
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());

            //异步执行会员卡赠送任务(长期任务类型)
            membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

        }

    }
    /**
     * 获取添加答案回复界面信息
     * @return
     */
    public Map<String,Object> getAddReplyToAnswerViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.answer_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//验证码key
            }
        }
        //如果全局不允许提交答案
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isAllowAnswer()){
            returnValue.put("allowReply",true);//允许提交回复
            returnValue.put("availableTag", answerComponent.replyAvailableTag());//回复编辑器允许使用标签
            returnValue.put("fileSystem", fileComponent.getFileSystem());
        }else{
            returnValue.put("allowReply",false);//不允许提交回复
        }
        return returnValue;
    }
    /**
     * 添加答案回复
     * @param answerId 答案回复Id
     * @param friendReplyId 对方回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addAnswerReply(Long answerId,Long friendReplyId,String content,
                               String captchaKey,String captchaValue,
                               HttpServletRequest request){
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answerReply", "只读模式不允许提交数据"));
        }



        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answerReply", "答案Id不能为空"));
        }

        //验证码
        boolean isCaptcha = captchaComponent.answer_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }

        Answer answer = answerRepository.findByAnswerId(answerId);
        if(answer == null){
            throw new BusinessException(Map.of("answer", "答案不存在"));
        }
        Question question = questionRepository.findById(answer.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answer", "问题不存在"));
        }

        //是否全局允许回答
        if(!systemSetting.isAllowAnswer()){
            errors.put("answerReply", "禁止回答");
        }
        if(!question.isAllow()){
            errors.put("answerReply", "禁止回答");
        }
        if(!question.getStatus().equals(20)){//已发布
            errors.put("answerReply", "已发布的问题才允许回复");
        }

        AnswerReply friendAnswerReply = null;
        if(friendReplyId != null && friendReplyId >0){
            friendAnswerReply = answerRepository.findReplyByReplyId(friendReplyId);
            if(friendAnswerReply != null){
                if(!friendAnswerReply.getAnswerId().equals(answerId)){
                    errors.put("friendReplyId", "对方回复Id和答案Id不对应");
                }
            }else{
                errors.put("friendReplyId", "对方回复Id不存在");
            }
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10013000,null);//如果无权限设置403状态码


        //如果实名用户才允许回答
        if(systemSetting.isRealNameUserAllowAnswer()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("answerReply", "实名用户才允许回答");
            }

        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        //回复
        AnswerReply answerReply = new AnswerReply();
        LocalDateTime postTime = LocalDateTime.now();

        //前台提交答案审核状态
        if(systemSetting.getAnswerReply_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            answerReply.setStatus(10);//10.待审核
        }else if(systemSetting.getAnswerReply_review().equals(30)){
            //是否有当前功能操作权限
            boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._10016000,null);
            if(flag_permission){
                answerReply.setStatus(20);//20.已发布
            }else{
                answerReply.setStatus(10);//10.待审核
            }
        }else{
            answerReply.setStatus(20);//20.已发布
        }

        content = textFilterComponent.filterReplyTag(request,content.trim(),settingComponent.readAnswerEditorTag());

        CorrectionResult correctionResult = textFilterComponent.correctionReplyTag(request,content);
        if(correctionResult.getCorrectedHtml() == null || correctionResult.getCorrectedHtml().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        String replyContent = correctionResult.getCorrectedHtml();

        String riskControlText = correctionResult.getCorrectedHtml();

        //不含标签内容
        String text = textFilterComponent.filterText(content);
        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        if(!correctionResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }


        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            replyContent = sensitiveWordFilterComponent.filterSensitiveWord(replyContent, wordReplace);
        }


        answerReply.setPostTime(postTime);
        answerReply.setAnswerId(answer.getId());
        answerReply.setIsStaff(false);
        answerReply.setUserName(accessUser.getUserName());
        answerReply.setIp(IpAddress.getClientIpAddress(request));
        answerReply.setContent(replyContent);
        answerReply.setQuestionId(answer.getQuestionId());

        if(friendAnswerReply != null){
            answerReply.setFriendReplyId(friendReplyId);
            answerReply.setFriendReplyIdGroup(friendAnswerReply.getFriendReplyIdGroup()+friendReplyId+",");
            answerReply.setIsFriendStaff(friendAnswerReply.getIsStaff());
            answerReply.setFriendUserName(friendAnswerReply.getUserName());
        }

        //保存回复
        answerRepository.saveReply(answerReply);

        PointLog pointLog = new PointLog();
        pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
        pointLog.setModule(900);//900.答案回复
        pointLog.setParameterId(answer.getId());//参数Id
        pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
        pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

        pointLog.setPoint(systemSetting.getAnswerReply_rewardPoint());//积分
        pointLog.setUserName(accessUser.getUserName());//用户名称
        pointLog.setRemark("");

        //增加用户积分
        userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getAnswerReply_rewardPoint(), pointComponent.createPointLogObject(pointLog));


        //用户动态
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
        userDynamic.setUserName(accessUser.getUserName());
        userDynamic.setModule(700);//模块 700.答案回复
        userDynamic.setQuestionId(answerReply.getQuestionId());
        userDynamic.setAnswerId(answerReply.getAnswerId());
        userDynamic.setAnswerReplyId(answerReply.getId());
        userDynamic.setPostTime(answerReply.getPostTime());
        userDynamic.setStatus(answerReply.getStatus());
        userDynamic.setFunctionIdGroup(","+answerReply.getQuestionId()+","+answerReply.getAnswerId()+","+answerReply.getId()+",");
        Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
        userRepository.saveUserDynamic(new_userDynamic);

        //删除缓存
        userCacheManager.delete_cache_findUserById(accessUser.getUserId());
        userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());
        followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());

        //修改问题最后回复时间
        questionRepository.updateQuestionAnswerTime(answer.getQuestionId(),postTime);

        if(!question.getIsStaff()){
            User _user = userCacheManager.query_cache_findUserByUserName(question.getUserName());
            //别人回复了我的问题
            if(_user != null && !_user.getId().equals(accessUser.getUserId())){//作者回复不发提醒给自己
                //如果别人回复了问题发布者的答案，则不发本类型提醒给问题发布者；如果回复了问题发布者的回复，则不发本类型提醒给问题发布者
                if(!answer.getUserName().equals(question.getUserName()) && !question.getUserName().equals(answerReply.getFriendUserName())){
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(130);//130:别人回复了我的问题
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setQuestionId(answer.getQuestionId());//问题Id
                    remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
                    remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());

                }
            }
        }

        User _user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
        //别人回复了我的答案
        if(!answer.getIsStaff() && _user != null
                && !_user.getId().equals(accessUser.getUserId())//不发提醒给自己
                && !answer.getUserName().equals(answerReply.getFriendUserName())//如果回复了答案发布者的回复，则不发本类型提醒给答案发布者
        ){

            Remind remind = new Remind();
            remind.setId(remindConfig.createRemindId(_user.getId()));
            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
            remind.setTypeCode(140);//140:别人回复了我的答案
            remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
            remind.setQuestionId(answer.getQuestionId());//问题Id
            remind.setQuestionAnswerId(answer.getId());//我的问题答案Id
            remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id


            Object remind_object = remindComponent.createRemindObject(remind);
            remindRepository.saveRemind(remind_object);

            //删除提醒缓存
            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
        }

        Set<String> userNameList = new HashSet<String>();
        List<AnswerReply> replyList = answerRepository.findReplyByAnswerId(answer.getId());
        if(replyList != null && replyList.size() >0){
            for(AnswerReply _reply : replyList){
                //如果是问题发布者的回复，则不发本类型提醒给问题的发布者
                if(!question.getIsStaff() && _reply.getUserName().equals(question.getUserName())){
                    continue;
                }
                //如果是答案发布者的回复，则不发本类型提醒给答案的发布者
                if(!answer.getIsStaff() && _reply.getUserName().equals(answer.getUserName())){
                    continue;
                }
                //员工的回复不发提醒
                if(_reply.getIsStaff()){
                    continue;
                }

                //如果是自己的回复，则不发本类型提醒给自己
                if(_reply.getUserName().equals(accessUser.getUserName())){
                    continue;
                }

                //如果是被回复用户，则不发本类型提醒。由后面的代码发（160:别人回复了我的答案回复）类型提醒
                if(_reply.getUserName().equals(answerReply.getFriendUserName())){
                    continue;
                }

                //如果同一个用户有多条回复,只发一条提醒
                if(userNameList.contains(_reply.getUserName())){
                    continue;
                }


                userNameList.add(_reply.getUserName());

                _user = userCacheManager.query_cache_findUserByUserName(_reply.getUserName());

                if(_user != null){
                    //提醒
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(150);//150:别人回复了我回复过的答案
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setQuestionId(_reply.getQuestionId());//问题Id
                    remind.setQuestionReplyId(_reply.getId());//我的问题回复Id


                    remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
                    remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id


                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                }
            }
        }


        if(friendAnswerReply != null && !friendAnswerReply.getIsStaff()){
            _user = userCacheManager.query_cache_findUserByUserName(friendAnswerReply.getUserName());
            if(_user != null && !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己
                //提醒
                Remind remind = new Remind();
                remind.setId(remindConfig.createRemindId(_user.getId()));
                remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                remind.setTypeCode(160);//160:别人回复了我的答案回复
                remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                remind.setQuestionId(friendAnswerReply.getQuestionId());//问题Id
                remind.setQuestionReplyId(friendAnswerReply.getId());//我的问题回复Id


                remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
                remind.setFriendQuestionReplyId(answerReply.getId());//对方的问题回复Id


                Object remind_object = remindComponent.createRemindObject(remind);
                remindRepository.saveRemind(remind_object);

                //删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
            }
        }


        if(correctionResult.getMentionUserNameList() != null && !correctionResult.getMentionUserNameList().isEmpty()){
            int count = 0;
            for(String mentionUserName : correctionResult.getMentionUserNameList()){
                count++;
                if(systemSetting.getAllowMentionMaxNumber() != null && count > systemSetting.getAllowMentionMaxNumber()){
                    break;
                }
                User _mentionUser = userCacheManager.query_cache_findUserByUserName(mentionUserName);
                if(_mentionUser != null && !mentionUserName.equals(accessUser.getUserName())){//不发提醒给自己
                    //提醒用户
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_mentionUser.getId()));
                    remind.setReceiverUserId(_mentionUser.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(250);// 250:别人在答案回复提到我
                    remind.setSendTimeFormat(new Date().getTime());//发送时间格式化
                    remind.setQuestionId(question.getId());//问题Id
                    remind.setFriendQuestionAnswerId(answer.getId());//对方的问题答案Id
                    remind.setFriendQuestionReplyId(answerReply.getId());//对方的答案回复Id
                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_mentionUser.getId());


                }
            }
        }

        //异步执行会员卡赠送任务(长期任务类型)
        membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(accessUser.getUserName());

        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("answer", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }
    /**
     * 获取修改答案回复界面信息
     * @param replyId 答案回复Id
     * @return
     */
    public Map<String,Object> getEditReplyToAnswerViewModel(Long replyId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.answer_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//验证码key
            }
        }
        if(accessUser != null && replyId != null && replyId >0L){
            AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(replyId);//查询缓存
            if(answerReply != null && answerReply.getStatus() <100 && answerReply.getUserName().equals(accessUser.getUserName())){
                answerReply.setIp(null);//IP地址不显示
                returnValue.put("reply",answerReply);
            }
        }

        //如果全局不允许提交答案
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isAllowAnswer()){
            returnValue.put("allowReply",true);//允许提交回复
            returnValue.put("availableTag", answerComponent.replyAvailableTag());//回复编辑器允许使用标签
            returnValue.put("fileSystem", fileComponent.getFileSystem());
        }else{
            returnValue.put("allowReply",false);//不允许提交回复
        }
        return returnValue;
    }
    /**
     * 修改答案回复
     * @param replyId 回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void editAnswerReply(Long replyId,String content,
                                String captchaKey,String captchaValue,
                                HttpServletRequest request){

        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answerReply", "只读模式不允许提交数据"));
        }



        if(replyId == null || replyId <=0L){
            throw new BusinessException(Map.of("answerReply", "回复Id不能为空"));
        }

        //验证码
        boolean isCaptcha = captchaComponent.answer_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }

        AnswerReply answerReply = answerRepository.findReplyByReplyId(replyId);
        if(answerReply == null){
            throw new BusinessException(Map.of("answerReply", "回复不存在"));
        }
        Question question = questionRepository.findById(answerReply.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answerReply", "问题不存在"));
        }

        if(!answerReply.getUserName().equals(accessUser.getUserName())){
            errors.put("answerReply", "只允许修改自己发布的回复");
        }
        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10014000,null);//如果无权限设置403状态码

        //如果全局不允许提交答案
        if(!systemSetting.isAllowAnswer()){
            errors.put("answerReply", "禁止回复");
        }

        //如果实名用户才允许提交答案
        if(systemSetting.isRealNameUserAllowAnswer()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("answerReply", "实名用户才允许提交回复");
            }

        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        //旧状态
        Integer old_status = answerReply.getStatus();
        //旧内容
        String old_content = answerReply.getContent();

        if(answerReply.getStatus().equals(20)){//如果已发布，则重新执行发贴审核逻辑
            //前台发表评论审核状态
            if(systemSetting.getReply_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
                answerReply.setStatus(10);//10.待审核
            }else if(systemSetting.getReply_review().equals(30)){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._10016000,null);
                if(flag_permission){
                    answerReply.setStatus(20);//20.已发布
                }else{
                    answerReply.setStatus(10);//10.待审核
                }
            }else{
                answerReply.setStatus(20);//20.已发布
            }
        }


        content = textFilterComponent.filterReplyTag(request,content.trim(),settingComponent.readAnswerEditorTag());

        CorrectionResult correctionResult = textFilterComponent.correctionReplyTag(request,content);
        if(correctionResult.getCorrectedHtml() == null || correctionResult.getCorrectedHtml().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        String replyContent = correctionResult.getCorrectedHtml();

        String riskControlText = correctionResult.getCorrectedHtml();

        //不含标签内容
        String text = textFilterComponent.filterText(content);
        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        if(!correctionResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "内容不能为空")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            replyContent = sensitiveWordFilterComponent.filterSensitiveWord(replyContent, wordReplace);
        }
        answerReply.setContent(replyContent);

        LocalDateTime time = LocalDateTime.now();
        //修改回复
        int i = answerRepository.updateReply(answerReply.getId(),answerReply.getContent(),answerReply.getUserName(),answerReply.getStatus(),time);

        if(i ==0){
            throw new BusinessException(Map.of("answerReply", "修改回复失败")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        if(i >0 && !old_status.equals(answerReply.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
            if(user != null){
                //修改回复状态
                userRepository.updateUserDynamicReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus());
            }

        }

        //删除缓存
        answerCacheManager.delete_cache_findReplyByReplyId(answerReply.getId());


        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("answer", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("answer", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }
    /**
     * 删除答案回复
     * @param replyId 回复Id
     */
    public void deleteAnswerReply(Long replyId){
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("answerReply", "只读模式不允许提交数据"));
        }


        if(replyId == null || replyId <=0L){
            throw new BusinessException(Map.of("answerReply", "回复Id不能为空"));
        }

        AnswerReply answerReply = answerRepository.findReplyByReplyId(replyId);
        if(answerReply == null){
            throw new BusinessException(Map.of("answerReply", "回复不存在"));
        }
        Question question = questionRepository.findById(answerReply.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("answerReply", "问题不存在"));
        }

        if(!answerReply.getUserName().equals(accessUser.getUserName())){
            throw new BusinessException(Map.of("answerReply", "只允许删除自己发布的回复"));
        }

        if(answerReply.getStatus() > 100){
            throw new BusinessException(Map.of("answerReply", "回复已经删除"));
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._10015000,null);//如果无权限设置403状态码


        Integer constant = 100;
        int i = answerRepository.markDeleteReply(answerReply.getId(),constant);
        if(i ==0){
            throw new BusinessException(Map.of("answerReply", "删除回复失败"));
        }

        User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
        if(user != null){
            //修改回复状态
            userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus()+constant);
        }
        //删除缓存
        answerCacheManager.delete_cache_findReplyByReplyId(answerReply.getId());

    }
    /**
     * 答案图片上传
     * @param questionId 问题Id
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String, Object> uploadImage(Long questionId, String fileName,
                                           MultipartFile file, String fileServerAddress){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        if(systemSetting.getCloseSite().equals(2)){
            returnValue.put("error", 1);
            returnValue.put("message", "只读模式不允许提交数据");
            return returnValue;
        }
        //如果全局不允许提交答案
        if(!systemSetting.isAllowAnswer()){
            returnValue.put("error", 1);
            returnValue.put("message", "不允许提交答案");
            return returnValue;
        }


        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        //上传文件编号
        String number = "b"+accessUser.getUserId();

        //如果实名用户才允许提交答案
        if(systemSetting.isRealNameUserAllowAnswer()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                returnValue.put("error", 1);
                returnValue.put("message","实名用户才允许回答");
                return returnValue;
            }
        }


        if (number.trim().isEmpty() || questionId == null || questionId<0L) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }

        AnswerClientServiceImpl.FileUploadConfig config = validateUpload("image", accessUser.getUserName());
        if (!config.success()) {
            returnValue.put("error", 1);
            returnValue.put("message", config.message());
            return returnValue;
        }


        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload("image",fileName, questionId, number, returnValue);
        } else { // 本地系统
            return handleLocalUpload("image",file, questionId, number, fileServerAddress, returnValue);
        }
    }
    /**
     * 获取用户回答总数
     * @param userName 用户名称
     * @return
     */
    public UserAnswerCountDTO getUserAnswerCount(String userName){
        if(userName == null || userName.trim().isEmpty()) {
            return new UserAnswerCountDTO(0L);
        }

        User user = userCacheManager.query_cache_findUserByUserName(userName.trim());
        if(user != null){
            return new UserAnswerCountDTO(answerCacheManager.query_cache_answerCount(user.getUserName()));
        }
        return new UserAnswerCountDTO(0L);
    }


    /**
     * 获取我的答案
     * @param page 页码
     * @return
     */
    public PageView<Answer> getAnswerList(int page){
        //调用分页算法代码
        PageView<Answer> pageView = new PageView<Answer>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(accessUser.getUserName());

        jpql.append(" and o.isStaff=?"+ (params.size()+1));
        params.add(false);

        jpql.append(" and o.status<?"+ (params.size()+1));
        params.add(100);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        QueryResult<Answer> qr = answerRepository.getScrollData(Answer.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> questionIdList = new ArrayList<Long>();
            for(Answer o :qr.getResultlist()){
                o.setIp(null);//IP地址不显示

                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!questionIdList.contains(o.getQuestionId())){
                    questionIdList.add(o.getQuestionId());
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

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取我的答案回复
     *  @param page 页码
     * @return
     */
    public PageView<AnswerReply> getAnswerReplyList(int page){
        //调用分页算法代码
        PageView<AnswerReply> pageView = new PageView<AnswerReply>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(accessUser.getUserName());

        jpql.append(" and o.isStaff=?"+ (params.size()+1));
        params.add(false);

        jpql.append(" and o.status<?"+ (params.size()+1));
        params.add(100);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        QueryResult<AnswerReply> qr = questionRepository.getScrollData(AnswerReply.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> questionIdList = new ArrayList<Long>();
            for(AnswerReply o :qr.getResultlist()){
                o.setIp(null);//IP地址不显示
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!questionIdList.contains(o.getQuestionId())){
                    questionIdList.add(o.getQuestionId());
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
     * @return HtmlFilterResult Html过滤结果
     */
    private AnswerClientServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content,settingComponent.readAnswerEditorTag());
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"answer",settingComponent.readAnswerEditorTag());
        return new AnswerClientServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);

    }

    /**
     * 校验上传参数
     * @param dir 上传类型，分别为image、media、file
     * @param userName 用户名称
     * @return
     */
    private AnswerClientServiceImpl.FileUploadConfig validateUpload(String dir, String userName) {
        EditorTag editor = settingComponent.readAnswerEditorTag();
        if (editor == null) return AnswerClientServiceImpl.FileUploadConfig.fail("读取配置失败");

        // 根据 dir 定义不同的规则
        return switch (dir) {
            case "image" -> {
                if (!editor.isImage()) yield AnswerClientServiceImpl.FileUploadConfig.fail("当前图片类型不允许上传");
                if (!userRoleComponent.isPermission(ResourceEnum._2002000, null))
                    yield AnswerClientServiceImpl.FileUploadConfig.fail("权限不足");
                yield AnswerClientServiceImpl.FileUploadConfig.ok(editor.getImageFormat(), editor.getImageSize());
            }

            default -> AnswerClientServiceImpl.FileUploadConfig.fail("缺少dir参数");
        };
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param questionId 问题Id
     * @param number 上传文件编号
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, Long questionId, String number, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }
        AnswerClientServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
        if (allowFormat.formatList == null || allowFormat.formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),allowFormat.formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,questionId +"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/answer/"+questionId+"/"+newFileName,allowFormat.fileSize);//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }

    /**
     * 根据文件类型获取允许上传的格式列表
     * @param dir 上传类型，分别为image、media、file
     * @param result 返回信息
     * @return
     */
    private AnswerClientServiceImpl.AllowFormat getAllowFormats(String dir,Map<String, Object> result) {
        List<String> formatList = new ArrayList<>();
        long fileSize = 0;
        switch (dir) {
            case "image":
                EditorTag editorSiteObject = settingComponent.readAnswerEditorTag();
                if(editorSiteObject != null && editorSiteObject.isImage()){//允许上传图片
                    formatList = editorSiteObject.getImageFormat();
                    fileSize = editorSiteObject.getImageSize();;
                }else{
                    result.put("error", 1);
                    result.put("message", "当前图片类型不允许上传");
                }
                break;
            default:
                result.put("error", 1);
                result.put("message", "缺少dir参数");
        }
        return new AnswerClientServiceImpl.AllowFormat(formatList,fileSize);
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param questionId 问题Id
     * @param number  上传文件编号
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, Long questionId, String number, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        AnswerClientServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
        if (allowFormat.formatList == null || allowFormat.formatList.isEmpty()) {
            return result;
        }
        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),allowFormat.formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }

        if(file.getSize()/1024 > allowFormat.fileSize){
            result.put("error", 1);
            result.put("message", "文件超出允许上传大小");
            return result;
        }

        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
        String pathDir = "file"+File.separator+"answer"+File.separator + questionId +File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,questionId +"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/answer/"+questionId+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }
}
