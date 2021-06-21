package cms.web.action.staff;


import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.service.staff.StaffService;

/**
 * 员工管理
 *
 */
@Component("staffManage")
public class StaffManage {
		
	@Resource StaffService staffService;
	
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
}
