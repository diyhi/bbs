package cms.service.user;


import cms.dto.user.UserCustomRequest;
import cms.model.user.UserCustom;

import java.util.List;
import java.util.Map;

/**
 * 用户自定义注册功能项服务
 */
public interface UserCustomService {

    /**
     * 获取所有用户自定义注册功能项
     */
    public List<UserCustom> getAllUserCustom();
    /**
     * 添加用户自定义注册功能项
     * @param userCustomRequest 用户自定义注册功能项表单
     */
    public void addUserCustom(UserCustomRequest userCustomRequest);
    /**
     * 获取修改用户自定义注册功能项界面信息
     * @param userCustomId 用户自定义注册功能项Id
     * @return
     */
    public Map<String,Object> getEditUserCustomViewModel(Integer userCustomId);

    /**
     * 修改用户自定义注册功能项
     * @param userCustomRequest 用户自定义注册功能项表单
     */
    public void editUserCustom(UserCustomRequest userCustomRequest);
    /**
     * 删除用户自定义注册功能项
     * @param id 用户自定义注册功能项Id
     */
    public void deleteUserCustom(Integer id);
}