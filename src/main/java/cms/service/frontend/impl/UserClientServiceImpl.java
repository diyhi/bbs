package cms.service.frontend.impl;

import cms.component.*;
import cms.component.fileSystem.FileComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.setting.EmailSettingCacheManager;
import cms.component.setting.SettingCacheManager;
import cms.component.setting.SettingComponent;
import cms.component.sms.SmsCacheManager;
import cms.component.sms.SmsComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.thirdParty.ThirdPartyCacheManager;
import cms.component.thirdParty.ThirdPartyComponent;
import cms.component.user.*;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.frontendModule.*;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.thirdParty.WeiXinOpenId;
import cms.dto.user.*;
import cms.model.payment.PaymentLog;
import cms.model.setting.AllowLoginAccountType;
import cms.model.setting.AllowRegisterAccountType;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.user.*;
import cms.repository.payment.PaymentRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserCustomRepository;
import cms.repository.user.UserGradeRepository;
import cms.repository.user.UserRepository;
import cms.repository.user.UserRoleRepository;
import cms.service.frontend.UserClientService;
import cms.utils.*;
import cms.utils.Base64;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 前台用户服务
 */
@Service
public class UserClientServiceImpl implements UserClientService {
    private static final Logger logger = LogManager.getLogger(UserClientServiceImpl.class);


    @Resource
    SettingRepository settingRepository;
    @Resource
    SettingComponent settingComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    UserCustomRepository userCustomRepository;
    @Resource
    SmsComponent smsComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    UserComponent userComponent;
    @Resource
    OAuthComponent oAuthComponent;
    @Resource
    UserLoginLogComponent userLoginLogComponent;
    @Resource
    UserLoginLogConfig userLoginLogConfig;
    @Resource SmsCacheManager smsCacheManager;
    @Resource EmailSettingCacheManager emailSettingCacheManager;
    @Resource UserCacheManager userCacheManager;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    UserGradeRepository userGradeRepository;
    @Resource OAuthCacheManager oAuthCacheManager;
    @Resource SettingCacheManager settingCacheManager;
    @Resource
    ThirdPartyComponent thirdPartyComponent;
    @Resource  ThirdPartyCacheManager thirdPartyCacheManager;
    @Resource
    UserRoleRepository userRoleRepository;
    @Resource UserRoleCacheManager userRoleCacheManager;
    @Resource StaffCacheManager staffCacheManager;
    @Resource
    PaymentRepository paymentRepository;


