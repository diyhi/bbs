package cms.bean;

import java.io.Serializable;

public class PageForm implements Serializable{;
	private static final long serialVersionUID = -5918317799715854848L;
	
	/** 分页 当前页 **/
	private Integer page = 1;
	/** 设置查询标记,用来指定执行某些SQL语句 **/
	private String query;
	
	public Integer getPage() {
		if(page == null){
			return 1;
		}
		return page<1 ? 1 : page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
}
