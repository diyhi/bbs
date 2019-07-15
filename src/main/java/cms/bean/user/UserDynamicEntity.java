package cms.bean.user;

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
 * 用户动态Entity
 *
 */

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class UserDynamicEntity implements Serializable{
	private static final long serialVersionUID = 7922158419619674156L;
	
	/** Id  Id的后4位为用户Id的后4位**/
	@Id @Column(length=36)
	protected String id;
	/** 用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 呢称 **/
	@Transient
	protected String nickname;
	/** 头像路径 **/
	@Transient
	protected String avatarPath;
	/** 头像名称 **/
	@Transient
	protected String avatarName;
	/** 模块 100.话题  200.评论 300.引用评论 400.回复 **/
	protected Integer module;
	
	/** 话题Id -1表示默认空值 **/
	protected Long topicId = -1L;
	/** 评论Id -1表示默认空值 **/
	protected Long commentId = -1L;	
	/** 引用评论Id -1表示默认空值 **/
	protected Long quoteCommentId = -1L;
	/** 回复Id -1表示默认空值 **/
	protected Long replyId = -1L;
	
	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	/** 话题内容 **/
	@Transient
	protected String topicContent;
	/** 话题查看总数 **/
	@Transient
	protected Long topicViewTotal;
	/** 话题评论总数 **/
	@Transient
	protected Long topicCommentTotal = 0L;
	/** 评论内容 **/
	@Transient
	protected String commentContent;
	/** 引用评论内容 **/
	@Transient
	protected String quoteCommentContent;
	/** 回复内容 **/
	@Transient
	protected String replyContent;
	/** 发表时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	
	/** 状态 10.待审核 20.已发布 110.待审核删除 120.已发布删除 **/
	protected Integer status = 10;

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

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public String getTopicContent() {
		return topicContent;
	}

	public void setTopicContent(String topicContent) {
		this.topicContent = topicContent;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getQuoteCommentId() {
		return quoteCommentId;
	}

	public void setQuoteCommentId(Long quoteCommentId) {
		this.quoteCommentId = quoteCommentId;
	}

	public String getQuoteCommentContent() {
		return quoteCommentContent;
	}

	public void setQuoteCommentContent(String quoteCommentContent) {
		this.quoteCommentContent = quoteCommentContent;
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

	public Long getTopicViewTotal() {
		return topicViewTotal;
	}

	public void setTopicViewTotal(Long topicViewTotal) {
		this.topicViewTotal = topicViewTotal;
	}

	public Long getTopicCommentTotal() {
		return topicCommentTotal;
	}

	public void setTopicCommentTotal(Long topicCommentTotal) {
		this.topicCommentTotal = topicCommentTotal;
	}
	
}
