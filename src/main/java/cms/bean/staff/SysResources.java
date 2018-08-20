package cms.bean.staff;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 资源表
 * @author Administrator
 *
 */
@Entity
public class SysResources implements Serializable {
	private static final long serialVersionUID = 9031856537579017249L;
	
	@Id @Column(length=32)
	private String id;
	/** 名称 **/
	private String name;
	/** 备注 **/
	private String remarks;
	/** URL **/
	private String url;
	/** 附加URL类型 1.GET 2.POST **/
	private Integer urlType;
	/** 附加URL所属父ID **/
	private String urlParentId;
	/** 优先级 **/ 
	private Integer priority;
	
	
	/** 模块 **/
	private String module;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public Integer getUrlType() {
		return urlType;
	}

	public void setUrlType(Integer urlType) {
		this.urlType = urlType;
	}

	public String getUrlParentId() {
		return urlParentId;
	}

	public void setUrlParentId(String urlParentId) {
		this.urlParentId = urlParentId;
	}
	
}
