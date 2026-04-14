package cms.service.staff;

import cms.dto.admin.AdminAuthorization;
import cms.model.staff.SysUsers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 员工服务
 */
public interface StaffService {

    /**
     * 获取员工列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getStaffList(int page,String fileServerAddress);

    /**
     * 获取添加员工界面信息
     * @param username 员工账号
     * @param isSysAdmin 是否为超级用户
     * @return
     */
    public Map<String,Object> getAddStaffViewModel(String username,boolean isSysAdmin);

    /**
     * 添加员工
     * @param sysUsersForm 员工表单
     * @param repeatPassword 重复密码
     * @param sysRolesId 角色Id集合
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     * @param file 头像文件
     */
    public void addStaff(SysUsers sysUsersForm, String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y,MultipartFile file);

    /**
     * 获取修改员工界面信息
     * @param userId 员工Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditStaffViewModel(String userId,String fileServerAddress);
    /**
     * 修改员工
     * @param sysUsersForm   员工表单
     * @param userId   员工Id
     * @param repeatPassword 重复密码
     * @param sysRolesId     角色Id集合
     * @param width          图片宽
     * @param height         图片高
     * @param x              X轴
     * @param y              Y轴
     * @param file           头像文件
     */
    public void editStaff(SysUsers sysUsersForm,String userId, String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y, MultipartFile file);

    /**
     * 获取员工修改自身界面信息
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditStaffSelfViewModel(String fileServerAddress);
    /**
     * 修改员工自身信息
     * @param sysUsersForm   员工表单
     * @param repeatPassword 重复密码
     * @param sysRolesId     角色Id集合
     * @param width          图片宽
     * @param height         图片高
     * @param x              X轴
     * @param y              Y轴
     * @param file           头像文件
     */
    public void editSelfInfo(SysUsers sysUsersForm,String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y, MultipartFile file);
        /**
         * 删除员工
         * @param userId   员工Id
         */
    public void deleteStaff(String userId);
}
