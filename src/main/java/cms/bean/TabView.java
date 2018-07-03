package cms.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 选项卡数据展示
 *
 */
public class TabView<T> implements Serializable{
	private static final long serialVersionUID = 6645782022019533851L;
	
	/** 名称 **/
	private String name;
	/** 数据展示列表 **/
	private List<DataView<T>> dataViewList = new ArrayList<DataView<T>>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<DataView<T>> getDataViewList() {
		return dataViewList;
	}
	public void setDataViewList(List<DataView<T>> dataViewList) {
		this.dataViewList = dataViewList;
	}
}
