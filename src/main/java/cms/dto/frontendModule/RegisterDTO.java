package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员注册表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1483895355630744665L;
    /** 会员用户名**/
    private String userName;
    /** 账号 **/
    private String account;
    /** 呢称 **/
    private String nickname;
    /** 密码 密码结构: sha256(sha256(密码)+[盐值])  **/
    private String password;

    /** 是否允许显示用户动态 **/
    private Boolean allowUserDynamic = true;

    /** 邮箱地址 **/
    private String email;
    /** 密码提示问题 **/
    private String issue;
    /** 密码提示答案 **/
    private String answer;


    /** 实名认证绑定手机 **/
    private String mobile;
    /** 是否实名认证 **/
    private boolean realNameAuthentication = false;

    /** 用户类型 10:本地账号密码用户 20: 手机用户 30: 邮箱用户 40:微信用户 80:其他用户**/
    private Integer type = 10;
    /** 验证码Key **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;
    /** 第三方用户获取唯一标识  例如微信公众号openid **/
    private String thirdPartyOpenId;
    /** 短信验证码 **/
    private String smsCode;
    /** 区号 **/
    private String countryCode;
    /** 邮箱验证码 **/
    private String emailCode;

    /** 跳转URL **/
    private String jumpUrl;

}
