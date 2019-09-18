package cms.bean.payment;

import java.io.Serializable;

/**
 * 支付宝参数
 *
 */
public class Alipay implements Serializable{
	private static final long serialVersionUID = 1284080713919267980L;
	
	/** 手机网站支付的APPID **/
	private String app_id="";
	/** 商户的私钥且转pkcs8格式 **/
	private String rsa_private_key="";
	/** 支付宝公钥 **/
	private String alipay_public_key="";

	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getRsa_private_key() {
		return rsa_private_key;
	}
	public void setRsa_private_key(String rsa_private_key) {
		this.rsa_private_key = rsa_private_key;
	}
	public String getAlipay_public_key() {
		return alipay_public_key;
	}
	public void setAlipay_public_key(String alipay_public_key) {
		this.alipay_public_key = alipay_public_key;
	}


}
