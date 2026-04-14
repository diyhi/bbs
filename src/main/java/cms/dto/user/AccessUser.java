package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


/**
 * 访问用户
 *
 */
@Getter
@Setter
public class AccessUser implements Serializable{
	@Serial
    private static final long serialVersionUID = -3642575642338311674L;

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
	/** 登录接口 0.本地 10.微信 50.其他开放平台 **/
	private Integer loginInterface = 0;
	
	public AccessUser() {}
	public AccessUser(Long userId, String userName,String account, String nickname, String avatarPath, String avatarName,
			Long securityDigest, boolean rememberMe,String openId,Integer loginInterface) {
		this.userId = userId;
		this.userName = userName;
		this.account = account;
		this.nickname = nickname;
		this.avatarPath = avatarPath;
		this.avatarName = avatarName;
		this.securityDigest = securityDigest;
		this.rememberMe = rememberMe;
		this.openId = openId;
		this.loginInterface = loginInterface;
	}
}
