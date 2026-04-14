package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码校验
 */
@Getter
@Setter
public class CaptchaVerification implements Serializable {
    @Serial
    private static final long serialVersionUID = -4474077072705521795L;
    /** 校验成功 **/
    private Boolean success = false;

    public CaptchaVerification() {}
    public CaptchaVerification(boolean success) {
        this.success = success;
    }
}
