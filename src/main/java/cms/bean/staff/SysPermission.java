package cms.bean.staff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 权限表
 * @author Administrator
 *
 */
@Entity
public class SysPermission implements Serializable{
	private static final long serialVersionUID = 5143962374463749452L;
	
	@Id @Column(length=32)
	private String id;
	/** 名称    AUTH_0e37599e-c990-4867-a7d6-f7795b8f02ae_GET_READ **/
	@Column(length=200)
	private String name;
	/** 说明 **/
	private String remarks;
	/** 优先级 **/ 
	private Integer priority;
	/** 请求类型 **/ 
	@Column(length=4)
	private String methods;
	
	
	
	//临时保存选中的资源权限(GET)
	@Transient
	private List<String> permissionName_GET= new ArrayList<String>();
	//临时保存选中的资源权限(POST)
	@Transient
	private List<String> permissionName_POST= new ArrayList<String>();

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

	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getMethods() {
		return methods;
	}
	public void setMethods(String methods) {
		this.methods = methods;
	}
	public List<String> getPermissionName_GET() {
		return permissionName_GET;
	}
	public void setPermissionName_GET(List<String> permissionName_GET) {
		this.permissionName_GET = permissionName_GET;
	}
	public List<String> getPermissionName_POST() {
		return permissionName_POST;
	}
	public void setPermissionName_POST(List<String> permissionName_POST) {
		this.permissionName_POST = permissionName_POST;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysPermission other = (SysPermission) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
