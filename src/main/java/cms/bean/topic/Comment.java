package cms.bean.topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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


/**
 * 评论
 *
 */
@Entity
@Table(name="comment",indexes = {@Index(name="comment_1_idx", columnList="topicId,status"),@Index(name="comment_2_idx", columnList="quoteIdGroup"),@Index(name="comment_3_idx", columnList="userName,isStaff")})
public class Comment implements Serializable{
	private static final long serialVersionUID = -7576354708273189356L;
	
	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 是否是员工   true:员工   false:会员 **/
	private Boolean isStaff = false;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	/** 账号 **/
	@Transient
	private String account;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	/** 用户信息状态 -30.账号已注销(不显示数据) -20.账号已逻辑删除(不显示数据) -10.账号已禁用(不显示数据)  0.正常 10.账号已禁用(显示数据) 20.账号已逻辑删除(显示数据) **/
	@Transient
	private Integer userInfoStatus = 0;
	/** IP **/
	@Column(length=45)
	private String ip;
	/** IP归属地 **/
	@Transient
	private String ipAddress;
	/** 话题Id **/
	private Long topicId;
	/** 话题标题 **/
	@Transient
	private String topicTitle;
	/** 状态 10.待审核 20.已发布 110.待审核用户删除 120.已发布用户删除 100010.待审核员工删除 100020.已发布员工删除 **/
	private Integer status = 10;
	
	/** 引用json数据 **/
	@Lob
	private String quote;
	/** 自定义引用集合 **/
	@Transient
	private List<Quote> quoteList = new ArrayList<Quote>();
	/** 引用Id组 倒序存放**/
	private String quoteIdGroup = ",";
	/** 引用修改Id **/
	private String quoteUpdateId = ",";
	
	/** 评论内容 **/
	@Lob
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	/** 评论时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	/** 最后修改时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;
	
	/** 用户角色名称集合 **/
	@Transient
	private List<String> userRoleNameList = new ArrayList<String>();
	/** 总回复数 **/
	@Transient
	private Integer totalReply = 0;
	/** 回复集合 **/
	@Transient
	private List<Reply> replyList = new ArrayList<Reply>();

	/**
	 * 添加 回复
	 * @param reply
	 */
	public void addReply(Reply reply){
		this.setTotalReply(this.getTotalReply()+1);
		this.replyList.add(reply);
	}
	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}



	public Boolean getIsStaff() {
		return isStaff;
	}


	public void setIsStaff(Boolean isStaff) {
		this.isStaff = isStaff;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getQuote() {
		return quote;
	}


	public void setQuote(String quote) {
		this.quote = quote;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public List<Quote> getQuoteList() {
		return quoteList;
	}


	public void setQuoteList(List<Quote> quoteList) {
		this.quoteList = quoteList;
	}


	public List<Reply> getReplyList() {
		return replyList;
	}


	public void setReplyList(List<Reply> replyList) {
		this.replyList = replyList;
	}


	public Long getTopicId() {
		return topicId;
	}


	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public Date getPostTime() {
		return postTime;
	}


	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}


	public Integer getTotalReply() {
		return totalReply;
	}


	public void setTotalReply(Integer totalReply) {
		this.totalReply = totalReply;
	}


	public String getQuoteIdGroup() {
		return quoteIdGroup;
	}


	public void setQuoteIdGroup(String quoteIdGroup) {
		this.quoteIdGroup = quoteIdGroup;
	}


	public String getQuoteUpdateId() {
		return quoteUpdateId;
	}


	public void setQuoteUpdateId(String quoteUpdateId) {
		this.quoteUpdateId = quoteUpdateId;
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


	public String getTopicTitle() {
		return topicTitle;
	}


	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
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


	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}


	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
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


	public Boolean getIsMarkdown() {
		return isMarkdown;
	}


	public void setIsMarkdown(Boolean isMarkdown) {
		this.isMarkdown = isMarkdown;
	}


	public String getMarkdownContent() {
		return markdownContent;
	}


	public void setMarkdownContent(String markdownContent) {
		this.markdownContent = markdownContent;
	}
	
}
