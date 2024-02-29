package cms.bean.staff;

import java.io.Serializable;

/**
 * 权限菜单
 * @author Gao
 *
 */
public class PermissionMenu  implements Serializable{
	private static final long serialVersionUID = 1721622201354849134L;
	
	/** 名称 **/
	private String name;
	/** URL **/
	private String url;
	/** 请求类型 **/ 
	private String methods;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMethods() {
		return methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	
	
}
