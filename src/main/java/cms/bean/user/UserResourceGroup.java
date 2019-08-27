package cms.bean.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户资源组
 *
 */
public class UserResourceGroup implements Serializable{
	private static final long serialVersionUID = -3018468277759989366L;

	/** 编号 **/
	private Integer code;
	/** 名称 **/
	private String name;
	/** 类型 10:直接提交 20:预处理--标签 **/
	private Integer type = 10;
	/** 标签Id **/
	private Long tagId;
	/** 标签名称 **/
	private String tagName;
	/** 选中(只是用来标记后台添加修改功能的回显，不能在其它地方表示全选资源) **/
	private Boolean selected = false;
	
	/** 资源集合 **/
	private List<UserResource> userResourceList = new ArrayList<UserResource>();

	public void addUserResource(UserResource userResource) {
		this.getUserResourceList().add(userResource);
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<UserResource> getUserResourceList() {
		return userResourceList;
	}

	public void setUserResourceList(List<UserResource> userResourceList) {
		this.userResourceList = userResourceList;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
	
	
}
