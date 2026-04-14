package cms.service.staff;

import cms.dto.PageView;
import cms.dto.staff.PermissionObject;
import cms.model.staff.SysResources;
import cms.model.staff.SysRoles;
import cms.model.staff.SysUsers;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 访问控制服务
 */
public interface ACLService {


    /**
     * 获取资源列表
     *
     * @param page 页码
     * @return 分页视图对象
     */
    public PageView<SysRoles> getRolesList(int page);

    /**
     * 获取模块名称列表
     *
     * @param page 页码
     * @return 分页视图对象
     */
    public PageView<SysResources> getModuleNames(int page);



    /**
     * 删除资源
     * @param resourcesId 资源Id
     */
    public void deleteResources(String resourcesId);

    /**
     * 获取添加角色界面信息
     * @return
     */
    public LinkedHashMap<String, List<PermissionObject>> getAddRolesViewModel();
    /**
     * 添加角色界
     * @param sysRolesForm 角色表单
     * @param sysPermissionId 权限Id集合
     */
    public void addRoles(SysRoles sysRolesForm,String[] sysPermissionId);
    /**
     * 获取修改角色界面信息
     * @param rolesId 角色Id
     */
    public Map<String,Object> getEditRolesViewModel(String rolesId);
    /**
     * 修改角色
     * @param sysRolesForm 角色表单
     * @param sysPermissionId 权限Id集合
     * @param rolesId 角色Id
     */
    public void editRoles(SysRoles sysRolesForm, String[] sysPermissionId,String rolesId);
    /**
     * 删除角色
     * @param rolesId 角色Id
     */
    public void deleteRoles(String rolesId);
}