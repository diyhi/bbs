package cms.repository.filterWord;


import cms.model.filterWord.RiskSensitiveWords;
import cms.repository.besa.DAO;

import java.util.List;



/**
 * 风控敏感词管理接口
 *
 */
public interface RiskSensitiveWordsRepository extends DAO<RiskSensitiveWords> {
	/**
	 * 根据Id查询风控敏感词
	 * @param riskSensitiveWordsId 风控敏感词Id
	 * @return
	 */
	public RiskSensitiveWords findById(Integer riskSensitiveWordsId);
	/**
	 * 查询所有风控敏感词(不返回words字段)
	 * @return
	 */
	public List<RiskSensitiveWords> findAllRiskSensitiveWords();
	/**
	 * 查询所有风控敏感词(不返回words字段) - 缓存
	 * @return
	 */
	public List<RiskSensitiveWords> findAllRiskSensitiveWords_cache();
	
	/**
	 * 保存风控敏感词
	 * @param riskSensitiveWords 风控敏感词
	 */
	public void saveRiskSensitiveWords(RiskSensitiveWords riskSensitiveWords);
	/**
	 * 修改风控敏感词
	 * @param riskSensitiveWords 风控敏感词
	 * @return
	 */
	public Integer updateRiskSensitiveWords(RiskSensitiveWords riskSensitiveWords);
	/**
	 * 删除风控敏感词
	 * @param riskSensitiveWordsId 风控敏感词Id
	 */
	public Integer deleteRiskSensitiveWords(Integer riskSensitiveWordsId);
	
}
