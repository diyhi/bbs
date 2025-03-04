package cms.bean.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 站点栏目
 *
 */
public class Column implements Serializable{
	private static final long serialVersionUID = 4117213723041628243L;
	
	/** ID **/
	private Integer id;
	/** 栏目名称 **/
	private String name;
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
	/** 所属父类ID **/
	private Integer parentId = 0;
	/** 子栏目**/
	private List<Column> childColumn = new ArrayList<Column>();
	/** 排序 **/
	private Integer sort = 1;
	/** 链接方式   1.无   2.外部URL  3.内部URL  4.空白页**/
	private Integer linkMode = 1;
	/** URL **/
	private String url = "";

	/**
	 * 添加子栏目
	 * @param column
	 */
	public void addColumn(Column column){
		this.getChildColumn().add(column);
	}
	/**
	 * 添加子栏目
	 * @param column
	 */
	public void addColumn(List<Column> childColumn){
		this.getChildColumn().addAll(childColumn);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public List<Column> getChildColumn() {
		return childColumn;
	}

	public void setChildColumn(List<Column> childColumn) {
		this.childColumn = childColumn;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getLinkMode() {
		return linkMode;
	}
	public void setLinkMode(Integer linkMode) {
		this.linkMode = linkMode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, String> getMultiLanguageExtensionMap() {
		return multiLanguageExtensionMap;
	}
	public void setMultiLanguageExtensionMap(Map<String, String> multiLanguageExtensionMap) {
		this.multiLanguageExtensionMap = multiLanguageExtensionMap;
	}

	
}
