package cms.web.action.user;

import javax.annotation.Resource;

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
