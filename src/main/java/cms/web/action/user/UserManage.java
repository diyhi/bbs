package cms.web.action.user;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.bean.user.User;
import cms.bean.user.UserState;
import cms.service.user.UserService;

/**
 * 用户管理
 *
 */
@Component("userManage")
public class UserManage {
	@Resource UserService userService;
	
	
	
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
		List<String> userTypeDifferenceCodeList = Arrays.asList("-mobile","-email","-weixin");
		for(String userTypeDifferenceCode : userTypeDifferenceCodeList){
			String thirdPartyUserId = StringUtils.removeEnd(platformUserId, userTypeDifferenceCode);//移除后面相同的部分
			if(thirdPartyUserId.length() != platformUserId.length()){
				return thirdPartyUserId;
			}
		}
		
		return platformUserId;
		
	}
	
	

	/**
	 * 查询用户状态
	 * @param userName 用户名称
	 * @return
	 */
	@Cacheable(value="userManage_cache_userState",key="#userName")
	public UserState query_userState(String userName){
		User user = userService.findUserByUserName(userName);
		if(user!= null){
			return new UserState(user.getSecurityDigest(),user.getState());
		}
		return null;
	}
	/**
	 * 删除缓存用户状态
	 * @param userName 用户名称
	 * @return
	 */
	@CacheEvict(value="userManage_cache_userState",key="#userName")
	public void delete_userState(String userName){
		
	}
	
	
	/**
	 * 查询缓存 根据用户Id查询当前用户
	 * @param id 用户Id
	 * @return
	 */
	@Cacheable(value="userManage_cache_findUserById",key="#id")
	public User query_cache_findUserById(Long id){
		return userService.findUserById(id);
	}
	/**
	 * 删除缓存 根据用户Id查询当前用户
	 * @param id 用户Id
	 * @return
	 */
	@CacheEvict(value="userManage_cache_findUserById",key="#id")
	public void delete_cache_findUserById(Long id){
	}
	/**
	 * 查询缓存 根据用户名称查询当前用户
	 * @param userName 用户名称
	 * @return
	 */
	@Cacheable(value="userManage_cache_findUserByUserName",key="#userName")
	public User query_cache_findUserByUserName(String userName){
		return userService.findUserByUserName(userName);
	}
	/**
	 * 删除缓存 根据用户名称查询当前用户
	 * @param userName 用户名称
	 * @return
	 */
	@CacheEvict(value="userManage_cache_findUserByUserName",key="#userName")
	public void delete_cache_findUserByUserName(String userName){
	}

}
