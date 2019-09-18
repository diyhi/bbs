package cms.bean.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * 在线支付接口
 * @author Gao
 *
 */
@Entity
public class OnlinePaymentInterface implements Serializable{

	private static final long serialVersionUID = -8713258827061915758L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称 **/
	@Column(length=100)
	private String name;
	
	/** 接口产品  1.支付宝即时到账   4. 支付宝手机网站 **/
	private Integer interfaceProduct;

	/** 支持设备  第一位:电脑端; 第二位:移动网页端 第三位:移动应用 **/
	@Column(length=5)
	private String supportEquipment;
	
	
	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = true;
	
	/** 支付接口动态参数 **/
	@Lob
	private String dynamicParameter;
	
	/** 银行  **/
	@Transient
	private List<Bank> bankList = new ArrayList<Bank>();
	
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

	public List<Bank> getBankList() {
		return bankList;
	}

	public void setBankList(List<Bank> bankList) {
		this.bankList = bankList;
	}

	public String getSupportEquipment() {
		return supportEquipment;
	}

	public void setSupportEquipment(String supportEquipment) {
		this.supportEquipment = supportEquipment;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
