package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 表单验证码
 *
 */
@Getter
@Setter
public class FormCaptcha implements Serializable{
	@Serial
    private static final long serialVersionUID = -7871043677677083682L;

	/** 是否显示验证码 **/
	private boolean showCaptcha = false;
	
	/** 验证码编号 **/
	private String captchaKey;

	
}
