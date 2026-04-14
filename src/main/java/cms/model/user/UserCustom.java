package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户自定义注册功能项
 *
 */
@Getter
@Setter
@Entity
public class UserCustom implements Serializable{
	@Serial
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
	@Column(name="\"value\"", columnDefinition = "LONGTEXT")
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


}
