package cms.model.sms;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信发送服务
 *
 */
@Getter
@Setter
public class SendService implements Serializable{
	@Serial
    private static final long serialVersionUID = -1861840775791132030L;
	
	/** 接口产品 1.阿里大于  10.云片 **/
	private Integer interfaceProduct;
	/** 服务Id 1.绑定手机  10.营销广告  **/
	private Integer serviceId;
	/** 服务名称 **/
	private String serviceName;
	/** 阿里云国内短信签名 **/
	private String alidayu_signName;
	/** 阿里云国内短信模板代码 **/
	private String alidayu_templateCode;
	/** 阿里云国际短信签名 **/
	private String alidayu_internationalSignName;
	/** 阿里云国际短信模板代码 **/
	private String alidayu_internationalTemplateCode;
	/** 阿里云国内支持变量 key:变量字段  value:备注 **/
	private Map<String,String> alidayu_variable = new LinkedHashMap<String,String>();
	

}
