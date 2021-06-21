package cms.web.action.staff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PermissionObject;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.staff.SysPermission;
import cms.bean.staff.SysRoles;
import cms.bean.staff.SysRolesPermission;
import cms.bean.staff.SysUsers;
import cms.service.setting.SettingService;
import cms.service.staff.ACLService;
import cms.service.staff.StaffService;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ACL管理
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/control/acl/manage") 
public class ACLManageAction {
	@Resource StaffService staffService;//通过接口引用代理返回的对象
	@Resource ACLService aclService;//通过接口引用代理返回的对象
	@Resource SettingService settingService;
	

	
	/**-------------------------------------- 角色 ----------------------------------------**/
	
	
	/**
	 * 角色管理 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=addRoles",method=RequestMethod.GET)
	public String addRolesUI(ModelMap model,SysRoles sysRoles,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();//用户权限

		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		boolean issys = false;//是否是超级用户

		if(obj instanceof SysUsers){
			auths =((SysUsers)obj).getAuthorities();
			issys = ((SysUsers)obj).isIssys();
		}
		
		List<PermissionObject> modulePermissionList = aclService.findModulePermission();
		LinkedHashMap<String, List<PermissionObject>> permissionObjectMap = new LinkedHashMap<String, List<PermissionObject>>();
		if(modulePermissionList != null && modulePermissionList.size() >0){
			for (PermissionObject permissionObject : modulePermissionList) {
				//如果为系统最大权限则不可选
				if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
					continue;
				}
				
				//如果为附加URL则不可选
				if(permissionObject.isAppendUrl() == true){
					continue;
				}
					
				//自动勾选"系统-->后台框架"
				if("系统".equals(permissionObject.getModule())){
					permissionObject.setSelected(true);
				}

				List<PermissionObject> permissionObjectList = new ArrayList<PermissionObject>();
				if(permissionObjectMap.containsKey(permissionObject.getModule())){
					permissionObjectList.addAll(permissionObjectMap.get(permissionObject.getModule()));
				}
				if(auths != null && auths.size() >0){	
					for (GrantedAuthority ga:auths) {
						if(ga.getAuthority().equals(permissionObject.getPermissionName())){
							permissionObject.setLogonUserPermission(true);//当前用户可选择本权限
							break;
						}	
					}
				}
				if(issys){//如果是超级管理员
					permissionObject.setLogonUserPermission(true);//当前用户可选择本权限
				}
				
				permissionObjectList.add(permissionObject);
				permissionObjectMap.put(permissionObject.getModule(), permissionObjectList);		
			}
			
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,permissionObjectMap));
	}
	/**
	 * 角色管理 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=addRoles",method=RequestMethod.POST)
	public String addRoles(ModelMap model,SysRoles formbean,
			String[] sysPermissionId
			) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		
		if(formbean.getName() == null || "".equals(formbean.getName().trim())){
			error.put("name", "请填写角色名");
		}
		SysRoles sysRoles = new SysRoles();
		
		String username = "";//用户名称
		boolean issys = false;//是否是超级用户
		Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(obj instanceof SysUsers){
			issys = ((SysUsers)obj).isIssys();
			username =((SysUsers)obj).getUsername();
		}
		//系统模块权限Id
		String systemPermissionId = "";
		
		Set<String> permissionIdList = new HashSet<String>();//可用的权限ID
		List<SysRolesPermission> sysRolesPermissionList = new ArrayList<SysRolesPermission>();
		List<PermissionObject> modulePermissionList = null;
		if(username != null && !"".equals(username)){
			
			if(issys){
				modulePermissionList = aclService.findModulePermission();
				
				List<String> allSysPermissionId = new ArrayList<String>();//系统所有权限ID
				if(modulePermissionList != null && modulePermissionList.size() >0){
					for (PermissionObject permissionObject : modulePermissionList) {
						//如果为系统最大权限则不可选
						if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
							continue;
						}
				
						allSysPermissionId.add(permissionObject.getPermissionId());
						
					}
					if(sysPermissionId != null && sysPermissionId.length >0){
						for(String spi :sysPermissionId){
							
							if(allSysPermissionId.contains(spi)){
								permissionIdList.add(spi);
							}
						}
					}
				}
				
			}else{
				List<String> userPermissionId = staffService.findPermissionIdByUserAccount(username);
				if(userPermissionId != null && userPermissionId.size() >0){
					for(String s : userPermissionId){
						//如果为系统最大权限则不可选
						if("99999999999999999999999999999999".equals(s)){
							continue;
						}
						if(sysPermissionId != null && sysPermissionId.length >0){
							for(String spi :sysPermissionId){
								if(s.equals(spi)){
									permissionIdList.add(s);
								}
							}
						}
					}
				}
				
			}
			
			if(modulePermissionList == null){
				modulePermissionList = aclService.findModulePermission();
			}
			
			if(modulePermissionList != null && modulePermissionList.size() >0){
				for (PermissionObject permissionObject : modulePermissionList) {
					//如果为系统最大权限则不可选
					if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
						continue;
					}

					//自动勾选"系统-->后台框架"
					if("系统".equals(permissionObject.getModule())){
						systemPermissionId = permissionObject.getPermissionId();
					}
				}
			}
			//如果没选中"系统-->后台框架"，则自动选择
			if(!permissionIdList.contains(systemPermissionId)){
				error.put("permission", "必须选择【 系统-->后台框架】");
				
			}
			sysRoles.setId(UUIDUtil.getUUID32());
			sysRoles.setName(formbean.getName());
			sysRoles.setRemarks(formbean.getRemarks());
			
			
			if(permissionIdList != null && permissionIdList.size() >0){
				for (String permissionId : permissionIdList) {
					SysRolesPermission sysRolesPermission = new SysRolesPermission();
					sysRolesPermission.setPermissionId(permissionId);
					sysRolesPermission.setRoleId(sysRoles.getId());
					sysRolesPermissionList.add(sysRolesPermission);
				}
			}
		}else{
			error.put("permission", "没有权限");
		}
		
		if(error.size() == 0){
			aclService.saveRoles(sysRoles, sysRolesPermissionList);
		}

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 角色管理 修改页面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=editRoles",method=RequestMethod.GET)
	public String editRolesUI(ModelMap model,String rolesId
			) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
		
		if(rolesId != null && !"".equals(rolesId.trim())){
			Set<String> rolesPermissionName = new HashSet<String>();//要修改的角色权限name
			SysRoles roles = (SysRoles)aclService.find(SysRoles.class, rolesId);
			if(roles != null){
				List<SysPermission> sysPermissionList = aclService.findPermissionByRolesId(rolesId);
				if(sysPermissionList != null && sysPermissionList.size() >0){
					for(SysPermission sysPermission : sysPermissionList){
						rolesPermissionName.add(sysPermission.getName());
					}
				}
				
				Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();//用户权限
				Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
				boolean issys = false;//是否是超级用户
				boolean isPermissions = true;//当前登录用户是否包含本角色权限
				if(obj instanceof SysUsers){
					auths =((SysUsers)obj).getAuthorities();
					issys = ((SysUsers)obj).isIssys();
				}
				Set<String> userAuthority = new HashSet<String>();//用户权限
				if(auths != null && auths.size() >0){
					for(GrantedAuthority ga: auths){
						userAuthority.add(ga.getAuthority());
					}
				}
				if(!issys && userAuthority.containsAll(rolesPermissionName)){//如果用户权限包含角色权限
					if(userAuthority.size() == rolesPermissionName.size()){//当前登录用户不能修改和自己相同权限的角色
						isPermissions = false;	
					}else{
						isPermissions = true;
					}
				}
				
				List<PermissionObject> modulePermissionList = aclService.findModulePermission();
				LinkedHashMap<String, List<PermissionObject>> permissionObjectMap = new LinkedHashMap<String, List<PermissionObject>>();
				for (PermissionObject permissionObject : modulePermissionList) {
					//如果为系统最大权限则不可选
					if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
						continue;
					}
					
					//如果为附加URL则不可选
					if(permissionObject.isAppendUrl() == true){
						continue;
					}
					
					List<PermissionObject> permissionObjectList = new ArrayList<PermissionObject>();
					if(permissionObjectMap.containsKey(permissionObject.getModule())){
						permissionObjectList.addAll(permissionObjectMap.get(permissionObject.getModule()));
					}

					if(auths != null && auths.size() >0){
						for (GrantedAuthority ga:auths) {
							if(ga.getAuthority().equals(permissionObject.getPermissionName())&& isPermissions){
								permissionObject.setLogonUserPermission(true);//当前登录用户权限拥有本权限
								break;	
							}
						}						
					}
					if(issys){//如果是超级管理员
						permissionObject.setLogonUserPermission(true);//当前用户可选择本权限
					}
					if(rolesPermissionName.contains(permissionObject.getPermissionName())){
						permissionObject.setSelected(true);
					}
					
					permissionObjectList.add(permissionObject);
					permissionObjectMap.put(permissionObject.getModule(), permissionObjectList);		
				}
				roles.setLogonUserPermission(isPermissions);
				returnValue.put("roles",roles);
				returnValue.put("permissionObjectMap",permissionObjectMap);//返回消息
			}else{
				error.put("rolesId", "角色不存在");
			}
		}else{
			error.put("rolesId", "角色Id不能为空");
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 角色管理 修改
	 */
	@ResponseBody
	@RequestMapping(params="method=editRoles",method=RequestMethod.POST)
	public String editRoles(ModelMap model,SysRoles formbean,
			String[] sysPermissionId,String rolesId
			) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		SysRoles sysRoles = new SysRoles ();
		
