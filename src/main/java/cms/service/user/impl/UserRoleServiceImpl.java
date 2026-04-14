package cms.service.user.impl;

import cms.component.JsonComponent;
import cms.component.user.UserRoleComponent;
import cms.config.BusinessException;
import cms.dto.user.UserResource;
import cms.dto.user.UserResourceGroup;
import cms.dto.user.UserRoleRequest;
import cms.model.setting.SystemSetting;
import cms.model.user.*;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRoleRepository;
import cms.service.user.UserRoleService;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户角色服务
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {


    @Resource
    UserRoleRepository userRoleRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserRoleComponent userRoleComponent;


    /**
     * 获取所有用户角色
     * @return
     */
    public List<UserRole> getAllRole(){
        return userRoleRepository.findAllRole();
    }


    /**
     * 获取添加用户角色界面信息
     * @return
     */
    public Map<String,Object> getAddUserRoleViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //所有用户资源组集合
        List<UserResourceGroup> userResourceGroupList = userRoleComponent.readAllUserResourceGroup();

        //多语言扩展参数
        Map<String,String> multiLanguageExtensionParameterMap = new LinkedHashMap<String,String>();

        returnValue.put("multiLanguageExtensionParameterMap",multiLanguageExtensionParameterMap);
        returnValue.put("userResourceGroupList",userResourceGroupList);
        return returnValue;
    }

    /**
     * 添加用户角色
     * @param userRoleRequest 用户角色表单
     * @param request 请求信息
     */
    public void addUserRole(UserRoleRequest userRoleRequest, HttpServletRequest request){


        Map<String, String> errors = new HashMap<>();

        processAndValidateUserRoleFields(userRoleRequest, errors);

        List<UserResourceGroup> userResourceGroupList = getResourceGroupWithSelection(userRoleRequest.getResourceCode());

        Map<String,String> multiLanguageExtensionMap = processMultiLanguage(request, errors);
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        UserRole userRole = new UserRole();
        userRole.setName(userRoleRequest.getName().trim());
        userRole.setId(UUIDUtil.getUUID32());
        userRole.setSort(userRoleRequest.getSort());
        userRole.setUserResourceFormat(jsonComponent.toJSONString(userResourceGroupList));
        userRole.setMultiLanguageExtension(jsonComponent.toJSONString(multiLanguageExtensionMap));
        userRoleRepository.saveUserRole(userRole);
    }


    /**
     * 获取修改用户角色界面信息
     * @param userRoleId 用户角色Id
     * @return
     */
    public Map<String,Object> getEditUserRoleViewModel(String userRoleId) {
        if (userRoleId == null || userRoleId.trim().isEmpty()) {
            throw new BusinessException(Map.of("userRoleId", "用户角色Id不能为空"));
        }
        UserRole userRole = userRoleRepository.findRoleById(userRoleId);
        if (userRole == null) {
            throw new BusinessException(Map.of("userRoleId", "用户角色不存在"));
        }
        Map<String, Object> returnValue = new HashMap<String, Object>();


        //多语言扩展参数
        Map<String,String> multiLanguageExtensionParameterMap = new LinkedHashMap<String,String>();


        returnValue.put("multiLanguageExtensionParameterMap",multiLanguageExtensionParameterMap);


        if(userRole.getMultiLanguageExtension() != null && !userRole.getMultiLanguageExtension().trim().isEmpty()){
            Map<String,String> multiLanguageExtensionMap = jsonComponent.toObject(userRole.getMultiLanguageExtension(), HashMap.class);
            if(multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() >0){
                userRole.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
            }
        }

        returnValue.put("userRole", userRole);

        //所有用户资源组集合
        List<UserResourceGroup> userResourceGroupList = userRoleComponent.readAllUserResourceGroup();
        if(userRole.getUserResourceFormat() != null && !userRole.getUserResourceFormat().trim().isEmpty()){
            List<UserResourceGroup> old_userResourceGroupList = jsonComponent.toGenericObject(userRole.getUserResourceFormat(), new TypeReference< List<UserResourceGroup> >(){});
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
        return returnValue;
    }

    /**
     * 修改用户角色
     * @param userRoleRequest 用户角色表单
     * @param request 请求信息
     */
    public void editUserRole(UserRoleRequest userRoleRequest, HttpServletRequest request){
        if (userRoleRequest.getUserRoleId() == null || userRoleRequest.getUserRoleId().trim().isEmpty()) {
            throw new BusinessException(Map.of("userRoleId", "用户角色Id不能为空"));
        }

        Map<String, String> errors = new HashMap<>();

        processAndValidateUserRoleFields(userRoleRequest, errors);

        List<UserResourceGroup> userResourceGroupList = getResourceGroupWithSelection(userRoleRequest.getResourceCode());

        Map<String,String> multiLanguageExtensionMap = processMultiLanguage(request, errors);
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        UserRole userRole = new UserRole();
        userRole.setName(userRoleRequest.getName().trim());
        userRole.setId(userRoleRequest.getUserRoleId());
        userRole.setSort(userRoleRequest.getSort());
        userRole.setUserResourceFormat(jsonComponent.toJSONString(userResourceGroupList));
        userRole.setMultiLanguageExtension(jsonComponent.toJSONString(multiLanguageExtensionMap));
        userRoleRepository.updateUserRole(userRole);
    }


    /**
     * 修改为默认角色
     * @param userRoleId 用户角色Id
     * @param isDefaultRole 是否设置为默认角色
     */
    public void editToDefaultRole(String userRoleId,Boolean isDefaultRole){
        if(userRoleId == null || userRoleId.trim().isEmpty()){
            throw new BusinessException(Map.of("userRoleId", "用户角色Id不能为空"));
        }
        userRoleRepository.setAsDefaultRole(userRoleId.trim(),isDefaultRole);
    }

    /**
     * 删除用户角色
     * @param userRoleId 用户角色Id
     */
    public void deleteUserRole(String userRoleId){
        if(userRoleId == null || userRoleId.trim().isEmpty()){
            throw new BusinessException(Map.of("userRoleId", "用户角色Id不能为空"));
        }
        userRoleRepository.deleteUserRole(userRoleId.trim());
    }


    /**
     * 处理用户角色字段的解析和校验
     * @param userRoleRequest       用户角色表单
     * @param errors         错误集合
     */
    private void processAndValidateUserRoleFields(UserRoleRequest userRoleRequest, Map<String, String> errors) {
        if(userRoleRequest.getName() != null && !userRoleRequest.getName().trim().isEmpty()){
            if(userRoleRequest.getName().length() >40){
                errors.put("name", "不能超过40个字符");
            }
        }else{
            errors.put("name", "不能为空");
        }

    }

    /**
     * 构建包含资源勾选状态的资源组列表
     * @param selectedCodes 选择的用户资源组编号
     * @return
     */
    public List<UserResourceGroup> getResourceGroupWithSelection(String[] selectedCodes) {
        Set<String> resourceCodeSet = (selectedCodes == null) ? new HashSet<>() :
                Arrays.stream(selectedCodes).filter(StringUtils::isNotBlank).collect(Collectors.toSet());

        //所有用户资源组集合
        List<UserResourceGroup> userResourceGroupList = userRoleComponent.readAllUserResourceGroup();
        if(userResourceGroupList != null && !userResourceGroupList.isEmpty()){
            for(UserResourceGroup userResourceGroup: userResourceGroupList){
                List<UserResource> userResourceList = userResourceGroup.getUserResourceList();
                if(userResourceList != null && !userResourceList.isEmpty()){
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
        return userResourceGroupList;
    }

    /**
     * 处理多语言参数映射
     * @param request 请求信息
     * @param errors 错误信息
     * @return
     */
    public Map<String, String> processMultiLanguage(HttpServletRequest request, Map<String, String> errors) {
        //多语言扩展  key:字段-语言（例如：name-en_US） value:内容
        Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();


        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getLanguageFormExtension() != null && !systemSetting.getLanguageFormExtension().trim().isEmpty()){
            List<String> languageFormExtensionCodeList = jsonComponent.toObject(systemSetting.getLanguageFormExtension(), List.class);
            if(languageFormExtensionCodeList != null && !languageFormExtensionCodeList.isEmpty()){
                for(String languageFormExtensionCode : languageFormExtensionCodeList){

                    String nameParameter = request.getParameter("name-"+languageFormExtensionCode);
                    if(nameParameter != null && !nameParameter.trim().isEmpty()){
                        if(nameParameter.length() >40){
                            errors.put("name-"+languageFormExtensionCode, "不能超过40个字符");
                        }else{
                            multiLanguageExtensionMap.put("name-"+languageFormExtensionCode, nameParameter);
                        }
                    }

                }
            }
        }
        return multiLanguageExtensionMap;
    }



}