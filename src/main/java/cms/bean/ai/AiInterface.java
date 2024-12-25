package cms.bean.ai;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * AI大模型
 * @author Gao
 *
 */
@Entity
public class AiInterface implements Serializable{
	private static final long serialVersionUID = -7340690365799696749L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称  **/
	@Column(length=100)
	private String name;
	/** 接口产品 10:阿里云百炼大模型  20.火山方舟大模型 **/
	private Integer interfaceProduct;
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	/** 接口动态参数 **/
	@Lob
	private String dynamicParameter;
	/** 版本 **/
	private Integer version = 0;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}
	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getDynamicParameter() {
		return dynamicParameter;
	}
	public void setDynamicParameter(String dynamicParameter) {
		this.dynamicParameter = dynamicParameter;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
}
