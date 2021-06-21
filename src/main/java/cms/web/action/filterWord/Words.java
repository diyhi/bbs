package cms.web.action.filterWord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DFA单词树
 *
 */
public class Words extends HashMap<Character, Words> {
	private static final long serialVersionUID = 4120320421949440930L;
	
	/**
	 * 敏感词字符末尾标识，用于标识单词末尾字符
	 */
	private Set<Character> endCharacterSet = new HashSet<>();
	
	/**
	 * 默认构造
	 */
	public Words() {
	}
	//------------------------------------------------------------------------------- add word

	/**
	 * 增加一组单词
	 *
	 * @param words 单词集合
	 */
	public void addWords(Collection<String> words) {
		if (false == (words instanceof Set)) {
			words = new HashSet<>(words);
		}
		for (String word : words) {
			addWord(word);
		}
	}

	/**
	 * 添加单词，使用默认类型
	 *
	 * @param word 单词
	 */
	public void addWord(String word) {
		Words parent = null;
		Words current = this;
		Words child;
		char currentChar = 0;
		int length = word.length();
		for (int i = 0; i < length; i++) {
			currentChar = word.charAt(i);
			if (StopChar.isNotStopChar(currentChar)) {//只处理合法字符
				child = current.get(currentChar);
				if (child == null) {
					//无子类，新建一个子节点后存放下一个字符
					child = new Words();
					current.put(currentChar, child);
				}
				parent = current;
				current = child;
			}
		}
		if (null != parent) {
			parent.setEnd(currentChar);
		}
	}

	//------------------------------------------------------------------------------- match

	/**
	 * 指定文本是否包含树中的词
	 *
	 * @param text 被检查的文本
	 * @return 是否包含
	 */
	public boolean isMatch(String text) {
		if (null == text) {
			return false;
		}
		return null != match(text);
	}

	/**
	 * 获得第一个匹配的关键字
	 *
	 * @param text 被检查的文本
	 * @return 匹配到的关键字
	 */
	public String match(String text) {
		if (null == text) {
			return null;
		}
		List<String> matchAll = matchAll(text, 1);
		if (matchAll != null && matchAll.size() >0) {
			return matchAll.get(0);
		}
		return null;
	}

	//------------------------------------------------------------------------------- match all

	/**
	 * 找出所有匹配的关键字
	 *
	 * @param text 被检查的文本
	 * @return 匹配的词列表
	 */
	public List<String> matchAll(String text) {
		return matchAll(text, -1);
	}

	/**
	 * 找出所有匹配的关键字
	 *
	 * @param text  被检查的文本
	 * @param limit 限制匹配个数
	 * @return 匹配的词列表
	 */
	public List<String> matchAll(String text, int limit) {
		return matchAll(text, limit, false, false);
	}

	/**
	 * 找出所有匹配的关键字<br>
	 * 密集匹配原则：假如关键词有 ab,b，文本是abab，将匹配 [ab,b,ab]<br>
	 * 贪婪匹配（最长匹配）原则：假如关键字a,ab，最长匹配将匹配[a, ab]
	 *
	 * @param text           被检查的文本
	 * @param limit          限制匹配个数
	 * @param isDensityMatch 是否使用密集匹配原则
	 * @param isGreedMatch   是否使用贪婪匹配（最长匹配）原则
	 * @return 匹配的词列表
	 */
	public List<String> matchAll(String text, int limit, boolean isDensityMatch, boolean isGreedMatch) {
		if (null == text) {
			return null;
		}

		List<String> foundWords = new ArrayList<>();
		Words current = this;
		int length = text.length();
		
		char currentChar;
		for (int i = 0; i < length; i++) {
			StringBuffer wordBuffer = new StringBuffer("");
			for (int j = i; j < length; j++) {
				currentChar = text.charAt(j);

				if (false == StopChar.isNotStopChar(currentChar)) {
					if (wordBuffer.length() > 0) {
						//做为关键词中间的停顿词被当作关键词的一部分被返回
						wordBuffer.append(currentChar);
					} else {
						//停顿词做为关键词的第一个字符时需要跳过
						i++;
					}
					continue;
				} else if (false == current.containsKey(currentChar)) {
					//非关键字符被整体略过，重新以下个字符开始检查
					break;
				}
				wordBuffer.append(currentChar);
				if (current.isEnd(currentChar)) {
					//到达单词末尾，关键词成立，从此词的下一个位置开始查找
					foundWords.add(wordBuffer.toString());
					if (limit > 0 && foundWords.size() >= limit) {
						//超过匹配限制个数，直接返回
						return foundWords;
					}
					if (false == isDensityMatch) {
						//如果非密度匹配，跳过匹配到的词
						i = j;
					}
					if (false == isGreedMatch) {
						//如果懒惰匹配（非贪婪匹配）。当遇到第一个结尾标记就结束本轮匹配
						break;
					}
				}
				current = current.get(currentChar);
				if (null == current) {
					break;
				}
			}
			current = this;
		}
		return foundWords;
	}


	//--------------------------------------------------------------------------------------- Private method start

	/**
	 * 是否末尾
	 *
	 * @param c 检查的字符
	 * @return 是否末尾
	 */
	private boolean isEnd(Character c) {
		return this.endCharacterSet.contains(c);
	}

	/**
	 * 设置是否到达末尾
	 *
	 * @param c 设置结尾的字符
	 */
	private void setEnd(Character c) {
		if (null != c) {
			this.endCharacterSet.add(c);
		}
	}
	//--------------------------------------------------------------------------------------- Private method end
}

