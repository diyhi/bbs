package cms.bean.sms;

import java.io.Serializable;

/**
 * 阿里大于参数
 *
 */
public class Alidayu implements Serializable{
	private static final long serialVersionUID = 3019531034925238031L;

	private String accessKeyId="";
	
	private String accessKeySecret="";

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		this.accessKeySecret = accessKeySecret;
	}
	
	
}
