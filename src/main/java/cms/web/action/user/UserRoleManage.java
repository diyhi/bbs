package cms.web.action.user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.topic.Tag;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.bean.user.ResourceGroupEnum;
import cms.bean.user.UserResource;
import cms.bean.user.UserResourceGroup;
import cms.bean.user.UserRole;
import cms.bean.user.UserRoleGroup;
import cms.service.template.TemplateService;
import cms.service.topic.TagService;
import cms.service.user.UserRoleService;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
/**
 * 用户角色
 *
 */
@Component("userRoleManage")
public class UserRoleManage {
	//定义日志
	private static final Logger logger = LogManager.getLogger(UserRoleManage.class);
	
	@Resource TagService tagService;
	@Resource TemplateService templateService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource UserRoleService userRoleService;
	@Resource UserRoleManage userRoleManage;
	
	/**
	 * 读取所有用户资源组
	 * @return
	 */
	public List<UserResourceGroup> readAllUserResourceGroup(){
		List<UserResourceGroup> userResourceGroupList = new ArrayList<UserResourceGroup>();
		
		
		
		List<ResourceGroupEnum> resourceGroupEnumList = EnumUtils.getEnumList(ResourceGroupEnum.class);
		if(resourceGroupEnumList != null && resourceGroupEnumList.size() >0){
			for(ResourceGroupEnum resourceGroupEnum:  resourceGroupEnumList){
				
				if(resourceGroupEnum.getType().equals(20)){//需'预处理'类型资源组
					userResourceGroupList.addAll(this.processingTagResourceGroup(resourceGroupEnum));
					
				}else{//'直接提交'类型资源组
					UserResourceGroup userResourceGroup = new UserResourceGroup();
					userResourceGroup.setCode(resourceGroupEnum.getCode());
					userResourceGroup.setName(resourceGroupEnum.getName());
					userResourceGroup.setType(resourceGroupEnum.getType());
					List<ResourceEnum> resourceEnumList = EnumUtils.getEnumList(ResourceEnum.class);
					if(resourceEnumList != null && resourceEnumList.size() >0){
						for(ResourceEnum resourceEnum : resourceEnumList){
							if(resourceEnum.getResourceGroupCode().equals(userResourceGroup.getCode())){
								UserResource userResource = new UserResource();
								userResource.setCode(resourceEnum.getCode());
								userResource.setName(resourceEnum.getName());
								userResource.setResourceGroupCode(resourceEnum.getResourceGroupCode());
								userResourceGroup.addUserResource(userResource);
							}
							
						}
					}
					userResourceGroupList.add(userResourceGroup);
				}
				
			}
		}
		
	
		
		return userResourceGroupList;
	}
	
	
	/**
	 * 处理标签资源组
	 * @param resourceGroupEnum 资源组枚举
	 * @return
	 */
	private List<UserResourceGroup> processingTagResourceGroup(ResourceGroupEnum resourceGroupEnum){
		List<UserResourceGroup> userResourceGroupList = new ArrayList<UserResourceGroup>();
		
		List<Tag> tagList = tagService.findAllTag_cache();
		if(tagList != null && tagList.size() >0){
			for(Tag tag : tagList){
				UserResourceGroup userResourceGroup = new UserResourceGroup();
				userResourceGroup.setCode(resourceGroupEnum.getCode());
				userResourceGroup.setName(resourceGroupEnum.getName());
				userResourceGroup.setType(resourceGroupEnum.getType());
				userResourceGroup.setTagId(tag.getId());
				userResourceGroup.setTagName(tag.getName());
				
				//处理标签资源
				userResourceGroup.setUserResourceList(this.processingTagResource(resourceGroupEnum.getCode()));
				
				userResourceGroupList.add(userResourceGroup);
			}
		}
		return userResourceGroupList;
	}
	
