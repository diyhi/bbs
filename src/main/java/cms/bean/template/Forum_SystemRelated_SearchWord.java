package cms.bean.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 版块---系统相关--热门搜索词
 *
 */
public class Forum_SystemRelated_SearchWord implements Serializable{
	private static final long serialVersionUID = -1838750988740777785L;

	/** 版块---系统--热门搜索词Id **/
	private String searchWord_id;
	
	/** 热门搜索词集合 **/
	private List<String> searchWordList = new ArrayList<String>();

	public String getSearchWord_id() {
		return searchWord_id;
	}

	public void setSearchWord_id(String searchWord_id) {
		this.searchWord_id = searchWord_id;
	}

	public List<String> getSearchWordList() {
		return searchWordList;
	}

	public void setSearchWordList(List<String> searchWordList) {
		this.searchWordList = searchWordList;
	}
	
	
	
}
