package cms.component;

import cms.component.setting.SettingCacheManager;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import jakarta.annotation.Resource;



import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 验证码组件
 *
 */
@Component("captchaComponent")
public class CaptchaComponent {

    @Resource SettingRepository settingRepository;
    @Resource SettingCacheManager settingCacheManager;

    @Resource CaptchaCacheManager captchaCacheManager;


    /**
     * 校验验证码
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param errors 错误信息
     * @return
     */
    public void validateCaptcha(String captchaKey, String captchaValue, Map<String,String> errors){
        //验证验证码
        if(captchaKey != null && !captchaKey.trim().isEmpty()){
            //增加验证码重试次数
            //统计每分钟原来提交次数
            Integer original = settingCacheManager.getSubmitQuantity("captcha", captchaKey.trim());
            if(original != null){
                settingCacheManager.addSubmitQuantity("captcha", captchaKey.trim(),original+1);//刷新每分钟原来提交次数
            }else{
                settingCacheManager.addSubmitQuantity("captcha", captchaKey.trim(),1);//刷新每分钟原来提交次数
            }

            String captcha = captchaCacheManager.getCaptcha(captchaKey.trim());
            if(captchaValue != null && !captchaValue.trim().isEmpty()){
                if(captcha != null && !captcha.trim().isEmpty()){
                    if(!captcha.equalsIgnoreCase(captchaValue)){
                        errors.put("captchaValue","验证码错误");
                    }
                }else{
                    errors.put("captchaValue","验证码过期");
                }
            }else{
                errors.put("captchaValue","请输入验证码");
            }
            //删除验证码
            captchaCacheManager.deleteCaptcha(captchaKey.trim());
        }else{
            errors.put("captchaValue", "需要输入验证码");
        }
    }



    /**---------------------------------- 话题 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean topic_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getTopic_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("topic", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getTopic_submitQuantity()){
                return true;
            }
        }
        return false;
    }


    /**---------------------------------- 评论 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean comment_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getComment_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("comment", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getComment_submitQuantity()){
                return true;
            }

        }
        return false;
    }


    /**---------------------------------- 问题 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean question_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getQuestion_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("question", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getQuestion_submitQuantity()){
                return true;
            }
        }
        return false;
    }


    /**---------------------------------- 答案 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean answer_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getAnswer_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("answer", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getAnswer_submitQuantity()){
                return true;
            }

        }
        return false;
    }


    /**---------------------------------- 用户登录 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param account 账号
     * @return 是否需要验证码  true:要  false:不要
     */
    public boolean login_isCaptcha(String account){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLogin_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("login", account);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= settingRepository.findSystemSetting_cache().getLogin_submitQuantity()){
                return true;
            }

        }

        return false;
    }


    /**---------------------------------- 私信 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean privateMessage_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getPrivateMessage_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("privateMessage", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getPrivateMessage_submitQuantity()){
                return true;
            }


        }
        return false;
    }

    /**---------------------------------- AI助手 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean aiAssistant_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getAiAssistant_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("aiAssistant", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getAiAssistant_submitQuantity()){
                return true;
            }


        }
        return false;
    }

    /**---------------------------------- 举报 ---------------------------------------**/
    /**
     * 是否显示验证码
     * @param userName 用户名称
     * @return 验证码标记
     */
    public boolean report_isCaptcha(String userName){

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getReport_submitQuantity() <=0){//为0时每次都出现验证码
            return true;
        }else{
            //用户每分钟提交次数
            Integer quantity = settingCacheManager.getSubmitQuantity("report", userName);

            //如果每用户每分钟提交超过设定次数，则需验证码
            if(quantity != null && quantity >= systemSetting.getReport_submitQuantity()){
                return true;
            }
        }
        return false;
    }
}