	/**
	 * 处理标签资源
	 * @param resourceGroupCode 资源组编号
	 * @return
	 */
	private List<UserResource> processingTagResource(Integer resourceGroupCode){
		List<UserResource> userResourceList = new ArrayList<UserResource>();
		
		List<ResourceEnum> resourceEnumList = EnumUtils.getEnumList(ResourceEnum.class);
		if(resourceEnumList != null && resourceEnumList.size() >0){
			for(ResourceEnum resourceEnum:  resourceEnumList){
				
				if(resourceEnum.getResourceGroupCode().equals(resourceGroupCode)){
					UserResource userResource = new UserResource();
					userResource.setCode(resourceEnum.getCode());
					userResource.setName(resourceEnum.getName());
					userResource.setResourceGroupCode(resourceEnum.getResourceGroupCode());
					userResourceList.add(userResource);
				}
				
				
			}
		}
		return userResourceList;
	}
	
	
	/**
	 * 获取默认用户角色的资源组
	 * @return
	 */
	private List<UserResourceGroup> getDefaultUserResourceGroup(){
		List<UserRole> allRoleList = userRoleService.findAllRole_cache();
		if(allRoleList != null && allRoleList.size() >0){
			for(UserRole userRole : allRoleList){
				if(userRole.getDefaultRole()){//默认角色
					
					if(userRole.getUserResourceFormat() != null && !"".equals(userRole.getUserResourceFormat().trim())){
						List<UserResourceGroup> _userResourceGroupList = JsonUtils.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
						return _userResourceGroupList;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 查询用户角色名称(排除默认角色)
	 * @param userName 用户名称
	 * @return
	 */
	public List<String> queryUserRoleName(String userName){
		List<String> roleNameList = new ArrayList<String>();
		
		//查询用户角色组
  		List<UserRoleGroup> userRoleGroupList = userRoleManage.query_cache_findRoleGroupByUserName(userName);
  		if(userRoleGroupList != null && userRoleGroupList.size() >0){
  			List<UserRole> allRoleList = userRoleService.findAllRole_cache();
  			if(allRoleList != null && allRoleList.size() >0){
  				for(UserRole userRole : allRoleList){
  					if(!userRole.getDefaultRole()){//不是默认角色
  						for(UserRoleGroup userRoleGroup : userRoleGroupList){
  			  				if(userRoleGroup.getUserRoleId().equals(userRole.getId())){
	  			  				DateTime time = new DateTime(userRoleGroup.getValidPeriodEnd());  
			  					userRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
			  					//和系统时间比  
			  					if(time.isAfterNow()){//判断有效期
			  						roleNameList.add(userRole.getName());
			  					}
  			  					
  			  					break;
  			  				}
  			  			}
  					}
  				}
  			}
  			
  			
  			
  		}
  		return roleNameList;
	}
	
	/**
	 * 查询允许'查看话题内容'的角色名称(排除默认角色)
	 * @param tagId 标签Id
	 * @return
	 */
	public List<String> queryAllowViewTopicRoleName(Long tagId){
		List<String> roleNameList = new ArrayList<String>();
		List<UserRole> allRoleList = userRoleService.findAllRole_cache();
		if(allRoleList != null && allRoleList.size() >0){
			A:for(UserRole userRole : allRoleList){
				if(userRole.getDefaultRole()){//默认角色
					
					if(userRole.getUserResourceFormat() != null && !"".equals(userRole.getUserResourceFormat().trim())){
						List<UserResourceGroup> _userResourceGroupList = JsonUtils.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
						if(_userResourceGroupList != null && _userResourceGroupList.size() >0){
							for(UserResourceGroup userResourceGroup : _userResourceGroupList){
								
								if(userResourceGroup.getType().equals(20) && userResourceGroup.getTagId().equals(tagId)){
									List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
									if(userResourceList != null && userResourceList.size() >0){
										for(UserResource userResource : userResourceList){
											if(userResource.getCode().equals(ResourceEnum._1001000.getCode()) && userResource.getSelected()){//查看话题内容
												//如果默认角色允许查看
												break A;
											}
										}
									}
								}
							}
						}
					}
					
					
					
				}else{//不是默认角色
					if(userRole.getUserResourceFormat() != null && !"".equals(userRole.getUserResourceFormat().trim())){
						List<UserResourceGroup> _userResourceGroupList = JsonUtils.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
						if(_userResourceGroupList != null && _userResourceGroupList.size() >0){
							for(UserResourceGroup userResourceGroup : _userResourceGroupList){
								
								if(userResourceGroup.getType().equals(20) && userResourceGroup.getTagId().equals(tagId)){
									List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
									if(userResourceList != null && userResourceList.size() >0){
										for(UserResource userResource : userResourceList){
											if(userResource.getCode().equals(ResourceEnum._1001000.getCode()) && userResource.getSelected()){//查看话题内容
												roleNameList.add(userRole.getName());
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return roleNameList;
	}
	
	
	/**
	 * 获取用户的资源组
	 * @param userName
	 * @return
	 */
	private List<UserResourceGroup> getUserResourceGroup(String userName){
		List<UserResourceGroup> userResourceGroupList = new ArrayList<UserResourceGroup>();
		
		//用户角色集合
		List<UserRole> userRoleList = new ArrayList<UserRole>();
		
		//查询用户角色组
  		List<UserRoleGroup> userRoleGroupList = userRoleManage.query_cache_findRoleGroupByUserName(userName);
  		
		List<UserRole> allRoleList = userRoleService.findAllRole_cache();
		if(allRoleList != null && allRoleList.size() >0){
			for(UserRole userRole : allRoleList){
				if(userRole.getDefaultRole()){//默认角色
					userRoleList.add(userRole);
				}else{
					
					
					if(userRoleGroupList != null && userRoleGroupList.size() >0){
			  			for(UserRoleGroup userRoleGroup : userRoleGroupList){
			  				if(userRoleGroup.getUserRoleId().equals(userRole.getId())){
			  					DateTime time = new DateTime(userRoleGroup.getValidPeriodEnd());  
			  					userRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
			  					//和系统时间比  
			  					if(time.isAfterNow()){//判断有效期
			  						userRoleList.add(userRole);
			  					}
			  					break;
			  				}
			  			}
			  		}
				}
			}
			
		}
		
		if(userRoleList != null && userRoleList.size() >0){
			for(UserRole userRole : userRoleList){
				if(userRole.getUserResourceFormat() != null && !"".equals(userRole.getUserResourceFormat().trim())){
					List<UserResourceGroup> _userResourceGroupList = JsonUtils.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
					userResourceGroupList.addAll(_userResourceGroupList);
				}
			}
		}
		
		return userResourceGroupList;
	}
	
	/**
	 * 判断权限
	 * @param resourceEnum 资源枚举
	 * @param tagId 标签Id
	 * @return
	 */
	public boolean isPermission(ResourceEnum resourceEnum, Long tagId){
		List<UserRole> allRoleList = userRoleService.findAllRole_cache();
		if(allRoleList == null || allRoleList.size() ==0){//如果没有添加资源组
			return true;
		}
		
		if(resourceEnum.getResourceGroupCode().equals(1000000)){//标签
			//如果默认角色允许‘查看话题内容’，则未登录用户也可以查看
			List<UserResourceGroup> _userResourceGroupList = this.getDefaultUserResourceGroup();//获取默认用户角色的资源组
			if(_userResourceGroupList != null && _userResourceGroupList.size() >0){
				for(UserResourceGroup userResourceGroup : _userResourceGroupList){
					List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
					if(userResourceGroup.getType().equals(20) && userResourceList != null && userResourceList.size() >0 && userResourceGroup.getTagId().equals(tagId)){
						for(UserResource userResource : userResourceList){
							if(userResource.getCode().equals(1000) && userResource.getCode().equals(resourceEnum.getCode()) && userResource.getSelected()){
								return true;
							}
						}
					}
				}
			}
			
			
			//获取登录用户
		  	AccessUser accessUser = AccessUserThreadLocal.get();
		  	if(accessUser != null){

				//已添加资源组
		  		List<UserResourceGroup> userResourceGroupList = this.getUserResourceGroup(accessUser.getUserName());
		  		if(userResourceGroupList != null && userResourceGroupList.size() >0){
		  			for(UserResourceGroup userResourceGroup : userResourceGroupList){
		  				List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
		  				if(userResourceGroup.getType().equals(20) && userResourceList != null && userResourceList.size() >0 && userResourceGroup.getTagId().equals(tagId)){
		  					for(UserResource userResource : userResourceList){
		  						if(userResource.getResourceGroupCode().equals(resourceEnum.getResourceGroupCode()) && userResource.getCode().equals(resourceEnum.getCode()) && userResource.getSelected()){
		  							return true;
		  						}
		  					}
		  					
		  				}
		  				
		  				
		  			}
		  		}
		  	}
		}else{
			//获取登录用户
		  	AccessUser accessUser = AccessUserThreadLocal.get();
		  	if(accessUser != null){
		  		
		  		
				//已添加资源组
		  		List<UserResourceGroup> userResourceGroupList = this.getUserResourceGroup(accessUser.getUserName());
		  		if(userResourceGroupList != null && userResourceGroupList.size() >0){
		  			for(UserResourceGroup userResourceGroup : userResourceGroupList){
		  				List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
		  				if(userResourceList != null && userResourceList.size() >0){
		  					for(UserResource userResource : userResourceList){
		  						
		  						if(userResource.getResourceGroupCode().equals(resourceEnum.getResourceGroupCode()) && userResource.getCode().equals(resourceEnum.getCode()) && userResource.getSelected()){
		  							return true;
		  						}
		  					}
		  					
		  				}
		  			}
		  		}
		  	}	
		}
		
		
		
		return false;
	}

	/**
	 * 检查权限
	 * @param resourceEnum 资源枚举
	 * @param tagId 标签Id
	 * @return
	 */
	public boolean checkPermission(ResourceEnum resourceEnum, Long tagId){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();  
		
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
        
		//是否有当前功能操作权限
		boolean flag = this.isPermission(resourceEnum,tagId);
		
		if(flag == false){
			if(isAjax == true){
				response.setStatus(403);//设置状态码
	    		try {
					WebUtil.writeToWeb("", "json", response);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("前台检查权限生成JSON异常",e);
			        }
				}

			}else{
		        try {
		        	request.setAttribute("message","权限不足");
		        	
		        	//跳转到不存在页面，由cms.web.action.common.BlankAction.execute方法执行/message页将消息展示
					request.getRequestDispatcher("null").forward(request, response);//注意：这个"null"路径是不存在的
					
				} catch (ServletException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("前台检查权限转发异常",e1);
			        }
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("前台检查权限转发IO异常",e1);
			        }
				}
			}
		}
		return flag;
	}
	
	
	/**
	 * 查询缓存 根据用户名称查询角色组
	 * @param userName 用户名称
	 * @return
	 */
	@Cacheable(value="userRoleManage_cache_findRoleGroupByUserName",key="#userName")
	public List<UserRoleGroup> query_cache_findRoleGroupByUserName(String userName){
		return userRoleService.findRoleGroupByUserName(userName);
	}
	/**
	 * 删除缓存 根据用户名称查询角色组
	 * @param userName 用户名称
	 * @return
	 */
	@CacheEvict(value="userRoleManage_cache_findRoleGroupByUserName",key="#userName")
	public void delete_cache_findRoleGroupByUserName(String userName){
	}
	
	
}
