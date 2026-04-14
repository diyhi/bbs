package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页计算页码算法实体类
 *
 */
@Getter
@Setter
public class PageIndex implements Serializable{
	@Serial
    private static final long serialVersionUID = 951975034310230621L;
	
	/** 分页开始索引 **/
	private long startindex;
	/** 分页结束索引 **/
	private long endindex;
	
	public PageIndex(long startindex, long endindex) {
		this.startindex = startindex;
		this.endindex = endindex;
	}


}
