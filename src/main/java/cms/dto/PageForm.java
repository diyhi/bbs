package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class PageForm implements Serializable{;
	@Serial
    private static final long serialVersionUID = -5918317799715854848L;
	
	/** 分页 当前页 **/
    @Setter
	private Integer page = 1;
	/** 设置查询标记,用来指定执行某些SQL语句 **/
    @Getter
    @Setter
	private String query;
	
	public Integer getPage() {
		if(page == null){
			return 1;
		}
		return page<1 ? 1 : page;
	}


}
