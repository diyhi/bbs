package cms.web.action.filterWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 单词树
 *
 */
public class Words extends HashMap<Character, Words>{
	private static final long serialVersionUID = -3360553487822917751L;

	/** 默认的类型 */
	public final static int DEFAULT_TYPE = 0;
	
	/** 关键字类型，是否最后一个字符 */
	public Map<Integer, Boolean> types = new HashMap<Integer, Boolean>();
	
	//------------------------------------------------------------------------------- 构造 start
	/**
	 * 默认构造
	 */
	public Words() {
	}
	
	/**
	 * 构造方法
	 * @param type 类型
	 */
	public Words(int type) {
		types.put(type, false);
	}
	//------------------------------------------------------------------------------- 构造 end
	
	/**
	 * 添加单词，使用默认类型
	 * @param word 单词
	 */
	public void addWord(String word) {
		this.addWord(word, DEFAULT_TYPE);
	}
	
	/**
	 * 增加单词
	 * @param word 单词
	 * @param type 类型
	 */
	public void addWord(String word, int type){
		//已经是单词的末尾
		if(StringUtils.isBlank(word)){
			this.types.put(type, true);
			this.clear();
			return;
		}
		//已经是关键字的末尾，则结束（即只保留最短字符串）
		Boolean isEnd = this.types.get(type);
		if(isEnd != null && isEnd) return;
		this.types.put(type, false);
		
		word = word.trim();
		char currentChar = word.charAt(0);
		//跳过特殊字符（跳过本字符，从下一个字符开始）
		if(StopChar.isStopChar(currentChar)){
			this.addWord(word.substring(1), type);
			return;
		}
		
		Words child = this.get(currentChar);
		if(child != null){
			//已经存在子节点，在子节点中存放下一个字符
			child.addWord(word.substring(1), type);
		}else{
			//无子类，新建一个子节点后存放下一个字符
			child = new Words(type);
			this.put(currentChar, child);
			child.addWord(word.substring(1), type);
		}
	}
	
	/**
	 * 指定文本是否包含树中的词，默认类型
	 * @param text 被检查的文本
	 * @return 是否包含
	 */
	public boolean contains(String text) {
		return contains(text, DEFAULT_TYPE);
	}
	
	/**
	 * 指定文本是否包含树中的词
	 * @param text 被检查的文本
	 * @param type 类型
	 * @return 是否包含
	 */
	public boolean contains(String text, int type){
		while(! StringUtils.isBlank(text)){
			Words words = this;
			char[] array = text.toCharArray();
			for (char c : array) {
				//跳过特殊字符
				if(StopChar.isStopChar(c)) continue;
				
				//子节点中子节点、类型为空表示词不存在
				words = words.get(c);
				if(words == null) break;
				Boolean isEnd = words.types.get(type);
				if(isEnd == null) break; 
				
				if(isEnd) return true;
			}
			text = text.substring(1);
		}
		return false;
	}
	
	/**
	 * 获得第一个匹配的关键字，默认类型
	 * @param text 被检查的文本
	 * @return 匹配到的关键字
	 */
	public String getFindedFirstWord(String text) {
		return getFindedFirstWord(text, DEFAULT_TYPE);
	}
	
	/**
	 * 获得第一个匹配的关键字
	 * @param text 被检查的文本
	 * @param type 类型
	 * @return 匹配到的关键字
	 */
	public String getFindedFirstWord(String text, int type){
		while(! StringUtils.isBlank(text)){
			StringBuilder sb = new StringBuilder();
			Words words = this;
			char[] array = text.toCharArray();
			for (char c : array) {
				//跳过特殊字符
				if(StopChar.isStopChar(c)) continue;
				
				//子节点中子节点、类型为空表示词不存在
				words = words.get(c);
				if(words == null) break;
				sb.append(c);
				Boolean isEnd = words.types.get(type);
				if(isEnd == null) break; 
				
				if(isEnd) return sb.toString();
			}
			//从下一个字符开始查找
			text = text.substring(1);
		}
		return null;
	}
	
	/**
	 * 找出所有匹配的关键字，默认类型
	 * @param text 被检查的文本
	 * @return 匹配的词列表
	 */
	public List<String> getFindedAllWords(String text) {
		return getFindedAllWords(text, DEFAULT_TYPE);
	}
	
	/**
	 * 找出所有匹配的关键字
	 * @param text 被检查的文本
	 * @param type 文本的类型
	 * @return 匹配的词列表
	 */
	public List<String> getFindedAllWords(String text, int type) {
		List<String> findedWords = new ArrayList<String>();
		while(!StringUtils.isBlank(text)){
			StringBuilder sb = new StringBuilder();
			Words words = this;
			char[] array = text.toCharArray();
			for (char c : array) {
				//跳过特殊字符
				if(StopChar.isStopChar(c)) continue;
				
				//子节点中子节点、类型为空表示词不存在
				words = words.get(c);
				if(words == null) break;
				sb.append(c);
				Boolean isEnd = words.types.get(type);
				if(isEnd == null) break; 
				
				//到达单词末尾，关键词成立，从此词的下一个位置开始查找
				if(isEnd) {
					findedWords.add(sb.toString());
					int len = sb.length();
					sb.delete(0, len);
					text = text.substring(len);
					break;
				}
			}
			//从下一个字符开始
			if(text.length() > 0) {
				text = text.substring(1);
			}
		}
		return findedWords;
	}
	/**
	 * 找出所有匹配的原始关键字
	 * @param text 被检查的文本
	 * @param type 文本的类型
	 * @return 匹配的词列表
	 */
	public List<String> getFindedAllOriginalWords(String text) {
		return this.getFindedAllOriginalWords(text, DEFAULT_TYPE);
	}
	
	/**
	 * 找出所有匹配的原始关键字
	 * @param text 被检查的文本
	 * @param type 文本的类型
	 * @return 匹配的词列表
	 */
	public List<String> getFindedAllOriginalWords(String text, int type) {
		List<String> findedWords = new ArrayList<String>();
		while(!StringUtils.isBlank(text)){
			StringBuilder sb = new StringBuilder();
			Words words = this;
			char[] array = text.toCharArray();
			for (char c : array) {
				sb.append(c);
				//跳过特殊字符
				if(StopChar.isStopChar(c)) continue;
				
				//子节点中子节点、类型为空表示词不存在
				words = words.get(c);
				if(words == null) break;
				Boolean isEnd = words.types.get(type);
				if(isEnd == null) break; 
				
				//到达单词末尾，关键词成立，从此词的下一个位置开始查找
				if(isEnd) {
					findedWords.add(sb.toString());
					int len = sb.length();
					sb.delete(0, len);
					text = text.substring(len);
					break;
				}
			}
			//从下一个字符开始
			if(text.length() > 0) {
				text = text.substring(1);
			}
		}
		return findedWords;
	}
}
