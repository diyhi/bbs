package cms.bean.setting;

import java.io.Serializable;

/**
 * 允许注册账号类型
 * @author Gao
 *
 */
public class AllowRegisterAccountType implements Serializable{
	private static final long serialVersionUID = 9165054829593116204L;

	/** 本地账号密码用户 **/
	private boolean local = false;
	/** 手机用户 **/
	private boolean mobile = false;
	/** 邮箱用户 **/
	private boolean email = false;
	/** 微信用户 **/
	private boolean weChat = false;
	/** 其他用户 **/
	private boolean other = false;
	
	public boolean isLocal() {
		return local;
	}
	public void setLocal(boolean local) {
		this.local = local;
	}
	public boolean isMobile() {
		return mobile;
	}
	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}
	public boolean isEmail() {
		return email;
	}
	public void setEmail(boolean email) {
		this.email = email;
	}
	public boolean isWeChat() {
		return weChat;
	}
	public void setWeChat(boolean weChat) {
		this.weChat = weChat;
	}
	public boolean isOther() {
		return other;
	}
	public void setOther(boolean other) {
		this.other = other;
	}
	
	
}
