package cms.bean.topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;






/**
 * 引用
 *
 */
public class Quote implements Serializable{
	private static final long serialVersionUID = 3839982541792197957L;
	
	/** 评论Id **/
	private Long commentId;
	/** 是否是员工   true:员工   false:会员 **/
	private Boolean isStaff = false;
	/** IP **/
	private String ip;
	/** IP归属地 **/
	private String ipAddress;
	/** 用户名称 **/
	private String userName;
	/** 账号 **/
	private String account;
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
	
	public Long getCommentId() {
		return commentId;
	}
	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
	public Boolean getIsStaff() {
		return isStaff;
	}
	public void setIsStaff(Boolean isStaff) {
		this.isStaff = isStaff;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatarPath() {
		return avatarPath;
	}
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	public String getAvatarName() {
		return avatarName;
	}
	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}
	public List<String> getUserRoleNameList() {
		return userRoleNameList;
	}
	public void setUserRoleNameList(List<String> userRoleNameList) {
		this.userRoleNameList = userRoleNameList;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Integer getUserInfoStatus() {
		return userInfoStatus;
	}
	public void setUserInfoStatus(Integer userInfoStatus) {
		this.userInfoStatus = userInfoStatus;
	}
	
	
	
}
