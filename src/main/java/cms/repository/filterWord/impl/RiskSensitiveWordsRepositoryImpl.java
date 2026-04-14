package cms.repository.filterWord.impl;

import java.util.ArrayList;
import java.util.List;


import cms.model.filterWord.RiskSensitiveWords;
import cms.repository.besa.DaoSupport;
import cms.repository.filterWord.RiskSensitiveWordsRepository;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.utils.ObjectConversion;

/**
 * 风控敏感词管理接口实现类
 *
 */
@Repository
@Transactional
public class RiskSensitiveWordsRepositoryImpl extends DaoSupport<RiskSensitiveWords> implements RiskSensitiveWordsRepository {
	private static final Logger logger = LogManager.getLogger(RiskSensitiveWordsRepositoryImpl.class);
	
	/**
	 * 根据Id查询风控敏感词
	 * @param riskSensitiveWordsId 风控敏感词Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public RiskSensitiveWords findById(Integer riskSensitiveWordsId){
		Query query = em.createQuery("select o from RiskSensitiveWords o where o.id=?1")
			.setParameter(1, riskSensitiveWordsId);
			List<RiskSensitiveWords> list = query.getResultList();
			for(RiskSensitiveWords r : list){
				return r;
			}
		return null;
	}
	
	/**
	 * 查询所有风控敏感词(不返回words字段)
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<RiskSensitiveWords> findAllRiskSensitiveWords(){
		Query query = em.createQuery("select o.id, o.name, o.lastModified, o.version from RiskSensitiveWords o");
		
		List<Object[]> objectList = query.getResultList();
		List<RiskSensitiveWords> riskSensitiveWordsList = new ArrayList<RiskSensitiveWords>();
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				
				RiskSensitiveWords riskSensitiveWords = new RiskSensitiveWords();
				riskSensitiveWords.setId(ObjectConversion.conversion(object[0], ObjectConversion.INTEGER));
				riskSensitiveWords.setName(ObjectConversion.conversion(object[1], ObjectConversion.STRING));
				riskSensitiveWords.setLastModified(ObjectConversion.conversion(object[2], ObjectConversion.LOCALDATETIME));
				riskSensitiveWords.setVersion(ObjectConversion.conversion(object[3], ObjectConversion.INTEGER));
				riskSensitiveWordsList.add(riskSensitiveWords);
			}
		}
		return riskSensitiveWordsList;
	}
	
	
	/**
	 * 查询所有风控敏感词(不返回words字段) - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="findAllRiskSensitiveWords_cache",key="'findAllRiskSensitiveWords_default'")
	public List<RiskSensitiveWords> findAllRiskSensitiveWords_cache(){
		return this.findAllRiskSensitiveWords();
	}

	
	/**
	 * 保存风控敏感词
	 * @param riskSensitiveWords 风控敏感词
	 */
	@CacheEvict(value="findAllRiskSensitiveWords_cache",allEntries=true)
	public void saveRiskSensitiveWords(RiskSensitiveWords riskSensitiveWords){
		this.save(riskSensitiveWords);
	}
	
	/**
	 * 修改风控敏感词
	 * @param riskSensitiveWords 风控敏感词
	 * @return
	 */
	@CacheEvict(value="findAllRiskSensitiveWords_cache",allEntries=true)
	public Integer updateRiskSensitiveWords(RiskSensitiveWords riskSensitiveWords){
		Query query = em.createQuery("update RiskSensitiveWords o set o.name=?1, o.lastModified=?2,o.words=?3,o.version=o.version+1 where o.id=?4")
			.setParameter(1, riskSensitiveWords.getName())
			.setParameter(2, riskSensitiveWords.getLastModified())
			.setParameter(3, riskSensitiveWords.getWords())
			.setParameter(4, riskSensitiveWords.getId());
		return query.executeUpdate();
		
	}
	
	/**
	 * 删除风控敏感词
	 * @param riskSensitiveWordsId 风控敏感词Id
	 */
	@CacheEvict(value="findAllRiskSensitiveWords_cache",allEntries=true)
	public Integer deleteRiskSensitiveWords(Integer riskSensitiveWordsId){
		Query delete = em.createQuery("delete from RiskSensitiveWords o where o.id=?1")
				.setParameter(1,riskSensitiveWordsId);
		return delete.executeUpdate();
	}
	

}
