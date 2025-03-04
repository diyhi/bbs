package cms.bean.user;

import java.io.Serializable;

/**
 * E.164格式手机号码
 * @author Gao
 *
 */
public class E164Format  implements Serializable{
	private static final long serialVersionUID = 7319476673912201163L;

	/** 区号 （空默认为+86区号）**/
	private String countryCode = "";
	
	/** 手机号码 **/
	private String mobilePhone;

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	
}
