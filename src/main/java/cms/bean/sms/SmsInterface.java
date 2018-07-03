package cms.bean.sms;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * 短信接口
 *
 */
@Entity
public class SmsInterface implements Serializable{
	private static final long serialVersionUID = 8947489767344741648L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 名称 **/
	@Column(length=100)
	private String name;
	
	/** 接口产品  1.阿里大于  10.云片 **/
	private Integer interfaceProduct;

	/** 是否选择  true:启用 false: 禁用 **/
	private boolean enable = false;
	
	/** 短信接口动态参数 **/
	@Lob
	private String dynamicParameter;
	
	/** 短信发送服务 json格式 List<SendService> **/
	@Lob
	private String sendService;
	

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getSendService() {
		return sendService;
	}

	public void setSendService(String sendService) {
		this.sendService = sendService;
	}
	
	
}
