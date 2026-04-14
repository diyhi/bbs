package cms.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户资源组
 *
 */
@Getter
@Setter
public class UserResourceGroup implements Serializable{
	@Serial
    private static final long serialVersionUID = -3018468277759989366L;

	/** 编号 **/
	private Integer code;
	/** 名称 **/
	private String name;
	/** 类型 10:直接提交 20:预处理--标签 **/
	private Integer type = 10;
	/** 父标签Id **/
	private Long parentTagId;
	/** 父标签名称 **/
	private String parentTagName;
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

	
}
