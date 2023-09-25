package cms.bean.question;

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
 * 答案
 *
 */
@Entity
@Table(name="answer",indexes = {@Index(name="answer_1_idx", columnList="questionId,status,adoption"),@Index(name="answer_2_idx", columnList="userName,isStaff")})
public class Answer implements Serializable{
	private static final long serialVersionUID = -7929411590314542710L;
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
	/** 问题Id **/
	private Long questionId;
	/** 问题标题 **/
	@Transient
	private String questionTitle;
	/** 状态 10.待审核 20.已发布 110.待审核用户删除 120.已发布用户删除 100010.待审核员工删除 100020.已发布员工删除 **/
	private Integer status = 10;
	/** 答案是否采纳 **/
	private Boolean adoption = false;
	
	/** 答案内容 **/
	@Lob
	private String content;
	/** 是否使用Markdown **/
	private Boolean isMarkdown;
	/** Markdown内容 **/
	@Lob
	private String markdownContent;
	/** 回答时间 **/
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
	private List<AnswerReply> answerReplyList = new ArrayList<AnswerReply>();

	/**
	 * 添加 回复
	 * @param reply
	 */
	public void addAnswerReply(AnswerReply answerReply){
		this.setTotalReply(this.getTotalReply()+1);
		this.answerReplyList.add(answerReply);
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

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public List<String> getUserRoleNameList() {
		return userRoleNameList;
	}

	public void setUserRoleNameList(List<String> userRoleNameList) {
		this.userRoleNameList = userRoleNameList;
	}

	public Integer getTotalReply() {
		return totalReply;
	}

	public void setTotalReply(Integer totalReply) {
		this.totalReply = totalReply;
	}

	public List<AnswerReply> getAnswerReplyList() {
		return answerReplyList;
	}

	public void setAnswerReplyList(List<AnswerReply> answerReplyList) {
		this.answerReplyList = answerReplyList;
	}

	public Boolean getAdoption() {
		return adoption;
	}

	public void setAdoption(Boolean adoption) {
		this.adoption = adoption;
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
