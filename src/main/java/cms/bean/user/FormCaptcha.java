package cms.bean.user;

import java.io.Serializable;

/**
 * 表单验证码
 *
 */
public class FormCaptcha implements Serializable{
	private static final long serialVersionUID = -7871043677677083682L;

	/** 是否显示验证码 **/
	private boolean showCaptcha = false;
	
	/** 验证码编号 **/
	private String captchaKey;

	public boolean isShowCaptcha() {
		return showCaptcha;
	}

	public void setShowCaptcha(boolean showCaptcha) {
		this.showCaptcha = showCaptcha;
	}

	public String getCaptchaKey() {
		return captchaKey;
	}

	public void setCaptchaKey(String captchaKey) {
		this.captchaKey = captchaKey;
	}

	
}
