package cms.service.user;


import cms.model.user.User;
import cms.model.user.UserGrade;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * 用户等级服务
 */
public interface UserGradeService {

    /**
     * 获取所有用户等级
     */
    public List<UserGrade> getAllUserGrade();

    /**
     * 添加用户等级
     * @param userGradeForm 用户等级表单
     */
    public void addUserGrade(UserGrade userGradeForm);

    /**
     * 获取修改用户等级界面信息
     * @param userGradeId 用户等级Id
     * @return
     */
    public UserGrade getEditUserGradeViewModel(Integer userGradeId);
    /**
     * 修改用户等级
     * @param userGradeForm 用户等级表单
     */
    public void editUserGrade(UserGrade userGradeForm);
    /**
     * 删除用户等级
     * @param id 用户等级Id
     */
    public void deleteUserGrade(Integer id);
}