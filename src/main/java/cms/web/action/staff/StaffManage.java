package cms.web.action.staff;


import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import cms.bean.staff.PermissionMenu;
import cms.bean.staff.SysUsers;
import cms.service.staff.ACLService;
import cms.service.staff.StaffService;

/**
 * 员工管理
 *
 */
@Component("staffManage")
public class StaffManage {
		
	@Resource StaffService staffService;
	@Resource ACLService aclService;
	
	/**
	 * 查询用户的权限菜单
	 * @param userAccount 用户账号
	 * @return
	 */
	public List<PermissionMenu> queryStaffPermissionMenu(String userAccount){
		
		SysUsers sysUsers = staffService.findByUserAccount(userAccount);
		
		if(sysUsers != null && sysUsers.isEnabled()){
			
			if(sysUsers.isIssys()){//超级用户
				List<PermissionMenu> permissionMenuList = aclService.findAllPermissionMenu();
				return permissionMenuList;
			}else{
				//权限Id集合
				List<String> sysPermissionIdList = staffService.findPermissionIdByUserAccount(userAccount);
				if(sysPermissionIdList != null && sysPermissionIdList.size() >0){
					List<PermissionMenu> permissionMenuList = aclService.findPermissionMenuByPermissionIdList(sysPermissionIdList);
					return permissionMenuList;
				}
			}
		}
		return null;
	}
	
	/**
	 * 查询员工对应的权限菜单 -- 缓存
	 * @param userAccount 用户账号
	 * @return
	 */
	@Cacheable(value="staffManage_cache_queryStaffPermissionMenu",key="#userAccount")
	public List<PermissionMenu> query_staffPermissionMenu(String userAccount){
		return this.queryStaffPermissionMenu(userAccount);
	}
	/**
	 * 删除员工对应的权限菜单 -- 缓存
	 * @param userAccount 用户账号
	 * @return
	 */
	@CacheEvict(value="staffManage_cache_queryStaffPermissionMenu",key="#userAccount")
	public void delete_staffPermissionMenu(String userAccount){
		
	}
	/**
	 * 清空员工对应的权限菜单 -- 缓存
	 * @param userAccount 用户账号
	 * @return
	 */
	@CacheEvict(value="staffManage_cache_queryStaffPermissionMenu",allEntries=true)
	public void clear_staffPermissionMenu(){
		
	}
	
	/**
	 * 查询所有权限 -- 缓存
	 * @return
	 */
	@Cacheable(value="staffManage_cache_queryAllAuthorities")
	public Collection<GrantedAuthority> query_allAuthorities(){
		return staffService.loadAllAuthorities();
	}
	/**
	 * 清空所有权限 -- 缓存
	 */
	@CacheEvict(value="staffManage_cache_queryAllAuthorities",allEntries=true)
	public void clear_allAuthorities(){
	}
	
	/**
	 * 查询员工的权限 -- 缓存
	 * @param userAccount 用户账号
	 * @return
	 */
	@Cacheable(value="staffManage_cache_queryUserAuthoritiesByName",key="#userAccount")
	public Collection<GrantedAuthority> query_userAuthoritiesByName(String userAccount){
		return staffService.loadUserAuthoritiesByName(userAccount);
	}
	/**
	 * 删除员工的权限 -- 缓存
	 * @param userAccount 用户账号
	 * @return
	 */
	@CacheEvict(value="staffManage_cache_queryUserAuthoritiesByName",key="#userAccount")
	public void delete_userAuthoritiesByName(String userAccount){
		
	}
	/**
	 * 清空员工的权限 -- 缓存
	 * @return
	 */
	@CacheEvict(value="staffManage_cache_queryUserAuthoritiesByName",allEntries=true)
	public void clear_userAuthoritiesByName(){
		
	}
	
	
	/**
	 * 查询员工安全摘要
	 * @param staffName 员工名称
	 * @return
	 */
	@Cacheable(value="staffManage_cache_staffSecurityDigest",key="#staffName")
	public String query_staffSecurityDigest(String staffName){
		return staffService.findSecurityDigestByStaffName(staffName);
	}
	/**
	 * 删除缓存员工安全摘要
	 * @param staffName 员工名称
	 * @return
	 */
	@CacheEvict(value="staffManage_cache_staffSecurityDigest",key="#staffName")
	public void delete_staffSecurityDigest(String staffName){
		
	}
	

	/**
	 * 增加 登录失败次数
	 * @param staffName 员工名称
	 * @param count 总数
	 */
	@CachePut(value="staffManage_cache_loginFailureCount",key="#staffName")
	public Integer addLoginFailureCount(String staffName,Integer count){
		return count;
	}
	/**
	 * 查询 登录失败次数
	 * @param staffName 员工名称
	 * @return
	 */
	@Cacheable(value="staffManage_cache_loginFailureCount",key="#staffName")
	public Integer getLoginFailureCount(String staffName){
		return null;
	}
	/**
	 * 删除 登录失败次数
	 * @param staffName 员工名称
	 */
	@CacheEvict(value="staffManage_cache_loginFailureCount",key="#staffName")
	public void deleteLoginFailureCount(String staffName){
	}
	
	/**
	 * 查询缓存 根据账号查询当前员工
	 * @param userAccount 员工账号
	 * @return
	 */
	@Cacheable(value="staffManage_cache_findByUserAccount",key="#userAccount")
	public SysUsers query_cache_findByUserAccount(String userAccount){
		return staffService.findByUserAccount(userAccount);
	}
	/**
	 * 删除缓存 根据账号查询当前员工
	 * @param userAccount 员工账号
	 */
	@CacheEvict(value="staffManage_cache_findByUserAccount",key="#userAccount")
	public void delete_cache_findByUserAccount(String userAccount){
	}
	
	/**
	 * 查询缓存 根据呢称查询当前员工
	 * @param nickname 呢称
	 * @return
	 */
	@Cacheable(value="staffManage_cache_findByNickname",key="#nickname")
	public SysUsers query_cache_findByNickname(String nickname){
		return staffService.findByNickname(nickname);
	}
	/**
	 * 删除缓存 根据呢称查询当前员工
	 * @param nickname 呢称
	 */
	@CacheEvict(value="staffManage_cache_findByNickname",key="#nickname")
	public void delete_cache_findByNickname(String nickname){
	}
}
