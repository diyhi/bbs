package cms.service.user;

import java.util.Date;
import java.util.List;


import cms.bean.user.UserRole;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DAO;

/**
 * 用户角色接口
 *
 */
public interface UserRoleService extends DAO<UserRole> {
	/**
	 * 根据Id查询角色
	 * @param userRoleId 角色Id
	 * @return
	 */
	public UserRole findRoleById(String userRoleId);
	
	/**
	 * 查询所有角色
	 */
	public List<UserRole> findAllRole();
	/**
	 * 查询所有角色 - 缓存
	 */
	public List<UserRole> findAllRole_cache();
	/**
	 * 保存用户角色
	 * @param userRole 用户角色
	 */
	public void saveUserRole(UserRole userRole);
	
	/**
	 * 修改用户角色
	 * @param userRole 用户角色
	 */
	public Integer updateUserRole(UserRole userRole);
	/**
	 * 删除用户角色
	 * @param userRoleId 用户角色Id
	 */
	public int deleteUserRole(String userRoleId);

	/**
	 * 设置为默认角色
	 * @param userRoleId 用户角色Id
	 * @param defaultRole 是否设置为默认角色
	 */
	public Integer setAsDefaultRole(String userRoleId,Boolean defaultRole);
	/**
	 * 根据角色Id查询角色组
	 * @param userRoleId 角色Id
	 * @param userName 用户名称
	 * @return
	 */
	public UserRoleGroup findRoleGroupByUserRoleId(String userRoleId,String userName);
	
	/**
	 * 根据用户名称查询角色组
	 * @param userName 用户名称
	 * @return
	 */
	public List<UserRoleGroup> findRoleGroupByUserName(String userName);
	
	/**
	 * 保存用户角色组
	 * @param userRoleGroupList 用户角色组集合
	 */
	public void saveUserRoleGroup(List<UserRoleGroup> userRoleGroupList);
	
	/**
	 * 修改用户角色组
	 * @param userName 用户名称
	 * @param userRoleGroupList 用户角色组集合
	 */
	public void updateUserRoleGroup(String userName,List<UserRoleGroup> userRoleGroupList);
	/**
	 * 修改用户角色组
	 * @param userRoleId 角色Id
	 * @param userName 用户名称
	 * @param validPeriodEnd 有效期结束
	 */
	public Integer updateUserRoleGroup(String userRoleId,String userName,Date validPeriodEnd);
	
	/**
	 * 删除用户角色组
	 * @param userNameList 用户名称集合
	 */
	public int deleteUserRoleGroup(List<String> userNameList);
}
