package cms.bean.thirdParty;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * 第三方登录接口
 * @author Gao
 *
 */
@Entity
public class ThirdPartyLoginInterface implements Serializable{
	private static final long serialVersionUID = 345178962574816009L;
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称 **/
	@Column(length=100)
	private String name;
	
	/** 接口产品  10.微信  **/
	private Integer interfaceProduct;

	/** 支持设备  第一位:电脑端; 第二位:移动网页端 第三位:移动应用 第四位:微信浏览器 **/
	@Column(length=10)
	private String supportEquipment;
	
	
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	
	/** 接口动态参数 **/
	@Lob
	private String dynamicParameter;

	/** 排序 **/
	private Integer sort = 1;
	
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

	public String getSupportEquipment() {
		return supportEquipment;
	}

	public void setSupportEquipment(String supportEquipment) {
		this.supportEquipment = supportEquipment;
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

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
}
