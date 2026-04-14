package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 找回密码第一步表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordStep1DTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7078106570387019949L;

    /** 账号 **/
    private String account;
    /** 用户类型 **/
    private Integer type;
    /** 区号 **/
    private String countryCode;
    /** 手机号 **/
    private String mobile;
    /** 邮箱 **/
    private String email;
    /** 验证码Key **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
}
