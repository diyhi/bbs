package cms.web.action.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.UserResource;
import cms.bean.user.UserResourceGroup;
import cms.bean.user.UserRole;
import cms.service.user.UserRoleService;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;


/**
 * 用户角色管理
 *
 */
@Controller
@RequestMapping("/control/userRole/manage") 
public class UserRoleManageAction {
	@Resource UserRoleManage userRoleManage;
	@Resource UserRoleService userRoleService;
	
	/**
	 * 用户角色 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,UserRole userRole,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		//所有用户资源组集合
		List<UserResourceGroup> userResourceGroupList = userRoleManage.readAllUserResourceGroup();
		
		returnValue.put("userResourceGroupList",userResourceGroupList);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 用户角色 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,UserRole formbean,String[] resourceCode,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		UserRole userRole = new UserRole();
		
		
		if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
			if(formbean.getName().length() >40){
				error.put("name", "不能超过40个字符");
			}
			userRole.setName(formbean.getName().trim());
		}else{
			error.put("name", "不能为空");
		}
		userRole.setId(UUIDUtil.getUUID32());
		userRole.setSort(formbean.getSort());
		
		Set<String> resourceCodeSet = new HashSet<String>();
		if(resourceCode != null && resourceCode.length >0){
			for(String code : resourceCode){
				if(code != null && !"".equals(code.trim())){
					resourceCodeSet.add(code.trim());
				}
			}
		}
	
		//所有用户资源组集合
		List<UserResourceGroup> userResourceGroupList = userRoleManage.readAllUserResourceGroup();
		if(userResourceGroupList != null && userResourceGroupList.size() >0){
			for(UserResourceGroup userResourceGroup: userResourceGroupList){
				List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
				if(userResourceList != null && userResourceList.size() >0){
					int count = 0;
					for(UserResource userResource :userResourceList){
						
						if(resourceCodeSet.contains(userResourceGroup.getCode()+"_"+(userResourceGroup.getTagId() == null ? "" :userResourceGroup.getTagId())+"_"+userResource.getCode())){
							userResource.setSelected(true);
							count++;
						}
					}
					//是否全选
					if(count == userResourceList.size()){
						userResourceGroup.setSelected(true);
					}
				}
			}
		}
		
		userRole.setUserResourceFormat(JsonUtils.toJSONString(userResourceGroupList));
		
		
		if(error.size() ==0){
			userRoleService.saveUserRole(userRole);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
			
		}
	}
	
	
	/**
	 * 用户角色管理 修改界面显示
	 * @param model
	 * @param userRoleId 角色Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,String userRoleId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(userRoleId != null){
			UserRole userRole = userRoleService.findRoleById(userRoleId);
			if(userRole != null){
				returnValue.put("userRole", userRole);
				
				//所有用户资源组集合
				List<UserResourceGroup> userResourceGroupList = userRoleManage.readAllUserResourceGroup();
				if(userRole != null && userRole.getUserResourceFormat() != null && !"".equals(userRole.getUserResourceFormat().trim())){
					List<UserResourceGroup> old_userResourceGroupList = JsonUtils.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
					if(userResourceGroupList != null && userResourceGroupList.size() >0 && old_userResourceGroupList != null && old_userResourceGroupList.size() >0){
						for(UserResourceGroup userResourceGroup: userResourceGroupList){
							List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
							if(userResourceList != null && userResourceList.size() >0){
								if(userResourceGroup.getType().equals(10)){
									for(UserResourceGroup old_userResourceGroup: old_userResourceGroupList){
										if(old_userResourceGroup.getCode().equals(userResourceGroup.getCode())){
											List<UserResource> old_userResourceList = old_userResourceGroup.getUserResourceList();
											if(old_userResourceList != null && old_userResourceList.size() >0){
												for(UserResource userResource : userResourceList){
													for(UserResource old_userResource : old_userResourceList){
														if(userResource.getCode().equals(old_userResource.getCode())){
															userResource.setSelected(old_userResource.getSelected());
															break;
														}
													}
												}
											}
											break;
										}
										
										
									}
									
								}else if(userResourceGroup.getType().equals(20)){
									for(UserResourceGroup old_userResourceGroup: old_userResourceGroupList){
										if(old_userResourceGroup.getCode().equals(userResourceGroup.getCode()) && old_userResourceGroup.getTagId().equals(userResourceGroup.getTagId())){
											List<UserResource> old_userResourceList = old_userResourceGroup.getUserResourceList();
											if(old_userResourceList != null && old_userResourceList.size() >0){
												for(UserResource userResource : userResourceList){
													for(UserResource old_userResource : old_userResourceList){
														if(userResource.getCode().equals(old_userResource.getCode())){
															userResource.setSelected(old_userResource.getSelected());
															break;
														}
													}
												}
											}
											break;
										}
									}
								}
								//选中数量
								int selectedQuantity = 0;
								//是否全选
								for(UserResource userResource : userResourceList){
									if(userResource.getSelected()){
										selectedQuantity++;
									}
								}
								if(userResourceList.size() == selectedQuantity){
									userResourceGroup.setSelected(true);
								}
							}
						}
						
					}
				}
				returnValue.put("userResourceGroupList", userResourceGroupList);
			}else{
				error.put("userRoleId", "用户角色不存在");
			}
		}else{
			error.put("userRoleId", "Id不能为空");
		}
		
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 修改 用户角色
	 * @param model
	 * @param formbean
	 * @param userRoleId 角色Id
	 * @param resourceCode 资源编号 格式：${userResourceGroup.code}_${userResourceGroup.tagId}_${userResource.code}
	 * @param result
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,UserRole formbean,String userRoleId, String[] resourceCode,BindingResult result,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	

		Map<String,String> error = new HashMap<String,String>();
		UserRole userRole = new UserRole();
		
		if(userRoleId == null || "".equals(userRoleId.trim())){
			error.put("userRoleId", "角色Id不能为空");
		}
		if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
			if(formbean.getName().length() >40){
				error.put("name", "不能超过40个字符");
			}
			userRole.setName(formbean.getName().trim());
		}else{
			error.put("name", "不能为空");
		}
		userRole.setId(userRoleId);
		userRole.setSort(formbean.getSort());
		
		Set<String> resourceCodeSet = new HashSet<String>();
		if(resourceCode != null && resourceCode.length >0){
			for(String code : resourceCode){
				if(code != null && !"".equals(code.trim())){
					resourceCodeSet.add(code.trim());
				}
			}
		}
	
		//所有用户资源组集合
		List<UserResourceGroup> userResourceGroupList = userRoleManage.readAllUserResourceGroup();
		if(userResourceGroupList != null && userResourceGroupList.size() >0){
			for(UserResourceGroup userResourceGroup: userResourceGroupList){
				List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
				if(userResourceList != null && userResourceList.size() >0){
					int count = 0;
					for(UserResource userResource :userResourceList){
						
						if(resourceCodeSet.contains(userResourceGroup.getCode()+"_"+(userResourceGroup.getTagId() == null ? "" :userResourceGroup.getTagId())+"_"+userResource.getCode())){
							userResource.setSelected(true);
							count++;
						}
					}
					//是否全选
					if(count == userResourceList.size()){
						userResourceGroup.setSelected(true);
					}
				}
			}
		}
		
		userRole.setUserResourceFormat(JsonUtils.toJSONString(userResourceGroupList));
		
		
		
		if (error.size() == 0) { 
			userRoleService.updateUserRole(userRole);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		
	}
	
	/**
	 * 用户角色管理 删除
	 * @param model
	 * @param id 角色Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,String id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(id != null &&  !"".equals(id.trim())){
			userRoleService.deleteUserRole(id.trim());
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 用户角色管理 设为默认
	 * @param model
	 * @param id 角色Id
	 * @param defaultRole 是否设为默认角色
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=setAsDefault",method=RequestMethod.POST)
	public String setAsDefault(ModelMap model,String id,Boolean defaultRole,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(id != null &&  !"".equals(id.trim())){
			userRoleService.setAsDefaultRole(id,defaultRole);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
