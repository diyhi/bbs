package cms.service.frontend.impl;

import cms.component.CaptchaComponent;
import cms.config.BusinessException;
import cms.model.feedback.Feedback;
import cms.model.setting.SystemSetting;
import cms.repository.feedback.FeedbackRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.FeedbackClientService;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 前台在线留言服务
 */
@Service
public class FeedbackClientServiceImpl implements FeedbackClientService {


    @Resource SettingRepository settingRepository;
    @Resource
    FeedbackRepository feedbackRepository;
    @Resource
    CaptchaComponent captchaComponent;

    /**
     * 获取添加在线留言界面信息
     * @return
     */
    public Map<String,Object> getAddFeedbackViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        returnValue.put("captchaKey", UUIDUtil.getUUID32());//验证码

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        //如果全局不允许提交在线留言
        returnValue.put("allowFeedback",systemSetting.isAllowFeedback());
        return returnValue;
    }

    /**
     * 添加在线留言
     * @param name 名称
     * @param contact 联系方式
     * @param content 内容
     * @param ip IP地址
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @return
     */
    public Map<String,Object> addFeedback(String name, String contact, String content,String ip,
                                          String captchaKey, String captchaValue){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("feedback", "只读模式不允许提交数据"));
        }

        //校验验证码
        captchaComponent.validateCaptcha(captchaKey, captchaValue, errors);

        Feedback feedback = new Feedback();



        //如果不允许在线留言
        if(!systemSetting.isAllowFeedback()){
            errors.put("feedback", "在线留言已关闭");
        }

        //名称
        if(name != null && !name.trim().isEmpty()){
            if(name.trim().length() >100){
                errors.put("name", "不能超过100个字符");
            }else{
                feedback.setName(name.trim());
            }
        }else{
            errors.put("name", "名称不能为空");
        }
        //联系方式
        if(contact != null && !contact.trim().isEmpty()){
            if(contact.trim().length() >100){
                errors.put("contact", "不能超过100个字符");
            }else{
                feedback.setContact(contact.trim());
            }
        }else{
            errors.put("contact", "联系方式不能为空");
        }
        if(content != null && !content.trim().isEmpty()){
            if(content.trim().length() >1000){
                errors.put("content", "字符超长");
            }else{
                feedback.setContent(content.trim());
            }

        }else{
            errors.put("content", "内容不能为空");
        }


        if(errors.size() == 0){
            feedback.setIp(ip);
            //保存
            feedbackRepository.saveFeedback(feedback);

        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            returnValue.put("captchaKey", UUIDUtil.getUUID32());
        }else{
            returnValue.put("success", true);
        }
        return  returnValue;
    }

}