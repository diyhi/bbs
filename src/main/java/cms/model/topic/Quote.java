package cms.model.topic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



/**
 * 引用
 *
 */
@Getter
@Setter
public class Quote implements Serializable{
	@Serial
    private static final long serialVersionUID = 3839982541792197957L;
	
	/** 评论Id **/
	private Long commentId;
	/** 是否是员工   true:员工   false:会员 **/
	private Boolean isStaff = false;
	/** IP **/
	private String ip;
	/** IP归属地 **/
	private String ipAddress;
	/** 账号 **/
	private String account;
	/** 用户名称 **/
	private String userName;
	/** 呢称 **/
	private String nickname;
	/** 头像路径 **/
	private String avatarPath;
	/** 头像名称 **/
	private String avatarName;
	/** 用户信息状态 -30.账号已注销(不显示数据) -20.账号已逻辑删除(不显示数据) -10.账号已禁用(不显示数据)  0.正常 10.账号已禁用(显示数据) 20.账号已逻辑删除(显示数据) **/
	private Integer userInfoStatus = 0;
	/** 评论内容 **/
	private String content;
	
	/** 用户角色名称集合 **/
	private List<String> userRoleNameList = new ArrayList<String>();
	

}
