package cms.service.staff.impl;

import cms.component.staff.StaffCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.staff.PermissionObject;
import cms.dto.staff.SaveResourcesObject;
import cms.model.staff.*;
import cms.repository.setting.SettingRepository;
import cms.repository.staff.ACLRepository;
import cms.repository.staff.StaffRepository;
import cms.service.staff.ACLService;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 访问控制服务
 */
@Service
public class ACLServiceImpl implements ACLService {

    @Resource
    StaffCacheManager staffCacheManager;
    @Resource
    SettingRepository settingRepository;
    @Resource
    ACLRepository aclRepository;
    @Resource
    StaffRepository staffRepository;


    /**
     * 获取角色列表
     *
     * @param page 页码
     * @return 分页视图对象
     */
    public PageView<SysRoles> getRolesList(int page) {
        //调用分页算法代码
        PageView<SysRoles> pageView = new PageView<SysRoles>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);
        //当前页
        int firstindex = (page - 1) * pageView.getMaxresult();
        //排序
        QueryResult<SysRoles> qr = aclRepository.getScrollData(SysRoles.class, firstindex, pageView.getMaxresult());


        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        return pageView;
    }

    /**
     * 获取模块名称列表
     *
     * @param page 页码
     * @return 分页视图对象
     */
    public PageView<SysResources> getModuleNames(int page) {

        //调用分页算法代码
        PageView<SysResources> pageView = new PageView<SysResources>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 8);
        //当前页
        int firstindex = (page - 1) * pageView.getMaxresult();
        QueryResult<String> qr = aclRepository.modulePage(firstindex, pageView.getMaxresult());

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }


    /**
     * 删除资源
     * @param resourcesId 资源Id
     */
    public void deleteResources(String resourcesId){
        if (resourcesId == null || resourcesId.trim().isEmpty()) {
            throw new BusinessException(Map.of("resourcesId", "资源Id不能为空"));
        }

        List<String> permissionIdList = new ArrayList<String>();
        //根据资源表Id查询权限
        List<SysPermission> permissionList = aclRepository.findPermissionByResourcesId(resourcesId);
        if(permissionList != null && permissionList.size() >0){
            for(SysPermission permission : permissionList){
                permissionIdList.add(permission.getId());
            }
        }
        //删除权限资源Id
        List<String> delete_resourcesId = new ArrayList<String>();
        delete_resourcesId.add(resourcesId);
        List<SysResources> sysResourcesList =aclRepository.findResourcesByUrlParentId(resourcesId);
        if(sysResourcesList != null){
            for(SysResources sr: sysResourcesList){
                delete_resourcesId.add(sr.getId());
            }
        }
        aclRepository.deleteResources(resourcesId,permissionIdList,delete_resourcesId);
        staffCacheManager.clear_staffPermissionMenu();
        staffCacheManager.clear_allAuthorities();
        staffCacheManager.clear_userAuthoritiesByName();
    }

    /**
     * 获取添加角色界面信息
     * @return
     */
    public LinkedHashMap<String, List<PermissionObject>> getAddRolesViewModel() {

        Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();//用户权限

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean issys = false;//是否是超级用户

        if(obj instanceof SysUsers){
            auths =((SysUsers)obj).getAuthorities();
            issys = ((SysUsers)obj).isIssys();
        }

        List<PermissionObject> modulePermissionList = aclRepository.findModulePermission();
        LinkedHashMap<String, List<PermissionObject>> permissionObjectMap = new LinkedHashMap<String, List<PermissionObject>>();
        if(modulePermissionList != null && modulePermissionList.size() >0){
            for (PermissionObject permissionObject : modulePermissionList) {
                //如果为系统最大权限则不可选
                if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
                    continue;
                }

                //如果为附加URL则不可选
                if(permissionObject.isAppendUrl()){
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
                if(auths.size() >0){
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
        return permissionObjectMap;
    }


    /**
     * 添加角色
     * @param sysRolesForm 角色表单
     * @param sysPermissionId 权限Id集合
     */
    public void addRoles(SysRoles sysRolesForm,String[] sysPermissionId) {
        Map<String, String> errors = new HashMap<String, String>();
        if(sysRolesForm.getName() == null || sysRolesForm.getName().trim().isEmpty()){
            errors.put("name", "请填写角色名");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers loginUser = (SysUsers) authentication.getPrincipal();


        //筛选可用的权限ID
        Set<String> permissionIdList = filterValidPermissions(sysPermissionId, loginUser);

        //确保“系统-->后台框架”被选中
        if(!permissionIdList.contains(getSystemPermissionId())){
            errors.put("permission", "必须选择【 系统-->后台框架】");
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        SysRoles sysRoles = new SysRoles();
        sysRoles.setId(UUIDUtil.getUUID32());
        sysRoles.setName(sysRolesForm.getName());
        sysRoles.setRemarks(sysRolesForm.getRemarks());

        List<SysRolesPermission> sysRolesPermissionList = new ArrayList<SysRolesPermission>();
        if(permissionIdList.size() >0){
            for (String permissionId : permissionIdList) {
                SysRolesPermission sysRolesPermission = new SysRolesPermission();
                sysRolesPermission.setPermissionId(permissionId);
                sysRolesPermission.setRoleId(sysRoles.getId());
                sysRolesPermissionList.add(sysRolesPermission);
            }
        }

        aclRepository.saveRoles(sysRoles, sysRolesPermissionList);
        staffCacheManager.clear_staffPermissionMenu();
        staffCacheManager.clear_userAuthoritiesByName();
    }

    /**
     * 获取修改角色界面信息
     * @param rolesId 角色Id
     */
    public Map<String,Object> getEditRolesViewModel(String rolesId) {
        if (rolesId == null || rolesId.trim().isEmpty()) {
            throw new BusinessException(Map.of("rolesId", "角色Id不能为空"));
        }

        SysRoles roles = (SysRoles)aclRepository.find(SysRoles.class, rolesId);
        if(roles == null){
            throw new BusinessException(Map.of("rolesId", "角色不存在"));
        }

        Set<String> rolesPermissionName = new HashSet<String>();//要修改的角色权限name
        List<SysPermission> sysPermissionList = aclRepository.findPermissionByRolesId(rolesId);
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
        Map<String,Object> returnValue = new LinkedHashMap<String,Object>();
        List<PermissionObject> modulePermissionList = aclRepository.findModulePermission();
        LinkedHashMap<String, List<PermissionObject>> permissionObjectMap = new LinkedHashMap<String, List<PermissionObject>>();
        for (PermissionObject permissionObject : modulePermissionList) {
            //如果为系统最大权限则不可选
            if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
                continue;
            }

            //如果为附加URL则不可选
            if(permissionObject.isAppendUrl()){
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
        return returnValue;
    }

    /**
     * 修改角色
     * @param sysRolesForm 角色表单
     * @param sysPermissionId 权限Id集合
     * @param rolesId 角色Id
     */
    public void editRoles(SysRoles sysRolesForm, String[] sysPermissionId,String rolesId){
        Map<String, String> errors = new HashMap<String, String>();
        if(rolesId == null || rolesId.trim().isEmpty()){
            throw new BusinessException(Map.of("id", "角色Id不能为空"));
        }
        if(sysRolesForm.getName() == null || sysRolesForm.getName().trim().isEmpty()){
            errors.put("name", "请填写角色名");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers loginUser = (SysUsers) authentication.getPrincipal();

        if(!loginUser.isIssys()){//如果登录用户不是超级管理员
            validateUserPermission(loginUser,rolesId,errors);
        }

        //筛选可用的权限ID
        Set<String> permissionIdList = filterValidPermissions(sysPermissionId, loginUser);
        //确保“系统-->后台框架”被选中
        if(!permissionIdList.contains(getSystemPermissionId())){
            errors.put("permission", "必须选择【 系统-->后台框架】");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        List<SysRolesPermission> sysRolesPermissionList = new ArrayList<SysRolesPermission>();
        SysRoles sysRoles = new SysRoles ();
        sysRoles.setId(rolesId);
        sysRoles.setName(sysRolesForm.getName());
        sysRoles.setRemarks(sysRolesForm.getRemarks());
        if(permissionIdList.size() >0){
            for (String permissionId : permissionIdList) {
                SysRolesPermission sysRolesPermission = new SysRolesPermission();
                sysRolesPermission.setPermissionId(permissionId);
                sysRolesPermission.setRoleId(sysRoles.getId());
                sysRolesPermissionList.add(sysRolesPermission);
            }
        }

        aclRepository.updateRoles(sysRoles, sysRolesPermissionList);
        staffCacheManager.clear_staffPermissionMenu();
        staffCacheManager.clear_userAuthoritiesByName();
    }

    /**
     * 删除角色
     * @param rolesId 角色Id
     */
    public void deleteRoles(String rolesId){
        Map<String, String> errors = new HashMap<String, String>();
        if(rolesId == null || rolesId.trim().isEmpty()){
            throw new BusinessException(Map.of("id", "角色Id不能为空"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers loginUser = (SysUsers) authentication.getPrincipal();

        if(!loginUser.isIssys()) {//如果登录用户不是超级管理员
            validateUserPermission(loginUser, rolesId, errors);
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        aclRepository.deleteRoles(rolesId);
        staffCacheManager.clear_staffPermissionMenu();
        staffCacheManager.clear_userAuthoritiesByName();
    }




    /**
     * 验证资源表单中的字段。
     *
     * @param sysResourcesForm 资源表单
     * @param errors           错误信息
     */
    private void validateResourceFields(SysResources sysResourcesForm, Map<String, String> errors) {
        if (sysResourcesForm.getName() == null || sysResourcesForm.getName().trim().isEmpty()) {
            errors.put("name", "名称不能为空");
        }
        if (sysResourcesForm.getUrl() == null || sysResourcesForm.getUrl().trim().isEmpty()) {
            errors.put("url", "URL不能为空");
        }
        if (sysResourcesForm.getModule() == null || sysResourcesForm.getModule().trim().isEmpty()) {
            errors.put("module", "模块不能为空");
        }
        if (sysResourcesForm.getPriority() == null) {
            errors.put("priority", "资源优先级不能为空");
        }
    }

    /**
     * 处理 NULL 请求方式
     *
     * @param sysResources           资源
     * @param sysPermissionName_NULL 资源权限参数
     * @param priority_NULL          权限优先级
     * @param permissionRemarks_NULL 权限说明
     * @param sysResourcesList       保存资源对象集合
     * @param errors                 错误信息
     */
    private void processNullMethod(SysResources sysResources, String sysPermissionName_NULL, String priority_NULL, String permissionRemarks_NULL, List<SaveResourcesObject> sysResourcesList, Map<String, String> errors) {
        Map<String, String> validationErrors = new HashMap<>();
        if (sysPermissionName_NULL == null || sysPermissionName_NULL.trim().isEmpty()) {
            validationErrors.put("sysPermissionName_NULL", "资源权限参数不能为空");
        }
        if (!StringUtils.isNumeric(priority_NULL)) {
            validationErrors.put("priority_NULL", "请填写数字类型");
        }
        if (permissionRemarks_NULL == null || permissionRemarks_NULL.trim().isEmpty()) {
            validationErrors.put("permissionRemarks_NULL", "权限说明不能为空");
        }

        if (!validationErrors.isEmpty()) {
            errors.putAll(validationErrors);
            return;
        }

        SysPermission sysPermission_NULL = new SysPermission();
        sysPermission_NULL.setPriority(Integer.parseInt(priority_NULL));
        // 当权限标识前缀不为"AUTH_"时，要添加该标识。
        if (sysPermissionName_NULL.startsWith("AUTH_")) {
            sysPermission_NULL.setName(sysPermissionName_NULL);
        } else {
            sysPermission_NULL.setName("AUTH_" + sysPermissionName_NULL);
        }

        //如果为系统最大权限
        if (sysPermission_NULL.getPriority().equals(99999999)) {
            sysPermission_NULL.setId("99999999999999999999999999999999");
        } else {
            sysPermission_NULL.setId(UUIDUtil.getUUID32());
        }


        sysPermission_NULL.setRemarks(permissionRemarks_NULL);

        //保存权限资源表
        SysPermissionResources sysPermissionResources = new SysPermissionResources();
        sysPermissionResources.setResourceId(sysResources.getId());
        sysPermissionResources.setPermissionId(sysPermission_NULL.getId());

        sysResourcesList.add(new SaveResourcesObject(sysResources, sysPermission_NULL, sysPermissionResources));
    }

    /**
     * 处理 GET 请求方式
     *
     * @param sysResources              资源
     * @param sysPermissionName_GET     资源权限参数
     * @param priority_GET              权限优先级
     * @param permissionRemarks_GET     权限说明
     * @param URL_GET_Table             附加URL数组数量(GET方式)
     * @param additionalURL_GET         附加URL(GET方式)
     * @param additionalURL_remarks_GET 附加URL备注(GET方式)
     * @param sysResourcesList          保存资源对象集合
     * @param errors                    错误信息
     */
    private void processGetMethod(SysResources sysResources,
                                  String[] sysPermissionName_GET, String priority_GET, String permissionRemarks_GET,
                                  Integer[] URL_GET_Table, String[] additionalURL_GET, String[] additionalURL_remarks_GET,
                                  List<SaveResourcesObject> sysResourcesList, Map<String, String> errors) {
        Map<String, String> validationErrors = new HashMap<>();

        if (sysPermissionName_GET == null || sysPermissionName_GET.length == 0) {
            validationErrors.put("sysPermissionName_GET", "资源权限不能为空");
        }
        if (!StringUtils.isNumeric(priority_GET)) {
            validationErrors.put("priority_GET", "权限优先级请填写数字类型");
        }
        if (permissionRemarks_GET == null || permissionRemarks_GET.trim().isEmpty()) {
            validationErrors.put("permissionRemarks_GET", "权限说明不能为空");
        }

        if (!validationErrors.isEmpty()) {
            errors.putAll(validationErrors);
            return;
        }


        SysPermission sysPermission_GET = new SysPermission();
        StringBuffer sb = new StringBuffer();
        sb.append(sysResources.getId()).append("_");
        for (String permissionName : sysPermissionName_GET) {
            sb.append(permissionName).append("_");
        }
        sb.deleteCharAt(sb.length() - 1);

        String name = sb.toString();
        // 当权限标识前缀不为"AUTH_"时，要添加该标识。
        if (name.startsWith("AUTH")) {
            if (!name.startsWith("AUTH_")) {
                name = name.substring(0, 4) + "_"
                        + name.substring(4);
                sysPermission_GET.setName(name);
            }
        } else {
            sysPermission_GET.setName("AUTH_" + name);
        }

        //保存权限
        sysPermission_GET.setId(UUIDUtil.getUUID32());
        sysPermission_GET.setPriority(Integer.parseInt(priority_GET));
        sysPermission_GET.setRemarks(permissionRemarks_GET);
        sysPermission_GET.setMethods("GET");

        //保存权限资源表
        SysPermissionResources sysPermissionResources = new SysPermissionResources();
        sysPermissionResources.setResourceId(sysResources.getId());
        sysPermissionResources.setPermissionId(sysPermission_GET.getId());

        sysResourcesList.add(new SaveResourcesObject(sysResources, sysPermission_GET, sysPermissionResources));

        if (URL_GET_Table != null && URL_GET_Table.length > 0 && additionalURL_GET != null && additionalURL_GET.length > 0) {
            for (int m = 0; m < URL_GET_Table.length; m++) {//循环全部提交的附加URL数组
                String additional = additionalURL_GET[m];//附加URL(GET方式)

                if (additional != null && !additional.trim().isEmpty()) {
                    //保存资源
                    SysResources sysResources_additionalURL = new SysResources();//附加URL
                    String ids_URL_GET = UUIDUtil.getUUID32();
                    sysResources_additionalURL.setId(ids_URL_GET);
                    sysResources_additionalURL.setPriority(m);
                    if (additionalURL_remarks_GET != null && additionalURL_remarks_GET.length > 0) {
                        sysResources_additionalURL.setName(additionalURL_remarks_GET[m]);//附加URL备注(GET方式)
                    }

                    sysResources_additionalURL.setUrl(additional.trim());
                    sysResources_additionalURL.setUrlType(1);//附加URL方式
                    sysResources_additionalURL.setUrlParentId(sysResources.getId());//设置父ID
                    sysResources_additionalURL.setModule(sysResources.getModule());


                    //保存权限资源表
                    SysPermissionResources sysPermissionResourcesURL = new SysPermissionResources();
                    sysPermissionResourcesURL.setResourceId(sysResources_additionalURL.getId());
                    sysPermissionResourcesURL.setPermissionId(sysPermission_GET.getId());

                    sysResourcesList.add(new SaveResourcesObject(sysResources_additionalURL, null, sysPermissionResourcesURL));
                }

            }
        }

    }

    /**
     * 处理 POST 请求方式
     *
     * @param sysResources               资源
     * @param sysPermissionName_POST     资源权限参数
     * @param priority_POST              权限优先级
     * @param permissionRemarks_POST     权限说明
     * @param URL_POST_Table             附加URL数组数量(POST方式)
     * @param additionalURL_POST         附加URL(POST方式)
     * @param additionalURL_remarks_POST 附加URL备注(POST方式)
     * @param sysResourcesList           保存资源对象集合
     * @param errors                     错误信息
     */
    private void processPostMethod(SysResources sysResources,
                                   String[] sysPermissionName_POST, String priority_POST, String permissionRemarks_POST,
                                   Integer[] URL_POST_Table, String[] additionalURL_POST, String[] additionalURL_remarks_POST,
                                   List<SaveResourcesObject> sysResourcesList, Map<String, String> errors) {
        Map<String, String> validationErrors = new HashMap<>();

        if (sysPermissionName_POST == null || sysPermissionName_POST.length == 0) {
            validationErrors.put("sysPermissionName_POST", "资源权限不能为空");
        }
        if (!StringUtils.isNumeric(priority_POST)) {
            validationErrors.put("priority_POST", "权限优先级请填写数字类型");
        }
        if (permissionRemarks_POST == null || permissionRemarks_POST.trim().isEmpty()) {
            validationErrors.put("permissionRemarks_POST", "权限说明不能为空");
        }

        if (!validationErrors.isEmpty()) {
            errors.putAll(validationErrors);
            return;
        }


        SysPermission sysPermission_POST = new SysPermission();
        StringBuffer sb = new StringBuffer();

        sb.append(sysResources.getId()).append("_");
        for (String permissionName : sysPermissionName_POST) {
            sb.append(permissionName).append("_");
        }
        sb.deleteCharAt(sb.length() - 1);

        String name = sb.toString();
        // 当权限标识前缀不为"AUTH_"时，要添加该标识。
        if (name.startsWith("AUTH")) {
            if (!name.startsWith("AUTH_")) {
                name = name.substring(0, 4) + "_"
                        + name.substring(4);
                sysPermission_POST.setName(name);
            }
        } else {
            sysPermission_POST.setName("AUTH_" + name);
        }
        sysPermission_POST.setId(UUIDUtil.getUUID32());
        sysPermission_POST.setPriority(Integer.parseInt(priority_POST));
        sysPermission_POST.setRemarks(permissionRemarks_POST);
        sysPermission_POST.setMethods("POST");

        //保存权限资源表
        SysPermissionResources sysPermissionResources = new SysPermissionResources();
        sysPermissionResources.setResourceId(sysResources.getId());
        sysPermissionResources.setPermissionId(sysPermission_POST.getId());

        sysResourcesList.add(new SaveResourcesObject(sysResources, sysPermission_POST, sysPermissionResources));


        if (URL_POST_Table != null && URL_POST_Table.length > 0 && additionalURL_POST != null && additionalURL_POST.length > 0) {
            for (int m = 0; m < URL_POST_Table.length; m++) {//循环全部提交的附加URL数组
                String additional = additionalURL_POST[m];//附加URL(POST方式)
                if (additional != null && !additional.trim().isEmpty()) {
                    //保存资源
                    SysResources sysResources_additionalURL = new SysResources();//附加URL
                    sysResources_additionalURL.setId(UUIDUtil.getUUID32());
                    sysResources_additionalURL.setPriority(m);
                    if (additionalURL_remarks_POST != null && additionalURL_remarks_POST.length > 0) {
                        sysResources_additionalURL.setName(additionalURL_remarks_POST[m]);//附加URL备注(GET方式)
                    }
                    sysResources_additionalURL.setUrl(additional.trim());
                    sysResources_additionalURL.setUrlType(2);//附加URL方式
                    sysResources_additionalURL.setUrlParentId(sysResources.getId());//设置父ID
                    sysResources_additionalURL.setModule(sysResources.getModule());

                    //保存权限资源表
                    SysPermissionResources sysPermissionResourcesURL = new SysPermissionResources();
                    sysPermissionResourcesURL.setResourceId(sysResources_additionalURL.getId());
                    sysPermissionResourcesURL.setPermissionId(sysPermission_POST.getId());

                    sysResourcesList.add(new SaveResourcesObject(sysResources_additionalURL, null, sysPermissionResourcesURL));
                }
            }
        }
    }


    /**
     * 处理 NULL 请求方式的更新逻辑
     *
     * @param sysResources                资源
     * @param sysPermission_NULL          权限
     * @param permissionId_NULL           权限Id
     * @param sysPermissionName_NULL      资源权限参数
     * @param priority_NULL               权限优先级
     * @param permissionRemarks_NULL      权限说明
     * @param sysPermissionResources_NULL 权限资源NULL方式
     * @param errors                      错误信息
     */
    private void processNullMethodUpdate(SysResources sysResources, SysPermission sysPermission_NULL, String permissionId_NULL, String sysPermissionName_NULL, String priority_NULL, String permissionRemarks_NULL, SysPermissionResources sysPermissionResources_NULL, Map<String, String> errors) {
        Map<String, String> validationErrors = new HashMap<>();

        if (sysPermissionName_NULL == null || sysPermissionName_NULL.trim().isEmpty()) {
            validationErrors.put("sysPermissionName_NULL", "资源权限参数不能为空");
        }
        if (!StringUtils.isNumeric(priority_NULL)) {
            validationErrors.put("priority_NULL", "请填写数字类型");
        }
        if (permissionRemarks_NULL == null || permissionRemarks_NULL.trim().isEmpty()) {
            validationErrors.put("permissionRemarks_NULL", "权限说明不能为空");
        }
        if (!validationErrors.isEmpty()) {
            errors.putAll(validationErrors);
            return;
        }


        // 当权限标识前缀不为"AUTH_"时，要添加该标识。
        if (sysPermissionName_NULL.startsWith("AUTH_")) {
            sysPermission_NULL.setName(sysPermissionName_NULL);
        } else {
            sysPermission_NULL.setName("AUTH_" + sysPermissionName_NULL);
        }
        if (permissionId_NULL != null && !permissionId_NULL.isEmpty()) {
            sysPermission_NULL.setId(permissionId_NULL);
        } else {
            sysPermission_NULL.setId(UUIDUtil.getUUID32());
        }
        sysPermission_NULL.setPriority(Integer.parseInt(priority_NULL));
        sysPermission_NULL.setRemarks(permissionRemarks_NULL);

        //保存权限资源表
        sysPermissionResources_NULL.setResourceId(sysResources.getId());
        sysPermissionResources_NULL.setPermissionId(sysPermission_NULL.getId());

    }

    /**
     * 处理 GET 请求方式的更新逻辑
     *
     * @param sysResources               资源
     * @param permissionId_GET           权限Id(GET方式)
     * @param sysPermissionName_GET      资源权限参数
     * @param priority_GET               权限优先级
     * @param permissionRemarks_GET      权限说明
     * @param URL_GET_Table              附加URL数组数量(GET方式)
     * @param additionalURL_GET          附加URL(GET方式)
     * @param additionalURL_remarks_GET  附加URL备注(GET方式)
     * @param additionalURL_ID_GET       附加URL Id(GET方式)
     * @param sysPermission_GET          权限
     * @param sysPermissionResources_GET 权限资源
     * @param resourcesObjectList        保存资源对象
     * @param errors                     错误信息
     */
    private void processGetMethodUpdate(SysResources sysResources,
                                        String permissionId_GET, String[] sysPermissionName_GET, String priority_GET, String permissionRemarks_GET,
                                        Integer[] URL_GET_Table, String[] additionalURL_GET, String[] additionalURL_remarks_GET, String[] additionalURL_ID_GET,
                                        SysPermission sysPermission_GET, SysPermissionResources sysPermissionResources_GET, List<SaveResourcesObject> resourcesObjectList, Map<String, String> errors) {
        Map<String, String> validationErrors = new HashMap<>();

        if (sysPermissionName_GET == null || sysPermissionName_GET.length == 0) {
            validationErrors.put("sysPermissionName_GET", "资源权限不能为空");
        }
        if (!StringUtils.isNumeric(priority_GET)) {
            validationErrors.put("priority_GET", "权限优先级请填写数字类型");
        }
        if (permissionRemarks_GET == null || permissionRemarks_GET.trim().isEmpty()) {
            validationErrors.put("permissionRemarks_GET", "权限说明不能为空");
        }
        if (!validationErrors.isEmpty()) {
            errors.putAll(validationErrors);
            return;
        }


        StringBuffer sb = new StringBuffer();

        sb.append(sysResources.getId()).append("_");
        for (String permissionName : sysPermissionName_GET) {
            sb.append(permissionName).append("_");
        }
        sb.deleteCharAt(sb.length() - 1);

        String name = sb.toString();
        // 当权限标识前缀不为"AUTH_"时，要添加该标识。
        if (name.startsWith("AUTH")) {
            if (!name.startsWith("AUTH_")) {
                name = name.substring(0, 4) + "_"
                        + name.substring(4);
                sysPermission_GET.setName(name);
            }
        } else {
            sysPermission_GET.setName("AUTH_" + name);
        }
        //保存权限
        if (permissionId_GET != null && !permissionId_GET.isEmpty()) {
            sysPermission_GET.setId(permissionId_GET);
        } else {
            sysPermission_GET.setId(UUIDUtil.getUUID32());
        }
        sysPermission_GET.setPriority(Integer.parseInt(priority_GET));
        sysPermission_GET.setRemarks(permissionRemarks_GET);
        sysPermission_GET.setMethods("GET");


        //保存权限资源表
        sysPermissionResources_GET.setResourceId(sysResources.getId());
        sysPermissionResources_GET.setPermissionId(sysPermission_GET.getId());


        if (URL_GET_Table != null && URL_GET_Table.length > 0 && additionalURL_GET != null && additionalURL_GET.length > 0) {
            for (int m = 0; m < URL_GET_Table.length; m++) {//循环全部提交的附加URL数组
                String additional = additionalURL_GET[m];//附加URL(GET方式)
                if (additional != null && !additional.trim().isEmpty()) {
                    //保存资源
                    SysResources sysResources_additionalURL = new SysResources();//附加URL
                    if (additionalURL_ID_GET != null && additionalURL_ID_GET.length > 0 && additionalURL_ID_GET[m] != null && !"".equals(additionalURL_ID_GET[m])) {
                        sysResources_additionalURL.setId(additionalURL_ID_GET[m]);
                    } else {
                        sysResources_additionalURL.setId(UUIDUtil.getUUID32());
                    }

                    sysResources_additionalURL.setPriority(m);
                    if (additionalURL_remarks_GET != null && additionalURL_remarks_GET.length > 0) {
                        sysResources_additionalURL.setName(additionalURL_remarks_GET[m]);//附加URL备注(GET方式)
                    }
                    sysResources_additionalURL.setUrl(additional.trim());
                    sysResources_additionalURL.setUrlType(1);//附加URL方式
                    sysResources_additionalURL.setUrlParentId(sysResources.getId());//设置父ID
                    sysResources_additionalURL.setModule(sysResources.getModule());

                    //保存权限资源表
                    SysPermissionResources sysPermissionResourcesURL = new SysPermissionResources();
                    sysPermissionResourcesURL.setResourceId(sysResources_additionalURL.getId());
                    sysPermissionResourcesURL.setPermissionId(sysPermission_GET.getId());

                    resourcesObjectList.add(new SaveResourcesObject(sysResources_additionalURL, null, sysPermissionResourcesURL));

                }
            }
        }

    }

    /**
     * 处理 POST 请求方式的更新逻辑
     *
     * @param sysResources                资源
     * @param permissionId_POST           权限Id(POST方式)
     * @param sysPermissionName_POST      资源权限参数
     * @param priority_POST               权限优先级
     * @param permissionRemarks_POST      权限说明
     * @param URL_POST_Table              附加URL数组数量(POST方式)
     * @param additionalURL_POST          附加URL(POST方式)
     * @param additionalURL_remarks_POST  附加URL备注(POST方式)
     * @param additionalURL_ID_POST       附加URL Id(POST方式)
     * @param sysPermission_POST          权限
     * @param sysPermissionResources_POST 权限资源
     * @param resourcesObjectList         保存资源对象
     * @param errors                      错误信息
     */
    private void processPOSTMethodUpdate(SysResources sysResources,
                                         String permissionId_POST, String[] sysPermissionName_POST, String priority_POST, String permissionRemarks_POST,
                                         Integer[] URL_POST_Table, String[] additionalURL_POST, String[] additionalURL_remarks_POST, String[] additionalURL_ID_POST,
                                         SysPermission sysPermission_POST, SysPermissionResources sysPermissionResources_POST, List<SaveResourcesObject> resourcesObjectList, Map<String, String> errors) {
        Map<String, String> validationErrors = new HashMap<>();

        if (sysPermissionName_POST == null || sysPermissionName_POST.length == 0) {
            validationErrors.put("sysPermissionName_POST", "资源权限不能为空");
        }
        if (!StringUtils.isNumeric(priority_POST)) {
            validationErrors.put("priority_POST", "权限优先级请填写数字类型");
        }
        if (permissionRemarks_POST == null || permissionRemarks_POST.trim().isEmpty()) {
            validationErrors.put("permissionRemarks_POST", "权限说明不能为空");
        }

        if (!validationErrors.isEmpty()) {
            errors.putAll(validationErrors);
            return;
        }


        StringBuffer sb = new StringBuffer();

        sb.append(sysResources.getId()).append("_");
        for (String permissionName : sysPermissionName_POST) {
            sb.append(permissionName).append("_");
        }
        sb.deleteCharAt(sb.length() - 1);

        String name = sb.toString();
        // 当权限标识前缀不为"AUTH_"时，要添加该标识。
        if (name.startsWith("AUTH")) {
            if (!name.startsWith("AUTH_")) {
                name = name.substring(0, 4) + "_"
                        + name.substring(4);
                sysPermission_POST.setName(name);
            }
        } else {
            sysPermission_POST.setName("AUTH_" + name);
        }
        if (permissionId_POST != null && !permissionId_POST.isEmpty()) {
            sysPermission_POST.setId(permissionId_POST);
        } else {
            sysPermission_POST.setId(UUIDUtil.getUUID32());
        }
        sysPermission_POST.setPriority(Integer.parseInt(priority_POST));
        sysPermission_POST.setRemarks(permissionRemarks_POST);
        sysPermission_POST.setMethods("POST");

        //保存权限资源表
        sysPermissionResources_POST.setResourceId(sysResources.getId());
        sysPermissionResources_POST.setPermissionId(sysPermission_POST.getId());

        if (URL_POST_Table != null && URL_POST_Table.length > 0 && additionalURL_POST != null && additionalURL_POST.length > 0) {
            for (int m = 0; m < URL_POST_Table.length; m++) {//循环全部提交的附加URL数组
                String additional = additionalURL_POST[m];//附加URL(POST方式)
                if (additional != null && !additional.trim().isEmpty()) {
                    //保存资源
                    SysResources sysResources_additionalURL = new SysResources();//附加URL
                    if (additionalURL_ID_POST != null && additionalURL_ID_POST.length > 0 && additionalURL_ID_POST[m] != null && !"".equals(additionalURL_ID_POST[m])) {
                        sysResources_additionalURL.setId(additionalURL_ID_POST[m]);
                    } else {
                        String ids_URL_POST = UUIDUtil.getUUID32();
                        sysResources_additionalURL.setId(ids_URL_POST);
                    }

                    sysResources_additionalURL.setPriority(m);
                    if (additionalURL_remarks_POST != null && additionalURL_remarks_POST.length > 0) {
                        sysResources_additionalURL.setName(additionalURL_remarks_POST[m]);//附加URL备注(GET方式)
                    }
                    sysResources_additionalURL.setUrl(additional.trim());
                    sysResources_additionalURL.setUrlType(2);//附加URL方式
                    sysResources_additionalURL.setUrlParentId(sysResources.getId());//设置父ID
                    sysResources_additionalURL.setModule(sysResources.getModule());

                    //保存权限资源表
                    SysPermissionResources sysPermissionResourcesURL = new SysPermissionResources();
                    sysPermissionResourcesURL.setResourceId(sysResources_additionalURL.getId());
                    sysPermissionResourcesURL.setPermissionId(sysPermission_POST.getId());

                    resourcesObjectList.add(new SaveResourcesObject(sysResources_additionalURL, null, sysPermissionResourcesURL));

                }
            }
        }
    }

    /**
     * 根据当前用户权限筛选有效的权限ID。
     * @param selectedPermissionIds 选中的权限Id集合
     * @param loginUser 登录的员工
     * @return
     */
    private Set<String> filterValidPermissions(String[] selectedPermissionIds, SysUsers loginUser) {
        if (selectedPermissionIds == null || selectedPermissionIds.length == 0) {
            return Collections.emptySet();
        }
        Set<String> permissionIdList = new HashSet<String>();//可用的权限ID

        if (loginUser.isIssys()) {//超级用户
            List<PermissionObject> modulePermissionList = aclRepository.findModulePermission();

            List<String> allSysPermissionId = new ArrayList<String>();//系统所有权限ID
            if(modulePermissionList != null && modulePermissionList.size() >0){
                for (PermissionObject permissionObject : modulePermissionList) {
                    //如果为系统最大权限则不可选
                    if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
                        continue;
                    }
                    allSysPermissionId.add(permissionObject.getPermissionId());
                }
                for(String spi :selectedPermissionIds){
                    if(allSysPermissionId.contains(spi)){
                        permissionIdList.add(spi);
                    }
                }

            }
        } else {//普通用户
            List<String> userPermissionId = staffRepository.findPermissionIdByUserAccount(loginUser.getUserAccount());
            if(userPermissionId != null && userPermissionId.size() >0){
                for(String s : userPermissionId){
                    //如果为系统最大权限则不可选
                    if("99999999999999999999999999999999".equals(s)){
                        continue;
                    }
                    for(String spi :selectedPermissionIds){
                        if(s.equals(spi)){
                            permissionIdList.add(s);
                        }
                    }
                }
            }
        }
        return permissionIdList;
    }

    /**
     * 获取‘后台框架’Id
     * @return
     */
    private String getSystemPermissionId() {
        List<PermissionObject> modulePermissionList = aclRepository.findModulePermission();
        if(modulePermissionList != null && modulePermissionList.size() >0){
            for (PermissionObject permissionObject : modulePermissionList) {
                //如果为系统最大权限则不可选
                if("99999999999999999999999999999999".equals(permissionObject.getPermissionId())){
                    continue;
                }

                //自动勾选"系统-->后台框架"
                if("系统".equals(permissionObject.getModule())){
                    return permissionObject.getPermissionId();
                }
            }
        }
        return "";
    }

    /**
     * 用户是否有权限
     * @param loginUser 登录员工
     * @param rolesId 角色Id
     * @param errors 错误信息
     */
    private void validateUserPermission(SysUsers loginUser,String rolesId,Map<String, String> errors){
        Set<String> userAuthority = new HashSet<String>();//用户权限
        if(!loginUser.getAuthorities().isEmpty()){
            for(GrantedAuthority ga: loginUser.getAuthorities()){
                userAuthority.add(ga.getAuthority());
            }
        }

        Set<String> rolesPermissionName = new HashSet<String>();//当前角色权限name
        List<SysPermission> sysPermissionList = aclRepository.findPermissionByRolesId(rolesId);
        if(sysPermissionList != null && sysPermissionList.size() >0){
            for(SysPermission sysPermission : sysPermissionList){
                rolesPermissionName.add(sysPermission.getName());
            }
        }
        //检查是否越权修改  只能修改权限比自己角色小的角色
        if(userAuthority.containsAll(rolesPermissionName)){//如果用户权限不包含角色权限
            if(userAuthority.size() == rolesPermissionName.size()){
                errors.put("permission", "不能修改和自己相同权限的角色");
            }
        }else{
            errors.put("permission", "账号权限不足");
        }
    }
}