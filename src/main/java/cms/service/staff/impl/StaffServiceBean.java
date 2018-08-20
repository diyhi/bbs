package cms.service.staff.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.Query;

import cms.bean.QueryResult;
import cms.bean.staff.StaffLoginLog;
import cms.bean.staff.SysPermission;
import cms.bean.staff.SysUsers;
import cms.bean.staff.SysUsersRoles;
import cms.service.besa.DaoSupport;
import cms.service.staff.ACLService;
import cms.service.staff.StaffService;
import cms.web.action.staff.StaffLoginLogConfig;
import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工管理
 * @author Administrator
 *
 */
@Service
@Transactional
public class StaffServiceBean extends DaoSupport implements StaffService {
	 private static final Logger logger = LogManager.getLogger(StaffServiceBean.class);
	
	@Resource ACLService aclService;//通过接口引用代理返回的对象
	@Resource StaffLoginLogConfig staffLoginLogConfig;
	
	
	/**
	 * 得到用户权限
	 *@param userAccount 用户账号
	 *@return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<GrantedAuthority> loadUserAuthoritiesByName(String userAccount){
	
		try {
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			
			List<String> authorities = loadUserAuthorities(userAccount);
			if(authorities != null && authorities.size() >0){
				for (String roleName : authorities) {
					auths.add(new SimpleGrantedAuthority(roleName));  
				}
			}
			return auths;
		} catch (RuntimeException re) {
			if (logger.isErrorEnabled()) {
	            logger.error("得到用户权限",re);
	        }
			throw re;
		}
	}
	
	/**
	 * 得到所有权限
	 *@return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<GrantedAuthority> loadAllAuthorities(){
		try {
			List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
			List<SysPermission> sysPermissionList = aclService.findPermissionList();
			if(sysPermissionList != null && sysPermissionList.size() >0){
				for (SysPermission permission : sysPermissionList) {
					auths.add(new SimpleGrantedAuthority(permission.getName()));  
				}
			}
			return auths;
		} catch (RuntimeException re) {
			if (logger.isErrorEnabled()) {
	            logger.error("得到所有权限",re);
	        }
			throw re;
		}
	}

	/**
	 * 根据用户名称得到用户角色Id
	 *@param userAccount 用户账号
	 *@return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<String> findRoleIdByUserAccount(String userAccount){
		Query query = em.createQuery("" +
				"select o.roleId from SysUsersRoles o where o.userAccount=?1");
				query.setParameter(1, userAccount);
		return query.getResultList();	
	}
	/**
	 * 根据用户账号取得权限名称
	 *@param userAccount 用户账号
	 *@return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	private List<String> loadUserAuthorities(String userAccount) {
		
		Query query = em.createQuery("" +
				"select a.name from SysPermission a,SysRolesPermission b," +
				"SysUsersRoles c where a.id = b.permissionId and " +
				"b.roleId = c.roleId and c.userAccount = ?1");
				query.setParameter(1, userAccount);
		return query.getResultList();	
	}
	/**
	 * 根据用户账号取得权限Id
	 *@param userAccount 用户账号
	 *@return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<String> findPermissionIdByUserAccount(String userAccount){
		Query query = em.createQuery("" +
				"select a.permissionId from SysRolesPermission a," +
				"SysUsersRoles b where  " +
				"a.roleId = b.roleId and b.userAccount= ?1");
				query.setParameter(1, userAccount);
		return query.getResultList();					
	}
	/**
	 * 根据用户账号返回SysUsers实例对象。
	 *@param userAccount 用户账号，比如admin等。
	 *@return SysUsers实例对象。
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public SysUsers findByUserAccount(String userAccount){
		Query query = em.createQuery("" +
				"select o from SysUsers o where o.userAccount=?1");
				query.setParameter(1, userAccount);
		List<SysUsers> list = query.getResultList();
		SysUsers sysUsers = null;
		for(SysUsers su :list){
			sysUsers = su;
		}
		return sysUsers;
	}
	
	/**
	 * 根据员工名称查询员工安全摘要
	 * @param userName 用户名称
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public String findSecurityDigestByStaffName(String staffName){
		Query query =  em.createQuery("select o.securityDigest from SysUsers o where o.userAccount=?1");
		//设置参数 
		query.setParameter(1, staffName);
		List<String> securityDigestList = query.getResultList();
		if(securityDigestList != null && securityDigestList.size() >0){
			for(String securityDigest : securityDigestList){
				return securityDigest;
			}
		}
		return null;
	}
	
	
	
	/**
	 * 保存员工
	 * @param sysUsers 用户
	 * @param sysUsersRoleList 用户角色
	 */
	public void saveUser(SysUsers sysUsers, Set<SysUsersRoles> sysUsersRoleList){
		if(sysUsersRoleList != null && sysUsersRoleList.size() >0){
			for(SysUsersRoles sysUsersRoles : sysUsersRoleList){
				this.save(sysUsersRoles);
			}
		}
		
		this.save(sysUsers);	
	}
	
