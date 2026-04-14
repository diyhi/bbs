package cms.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 * 使用泛型避免类型转换;QueryResult<T>泛型定义在类上每个方法都可以使用泛型
 */
@Getter
@Setter
public class QueryResult<T> implements Serializable{
	@Serial
    private static final long serialVersionUID = 3191274370230750261L;

	/** 分页查询结果集 **/
	private List<T> resultlist;

	/**总记录数**/
	private long totalrecord = 0;




}
