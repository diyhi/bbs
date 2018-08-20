package cms.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 * 使用泛型避免类型转换;QueryResult<T>泛型定义在类上每个方法都可以使用泛型
 */
public class QueryResult<T> implements Serializable{
	private static final long serialVersionUID = 3191274370230750261L;


	/** 分页查询结果集 **/
	private List<T> resultlist;
	
	
	/**总记录数**/
	private long totalrecord = 0;


	public List<T> getResultlist() {
		return resultlist;
	}


	public void setResultlist(List<T> resultlist) {
		this.resultlist = resultlist;
	}


	public long getTotalrecord() {
		return totalrecord;
	}


	public void setTotalrecord(long totalrecord) {
		this.totalrecord = totalrecord;
	}

}