	/**
	 * 删除员工角色
	 * @param userAccount 用户账号
	 */
	private void deleteUsersRoles(String userAccount){
		if(userAccount != null && !"".equals(userAccount)){
			Query query = em.createQuery("delete from SysUsersRoles o where o.userAccount=?1")
			.setParameter(1,userAccount);
			query.executeUpdate();
		}
	}

	/**
	 * 修改员工
	 * @param sysUsers 用户
	 * @param usersRoleList 用户角色
	 */
	public void updateUser(SysUsers sysUsers, Set<SysUsersRoles> usersRoleList){
		//修改员工
		this.update(sysUsers);	
		//删除用户角色
		deleteUsersRoles(sysUsers.getUserAccount());
		//保存用户角色
		if(usersRoleList != null && usersRoleList.size() >0){
			for(SysUsersRoles sysUsersRoles : usersRoleList){
				this.save(sysUsersRoles);
			}
		}
			
	}
	/**
	 * 删除员工
	 * @param staffId 员工Id
	 * @param userAccount 用户账号
	 */
	public void deleteUser(String staffId,String userAccount){
		if(userAccount != null && !"".equals(userAccount)){
			Query query = em.createQuery("delete from SysUsers o where o.userAccount=?1")
			.setParameter(1,userAccount);
			query.executeUpdate();
			//删除员工角色
			this.deleteUsersRoles(userAccount);
			
			//删除员工日志
			this.deleteStaffLoginLog(staffId);
		}
	}
	
	
	/**----------------------------------- 员工登录日志 -------------------------------------**/
	
	
	/**
	 * 保存员工登录日志
	 * 先由staffLoginLogManage.createStaffLoginLogObject();方法生成对象再保存
	 * @param staffLoginLog 员工登录日志
	 */
	public void saveStaffLoginLog(Object staffLoginLog){
		this.save(staffLoginLog);	
	}
	/**
	 * 删除员工登录日志
	 * @param staffId 员工Id
	 */
	private Integer deleteStaffLoginLog(String staffId){
		
		int j = 0;
		int staffLoginLog_tableNumber = staffLoginLogConfig.getTableQuantity();
		for(int i = 0; i<staffLoginLog_tableNumber; i++){
			if(i == 0){//默认对象
				//删除积分日志
				Query query_staffLoginLog = em.createQuery("delete from StaffLoginLog o where o.staffId =?1")
						.setParameter(1, staffId);
				j += query_staffLoginLog.executeUpdate();
			}else{//带下划线对象
				Query query_staffLoginLog = em.createQuery("delete from StaffLoginLog_"+i+" o where o.staffId =?1")
						.setParameter(1, staffId);
				j += query_staffLoginLog.executeUpdate();
			}
		}
		return j;
	}
	
	/**
	 * 员工登录日志分页
	 * @param staffId 员工Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<StaffLoginLog> findStaffLoginLogPage(String staffId,int firstIndex, int maxResult){
		
		QueryResult<StaffLoginLog> qr = new QueryResult<StaffLoginLog>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = staffLoginLogConfig.staffIdRemainder(staffId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from StaffLoginLog o where o.staffId=?1 ORDER BY o.logonTime desc")
			.setParameter(1, staffId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<StaffLoginLog> staffLoginLogList= query.getResultList();
			qr.setResultlist(staffLoginLogList);

			query = em.createQuery("select count(o) from StaffLoginLog o where o.staffId=?1")
					.setParameter(1, staffId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from StaffLoginLog_"+tableNumber+" o where o.staffId=?1 ORDER BY o.logonTime desc")
			.setParameter(1, staffId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> staffLoginLog_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.staff.StaffLoginLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),StaffLoginLog.class, false); 
				List<StaffLoginLog> staffLoginLogList= new ArrayList<StaffLoginLog>();
				for(int j = 0;j< staffLoginLog_List.size(); j++) {  
					Object obj = staffLoginLog_List.get(j);
					StaffLoginLog staffLoginLog = new StaffLoginLog();
					copier.copy(obj,staffLoginLog, null);
					staffLoginLogList.add(staffLoginLog);
				}
				qr.setResultlist(staffLoginLogList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("员工登录日志分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("员工登录日志分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("员工登录日志分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from StaffLoginLog_"+tableNumber+" o where o.staffId=?1")
					.setParameter(1, staffId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
	
}
