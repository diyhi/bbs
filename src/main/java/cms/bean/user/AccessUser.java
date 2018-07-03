package cms.bean.user;

import java.io.Serializable;


/**
 * 访问用户
 *
 */
public class AccessUser implements Serializable{
	private static final long serialVersionUID = -3642575642338311674L;

	/** ID **/
	private Long userId;
	/** 会员用户名 **/
	private String userName;
	/** 安全摘要 **/
	private Long securityDigest;
	/** 记住密码 **/
	private boolean rememberMe = false;
	
	public AccessUser() {}
	public AccessUser(Long userId, String userName, Long securityDigest, boolean rememberMe) {
		this.userId = userId;
		this.userName = userName;
		this.securityDigest = securityDigest;
		this.rememberMe = rememberMe;
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getSecurityDigest() {
		return securityDigest;
	}
	public void setSecurityDigest(Long securityDigest) {
		this.securityDigest = securityDigest;
	}
	public boolean isRememberMe() {
		return rememberMe;
	}
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	
}
