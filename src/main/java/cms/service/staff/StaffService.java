package cms.service.staff;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import cms.bean.QueryResult;
import cms.bean.staff.StaffLoginLog;
import cms.bean.staff.SysUsers;
import cms.bean.staff.SysUsersRoles;
import cms.service.besa.DAO;

/**
 * 系统设置
 * @author Administrator
 *
 */
public interface StaffService extends DAO{
	
	/**
	 * 得到用户权限
	 *@param userAccount 用户账号
	 *@return
	 */
	public List<GrantedAuthority> loadUserAuthoritiesByName(String userAccount);
	/**
	 * 得到所有权限
	 *@return
	 */
	public List<GrantedAuthority> loadAllAuthorities();
	/**
	 * 根据用户账号得到用户角色Id
	 *@param userAccount 用户账号
	 *@return
	 */
	public List<String> findRoleIdByUserAccount(String userAccount);
	/**
	 * 根据用户账号取得权限Id
	 *@param userAccount 用户账号
	 *@return
	 */
	public List<String> findPermissionIdByUserAccount(String userAccount);
	/**
	 * 根据员工账号返回SysUsers实例对象。
	 *@param account 员工账号，比如admin等。
	 *@return SysUsers实例对象。
	 */
	public SysUsers findByUserAccount(String account);
	
	/**
	 * 根据员工呢称返回SysUsers实例对象。
	 *@param nickname 呢称
	 *@return SysUsers实例对象。
	 */
	public SysUsers findByNickname(String nickname);
	/**
	 * 根据员工名称查询员工安全摘要
	 * @param userName 用户名称
	 */
	public String findSecurityDigestByStaffName(String staffName);
	/**
	 * 保存员工
	 * @param sysUsers 用户
	 * @param sysUsersRoleList 用户角色
	 */
	public void saveUser(SysUsers sysUsers, Set<SysUsersRoles> sysUsersRoleList);
	/**
	 * 修改员工
	 * @param sysUsers 用户
	 * @param usersRoleList 用户角色
	 */
	public void updateUser(SysUsers sysUsers, Set<SysUsersRoles> usersRoleList);
	/**
	 * 修改员工
	 * @param sysUsers 用户
	 */
	public void updateUser(SysUsers sysUsers);
	/**
	 * 删除员工
	 * @param staffId 员工Id
	 * @param userAccount 用户账号
	 */
	public void deleteUser(String staffId,String userAccount);
	
	
	/**
	 * 保存员工登录日志
	 * 先由staffLoginLogManage.createStaffLoginLogObject();方法生成对象再保存
	 * @param staffLoginLog 员工登录日志
	 */
	public void saveStaffLoginLog(Object staffLoginLog);
	
	
	/**
	 * 员工登录日志分页
	 * @param staffId 员工Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	public QueryResult<StaffLoginLog> findStaffLoginLogPage(String staffId,int firstIndex, int maxResult);
	
	
}
