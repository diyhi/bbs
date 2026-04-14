package cms.dto.filterWord;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 过滤词
 *
 */
@Getter
@Setter
public class FilterWord implements Serializable{
	@Serial
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

}