		List<SysRolesPermission> sysRolesPermissionList = new ArrayList<SysRolesPermission>();
		
		if(rolesId == null || "".equals(rolesId.trim())){
			error.put("id", "角色不存在");
		}
		if(formbean.getName() == null || "".equals(formbean.getName().trim())){
			error.put("name", "请填写角色名");
		}
		
		if(error.size() ==0){
			sysRoles.setId(rolesId);
			sysRoles.setName(formbean.getName());
			sysRoles.setRemarks(formbean.getRemarks());
			
			Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();//用户权限
			String username = "";//用户名称
			boolean issys = false;//是否是超级用户
			boolean isPermissions = true;//当前登录用户是否包含本角色权限
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 

			if(obj instanceof SysUsers){
				auths =((SysUsers)obj).getAuthorities();
				issys = ((SysUsers)obj).isIssys();
				username =((SysUsers)obj).getUsername();
			}
			
			if(!issys){//如果登录用户不是超级管理员
				Set<String> userAuthority = new HashSet<String>();//用户权限
				for(GrantedAuthority ga: auths){
					userAuthority.add(ga.getAuthority());
				}
				Set<String> rolesPermissionName = new HashSet<String>();//当前角色权限name
				List<SysPermission> sysPermissionList = aclService.findPermissionByRolesId(rolesId);
				if(sysPermissionList != null && sysPermissionList.size() >0){
					for(SysPermission sysPermission : sysPermissionList){
						rolesPermissionName.add(sysPermission.getName());
					}
				}
				
				if(!issys && userAuthority.containsAll(rolesPermissionName)){//如果用户权限包含角色权限
					if(userAuthority.size() == rolesPermissionName.size()){//当前登录用户不能修改和自己相同权限的角色
						isPermissions = false;	
					}else{
						isPermissions = true;
					}
				}
				
				//检查是否越权修改  只能修改权限比自己角色小的角色
				if(userAuthority.containsAll(rolesPermissionName)){//如果用户权限不包含角色权限
					if(userAuthority.size() == rolesPermissionName.size()){
						error.put("permission", "不能修改和自己相同权限的角色");
					}
				}else{
					error.put("permission", "账号权限不足");
				}
			}
			sysRoles.setLogonUserPermission(isPermissions);
			//系统模块权限Id
			String systemPermissionId = "";
					
			Set<String> permissionIdList = new HashSet<String>();//可用的权限ID
			List<PermissionObject> modulePermissionList = null;
			if(username != null && !"".equals(username)){
				
				if(issys){
					modulePermissionList = aclService.findModulePermission();
					List<String> allSysPermissionId = new ArrayList<String>();//系统所有权限ID
					if(modulePermissionList != null && modulePermissionList.size() >0){
						for (PermissionObject permissionObject : modulePermissionList) {
							//如果为系统最大权限则不可选
							if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
								continue;
							}
							allSysPermissionId.add(permissionObject.getPermissionId());
						}
						if(sysPermissionId != null && sysPermissionId.length >0){
							for(String spi :sysPermissionId){
								if(allSysPermissionId.contains(spi)){
									permissionIdList.add(spi);
								}
							}
						}
					}
				}else{
					List<String> userPermissionId = staffService.findPermissionIdByUserAccount(username);
					for(String s : userPermissionId){
						if(sysPermissionId != null && sysPermissionId.length >0){
							//如果为系统最大权限则不可选
							if("99999999999999999999999999999999".equals(s)){
								continue;
							}
							for(String spi :sysPermissionId){
								if(s.equals(spi)){
									permissionIdList.add(s);
								}
							}
						}
					}
				}		
			}else{
				error.put("permission", "没有权限");		
			}
			
