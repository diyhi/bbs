package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 短信验证码表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SmsCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8157963080028136076L;

    /** 模块  1.绑定手机  2.更换绑定手机第一步  3.更换绑定手机第二步 100.注册  200.登录  300.找回密码 **/
    private Integer module;
    /** 区号 **/
    private String countryCode;
    /** 手机 **/
    private String mobile;
    /** 验证码键 **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
}
