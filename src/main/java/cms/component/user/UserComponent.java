package cms.component.user;

import java.util.Arrays;
import java.util.List;


import cms.dto.user.E164Format;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;



/**
 * 用户组件
 *
 */
@Component("userComponent")
public class UserComponent {
	private static final Logger logger = LogManager.getLogger(UserComponent.class);

	
	
	/**
	 * 第三方登录用户的唯一标识转为平台用户Id 
	 * @param thirdPartyUserId 第三方用户Id
	 * @param type 用户类型
	 * @return
	 */
	public String thirdPartyUserIdToPlatformUserId(String thirdPartyUserId,Integer type){
		if(type.equals(20)){//手机
			return thirdPartyUserId+"-mobile";
			
		}else if(type.equals(30)){//邮箱
			return thirdPartyUserId+"-email";
			
		}else if(type.equals(40)){//微信
			return thirdPartyUserId+"-weixin";
			
		}else if(type.equals(80)){//其他开放平台用户
			return thirdPartyUserId+"-other";
			
		}
		
		return thirdPartyUserId;
	}
	/**
	 * 根据用户类型查询用户识别码
	 * @param type 用户类型
	 * @return
	 */
	public String queryUserIdentifier(Integer type){
		if(type.equals(20)){//手机
			return "mobile";
			
		}else if(type.equals(30)){//邮箱
			return "email";
			
		}else if(type.equals(40)){//微信
			return "weixin";
			
		}else if(type.equals(80)){//其他开放平台用户
			return "other";
			
		}
		
		return "";
	}
	
	/**
	 * 平台用户Id转为第三方登录用户的唯一标识
	 * @param platformUserId 平台用户Id
	 * @return
	 */
	public String platformUserIdToThirdPartyUserId(String platformUserId){
		//平台用户Id的用户类型 区别编码
		List<String> userTypeDifferenceCodeList = Arrays.asList("-mobile","-email","-weixin","-other");
		for(String userTypeDifferenceCode : userTypeDifferenceCodeList){
			String thirdPartyUserId = Strings.CS.removeEnd(platformUserId, userTypeDifferenceCode);//移除后面相同的部分
			if(thirdPartyUserId.length() != platformUserId.length()){
				return thirdPartyUserId;
			}
		}
		
		return platformUserId;
		
	}
	
	/**
	 * 处理区号
	 * @param countryCode 区号
	 * @return
	 */
	public String processCountryCode(String countryCode){
		if(countryCode == null || countryCode.trim().isEmpty()){
			return "";
		}
		//如果是中国大陆区号，则省略+86
		if("86".equals(countryCode.trim())){
			return "";
		}else if("+86".equals(countryCode.trim())){
			return "";
		}else{
			if(Strings.CS.startsWith(countryCode, "+")){
				countryCode = countryCode.trim();
			}else{
				countryCode = "+"+countryCode.trim();
			}
		}
		return countryCode;
	}
	
	/**
	 * 解析手机号码中的区号
	 * @param mobilePhone 手机号码
	 * @return
	 */
	public E164Format analyzeCountryCode(String mobilePhone){
		E164Format e164Format = new E164Format();
		
		
		if(Strings.CS.startsWith(mobilePhone, "+")){
			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
			try {
				Phonenumber.PhoneNumber number = phoneUtil.parse(mobilePhone, null);
				e164Format.setCountryCode('+'+String.valueOf(number.getCountryCode()));
				e164Format.setMobilePhone(String.valueOf(number.getNationalNumber()));
			} catch (NumberParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("解析手机号码中的区号错误",e);
		        }
			}
		}else{
			e164Format.setMobilePhone(mobilePhone);
		}

		
		return e164Format;
	}
	

}
