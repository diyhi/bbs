package cms.model.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 银行
 *
 */
@Getter
@Setter
public class Bank implements Serializable{
	@Serial
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
}