    //?  匹配任何单字符
    //*  匹配0或者任意数量的字符
    //** 匹配0或者更多的目录
    private final PathMatcher matcher = new AntPathMatcher();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取会员注册界面信息
     * @return
     */
    public Map<String,Object> getRegisterViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom_cache();
        if(userCustomList != null && userCustomList.size() >0){
            Iterator<UserCustom> it = userCustomList.iterator();
            while(it.hasNext()){
                UserCustom userCustom = it.next();
                if(!userCustom.isVisible()){//如果不显示
                    it.remove();
                    continue;
                }
                if(userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()){
                    LinkedHashMap<String,String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
                    userCustom.setItemValue(itemValue);
                }

            }
        }
        returnValue.put("userCustomList", userCustomList);

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.isRegisterCaptcha()){//如果注册需要验证码
            String captchaKey = UUIDUtil.getUUID32();
            returnValue.put("captchaKey", captchaKey);
        }

        //允许注册账号类型
        List<Integer> allowRegisterAccountTypeCodeList = new ArrayList<Integer>();
        if(systemSetting.getAllowRegisterAccountType() != null && !systemSetting.getAllowRegisterAccountType().trim().isEmpty()){
            allowRegisterAccountTypeCodeList = jsonComponent.toObject(systemSetting.getAllowRegisterAccountType(), List.class);
        }
        returnValue.put("allowRegisterAccountType",allowRegisterAccountTypeCodeList);

        //是否允许国外手机号注册
        boolean isAllowForeignCellPhoneRegistration = smsComponent.isEnableInternationalSMS();
        returnValue.put("isAllowForeignCellPhoneRegistration",isAllowForeignCellPhoneRegistration);

        return returnValue;
    }

    /**
     * 会员注册
     * @param registerDTO 注册用户表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> register(RegisterDTO registerDTO, HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("register", "只读模式不允许提交数据"));
        }

        boolean isCaptcha = false;
        //用户自定义注册功能项参数
        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom_cache();


        //读取允许注册账号类型
        AllowRegisterAccountType allowRegisterAccountType = settingComponent.readAllowRegisterAccountType();

        if(allowRegisterAccountType == null || (!allowRegisterAccountType.isLocal() && !allowRegisterAccountType.isMobile() && !allowRegisterAccountType.isEmail())) {
            throw new BusinessException(Map.of("register", "不允许注册"));
        }

        if(registerDTO.getType() == null){
            throw new BusinessException(Map.of("type", "用户类型不能为空"));
        }

        boolean isLocalValid = Integer.valueOf(10).equals(registerDTO.getType()) && allowRegisterAccountType.isLocal();//10:本地账号密码用户
        boolean isMobileValid = Integer.valueOf(20).equals(registerDTO.getType()) && allowRegisterAccountType.isMobile();//20: 手机用户
        boolean isEmailValid = Integer.valueOf(30).equals(registerDTO.getType()) && allowRegisterAccountType.isEmail();//30: 邮箱用户

        if (!isLocalValid && !isMobileValid && !isEmailValid) {
            throw new BusinessException(Map.of("type", "用户类型错误"));
        }


        //访问令牌
        String accessToken = UUIDUtil.getUUID32();
        //刷新令牌
        String refreshToken = UUIDUtil.getUUID32();
        User user = new User();

        if (isLocalValid) {//10:本地账号密码用户
            if(systemSetting.isRegisterCaptcha()){//如果注册需要验证码
                isCaptcha = true;

                //校验验证码
                captchaComponent.validateCaptcha(registerDTO.getCaptchaKey(), registerDTO.getCaptchaValue(), errors);
            }
            if(registerDTO.getAccount() != null && !registerDTO.getAccount().trim().isEmpty()){
                if(registerDTO.getAccount().trim().length() < 3){
                    errors.put("account", "账号小于3个字符");
                }
                if(registerDTO.getAccount().trim().length() > 25){
                    errors.put("account", "账号大于25个字符");
                }
                if(!Verification.isNumericLettersUnderscore(registerDTO.getAccount().trim())){
                    errors.put("account", "账号只能输入由数字、26个英文字母或者下划线组成");
                }
                List<DisableUserName> disableUserNameList = userRepository.findAllDisableUserName_cache();
                if(disableUserNameList != null && disableUserNameList.size() >0){
                    for(DisableUserName disableUserName : disableUserNameList){
                        boolean flag = matcher.match(disableUserName.getName(), registerDTO.getAccount().trim());  //参数一: ant匹配风格   参数二:输入URL
                        if(flag){
                            errors.put("account","该账号不允许注册");
                        }
                    }
                }


                User u1 = userRepository.findUserByAccount(registerDTO.getAccount().trim());
                if(u1 != null){
                    errors.put("account", "该账号已注册");
                }
                User u2 = userRepository.findUserByNickname(registerDTO.getAccount().trim());
                if(u2 != null){
                    errors.put("account", "该账号不能和其他用户呢称相同");
                }

                user.setAccount(registerDTO.getAccount().trim());
            }else{
                errors.put("account", "账号不能为空");
            }

            if(registerDTO.getIssue() != null && !registerDTO.getIssue().trim().isEmpty()){//密码提示问题
                if(registerDTO.getIssue().length()>50){
                    errors.put("issue", "密码提示问题不能超过50个字符");
                }
                user.setIssue(registerDTO.getIssue().trim());
            }else{
                errors.put("issue", "密码提示问题不能为空");
            }
            if(registerDTO.getAnswer() != null && !registerDTO.getAnswer().trim().isEmpty()){//密码提示答案
                if(registerDTO.getAnswer().trim().length() != 64){//判断是否是64位SHA256
                    errors.put("answer", "密码提示答案长度错误");
                }else{
                    //密码提示答案由  密码提示答案原文sha256  进行sha256组成
                    user.setAnswer(SHA.sha256Hex(registerDTO.getAnswer().trim()));
                }
            }else{
                errors.put("answer", "密码提示答案不能为空");
            }

            user.setUserName(UUIDUtil.getUUID22());
            user.setPlatformUserId(user.getUserName());
        }
        if (isMobileValid) {//20: 手机用户
            String _countryCode = userComponent.processCountryCode(registerDTO.getCountryCode());


            if(registerDTO.getMobile() != null && !registerDTO.getMobile().trim().isEmpty()){
                if(registerDTO.getMobile().trim().length() >18){
                    errors.put("mobile", "手机号码超长");
                }else{
                    boolean mobile_verification = Verification.isPositiveInteger(registerDTO.getMobile().trim());//正整数
                    if(!mobile_verification){
                        errors.put("mobile", "手机号码不正确");

                    }else{

                        String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+registerDTO.getMobile().trim(),20);
                        User mobile_user = userRepository.findUserByPlatformUserId(platformUserId);

                        if(mobile_user != null){
                            errors.put("mobile", "手机号码已注册");

                        }
                    }
                }

                //实名认证绑定手机
                user.setMobile(_countryCode+registerDTO.getMobile().trim());
                //是否实名认证
                user.setRealNameAuthentication(true);
                String id = UUIDUtil.getUUID22();
                user.setUserName(id);//会员用户名
                user.setAccount(userComponent.queryUserIdentifier(20)+"-"+id);//用户名和账号可以用不相同的UUID
                user.setPlatformUserId(userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+registerDTO.getMobile().trim(),20));
            }else{
                errors.put("mobile", "手机号不能为空");
            }


            if(registerDTO.getSmsCode() != null && !registerDTO.getSmsCode().trim().isEmpty()){
                if(registerDTO.getSmsCode().trim().length() >6){
                    errors.put("smsCode", "手机验证码超长");
                }else{
                    if(errors.size() ==0){

                        //生成绑定手机验证码标记
                        String numeric = smsCacheManager.smsCode_generate(100,user.getPlatformUserId(), _countryCode+registerDTO.getMobile().trim(),null);
                        if(numeric != null){
                            if(!numeric.equals(registerDTO.getSmsCode())){
                                errors.put("smsCode", "手机验证码错误");
                            }

                        }else{
                            errors.put("smsCode", "手机验证码不存在或已过期");
                        }

                        //删除手机验证码标记
                        smsCacheManager.smsCode_delete(100,user.getPlatformUserId(), _countryCode+registerDTO.getMobile().trim());
                    }
                }
            }else{
                errors.put("smsCode", "手机验证码不能为空");
            }
        }
        if (isEmailValid) {//30: 邮箱用户
            if(registerDTO.getEmail() != null && !registerDTO.getEmail().trim().isEmpty()){
                if(registerDTO.getEmail().trim().length() >90){
                    errors.put("email", "邮箱长度不正确");
                }else{
                    boolean email_verification = Verification.isEmail(registerDTO.getEmail().trim());
                    if(!email_verification){
                        errors.put("email", "邮箱格式错误");

                    }else{

                        String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(registerDTO.getEmail().trim(),30);
                        User email_user = userRepository.findUserByPlatformUserId(platformUserId);

                        if(email_user != null){
                            errors.put("email", "该邮箱已注册");

                        }
                    }
                }


                user.setEmail(registerDTO.getEmail().trim());
                String id = UUIDUtil.getUUID22();
                user.setUserName(id);//会员用户名
                user.setAccount(userComponent.queryUserIdentifier(30)+"-"+id);//用户名和账号可以用不相同的UUID
                user.setPlatformUserId(userComponent.thirdPartyUserIdToPlatformUserId(registerDTO.getEmail().trim(),30));
            }else{
                errors.put("email", "邮箱不能为空");
            }


            if(registerDTO.getEmailCode() != null && !registerDTO.getEmailCode().trim().isEmpty()){
                if(registerDTO.getEmailCode().trim().length() >6){
                    errors.put("emailCode", "邮箱验证码超长");
                }else{
                    if(errors.size() ==0){

                        //获取绑定邮箱验证码标记
                        String numeric = emailSettingCacheManager.getEmailCode(100,user.getPlatformUserId(), registerDTO.getEmail().trim());
                        if(numeric != null){
                            if(!numeric.equals(registerDTO.getEmailCode())){
                                errors.put("emailCode", "邮箱验证码错误");
                            }

                        }else{
                            errors.put("emailCode", "邮箱验证码不存在或已过期");
                        }

                        //删除邮箱验证码标记
                        emailSettingCacheManager.deleteEmailCode(100,user.getPlatformUserId(), registerDTO.getEmail().trim());
                    }
                }
            }else{
                errors.put("emailCode", "邮箱验证码不能为空");
            }
        }


        //盐值
        user.setSalt(UUIDUtil.getUUID32());

        if(registerDTO.getPassword() != null && !registerDTO.getPassword().trim().isEmpty()){
            if(registerDTO.getPassword().trim().length() != 64){//判断是否是64位SHA256
                errors.put("password", "密码长度错误");
            }else{
                user.setPassword(SHA.sha256Hex(registerDTO.getPassword().trim()+"["+user.getSalt()+"]"));
            }
        }else{
            errors.put("password", "密码不能为空");
        }


        user.setRegistrationDate(LocalDateTime.now());

        if(userCustomList != null && userCustomList.size() >0){
            for(UserCustom userCustom : userCustomList){
                //用户自定义注册功能项用户输入值集合
                List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();

                if(userCustom.isVisible()){//显示
                    if(userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()){
                        LinkedHashMap<String,String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
                        userCustom.setItemValue(itemValue);
                    }
                    if(userCustom.getChooseType().equals(1)){//1.输入框
                        String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                        if(userCustom_value != null && !userCustom_value.trim().isEmpty()){
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setContent(userCustom_value.trim());
                            userInputValueList.add(userInputValue);


                            if(userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()){
                                errors.put("userCustom_"+userCustom.getId(), "长度超过 "+userCustom_value.length()+" 个字符");
                            }

                            int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
                            switch(fieldFilter){
                                case 1 : //输入数字
                                    if(!Verification.isPositiveIntegerZero(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入数字");
                                    }
                                    break;
                                case 2 : //输入字母
                                    if(!Verification.isLetter(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入字母");
                                    }
                                    break;
                                case 3 : //只能输入数字和字母
                                    if(!Verification.isNumericLetters(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入数字和字母");
                                    }
                                    break;
                                case 4 : //只能输入汉字
                                    if(!Verification.isChineseCharacter(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入汉字");
                                    }
                                    break;
                                case 5 : //正则表达式过滤
                                    if(!userCustom_value.matches(userCustom.getRegular())){
                                        errors.put("userCustom_"+userCustom.getId(), "输入错误");
                                    }
                                    break;
                                //	default:
                            }
                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }

                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }else if(userCustom.getChooseType().equals(2)){//2.单选按钮
                        String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                        if(userCustom_value != null && !userCustom_value.trim().isEmpty()){

                            String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                            if(itemValue != null ){
                                UserInputValue userInputValue = new UserInputValue();
                                userInputValue.setUserCustomId(userCustom.getId());
                                userInputValue.setOptions(userCustom_value.trim());
                                userInputValueList.add(userInputValue);

                            }else{
                                if(userCustom.isRequired()){//是否必填
                                    errors.put("userCustom_"+userCustom.getId(), "必填项");
                                }
                            }

                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);

                    }else if(userCustom.getChooseType().equals(3)){//3.多选按钮
                        String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());

                        if(userCustom_value_arr != null && userCustom_value_arr.length >0){
                            for(String userCustom_value : userCustom_value_arr){

                                if(userCustom_value != null && !userCustom_value.trim().isEmpty()){

                                    String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                    if(itemValue != null ){
                                        UserInputValue userInputValue = new UserInputValue();
                                        userInputValue.setUserCustomId(userCustom.getId());
                                        userInputValue.setOptions(userCustom_value.trim());
                                        userInputValueList.add(userInputValue);
                                    }


                                }
                            }
                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        if(userInputValueList.size() == 0){
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);

                    }else if(userCustom.getChooseType().equals(4)){//4.下拉列表
                        String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());

                        if(userCustom_value_arr != null && userCustom_value_arr.length >0){
                            for(String userCustom_value : userCustom_value_arr){

                                if(userCustom_value != null && !userCustom_value.trim().isEmpty()){

                                    String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                    if(itemValue != null ){
                                        UserInputValue userInputValue = new UserInputValue();
                                        userInputValue.setUserCustomId(userCustom.getId());
                                        userInputValue.setOptions(userCustom_value.trim());
                                        userInputValueList.add(userInputValue);
                                    }


                                }
                            }
                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        if(userInputValueList.size() == 0){
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }else if(userCustom.getChooseType().equals(5)){// 5.文本域
                        String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                        if(userCustom_value != null && !userCustom_value.trim().isEmpty()){
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setContent(userCustom_value);

                            userInputValueList.add(userInputValue);

                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }
                }
            }

        }


        if(errors.isEmpty()) {
            user.setType(registerDTO.getType());//用户类型


            //用户自定义注册功能项用户输入值集合
            List<UserInputValue> all_userInputValueList = new ArrayList<UserInputValue>();

            if (userCustomList != null && userCustomList.size() > 0) {
                for (UserCustom userCustom : userCustomList) {
                    all_userInputValueList.addAll(userCustom.getUserInputValueList());
                }
            }
            user.setSecurityDigest(new Date().getTime());
            try {
                userRepository.saveUser(user, all_userInputValueList, null);
            } catch (Exception e) {
                errors.put("register", "注册会员出错");
                // TODO Auto-generated catch block
                //	e.printStackTrace();
            }
        }

        //自动登录
        if(errors.isEmpty()){
            //写入登录日志
            UserLoginLog userLoginLog = new UserLoginLog();
            userLoginLog.setId(userLoginLogConfig.createUserLoginLogId(user.getId()));
            userLoginLog.setIp(IpAddress.getClientIpAddress(request));
            userLoginLog.setUserId(user.getId());
            userLoginLog.setTypeNumber(10);//登录
            userLoginLog.setLogonTime(LocalDateTime.now());
            Object new_userLoginLog = userLoginLogComponent.createUserLoginLogObject(userLoginLog);
            userRepository.saveUserLoginLog(new_userLoginLog);


            //自动登录
            String openId = "";//第三方openId
            if(registerDTO.getThirdPartyOpenId() != null && !registerDTO.getThirdPartyOpenId().trim().isEmpty()){
                openId = registerDTO.getThirdPartyOpenId();
                oAuthCacheManager.addOpenId(openId,refreshToken);
            }

            oAuthCacheManager.addAccessToken(accessToken, new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(), user.getSecurityDigest(),false,openId,0));
            oAuthCacheManager.addRefreshToken(refreshToken, new RefreshUser(accessToken,user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId,0));

            //将访问令牌添加到Cookie
            WebUtil.addCookie(request,response, "cms_accessToken", accessToken, 0);
            //将刷新令牌添加到Cookie
            WebUtil.addCookie(request,response, "cms_refreshToken", refreshToken, 0);
            AccessUserThreadLocal.set(new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),false,openId,0));

            //删除缓存
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());

            //异步执行会员卡赠送任务(长期任务类型)
            membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

        }


        if(errors.isEmpty()){
            //跳转URL
            String newJumpUrl = "";
            if(registerDTO.getJumpUrl() != null && !registerDTO.getJumpUrl().trim().isEmpty()){
                //Base64解码后参数进行URL编码
                String parameter = WebUtil.parameterEncoded(cms.utils.Base64.decodeBase64URL(registerDTO.getJumpUrl().trim()));

                newJumpUrl = response.encodeRedirectURL(parameter);
            }else{
                newJumpUrl = "index";
            }
            if("login".equalsIgnoreCase(newJumpUrl)){
                newJumpUrl = "index";
            }

            returnValue.put("success", true);
            returnValue.put("jumpUrl", newJumpUrl);
            returnValue.put("systemUser", new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),null,false,"",0));//登录用户

            returnValue.put("accessToken", accessToken);
            returnValue.put("refreshToken", refreshToken);
        }else{
            returnValue.put("success", false);
            returnValue.put("error", errors);

            if(isCaptcha){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());
            }
        }

        return returnValue;
    }

    /**
     * 获取会员登录界面信息
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getLoginViewModel(HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        FormCaptcha formCaptcha = new FormCaptcha();
        boolean isCaptcha = false;
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLogin_submitQuantity() <=0){//每分钟连续登录密码错误N次时出现验证码
            isCaptcha = true;
        }else{
            String account = WebUtil.getCookieByName(request.getCookies(),"cms_account");
            if(account != null && !account.trim().isEmpty()){
                //是否需要验证码  true:要  false:不要
                isCaptcha = captchaComponent.login_isCaptcha(account);
            }
        }

        if(isCaptcha){
            formCaptcha.setShowCaptcha(true);
            formCaptcha.setCaptchaKey(UUIDUtil.getUUID32());
        }

        returnValue.put("formCaptcha", formCaptcha);

        //是否允许国外手机号注册
        boolean isAllowForeignCellPhoneRegistration = smsComponent.isEnableInternationalSMS();

        returnValue.put("isAllowForeignCellPhoneRegistration",isAllowForeignCellPhoneRegistration);


        //允许登录账号类型
        List<Integer> allowLoginAccountTypeCodeList = new ArrayList<Integer>();
        if(systemSetting.getAllowLoginAccountType() != null && !systemSetting.getAllowLoginAccountType().trim().isEmpty()){
            allowLoginAccountTypeCodeList = jsonComponent.toObject(systemSetting.getAllowLoginAccountType(), List.class);
        }
        returnValue.put("allowLoginAccountType",allowLoginAccountTypeCodeList);

        return returnValue;
    }

    /**
     * 会员登录
     * @param loginDTO 注册登录表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> login(LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response){
        Map<String,String> errors = new HashMap<String,String>();

        //是否需要验证码
        boolean isCaptcha = false;

        String _countryCode = userComponent.processCountryCode(loginDTO.getCountryCode());
        List<Integer> numbers = Arrays.asList(10,20,30);
        if(loginDTO.getType() != null && numbers.contains(loginDTO.getType())){

            //读取允许登录账号类型
            AllowLoginAccountType allowLoginAccountType =  settingComponent.readAllowLoginAccountType();



            if(loginDTO.getType().equals(10)){//10:本地账号密码用户
                if(!allowLoginAccountType.isLocal()){
                    errors.put("login", "登录入口已关闭");
                }
                if(loginDTO.getAccount() == null || loginDTO.getAccount().trim().isEmpty()){
                    //账号不能为空
                    errors.put("account", "账号不能为空");
                }else{
                    loginDTO.setAccount(loginDTO.getAccount().trim());
                    isCaptcha = captchaComponent.login_isCaptcha(loginDTO.getAccount());

                }
            }else if(loginDTO.getType().equals(20)){//20: 手机用户
                if(!allowLoginAccountType.isMobile()){
                    errors.put("login", "登录入口已关闭");
                }
                if(loginDTO.getMobile() == null || loginDTO.getMobile().trim().isEmpty()){
                    //手机号不能为空
                    errors.put("mobile","手机号不能为空");
                }else{
                    loginDTO.setMobile(loginDTO.getMobile().trim());

                    String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+loginDTO.getMobile().trim(),20);
                    isCaptcha = captchaComponent.login_isCaptcha(platformUserId);
                }
            }else if(loginDTO.getType().equals(30)){//30: 邮箱用户
                if(!allowLoginAccountType.isEmail()){
                    errors.put("login", "登录入口已关闭");
                }
                if(loginDTO.getEmail() == null || loginDTO.getEmail().trim().isEmpty()){
                    //邮箱不能为空
                    errors.put("email","邮箱不能为空");
                }else{
                    loginDTO.setEmail(loginDTO.getEmail().trim());

                    String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(loginDTO.getEmail().trim(),30);
                    isCaptcha = captchaComponent.login_isCaptcha(platformUserId);
                }
            }
        }else{
            errors.put("type", "用户类型不能为空");
        }

        if(loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()){
            //密码不能为空
            errors.put("password", "密码不能为空");
        }else{
            if(loginDTO.getPassword().trim().length() != 64){//判断是否是64位SHA256
                errors.put("password", "密码长度错误");
            }
        }
        if(loginDTO.getRememberMe() == null){
            loginDTO.setRememberMe(false);
        }
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(loginDTO.getCaptchaKey(), loginDTO.getCaptchaValue(), errors);
        }

        //访问令牌
        String accessToken = UUIDUtil.getUUID32();
        //刷新令牌
        String refreshToken = UUIDUtil.getUUID32();

        User user = null;
        if(errors.size() == 0){
            if(loginDTO.getType().equals(10)){//10:本地账号密码用户
                //验证用户名
                user = userRepository.findUserByAccount(loginDTO.getAccount());
            }else if(loginDTO.getType().equals(20)){//20: 手机用户
                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+loginDTO.getMobile(),20);
                user = userRepository.findUserByPlatformUserId(platformUserId);
            }else if(loginDTO.getType().equals(30)){//30: 邮箱用户
                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(loginDTO.getEmail(),30);
                user = userRepository.findUserByPlatformUserId(platformUserId);
            }


            if(user != null){
                List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();
                if(userGradeList != null && userGradeList.size() >0){
                    for(UserGrade userGrade : userGradeList){//取得所有等级
                        if(user.getPoint() >= userGrade.getNeedPoint()){
                            user.setGradeId(userGrade.getId());
                            user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                            break;
                        }
                    }
                }
                //密码
                loginDTO.setPassword(SHA.sha256Hex(loginDTO.getPassword().trim()+"["+user.getSalt()+"]"));

                if(user.getCancelAccountTime() != -1L){
                    errors.put("account", "用户不存在");
                }

                //判断密码
                if(user.getState() >1){
                    //禁止账号
                    errors.put("account", "禁止账号");
                }

                if(errors.size() ==0 && loginDTO.getPassword().equals(user.getPassword())){




                    //删除缓存用户状态
                    userCacheManager.delete_userState(user.getUserName());

                    //写入登录日志
                    UserLoginLog userLoginLog = new UserLoginLog();
                    userLoginLog.setId(userLoginLogConfig.createUserLoginLogId(user.getId()));
                    userLoginLog.setIp(IpAddress.getClientIpAddress(request));
                    userLoginLog.setUserId(user.getId());
                    userLoginLog.setTypeNumber(10);//登录
                    userLoginLog.setLogonTime(LocalDateTime.now());
                    Object new_userLoginLog = userLoginLogComponent.createUserLoginLogObject(userLoginLog);
                    userRepository.saveUserLoginLog(new_userLoginLog);



                    String openId = "";//第三方openId
                    if(loginDTO.getThirdPartyOpenId() != null && !loginDTO.getThirdPartyOpenId().trim().isEmpty()){
                        openId = loginDTO.getThirdPartyOpenId().trim();
                        oAuthCacheManager.addOpenId(openId,refreshToken);
                    }

                    oAuthCacheManager.addAccessToken(accessToken, new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),loginDTO.getRememberMe(),openId,0));
                    oAuthCacheManager.addRefreshToken(refreshToken, new RefreshUser(accessToken,user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),loginDTO.getRememberMe(),openId,0));



                    //存放时间 单位/秒
                    int maxAge = 0;
                    if(loginDTO.getRememberMe()){
                        maxAge = WebUtil.cookieMaxAge;//默认Cookie有效期
                    }

                    //将访问令牌添加到Cookie
                    WebUtil.addCookie(request,response, "cms_accessToken", accessToken, maxAge);
                    //将刷新令牌添加到Cookie
                    WebUtil.addCookie(request,response, "cms_refreshToken", refreshToken, maxAge);
                    AccessUserThreadLocal.set(new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),user.getSecurityDigest(),loginDTO.getRememberMe(),openId,0));

                    //异步执行会员卡赠送任务(长期任务类型)
                    membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());

                }else{
                    //密码错误
                    errors.put("password", "密码错误");
                }
            }else{
                if(loginDTO.getType().equals(10)){//10:本地账号密码用户
                    //账号错误
                    errors.put("account",  "账号错误");
                }else if(loginDTO.getType().equals(20)){//20: 手机用户
                    errors.put("mobile",  "手机号错误");
                }else if(loginDTO.getType().equals(30)){//30: 邮箱用户
                    errors.put("email",  "邮箱错误");
                }
            }

        }

        //登录标记
        String loginAccount = null;
        if(loginDTO.getType() != null){
            if(loginDTO.getType().equals(10)){//10:本地账号密码用户
                loginAccount = loginDTO.getAccount();
            }else if(loginDTO.getType().equals(20)){//20: 手机用户 密码登录
                if(loginDTO.getMobile() != null && !loginDTO.getMobile().trim().isEmpty()){
                    loginAccount = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+loginDTO.getMobile().trim(),20);
                }
            }else if(loginDTO.getType().equals(30)){//30: 邮箱用户 密码登录
                if(loginDTO.getEmail() != null && !loginDTO.getEmail().trim().isEmpty()){
                    loginAccount = userComponent.thirdPartyUserIdToPlatformUserId(loginDTO.getEmail().trim(),30);
                }
            }
        }

        //登录失败处理
        if(errors.size() >0){
            //统计每分钟原来提交次数
            Integer original = settingCacheManager.getSubmitQuantity("login", loginAccount);
            if(original != null){
                settingCacheManager.addSubmitQuantity("login", loginAccount,original+1);//刷新每分钟原来提交次数
            }else{
                settingCacheManager.addSubmitQuantity("login", loginAccount,1);//刷新每分钟原来提交次数
            }

            //添加用户名到Cookie
            WebUtil.addCookie(request,response, "cms_account", loginAccount, 60);
        }else{
            //删除每分钟原来提交次数
            settingCacheManager.deleteSubmitQuantity("login", loginAccount);
            WebUtil.deleteCookie(response, "cms_account");
        }

        //跳转URL
        String _jumpUrl = "";
        if(loginDTO.getJumpUrl() != null && !loginDTO.getJumpUrl().trim().isEmpty()){
            //Base64解码后参数进行URL编码
            String parameter = WebUtil.parameterEncoded(Base64.decodeBase64URL(loginDTO.getJumpUrl().trim()));

            _jumpUrl = response.encodeRedirectURL(parameter);
        }else{
            _jumpUrl = "index";
        }
        if("login".equalsIgnoreCase(_jumpUrl)){
            _jumpUrl = "index";
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);

            //重新判断是否需要验证码
            if(loginDTO.getType() != null && numbers.contains(loginDTO.getType())){
                if(loginDTO.getType().equals(10)){//10:本地账号密码用户
                    if(loginDTO.getAccount() != null && !loginDTO.getAccount().trim().isEmpty()){
                        isCaptcha = captchaComponent.login_isCaptcha(loginDTO.getAccount());
                    }
                }else if(loginDTO.getType().equals(20)){//20: 手机用户
                    if(loginDTO.getMobile() != null && !loginDTO.getMobile().trim().isEmpty()){
                        String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+loginDTO.getMobile().trim(),20);
                        isCaptcha = captchaComponent.login_isCaptcha(platformUserId);
                    }
                }
            }
            if(isCaptcha){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());
            }
        }else{
            returnValue.put("success", true);
            returnValue.put("jumpUrl", _jumpUrl);
            returnValue.put("systemUser", new AccessUser(user.getId(),user.getUserName(),user.getAccount(),user.getNickname(),fileComponent.fileServerAddress(request)+user.getAvatarPath(),user.getAvatarName(),null,false,"",0));//登录用户

            returnValue.put("accessToken", accessToken);
            returnValue.put("refreshToken", refreshToken);

        }
        return returnValue;
    }

    /**
     * 会话续期
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> refreshToken(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        //从Header获取
        UserAuthorization headerUserAuthorization = WebUtil.getAuthorization(request);
        if(headerUserAuthorization != null){
            String accessToken = headerUserAuthorization.getAccessToken();
            String refreshToken = headerUserAuthorization.getRefreshToken();
            if(accessToken != null && !accessToken.trim().isEmpty() && refreshToken != null && !refreshToken.trim().isEmpty()){

                RefreshUser refreshUser = oAuthCacheManager.getRefreshUserByRefreshToken(refreshToken);
                if(refreshUser != null){
                    String newAccessToken = UUIDUtil.getUUID32();
                    String newRefreshToken = UUIDUtil.getUUID32();
                    //令牌续期
                    boolean flag = oAuthComponent.tokenRenewal(refreshToken,refreshUser,newAccessToken,newRefreshToken,request,response);

                    if(flag){
                        returnValue.put("accessToken", newAccessToken);
                        returnValue.put("refreshToken", newRefreshToken);
                        AccessUser accessUser = AccessUserThreadLocal.get();
                        returnValue.put("systemUser",accessUser);//登录用户
                        return returnValue;
                    }
                }
            }
        }
        return null;
    }
    /**
     * 恢复微信浏览器会话
     * 微信浏览器被清理缓存后公众号自动登录
     * @param code code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> recoverWeChatBrowserSession(String code, HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        if(code != null && !code.trim().isEmpty() && WebUtil.isWeChatBrowser(request)){//如果是微信客户端
            WeiXinOpenId weiXinOpenId = thirdPartyComponent.queryWeiXinOpenId(code.trim());
            if(weiXinOpenId != null && weiXinOpenId.getOpenId() != null && !weiXinOpenId.getOpenId().isEmpty()){
                //添加到缓存
                thirdPartyCacheManager.addWeiXinOpenId(code.trim(), weiXinOpenId);


                //刷新令牌号
                String refreshToken = oAuthCacheManager.getRefreshTokenByOpenId(weiXinOpenId.getOpenId());
                if(refreshToken != null && !refreshToken.trim().isEmpty()){


                    RefreshUser refreshUser = oAuthCacheManager.getRefreshUserByRefreshToken(refreshToken.trim());
                    if(refreshUser != null){

                        //存放时间 单位/秒
                        int maxAge = 0;
                        if(refreshUser.isRememberMe()){
                            maxAge = WebUtil.cookieMaxAge;//默认Cookie有效期
                        }
                        //将令牌写入Cookie

                        //将访问令牌添加到Cookie
                        WebUtil.addCookie(request,response, "cms_accessToken", refreshUser.getAccessToken(), maxAge);
                        //将刷新令牌添加到Cookie
                        WebUtil.addCookie(request,response, "cms_refreshToken", refreshToken, maxAge);

                        AccessUser accessUser = oAuthCacheManager.getAccessUserByAccessToken(refreshUser.getAccessToken());

                        returnValue.put("systemUser", accessUser);//登录用户
                        returnValue.put("accessToken", refreshUser.getAccessToken());
                        returnValue.put("refreshToken", refreshToken);


                    }

                }
                returnValue.put("openId", weiXinOpenId.getOpenId());
            }
        }
        return returnValue;
    }


    /**
     * 会员退出
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> logout(HttpServletRequest request, HttpServletResponse response){
        String _refreshToken = WebUtil.getCookieByName(request, "cms_refreshToken");
        String _accessToken = WebUtil.getCookieByName(request, "cms_accessToken");

        //从Header获取
        UserAuthorization headerUserAuthorization = WebUtil.getAuthorization(request);
        if(headerUserAuthorization != null){
            _accessToken = headerUserAuthorization.getAccessToken();
            _refreshToken = headerUserAuthorization.getRefreshToken();
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            userCacheManager.delete_userState(accessUser.getUserName());

        }
        if(_refreshToken != null && !_refreshToken.trim().isEmpty()){
            //删除刷新令牌
            oAuthCacheManager.deleteRefreshToken(_refreshToken);
        }
        if(_accessToken != null && !_accessToken.trim().isEmpty()){
            //删除访问令牌
            oAuthCacheManager.deleteAccessToken(_accessToken.trim());
        }
        WebUtil.deleteCookie(response, "cms_refreshToken");
        WebUtil.deleteCookie(response, "cms_accessToken");

        Map<String,Object> returnValue = new HashMap<String,Object>();

        returnValue.put("success", true);
        returnValue.put("jumpUrl", "login");
        return returnValue;
    }


    /**
     * 获取找回密码第一步界面信息
     * @return
     */
    public Map<String,Object> getForgotPasswordStep1ViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        String captchaKey = UUIDUtil.getUUID32();
        returnValue.put("captchaKey",captchaKey);


        //是否允许国外手机号注册
        boolean isAllowForeignCellPhoneRegistration = smsComponent.isEnableInternationalSMS();

        returnValue.put("isAllowForeignCellPhoneRegistration",isAllowForeignCellPhoneRegistration);

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //允许登录账号类型
        List<Integer> allowLoginAccountTypeCodeList = new ArrayList<Integer>();
        if(systemSetting.getAllowLoginAccountType() != null && !systemSetting.getAllowLoginAccountType().trim().isEmpty()){
            allowLoginAccountTypeCodeList = jsonComponent.toObject(systemSetting.getAllowLoginAccountType(), List.class);
        }
        returnValue.put("allowLoginAccountType",allowLoginAccountTypeCodeList);

        return returnValue;
    }

    /**
     * 获取找回密码第一步
     * @param forgotPasswordStep1DTO 找回密码第一步表单
     * @return
     */
    public Map<String,Object> forgotPasswordStep1(ForgotPasswordStep1DTO forgotPasswordStep1DTO){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();


        List<Integer> numbers = Arrays.asList(10,20,30);
        if(forgotPasswordStep1DTO.getType() != null && numbers.contains(forgotPasswordStep1DTO.getType())){
            if(forgotPasswordStep1DTO.getType().equals(10)){//10:本地账号密码用户
                if(forgotPasswordStep1DTO.getAccount() == null || forgotPasswordStep1DTO.getAccount().trim().isEmpty()){
                    //用户名不能为空
                    errors.put("account", "账号不能为空");
                }else{
                    forgotPasswordStep1DTO.setAccount(forgotPasswordStep1DTO.getAccount().trim());
                }
            }else if(forgotPasswordStep1DTO.getType().equals(20)){//20: 手机用户
                if(forgotPasswordStep1DTO.getMobile() == null || forgotPasswordStep1DTO.getMobile().trim().isEmpty()){
                    //手机号不能为空
                    errors.put("mobile", "手机号不能为空");
                }else{
                    forgotPasswordStep1DTO.setMobile(forgotPasswordStep1DTO.getMobile().trim());


                }
            }else if(forgotPasswordStep1DTO.getType().equals(30)){//30: 邮箱用户
                if(forgotPasswordStep1DTO.getEmail() == null || forgotPasswordStep1DTO.getEmail().trim().isEmpty()){
                    //邮箱不能为空
                    errors.put("email", "邮箱不能为空");
                }else{
                    forgotPasswordStep1DTO.setEmail(forgotPasswordStep1DTO.getEmail().trim());

                }
            }
        }else{
            errors.put("type", "用户类型不能为空");
        }

        //校验验证码
        captchaComponent.validateCaptcha(forgotPasswordStep1DTO.getCaptchaKey(), forgotPasswordStep1DTO.getCaptchaValue(), errors);

        String _countryCode = userComponent.processCountryCode(forgotPasswordStep1DTO.getCountryCode());
        String userName = "";

        if(errors.size()==0){
            if(forgotPasswordStep1DTO.getType().equals(10)){//10:本地账号密码用户
                User user = userRepository.findUserByAccount(forgotPasswordStep1DTO.getAccount().trim());
                if(user == null){
                    errors.put("account", "用户不存在");
                }else{
                    if(user.getCancelAccountTime() != -1L){
                        errors.put("account", "用户不存在");
                    }

                    if(user.getType() != 10){
                        errors.put("account", "用户不是本地密码账户");
                    }else{
                        userName = user.getUserName();
                    }

                }
            }else if(forgotPasswordStep1DTO.getType().equals(20)){//20: 手机用户
                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+forgotPasswordStep1DTO.getMobile().trim(),20);
                User mobile_user = userRepository.findUserByPlatformUserId(platformUserId);
                if(mobile_user == null){
                    errors.put("mobile", "手机用户不存在");
                }else{
                    if(mobile_user.getCancelAccountTime() != -1L){
                        errors.put("mobile", "用户不存在");
                    }
                    if(mobile_user.getType() != 20){
                        errors.put("mobile", "此手机号不是手机账户类型");
                    }else{
                        userName = mobile_user.getUserName();
                    }

                }

            }else if(forgotPasswordStep1DTO.getType().equals(30)){//30: 邮箱用户
                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(forgotPasswordStep1DTO.getEmail().trim(),30);
                User email_user = userRepository.findUserByPlatformUserId(platformUserId);
                if(email_user == null){
                    errors.put("email", "邮箱用户不存在");
                }else{
                    if(email_user.getCancelAccountTime() != -1L){
                        errors.put("email", "用户不存在");
                    }
                    if(email_user.getType() != 30){
                        errors.put("email", "此邮箱不是邮箱账户类型");
                    }else{
                        userName = email_user.getUserName();
                    }

                }

            }
        }

        if(!errors.isEmpty()){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);
            returnValue.put("jumpUrl", "forgotPassword/step2"+"?userName="+userName+"&countryCode="+_countryCode+"&mobile="+forgotPasswordStep1DTO.getMobile()+"&email="+forgotPasswordStep1DTO.getEmail());
        }

        return returnValue;
    }
    /**
     * 获取找回密码第二步界面信息
     * @param userName 用户名称
     * @return
     */
    public Map<String,Object> getForgotPasswordStep2ViewModel(String userName){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        User newUser = new User();

        Map<String,String> errors = new HashMap<String,String>();
        if(userName != null && !userName.trim().isEmpty()){
            User user = userRepository.findUserByUserName(userName.trim());
            if(user != null){
                if(user.getCancelAccountTime() != -1L){
                    errors.put("account", "用户不存在");
                }else{
                    newUser.setId(user.getId());
                    newUser.setUserName(user.getUserName());
                    newUser.setAccount(user.getAccount());
                    newUser.setIssue(user.getIssue());
                    newUser.setType(user.getType());
                    returnValue.put("user", newUser);
                }
            }else{
                errors.put("account", "用户不存在");
            }
        }else{
            errors.put("account", "用户名称不能为空");
        }

        //显示验证码
        String captchaKey = UUIDUtil.getUUID32();
        returnValue.put("captchaKey",captchaKey);

        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);
        }
        
        return returnValue;
    }

    /**
     * 获取找回密码第二步
     * @param forgotPasswordStep2DTO 找回密码第二步表单
     * @return
     */
    public Map<String,Object> forgotPasswordStep2(ForgotPasswordStep2DTO forgotPasswordStep2DTO){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("user", "只读模式不允许提交数据"));
        }

        User user = null;

        List<Integer> numbers = Arrays.asList(10,20,30);
        if(forgotPasswordStep2DTO.getType() != null && numbers.contains(forgotPasswordStep2DTO.getType())){
            if(forgotPasswordStep2DTO.getUserName() == null || forgotPasswordStep2DTO.getUserName().trim().isEmpty()){
                //用户名不能为空
                errors.put("account", "用户名称不能为空");
            }else{
                if(forgotPasswordStep2DTO.getType().equals(10)){//10:本地账号密码用户

                    //校验验证码
                    captchaComponent.validateCaptcha(forgotPasswordStep2DTO.getCaptchaKey(), forgotPasswordStep2DTO.getCaptchaValue(), errors);


                    if(errors.size()==0){
                        user = userRepository.findUserByUserName(forgotPasswordStep2DTO.getUserName().trim());
                        if(user == null){
                            errors.put("account", "账号错误");
                        }else{

                            if(user.getCancelAccountTime() != -1L){
                                errors.put("account", "用户不存在");
                            }

                            if(user.getType() != 10){
                                errors.put("account", "用户不是本地密码账户");
                            }
                            if(errors.size()==0){
                                if(forgotPasswordStep2DTO.getAnswer() != null && !forgotPasswordStep2DTO.getAnswer().trim().isEmpty()){//密码提示答案
                                    if(forgotPasswordStep2DTO.getAnswer().trim().length() != 64){//判断是否是64位SHA256
                                        errors.put("answer", "密码提示答案长度错误");
                                    }else{

                                        String answer = SHA.sha256Hex(forgotPasswordStep2DTO.getAnswer().trim());
                                        //比较密码答案
                                        if(!answer.equals(user.getAnswer())){
                                            errors.put("answer", "密码提示答案错误");
                                        }
                                    }
                                }else{
                                    errors.put("answer", "密码提示答案不能为空");
                                }
                            }
                        }
                    }





                }else if(forgotPasswordStep2DTO.getType().equals(20)){//20: 手机用户
                    if(errors.size()==0){
                        user = userRepository.findUserByUserName(forgotPasswordStep2DTO.getUserName().trim());
                        if(user == null){
                            errors.put("account", "账号错误");
                        }else{
                            if(user.getType() != 20){
                                errors.put("account", "此手机号不是手机账户类型");
                            }
                            if(user.getCancelAccountTime() != -1L){
                                errors.put("account", "用户不存在");
                            }
                        }
                    }




                    if(forgotPasswordStep2DTO.getSmsCode() != null && !forgotPasswordStep2DTO.getSmsCode().trim().isEmpty()){
                        if(forgotPasswordStep2DTO.getSmsCode().trim().length() >6){
                            errors.put("smsCode", "手机验证码超长");
                        }else{
                            if(errors.size() ==0){

                                //获取绑定手机验证码标记
                                String numeric = smsCacheManager.smsCode_generate(300,user.getPlatformUserId(), user.getMobile(),null);
                                if(numeric != null){
                                    if(!numeric.equals(forgotPasswordStep2DTO.getSmsCode())){
                                        errors.put("smsCode", "手机验证码错误");
                                    }

                                }else{
                                    errors.put("smsCode", "手机验证码不存在或已过期");
                                }

                                //删除手机验证码标记
                                smsCacheManager.smsCode_delete(300,user.getPlatformUserId(), user.getMobile());
                            }
                        }
                    }else{
                        errors.put("smsCode", "手机验证码不能为空");
                    }
                }else if(forgotPasswordStep2DTO.getType().equals(30)){//30: 邮箱用户
                    if(errors.size()==0){
                        user = userRepository.findUserByUserName(forgotPasswordStep2DTO.getUserName().trim());
                        if(user == null){
                            errors.put("account", "账号错误");
                        }else{
                            if(user.getType() != 30){
                                errors.put("account", "此邮箱不是邮箱账户类型");
                            }
                            if(user.getCancelAccountTime() != -1L){
                                errors.put("account", "用户不存在");
                            }
                        }
                    }




                    if(forgotPasswordStep2DTO.getEmailCode() != null && !forgotPasswordStep2DTO.getEmailCode().trim().isEmpty()){
                        if(forgotPasswordStep2DTO.getEmailCode().trim().length() >6){
                            errors.put("emailCode", "邮箱验证码超长");
                        }else{
                            if(errors.size() ==0){

                                //获取绑定邮箱验证码标记
                                String numeric = emailSettingCacheManager.getEmailCode(300,user.getPlatformUserId(), user.getEmail());
                                if(numeric != null){
                                    if(!numeric.equals(forgotPasswordStep2DTO.getEmailCode())){
                                        errors.put("emailCode", "邮箱验证码错误");
                                    }

                                }else{
                                    errors.put("emailCode", "手机验证码不存在或已过期");
                                }

                                //删除邮箱验证码标记
                                emailSettingCacheManager.deleteEmailCode(300,user.getPlatformUserId(), user.getEmail());
                            }
                        }
                    }else{
                        errors.put("emailCode", "邮箱验证码不能为空");
                    }
                }
            }


        }else{
            errors.put("type", "用户类型不能为空");
        }

        //新密码
        String newPassword = "";

        if(user != null){
            if(forgotPasswordStep2DTO.getPassword() != null && !forgotPasswordStep2DTO.getPassword().trim().isEmpty()){
                if(forgotPasswordStep2DTO.getPassword().trim().length() != 64){//判断是否是64位SHA256
                    errors.put("password", "密码长度错误");
                }else{
                    newPassword = SHA.sha256Hex(forgotPasswordStep2DTO.getPassword().trim()+"["+user.getSalt()+"]");
                }
            }else{
                errors.put("password", "密码不能为空");
            }
        }


        if(errors.size() == 0){
            //修改密码

            int i = userRepository.updatePassword(forgotPasswordStep2DTO.getUserName().trim(), newPassword,new Date().getTime(), user.getUserVersion());
            userCacheManager.delete_userState(forgotPasswordStep2DTO.getUserName().trim());
            if(i == 0){
                errors.put("user", "找回密码错误");
            }

        }

        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);

            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);

        }
        return returnValue;
    }

    /**
     * 获取实名认证界面信息
     * @return
     */
    public Map<String,Object> getRealNameAuthenticationViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User user = userRepository.findUserByUserName(accessUser.getUserName());
        if(user != null){
            //仅显示指定的字段
            User viewUser = new User();
            viewUser.setId(user.getId());
            viewUser.setUserName(user.getUserName());//会员用户名
            viewUser.setNickname(user.getNickname());//呢称
            viewUser.setMobile(user.getMobile());
            viewUser.setRealNameAuthentication(user.isRealNameAuthentication());

            returnValue.put("user",viewUser);
        }
        return returnValue;
    }
    /**
     * 获取手机绑定界面信息
     * @return
     */
    public Map<String,Object> getPhoneBindingViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        String captchaKey = UUIDUtil.getUUID32();
        returnValue.put("captchaKey",captchaKey);

        //是否允许国外手机号注册
        boolean isAllowForeignCellPhoneRegistration = smsComponent.isEnableInternationalSMS();

        returnValue.put("isAllowForeignCellPhoneRegistration",isAllowForeignCellPhoneRegistration);
        return returnValue;
    }
    /**
     * 手机绑定
     * @param phoneBindingDTO 手机绑定表单
     * @return
     */
    public Map<String,Object> phoneBinding(PhoneBindingDTO phoneBindingDTO){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("smsCode", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User new_user = userRepository.findUserByUserName(accessUser.getUserName());;

        if(phoneBindingDTO.getMobile() != null && !phoneBindingDTO.getMobile().trim().isEmpty()){
            if(phoneBindingDTO.getMobile().trim().length() >18){
                errors.put("mobile", "手机号码超长");
            }else{
                boolean mobile_verification = Verification.isPositiveInteger(phoneBindingDTO.getMobile().trim());//正整数
                if(!mobile_verification){
                    errors.put("mobile", "手机号码不正确");
                }else{

                    if(new_user != null){
                        if(new_user.getMobile() != null && !new_user.getMobile().isEmpty()){
                            errors.put("mobile", "手机号码不能重复绑定");
                        }

                    }else{
                        errors.put("mobile", "用户不存在");
                    }
                }
            }
        }else{
            errors.put("mobile", "手机号不能为空");
        }
        String _countryCode = userComponent.processCountryCode(phoneBindingDTO.getCountryCode());


        if(phoneBindingDTO.getSmsCode() != null && !phoneBindingDTO.getSmsCode().trim().isEmpty()){
            if(phoneBindingDTO.getSmsCode().trim().length() >6){
                errors.put("smsCode", "手机验证码超长");
            }else{
                if(errors.size() ==0){

                    //生成绑定手机验证码标记
                    String numeric = smsCacheManager.smsCode_generate(1,new_user.getPlatformUserId(), _countryCode+phoneBindingDTO.getMobile().trim(),null);
                    if(numeric != null){
                        if(!numeric.equals(phoneBindingDTO.getSmsCode())){
                            errors.put("smsCode", "手机验证码错误");
                        }

                    }else{
                        errors.put("smsCode", "手机验证码不存在或已过期");
                    }
                }
            }
        }else{
            errors.put("smsCode", "手机验证码不能为空");
        }

        if(phoneBindingDTO.getMobile() != null && !phoneBindingDTO.getMobile().trim().isEmpty()){
            //删除绑定手机验证码标记
            smsCacheManager.smsCode_delete(1,new_user.getPlatformUserId(), _countryCode+phoneBindingDTO.getMobile().trim());

        }

        if(errors.size() ==0){
            userRepository.updateUserMobile(new_user.getUserName(),_countryCode+phoneBindingDTO.getMobile().trim(),true);
            //删除缓存
            userCacheManager.delete_cache_findUserById(new_user.getId());
            userCacheManager.delete_cache_findUserByUserName(new_user.getUserName());
        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }

    /**
     * 获取修改手机绑定第一步界面信息
     * @return
     */
    public Map<String,Object> getUpdatePhoneBindingStep1ViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        String captchaKey = UUIDUtil.getUUID32();
        returnValue.put("captchaKey",captchaKey);


        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User user = userRepository.findUserByUserName(accessUser.getUserName());
        if(user != null && user.getMobile() != null && !user.getMobile().trim().isEmpty()){
            E164Format e164Format = userComponent.analyzeCountryCode(user.getMobile());
            String countryCode = e164Format.getCountryCode();//区号
            String mobile = e164Format.getMobilePhone();

            returnValue.put("countryCode",countryCode);
            returnValue.put("mobile",mobile);
        }
        return returnValue;
    }
    /**
     * 修改手机绑定第一步
     * @param smsCode 短信验证码
     * @return
     */
    public Map<String,Object> updatePhoneBinding_1(String smsCode){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("smsCode", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User new_user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
        if(new_user != null){
            if(new_user.getMobile() == null || new_user.getMobile().isEmpty()){
                errors.put("smsCode", "你还没有绑定手机");
            }


            if(errors.size() ==0 && smsCode != null && !smsCode.trim().isEmpty()){
                if(smsCode.trim().length() >6){
                    errors.put("smsCode", "手机验证码超长");
                }else{
                    if(errors.size() ==0){

                        //生成绑定手机验证码标记
                        String numeric = smsCacheManager.smsCode_generate(2,new_user.getPlatformUserId(), new_user.getMobile(),null);
                        if(numeric != null){
                            if(!numeric.equals(smsCode)){
                                errors.put("smsCode", "手机验证码错误");
                            }

                        }else{
                            errors.put("smsCode", "手机验证码不存在或已过期");
                        }
                    }
                }
            }else{
                errors.put("smsCode", "手机验证码不能为空");
            }

            //删除绑定手机验证码标记
            smsCacheManager.smsCode_delete(2,new_user.getPlatformUserId(), new_user.getMobile());
        }else{
            errors.put("smsCode", "用户不存在");
        }

        if(errors.size() ==0){
            smsCacheManager.replaceCode_delete(new_user.getId(), new_user.getMobile());
            smsCacheManager.replaceCode_generate(new_user.getId(), new_user.getMobile(),true);//标记可以进行第二步验证
        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);
            returnValue.put("jumpUrl", "user/control/updatePhoneBinding/step2");
        }
        return returnValue;
    }

    /**
     * 获取修改手机绑定第二步界面信息
     * @return
     */
    public Map<String,Object> getUpdatePhoneBindingStep2ViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        String captchaKey = UUIDUtil.getUUID32();
        returnValue.put("captchaKey",captchaKey);
        //是否允许国外手机号注册
        boolean isAllowForeignCellPhoneRegistration = smsComponent.isEnableInternationalSMS();

        returnValue.put("isAllowForeignCellPhoneRegistration",isAllowForeignCellPhoneRegistration);
        return returnValue;
    }
    /**
     * 修改手机绑定第二步
     * @param countryCode 区号
     * @param mobile 手机号
     * @param smsCode 短信验证码
     * @return
     */
    public Map<String,Object> updatePhoneBinding_2(String countryCode,String mobile,String smsCode){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("smsCode", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User new_user = userRepository.findUserByUserName(accessUser.getUserName());
        String _countryCode = userComponent.processCountryCode(countryCode);

        if(mobile != null && !mobile.trim().isEmpty()){
            if(mobile.trim().length() >18){
                errors.put("mobile", "手机号码超长");
            }else{
                boolean mobile_verification = Verification.isPositiveInteger(mobile.trim());//正整数
                if(!mobile_verification){
                    errors.put("mobile", "手机号码不正确");
                }else{

                    if(new_user != null){
                        if(new_user.getMobile() != null && !new_user.getMobile().isEmpty()){
                            if(new_user.getMobile().equals(mobile.trim())){
                                errors.put("mobile", "新手机号码不能和旧用机号码相同");
                            }
                        }
                    }
                }

                if(new_user.getType().equals(20)){//用户类型 20: 手机用户
                    String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+mobile.trim(),20);
                    User mobile_user = userRepository.findUserByPlatformUserId(platformUserId);

                    if(mobile_user != null){
                        errors.put("mobile", "手机号码已注册");

                    }
                }
            }
        }else{
            errors.put("mobile", "手机号不能为空");
        }


        if(smsCode != null && !smsCode.trim().isEmpty()){
            if(smsCode.trim().length() >6){
                errors.put("smsCode", "手机验证码超长");
            }else{
                if(errors.size() ==0){

                    //生成绑定手机验证码标记
                    String numeric = smsCacheManager.smsCode_generate(3,new_user.getPlatformUserId(), _countryCode+mobile.trim(),null);
                    if(numeric != null){
                        if(!numeric.equals(smsCode)){
                            errors.put("smsCode", "手机验证码错误");
                        }

                    }else{
                        errors.put("smsCode", "手机验证码不存在或已过期");
                    }
                }
            }
        }else{
            errors.put("smsCode", "手机验证码不能为空");
        }

        if(mobile != null && !mobile.trim().isEmpty()){
            //删除绑定手机验证码标记
            smsCacheManager.smsCode_delete(3,new_user.getPlatformUserId(), _countryCode+mobile.trim());
        }


        if(errors.size() ==0){
            boolean allow = smsCacheManager.replaceCode_generate(new_user.getId(), new_user.getMobile(),false);//查询是否验证第一步成功
            if(!allow){
                errors.put("smsCode", "旧手机号码校验失败");
            }
            //删除
            smsCacheManager.replaceCode_delete(new_user.getId(), new_user.getMobile());


        }

        if(errors.size() ==0){
            if(new_user.getType().equals(20)){//用户类型 20: 手机用户
                String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+mobile.trim(),20);
                userRepository.updateUserMobile(new_user.getUserName(),_countryCode+mobile.trim(),true,platformUserId);

            }else{
                userRepository.updateUserMobile(new_user.getUserName(),_countryCode+mobile.trim(),true);
            }


            //删除缓存
            userCacheManager.delete_cache_findUserById(new_user.getId());
            userCacheManager.delete_cache_findUserByUserName(new_user.getUserName());
        }

        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }

    /**
     * 根据账号或呢称查询用户
     * @param keyword 关键字
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public User getUser(String keyword, String fileServerAddress){
        if(keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        User u1 = userRepository.findUserByAccount(keyword.trim());
        if(u1 != null && u1.getState().equals(1) && u1.getCancelAccountTime().equals(-1L)){


            //仅显示指定的字段
            User viewUser = new User();
            viewUser.setId(u1.getId());//Id
            viewUser.setUserName(u1.getUserName());//会员用户名
            viewUser.setAccount(u1.getAccount());//账号
            viewUser.setNickname(u1.getNickname());//呢称
            viewUser.setState(u1.getState());//用户状态
            viewUser.setPoint(u1.getPoint());//当前积分
            viewUser.setGradeId(u1.getGradeId());//等级Id
            viewUser.setGradeName(u1.getGradeName());//等级名称
            viewUser.setAvatarPath(fileServerAddress+u1.getAvatarPath());//头像路径
            viewUser.setAvatarName(u1.getAvatarName());//头像名称
            return viewUser;
        }else{
            User u2 = userRepository.findUserByNickname(keyword.trim());
            if(u2 != null && u2.getState().equals(1) && u2.getCancelAccountTime().equals(-1L)){
                //仅显示指定的字段
                User viewUser = new User();
                viewUser.setId(u2.getId());//Id
                viewUser.setUserName(u2.getUserName());//会员用户名
                viewUser.setAccount(u2.getAccount());//账号
                viewUser.setNickname(u2.getNickname());//呢称
                viewUser.setState(u2.getState());//用户状态
                viewUser.setPoint(u2.getPoint());//当前积分
                viewUser.setGradeId(u2.getGradeId());//等级Id
                viewUser.setGradeName(u2.getGradeName());//等级名称
                viewUser.setAvatarPath(fileServerAddress+u2.getAvatarPath());//头像路径
                viewUser.setAvatarName(u2.getAvatarName());//头像名称
                return viewUser;
            }
        }
        return null;
    }

    /**
     * 获取积分
     * @param page 页码
     * @return
     */
    public Map<String,Object> getPoint(int page){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        //调用分页算法代码
        PageView<PointLog> pageView = new PageView<PointLog>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();


        User user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
        if(user != null){
            QueryResult<PointLog> qr =  userRepository.findPointLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());
            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
            returnValue.put("pageView", pageView);
            List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();//取得用户所有等级
            if(userGradeList != null && userGradeList.size() >0){
                for(UserGrade userGrade : userGradeList){
                    if(user.getPoint() >= userGrade.getNeedPoint()){
                        user.setGradeId(userGrade.getId());
                        user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                        break;
                    }
                }
            }

            //仅显示指定的字段
            User viewUser = new User();
            viewUser.setId(user.getId());
            viewUser.setUserName(user.getUserName());//会员用户名
            viewUser.setNickname(user.getNickname());//呢称
            viewUser.setIssue(user.getIssue());//密码提示问题
            viewUser.setRegistrationDate(user.getRegistrationDate());//注册日期
            viewUser.setPoint(user.getPoint());//当前积分
            viewUser.setGradeId(user.getGradeId());//等级Id
            viewUser.setGradeName(user.getGradeName());//将等级值设进等级参数里

            returnValue.put("user",viewUser);
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting != null){
            RewardPointInfo rewardPointInfo = new RewardPointInfo();
            rewardPointInfo.setTopic_rewardPoint(systemSetting.getTopic_rewardPoint());
            rewardPointInfo.setComment_rewardPoint(systemSetting.getComment_rewardPoint());
            rewardPointInfo.setReply_rewardPoint(systemSetting.getReply_rewardPoint());
            rewardPointInfo.setQuestion_rewardPoint(systemSetting.getQuestion_rewardPoint());
            rewardPointInfo.setAnswer_rewardPoint(systemSetting.getAnswer_rewardPoint());
            rewardPointInfo.setAnswerReply_rewardPoint(systemSetting.getAnswerReply_rewardPoint());

            returnValue.put("rewardPointInfo",rewardPointInfo);

        }
        return returnValue;
    }

    /**
     * 获取修改会员界面信息
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditUserViewModel(String fileServerAddress){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User user = userRepository.findUserByUserName(accessUser.getUserName());
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade();//取得用户所有等级
        if(userGradeList != null && userGradeList.size() >0){
            for(UserGrade userGrade : userGradeList){
                if(user.getPoint() >= userGrade.getNeedPoint()){
                    user.setGradeId(userGrade.getId());
                    user.setGradeName(userGrade.getName());//将等级值设进等级参数里
                    break;
                }
            }
        }

        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom_cache();
        if(userCustomList != null && userCustomList.size() >0){
            Iterator <UserCustom> it = userCustomList.iterator();
            while(it.hasNext()){
                UserCustom userCustom = it.next();
                if(!userCustom.isVisible()){//如果不显示
                    it.remove();
                    continue;
                }
                if(userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()){
                    LinkedHashMap<String,String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
                    userCustom.setItemValue(itemValue);
                }

            }
        }

        List<UserInputValue> userInputValueList= userCustomRepository.findUserInputValueByUserName(user.getId());
        if(userInputValueList != null && userInputValueList.size() >0){
            for(UserCustom userCustom : userCustomList){
                for(UserInputValue userInputValue : userInputValueList){
                    if(userCustom.getId().equals(userInputValue.getUserCustomId())){
                        userCustom.addUserInputValue(userInputValue);
                    }
                }
            }
        }


        //仅显示指定的字段
        User viewUser = new User();
        viewUser.setId(user.getId());
        viewUser.setUserName(user.getUserName());//会员用户名
        viewUser.setAccount(user.getAccount());//账号
        viewUser.setNickname(user.getNickname());//呢称
        viewUser.setAllowUserDynamic(user.getAllowUserDynamic());//是否允许显示用户动态
        viewUser.setIssue(user.getIssue());//密码提示问题
        viewUser.setRegistrationDate(user.getRegistrationDate());//注册日期
        viewUser.setPoint(user.getPoint());//当前积分
        viewUser.setGradeId(user.getGradeId());//等级Id
        viewUser.setGradeName(user.getGradeName());//将等级值设进等级参数里
        viewUser.setAvatarPath(fileServerAddress+user.getAvatarPath());//头像路径
        viewUser.setAvatarName(user.getAvatarName());//头像名称
        viewUser.setType(user.getType());
        viewUser.setPlatformUserId(user.getPlatformUserId());


        //有效的用户角色
        List<UserRole> validUserRoleList = new ArrayList<UserRole>();

        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole_cache();
        if(userRoleList != null && userRoleList.size() >0){
            List<UserRoleGroup> userRoleGroupList = userRoleCacheManager.query_cache_findRoleGroupByUserName(user.getUserName());

            List<String> languageFormExtensionCodeList = null;
            SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
            if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
                languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            }

            for(UserRole userRole : userRoleList){
                if(userRole.getDefaultRole()){//如果是默认角色
                    continue;
                }else{
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0);// 2999年1月1日0点0分
                    userRole.setValidPeriodEnd(defaultTime);
                }

                if(userRoleGroupList != null && userRoleGroupList.size() >0){
                    for(UserRoleGroup userRoleGroup : userRoleGroupList){
                        if(userRole.getId().equals(userRoleGroup.getUserRoleId())){
                            UserRole validUserRole = new UserRole();
                            validUserRole.setId(userRole.getId());
                            validUserRole.setName(userRole.getName());


                            validUserRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
                            validUserRoleList.add(validUserRole);
                        }
                    }
                }
            }
        }
        returnValue.put("user", viewUser);
        returnValue.put("userCustomList", userCustomList);
        returnValue.put("userRoleList", validUserRoleList);
        return returnValue;
    }

    /**
     * 修改会员
     * @param userDTO 会员表单
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Map<String,Object> editUser(UserDTO userDTO, HttpServletRequest request,HttpServletResponse response){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("user", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User user = userRepository.findUserByUserName(accessUser.getUserName());


        User new_user = new User();

        List<UserCustom> userCustomList = userCustomRepository.findAllUserCustom_cache();


        //用户自定义注册功能项参数
        if(userCustomList != null && userCustomList.size() >0){
            for(UserCustom userCustom : userCustomList){
                //用户自定义注册功能项用户输入值集合
                List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();

                if(userCustom.isVisible()){//显示
                    if(userCustom.getValue() != null && !userCustom.getValue().trim().isEmpty()){
                        LinkedHashMap<String,String> itemValue = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});
                        userCustom.setItemValue(itemValue);
                    }
                    if(userCustom.getChooseType().equals(1)){//1.输入框

                        String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());
                        if(userCustom_value != null && !userCustom_value.trim().isEmpty()){
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setContent(userCustom_value.trim());
                            userInputValueList.add(userInputValue);



                            if(userCustom.getMaxlength() != null && userCustom_value.length() > userCustom.getMaxlength()){
                                errors.put("userCustom_"+userCustom.getId(), "长度超过 "+userCustom_value.length()+" 个字符");
                            }

                            int fieldFilter = userCustom.getFieldFilter();//字段过滤方式    0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤
                            switch(fieldFilter){
                                case 1 : //输入数字
                                    if(!Verification.isPositiveIntegerZero(userCustom_value.trim()) ){
                                        errors.put("userCustom_"+userCustom.getId(),"只允许输入数字");
                                    }
                                    break;
                                case 2 : //输入字母
                                    if(!Verification.isLetter(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入字母");
                                    }
                                    break;
                                case 3 : //只能输入数字和字母
                                    if(!Verification.isNumericLetters(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入数字和字母");
                                    }
                                    break;
                                case 4 : //只能输入汉字
                                    if(!Verification.isChineseCharacter(userCustom_value.trim())){
                                        errors.put("userCustom_"+userCustom.getId(), "只允许输入汉字");
                                    }
                                    break;
                                case 5 : //正则表达式过滤
                                    if(!userCustom_value.trim().matches(userCustom.getRegular())){
                                        errors.put("userCustom_"+userCustom.getId(), "输入错误");
                                    }
                                    break;
                                //	default:
                            }
                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }

                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }else if(userCustom.getChooseType().equals(2)){//2.单选框

                        String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                        if(userCustom_value != null && !userCustom_value.trim().isEmpty()){

                            String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                            if(itemValue != null ){
                                UserInputValue userInputValue = new UserInputValue();
                                userInputValue.setUserCustomId(userCustom.getId());
                                userInputValue.setOptions(userCustom_value.trim());
                                userInputValueList.add(userInputValue);

                            }else{
                                if(userCustom.isRequired()){//是否必填
                                    errors.put("userCustom_"+userCustom.getId(),"必填项");
                                }
                            }

                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);

                    }else if(userCustom.getChooseType().equals(3)){//3.多选框

                        String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());

                        if(userCustom_value_arr != null && userCustom_value_arr.length >0){
                            for(String userCustom_value : userCustom_value_arr){

                                if(userCustom_value != null && !userCustom_value.trim().isEmpty()){

                                    String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                    if(itemValue != null ){
                                        UserInputValue userInputValue = new UserInputValue();
                                        userInputValue.setUserCustomId(userCustom.getId());
                                        userInputValue.setOptions(userCustom_value.trim());
                                        userInputValueList.add(userInputValue);
                                    }


                                }
                            }
                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        if(userInputValueList.size() == 0){
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);

                    }else if(userCustom.getChooseType().equals(4)){//4.下拉列表
                        String[] userCustom_value_arr = request.getParameterValues("userCustom_"+userCustom.getId());

                        if(userCustom_value_arr != null && userCustom_value_arr.length >0){
                            for(String userCustom_value : userCustom_value_arr){

                                if(userCustom_value != null && !userCustom_value.trim().isEmpty()){

                                    String itemValue = userCustom.getItemValue().get(userCustom_value.trim());
                                    if(itemValue != null ){
                                        UserInputValue userInputValue = new UserInputValue();
                                        userInputValue.setUserCustomId(userCustom.getId());
                                        userInputValue.setOptions(userCustom_value.trim());
                                        userInputValueList.add(userInputValue);
                                    }


                                }
                            }
                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        if(userInputValueList.size() == 0){
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }else if(userCustom.getChooseType().equals(5)){// 5.文本域
                        String userCustom_value = request.getParameter("userCustom_"+userCustom.getId());

                        if(userCustom_value != null && !userCustom_value.trim().isEmpty()){
                            UserInputValue userInputValue = new UserInputValue();
                            userInputValue.setUserCustomId(userCustom.getId());
                            userInputValue.setContent(userCustom_value);
                            userInputValueList.add(userInputValue);

                        }else{
                            if(userCustom.isRequired()){//是否必填
                                errors.put("userCustom_"+userCustom.getId(), "必填项");
                            }
                        }
                        userCustom.setUserInputValueList(userInputValueList);
                    }
                }
            }
        }

        //验证数据
        if(user.getType() <=30){//本地账户才允许设置的参数
            if(userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()){
                if(userDTO.getPassword().trim().length() != 64){//判断是否是64位SHA256
                    errors.put("password", "密码长度错误");
                }else{
                    new_user.setPassword(SHA.sha256Hex(userDTO.getPassword().trim()+"["+user.getSalt()+"]"));
                    new_user.setSecurityDigest(new Date().getTime());


                }

                //比较旧密码
                if(userDTO.getOldPassword() != null && !userDTO.getOldPassword().trim().isEmpty()){
                    if(!user.getPassword().equals(SHA.sha256Hex(userDTO.getOldPassword().trim()+"["+user.getSalt()+"]"))){
                        errors.put("oldPassword", "旧密码错误");
                    }
                }else{
                    errors.put("oldPassword", "旧密码不能为空");
                }
            }else{
                new_user.setPassword(user.getPassword());
                new_user.setSecurityDigest(user.getSecurityDigest());
            }
        }else{
            new_user.setSecurityDigest(user.getSecurityDigest());
        }


        if(userDTO.getNickname() != null && !userDTO.getNickname().trim().isEmpty()){
            userDTO.setNickname(StringUtil.deleteWhitespace(userDTO.getNickname()));
            if(user.getNickname() == null || user.getNickname().trim().isEmpty()){

                if(userDTO.getNickname().length()>15){
                    errors.put("nickname", "呢称不能超过15个字符");
                }
                User u = userRepository.findUserByNickname(userDTO.getNickname().trim());
                if(u != null){
                    errors.put("nickname", "该呢称已存在");
                }
            }else{
                errors.put("nickname", "不允许修改呢称");
            }

            User u1 = userRepository.findUserByUserName(userDTO.getNickname().trim());
            if(u1 != null){
                errors.put("nickname", "呢称不能和其他用户名相同");
            }

            User u2 = userRepository.findUserByAccount(userDTO.getNickname().trim());
            if(u2 != null){
                errors.put("nickname", "呢称不能和其他账号相同");
            }

            SysUsers s1 = staffCacheManager.query_cache_findByUserAccount(userDTO.getNickname().trim());
            if(s1 != null){
                errors.put("nickname", "呢称不能和员工账号相同");
            }
            SysUsers s2 = staffCacheManager.query_cache_findByNickname(userDTO.getNickname().trim());
            if(s2 != null){
                errors.put("nickname", "呢称不能和员工呢称相同");
            }


            List<DisableUserName> disableUserNameList = userRepository.findAllDisableUserName_cache();
            if(disableUserNameList != null && disableUserNameList.size() >0){
                for(DisableUserName disableUserName : disableUserNameList){
                    boolean flag = matcher.match(disableUserName.getName(), userDTO.getNickname().trim());  //参数一: ant匹配风格   参数二:输入URL
                    if(flag){
                        errors.put("nickname", "该呢称不允许使用");
                    }
                }
            }

            User u = userRepository.findUserByNickname(userDTO.getNickname().trim());
            if(u != null){
                if(user.getNickname() == null || user.getNickname().isEmpty() || !userDTO.getNickname().trim().equals(user.getNickname())){
                    errors.put("nickname","该呢称已存在");
                }

            }
            new_user.setNickname(userDTO.getNickname().trim());
        }

        new_user.setId(user.getId());
        new_user.setUserName(user.getUserName());
        if(new_user.getNickname() == null || new_user.getNickname().trim().isEmpty()){
            new_user.setNickname(user.getNickname());
        }
        if(userDTO.getAllowUserDynamic() != null){
            new_user.setAllowUserDynamic(userDTO.getAllowUserDynamic());//允许显示用户动态
        }


        new_user.setUserVersion(user.getUserVersion());

        //访问令牌
        String accessToken = null;
        //刷新令牌
        String refreshToken = null;
        //新的访问用户对象
        AccessUser newAccessUser = null;


        //提交
        if(errors.size() == 0){
            List<UserInputValue> userInputValueList= userCustomRepository.findUserInputValueByUserName(user.getId());

            //添加注册功能项用户输入值集合
            List<UserInputValue> add_userInputValue = new ArrayList<UserInputValue>();
            //删除注册功能项用户输入值Id集合
            List<Long> delete_userInputValueIdList = new ArrayList<Long>();
            if(userCustomList != null && userCustomList.size() >0){
                for(UserCustom userCustom : userCustomList){
                    List<UserInputValue> new_userInputValueList = userCustom.getUserInputValueList();
                    if(new_userInputValueList != null && new_userInputValueList.size() >0){
                        A:for(UserInputValue new_userInputValue : new_userInputValueList){
                            if(userInputValueList != null && userInputValueList.size() >0){
                                for(UserInputValue old_userInputValue : userInputValueList){
                                    if(new_userInputValue.getUserCustomId().equals(old_userInputValue.getUserCustomId())){


                                        if(new_userInputValue.getOptions().equals("-1")){

                                            if(new_userInputValue.getContent() == null){
                                                if(old_userInputValue.getContent() == null){
                                                    userInputValueList.remove(old_userInputValue);
                                                    continue A;
                                                }
                                            }else{
                                                if(new_userInputValue.getContent().equals(old_userInputValue.getContent())){
                                                    userInputValueList.remove(old_userInputValue);
                                                    continue A;
                                                }
                                            }

                                        }else{
                                            if(new_userInputValue.getOptions().equals(old_userInputValue.getOptions())){
                                                userInputValueList.remove(old_userInputValue);
                                                continue A;
                                            }
                                        }
                                    }
                                }
                            }
                            add_userInputValue.add(new_userInputValue);
                        }
                    }
                }
            }
            if(userInputValueList != null && userInputValueList.size() >0){
                for(UserInputValue old_userInputValue : userInputValueList){
                    delete_userInputValueIdList.add(old_userInputValue.getId());

                }
            }


            int i = userRepository.updateUser2(new_user,add_userInputValue,delete_userInputValueIdList);
            userCacheManager.delete_userState(new_user.getUserName());
            //删除缓存
            userCacheManager.delete_cache_findUserById(new_user.getId());
            userCacheManager.delete_cache_findUserByUserName(new_user.getUserName());




            if(i == 0){
                errors.put("user", "修改用户失败");
            }else{
                //有修改密码的情况要执行更新OAuth令牌操作
                if((userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) ||
                        (userDTO.getNickname() != null && !userDTO.getNickname().trim().isEmpty())){

                    User _user = userRepository.findUserByUserName(accessUser.getUserName());

                    String _accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
                    String _refreshToken = WebUtil.getCookieByName(request, "cms_refreshToken");
                    //从Header获取
                    UserAuthorization headerUserAuthorization = WebUtil.getAuthorization(request);
                    if(headerUserAuthorization != null){
                        _accessToken = headerUserAuthorization.getAccessToken();
                        _refreshToken = headerUserAuthorization.getRefreshToken();
                    }
                    if(_accessToken != null && !_accessToken.trim().isEmpty()){
                        //删除访问令牌
                        oAuthCacheManager.deleteAccessToken(_accessToken.trim());
                    }

                    if(_refreshToken != null && !_refreshToken.trim().isEmpty()){
                        //删除刷新令牌
                        oAuthCacheManager.deleteRefreshToken(_refreshToken);
                    }
                    //访问令牌
                    accessToken = UUIDUtil.getUUID32();
                    //刷新令牌
                    refreshToken = UUIDUtil.getUUID32();

                    //获取cookie的存活时间
                    int maxAge = WebUtil.getCookieMaxAge(request, "cms_accessToken"); //存放时间 单位/秒
                    boolean rememberMe = maxAge >0 ? true :false;


                    oAuthCacheManager.addAccessToken(accessToken, new AccessUser(_user.getId(),_user.getUserName(),_user.getAccount(),_user.getNickname(),fileComponent.fileServerAddress(request)+_user.getAvatarPath(),_user.getAvatarName(),_user.getSecurityDigest(),rememberMe,accessUser.getOpenId(),accessUser.getLoginInterface()));
                    oAuthCacheManager.addRefreshToken(refreshToken, new RefreshUser(accessToken,_user.getId(),_user.getUserName(),_user.getAccount(),_user.getNickname(),fileComponent.fileServerAddress(request)+_user.getAvatarPath(),_user.getAvatarName(),_user.getSecurityDigest(),rememberMe,accessUser.getOpenId(),accessUser.getLoginInterface()));

                    //第三方openId
                    oAuthCacheManager.addOpenId(accessUser.getOpenId(),refreshToken);

                    //将访问令牌添加到Cookie
                    WebUtil.addCookie(request,response, "cms_accessToken", accessToken, maxAge);
                    //将刷新令牌添加到Cookie
                    WebUtil.addCookie(request,response, "cms_refreshToken", refreshToken, maxAge);
                    newAccessUser = new AccessUser(_user.getId(),_user.getUserName(),_user.getAccount(),_user.getNickname(),fileComponent.fileServerAddress(request)+_user.getAvatarPath(),_user.getAvatarName(),_user.getSecurityDigest(),rememberMe,accessUser.getOpenId(),accessUser.getLoginInterface());
                    AccessUserThreadLocal.set(newAccessUser);

                }
            }
        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);
            returnValue.put("systemUser", newAccessUser);//登录用户

            returnValue.put("accessToken", accessToken);
            returnValue.put("refreshToken", refreshToken);
        }
        return returnValue;
    }

    /**
     * 更新头像
     * @param imgFile 图片文件
     * @param width  图片宽
     * @param height 图片高
     * @param x      X轴
     * @param y      Y轴
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> updateAvatar(MultipartFile imgFile, Integer width, Integer height, Integer x, Integer y,HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("imgFile", "只读模式不允许提交数据"));
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();




        User user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());



        // 文件大小和类型验证
        long imageSize = 5 * 1024L; // 5MB
        if (imgFile.getSize() / 1024 > imageSize) {
            throw new BusinessException(Map.of("imgFile", "文件超出允许上传大小"));
        }

        List<String> allowedFormats = Arrays.asList("gif", "jpg", "jpeg", "bmp", "png","webp");
        String originalFileName = imgFile.getOriginalFilename();
        if (!FileUtil.validateFileSuffix(originalFileName, allowedFormats)) {
            throw new BusinessException(Map.of("imgFile", "当前文件类型不允许上传"));
        }

        byte[] fileBytes;
        try {
            fileBytes = imgFile.getBytes();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("读取上传文件字节失败", e);
            }
            throw new BusinessException(Map.of("imgFile", "文件读取失败"));
        }

        int srcWidth;
        int srcHeight;
        int maxWidth = 200;
        int maxHeight = 200;
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            BufferedImage bufferImage = ImageIO.read(is);
            if (bufferImage == null) {
                throw new BusinessException(Map.of("imgFile", "无法读取图片文件"));
            }

            srcWidth = bufferImage.getWidth();
            srcHeight = bufferImage.getHeight();


            // 图像尺寸验证
            if ("blob".equalsIgnoreCase(originalFileName)) {
                if (srcWidth > maxWidth || srcHeight > maxHeight) {
                    throw new BusinessException(Map.of("imgFile", "超出最大尺寸 (" + maxWidth + "x" + maxHeight + ")"));
                }
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("解析图片尺寸失败", e);
            }
            throw new BusinessException(Map.of("imgFile", "图片处理失败"));
        }
        // 裁剪坐标验证
        if (width != null && (width <= 0 || width.toString().length() >= 8))
            errors.put("width", "宽度必须大于0且少于8位");
        if (height != null && (height <= 0 || height.toString().length() >= 8))
            errors.put("height", "高度必须大于0且少于8位");
        if (x != null && (x < 0 || x.toString().length() >= 8)) errors.put("x", "X轴必须大于或等于0且少于8位");
        if (y != null && (y < 0 || y.toString().length() >= 8)) errors.put("y", "Y轴必须大于或等于0且少于8位");

        if (errors.isEmpty()) {
            //保存文件并生成缩略图
            String suffix = FileUtil.getExtension(originalFileName).toLowerCase();
            // 如果是 blob，默认使用 png
            if (suffix.isEmpty() || "blob".equalsIgnoreCase(suffix)) {
                suffix = "png";
            }
            String newFileName = UUIDUtil.getUUID32() + "." + suffix;
            String date = user.getRegistrationDate().format(formatter);
            String pathDir = "file"+File.separator+"avatar"+File.separator + date +File.separator ;
            String pathDir_100 = "file"+File.separator+"avatar"+File.separator + date +File.separator +"100x100" +File.separator;

            fileComponent.createFolder(pathDir);
            fileComponent.createFolder(pathDir_100);




            // 原图在 200x200 范围内
            if (srcWidth <= 200 && srcHeight <= 200) {
                // 保存原图到 pathDir (作为 200x200 级别的备份)
                fileComponent.writeFile(pathDir, newFileName, fileBytes);

                //判断是否需要缩小到 100x100
                if (srcWidth <= 100 && srcHeight <= 100) {
                    // 图片很小，直接拷贝字节
                    fileComponent.writeFile(pathDir_100, newFileName, fileBytes);
                } else {
                    // 图片在 100-200 之间，等比例缩放生成 100x100 缩略图
                    fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, 100, 100);
                }
            }else {//原图大于 200x200，需要裁剪并生成两套缩略图

                if (x != null && y != null && width != null && height != null) {
                    // 根据裁剪坐标生成 200x200 缩略图
                    fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir + newFileName, suffix, x, y, width, height, 200, 200);
                    // 根据裁剪坐标生成 100x100 缩略图
                    fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, x, y, width, height, 100, 100);
                } else {
                    // 如果没有裁剪参数
                    fileComponent.writeFile(pathDir, newFileName, fileBytes);// 保存原始文件
                    fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, 100, 100);
                }
            }




            userRepository.updateUserAvatar(accessUser.getUserName(), newFileName);
            //删除缓存
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());

            //删除旧头像
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                String oldPathFile = pathDir + user.getAvatarName();
                //删除旧头像
                fileComponent.deleteFile(oldPathFile);
                String oldPathFile_100 = pathDir_100 + user.getAvatarName();
                //删除旧头像100*100
                fileComponent.deleteFile(oldPathFile_100);
            }

            String accessToken = WebUtil.getCookieByName(request, "cms_accessToken");
            //从Header获取
            UserAuthorization headerUserAuthorization = WebUtil.getAuthorization(request);
            if(headerUserAuthorization != null){
                accessToken = headerUserAuthorization.getAccessToken();
            }
            if(accessToken != null && !accessToken.trim().isEmpty()){
                //删除访问令牌
                oAuthCacheManager.deleteAccessToken(accessToken.trim());

                //修改‘访问用户’的头像名称
                accessUser.setAvatarName(newFileName);
                //添加访问令牌
                oAuthCacheManager.addAccessToken(accessToken.trim(), accessUser);
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
     * 获取用户登录日志
     * @param page 页码
     * @return
     */
    public PageView<UserLoginLog> getUserLoginLog(int page){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //调用分页算法代码
        PageView<UserLoginLog> pageView = new PageView<UserLoginLog>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();


        QueryResult<UserLoginLog> qr = userRepository.findUserLoginLogPage(accessUser.getUserId(),firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(UserLoginLog userLoginLog : qr.getResultlist()){
                userLoginLog.setIpAddress(IpAddress.queryAddress(userLoginLog.getIp()));
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
    /**
     * 获取账户余额
     * @param page 页码
     * @return
     */
    public Map<String,Object> getBalance(int page){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        User user = userRepository.findUserByUserName(accessUser.getUserName());

        returnValue.put("deposit",user.getDeposit());
        if(user != null){
            //调用分页算法代码
            PageView<PaymentLog> pageView = new PageView<PaymentLog>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
            //当前页
            int firstIndex = (page-1)*pageView.getMaxresult();

            QueryResult<PaymentLog> qr =  paymentRepository.findPaymentLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());

            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
            returnValue.put("pageView", pageView);

        }
        return returnValue;
    }
}
