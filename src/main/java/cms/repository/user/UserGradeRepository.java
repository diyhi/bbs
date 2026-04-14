package cms.repository.user;

import cms.model.user.UserGrade;
import cms.repository.besa.DAO;

import java.util.List;



/**
 * 会员等级接口
 *
 */
public interface UserGradeRepository extends DAO<UserGrade> {
	/**
	 * 根据Id查询等级
	 * @param userGradeId 等级Id
	 * @return
	 */
	public UserGrade findGradeById(Integer userGradeId);
	/**
	 * 根据需要积分查询等级
	 * @param needPoint 需要积分
	 * @return
	 */
	public UserGrade findGradeByNeedPoint(Long needPoint);
	/**
	 * 查询所有设置的等级
	 */
	public List<UserGrade> findAllGrade();
	/**
	 * 查询所有设置的等级 - 缓存
	 */
	public List<UserGrade> findAllGrade_cache();
	/**
	 * 保存用户等级
	 * @param userGrade 用户等级
	 */
	public void saveUserGrade(UserGrade userGrade);
	
	/**
	 * 修改用户等级
	 * @param userGrade 用户等级
	 */
	public void updateUserGrade(UserGrade userGrade);
	/**
	 * 删除用户等级
	 * @param userGradeId 用户等级Id
	 */
	public int deleteUserGrade(Integer userGradeId);
}
