package cms.bean.setting;

import java.io.Serializable;

/**
 * 系统节点参数
 *
 */
public class SystemNode implements Serializable{
	private static final long serialVersionUID = 3128993157307005005L;
	
	/** 分配最大内存 **/
	private long maxMemory;
	/** 已分配内存 **/
	private long totalMemory;
	/** 已分配内存中的剩余空间 **/
	private long freeMemory;
	/** 空闲内存 **/
	private long usableMemory;
	
	

	
	public long getMaxMemory() {
		return maxMemory;
	}
	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}
	public long getTotalMemory() {
		return totalMemory;
	}
	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}
	public long getUsableMemory() {
		return usableMemory;
	}
	public void setUsableMemory(long usableMemory) {
		this.usableMemory = usableMemory;
	}
	public long getFreeMemory() {
		return freeMemory;
	}
	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}
	
}
