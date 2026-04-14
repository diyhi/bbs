package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 手机绑定表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneBindingDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6656957226908322084L;

    /** 区号 **/
    private String countryCode;
    /** 手机号 **/
    private String mobile;
    /** 手机验证码 **/
    private String smsCode;
    /** 验证码Key
    private String captchaKey; **/
    /** 验证码值
    private String captchaValue; **/
}
