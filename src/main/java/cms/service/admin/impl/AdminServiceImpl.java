package cms.service.admin.impl;

import cms.component.CaptchaCacheManager;
import cms.component.admin.AdminCacheManager;
import cms.component.fileSystem.FileComponent;
import cms.component.setting.SettingCacheManager;
import cms.component.staff.StaffCacheManager;
import cms.component.staff.StaffLoginLogComponent;
import cms.component.staff.StaffLoginLogShardingConfig;
import cms.config.BusinessException;
import cms.dto.QueryResult;
import cms.dto.admin.AdminAccessToken;
import cms.dto.admin.AdminAuthorization;
import cms.dto.admin.AdminRefreshToken;
import cms.dto.admin.SysUserDTO;
import cms.model.staff.StaffLoginLog;
import cms.model.staff.SysUsers;
import cms.repository.feedback.FeedbackRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.report.ReportRepository;
import cms.repository.staff.StaffRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import cms.service.admin.AdminService;
import cms.utils.IpAddress;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员服务
 */
@Service
public class AdminServiceImpl  implements AdminService {
    @Resource StaffRepository staffRepository;
    @Resource FileComponent fileComponent;
    @Resource StaffCacheManager staffCacheManager;
    @Resource TopicRepository topicRepository;
    @Resource CommentRepository commentRepository;
    @Resource QuestionRepository questionRepository;
    @Resource AnswerRepository answerRepository;
    @Resource FeedbackRepository feedbackRepository;
    @Resource ReportRepository reportRepository;
    @Resource AdminCacheManager adminCacheManager;
    @Resource Environment environment;
    @Resource SettingCacheManager settingCacheManager;
    @Resource CaptchaCacheManager captchaCacheManager;
    @Resource StaffLoginLogShardingConfig staffLoginLogShardingConfig;
    @Resource AuthenticationManager authenticationManager;
    @Resource StaffLoginLogComponent staffLoginLogComponent;


    /**
     * 登录界面信息
     * @param username 员工账号
     * @return
     */
    public Map<String, Object> getLoginViewModel(String username) {
        Map<String, Object> returnValue = new HashMap<>();

        //是否需要验证码  true:要  false:不要
        boolean isCaptcha = false;

        //登录失败超过N次，出现验证码 0为每次都出现验证码
        int failedLoginCount = environment.getProperty("bbs.admin.failedLoginCount", Integer.class, 0);
        if(failedLoginCount <=0){//每分钟连续登录密码错误N次时出现验证码
            isCaptcha = true;
        }else{
            if(username != null && !username.trim().isEmpty()){
                Integer errorCount = staffCacheManager.getLoginFailureCount(username);//查询错误次数
                if(errorCount != null && errorCount >= failedLoginCount){
                    isCaptcha = true;
                }
            }

        }
        if(isCaptcha){
            returnValue.put("isCaptcha", true);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("isCaptcha", false);
        }
        return returnValue;
    }

