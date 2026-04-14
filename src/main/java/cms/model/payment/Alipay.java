package cms.model.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支付宝参数
 *
 */
@Getter
@Setter
public class Alipay implements Serializable{
	@Serial
    private static final long serialVersionUID = 1284080713919267980L;
	
	/** 手机网站支付的APPID **/
	private String app_id="";
	/** 商户的私钥且转pkcs8格式 **/
	private String rsa_private_key="";
	/** 支付宝公钥 **/
	private String alipay_public_key="";

}
