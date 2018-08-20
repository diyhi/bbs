package cms.bean;

import java.io.Serializable;

/**
 * 分页计算页码算法实体类
 *
 */
public class PageIndex implements Serializable{
	private static final long serialVersionUID = 951975034310230621L;
	
	/** 分页开始索引 **/
	private long startindex;
	/** 分页结束索引 **/
	private long endindex;
	
	public PageIndex(long startindex, long endindex) {
		this.startindex = startindex;
		this.endindex = endindex;
	}
	public long getStartindex() {
		return startindex;
	}
	public void setStartindex(long startindex) {
		this.startindex = startindex;
	}
	public long getEndindex() {
		return endindex;
	}
	public void setEndindex(long endindex) {
		this.endindex = endindex;
	}
	
}
