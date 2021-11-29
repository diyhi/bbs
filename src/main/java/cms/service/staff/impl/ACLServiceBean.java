package cms.service.staff.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import cms.bean.PermissionObject;
import cms.bean.QueryResult;
import cms.bean.SaveResourcesObject;
import cms.bean.staff.SysPermission;
import cms.bean.staff.SysPermissionResources;
import cms.bean.staff.SysResources;
import cms.bean.staff.SysRoles;
import cms.bean.staff.SysRolesPermission;
import cms.service.besa.DaoSupport;
import cms.service.staff.ACLService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * ACL管理
 * @author Administrator
 *
 */
@Service
@Transactional
public class ACLServiceBean extends DaoSupport implements ACLService {
	
	/**
	 * 取得所有模块的权限
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<PermissionObject> findModulePermission(){
		Query query = em.createQuery("" +
			"select b.url, c.methods,c.name,b.module,b.urlParentId,c.id,c.remarks from SysPermissionResources a,SysResources b," +
			"SysPermission c where a.resourceId = b.id and " +
			"a.permissionId = c.id ORDER BY b.priority ASC , c.priority ASC");
		query.getResultList();
		List<PermissionObject> permissionObjectList = new ArrayList<PermissionObject>();
		Iterator iter = query.getResultList().iterator(); 
		while (iter.hasNext()){ 
			Object[] o = (Object[]) iter.next(); 
			PermissionObject permissionObject = new PermissionObject();
			String url = (String) o[0]; 
			String methods = (String) o[1]; 
			String permissionName = (String) o[2]; 
			String module = (String) o[3]; 
			String urlParentId = (String) o[4];  
			String permissionId = (String) o[5]; 
			String remarks = (String) o[6];  
			permissionObject.setUrl(url);
			permissionObject.setMethods(methods);
			permissionObject.setPermissionName(permissionName);
			permissionObject.setModule(module);
			if(urlParentId != null && !"".equals(urlParentId.trim())){
				permissionObject.setAppendUrl(true);
			}else{
				permissionObject.setAppendUrl(false);
			}
			
			permissionObject.setPermissionId(permissionId);
			permissionObject.setRemarks(remarks);
			
			permissionObjectList.add(permissionObject);
		}
		return permissionObjectList;

	}
	/**
	 * 根据资源表Id查询权限
	 * @param resourcesId 资源表Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SysPermission> findPermissionByResourcesId(String resourcesId){
		Query query = em.createQuery("select b from SysPermissionResources a," +
			"SysPermission b where a.permissionId = b.id and " +
			"a.resourceId = ?1");
		query.setParameter(1, resourcesId);	
		return query.getResultList();
	}
	
	/**
	 * 根据资源Id集合取得资源
	 * @param sysResourcesIdList 资源Id集合
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SysResources> findSysResourcesBysysResourcesId(List<String> sysResourcesIdList){
		Query query = em.createQuery("select o from SysResources o where o.id in(:sysResourcesId)");
		query.setParameter("sysResourcesId", sysResourcesIdList);	
		return query.getResultList();
	}
	
	
	/******************* SysPermissionDao权限 *********************/
	/**
	 * 得到权限列表。
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SysPermission> findPermissionList() {
		Query query = em.createQuery("select o from SysPermission o ");
		return query.getResultList();
	}
	
	
	/******************* SysResourcesDao资源 *********************/

