package cms.service.frontend;


import cms.dto.frontendModule.SmsCodeDTO;

import java.util.Map;

/**
 * 前台短信验证码服务接口
 */
public interface SmsClientService {
    /**
     * 获取短信验证码
     * @param smsCodeDTO 短信验证码表单
     * @return
     */
    public Map<String,Object> getSmsCode(SmsCodeDTO smsCodeDTO);
    /**
     * 获取绑定短信验证码
     * @param smsCodeDTO 短信验证码表单
     * @return
     */
    public Map<String,Object> getBindingSmsCode(SmsCodeDTO smsCodeDTO);

}
