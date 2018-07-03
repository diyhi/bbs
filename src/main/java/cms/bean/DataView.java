package cms.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 数据展示
 *
 */
public class DataView<T> implements Serializable{
	private static final long serialVersionUID = 360640663429725446L;
	
	/** 名称 **/
	private String name;
	/** 显示记录集 **/
	private List<T> records;
	/** 更多URL **/
	private String moreUrl;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<T> getRecords() {
		return records;
	}
	public void setRecords(List<T> records) {
		this.records = records;
	}
	public String getMoreUrl() {
		return moreUrl;
	}
	public void setMoreUrl(String moreUrl) {
		this.moreUrl = moreUrl;
	}
	
}
