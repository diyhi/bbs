package cms.bean.like;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 点赞 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
public class LikeEntity implements Serializable{
	private static final long serialVersionUID = -1496184788206840632L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	/** 点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 账号 **/
	@Transient
	protected String account;
	/** 呢称 **/
	@Transient
	protected String nickname;
	/** 头像路径 **/
	@Transient
	protected String avatarPath;
	/** 头像名称 **/
	@Transient
	protected String avatarName;
	
	/** 发布话题的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	
	/** 加入时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date addtime = new Date();
	
	/** 模块  10:话题  20:评论  30:评论回复  40:问题  50:答案  60:答案回复 **/
	protected Integer module;
	
	/** 话题Id **/
	protected Long topicId;

	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	
	/** 评论Id **/
	protected Long commentId;
	/** 评论回复Id **/
	protected Long commentReplyId;
	
	/** 问题Id **/
	protected Long questionId;
	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	/** 答案Id **/
	protected Long answerId;
	/** 答案回复Id **/
	protected Long answerReplyId;
	
	
	/** 内容摘要 **/
	@Transient
	protected String summary;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public String getPostUserName() {
		return postUserName;
	}

	public void setPostUserName(String postUserName) {
		this.postUserName = postUserName;
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getCommentReplyId() {
		return commentReplyId;
	}

	public void setCommentReplyId(Long commentReplyId) {
		this.commentReplyId = commentReplyId;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public Long getAnswerId() {
		return answerId;
	}

	public void setAnswerId(Long answerId) {
		this.answerId = answerId;
	}

	public Long getAnswerReplyId() {
		return answerReplyId;
	}

	public void setAnswerReplyId(Long answerReplyId) {
		this.answerReplyId = answerReplyId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	
	
	
}
