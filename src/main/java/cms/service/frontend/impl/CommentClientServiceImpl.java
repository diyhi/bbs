package cms.service.frontend.impl;

import cms.component.*;
import cms.component.fileSystem.FileComponent;
import cms.component.filterWord.SensitiveWordFilterComponent;
import cms.component.follow.*;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.setting.SettingCacheManager;
import cms.component.setting.SettingComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.topic.*;
import cms.component.user.*;
import cms.config.BusinessException;
import cms.dto.*;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.frontendModule.ConfigCommentPage;
import cms.model.frontendModule.FrontendApi;
import cms.model.message.Remind;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.topic.Comment;
import cms.model.topic.Quote;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.model.user.UserDynamic;
import cms.repository.message.RemindRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.CommentClientService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 前台评论服务
 */
@Service
public class CommentClientServiceImpl implements CommentClientService {
    private static final Logger logger = LogManager.getLogger(CommentClientServiceImpl.class);

    @Resource
    UserCacheManager userCacheManager;
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    CommentRepository commentRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    TopicRepository topicRepository;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    CommentComponent commentComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource StaffCacheManager staffCacheManager;
    @Resource
    JsonComponent jsonComponent;
    @Resource CommentCacheManager commentCacheManager;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    UserRepository userRepository;
    @Resource TopicCacheManager topicCacheManager;
    @Resource
    SettingComponent settingComponent;
    @Resource
    SensitiveWordFilterComponent sensitiveWordFilterComponent;
    @Resource
    HotTopicComponent hotTopicComponent;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource PointComponent pointComponent;
    @Resource
    UserDynamicComponent userDynamicComponent;
    @Resource
    UserDynamicConfig userDynamicConfig;
    @Resource FollowCacheManager followCacheManager;
    @Resource
    RemindRepository remindRepository;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource SettingCacheManager settingCacheManager;
    @Resource
    TopicComponent topicComponent;



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
        public static CommentClientServiceImpl.FileUploadConfig ok(List<String> formats, long maxSize) {
            return new CommentClientServiceImpl.FileUploadConfig(true, null, formats, maxSize);
        }
        public static CommentClientServiceImpl.FileUploadConfig fail(String message) {
            return new CommentClientServiceImpl.FileUploadConfig(false, message, null, 0);
        }
    }


    /**
     * 获取评论分页
     * @param page 页码
     * @param topicId 话题Id
     * @param commentId 评论Id
     * @param request 请求信息
     * @return
     */
    public PageView<Comment> getCommentPage(Integer page, Long topicId, Long commentId, HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigCommentPage configCommentPage) {
            int pageCount = 10;// 页码显示总数
            int sort = 1;//排序
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if (configCommentPage.getMaxResult() != null && configCommentPage.getMaxResult() > 0) {
                maxResult = configCommentPage.getMaxResult();
            }
            //排序
            if(configCommentPage.getSort() != null && configCommentPage.getSort() >0){
                sort = configCommentPage.getSort();
            }

            //页码显示总数
            if(configCommentPage.getPageCount() != null && configCommentPage.getPageCount() >0){
                pageCount = configCommentPage.getPageCount();
            }

            AccessUser accessUser = AccessUserThreadLocal.get();

            PageForm pageForm = new PageForm();
            pageForm.setPage(page);

            if(commentId != null && commentId >0L && page == null){
                Integer row_sort = 1;
                if(sort == 1){
                    row_sort = 1;
                }else if(sort == 2){
                    row_sort = 2;
                }

                Long row = commentRepository.findRowByCommentId(commentId,topicId,20,row_sort);
                if(row != null && row >0L){


                    Integer _page = Integer.parseInt(String.valueOf(row))/maxResult;
                    if(Integer.parseInt(String.valueOf(row))%maxResult >0){//余数大于0要加1

                        _page = _page+1;
                    }
                    pageForm.setPage(_page);
                }
            }

            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();
            PageView<Comment> pageView = new PageView<Comment>(maxResult,pageForm.getPage(),pageCount);
            //当前页
            int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();


            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

            if(topicId != null && topicId >0L){
                jpql.append(" o.topicId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
                params.add(topicId);//设置o.parentId=?2参数
            }
            jpql.append(" and o.status=?"+ (params.size()+1));
            params.add(20);

            //删除第一个and
            String jpql_str = StringUtils.difference(" and", jpql.toString());


            //排行依据
            if(sort == 1){
                orderby.put("postTime", "desc");//发布时间排序   新-->旧
            }else if(sort == 2){
                orderby.put("postTime", "asc");//发布时间排序  旧-->新
            }
            //根据sort字段降序排序
            QueryResult<Comment> qr = commentRepository.getScrollData(Comment.class,firstIndex, pageView.getMaxresult(),jpql_str,params.toArray(),orderby);


            List<Long> commentIdList = new ArrayList<Long>();
            List<Comment> commentList = qr.getResultlist();

            //引用修改Id集合
            List<Long> quoteUpdateIdList = new ArrayList<Long>();
            //重新查询Id
            List<Long> query_quoteUpdateIdList = new ArrayList<Long>();
            //新引用集合
            Map<Long,String> new_quoteList = new HashMap<Long,String>();//key:自定义评论Id value:自定义评论内容(文本)

            Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id 角色名称集合

            SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

            if(commentList != null && commentList.size() >0){
                for(Comment comment : commentList){
                    if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
                        //处理富文本路径
                        comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
                    }
                    //只有中国地区简体中文语言才显示IP归属地
                    if(accessUser != null && systemSetting.isShowIpAddress() && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
                        comment.setIpAddress(IpAddress.queryProvinceAddress(comment.getIp()));
                    }
                    comment.setIp(null);//IP地址不显示





                    if(!comment.getIsStaff()){//会员
                        User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
                        if(user != null){
                            comment.setAccount(user.getAccount());
                            comment.setNickname(user.getNickname());
                            comment.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                            comment.setAvatarName(user.getAvatarName());
                            userRoleNameMap.put(comment.getUserName(), null);

                            if(user.getCancelAccountTime() != -1L){//账号已注销
                                comment.setUserInfoStatus(-30);
                            }
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

                    if(comment.getQuoteUpdateId() != null && comment.getQuoteUpdateId().length() >1){
                        String[] quoteUpdateId_arr = comment.getQuoteUpdateId().split(",");
                        if(quoteUpdateId_arr.length >0){
                            for(String quoteUpdateId : quoteUpdateId_arr){
                                if(quoteUpdateId != null && !quoteUpdateId.trim().isEmpty()){
                                    Long l = Long.parseLong(quoteUpdateId);
                                    if(!quoteUpdateIdList.contains(l)){
                                        quoteUpdateIdList.add(l);
                                    }
                                }
                            }
                        }
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

            if(userRoleNameMap.size() >0){
                for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                    List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey(),request);
                    entry.setValue(roleNameList);
                }
            }


            if(query_quoteUpdateIdList.size() >0){
                List<Comment> quote_commentList = commentRepository.findByCommentIdList(query_quoteUpdateIdList);
                if(quote_commentList != null && quote_commentList.size() >0){
                    for(Comment comment : quote_commentList){
                        if(comment.getStatus().equals(20)){
                            new_quoteList.put(comment.getId(), textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent())));
                        }
                    }
                }
            }




            if(commentList != null && commentList.size() >0){
                for(Comment comment : commentList){
                    commentIdList.add(comment.getId());
                    if(comment.getQuote() != null && !comment.getQuote().trim().isEmpty()){
                        //旧引用
                        List<Quote> quoteList = jsonComponent.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
                        if(quoteList != null && quoteList.size() >0){
                            for(Quote quote :quoteList){
                                if(new_quoteList.containsKey(quote.getCommentId())){
                                    quote.setContent(new_quoteList.get(quote.getCommentId()));
                                }else{
                                    //如果引用的评论已删除
                                    Comment quoteComment = commentCacheManager.query_cache_findByCommentId(quote.getCommentId());
                                    if(quoteComment == null || !quoteComment.getStatus().equals(20)){
                                        quote.setContent("");
                                    }
                                }
                                quote.setIp(null);//IP地址不显示
                                if(quote.getIsStaff() == false){//会员
                                    User user = userCacheManager.query_cache_findUserByUserName(quote.getUserName());
                                    quote.setAccount(user.getAccount());
                                    quote.setNickname(user.getNickname());
                                    quote.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                    quote.setAvatarName(user.getAvatarName());

                                    if(user.getCancelAccountTime() != -1L){//账号已注销
                                        quote.setUserInfoStatus(-30);
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

                                //非正常状态用户清除显示数据
                                if(quote.getUserInfoStatus() <0){
                                    quote.setUserName(null);
                                    quote.setAccount(null);
                                    quote.setNickname(null);
                                    quote.setAvatarPath(null);
                                    quote.setAvatarName(null);
                                    quote.setUserRoleNameList(new ArrayList<String>());
                                    quote.setContent("");
                                }
                            }
                        }
                        comment.setQuoteList(quoteList);
                    }

                    if(!comment.getIsStaff()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(comment.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && roleNameList.size() >0){
                                    comment.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }

                    //非正常状态用户清除显示数据
                    if(comment.getUserInfoStatus() <0){
                        comment.setUserName(null);
                        comment.setAccount(null);
                        comment.setNickname(null);
                        comment.setAvatarPath(null);
                        comment.setAvatarName(null);
                        comment.setUserRoleNameList(new ArrayList<String>());
                        comment.setContent("");
                        comment.setMarkdownContent("");
                    }
                }
            }

            if(commentIdList.size() >0){
                List<Reply> replyList = commentRepository.findReplyByCommentId(commentIdList,20);
                if(replyList != null && replyList.size() >0){
                    for(Comment comment : commentList){
                        for(Reply reply : replyList){
                            if(comment.getId().equals(reply.getCommentId())){
                                //只有中国地区简体中文语言才显示IP归属地
                                if(accessUser != null && systemSetting.isShowIpAddress() && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
                                    reply.setIpAddress(IpAddress.queryProvinceAddress(reply.getIp()));
                                }
                                reply.setIp(null);//IP地址不显示
                                if(!reply.getIsStaff()){//会员
                                    User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
                                    if(user != null){
                                        reply.setAccount(user.getAccount());
                                        reply.setNickname(user.getNickname());
                                        reply.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                                        reply.setAvatarName(user.getAvatarName());

                                        List<String> roleNameList = userRoleComponent.queryUserRoleName(reply.getUserName(),request);
                                        reply.setUserRoleNameList(roleNameList);

                                        if(user.getCancelAccountTime() != -1L){//账号已注销
                                            reply.setUserInfoStatus(-30);
                                        }
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
                                        if(user != null && user.getState().equals(1) && user.getCancelAccountTime().equals(-1L)){
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

                                //非正常状态用户清除显示数据
                                if(reply.getUserInfoStatus() <0){
                                    reply.setUserName(null);
                                    reply.setAccount(null);
                                    reply.setNickname(null);
                                    reply.setAvatarPath(null);
                                    reply.setAvatarName(null);
                                    reply.setUserRoleNameList(new ArrayList<String>());
                                    reply.setContent(null);
                                }

                                comment.addReply(reply);
                            }
                        }
                        //回复排序
                        commentComponent.replySort(comment.getReplyList());
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
     * 获取添加评论界面信息
     * @return
     */
    public Map<String,Object> getAddCommentViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AccessUser accessUser = AccessUserThreadLocal.get();

        if(accessUser != null){
            boolean captchaKey = captchaComponent.comment_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());//是否有验证码
            }
        }
        //如果全局不允许提交评论
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isAllowComment()){
            returnValue.put("allowComment",true);//允许提交评论
        }else{
            returnValue.put("allowComment",false);//不允许提交评论
        }

        returnValue.put("availableTag", commentComponent.availableTag());//评论编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 添加评论
     * @param topicId 话题Id
     * @param content 评论内容
     * @param isMarkdown 是否使用Markdown
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addComment(Long topicId, String content, Boolean isMarkdown, String markdownContent,
                                         String captchaKey, String captchaValue, HttpServletRequest request){

        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("comment", "只读模式不允许提交数据"));
        }



        if(topicId == null || topicId <=0L){
            throw new BusinessException(Map.of("comment", "话题Id不能为空"));
        }
        Topic topic  = topicCacheManager.queryTopicCache(topicId);
        if(topic == null){
            throw new BusinessException(Map.of("comment", "话题不存在"));
        }

        //验证码
        boolean isCaptcha = captchaComponent.comment_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }
        Comment comment = new Comment();
        LocalDateTime postTime = LocalDateTime.now();




        //是否全局允许评论
        if(!systemSetting.isAllowComment()){
            errors.put("comment", "禁止评论");
        }
        if(!topic.isAllow()){
            errors.put("comment", "禁止评论");
        }
        if(!topic.getStatus().equals(20)){//已发布
            errors.put("comment", "已发布话题才允许评论");
        }

        //前台发表评论审核状态
        if(systemSetting.getComment_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            comment.setStatus(10);//10.待审核
        }else if(systemSetting.getComment_review().equals(30)){
            //是否有当前功能操作权限
            boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1012000,topic.getTagId());
            if(flag_permission){
                comment.setStatus(20);//20.已发布
            }else{
                comment.setStatus(10);//10.待审核
            }
        }else{
            comment.setStatus(20);//20.已发布
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._1007000,topic.getTagId());//如果无权限设置403状态码


        //如果实名用户才允许评论
        if(systemSetting.isRealNameUserAllowComment()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("comment", "实名用户才允许评论");
            }

        }

        if (isMarkdown != null && isMarkdown) {
            if(systemSetting.getSupportEditor().equals(10)){
                errors.put("comment", "不支持此编辑器");
            }
        } else {
            if(systemSetting.getSupportEditor().equals(20)){
                errors.put("comment", "不支持此编辑器");
            }
        }

        // 处理内容过滤和文件路径
        CommentClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, content, isMarkdown, markdownContent);

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
        comment.setPostTime(postTime);
        comment.setTopicId(topicId);
        comment.setContent(value);
        comment.setIsStaff(false);
        comment.setUserName(accessUser.getUserName());
        comment.setIsMarkdown(isMarkdown);
        comment.setMarkdownContent(htmlFilterResult.formatterMarkdown);


        comment.setIp(IpAddress.getClientIpAddress(request));
        //保存评论
        commentRepository.saveComment(comment);
        //添加热门话题
        hotTopicComponent.addHotTopic(topic);

        PointLog pointLog = new PointLog();
        pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
        pointLog.setModule(200);//200.评论
        pointLog.setParameterId(comment.getId());//参数Id
        pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
        pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

        pointLog.setPoint(systemSetting.getComment_rewardPoint());//积分
        pointLog.setUserName(accessUser.getUserName());//用户名称
        pointLog.setRemark("");

        //增加用户积分
        userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getComment_rewardPoint(), pointComponent.createPointLogObject(pointLog));

        //用户动态
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
        userDynamic.setUserName(accessUser.getUserName());
        userDynamic.setModule(200);//模块 200.评论
        userDynamic.setTopicId(topic.getId());
        userDynamic.setCommentId(comment.getId());
        userDynamic.setPostTime(postTime);
        userDynamic.setStatus(comment.getStatus());
        userDynamic.setFunctionIdGroup(","+topic.getId()+","+comment.getId()+",");
        Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
        userRepository.saveUserDynamic(new_userDynamic);


        //删除缓存
        userCacheManager.delete_cache_findUserById(accessUser.getUserId());
        userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());
        topicCacheManager.delete_cache_findWhetherCommentTopic(comment.getTopicId(),accessUser.getUserName());
        topicCacheManager.deleteTopicCache(topicId);

        followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());

        //修改话题最后回复时间
        topicRepository.updateTopicReplyTime(topicId, postTime);


        if(!topic.getIsStaff()){
            User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主评论不发提醒给自己

                //提醒楼主
                Remind remind = new Remind();
                remind.setId(remindConfig.createRemindId(_user.getId()));
                remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                remind.setTypeCode(10);//10:别人评论了我的话题
                remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                remind.setTopicId(topic.getId());//话题Id
                remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id

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
                    remind.setTypeCode(210);// 210:别人在评论提到我
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setTopicId(topic.getId());//话题Id
                    remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id

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
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+ File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }
            }
        }
        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("comment", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }



    /**
     * 获取引用评论界面信息
     * @param commentId 评论Id
     */
    public Map<String,Object> getQuoteCommentViewModel(Long commentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.comment_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
            }
        }
        if(commentId != null && commentId >0L){
            Comment comment = commentRepository.findByCommentId(commentId);
            if(comment != null && comment.getStatus() <100){
                returnValue.put("quoteContent", textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent())));//引用内容
            }
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();


        if(systemSetting.isAllowComment()){
            returnValue.put("allowComment",true);//允许提交评论
        }else{
            returnValue.put("allowComment",false);//不允许提交评论
        }
        returnValue.put("availableTag", commentComponent.availableTag());//评论编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 添加引用评论
     * @param commentId 评论Id
     * @param content 评论内容
     * @param isMarkdown 是否使用Markdown
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addQuoteComment(Long commentId, String content, Boolean isMarkdown, String markdownContent,
                                String captchaKey, String captchaValue,
                                HttpServletRequest request){

        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("comment", "只读模式不允许提交数据"));
        }



        //验证码
        boolean isCaptcha = captchaComponent.comment_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }

        if(commentId == null || commentId <=0) {
            throw new BusinessException(Map.of("comment", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentId);
        if(comment == null){
            throw new BusinessException(Map.of("comment", "引用评论不能为空"));
        }
        Topic topic = topicRepository.findById(comment.getTopicId());
        if(topic == null){
            throw new BusinessException(Map.of("comment", "话题不存在"));
        }
        //是否全局允许评论
        if(!systemSetting.isAllowComment()){
            errors.put("comment", "禁止评论");
        }
        if(!topic.isAllow()){
            errors.put("comment", "禁止评论");
        }
        if(!topic.getStatus().equals(20)){//已发布
            errors.put("comment", "已发布话题才允许评论");
        }
        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._1007000,topic.getTagId());//如果无权限设置403状态码
        //如果实名用户才允许评论
        if(systemSetting.isRealNameUserAllowComment()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("comment", "实名用户才允许评论");
            }
        }

        Comment newComment = new Comment();
        LocalDateTime postTime =  LocalDateTime.now();

        //前台发表评论审核状态
        if(systemSetting.getComment_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            newComment.setStatus(10);//10.待审核
        }else if(systemSetting.getComment_review().equals(30)){
            //是否有当前功能操作权限
            boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1012000,topic.getTagId());
            if(flag_permission){
                newComment.setStatus(20);//20.已发布
            }else{
                newComment.setStatus(10);//10.待审核
            }
        }else{
            newComment.setStatus(20);//20.已发布
        }

        if (isMarkdown != null && isMarkdown) {
            if(systemSetting.getSupportEditor().equals(10)){
                errors.put("comment", "不支持此编辑器");
            }
        } else {
            if(systemSetting.getSupportEditor().equals(20)){
                errors.put("comment", "不支持此编辑器");
            }
        }

        // 处理内容过滤和文件路径
        CommentClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, content, isMarkdown, markdownContent);

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



        //旧引用
        List<Quote> old_customQuoteList = new ArrayList<Quote>();
        //旧引用Id组
        String old_customQuoteId = ","+comment.getId()+comment.getQuoteIdGroup();
        if(comment.getQuote() != null && !comment.getQuote().trim().isEmpty()){
            //旧引用
            old_customQuoteList = jsonComponent.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
        }


        //自定义引用
        Quote customQuote = new Quote();
        customQuote.setCommentId(comment.getId());
        customQuote.setIsStaff(comment.getIsStaff());
        customQuote.setUserName(comment.getUserName());
        customQuote.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent())));
        old_customQuoteList.add(customQuote);

        //评论
        newComment.setPostTime(postTime);
        newComment.setTopicId(comment.getTopicId());
        newComment.setIp(IpAddress.getClientIpAddress(request));
        if(systemSetting.isAllowFilterWord()){
            String wordReplace = "";
            if(systemSetting.getFilterWordReplace() != null){
                wordReplace = systemSetting.getFilterWordReplace();
            }
            value = sensitiveWordFilterComponent.filterSensitiveWord(value, wordReplace);
        }
        newComment.setContent(value);
        newComment.setIsStaff(false);
        newComment.setQuoteIdGroup(old_customQuoteId);
        newComment.setUserName(accessUser.getUserName());
        newComment.setQuote(jsonComponent.toJSONString(old_customQuoteList));
        newComment.setIsMarkdown(isMarkdown);
        newComment.setMarkdownContent(htmlFilterResult.formatterMarkdown);;

        //保存评论
        commentRepository.saveComment(newComment);
        //添加热门话题
        hotTopicComponent.addHotTopic(topic);


        PointLog pointLog = new PointLog();
        pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
        pointLog.setModule(200);//200.评论
        pointLog.setParameterId(comment.getId());//参数Id
        pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
        pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

        pointLog.setPoint(systemSetting.getComment_rewardPoint());//积分
        pointLog.setUserName(accessUser.getUserName());//用户名称
        pointLog.setRemark("");

        //增加用户积分
        userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getComment_rewardPoint(), pointComponent.createPointLogObject(pointLog));


        //用户动态
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
        userDynamic.setUserName(accessUser.getUserName());
        userDynamic.setModule(300);//模块 300.引用评论
        userDynamic.setTopicId(topic.getId());
        userDynamic.setCommentId(newComment.getId());
        userDynamic.setQuoteCommentId(comment.getId());
        userDynamic.setPostTime(newComment.getPostTime());
        userDynamic.setStatus(newComment.getStatus());
        userDynamic.setFunctionIdGroup(","+topic.getId()+","+newComment.getId()+",");
        Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
        userRepository.saveUserDynamic(new_userDynamic);



        //删除缓存
        userCacheManager.delete_cache_findUserById(accessUser.getUserId());
        userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());

        followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());

        //修改话题最后回复时间
        topicRepository.updateTopicReplyTime(newComment.getTopicId(),postTime);


        User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
        //别人评论了我的话题
        if(!topic.getIsStaff() && _user != null && !_user.getId().equals(accessUser.getUserId())){//楼主评论不发提醒给自己
            //如果别人引用话题发布者的评论,则不发本类型提醒给话题发布者
            if(!comment.getUserName().equals(topic.getUserName())){
                //提醒楼主
                Remind remind = new Remind();
                remind.setId(remindConfig.createRemindId(_user.getId()));
                remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                remind.setTypeCode(10);//10:别人评论了我的话题
                remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                remind.setTopicId(comment.getTopicId());//话题Id
                remind.setFriendTopicCommentId(newComment.getId());//对方的话题评论Id

                Object remind_object = remindComponent.createRemindObject(remind);
                remindRepository.saveRemind(remind_object);

                //删除提醒缓存
                remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());

            }
        }


        _user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
        //别人引用了我的评论
        if(!comment.getIsStaff() && _user != null && !_user.getId().equals(accessUser.getUserId())){//引用自已的评论不发提醒给自己

            //提醒楼主
            Remind remind = new Remind();
            remind.setId(remindConfig.createRemindId(_user.getId()));
            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
            remind.setTypeCode(30);//30:别人引用了我的评论
            remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化
            remind.setTopicId(comment.getTopicId());//话题Id
            remind.setTopicCommentId(comment.getId());//我的话题评论Id
            remind.setFriendTopicCommentId(newComment.getId());//对方的话题评论Id

            Object remind_object = remindComponent.createRemindObject(remind);
            remindRepository.saveRemind(remind_object);

            //删除提醒缓存
            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
        }

        if(htmlFilterResult.htmlProcessingResult.getMentionUserNameList() != null && !htmlFilterResult.htmlProcessingResult.getMentionUserNameList().isEmpty()){
            int count = 0;
            for(String mentionUserName : htmlFilterResult.htmlProcessingResult.getMentionUserNameList()){
                count++;
                if(systemSetting.getAllowMentionMaxNumber() != null && count > systemSetting.getAllowMentionMaxNumber()){
                    break;
                }
                User _user2 = userCacheManager.query_cache_findUserByUserName(mentionUserName);
                if(_user2 != null && !mentionUserName.equals(accessUser.getUserName())){//不发提醒给自己
                    //提醒用户
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user2.getId()));
                    remind.setReceiverUserId(_user2.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(210);// 210:别人在评论提到我
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setTopicId(comment.getTopicId());//话题Id
                    remind.setFriendTopicCommentId(newComment.getId());//对方的话题评论Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user2.getId());


                }
            }
        }

        //异步执行会员卡赠送任务(长期任务类型)
        membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(accessUser.getUserName());

        String fileNumber = "b"+ accessUser.getUserId();

        if(htmlFilterResult.htmlProcessingResult().getImageNameList() != null && !htmlFilterResult.htmlProcessingResult().getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult().getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }

        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("comment", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }

    /**
     * 获取修改评论界面信息
     * @param commentId 评论Id
     */
    public Map<String,Object> getEditCommentViewModel(Long commentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.comment_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
            }
        }
        if(accessUser != null && commentId != null && commentId >0L){
            Comment comment = commentCacheManager.query_cache_findByCommentId(commentId);//查询缓存
            if(comment != null && comment.getStatus() <100 && comment.getUserName().equals(accessUser.getUserName())){
                comment.setIp(null);//IP地址不显示


                if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
                    //处理富文本路径
                    comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
                }

                returnValue.put("comment",comment);
            }
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //如果全局不允许提交评论
        if(!systemSetting.isAllowComment()){
            returnValue.put("allowComment",false);//不允许提交评论
        }else{
            returnValue.put("allowComment",true);//允许提交评论
        }
        returnValue.put("availableTag", commentComponent.availableTag());//评论编辑器允许使用标签
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 修改评论
     * @param commentId 评论Id
     * @param content 内容
     * @param markdownContent Markdown内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void editComment(Long commentId,String content,String markdownContent,
                            String captchaKey,String captchaValue, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("comment", "只读模式不允许提交数据"));
        }



        //验证码
        boolean isCaptcha = captchaComponent.comment_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }

        if(commentId == null || commentId <=0) {
            throw new BusinessException(Map.of("comment", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentId);
        if(comment == null){
            throw new BusinessException(Map.of("comment", "引用评论不能为空"));
        }
        Topic topic = topicRepository.findById(comment.getTopicId());
        if(topic == null){
            throw new BusinessException(Map.of("comment", "话题不存在"));
        }

        if(!comment.getUserName().equals(accessUser.getUserName())){
            errors.put("comment", "只允许修改自己发布的评论");
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._1008000,topic.getTagId());//如果无权限设置403状态码
        //如果全局不允许提交评论
        if(!systemSetting.isAllowComment()){
            errors.put("comment", "禁止评论");
        }

        //如果实名用户才允许提交评论
        if(systemSetting.isRealNameUserAllowComment()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("comment", "实名用户才允许评论");
            }

        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        //旧状态
        Integer old_status = comment.getStatus();
        //旧内容
        String old_content = comment.getContent();

        if(comment.getStatus().equals(20)){//如果已发布，则重新执行发贴审核逻辑
            //前台发表评论审核状态
            if(systemSetting.getComment_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
                comment.setStatus(10);//10.待审核
            }else if(systemSetting.getComment_review().equals(30)){
                if(topic.getTagId() != null && topic.getTagId() >0L){
                    //是否有当前功能操作权限
                    boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1008000,topic.getTagId());
                    if(_flag_permission){
                        comment.setStatus(20);//20.已发布
                    }else{
                        comment.setStatus(10);//10.待审核
                    }
                }
            }else{
                comment.setStatus(20);//20.已发布
            }
        }

        // 处理内容过滤和文件路径
        CommentClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, content, comment.getIsMarkdown(), markdownContent);

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

        comment.setContent(value);
        comment.setMarkdownContent(htmlFilterResult.formatterMarkdown);

        LocalDateTime time = LocalDateTime.now();
        int i = commentRepository.updateComment(comment.getId(),comment.getContent(),comment.getMarkdownContent(),comment.getStatus(),time,comment.getUserName());

        if(i ==0){
            throw new BusinessException(Map.of("comment", "修改评论失败")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        if(comment.getStatus() < 100 && !old_status.equals(comment.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
            if(user != null){
                //修改评论状态
                userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),comment.getStatus());
            }

        }

        //删除缓存
        commentCacheManager.delete_cache_findByCommentId(comment.getId());



        //上传文件编号
        String fileNumber = topicComponent.generateFileNumber(comment.getUserName(), comment.getIsStaff());

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }


        //旧图片名称
        List<String> old_ImageName = textFilterComponent.readImageName(old_content,"comment");
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
            if(old_ImageName.size() >0){
                for(String imagePath : old_ImageName){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
                        continue;
                    }
                    //替换路径中的..号
                    imagePath = FileUtil.toRelativePath(imagePath);
                    //替换路径分割符
                    imagePath = Strings.CS.replace(imagePath, "/", File.separator);

                    Boolean state = fileComponent.deleteFile(imagePath);
                    if(state != null && !state){
                        //替换指定的字符，只替换第一次出现的
                        imagePath = Strings.CS.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
                        imagePath = Strings.CS.replace(imagePath, File.separator, "_");//替换所有出现过的字符

                        //创建删除失败文件
                        fileComponent.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+imagePath);

                    }
                }

            }
        }



        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("comment", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }

    /**
     * 删除评论
     * @param commentId 评论Id
     * @return
     */
    public Map<String,Object> deleteComment(Long commentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("comment", "只读模式不允许提交数据"));
        }

        Comment comment = null;
        Topic topic = null;


        if(commentId != null && commentId >0L){
            comment = commentRepository.findByCommentId(commentId);
            if(comment != null){
                topic = topicRepository.findById(comment.getTopicId());
                if(topic != null){
                    if(!comment.getUserName().equals(accessUser.getUserName())){
                        errors.put("comment", "只允许删除自己发布的评论");
                    }

                    if(comment.getStatus() > 100){
                        errors.put("comment", "评论已经删除");
                    }

                    //是否有当前功能操作权限
                    userRoleComponent.checkPermission(ResourceEnum._1009000,topic.getTagId());//如果无权限设置403状态码


                }else{
                    errors.put("comment", "话题不存在");
                }

            }else{
                errors.put("comment", "评论不存在");
            }

        }else{
            errors.put("comment", "评论Id不能为空");
        }



        if(errors.size() == 0){
            Integer constant = 100;
            int i = commentRepository.markDeleteComment(comment.getId(),constant);

            if(i >0 && comment.getStatus() < 100){
                User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
                if(user != null){
                    //修改评论状态
                    userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),comment.getStatus()+constant);
                }

            }

            if(i >0){
                //删除缓存
                commentCacheManager.delete_cache_findByCommentId(comment.getId());


            }else{
                errors.put("comment", "删除评论失败");
            }

        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);

        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }

    /**
     * 评论  图片上传
     * @param topicId 话题Id
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String, Object> uploadImage(Long topicId, String fileName,
                              MultipartFile file, String fileServerAddress){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        if(systemSetting.getCloseSite().equals(2)){
            returnValue.put("error", 1);
            returnValue.put("message", "只读模式不允许提交数据");
            return returnValue;
        }
        //如果全局不允许提交评论
        if(!systemSetting.isAllowComment()){
            returnValue.put("error", 1);
            returnValue.put("message", "不允许提交评论");
            return returnValue;
        }


        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        //上传文件编号
        String number = "b"+accessUser.getUserId();

        //如果实名用户才允许提交话题
        if(systemSetting.isRealNameUserAllowComment()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                returnValue.put("error", 1);
                returnValue.put("message","实名用户才允许评论");
                return returnValue;
            }
        }


        if (number.trim().isEmpty() || topicId == null || topicId<0L) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }

        CommentClientServiceImpl.FileUploadConfig config = validateUpload("image", accessUser.getUserName());
        if (!config.success()) {
            returnValue.put("error", 1);
            returnValue.put("message", config.message());
            return returnValue;
        }





        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload("image",fileName, topicId, number, returnValue);
        } else { // 本地系统
            return handleLocalUpload("image",file, topicId, number, fileServerAddress, returnValue);
        }
    }


    /**
     * 获取添加评论回复界面信息
     * @return
     */
    public Map<String,Object> getAddCommentReplyViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.comment_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//验证码key
            }
        }
        //如果全局不允许提交回复
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isAllowComment()){
            returnValue.put("allowReply",true);//允许提交回复
            returnValue.put("availableTag",  commentComponent.replyAvailableTag());//回复编辑器允许使用标签
            returnValue.put("fileSystem", fileComponent.getFileSystem());
        }else{
            returnValue.put("allowReply",false);//不允许提交回复
        }
        return returnValue;
    }

    /**
     * 添加评论回复
     * @param commentId 评论Id
     * @param friendReplyId 对方回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void addReply(Long commentId, Long friendReplyId, String content,
                                        String captchaKey, String captchaValue,
                                        HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("reply", "只读模式不允许提交数据"));
        }

        //验证码
        boolean isCaptcha = captchaComponent.comment_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }
        if(commentId == null || commentId <=0) {
            throw new BusinessException(Map.of("reply", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentId);
        if(comment == null){
            throw new BusinessException(Map.of("reply", "引用评论不能为空"));
        }

        Topic topic = topicRepository.findById(comment.getTopicId());
        if(topic == null){
            throw new BusinessException(Map.of("reply", "话题不存在"));
        }
        //是否全局允许评论
        if(!systemSetting.isAllowComment()){
            errors.put("reply", "禁止评论");
        }
        if(!topic.isAllow()){
            errors.put("reply", "禁止评论");
        }
        if(!topic.getStatus().equals(20)){//已发布
            errors.put("reply", "已发布话题才允许回复");
        }
        Reply friendReply = null;
        if(friendReplyId != null && friendReplyId >0){
            friendReply = commentRepository.findReplyByReplyId(friendReplyId);
            if(friendReply != null){
                if(!friendReply.getCommentId().equals(commentId)){
                    errors.put("friendReplyId", "对方回复Id和评论Id不对应");
                }
            }else{
                errors.put("friendReplyId", "对方回复Id不存在");
            }
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._1013000,topic.getTagId());//如果无权限设置403状态码

        //如果实名用户才允许评论
        if(systemSetting.isRealNameUserAllowComment()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("reply", "实名用户才允许评论");
            }

        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        //回复
        Reply reply = new Reply();
        LocalDateTime postTime =  LocalDateTime.now();


        //前台发表评论审核状态
        if(systemSetting.getReply_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            reply.setStatus(10);//10.待审核
        }else if(systemSetting.getReply_review().equals(30)){
            //是否有当前功能操作权限
            boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1016000,topic.getTagId());
            if(flag_permission){
                reply.setStatus(20);//20.已发布
            }else{
                reply.setStatus(10);//10.待审核
            }
        }else{
            reply.setStatus(20);//20.已发布
        }



        content = textFilterComponent.filterReplyTag(request,content.trim(),settingComponent.readEditorTag());

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
        reply.setPostTime(postTime);
        reply.setCommentId(comment.getId());
        reply.setIsStaff(false);
        reply.setUserName(accessUser.getUserName());
        reply.setIp(IpAddress.getClientIpAddress(request));
        reply.setContent(replyContent);
        reply.setTopicId(comment.getTopicId());
        if(friendReply != null){
            reply.setFriendReplyId(friendReplyId);
            reply.setFriendReplyIdGroup(friendReply.getFriendReplyIdGroup()+friendReplyId+",");
            reply.setIsFriendStaff(friendReply.getIsStaff());
            reply.setFriendUserName(friendReply.getUserName());
        }
        //保存回复
        commentRepository.saveReply(reply);

        PointLog pointLog = new PointLog();
        pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
        pointLog.setModule(300);//300.回复
        pointLog.setParameterId(comment.getId());//参数Id
        pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
        pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

        pointLog.setPoint(systemSetting.getReply_rewardPoint());//积分
        pointLog.setUserName(accessUser.getUserName());//用户名称
        pointLog.setRemark("");

        //增加用户积分
        userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getReply_rewardPoint(), pointComponent.createPointLogObject(pointLog));


        //用户动态
        UserDynamic userDynamic = new UserDynamic();
        userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
        userDynamic.setUserName(accessUser.getUserName());
        userDynamic.setModule(400);//模块 400.回复
        userDynamic.setTopicId(reply.getTopicId());
        userDynamic.setCommentId(reply.getCommentId());
        userDynamic.setReplyId(reply.getId());
        userDynamic.setPostTime(reply.getPostTime());
        userDynamic.setStatus(reply.getStatus());
        userDynamic.setFunctionIdGroup(","+reply.getTopicId()+","+reply.getCommentId()+","+reply.getId()+",");
        Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
        userRepository.saveUserDynamic(new_userDynamic);



        //删除缓存
        userCacheManager.delete_cache_findUserById(accessUser.getUserId());
        userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());
        followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());

        //修改话题最后回复时间
        topicRepository.updateTopicReplyTime(comment.getTopicId(),postTime);

        if(!topic.getIsStaff()){
            User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            //别人回复了我的话题
            if(_user != null && !_user.getId().equals(accessUser.getUserId())){//楼主回复不发提醒给自己
                //如果别人回复了话题发布者的评论，则不发本类型提醒给话题发布者；如果回复了话题发布者的回复，则不发本类型提醒给话题发布者
                if(!comment.getUserName().equals(topic.getUserName()) && !topic.getUserName().equals(reply.getFriendUserName())){
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(20);//20:别人回复了我的话题
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化
                    remind.setTopicId(comment.getTopicId());//话题Id
                    remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id
                    remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());

                }
            }
        }

        User _user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
        //别人回复了我的评论
        if(!comment.getIsStaff() && _user != null
                && !_user.getId().equals(accessUser.getUserId())//不发提醒给自己
                && !comment.getUserName().equals(reply.getFriendUserName())//如果回复了评论发布者的回复，则不发本类型提醒给评论发布者
        ){

            Remind remind = new Remind();
            remind.setId(remindConfig.createRemindId(_user.getId()));
            remind.setReceiverUserId(_user.getId());//接收提醒用户Id
            remind.setSenderUserId(accessUser.getUserId());//发送用户Id
            remind.setTypeCode(40);//40:别人回复了我的评论
            remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化
            remind.setTopicId(comment.getTopicId());//话题Id
            remind.setTopicCommentId(comment.getId());//我的话题评论Id
            remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id


            Object remind_object = remindComponent.createRemindObject(remind);
            remindRepository.saveRemind(remind_object);

            //删除提醒缓存
            remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
        }

        Set<String> userNameList = new HashSet<String>();
        List<Reply> replyList = commentRepository.findReplyByCommentId(comment.getId());
        if(replyList != null && replyList.size() >0){
            for(Reply _reply : replyList){
                //如果是话题发布者的回复，则不发本类型提醒给话题的发布者
                if(!topic.getIsStaff() && _reply.getUserName().equals(topic.getUserName())){
                    continue;
                }
                //如果是评论发布者的回复，则不发本类型提醒给评论的发布者
                if(!comment.getIsStaff() && _reply.getUserName().equals(comment.getUserName())){
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

                //如果是被回复用户，则不发本类型提醒。由后面的代码发（55:别人回复了我的评论回复）类型提醒
                if(_reply.getUserName().equals(reply.getFriendUserName())){
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
                    remind.setTypeCode(50);//50:别人回复了我回复过的评论
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化
                    remind.setTopicId(_reply.getTopicId());//话题Id
                    remind.setTopicReplyId(_reply.getId());//我的话题回复Id


                    remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id
                    remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id


                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());
                }
            }
        }


        if(friendReply != null && !friendReply.getIsStaff()){
            _user = userCacheManager.query_cache_findUserByUserName(friendReply.getUserName());
            if(_user != null && !_user.getId().equals(accessUser.getUserId())){//不发提醒给自己
                //提醒
                Remind remind = new Remind();
                remind.setId(remindConfig.createRemindId(_user.getId()));
                remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                remind.setTypeCode(55);//55:别人回复了我的评论回复
                remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化
                remind.setTopicId(friendReply.getTopicId());//话题Id
                remind.setTopicReplyId(friendReply.getId());//我的话题回复Id


                remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id
                remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id


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
                    remind.setTypeCode(220);// 220:别人在评论回复提到我
                    remind.setSendTimeFormat(postTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() );//发送时间格式化
                    remind.setTopicId(topic.getId());//话题Id
                    remind.setFriendTopicCommentId(comment.getId());//对方的话题评论Id
                    remind.setFriendTopicReplyId(reply.getId());//对方的话题回复Id
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
        Integer original = settingCacheManager.getSubmitQuantity("comment", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }


    }

    /**
     * 修改评论回复界面信息
     * @param replyId 回复Id
     */
    public Map<String,Object> getEditCommentReplyViewModel(Long replyId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.comment_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey",UUIDUtil.getUUID32());//验证码key
            }
        }
        if(accessUser != null && replyId != null && replyId >0L){
            Reply reply = commentCacheManager.query_cache_findReplyByReplyId(replyId);//查询缓存
            if(reply != null && reply.getStatus() <100 && reply.getUserName().equals(accessUser.getUserName())){
                reply.setIp(null);//IP地址不显示
                returnValue.put("reply",reply);
            }
        }
        //如果全局不允许提交回复
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isAllowComment()){
            returnValue.put("allowReply",true);//允许提交回复
            returnValue.put("availableTag", commentComponent.replyAvailableTag());//回复编辑器允许使用标签
            returnValue.put("fileSystem", fileComponent.getFileSystem());
        }else{
            returnValue.put("allowReply",false);//不允许提交回复
        }
        return returnValue;
    }

    /**
     * 修改评论回复
     * @param replyId 回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     */
    public void editReply(Long replyId,String content,String captchaKey,String captchaValue,
                          HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("reply", "只读模式不允许提交数据"));
        }



        //验证码
        boolean isCaptcha = captchaComponent.comment_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);
        }
        if(replyId == null || replyId <=0) {
            throw new BusinessException(Map.of("reply", "回复Id不能为空"));
        }
        Reply reply = commentRepository.findReplyByReplyId(replyId);
        if(reply == null){
            throw new BusinessException(Map.of("reply", "回复不存在"));
        }
        Topic topic = topicRepository.findById(reply.getTopicId());
        if(topic == null){
            throw new BusinessException(Map.of("reply","话题不存在"));
        }
        //旧状态
        Integer old_status = reply.getStatus();
        //旧内容
        String old_content = reply.getContent();

        if(!reply.getUserName().equals(accessUser.getUserName())){
            errors.put("reply", "只允许修改自己发布的回复");
        }

        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._1014000,topic.getTagId());//如果无权限设置403状态码
        //如果全局不允许提交评论
        if(!systemSetting.isAllowComment()){
            errors.put("reply", "禁止回复");
        }

        //如果实名用户才允许提交评论
        if(systemSetting.isRealNameUserAllowComment()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                errors.put("reply", "实名用户才允许提交回复");
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors)
                    .addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }
        if(reply.getStatus().equals(20)){//如果已发布，则重新执行发贴审核逻辑
            //前台发表评论审核状态
            if(systemSetting.getReply_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
                reply.setStatus(10);//10.待审核
            }else if(systemSetting.getReply_review().equals(30)){
                if(topic.getTagId() != null && topic.getTagId() >0L){
                    //是否有当前功能操作权限
                    boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1016000,topic.getTagId());
                    if(_flag_permission){
                        reply.setStatus(20);//20.已发布
                    }else{
                        reply.setStatus(10);//10.待审核
                    }
                }
            }else{
                reply.setStatus(20);//20.已发布
            }
        }

        content = textFilterComponent.filterReplyTag(request,content.trim(),settingComponent.readEditorTag());

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
        reply.setContent(replyContent);


        LocalDateTime time =  LocalDateTime.now();
        //修改回复
        int i = commentRepository.updateReply(reply.getId(),reply.getContent(),reply.getUserName(),reply.getStatus(),time);

        if(i ==0){
            throw new BusinessException(Map.of("reply", "修改回复失败")).addDataIf(isCaptcha, "captchaKey", UUIDUtil.getUUID32());
        }

        if(i >0 && !old_status.equals(reply.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
            if(user != null){
                //修改回复状态
                userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus());
            }

        }

        //删除缓存
        commentCacheManager.delete_cache_findReplyByReplyId(reply.getId());



        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("comment", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("comment", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }
    }

    /**
     * 删除评论回复
     * @param replyId 评论回复Id
     * @return
     */
    public Map<String,Object> deleteReply(Long replyId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("reply", "只读模式不允许提交数据"));
        }

        Topic topic = null;
        Reply reply = null;

        if(replyId != null && replyId >0L){
            reply = commentRepository.findReplyByReplyId(replyId);
            if(reply != null){
                topic = topicRepository.findById(reply.getTopicId());
                if(topic != null){
                    if(!reply.getUserName().equals(accessUser.getUserName())){
                        errors.put("reply", "只允许删除自己发布的回复");
                    }

                    if(reply.getStatus() > 100){
                        errors.put("reply", "回复已经删除");
                    }

                    //是否有当前功能操作权限
                    userRoleComponent.checkPermission(ResourceEnum._1015000,topic.getTagId());//如果无权限设置403状态码


                }else{
                    errors.put("reply", "话题不存在");
                }

            }else{
                errors.put("reply", "回复不存在");
            }

        }else{
            errors.put("reply", "回复Id不能为空");
        }


        if(errors.size() == 0){
            Integer constant = 100;
            int i = commentRepository.markDeleteReply(reply.getId(),constant);


            if(i >0){
                User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
                if(user != null){
                    //修改回复状态
                    userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus()+constant);
                }

            }

            if(i >0){
                //删除缓存
                commentCacheManager.delete_cache_findReplyByReplyId(reply.getId());
            }else{
                errors.put("reply", "删除回复失败");
            }

        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);

        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }


    /**
     * 获取我的评论
     * @param page 页码
     * @return
     */
    public PageView<Comment> getCommentList(int page){
        //调用分页算法代码
        PageView<Comment> pageView = new PageView<Comment>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
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


        QueryResult<Comment> qr = commentRepository.getScrollData(Comment.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            for(Comment o :qr.getResultlist()){
                o.setIp(null);//IP地址不显示

                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!topicIdList.contains(o.getTopicId())){
                    topicIdList.add(o.getTopicId());
                }
            }
            List<Topic> topicList = topicRepository.findTitleByIdList(topicIdList);
            if(topicList != null && topicList.size() >0){
                for(Comment o :qr.getResultlist()){
                    for(Topic topic : topicList){
                        if(topic.getId().equals(o.getTopicId())){
                            o.setTopicTitle(topic.getTitle());
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
     * 获取我的评论回复
     * @param page 页码
     * @return
     */
    public PageView<Reply> getReplyList(int page){
        //调用分页算法代码
        PageView<Reply> pageView = new PageView<Reply>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
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


        QueryResult<Reply> qr = topicRepository.getScrollData(Reply.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<Long> topicIdList = new ArrayList<Long>();
            for(Reply o :qr.getResultlist()){
                o.setIp(null);//IP地址不显示
                o.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(o.getContent())));
                if(!topicIdList.contains(o.getTopicId())){
                    topicIdList.add(o.getTopicId());
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
    private CommentClientServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content,settingComponent.readEditorTag());
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"comment",settingComponent.readEditorTag());
        return new CommentClientServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);

    }


    /**
     * 校验上传参数
     * @param dir 上传类型，分别为image、media、file
     * @param userName 用户名称
     * @return
     */
    private CommentClientServiceImpl.FileUploadConfig validateUpload(String dir, String userName) {
        EditorTag editor = settingComponent.readEditorTag();
        if (editor == null) return CommentClientServiceImpl.FileUploadConfig.fail("读取配置失败");

        // 根据 dir 定义不同的规则
        return switch (dir) {
            case "image" -> {
                if (!editor.isImage()) yield CommentClientServiceImpl.FileUploadConfig.fail("当前图片类型不允许上传");
                if (!userRoleComponent.isPermission(ResourceEnum._2002000, null))
                    yield CommentClientServiceImpl.FileUploadConfig.fail("权限不足");
                yield CommentClientServiceImpl.FileUploadConfig.ok(editor.getImageFormat(), editor.getImageSize());
            }

            default -> CommentClientServiceImpl.FileUploadConfig.fail("缺少dir参数");
        };
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param topicId 话题Id
     * @param number 上传文件编号
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, Long topicId, String number, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }
        CommentClientServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
        if (allowFormat.formatList == null || allowFormat.formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),allowFormat.formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,topicId +"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/comment/"+topicId+"/"+newFileName,allowFormat.fileSize);//创建预签名


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
    private CommentClientServiceImpl.AllowFormat getAllowFormats(String dir,Map<String, Object> result) {
        List<String> formatList = new ArrayList<>();
        long fileSize = 0;
        switch (dir) {
            case "image":
                EditorTag editorSiteObject = settingComponent.readEditorTag();
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
        return new CommentClientServiceImpl.AllowFormat(formatList,fileSize);
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param topicId 话题Id
     * @param number  上传文件编号
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, Long topicId, String number, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        CommentClientServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
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
        String pathDir = "file"+File.separator+"comment"+File.separator + topicId +File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,topicId +"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/comment/"+topicId+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }
}
