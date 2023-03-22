package cms.web.action.staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import cms.bean.PageForm;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.staff.SysRoles;
import cms.bean.staff.SysUsers;
import cms.bean.staff.SysUsersRoles;
import cms.service.staff.ACLService;
import cms.service.staff.StaffService;
import cms.utils.JsonUtils;
import cms.utils.SHA;
import cms.utils.UUIDUtil;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.crypto.password.PasswordEncoder;
/**
 * 员工管理
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/control/staff/manage") 
public class StaffManageAction {
	@Resource StaffService staffService;//通过接口引用代理返回的对象
	@Resource ACLService aclService;//通过接口引用代理返回的对象

	@Resource StaffManage staffManage;
	//Spring security加密
	@Resource PasswordEncoder passwordEncoder;
	
	
	/**
	 * 员工管理 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=addStaff",method=RequestMethod.GET)
	public String addStaffUI(ModelMap model,SysUsers sysUsers
			) throws Exception {
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		
		
		List<String> roleIdList = new ArrayList<String>();
		String username = "";//用户名称	
		boolean issys = false;//是否是超级用户
		
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof UserDetails){
			username =((UserDetails)obj).getUsername();
			roleIdList = staffService.findRoleIdByUserAccount(username);
		}
		if(obj instanceof SysUsers){
			issys = ((SysUsers)obj).isIssys();
		}
		List<SysRoles> sysRolesList = aclService.findRolesList();
		if(sysRolesList != null && sysRolesList.size() >0){
			for(SysRoles sr : sysRolesList){
				if(roleIdList.contains(sr.getId())){
					sr.setLogonUserPermission(true);
				}
				if(issys){//如果登录用户是超级管理员
					sr.setLogonUserPermission(true);
				}
			}
		}
		
		
		returnValue.put("sysRolesList",sysRolesList);
		returnValue.put("isSysAdmin",issys);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	/**
	 * 员工管理 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=addStaff",method=RequestMethod.POST)
	public String addStaff(ModelMap model,SysUsers formbean,String[] sysRolesId,String repeatPassword
			) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		
		SysUsers sysUsers = new SysUsers();
		
		//账号
		if(formbean.getUserAccount() == null || "".equals(formbean.getUserAccount())){
			
			error.put("userAccount", "账号不能为空");
		}else{
			if(formbean.getUserAccount().trim().length() >30){
				error.put("userAccount", "不能超过30个字符");
			}
			SysUsers su = (SysUsers)staffService.findByUserAccount(formbean.getUserAccount().trim());
			if(su != null){
				error.put("userAccount", "该账号已存在");
			}
		}
		
		String sha256_password = "";
		
		if(repeatPassword != null && !"".equals(repeatPassword.trim())){
			
			//密码
			if(formbean.getUserPassword() != null && !"".equals(formbean.getUserPassword().trim())){
				if(!formbean.getUserPassword().trim().equals(repeatPassword)){
					error.put("userPassword", "两次输入密码不相同");
				}else{
					sha256_password = SHA.sha256Hex(formbean.getUserPassword().trim());
				}
			}else{
				error.put("userPassword", "密码不能为空");
			}
		}else{
			error.put("repeatPassword", "重复密码不能为空");
		}
		
		if(error.size() == 0){
			List<SysRoles> sysRolesList = aclService.findRolesList();
			
			//当前用户角色Id
			List<String> roleIdList = new ArrayList<String>();
			String username = "";//用户名称
			boolean issys = false;//是否是超级用户
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof UserDetails){
				username =((UserDetails)obj).getUsername();
				roleIdList = staffService.findRoleIdByUserAccount(username);
			}
			if(obj instanceof SysUsers){
				issys = ((SysUsers)obj).isIssys();
			}
			

			sysUsers.setUserId(UUIDUtil.getUUID32());//Id
			if(issys){//如果登录用户是超级管理员才接收设置超级管理员值
				sysUsers.setIssys(formbean.isIssys());//是否是超级用户
			}else{
				sysUsers.setIssys(false);
			}
		
			sysUsers.setUserAccount(formbean.getUserAccount() != null ? formbean.getUserAccount().trim() : "");//用户账号
			sysUsers.setFullName(formbean.getFullName());//姓名
			
			// 密码通过盐值加密以备存储入数据库
			String newPassword = passwordEncoder.encode(sha256_password);
			sysUsers.setUserPassword(newPassword);//密码
			sysUsers.setUserDesc(formbean.getUserDesc());//用户备注
			sysUsers.setUserDuty(formbean.getUserDuty());//职位
			sysUsers.setEnabled(formbean.isEnabled());//是否使用
			sysUsers.setUsername(sysUsers.getUserAccount());//账号
			sysUsers.setSecurityDigest(UUIDUtil.getUUID32());
			List<String> select_rolesIdList = new ArrayList<String>();//选中可用的角色ID

			if(issys){//如果是超级用户,则有可添加系统所有角色
				//如果输入用户不是超级管理员
				if(sysUsers.isIssys() == false){
					List<String> allSysRolesId  = new ArrayList<String>();//系统所有的角色ID
					if(sysRolesList != null && sysRolesList.size() >0){
						for(SysRoles sr : sysRolesList){
							allSysRolesId.add(sr.getId());
						}
					}
					if(sysRolesId != null && sysRolesId.length >0){
						for(String s : sysRolesId){
							if(allSysRolesId.contains(s)){//如果输入的角色ID属于系统的角色ID
								select_rolesIdList.add(s);
							}
						}
					}
				}
				
			}else{
				if(sysRolesId != null && sysRolesId.length >0){
					for(String s : sysRolesId){
						if(roleIdList.contains(s)){
							select_rolesIdList.add(s);
						}
					}
				}
			}
			
			Set<SysUsersRoles> userRoles = new HashSet<SysUsersRoles>();
			for (String rolesId : select_rolesIdList) {
				userRoles.add(new SysUsersRoles(sysUsers.getUserAccount(),rolesId));
			}
			
			staffService.saveUser(sysUsers, userRoles);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		

		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 员工管理 修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=editStaff",method=RequestMethod.GET)
	public String editStaffUI(ModelMap model,String userId
			) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		
		if(userId != null && !"".equals(userId.trim())){
			SysUsers sysUsers = (SysUsers)staffService.find(SysUsers.class, userId);
			if(sysUsers != null){
				//当前用户角色Id
				List<String> roleIdList = new ArrayList<String>();
				String username = "";//用户名称
				boolean issys = false;//是否是超级用户
				boolean isSysAdmin = false;//是否可以修改超级管理员权限
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				if(obj instanceof UserDetails){
					username =((UserDetails)obj).getUsername();
					roleIdList = staffService.findRoleIdByUserAccount(username);
					
				}
				if(obj instanceof SysUsers){
					issys = ((SysUsers)obj).isIssys();
					if(issys){
						isSysAdmin = true;
					}
				}
				
				//登录用户不能修改自己的超级管理员权限
				if(sysUsers.getUserAccount().equals(username)){
					isSysAdmin = false;
				}
				
				if(issys){//如果是超级用户,则有修改权限
					sysUsers.setLogonUserPermission(true);
				}else{//如果不是超级用户
					if(sysUsers.isIssys()){//非超级用户不能修改超级用户的资料
						sysUsers.setLogonUserPermission(false);
					}else{	
						//要修改的用户的权限ID
						List<String> userPermission = staffService.findPermissionIdByUserAccount(sysUsers.getUserAccount());
						//登录用户权限ID
						List<String> logonUserPermissionId = staffService.findPermissionIdByUserAccount(username);
						
						if(logonUserPermissionId != null && userPermission != null){
							//只允许修改自己的或比自己权限小的用户
							if(logonUserPermissionId.containsAll(userPermission)){//如果登录用户权限包含要修改的用户的权限
								sysUsers.setLogonUserPermission(true);
								
								if(logonUserPermissionId.size()==userPermission.size()){
									if(!sysUsers.getUserAccount().equals(username)){
										//如果权限相同不是修改自己
										sysUsers.setLogonUserPermission(false);
									}
								}
							}
						}
						
					}	
				}
				
				List<SysRoles> sysRolesList = aclService.findRolesList();	
				//选中用户角色Id
				List<String> select_roleIdList = staffService.findRoleIdByUserAccount(sysUsers.getUserAccount());
				if(sysRolesList != null && sysRolesList.size() >0){
					for(SysRoles sr : sysRolesList){
						if(roleIdList != null && roleIdList.size() >0 && roleIdList.contains(sr.getId())){
							sr.setLogonUserPermission(true);//当前登录用户权限是否拥有本权限
						}
						if(select_roleIdList != null && select_roleIdList.size() >0 && select_roleIdList.contains(sr.getId())){
							sr.setSelected(true);//是否选中
						}
						
						if(issys){//如果是超级用户,则有修改权限
							sr.setLogonUserPermission(true);
							
						}else{
							//不能修改自已的权限
							if(sysUsers.getUserAccount().equals(username)){
								sr.setLogonUserPermission(false);
							}
							//用户如果没有修改权限
							if(sysUsers.isLogonUserPermission() == false){
								sr.setLogonUserPermission(false);
							}
						}
						
						//如果要修改的用户是超级用户则显示全部选中
						if(sysUsers.isIssys()){//非超级用户不能修改超级用户的资料
							sr.setSelected(true);//是否选中
						}
						
					}
				}
				
				sysUsers.setPassword("");//密码不显示
				sysUsers.setUserPassword("");
				returnValue.put("sysRolesList",sysRolesList);
				returnValue.put("sysUsers",sysUsers);
				returnValue.put("isSysAdmin",isSysAdmin);
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
			}else{
				error.put("userId", "员工不存在");
			}
		}else{
			error.put("userId", "员工Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	/**
	 * 员工管理 修改
	 */
	@ResponseBody
	@RequestMapping(params="method=editStaff",method=RequestMethod.POST)
	public String editStaff(ModelMap model,String userId,SysUsers formbean,String[] sysRolesId,
			String repeatPassword,PageForm pageForm) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		if(userId != null && !"".equals(userId.trim())){
			SysUsers sysUsers = (SysUsers)staffService.find(SysUsers.class, userId);
			if(sysUsers != null){
				String sha256_password = "";
				boolean isSecurityDigest = false;//是否修改安全摘要
				
				
				if(formbean.getUserPassword() != null && !"".equals(formbean.getUserPassword().trim())){
					if(repeatPassword != null && !"".equals(repeatPassword.trim())){
						if(formbean.getUserPassword().equals(repeatPassword)){
							sha256_password = SHA.sha256Hex(formbean.getUserPassword().trim());
							isSecurityDigest = true;
						}else{
							error.put("userPassword", "两次输入密码不相同");
						}
					}else{
						error.put("userPassword", "重复密码不能为空");
					}
				}
				List<String> roleIdList = new ArrayList<String>();//登录用户角色Id
				String username = "";//用户名称
				boolean issys = false;//是否是超级用户
				boolean isSysAdmin = false;//是否可以修改超级管理员权限
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				if(obj instanceof SysUsers){
					issys = ((SysUsers)obj).isIssys();
					if(issys){
						isSysAdmin = true;
					}
				}
				if(obj instanceof UserDetails){
					username =((UserDetails)obj).getUsername();	
					if(!issys){//如果不是超级用户
						roleIdList = staffService.findRoleIdByUserAccount(username);
					}
					
				}
				
				//登录用户不能修改自己的超级管理员权限
				if(sysUsers.getUserAccount().equals(username)){
					isSysAdmin = false;
				}
				
				sysUsers.setUsername(sysUsers.getUserAccount());
				
				if(sha256_password != null && !"".equals(sha256_password)){
					// 密码通过盐值加密以备存储入数据库
					String newPassword = passwordEncoder.encode(sha256_password);
					sysUsers.setUserPassword(newPassword);
				}
				if(sysUsers.isEnabled() != formbean.isEnabled()){//如果修改是否允许使用
					isSecurityDigest = true;
				}
				sysUsers.setFullName(formbean.getFullName());	
				sysUsers.setUserDesc(formbean.getUserDesc());
				sysUsers.setUserDuty(formbean.getUserDuty());
				sysUsers.setEnabled(formbean.isEnabled());
				if(isSysAdmin){
					sysUsers.setIssys(formbean.isIssys());
				}
				
				
				List<String> select_rolesIdList  = new ArrayList<String>();//可用的角色ID
				
			
				if(issys){//如果是超级用户,则有修改权限
					sysUsers.setLogonUserPermission(true);
					//如果输入用户不是超级管理员
					if(sysUsers.isIssys() == false){	
						List<SysRoles> sysRolesList = aclService.findRolesList();
						List<String> allSysRolesId  = new ArrayList<String>();//系统所有的角色ID
						for(SysRoles sr : sysRolesList){
							allSysRolesId.add(sr.getId());
						}
						if(sysRolesId != null && sysRolesId.length >0){
							for(String s : sysRolesId){
								if(allSysRolesId.contains(s)){//如果输入的角色ID属于系统的角色ID
									select_rolesIdList.add(s);
								}
							}
						}
					}
				}else{
					if(sysUsers.isIssys() == true){
						error.put("permission", "非超级用户不能修改超级用户的资料");
					}else{//如果输入用户不是超级管理员
						//不能修改自已的权限
						if(sysUsers.getUserAccount().equals(username)){
							if(roleIdList != null && roleIdList.size() >0){
								for(String s : roleIdList){
									select_rolesIdList.add(s);
								}
							}
						}else{
							if(sysRolesId != null && sysRolesId.length >0){
								for(String s : sysRolesId){
									
									if(roleIdList.contains(s)){//如果输入的角色ID属于系统的角色ID
										select_rolesIdList.add(s);
									}
								}
							}
							
							//要修改的用户的权限ID
							List<String> userPermission = staffService.findPermissionIdByUserAccount(sysUsers.getUserAccount());
							//登录用户权限ID
							List<String> logonUserPermissionId = staffService.findPermissionIdByUserAccount(username);
			
							if(logonUserPermissionId != null && userPermission != null){
								//只允许修改自己的或比自己权限小的用户
								if(logonUserPermissionId.containsAll(userPermission)){//如果登录用户权限包含要修改的用户的权限
									sysUsers.setLogonUserPermission(true);
									if(logonUserPermissionId.size()==userPermission.size()){//如果登录用户权限与要修改的用户的权限相同
										if(!sysUsers.getUserAccount().equals(username)){
											//如果权限相同不是修改自己
											error.put("permission", "账号权限不足");
											sysUsers.setLogonUserPermission(false);
										}
										
									}
									
								}else{
									error.put("permission", "账号权限不足");
								}
							}
						}
					}
				}

				Set<SysUsersRoles> userRoles = new HashSet<SysUsersRoles>();
				for (String rolesId : select_rolesIdList) {
					userRoles.add(new SysUsersRoles(sysUsers.getUserAccount(),rolesId));
				}
				
				if(error.size() == 0){
					if(isSecurityDigest){
						sysUsers.setSecurityDigest(UUIDUtil.getUUID32());
					}
					staffService.updateUser(sysUsers, userRoles);
					staffManage.delete_staffSecurityDigest(sysUsers.getUserAccount());
				}
			}else{
				error.put("userId", "员工不存在");
			}
		}else{
			error.put("userId", "员工Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 员工管理 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=deleteStaff",method=RequestMethod.POST)
	public String deleteStaff(ModelMap model,String userId
			) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		if(userId != null && !"".equals(userId.trim())){
			SysUsers sysUsers = (SysUsers)staffService.find(SysUsers.class, userId);//要删除的用户
			
			
			String username = "";//用户名称
			boolean issys = false;//是否是超级用户
			
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if(obj instanceof SysUsers){
				issys = ((SysUsers)obj).isIssys();
			}
			if(obj instanceof UserDetails){
				username =((UserDetails)obj).getUsername();	
			}
			
			//登录用户不能删除自己
			if(sysUsers.getUserAccount().equals(username)){
				error.put("staff", "不能删除自己");
			}

			if(sysUsers.isIssys()){//非超级用户不能删除超级用户的资料
				if(!issys){
					error.put("staff", "账号权限不足，不能删除超级管理员");
				}
			}
			
			if(!issys){//如果不是超级用户
				//要修改的用户的权限ID
				List<String> userPermission = staffService.findPermissionIdByUserAccount(sysUsers.getUserAccount());
				//登录用户权限ID
				List<String> logonUserPermissionId = staffService.findPermissionIdByUserAccount(username);
				if(logonUserPermissionId != null && userPermission != null){
					//只允许删除比自己权限小的用户
					if(logonUserPermissionId.containsAll(userPermission)){//如果登录用户权限包含要修改的用户的权限
						if(logonUserPermissionId.size()==userPermission.size()){//如果登录用户权限与要修改的用户的权限相同
							error.put("staff", "不能删除相同权限的用户");
						}		
					}else{
						error.put("staff", "账号权限不足");
					}
				}
				
			}
			if(error.size() == 0){
				staffService.deleteUser(sysUsers.getUserId(),sysUsers.getUserAccount());
				staffManage.delete_staffSecurityDigest(sysUsers.getUserAccount());
			}
			
		}else{
			error.put("staff", "Id不存在");
		}

		if(error.size()==0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
}
