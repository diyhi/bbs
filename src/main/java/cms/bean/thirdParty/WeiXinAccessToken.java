package cms.bean.thirdParty;

import java.io.Serializable;

/**
 * 微信access_token	网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
 * @author Gao
 *
 */
public class WeiXinAccessToken implements Serializable{
	private static final long serialVersionUID = 2117726863948253392L;
	
	/** 用户的唯一标识 每个用户对每个公众号的OpenID是唯一的。对于不同公众号，同一用户的openid不同 **/
	private String openId = "";
	/** 微信用户的唯一标识 当且仅当该网站应用已获得该用户的userinfo授权时，才会出现该字段 **/
	private String unionId = "";
	/** 接口调用凭证 **/
	private String access_token = "";
	/** access_token接口调用凭证超时时间，单位（秒） **/
	private String expires_in = "";
	/** 用户刷新access_token **/
	private String refresh_token = "";
	/** 用户授权的作用域，使用逗号（,）分隔 **/
	private String scope = "";
	
	
	/** 错误代码 **/
	private String errorCode = "";
	/** 错误信息 **/
	private String errorMessage = "";
	
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public String getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	public String getRefresh_token() {
		return refresh_token;
	}
	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
}
