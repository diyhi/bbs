package cms.dto.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信配置信息
 * @author Gao
 *
 */
@Getter
@Setter
public class WeChatConfig implements Serializable{
	@Serial
    private static final long serialVersionUID = -5692566131895731007L;
	
	/** 开放平台 - 应用唯一标识 **/
	private String op_appID="";
	/** 开放平台 - 应用密钥 **/
	private String op_appSecret="";
	
	/** 公众号 - 应用唯一标识 **/
	private String oa_appID="";
	/** 公众号 - 应用密钥 **/
	private String oa_appSecret="";
	
}
