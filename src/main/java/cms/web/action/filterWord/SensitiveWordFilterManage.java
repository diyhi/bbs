package cms.web.action.filterWord;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cms.utils.FileUtil;
import cms.utils.PathUtil;

/**
 * 敏感词过滤管理
 *
 */
@Component("sensitiveWordFilterManage")
public class SensitiveWordFilterManage {
	private static final Logger logger = LogManager.getLogger(SensitiveWordFilterManage.class);
	
	
	//上次执行版本
	private String version = "";
	
	private static Words words = new Words(); 
	

	
	/**
	 * 返回过滤后的字符
	 * @param content 输入内容
	 * @param replaceChar 敏感词替换字符
	 * @return
	 */
	public String filterSensitiveWord(String content,String replaceChar){
		List<String> findedWords = this.getFindedAllSensitive(content.toLowerCase());
		if(findedWords != null && findedWords.size() >0){
			for(String str :findedWords){
			//	content = replace(content, str, replaceChar);
				content = StringUtils.replaceIgnoreCase(content, str, replaceChar);//忽略大小写替换； String.replaceAll不能直接替换特殊字符，例如：$/
			}
		}
		return content;
	}
	
	/**
	 * 清空词库
	 */
	private void clearWord() {
		words.clear();
	} 
	

	/**
	 * 是否包含敏感词
	 * @param text 文本
	 * @return 是否包含
	 */
	public boolean containsSensitive(String text){
		return words.isMatch(text);
	}
	
	
	/**
	 * 查找敏感词，返回找到的第一个敏感词
	 * @param text 文本
	 * @return 敏感词
	 */
	public String getFindedFirstSensitive(String text){
		return words.match(text);
	}
	/**
	 * 查找敏感词，返回找到的所有敏感词
	 * @param text 文本
	 * @return 敏感词
	 */
	public List<String> getFindedAllSensitive(String text){
		return words.matchAll(text);
	}
	
	/**
	 * 查找敏感词，返回找到的所有敏感词<br>
	 * 密集匹配原则：假如关键词有 ab,b，文本是abab，将匹配 [ab,b,ab]<br>
	 * 贪婪匹配（最长匹配）原则：假如关键字a,ab，最长匹配将匹配[a, ab]
	 * 
	 * @param text 文本
	 * @param isDensityMatch 是否使用密集匹配原则
	 * @param isGreedMatch 是否使用贪婪匹配（最长匹配）原则
	 * @return 敏感词
	 */
	public List<String> getFindedAllSensitive(String text, boolean isDensityMatch, boolean isGreedMatch){
		return words.matchAll(text, -1, isDensityMatch, isGreedMatch);
	}
	
	
	/**
	 * @describe:可以替换特殊字符的替换方法,replaceAll只能替换普通字符串,含有特殊字符的不能替换
	 * @param strSource 用户输入的字符串
	 * @param strFrom 需要替换的字符
	 * @param strTo	需要替换的字符替换为该字符串
	 * @return
	 */
	private static String replace(String strSource, String strFrom, String strTo) {
	    if (strSource == null) {
	        return null;
	    }
	    int i = 0;
	    if ((i = strSource.indexOf(strFrom, i)) >= 0) {
	        char[] cSrc = strSource.toCharArray();
	        char[] cTo = strTo.toCharArray();
	        int len = strFrom.length();
	        StringBuffer buf = new StringBuffer(cSrc.length);
	        buf.append(cSrc, 0, i).append(cTo);
	        i += len;
	        int j = i;
	        while ((i = strSource.indexOf(strFrom, i)) > 0) {
	            buf.append(cSrc, j, i - j).append(cTo);
	            i += len;
	            j = i;
	        }
	        buf.append(cSrc, j, cSrc.length - j);
	        return buf.toString();
	    }
	    return strSource;
	}

	/**
     * 判断是否更新  true:已更新  false:没有更新
     * @return
     */
    public boolean isUpdate(){
    	String path = "WEB-INF"+File.separator+"data"+File.separator+"filterWord"+File.separator+"word.txt";
    	
    	String new_version = "";
    	
    	//读取文件
    	File file = new File(PathUtil.path()+File.separator+path);
    	if(file.exists()){
    		new_version = file.length()+"-"+file.lastModified();
    	}
    	if(!version.equals(new_version)){
    		
    		version = new_version;
    		return true;
    	}
    	return false;
    	
    }
	
    /**
	 * 读取数据并且更新到词库模型
	 */
	@Scheduled(fixedDelay=10000)//10秒
	public void updateData(){
		try {
			//是否有更新 
			if(this.isUpdate() == true){
				//清空词库
				this.clearWord();
				String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"filterWord"+File.separator;
				File file = new File(path+"word.txt");
				if(file.exists()){
					List<String> wordList = FileUtil.readLines(file,"utf-8");
					if(wordList != null && wordList.size() >0){
						for(String word : wordList){
							if(word != null && !"".equals(word.trim())){
								words.addWord(word.toLowerCase().trim());  
							}
						}
					}
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读取敏感词数据并且更新到词库模型",e);
	        }
		}
	}
	
	
}
