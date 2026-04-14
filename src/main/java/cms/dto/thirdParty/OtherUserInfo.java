package cms.dto.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 其他开放平台用户基本信息
 * @author Gao
 *
 */
@Getter
@Setter
public class OtherUserInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = 7222033753788044785L;
	
	/** 用户的唯一标识 **/
	private String openId = "";
	/** 用户的昵称 **/
	private String nickname	;
	
	/** 访问令牌 **/
	private String accessToken;
	/** 刷新令牌 **/
	private String refreshToken;
	
	
	/** 错误代码 **/
	private String errorCode = "";
	/** 错误信息 **/
	private String errorMessage = "";
	

}
