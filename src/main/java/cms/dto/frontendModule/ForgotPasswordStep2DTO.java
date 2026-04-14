package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 找回密码第二步表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordStep2DTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4441780681584027864L;

    /** 会员用户名 **/
    private String userName;
    /** 密码 **/
    private String password;
    /** 用户类型 **/
    private Integer type;
    /** 密码提示答案 **/
    private String answer;
    /** 短信验证码 **/
    private String smsCode;
    /** 邮箱验证码 **/
    private String emailCode;
    /** 验证码Key **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
}
