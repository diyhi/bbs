package cms.bean.sms;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信发送服务
 *
 */
public class SendService implements Serializable{
	private static final long serialVersionUID = -1861840775791132030L;
	
	/** 接口产品 1.阿里大于  10.云片 **/
	private Integer interfaceProduct;
	/** 服务Id 1.绑定手机  10.营销广告  **/
	private Integer serviceId;
	/** 服务名称 **/
	private String serviceName;
	/** 阿里大于短信签名 **/
	private String alidayu_signName;
	/** 阿里大于短信模板代码 **/
	private String alidayu_templateCode;
	/** 阿里大于支持变量 key:变量字段  value:备注 **/
	private Map<String,String> alidayu_variable = new LinkedHashMap<String,String>();
	
	
	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}
	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getAlidayu_signName() {
		return alidayu_signName;
	}
	public void setAlidayu_signName(String alidayu_signName) {
		this.alidayu_signName = alidayu_signName;
	}
	public String getAlidayu_templateCode() {
		return alidayu_templateCode;
	}
	public void setAlidayu_templateCode(String alidayu_templateCode) {
		this.alidayu_templateCode = alidayu_templateCode;
	}
	public Map<String, String> getAlidayu_variable() {
		return alidayu_variable;
	}
	public void setAlidayu_variable(Map<String, String> alidayu_variable) {
		this.alidayu_variable = alidayu_variable;
	}
	
	
}