	/**
	 * 保存资源
	 * @param sysResourcesList 资源对象
	 */
	public void saveResources(List<SaveResourcesObject> sysResourcesList){	
		for(SaveResourcesObject saveResourcesObject: sysResourcesList){
			//保存权限
			if(saveResourcesObject.getSysPermission() != null){
				this.save(saveResourcesObject.getSysPermission());
			}
			//保存资源
			if(saveResourcesObject.getSysResources() != null){
				this.save(saveResourcesObject.getSysResources());
			}
			//权限资源表
			if(saveResourcesObject.getSysPermissionResources() != null){
				this.save(saveResourcesObject.getSysPermissionResources());
			}
			
		}
	}

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
			List<String> allOldPermissionId,List<String> delete_resourcesId, List<SaveResourcesObject> resourcesObjectList){	
		//修改资源
		this.update(sysResources);
		//修改权限
		if(sysPermission_NULL != null){
			this.update(sysPermission_NULL);
			allOldPermissionId.remove(sysPermission_NULL.getId());
		}
		if(sysPermission_GET != null){
			this.update(sysPermission_GET);
			
			allOldPermissionId.remove(sysPermission_GET.getId());
			
			
		}
		if(sysPermission_POST != null){
			this.update(sysPermission_POST);
			allOldPermissionId.remove(sysPermission_POST.getId());
		}
		//删除权限
		deletePermission(allOldPermissionId);
		
		//删除所有权限资源
		deletePermissionResources(delete_resourcesId);
		
		//添加权限资源
		if(sysPermissionResources_NULL != null){
			this.save(sysPermissionResources_NULL);
		}
		if(sysPermissionResources_GET != null){
			this.save(sysPermissionResources_GET);
		}
		if(sysPermissionResources_POST != null){
			this.save(sysPermissionResources_POST);
		}
		
		//删除附加资源
		deleteAppendResources(sysResources.getId());
		
		//添加附加资源
		if(resourcesObjectList != null && resourcesObjectList.size() >0){
			for(SaveResourcesObject saveResourcesObject :resourcesObjectList){
				//保存资源
				if(saveResourcesObject.getSysResources() != null){
					this.save(saveResourcesObject.getSysResources());
				}
				//权限资源表
				if(saveResourcesObject.getSysPermissionResources() != null){
					this.save(saveResourcesObject.getSysPermissionResources());
				}
			}
		}
	}
	
	//删除附加资源
	private void deleteAppendResources(String urlParentId){
		if(urlParentId != null && !"".equals(urlParentId)){
			Query query = em.createQuery("delete from SysResources o where o.urlParentId=?1 ")
			.setParameter(1,urlParentId);
			query.executeUpdate();
		}
	}
	//删除权限资源
	private void deletePermissionResources(List<String> delete_resourcesId){
		if(delete_resourcesId != null && delete_resourcesId.size() >0){
			Query query = em.createQuery("delete from SysPermissionResources o where o.resourceId in(:resourcesId) ")
			.setParameter("resourcesId",delete_resourcesId);
			query.executeUpdate();
		}
	}
	//删除权限
	private void deletePermission(List<String> permissionIdList){
		if(permissionIdList != null && permissionIdList.size() >0){
			Query query = em.createQuery("delete from SysPermission o where o.id in(:permissionId)")
			.setParameter("permissionId",permissionIdList);
			query.executeUpdate();
		}
	}
	
	/**
	 * 删除资源
	 * @param resourcesId 资源Id
	 * @param permissionIdList 权限Id集合
	 * @param delete_resourcesId 权限资源的资源Id集合
	 */
	public void deleteResources(String resourcesId,List<String> permissionIdList,List<String> delete_resourcesId){
		//删除资源
		this.delete(SysResources.class, resourcesId);
		
		//删除附加资源
		deleteAppendResources(resourcesId);

		//删除权限
		deletePermission(permissionIdList);
		
		//删除所有权限资源
		deletePermissionResources(delete_resourcesId);
		
		

	}
	/**
	 * 得到资源列表
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SysResources> getResourcesList() {
		Query query = em.createQuery("select o from SysResources o ");
		return query.getResultList();
	}
	
	/**
	 * 模块分页显示
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<String> modulePage(int firstindex, int maxresult){
		QueryResult<String> qr = new QueryResult<String>();

		//JPA不支持from (select ……)方式的子查询   支持改为类似方式select * from test t where t.age = (select max(age) from test where t.class = class) order by class;  
		//select t1.module from (select * from sysresources o order by o.priority desc) t1 GROUP BY t1.module order by t1.priority asc
		
		Query query = em.createQuery("select o.module,MAX(o.priority) as priority from SysResources o GROUP BY o.module order by priority asc");
		
		
		
		if(firstindex != -1 && maxresult != -1){
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstindex);
			//获取多少条数据
			query.setMaxResults(maxresult);
		}
		List<String> resultList = new ArrayList<String>();
		List<Object[]> objList = query.getResultList();
		for(Object[] obj: objList){//只取第一项结果
			resultList.add((String)obj[0]);
		}
		//把查询结果设进去
		qr.setResultlist(resultList);
		
		//获取总记录数
		query = em.createQuery("select count(distinct o.module) from SysResources o");
		//因为统计返回的是一行一列的值,所以用getSingleResult()获取一行一列形式的值
		qr.setTotalrecord((Long)query.getSingleResult());
		return qr;
	}
	/**
	 * 根据附加URL所属父ID查询资源
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SysResources> findResourcesByUrlParentId(String urlParentId) {
		Query query = em.createQuery("select o from SysResources o where o.urlParentId=?1 ORDER BY o.priority ASC");
		query.setParameter(1, urlParentId);
		return query.getResultList();
	}
	
	/******************* SysRolesDao角色 *********************/
	
	/**
	 * 根据 用户账号查询角色名称
	 * @param userAccount 用户账号
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Map<String,List<String>> findRolesByUserAccount(List<String> userAccountList) {
		Query query = em.createQuery("select b.userAccount,a.name from SysRoles a,SysUsersRoles b where a.id=b.roleId and b.userAccount in(:userAccount)");
		query.setParameter("userAccount", userAccountList);
		List objectList = query.getResultList();
		
		//角色名称
		Map<String,List<String>> rolesNameList = new HashMap<String,List<String>>();
		if(objectList != null && objectList.size() >0){
			Iterator iter = objectList.iterator(); 
			while (iter.hasNext()){ 
				Object[] o = (Object[]) iter.next(); 
				String _userAccount = (String) o[0]; 
				String _rolesName = (String) o[1]; 
				
				List<String> rolesName = rolesNameList.get(_userAccount);
				if(rolesName != null){
					rolesName.add(_rolesName);
				}else{
					rolesName = new ArrayList<String>();
					rolesName.add(_rolesName);
				}
				rolesNameList.put(_userAccount, rolesName);
				
			}
		}
		
		
		return rolesNameList;
		
	}
	
	/**
	 * 根据角色Id查询权限
	 * @param rolesId 角色Id
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SysPermission> findPermissionByRolesId(String rolesId) {
		Query query = em.createQuery("select b from SysRolesPermission a,SysPermission b where a.permissionId=b.id and a.roleId=?1");
		query.setParameter(1, rolesId);
		return query.getResultList();
	}
	/**
	 * 得到角色列表
	 */
	public List<SysRoles> findRolesList(){
		Query query = em.createQuery("select o from SysRoles o ");
		return query.getResultList();
	}
	/**
	 * 保存角色
	 * @param sysResources 角色
	 * @param sysRolesPermissionList 角色权限集合
	 */
	public void saveRoles(SysRoles sysRoles,List<SysRolesPermission> sysRolesPermissionList){
		this.save(sysRoles);
		
		if(sysRolesPermissionList != null && sysRolesPermissionList.size() >0){
			for(SysRolesPermission sysRolesPermission : sysRolesPermissionList){
				this.save(sysRolesPermission);
			}
		}
	}
	
	
	/**
	 * 删除角色权限
	 * @param roleId 角色Id
	 */
	private void deleteRolesPermission(String roleId){
		if(roleId != null && !"".equals(roleId)){
			Query query = em.createQuery("delete from SysRolesPermission o where o.roleId=?1")
			.setParameter(1,roleId);
			query.executeUpdate();
		}
	}
	/**
	 * 修改角色
	 * @param sysResources 角色
	 * @param rolesPermissionList 角色权限集合
	 */
	public void updateRoles(SysRoles sysRoles,List<SysRolesPermission> rolesPermissionList){
		//修改角色
		this.update(sysRoles);
		//删除角色权限
		this.deleteRolesPermission(sysRoles.getId());
		//保存角色权限
		if(rolesPermissionList != null && rolesPermissionList.size() >0){
			for(SysRolesPermission rolesPermission :rolesPermissionList){
				this.save(rolesPermission);
			}
		}
	}
	
	/**
	 * 删除角色
	 * @param roleId 角色Id
	 */
	public void deleteRoles(String roleId){
		//删除角色
		this.delete(SysRoles.class, roleId);
		//删除角色权限
		this.deleteRolesPermission(roleId);
	}
	
	
}
