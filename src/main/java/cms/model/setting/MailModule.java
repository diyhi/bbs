package cms.model.setting;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 邮件模块
 * @author Gao
 *
 */
@Getter
@Setter
public class MailModule implements Serializable{
	@Serial
    private static final long serialVersionUID = -5846097613553150990L;

	/** 类型  10：验证码   20：注册完成消息 **/
	private Integer type;
	/** 名称 **/
	private String name;
	/** 支持变量 key:变量字段  value:备注 **/
	private Map<String,String> variableMap = new LinkedHashMap<String,String>();
	
	public void addVariable(String key, String value) {
		variableMap.put(key, value);
	}
}