    /**
     * 登录
     * @param username 员工账号
     * @param password 密码
     * @param captchaKey 验证码key
     * @param captchaValue 验证码值
     * @param ip IP地址
     * @return
     */
    public AdminAuthorization login(String username, String password, String captchaKey, String captchaValue, String ip) {
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "用户名不能为空");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "密码不能为空");
        }else{
            if(password.trim().length() != 64){
                errors.put("password", "密码不是SHA256格式");
            }
        }

        //是否需要验证码  true:要  false:不要
        boolean isCaptcha = false;
        //登录失败超过N次，出现验证码 0为每次都出现验证码
        int failedLoginCount = environment.getProperty("bbs.admin.failedLoginCount", Integer.class, 0);
        if(failedLoginCount <=0){//每分钟连续登录密码错误N次时出现验证码
            isCaptcha = true;
        }else{

            if(username != null && !username.trim().isEmpty()){
                Integer errorCount = staffCacheManager.getLoginFailureCount(username.trim());//先查询错误次数

                if(errorCount != null && errorCount >= failedLoginCount){
                    isCaptcha = true;
                }
            }

        }

        if(isCaptcha){
            if (captchaKey == null || captchaKey.trim().isEmpty()) {
                errors.put("captchaKey","验证码编号不能为空");
            }
            if (captchaValue == null || captchaValue.trim().isEmpty()) {
                errors.put("captchaValue","验证码值不能为空");
            }

            if(captchaKey != null && !captchaKey.trim().isEmpty() && captchaValue != null && !captchaValue.trim().isEmpty()){
                //增加验证码重试次数
                Integer original = settingCacheManager.getSubmitQuantity("captcha", captchaKey.trim());
                if(original != null){
                    settingCacheManager.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
                }else{
                    settingCacheManager.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
                }

                String _captcha = captchaCacheManager.getCaptcha(captchaKey.trim());
                if(_captcha != null && !_captcha.trim().isEmpty()){
                    if(!_captcha.equalsIgnoreCase(captchaValue.trim())){
                        errors.put("captchaValue","验证码错误");
                    }
                }else{
                    errors.put("captchaValue","验证码过期");
                }
                //删除验证码
                captchaCacheManager.deleteCaptcha(captchaKey);
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }


        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        try {
            // 通过AuthenticationManager触发认证流程
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 认证成功
            //SecurityContextHolder.getContext().setAuthentication(authentication);// 将认证信息存入 SecurityContext


            //清除登录次数统计
            staffCacheManager.deleteLoginFailureCount(username);

            SysUsers sysUsers = (SysUsers) authentication.getPrincipal();
            if(sysUsers != null){
                //写入登录日志
                StaffLoginLog staffLoginLog = new StaffLoginLog();
                staffLoginLog.setId(staffLoginLogShardingConfig.createStaffLoginLogId(sysUsers.getUserId()));
                staffLoginLog.setIp(ip);
                staffLoginLog.setStaffId(sysUsers.getUserId());
                staffLoginLog.setLogonTime(LocalDateTime.now());
                Object new_staffLoginLog = staffLoginLogComponent.createStaffLoginLogObject(staffLoginLog);

                staffRepository.saveStaffLoginLog(new_staffLoginLog);
            }
            //删除缓存员工安全摘要
            staffCacheManager.delete_staffSecurityDigest(username);

            String refresh_token = UUIDUtil.getUUID32();
            String access_token = UUIDUtil.getUUID32();

            adminCacheManager.addRefreshToken(refresh_token,new AdminRefreshToken(access_token,sysUsers.getUserId(),username,sysUsers.getSecurityDigest()));
            adminCacheManager.addAccessToken(access_token,new AdminAccessToken(refresh_token,sysUsers.getUserId(),username,sysUsers.getSecurityDigest()));

            //{"access_token":"0e2ql02rNxZ199fbTHr4kq9TX_A","token_type":"bearer","refresh_token":"7Q0wEwoHNZtO-zZ6BFzdd-HQVpA","expires_in":49,"scope":"all"}
            return new AdminAuthorization(access_token,refresh_token,"bearer");
        } catch (AuthenticationException e) {
            // 认证失败
            Integer original = staffCacheManager.getLoginFailureCount(username);//原来总次数
            if(original != null){
                staffCacheManager.addLoginFailureCount(username,original+1);
            }else{
                staffCacheManager.addLoginFailureCount(username,1);
            }
        }
        throw new BusinessException(Map.of("login", "登录错误"));
    }

    /**
     * 刷新访问令牌
     * @param refreshToken 刷新令牌
     * @return
     */
    public AdminAuthorization refreshAccessToken(String refreshToken){
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new BusinessException(Map.of("refreshToken", "刷新令牌不能为空"));
        }

        AdminRefreshToken adminRefreshToken = adminCacheManager.getAdminRefreshTokenByRefreshToken(refreshToken.trim());

        if(adminRefreshToken == null) {
            throw new BusinessException(Map.of("refreshToken", "刷新令牌不存在"));
        }

        //根据用户名取得一个SysUsers对象，以获取该用户的其他信息。
        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(adminRefreshToken.getUserAccount());

        if(sysUsers == null){
            throw new BusinessException(Map.of("refreshToken", "账号不存在"));
        }

        if(!sysUsers.isEnabled()){
            throw new BusinessException(Map.of("refreshToken", "用户已被禁用"));
        }

        if(!adminRefreshToken.getSecurityDigest().equals(sysUsers.getSecurityDigest())){
            throw new BusinessException(Map.of("refreshToken", "安全摘要已改变"));
        }

        String access_token = UUIDUtil.getUUID32();

        adminCacheManager.addAccessToken(access_token,new AdminAccessToken(refreshToken.trim(),sysUsers.getUserId(),sysUsers.getUserAccount(),sysUsers.getSecurityDigest()));

        return new AdminAuthorization(access_token,refreshToken.trim(),"bearer");
    }

    /**
     * 撤销令牌
     * @param accessToken 访问令牌
     */
    public void revokeToken(String accessToken){
        if (accessToken.trim().isEmpty()) {
            throw new BusinessException(Map.of("accessToken", "访问令牌不能为空"));
        }

        AdminAccessToken adminAccessToken = adminCacheManager.getAdminAccessTokenByAccessToken(accessToken.trim());
        if(adminAccessToken == null) {
            throw new BusinessException(Map.of("accessToken", "退出失败，访问令牌无效"));
        }

        adminCacheManager.deleteRefreshToken(adminAccessToken.getRefreshToken());
        adminCacheManager.deleteAccessToken(accessToken.trim());
        staffCacheManager.delete_staffSecurityDigest(adminAccessToken.getSecurityDigest());
    }


    /**
     * 查询管理员信息
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String,Object> queryAdminInfo(String fileServerAddress){
        Map<String,Object> returnValue = new HashMap<String,Object>();


        SysUserDTO sysUserDTO = new SysUserDTO();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            Object principal = authentication.getPrincipal();
            if(principal instanceof SysUsers){
                String userId =((SysUsers)principal).getUserId();//用户Id


                SysUsers sysUsers = (SysUsers)staffRepository.find(SysUsers.class, userId);
                if(sysUsers != null){
                    sysUserDTO.setUserId(sysUsers.getUserId());//用户id
                    sysUserDTO.setUserAccount(sysUsers.getUserAccount());//用户账号
                    sysUserDTO.setFullName(sysUsers.getFullName());//姓名
                    sysUserDTO.setUserDuty(sysUsers.getUserDuty());//用户的职位
                    sysUserDTO.setIssys(sysUsers.isIssys());//是否是超级用户
                    sysUserDTO.setAvatarName(sysUsers.getAvatarName());
                    sysUserDTO.setAvatarPath(fileServerAddress+sysUsers.getAvatarPath());
                    returnValue.put("permissionMenuList", staffCacheManager.query_staffPermissionMenu(sysUsers.getUserAccount()));
                }
            }
        }
        returnValue.put("sysUsers", sysUserDTO);
        returnValue.put("fileStorageSystem", fileComponent.getFileSystem());//文件存储系统 0.本地系统 10.SeaweedFS 20.MinIO 30.阿里云OSS


        return returnValue;
    }


    /**
     * 查询管理员概览数据
     * @return
     */
    public Map<String,Object> adminOverview(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        SysUsers sysUsers = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            Object o = authentication.getPrincipal();
            if(o instanceof SysUsers){
                sysUsers = (SysUsers)o;
            }
        }


        QueryResult<StaffLoginLog> qr = staffRepository.findStaffLoginLogPage(sysUsers.getUserId(), 0, 5);
        List<StaffLoginLog> staffLoginLogList = qr.getResultlist();
        if(staffLoginLogList != null && staffLoginLogList.size() >0){
            for(StaffLoginLog staffLoginLog : staffLoginLogList){
                if(staffLoginLog.getIp() != null && !staffLoginLog.getIp().trim().isEmpty()){
                    staffLoginLog.setIpAddress(IpAddress.queryAddress(staffLoginLog.getIp()));
                }
            }
        }
        returnValue.put("staffLoginLogList", staffLoginLogList);

        Long auditTopicCount = topicRepository.auditTopicCount();
        returnValue.put("auditTopicCount", auditTopicCount);

        Long auditCommentCount = commentRepository.auditCommentCount();
        returnValue.put("auditCommentCount", auditCommentCount);

        Long auditCommentReplyCount = commentRepository.auditReplyCount();
        returnValue.put("auditCommentReplyCount", auditCommentReplyCount);


        Long auditQuestionCount = questionRepository.auditQuestionCount();
        returnValue.put("auditQuestionCount", auditQuestionCount);
        Long auditAnswerCount = answerRepository.auditAnswerCount();
        returnValue.put("auditAnswerCount", auditAnswerCount);

        Long auditAnswerReplyCount = answerRepository.auditReplyCount();
        returnValue.put("auditAnswerReplyCount", auditAnswerReplyCount);

        Long feedbackCount = feedbackRepository.feedbackCount();
        returnValue.put("feedbackCount", feedbackCount);

        Long reportCount = reportRepository.reportCount();
        returnValue.put("reportCount", reportCount);

        return returnValue;
    }
}
