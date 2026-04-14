package cms.model.thirdParty;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支持登录接口
 * @author Gao
 *
 */
@Getter
@Setter
public class SupportLoginInterface implements Serializable{
	@Serial
    private static final long serialVersionUID = 1504895004111861984L;
	
	/** 名称 **/
	private String name;

	/** 第三方登录接口Id **/
	private Integer thirdPartyLoginInterfaceId;
	
	/** 接口产品  10.微信公众号  50.其他开放平台 **/
	private Integer interfaceProduct;

	
}
