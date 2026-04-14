package cms.dto.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信用户基本信息
 * @author Gao
 *
 */
@Getter
@Setter
public class WeiXinUserInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -4541453549232674967L;
	
	/** 公众号用户的唯一标识 每个用户对每个公众号的OpenID是唯一的。对于不同公众号，同一用户的openid不同 **/
	private String openId = "";
	/** 微信用户的唯一标识 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。如果开发者拥有多个移动应用、网站应用、和公众帐号（包括小程序），可通过 UnionID 来区分用户的唯一性，因为只要是同一个微信开放平台帐号下的移动应用、网站应用和公众帐号（包括小程序），用户的 UnionID 是唯一的。换句话说，同一用户，对同一个微信开放平台下的不同应用，UnionID是相同的。**/
	private String unionId = "";
	/** 用户的昵称 **/
	private String nickname	;
	
	/** 错误代码 **/
	private String errorCode = "";
	/** 错误信息 **/
	private String errorMessage = "";

}
