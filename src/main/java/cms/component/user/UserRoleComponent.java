package cms.component.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import cms.component.AccessSourceDeviceComponent;
import cms.component.JsonComponent;
import cms.component.topic.TagComponent;
import cms.config.CustomAccessDeniedException;
import cms.dto.user.*;
import cms.model.setting.SystemSetting;
import cms.model.topic.Tag;
import cms.model.user.UserRole;
import cms.model.user.UserRoleGroup;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TagRepository;
import cms.repository.user.UserRoleRepository;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.EnumUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import tools.jackson.core.type.TypeReference;


import cms.utils.AccessUserThreadLocal;
/**
 * 用户角色组件
 *
 */
@Component("userRoleComponent")
public class UserRoleComponent {
	//定义日志
	private static final Logger logger = LogManager.getLogger(UserRoleComponent.class);
	
	@Resource TagRepository tagRepository;
	@Resource AccessSourceDeviceComponent accessSourceDeviceComponent;
	@Resource UserRoleRepository userRoleRepository;
	@Resource SettingRepository settingRepository;
	@Resource TagComponent tagComponent;
    @Resource UserRoleCacheManager userRoleCacheManager;
    @Resource JsonComponent jsonComponent;

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
					if(resourceEnumList.size() >0){
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
		
		List<Tag> tagList = tagRepository.findAllTag_cache();
		if(tagList != null && tagList.size() >0){
			
			List<Tag> new_tagList = new ArrayList<Tag>();//排序后标签
			
			if(tagList != null && tagList.size() >0){
				
				//组成排序数据
				Iterator<Tag> tagList_iter = tagList.iterator();   
				while(tagList_iter.hasNext()){   
					Tag tag = tagList_iter.next();
					
					//如果是根节点
					if(tag.getParentId().equals(0L)){
						
						new_tagList.add(tag);
						tagList_iter.remove();   
				    }  
				}
				//组合子标签
				for(Tag tag :new_tagList){
					tagComponent.childTag(tag,tagList);
				}
				//排序
				tagComponent.tagSort(new_tagList);
			}
			
			
			
			
			for(Tag tag : new_tagList){
				if(tag.getChildNodeNumber() ==0){
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
				
				for(Tag childTag : tag.getChildTag()){
					if(childTag.getChildNodeNumber() ==0){
						UserResourceGroup userResourceGroup = new UserResourceGroup();
						userResourceGroup.setCode(resourceGroupEnum.getCode());
						userResourceGroup.setName(resourceGroupEnum.getName());
						userResourceGroup.setType(resourceGroupEnum.getType());
						userResourceGroup.setTagId(childTag.getId());
						userResourceGroup.setTagName(childTag.getName());
						userResourceGroup.setParentTagId(tag.getId());
						userResourceGroup.setParentTagName(tag.getName());
						
						//处理标签资源
						userResourceGroup.setUserResourceList(this.processingTagResource(resourceGroupEnum.getCode()));
						
						userResourceGroupList.add(userResourceGroup);
					}
				}
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
		if(resourceEnumList.size() >0){
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
		List<UserRole> allRoleList = userRoleRepository.findAllRole_cache();
		if(allRoleList != null && allRoleList.size() >0){
			for(UserRole userRole : allRoleList){
				if(userRole.getDefaultRole()){//默认角色
					
					if(userRole.getUserResourceFormat() != null && !userRole.getUserResourceFormat().trim().isEmpty()){
						List<UserResourceGroup> _userResourceGroupList = jsonComponent.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
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
		return this.queryUserRoleName(userName,null);
	}
	/**
	 * 查询用户角色名称(排除默认角色)
	 * @param userName 用户名称
	 * @param request 支持多语言切换需要此参数
	 * @return
	 */
	public List<String> queryUserRoleName(String userName, HttpServletRequest request){
		List<String> roleNameList = new ArrayList<String>();
		
		//查询用户角色组
  		List<UserRoleGroup> userRoleGroupList = userRoleCacheManager.query_cache_findRoleGroupByUserName(userName);
  		if(userRoleGroupList != null && userRoleGroupList.size() >0){
  			List<UserRole> allRoleList = userRoleRepository.findAllRole_cache();
  			
  			List<String> languageFormExtensionCodeList = null;
  			if(request != null){
  				SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
  				if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
  					languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
  				}	
			}
  			
  			if(allRoleList != null && allRoleList.size() >0){
  				for(UserRole userRole : allRoleList){
  					if(!userRole.getDefaultRole()){//不是默认角色
  						if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
							if(userRole.getMultiLanguageExtension() != null && !userRole.getMultiLanguageExtension().trim().isEmpty()){
								Map<String,String> multiLanguageExtensionMap = jsonComponent.toObject(userRole.getMultiLanguageExtension(), HashMap.class);
								if(multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() >0){
									userRole.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
								}
							}
						}
  						
  						
  						for(UserRoleGroup userRoleGroup : userRoleGroupList){
  			  				if(userRoleGroup.getUserRoleId().equals(userRole.getId())){
                                LocalDateTime now = LocalDateTime.now();
			  					userRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
			  					//和系统时间比  
			  					if(userRoleGroup.getValidPeriodEnd().isAfter(now)){//判断有效期

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
		return this.queryAllowViewTopicRoleName(tagId,null);
	}
	/**
	 * 查询允许'查看话题内容'的角色名称(排除默认角色)
	 * @param tagId 标签Id
	 * @param request 支持多语言切换需要此参数
	 * @return
	 */
	public List<String> queryAllowViewTopicRoleName(Long tagId,HttpServletRequest request){
		List<String> roleNameList = new ArrayList<String>();
		List<UserRole> allRoleList = userRoleRepository.findAllRole_cache();
		if(allRoleList != null && allRoleList.size() >0){
			List<String> languageFormExtensionCodeList = null;
  			if(request != null){
  				SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
  				if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
  					languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
  				}	
			}
			
			
			A:for(UserRole userRole : allRoleList){
				if(userRole.getDefaultRole()){//默认角色
					
					if(userRole.getUserResourceFormat() != null && !userRole.getUserResourceFormat().trim().isEmpty()){
						List<UserResourceGroup> _userResourceGroupList = jsonComponent.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
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
					
					if(languageFormExtensionCodeList != null && languageFormExtensionCodeList.size() >0){
						if(userRole.getMultiLanguageExtension() != null && !userRole.getMultiLanguageExtension().trim().isEmpty()){
							Map<String,String> multiLanguageExtensionMap = jsonComponent.toObject(userRole.getMultiLanguageExtension(), HashMap.class);
							if(multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() >0){
								userRole.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
							}
						}
					}
					
					if(userRole.getUserResourceFormat() != null && !userRole.getUserResourceFormat().trim().isEmpty()){
						List<UserResourceGroup> _userResourceGroupList = jsonComponent.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
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
  		List<UserRoleGroup> userRoleGroupList = userRoleCacheManager.query_cache_findRoleGroupByUserName(userName);
  		
		List<UserRole> allRoleList = userRoleRepository.findAllRole_cache();
		if(allRoleList != null && allRoleList.size() >0){
			for(UserRole userRole : allRoleList){
				if(userRole.getDefaultRole()){//默认角色
					userRoleList.add(userRole);
				}else{
					
					
					if(userRoleGroupList != null && userRoleGroupList.size() >0){
			  			for(UserRoleGroup userRoleGroup : userRoleGroupList){
			  				if(userRoleGroup.getUserRoleId().equals(userRole.getId())){
                                LocalDateTime now = LocalDateTime.now();
                                userRole.setValidPeriodEnd(userRoleGroup.getValidPeriodEnd());
                                //和系统时间比
                                if(userRoleGroup.getValidPeriodEnd().isAfter(now)){//判断有效期
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
				if(userRole.getUserResourceFormat() != null && !userRole.getUserResourceFormat().trim().isEmpty()){
					List<UserResourceGroup> _userResourceGroupList = jsonComponent.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
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
		List<UserRole> allRoleList = userRoleRepository.findAllRole_cache();
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
		//是否有当前功能操作权限
		boolean flag = this.isPermission(resourceEnum,tagId);
		if(!flag){
            throw new CustomAccessDeniedException("Access Denied");//设置403状态码
		}
		return flag;
	}
	
	
	/**
	 * 查询用户所属角色资源是否免审核
	 * @param resourceEnum 资源枚举
	 * @param userName 用户名称
	 * @param tagId 标签Id
	 * @return
	 */
	public boolean isExemptFromReview(ResourceEnum resourceEnum,String userName, Long tagId){
		
		if(resourceEnum.getResourceGroupCode().equals(1000000)){//话题功能
			//已添加资源组
	  		List<UserResourceGroup> userResourceGroupList = this.getUserResourceGroup(userName);
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
			
			
			
		}else{//其它功能
			//已添加资源组
	  		List<UserResourceGroup> userResourceGroupList = this.getUserResourceGroup(userName);
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

		return false;
	}
	
	

	
}
