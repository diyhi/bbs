package cms.dto.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信openid
 * @author Gao
 *
 */
@Getter
@Setter
public class WeiXinOpenId implements Serializable{
	@Serial
    private static final long serialVersionUID = 9152688025746476435L;

	/** 用户的唯一标识 每个用户对每个公众号的OpenID是唯一的。对于不同公众号，同一用户的openid不同 **/
	private String openId = "";
	
	/** 错误代码 **/
	private String errorCode = "";
	/** 错误信息 **/
	private String errorMessage = "";

}
