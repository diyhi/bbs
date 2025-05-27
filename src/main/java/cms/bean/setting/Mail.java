package cms.bean.setting;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 邮件
 * @author Gao
 *
 */
public class Mail implements Serializable{
	private static final long serialVersionUID = 8481168977115985562L;
	
	/** 类型  10：验证码   20：注册完成消息 **/
	private Integer type;
	/** 标题 **/
	private String title;
	/** 模板 **/
	private String template;
	
	/** 多语言扩展  key:字段-语言（例如：name-en_US） value:内容**/
	private Map<String,String> multiLanguageExtensionMap = new HashMap<String,String>();
	
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public Map<String, String> getMultiLanguageExtensionMap() {
		return multiLanguageExtensionMap;
	}
	public void setMultiLanguageExtensionMap(Map<String, String> multiLanguageExtensionMap) {
		this.multiLanguageExtensionMap = multiLanguageExtensionMap;
	}
	
	
	
}