			if(modulePermissionList == null){
				modulePermissionList = aclService.findModulePermission();
			}
			
			if(modulePermissionList != null && modulePermissionList.size() >0){
				for (PermissionObject permissionObject : modulePermissionList) {
					//如果为系统最大权限则不可选
					if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
						continue;
					}

					//自动勾选"系统-->后台框架"
					if("系统".equals(permissionObject.getModule())){
						systemPermissionId = permissionObject.getPermissionId();
					}
				}
			}
			//如果没选中"系统-->后台框架"，则自动选择
			if(!permissionIdList.contains(systemPermissionId)){
				error.put("permission", "必须选择【 系统-->后台框架】");
				
			}
			
			if(permissionIdList != null && permissionIdList.size() >0){
				for (String permissionId : permissionIdList) {
					SysRolesPermission sysRolesPermission = new SysRolesPermission();
					sysRolesPermission.setPermissionId(permissionId);
					sysRolesPermission.setRoleId(sysRoles.getId());
					sysRolesPermissionList.add(sysRolesPermission);
				}
			}
			
			
			if(error.size() == 0){
				aclService.updateRoles(sysRoles, sysRolesPermissionList);
			}
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 角色管理 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=deleteRoles",method=RequestMethod.POST)
	public String deleteRoles(ModelMap model ,String rolesId
			) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		//角色只有上级或超级管理员才能删除
		if(rolesId != null && !"".equals(rolesId.trim())){
			Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();//用户权限
			String username = "";//用户名称
			boolean issys = false;//是否是超级用户
			Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 

			if(obj instanceof SysUsers){
				auths =((SysUsers)obj).getAuthorities();
				issys = ((SysUsers)obj).isIssys();
				username =((SysUsers)obj).getUsername();
			}
			
			if(!issys){//如果登录用户不是超级管理员
				Set<String> userAuthority = new HashSet<String>();//用户权限
				for(GrantedAuthority ga: auths){
					userAuthority.add(ga.getAuthority());
				}

				Set<String> rolesPermissionName = new HashSet<String>();//当前角色权限name
				List<SysPermission> sysPermissionList = aclService.findPermissionByRolesId(rolesId);
				if(sysPermissionList != null && sysPermissionList.size() >0){
					for(SysPermission sysPermission : sysPermissionList){
						rolesPermissionName.add(sysPermission.getName());
					}
				}
				//检查是否越权删除  只能删除权限比自己角色小的角色
				if(userAuthority.containsAll(rolesPermissionName)){//如果用户权限不包含角色权限
					if(userAuthority.size()==rolesPermissionName.size()){//如果登录用户权限与要删除的用户的权限相同	
						error.put("role", "不能删除相同权限的角色");
					}
				}else{
					error.put("role", "账号权限不足");
				}
			}
			
			if(username == null || "".equals(username)){
				error.put("role", "没有权限");
			}
			if(error.size() == 0){
				aclService.deleteRoles(rolesId);
			}
			
		}else{
			error.put("role", "角色不存在");
		}
		
		if(error.size()==0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
