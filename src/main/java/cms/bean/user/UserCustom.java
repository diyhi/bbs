package cms.bean.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

/**
 * 用户自定义注册功能项
 *
 */
@Entity
public class UserCustom implements Serializable{
	private static final long serialVersionUID = 8548112564604596810L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;	
	/** 注册项名称 **/
	@Column(length=50)
	private String name;
	/** 是否必填 **/
	private boolean required = false;		
	/** 后台可搜索 **/
	private boolean search = false;
	/** 是否显示 **/
	@Column(name="\"visible\"")
	private boolean visible = true;
	/** 选框类型  1.输入框 2.单选按钮  3.多选按钮 4.下拉列表  5.文本域textarea   **/
	private Integer chooseType = 1;
	
	/** 参数值  json LinkedHashMap<String,String>格式 **/
	@Lob
	@Column(name="\"value\"")
	private String value;
	/** 参数值 **/
	@Transient
	private LinkedHashMap<String,String> itemValue = new LinkedHashMap<String,String>();
	
	
	/** 字段过滤方式  0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤 **/
	private Integer fieldFilter = 0;

	/** 输入框的宽度 **/
	private Integer size;
	/** 输入框中字符的最大长度 **/
	private Integer maxlength;
	
	
	/** 是否可选择多个选项 true: multiple='multiple'  false: **/
	private boolean multiple;
	/** 下拉列表中可见选项的数目  **/
	private Integer selete_size;
	
	/** 文本域内的可见行数 **/
	@Column(name="\"rows\"")
	private Integer rows;
	/** 文本域内的可见宽度 **/
	private Integer cols;
	
	
	
	
	/** 过滤正则表达式 **/
	@Lob
	private String regular;
	/** 提示 Tip cue**/
	@Column(length=250)
	private String tip;
	
	/** 排序 **/
	private Integer sort = 0;
	
	
	/** 用户自定义注册功能项用户输入值 **/
	@Transient
	private List<UserInputValue> userInputValueList = new ArrayList<UserInputValue>();
	
	
	public void addUserInputValue(UserInputValue userInputValue) {
		this.userInputValueList.add(userInputValue);
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
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean isSearch() {
		return search;
	}
	public void setSearch(boolean search) {
		this.search = search;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Integer getChooseType() {
		return chooseType;
	}
	public void setChooseType(Integer chooseType) {
		this.chooseType = chooseType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getRegular() {
		return regular;
	}
	public void setRegular(String regular) {
		this.regular = regular;
	}
	public Integer getFieldFilter() {
		return fieldFilter;
	}
	public void setFieldFilter(Integer fieldFilter) {
		this.fieldFilter = fieldFilter;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Integer getMaxlength() {
		return maxlength;
	}
	public void setMaxlength(Integer maxlength) {
		this.maxlength = maxlength;
	}
	public Integer getRows() {
		return rows;
	}
	public void setRows(Integer rows) {
		this.rows = rows;
	}
	public Integer getCols() {
		return cols;
	}
	public void setCols(Integer cols) {
		this.cols = cols;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public Integer getSelete_size() {
		return selete_size;
	}
	public void setSelete_size(Integer selete_size) {
		this.selete_size = selete_size;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	public LinkedHashMap<String, String> getItemValue() {
		return itemValue;
	}
	public void setItemValue(LinkedHashMap<String, String> itemValue) {
		this.itemValue = itemValue;
	}
	public List<UserInputValue> getUserInputValueList() {
		return userInputValueList;
	}
	public void setUserInputValueList(List<UserInputValue> userInputValueList) {
		this.userInputValueList = userInputValueList;
	}
	
}
