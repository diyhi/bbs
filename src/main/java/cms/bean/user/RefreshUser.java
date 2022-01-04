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
	/** 账号 **/
	private String account;
	/** 呢称 **/
	private String nickname;
	/** 头像路径 **/
	private String avatarPath;
	/** 头像名称 **/
	private String avatarName;
	/** 安全摘要 **/
	private Long securityDigest;
	/** 记住密码 **/
	private boolean rememberMe = false;
	
	/** 第三方用户的唯一标识 例如微信的openid **/
	private String openId;
	
	public RefreshUser() {}
	public RefreshUser(String accessToken, Long userId, String userName, String account, String nickname, String avatarPath,
			String avatarName, Long securityDigest, boolean rememberMe,String openId) {
		this.accessToken = accessToken;
		this.userId = userId;
		this.userName = userName;
		this.account = account;
		this.nickname = nickname;
		this.avatarPath = avatarPath;
		this.avatarName = avatarName;
		this.securityDigest = securityDigest;
		this.rememberMe = rememberMe;
		this.openId = openId;
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

	public String getAvatarPath() {
		return avatarPath;
	}

	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	
}
