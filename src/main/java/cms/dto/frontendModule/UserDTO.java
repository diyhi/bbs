package cms.dto.frontendModule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 会员表单
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4824669853663686090L;

    /** 密码 **/
    private String password;
    /** 旧密码 **/
    private String oldPassword;
    /** 呢称 **/
    private String nickname;
    /** 是否允许显示用户动态 **/
    private Boolean allowUserDynamic = true;
}
