package cms.bean.FilterWord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 过滤词
 *
 */
public class FilterWord implements Serializable{
	private static final long serialVersionUID = -3940229396644350845L;
	
	/** 过滤词数量 **/
	private Integer wordNumber;
	/** 词库大小 **/
	private String size;
	/** 词库最后修改时间 **/
	private Date lastModified;
	/** 前3个词 **/
	private List<String> beforeWordList = new ArrayList<String>();
	
	
	public void addBeforeWord(String word) {
		this.beforeWordList.add(word);
	}
	
	public Integer getWordNumber() {
		return wordNumber;
	}
	public void setWordNumber(Integer wordNumber) {
		this.wordNumber = wordNumber;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	public List<String> getBeforeWordList() {
		return beforeWordList;
	}
	public void setBeforeWordList(List<String> beforeWordList) {
		this.beforeWordList = beforeWordList;
	}
	
}
