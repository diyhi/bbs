package cms.service.user.impl;

import cms.config.BusinessException;
import cms.model.user.UserGrade;
import cms.repository.user.UserGradeRepository;
import cms.service.user.UserGradeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 用户等级服务
 */
@Service
public class UserGradeServiceImpl implements UserGradeService {

    @Resource
    UserGradeRepository userGradeRepository;


    /**
     * 获取所有用户等级
     */
    public List<UserGrade> getAllUserGrade(){
        return userGradeRepository.findAllGrade();
    }

    /**
     * 添加用户等级
     * @param userGradeForm 用户等级表单
     */
    public void addUserGrade(UserGrade userGradeForm){

        UserGrade oldUserGrade = userGradeRepository.findGradeByNeedPoint(userGradeForm.getNeedPoint());
        if(oldUserGrade != null){
            throw new BusinessException(Map.of("needPoint", "需要积分已存在"));
        }

        UserGrade userGrade = new UserGrade();
        userGrade.setName(userGradeForm.getName());
        userGrade.setNeedPoint(userGradeForm.getNeedPoint());
        userGradeRepository.saveUserGrade(userGrade);
    }

    /**
     * 获取修改用户等级界面信息
     * @param userGradeId 用户等级Id
     * @return
     */
    public UserGrade getEditUserGradeViewModel(Integer userGradeId){
        if (userGradeId == null) {
            throw new BusinessException(Map.of("id", "用户等级Id不存在"));
        }
        UserGrade userGrade = userGradeRepository.findGradeById(userGradeId);
        if (userGrade == null) {
            throw new BusinessException(Map.of("id", "用户等级不存在"));
        }
        return userGrade;
    }

    /**
     * 修改用户等级
     * @param userGradeForm 用户等级表单
     */
    public void editUserGrade(UserGrade userGradeForm){
        if (userGradeForm.getId() == null) {
            throw new BusinessException(Map.of("id", "用户等级Id不存在"));
        }
        UserGrade userGrade = userGradeRepository.findGradeById(userGradeForm.getId());
        if (userGrade == null) {
            throw new BusinessException(Map.of("id", "用户等级不存在"));
        }
        UserGrade oldUserGrade = userGradeRepository.findGradeByNeedPoint(userGradeForm.getNeedPoint());
        if(oldUserGrade != null){
            if(!userGrade.getId().equals(oldUserGrade.getId())){
                throw new BusinessException(Map.of("needPoint", "需要积分已存在"));
            }
        }
        userGrade.setName(userGradeForm.getName());
        userGrade.setNeedPoint(userGradeForm.getNeedPoint());
        userGradeRepository.updateUserGrade(userGrade);
    }

    /**
     * 删除用户等级
     * @param id 用户等级Id
     */
    public void deleteUserGrade(Integer id){
        if (id == null) {
            throw new BusinessException(Map.of("id", "用户等级Id不存在"));
        }
        userGradeRepository.deleteUserGrade(id);
    }
}