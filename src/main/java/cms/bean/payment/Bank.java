package cms.bean.payment;

import java.io.Serializable;

/**
 * 银行
 *
 */
public class Bank implements Serializable{
	private static final long serialVersionUID = -5312617596334317129L;
	
	/** 银行简码 **/
	private String code;
	/** 银行名称 **/
	private String name;
	/** 银行图标 **/
	private String icon;
	/** 选中 true:选中  false:未选中**/
	private boolean selected = false;
	
	
	public Bank() {}
	public Bank(String code, String name, String icon) {
		this.code = code;
		this.name = name;
		this.icon = icon;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	

}
