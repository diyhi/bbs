package cms.service.staff;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cms.bean.PermissionObject;
import cms.bean.QueryResult;
import cms.bean.SaveResourcesObject;
import cms.bean.staff.PermissionMenu;
import cms.bean.staff.SysPermission;
import cms.bean.staff.SysPermissionResources;
import cms.bean.staff.SysResources;
import cms.bean.staff.SysRoles;
import cms.bean.staff.SysRolesPermission;
import cms.service.besa.DAO;

/**
 * ACL管理
 * @author Administrator
 *
 */
public interface ACLService extends DAO{
	
	
	/**
	 * 取得所有模块的权限
	 * @return
	 */
	public List<PermissionObject> findModulePermission();
	/**
	 * 根据资源表Id查询权限
	 * @param resourcesId 资源表Id
	 * @return
	 */
	public List<SysPermission> findPermissionByResourcesId(String resourcesId);
	/**
	 * 根据资源Id集合取得资源
	 * @param sysResourcesIdList 资源Id集合
	 * @return
	 */
	public List<SysResources> findSysResourcesBysysResourcesId(List<String> sysResourcesIdList);
	/**
	 * 根据权限Id集合取得权限菜单
	 */
	public List<PermissionMenu> findPermissionMenuByPermissionIdList(List<String> permissionIdList);
	/**
	 * 获取所有权限菜单
	 */
	public List<PermissionMenu> findAllPermissionMenu();
	
	/******************* SysPermissionDao权限 *********************/
	/**
	 * 得到权限列表。
	 */
	public List<SysPermission> findPermissionList();
	
	
	/******************* SysResourcesDao资源 *********************/
	/**
	 * 保存资源权限
	 * @param sysResourcesList 资源对象
	 */
	public void saveResources(List<SaveResourcesObject> sysResourcesList);
	/**
	 * 修改资源权限
	 * @param sysResources 资源
	 * @param sysPermission_NULL 权限NULL方式
	 * @param sysPermission_GET 权限GET方式
	 * @param sysPermission_POST 权限POST方式
	 * @param sysPermissionResources_NULL 权限资源NULL方式
	 * @param sysPermissionResources_GET 权限资源GET方式
	 * @param sysPermissionResources_POST 权限资源POST方式
	 * @param allOldPermissionId 所有旧权限Id
	 * @param delete_resourcesId 权限资源的资源Id集合
	 * @param resourcesObjectList 附加资源对象
	 */
	public void updateResources(SysResources sysResources,SysPermission sysPermission_NULL,
			SysPermission sysPermission_GET,SysPermission sysPermission_POST,
			SysPermissionResources sysPermissionResources_NULL,SysPermissionResources sysPermissionResources_GET,SysPermissionResources sysPermissionResources_POST,
			List<String> allOldPermissionId,List<String> delete_resourcesId, List<SaveResourcesObject> resourcesObjectList);
	/**
	 * 删除资源
	 * @param resourcesId 资源Id
	 * @param permissionIdList 权限Id集合
	 * @param delete_resourcesId 权限资源的资源Id集合
	 */
	public void deleteResources(String resourcesId,List<String> permissionIdList,List<String> delete_resourcesId);
	/**
	 * 得到资源列表
	 */
	public List<SysResources> getResourcesList();

	/**
	 * 模块分页显示
	 */
	public QueryResult<String> modulePage(int firstindex, int maxresult);
	/**
	 * 根据附加URL所属父ID查询资源
	 */
	public List<SysResources> findResourcesByUrlParentId(String urlParentId);
	/******************* SysRolesDao角色 *********************/
	
	/**
	 * 根据 用户账号查询角色名称
	 * @param userAccount 用户账号
	 */
	public Map<String,List<String>> findRolesByUserAccount(List<String> userAccountList);
	/**
	 * 根据角色Id查询权限
	 * @param rolesId 角色Id
	 */
	public List<SysPermission> findPermissionByRolesId(String rolesId);
	/**
	 * 得到角色列表
	 */
	public List<SysRoles> findRolesList();
	/**
	 * 保存角色
	 * @param sysResources 角色
	 * @param sysRolesPermissionList 角色权限集合
	 */
	public void saveRoles(SysRoles sysRoles,List<SysRolesPermission> sysRolesPermissionList);
	/**
	 * 修改角色
	 * @param sysResources 角色
	 * @param rolesPermissionList 角色权限集合
	 */
	public void updateRoles(SysRoles sysRoles,List<SysRolesPermission> rolesPermissionList);
	/**
	 * 删除角色
	 * @param roleId 角色Id
	 */
	public void deleteRoles(String roleId);
	
}
