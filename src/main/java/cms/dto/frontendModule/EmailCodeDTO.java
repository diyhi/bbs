package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 邮箱验证码表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -8498084043698716357L;

    /** 模块  100.注册  200.登录  300.找回密码 **/
    private Integer module;
    /** 邮箱 **/
    private String email;
    /** 验证码键 **/
    private String captchaKey;
    /** 验证码值 **/
    private String captchaValue;

}
