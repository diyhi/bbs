package cms.service.user;


import cms.dto.user.UserRoleRequest;
import cms.model.user.UserRole;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;


/**
 * 用户角色服务
 */
public interface UserRoleService {

    /**
     * 获取所有用户角色
     * @return
     */
    public List<UserRole> getAllRole();
    /**
     * 获取添加用户角色界面信息
     * @return
     */
    public Map<String,Object> getAddUserRoleViewModel();
    /**
     * 添加用户角色
     * @param userRoleRequest 用户角色表单
     * @param request 请求信息
     */
    public void addUserRole(UserRoleRequest userRoleRequest, HttpServletRequest request);
    /**
     * 获取修改用户角色界面信息
     * @param userRoleId 用户角色Id
     * @return
     */
    public Map<String,Object> getEditUserRoleViewModel(String userRoleId);
    /**
     * 修改用户角色
     * @param userRoleRequest 用户角色表单
     * @param request 请求信息
     */
    public void editUserRole(UserRoleRequest userRoleRequest, HttpServletRequest request);
    /**
     * 修改为默认角色
     * @param userRoleId 用户角色Id
     * @param isDefaultRole 是否设置为默认角色
     */
    public void editToDefaultRole(String userRoleId,Boolean isDefaultRole);
    /**
     * 删除用户角色
     * @param userRoleId 用户角色Id
     */
    public void deleteUserRole(String userRoleId);
}