package cms.repository.user.impl;


import java.util.List;


import cms.model.user.UserGrade;
import cms.repository.besa.DaoSupport;
import cms.repository.user.UserGradeRepository;
import jakarta.persistence.Query;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * 会员等级管理接口实现类
 *
 */
@Repository
@Transactional
public class UserGradeRepositoryImpl extends DaoSupport<UserGrade> implements
        UserGradeRepository {

	/**
	 * 根据Id查询等级
	 * @param userGradeId 等级Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UserGrade findGradeById(Integer userGradeId){
		Query query =  em.createQuery("select o from UserGrade o where o.id=?1");
		query.setParameter(1, userGradeId);
		List<UserGrade> userGradeList = query.getResultList();
		if(userGradeList != null && userGradeList.size() >0){
			for(UserGrade userGrade : userGradeList){
				return userGrade;
			}
		}
		return null;
	}
	/**
	 * 根据需要积分查询等级
	 * @param needPoint 需要积分
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UserGrade findGradeByNeedPoint(Long needPoint){
		Query query =  em.createQuery("select o from UserGrade o where o.needPoint=?1");
		query.setParameter(1, needPoint);
		List<UserGrade> userGradeList = query.getResultList();
		if(userGradeList != null && userGradeList.size() >0){
			for(UserGrade userGrade : userGradeList){
				return userGrade;
			}
		}
		return null;
	}
	
	
	
	/**
	 * 查询所有设置的等级
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<UserGrade> findAllGrade(){
		Query query =  em.createQuery("select o from UserGrade o ORDER BY o.needPoint DESC");
		return query.getResultList();
	}
	/**
	 * 查询所有设置的等级 - 缓存
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="userGradeRepositoryImpl_cache",key="'findAllGrade_default'")
	public List<UserGrade> findAllGrade_cache(){
		return this.findAllGrade();
		
	}
	
	/**
	 * 保存用户等级
	 * @param userGrade 用户等级
	 */
	@CacheEvict(value="userGradeRepositoryImpl_cache",allEntries=true)
	public void saveUserGrade(UserGrade userGrade){
		this.save(userGrade);
	}
	
	/**
	 * 修改用户等级
	 * @param userGrade 用户等级
	 */
	@CacheEvict(value="userGradeRepositoryImpl_cache",allEntries=true)
	public void updateUserGrade(UserGrade userGrade){
		this.update(userGrade);
	}
	/**
	 * 删除用户等级
	 * @param userGradeId 用户等级Id
	 */
	@CacheEvict(value="userGradeRepositoryImpl_cache",allEntries=true)
	public int deleteUserGrade(Integer userGradeId){
		Query delete = em.createQuery("delete from UserGrade o where o.id=?1")
			.setParameter(1, userGradeId);
		return delete.executeUpdate();
	}
}
