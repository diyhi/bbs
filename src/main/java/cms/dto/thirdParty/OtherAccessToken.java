package cms.dto.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 其他开放平台access_token
 * @author Gao
 *
 */
@Getter
@Setter
public class OtherAccessToken implements Serializable{
	@Serial
    private static final long serialVersionUID = 6855833822750511335L;
	
	
	/** 用户的唯一标识 **/
	private String openId = "";
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

}
