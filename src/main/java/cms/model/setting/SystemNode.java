package cms.model.setting;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统节点参数
 *
 */
@Getter
@Setter
public class SystemNode implements Serializable{
	@Serial
    private static final long serialVersionUID = 3128993157307005005L;
	
	/** 分配最大内存 **/
	private long maxMemory;
	/** 已分配内存 **/
	private long totalMemory;
	/** 已分配内存中的剩余空间 **/
	private long freeMemory;
	/** 空闲内存 **/
	private long usableMemory;
}
