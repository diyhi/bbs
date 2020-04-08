package cms.bean.topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import cms.bean.MediaInfo;

/**
 * 话题
 *
 */
@Entity
@Table(indexes = {@Index(name="topic_idx", columnList="tagId,status"),@Index(name="topic_3_idx", columnList="userName,postTime"),@Index(name="topic_5_idx", columnList="status,sort,lastReplyTime")})
public class Topic implements Serializable{
	private static final long serialVersionUID = -684257451052921859L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 标题 **/
	@Column(length=190)
	private String title;
	
	/** 标签Id **/
	private Long tagId;
	/** 标签名称 **/
	@Transient
	private String tagName;

	/** 话题内容 **/
	@Lob
	private String content;
	/** 内容摘要 **/
	@Lob
	private String summary;
	
	
	
	/** 发表时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	/** 最后回复时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastReplyTime;
	/** 最后修改时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	
	/** 前3张图片 List<ImageInfo>json格式 **/
	@Lob
	private String image;
	@Transient
	private List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
	
	/** 媒体文件信息集合 **/
	@Transient
	private List<MediaInfo> mediaInfoList = new ArrayList<MediaInfo>();
	
	/** 评论总数 **/
	private Long commentTotal = 0L;
	/** 允许评论 **/
	private boolean allow = true;
	
	/** 查看总数 **/
	private Long viewTotal = 0L;
	
	
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	
	/** 话题允许查看的角色名称集合(默认角色除外) **/
	@Transient
	private List<String> allowRoleViewList = new ArrayList<String>();
	
	/** key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁 **/
	@Transient
	private LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();
	
	
	/** 是否为员工 true:员工  false:会员 **/
	private Boolean isStaff = false;
	/** 排序  **/
	private Integer sort = 0;
	/** 状态 10.待审核 20.已发布 110.待审核删除 120.已发布删除 **/
	private Integer status = 10;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public boolean isAllow() {
		return allow;
	}
	public void setAllow(boolean allow) {
		this.allow = allow;
	}
	public Date getPostTime() {
		return postTime;
	}
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}
	public Date getLastReplyTime() {
		return lastReplyTime;
	}
	public void setLastReplyTime(Date lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}
	public Long getCommentTotal() {
		return commentTotal;
	}
	public void setCommentTotal(Long commentTotal) {
		this.commentTotal = commentTotal;
	}
	public Long getViewTotal() {
		return viewTotal;
	}
	public void setViewTotal(Long viewTotal) {
		this.viewTotal = viewTotal;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public List<ImageInfo> getImageInfoList() {
		return imageInfoList;
	}
	public void setImageInfoList(List<ImageInfo> imageInfoList) {
		this.imageInfoList = imageInfoList;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
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
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public List<String> getUserRoleNameList() {
		return userRoleNameList;
	}
	public void setUserRoleNameList(List<String> userRoleNameList) {
		this.userRoleNameList = userRoleNameList;
	}
	public List<String> getAllowRoleViewList() {
		return allowRoleViewList;
	}
	public void setAllowRoleViewList(List<String> allowRoleViewList) {
		this.allowRoleViewList = allowRoleViewList;
	}
	public LinkedHashMap<Integer, Boolean> getHideTagTypeMap() {
		return hideTagTypeMap;
	}
	public void setHideTagTypeMap(LinkedHashMap<Integer, Boolean> hideTagTypeMap) {
		this.hideTagTypeMap = hideTagTypeMap;
	}
	public List<MediaInfo> getMediaInfoList() {
		return mediaInfoList;
	}
	public void setMediaInfoList(List<MediaInfo> mediaInfoList) {
		this.mediaInfoList = mediaInfoList;
	}
	
}
