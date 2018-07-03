package cms.bean.user;

import java.io.Serializable;


/**
 * 刷新用户
 *
 */
public class RefreshUser implements Serializable{
	private static final long serialVersionUID = 18811897311456431L;
	
	/** 访问令牌 **/
	private String accessToken;
	/** ID **/
	private Long userId;
	/** 会员用户名 **/
	private String userName;
	/** 安全摘要 **/
	private Long securityDigest;
	/** 记住密码 **/
	private boolean rememberMe = false;
	
	public RefreshUser() {}
	
	public RefreshUser(String accessToken, Long userId, String userName, Long securityDigest, boolean rememberMe) {
		this.accessToken = accessToken;
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
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
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
