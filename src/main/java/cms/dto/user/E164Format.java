package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * E.164格式手机号码
 * @author Gao
 *
 */
@Getter
@Setter
public class E164Format  implements Serializable{
	@Serial
    private static final long serialVersionUID = 7319476673912201163L;

	/** 区号 （空默认为+86区号）**/
	private String countryCode = "";
	
	/** 手机号码 **/
	private String mobilePhone;

}
