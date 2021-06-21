package cms.service.user.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.user.UserRole;
import cms.bean.user.UserRoleGroup;
import cms.service.besa.DaoSupport;
import cms.service.user.UserRoleService;

/**
 * 用户角色实现
 *
 */
@Service
@Transactional
public class UserRoleServiceBean extends DaoSupport<UserRole> implements
		UserRoleService {

	/**
	 * 根据Id查询角色
	 * @param userRoleId 角色Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UserRole findRoleById(String userRoleId){
		Query query =  em.createQuery("select o from UserRole o where o.id=?1");
		query.setParameter(1, userRoleId);
		List<UserRole> userRoleList = query.getResultList();
		if(userRoleList != null && userRoleList.size() >0){
			for(UserRole userRole : userRoleList){
				return userRole;
			}
		}
		return null;
	}

	
	/**
	 * 查询所有角色
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<UserRole> findAllRole(){
		Query query =  em.createQuery("select o from UserRole o ORDER BY o.defaultRole DESC, o.sort DESC");
		return query.getResultList();
	}
	/**
	 * 查询所有角色 - 缓存
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="userRoleServiceBean_cache",key="'findAllRole_default'")
	public List<UserRole> findAllRole_cache(){
		return this.findAllRole();
		
	}
	
	/**
	 * 保存用户角色
	 * @param userRole 用户角色
	 */
	@CacheEvict(value="userRoleServiceBean_cache",allEntries=true)
	public void saveUserRole(UserRole userRole){
		this.save(userRole);
	}
	
	/**
	 * 修改用户角色
	 * @param userRole 用户角色
	 */
	@CacheEvict(value="userRoleServiceBean_cache",allEntries=true)
	public Integer updateUserRole(UserRole userRole){
		Query query = em.createQuery("update UserRole o set o.name=?1,o.sort=?2, o.remark=?3,o.userResourceFormat=?4 where o.id=?5")
			.setParameter(1, userRole.getName())
			.setParameter(2, userRole.getSort())
			.setParameter(3, userRole.getRemark())
			.setParameter(4, userRole.getUserResourceFormat())
			.setParameter(5, userRole.getId());
		int i = query.executeUpdate();
		return i;
				
			
	}
	/**
	 * 删除用户角色
	 * @param userRoleId 用户角色Id
	 */
	@CacheEvict(value="userRoleServiceBean_cache",allEntries=true)
	public int deleteUserRole(String userRoleId){
		Query delete = em.createQuery("delete from UserRole o where o.id=?1")
			.setParameter(1, userRoleId);
		return delete.executeUpdate();
	}
	
	/**
	 * 设置为默认角色
	 * @param userRoleId 用户角色Id
	 * @param defaultRole 是否设置为默认角色
	 */
	@CacheEvict(value="userRoleServiceBean_cache",allEntries=true)
	public Integer setAsDefaultRole(String userRoleId,Boolean defaultRole){
		int i = 0;
		Query query = em.createQuery("update UserRole o set o.defaultRole=?1 ");
		//给SQL语句设置参数
		query.setParameter(1, false);
		i = query.executeUpdate();
		
		if(defaultRole != null && defaultRole){//设置默认角色
			query = em.createQuery("update UserRole o set o.defaultRole=?1 where o.id=?2");
			//给SQL语句设置参数
			query.setParameter(1, true);
			query.setParameter(2, userRoleId);
			i += query.executeUpdate();
			
		}
		return i;
	}
	
	/**---------------------------------------------------- 角色组 -----------------------------------------------------**/
	/**
	 * 根据角色Id查询角色组
	 * @param userRoleId 角色Id
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UserRoleGroup findRoleGroupByUserRoleId(String userRoleId,String userName){
		Query query =  em.createQuery("select o from UserRoleGroup o where o.userRoleId=?1 and o.userName=?2");
		query.setParameter(1, userRoleId);
		query.setParameter(2, userName);
		List<UserRoleGroup> userRoleGroupList = query.getResultList();
		if(userRoleGroupList != null && userRoleGroupList.size() >0){
			for(UserRoleGroup userRoleGroup : userRoleGroupList){
				return userRoleGroup;
			}
		}
		return null;
	}
	
	
	/**
	 * 根据用户名称查询角色组
	 * @param userName 用户名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<UserRoleGroup> findRoleGroupByUserName(String userName){
		Query query =  em.createQuery("select o from UserRoleGroup o where o.userName=?1");
		query.setParameter(1, userName);
		List<UserRoleGroup> userRoleGroupList = query.getResultList();
		return userRoleGroupList;
	}
	
	/**
	 * 保存用户角色组
	 * @param userRoleGroupList 用户角色组集合
	 */
	public void saveUserRoleGroup(List<UserRoleGroup> userRoleGroupList){
		for(UserRoleGroup userRoleGroup : userRoleGroupList){
			this.save(userRoleGroup);
		}
	}
	
	/**
	 * 修改用户角色组
	 * @param userName 用户名称
	 * @param userRoleGroupList 用户角色组集合
	 */
	public void updateUserRoleGroup(String userName,List<UserRoleGroup> userRoleGroupList){
		List<String> userNameList = new ArrayList<String>();
		userNameList.add(userName);
		this.deleteUserRoleGroup(userNameList);
		if(userRoleGroupList != null && userRoleGroupList.size() >0){
			this.saveUserRoleGroup(userRoleGroupList);
		}
		
	}
	
	/**
	 * 修改用户角色组
	 * @param userRoleId 角色Id
	 * @param userName 用户名称
	 * @param validPeriodEnd 有效期结束
	 */
	public Integer updateUserRoleGroup(String userRoleId,String userName,Date validPeriodEnd){
		Query query = em.createQuery("update UserRoleGroup o set o.validPeriodEnd=?1 where o.userRoleId=?2 and o.userName=?3")
				.setParameter(1, validPeriodEnd)
				.setParameter(2, userRoleId)
				.setParameter(3, userName);
		return query.executeUpdate();
	}
	
	/**
	 * 删除用户角色组
	 * @param userNameList 用户名称集合
	 */
	public int deleteUserRoleGroup(List<String> userNameList){
		Query delete = em.createQuery("delete from UserRoleGroup o where o.userName in(:userName)")
			.setParameter("userName", userNameList);
		return delete.executeUpdate();
	}
	
}
