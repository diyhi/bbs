package cms.dto.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 其他开放平台配置信息
 * @author Gao
 *
 */
@Getter
@Setter
public class OtherConfig implements Serializable{
	@Serial
    private static final long serialVersionUID = 3170012397245054448L;
	
	/** 开放平台 - 应用ID **/
	private String appID="";
	/** 开放平台 - 应用私钥 **/
	private String appSecret="";
}
