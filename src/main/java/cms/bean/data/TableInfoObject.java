package cms.bean.data;

import java.io.Serializable;

/**
 * 数据表信息
 * @author Administrator
 *
 */
public class TableInfoObject implements Serializable{
	private static final long serialVersionUID = 1132128114608257942L;
	
	/** 表名称 **/
	private String name;
	/** 索引大小 **/
	private String indexSize;
	/** 表记录条数**/
	private Long rows;
	/** 表大小 **/
	private String dataSize;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIndexSize() {
		return indexSize;
	}
	public void setIndexSize(String indexSize) {
		this.indexSize = indexSize;
	}
	public Long getRows() {
		return rows;
	}
	public void setRows(Long rows) {
		this.rows = rows;
	}
	public String getDataSize() {
		return dataSize;
	}
	public void setDataSize(String dataSize) {
		this.dataSize = dataSize;
	}
	
	
}
