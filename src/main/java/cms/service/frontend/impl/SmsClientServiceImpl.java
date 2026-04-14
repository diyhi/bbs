package cms.service.frontend.impl;

import cms.component.CaptchaComponent;
import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.setting.SettingComponent;
import cms.component.sms.SmsCacheManager;
import cms.component.sms.SmsComponent;
import cms.component.user.UserCacheManager;
import cms.component.user.UserComponent;
import cms.config.BusinessException;
import cms.dto.frontendModule.SmsCodeDTO;
import cms.dto.user.AccessUser;
import cms.dto.user.E164Format;
import cms.model.setting.AllowRegisterAccountType;
import cms.model.setting.SystemSetting;
import cms.model.user.User;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.SmsClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.UUIDUtil;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 前台短信验证码服务
 */
@Service
public class SmsClientServiceImpl implements SmsClientService {
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    SettingComponent settingComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    UserComponent userComponent;
    @Resource
    UserRepository userRepository;
    @Resource
    SmsComponent smsComponent;
    @Resource SmsCacheManager smsCacheManager;
    @Autowired
    private UserCacheManager userCacheManager;


    /**
     * 获取短信验证码
     * @param smsCodeDTO 短信验证码表单
     * @return
     */
    public Map<String,Object> getSmsCode(SmsCodeDTO smsCodeDTO){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("smsCode", "只读模式不允许提交数据"));
        }

        //是否允许发短信
        boolean isAllowSMS = false;


        if(smsCodeDTO.getModule() != null){
            List<Integer> numbers = Arrays.asList(100, 200,300);

            if(!numbers.contains(smsCodeDTO.getModule())){
                errors.put("smsCode", "模块错误");
            }else{
                AllowRegisterAccountType allowRegisterAccountType = settingComponent.readAllowRegisterAccountType();
                if(smsCodeDTO.getModule().equals(100)){//注册
                    if(allowRegisterAccountType != null && allowRegisterAccountType.isMobile()){
                        isAllowSMS = true;
                    }
                }
                if(smsCodeDTO.getModule().equals(300)){//找回密码
                    isAllowSMS = true;
                }


            }
        }else{
            errors.put("smsCode", "模块错误");
        }

        if(!isAllowSMS){
            errors.put("smsCode", "不允许发短信");
        }


        //校验验证码
        captchaComponent.validateCaptcha(smsCodeDTO.getCaptchaKey(), smsCodeDTO.getCaptchaValue(), errors);


        String _countryCode = userComponent.processCountryCode(smsCodeDTO.getCountryCode());


        if(smsCodeDTO.getMobile() != null && !smsCodeDTO.getMobile().trim().isEmpty()){
            if(smsCodeDTO.getMobile().trim().length() >18){
                errors.put("smsCode", "手机号码超长");
            }else{
                boolean mobile_verification = Verification.isPositiveInteger(smsCodeDTO.getMobile().trim());//正整数
                if(!mobile_verification){
                    errors.put("smsCode", "手机号码不正确");
                }else{
                    if(errors.size() == 0 && smsCodeDTO.getModule().equals(300)){
                        String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+smsCodeDTO.getMobile().trim(),20);
                        User mobile_user = userRepository.findUserByPlatformUserId(platformUserId);
                        if(mobile_user == null){
                            errors.put("smsCode", "手机用户不存在");
                        }
                    }
                }
            }
        }else{
            errors.put("smsCode", "手机号不能为空");
        }

        if(errors.isEmpty()){
            String platformUserId = userComponent.thirdPartyUserIdToPlatformUserId(_countryCode+smsCodeDTO.getMobile().trim(),20);

            String randomNumeric = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));;//6位随机数  生成 000000 - 999999 之间的随机数
            String errorInfo = smsComponent.sendSms_code(platformUserId,_countryCode,smsCodeDTO.getMobile(),randomNumeric);//6位随机数
            if(errorInfo != null){
                errors.put("smsCode", errorInfo);
            }else{
                //删除绑定手机验证码标记
                smsCacheManager.smsCode_delete(smsCodeDTO.getModule(),platformUserId, _countryCode+smsCodeDTO.getMobile().trim());
                //生成绑定手机验证码标记
                smsCacheManager.smsCode_generate(smsCodeDTO.getModule(),platformUserId, _countryCode+smsCodeDTO.getMobile().trim(),randomNumeric);

            }
        }
        
        if(!errors.isEmpty()){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);
        }

        return returnValue;
    }

    /**
     * 获取绑定短信验证码
     * @param smsCodeDTO 短信验证码表单
     * @return
     */
    public Map<String,Object> getBindingSmsCode(SmsCodeDTO smsCodeDTO){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
        Map<String,String> errors = new HashMap<String,String>();

        if(smsCodeDTO.getModule() == null || smsCodeDTO.getModule() <1 || smsCodeDTO.getModule() >3){
            throw new BusinessException(Map.of("message", "模块错误"));
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("message", "只读模式不允许提交数据"));
        }

        //校验验证码
        captchaComponent.validateCaptcha(smsCodeDTO.getCaptchaKey(), smsCodeDTO.getCaptchaValue(), errors);

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        User new_user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());

        if(smsCodeDTO.getModule().equals(1) || smsCodeDTO.getModule().equals(3)){//1.绑定手机  3.更换绑定手机第二步
            if(smsCodeDTO.getMobile() != null && !smsCodeDTO.getMobile().trim().isEmpty()){
                if(smsCodeDTO.getMobile().trim().length() >18){
                    errors.put("mobile", "手机号码超长");
                }else{
                    boolean mobile_verification = Verification.isPositiveInteger(smsCodeDTO.getMobile().trim());//正整数
                    if(!mobile_verification){
                        errors.put("mobile", "手机号码不正确");
                    }
                }
            }else{
                errors.put("mobile", "手机号不能为空");
            }
        }else{//2.更换绑定手机第一步
            if(new_user == null){
                errors.put("message", "用户不存在");
            }else{
                if(new_user.getMobile() != null && !new_user.getMobile().isEmpty()){


                    E164Format e164Format = userComponent.analyzeCountryCode(new_user.getMobile());
                    smsCodeDTO.setCountryCode(e164Format.getCountryCode());//区号
                    smsCodeDTO.setMobile(e164Format.getMobilePhone());
                }else{
                    errors.put("message", "用户还没绑定手机");
                }
            }
        }
        if(errors.size() == 0){
            smsCodeDTO.setCountryCode(userComponent.processCountryCode(smsCodeDTO.getCountryCode()));

            String randomNumeric = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));;//6位随机数  生成 000000 - 999999 之间的随机数
            String errorInfo = smsComponent.sendSms_code(new_user.getPlatformUserId(),smsCodeDTO.getCountryCode(),smsCodeDTO.getMobile(),randomNumeric);//6位随机数
            if(errorInfo != null){
                errors.put("smsCode", errorInfo);
            }else{
                //删除绑定手机验证码标记
                smsCacheManager.smsCode_delete(smsCodeDTO.getModule(),new_user.getPlatformUserId(), smsCodeDTO.getCountryCode()+smsCodeDTO.getMobile().trim());
                //生成绑定手机验证码标记
                smsCacheManager.smsCode_generate(smsCodeDTO.getModule(),new_user.getPlatformUserId(), smsCodeDTO.getCountryCode()+smsCodeDTO.getMobile().trim(),randomNumeric);

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
}
