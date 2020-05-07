package cms.bean.thirdParty;

import java.io.Serializable;

/**
 * 支持登录接口
 * @author Gao
 *
 */
public class SupportLoginInterface implements Serializable{
	private static final long serialVersionUID = 1504895004111861984L;
	
	/** 名称 **/
	private String name;

	/** 第三方登录接口Id **/
	private Integer thirdPartyLoginInterfaceId;
	
	/** 接口产品  10.微信公众号  **/
	private Integer interfaceProduct;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getThirdPartyLoginInterfaceId() {
		return thirdPartyLoginInterfaceId;
	}

	public void setThirdPartyLoginInterfaceId(Integer thirdPartyLoginInterfaceId) {
		this.thirdPartyLoginInterfaceId = thirdPartyLoginInterfaceId;
	}

	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}

	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}

	
	
}
