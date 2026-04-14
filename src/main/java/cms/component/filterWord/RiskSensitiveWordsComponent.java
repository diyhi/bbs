package cms.component.filterWord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import cms.component.JsonComponent;
import cms.model.filterWord.RiskSensitiveWords;
import cms.model.riskControl.AssociateSensitiveWord;
import cms.model.riskControl.RiskControlModel;
import cms.repository.filterWord.RiskSensitiveWordsRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import tools.jackson.core.type.TypeReference;

import cms.utils.RateLimiterUtil;

/**
 * 风控敏感词组件
 * @author Gao
 *
 */
@Component("riskSensitiveWordsComponent")
public class RiskSensitiveWordsComponent {
	
	@Resource RiskSensitiveWordsRepository riskSensitiveWordsRepository;
    @Resource JsonComponent jsonComponent;


	//key:风控敏感词Id  value: DFA单词树
	private final static ConcurrentHashMap<Integer,Words> wordsMap = new ConcurrentHashMap<Integer,Words>();
	//key:风控敏感词Id  value: 版本号
	private final static ConcurrentHashMap<Integer,Integer> versionMap = new ConcurrentHashMap<Integer,Integer>();
	
	
	
	/**
	 * 加载敏感词
	 * @param riskControlModel 风控模型
	 * @return
	 */
	public void loadSensitiveWords(RiskControlModel riskControlModel){
		if(riskControlModel.getAssociateSensitiveWordList() != null && riskControlModel.getAssociateSensitiveWordList().size() >0){
			for(AssociateSensitiveWord associateSensitiveWord : riskControlModel.getAssociateSensitiveWordList()){
				if(versionMap.get(associateSensitiveWord.getRiskSensitiveWordsId()) == null){//如果还没有加载敏感词
					this.addRiskSensitiveWords(associateSensitiveWord.getRiskSensitiveWordsId());
				}else{//加入定期维护任务
					if(RateLimiterUtil.apply_minutes("loadSensitiveWords", 3,20L)){//限流 每分钟最多执行3次
						refreshWords();
					}
				}
			}
		}
	}
	
	
	/**
	 * 添加敏感词到DFA单词树
	 * @param riskSensitiveWordsId 风控敏感词Id
	 * @return
	 */
	public void addRiskSensitiveWords(Integer riskSensitiveWordsId){
		
		RiskSensitiveWords riskSensitiveWords = riskSensitiveWordsRepository.findById(riskSensitiveWordsId);
		if(riskSensitiveWords != null){
			if(riskSensitiveWords.getWords() != null && !riskSensitiveWords.getWords().trim().isEmpty()){
				List<String> wordsList = jsonComponent.toGenericObject(riskSensitiveWords.getWords(), new TypeReference< List<String> >(){});
				if(wordsList != null && wordsList.size() >0){
					Words words = new Words();
					for(String w : wordsList){
						words.addWord(w);
					}
					wordsMap.put(riskSensitiveWordsId, words);
				}
			}
			versionMap.put(riskSensitiveWordsId, riskSensitiveWords.getVersion());
		}
	}
	
	
	/**
	 * 刷新DFA单词树
	 * @return
	 */
	public void refreshWords(){
		List<RiskSensitiveWords> riskSensitiveWordsList = riskSensitiveWordsRepository.findAllRiskSensitiveWords_cache();
		if(riskSensitiveWordsList != null && riskSensitiveWordsList.size() >0){
			for(RiskSensitiveWords riskSensitiveWords: riskSensitiveWordsList){
				Integer version = versionMap.get(riskSensitiveWords.getId());
				if(version != null && !version.equals(riskSensitiveWords.getVersion())){
					Words words = wordsMap.get(riskSensitiveWords.getId());
					if(words != null){
						words.clear();
					}
					this.addRiskSensitiveWords(riskSensitiveWords.getId());
				}
			}
		}else{
			clearSensitiveWords();
		}
	}
	
	
	
	/**
	 * 匹配的所有敏感词
	 * @param riskControlModel 风控模型
	 * @param text 文本
	 * @return
	 */
	public List<String> matchAllSensitiveWords(RiskControlModel riskControlModel,String text){
		List<String> allSensitiveWordsList = new ArrayList<String>();
		if(riskControlModel.getAssociateSensitiveWordList() != null && riskControlModel.getAssociateSensitiveWordList().size() >0){
			for(AssociateSensitiveWord associateSensitiveWord : riskControlModel.getAssociateSensitiveWordList()){
				Words words = wordsMap.get(associateSensitiveWord.getRiskSensitiveWordsId());
				if(words != null){
					List<String> sensitiveWordsList = words.matchAll(text);
					allSensitiveWordsList.addAll(sensitiveWordsList);
				}
				
			}
		}
		return allSensitiveWordsList;
	}
	
	/**
	 * 匹配敏感词
	 * @param riskSensitiveWordsId 风控敏感词Id
	 * @param text 文本
	 * @return
	 */
	public List<String> matchSensitiveWords(Integer riskSensitiveWordsId,String text){
		List<String> sensitiveWordsList = new ArrayList<String>();
		Words words = wordsMap.get(riskSensitiveWordsId);
		if(words != null){
			sensitiveWordsList.addAll(words.matchAll(text));
		}
		return sensitiveWordsList;
	}
	
	/**
	 * 清空词库
	 */
	public void clearSensitiveWords(){
		versionMap.clear();
		for (Map.Entry<Integer,Words> entry : wordsMap.entrySet()) {
			entry.getValue().clear();//清空词库
		}
		wordsMap.clear();
	}
	
	
}
