package cms.bean.ai;

import java.io.Serializable;

/**
 * AI大模型产品
 * @author Gao
 *
 */
public class AiProduct implements Serializable{
	private static final long serialVersionUID = -8649233994892624474L;
	
	/** 接口产品 **/
	private Integer interfaceProduct;
	/** 名称 **/
	private String name;
	
	
	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}
	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
