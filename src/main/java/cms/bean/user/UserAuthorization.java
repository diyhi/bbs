package cms.bean.user;

import java.io.Serializable;


/**
 * 用户授权
 * @author Gao
 *
 */
public class UserAuthorization  implements Serializable{
	private static final long serialVersionUID = 4195148952778354166L;
	
	/** 访问令牌 **/
	private String accessToken = "";
	/** 刷新令牌 **/
	private String refreshToken = "";
	/** 访问用户 **/
	private AccessUser accessUser;
	
	public UserAuthorization() {}
	public UserAuthorization(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	public UserAuthorization(String accessToken, String refreshToken, AccessUser accessUser) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessUser = accessUser;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public AccessUser getAccessUser() {
		return accessUser;
	}
	public void setAccessUser(AccessUser accessUser) {
		this.accessUser = accessUser;
	}

	
}
