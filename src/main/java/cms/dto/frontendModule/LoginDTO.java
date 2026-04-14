package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员登录表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8754613589208748031L;

    /** 账号 **/
    private String account;
    /** 密码 **/
    private String password;
    /** 用户类型 **/
    private Integer type;
    /** 区号 **/
    private String countryCode;
    /** 手机号 **/
    private String mobile;
    /** 邮箱 **/
    private String email;
    /** 记住密码 **/
    private Boolean rememberMe;
    /** 跳转URL **/
    private String jumpUrl;
    /** 验证码Key **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
    /** 第三方用户获取唯一标识  例如微信公众号openid **/
    private String thirdPartyOpenId;
}
