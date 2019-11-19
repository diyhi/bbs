package cms.bean.message;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * 提醒Entity
 *
 */

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class RemindEntity implements Serializable{

	private static final long serialVersionUID = -8678342490584706957L;
	
	
	/** Id  Id的后4位为接收提醒用户Id的后4位 **/
	@Id @Column(length=36)
	protected String id;
	/** 接收提醒用户Id **/
	protected Long receiverUserId;
	
	
	/** 提醒发送用户Id **/
	protected Long senderUserId;
	/** 提醒发送用户名称 **/
	@Transient
	protected String senderUserName;
	/** 提醒发送用户呢称 **/
	@Transient
	protected String senderNickname;
	/** 提醒发送用户头像路径 **/
	@Transient
	protected String senderAvatarPath;
	/** 提醒发送用户头像名称 **/
	@Transient
	protected String senderAvatarName;
	
	
	/** 提醒类型代码编号  10:别人评论了我的话题  20:别人回复了我的话题 30:别人引用了我的评论 40:别人回复了我的评论 50:别人回复了我回复过的评论 60:别人解锁了我的话题 70.别人点赞了我的话题 80.别人关注了我 90.我关注的人发表了话题 100.我关注的人发表了评论 110.我关注的人发表了回复 120:别人回答了我的问题  130:别人回复了我的问题 140:别人回复了我的答案 150:别人回复了我回复过的答案 **/
	protected Integer typeCode;

	/** 关联数据
	@Lob
	protected String associativeData;**/
	/** 关联数据模块
	@Transient
	protected Object associativeDataModule;**/
	
	/** 提醒状态 10:未读  20:已读  110:未读删除  120:已读删除 **/ 
	protected Integer status = 10;
	
	/** 发送时间格式化 **/
	protected Long sendTimeFormat;
	
	/** 阅读时间格式化 **/
	protected Long readTimeFormat;
	

	/** 发送时间 **/
	@Transient
	protected Date sendTime;
	/** 阅读时间 **/
	@Transient
	protected Date readTime;
	
	
	/** 话题Id **/
	protected Long topicId = -1L;
	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	/** 我的话题评论Id **/
	protected Long topicCommentId;
	/** 我的话题回复Id **/
	protected Long topicReplyId;
	
	/** 对方的话题评论Id **/
	protected Long friendTopicCommentId;
	/** 对方的话题回复Id **/
	protected Long friendTopicReplyId;
	
	
	/** 问题Id **/
	protected Long questionId = -1L;
	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	/** 我的问题答案Id **/
	protected Long questionAnswerId;
	/** 我的问题回复Id **/
	protected Long questionReplyId;
	
	/** 对方的问题答案Id **/
	protected Long friendQuestionAnswerId;
	/** 对方的问题回复Id **/
	protected Long friendQuestionReplyId;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getReceiverUserId() {
		return receiverUserId;
	}
	public void setReceiverUserId(Long receiverUserId) {
		this.receiverUserId = receiverUserId;
	}
	public Long getSenderUserId() {
		return senderUserId;
	}
	public void setSenderUserId(Long senderUserId) {
		this.senderUserId = senderUserId;
	}
	public String getSenderUserName() {
		return senderUserName;
	}
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}
	public String getSenderAvatarPath() {
		return senderAvatarPath;
	}
	public void setSenderAvatarPath(String senderAvatarPath) {
		this.senderAvatarPath = senderAvatarPath;
	}
	public String getSenderAvatarName() {
		return senderAvatarName;
	}
	public void setSenderAvatarName(String senderAvatarName) {
		this.senderAvatarName = senderAvatarName;
	}
	public Integer getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(Integer typeCode) {
		this.typeCode = typeCode;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getSendTimeFormat() {
		return sendTimeFormat;
	}
	public void setSendTimeFormat(Long sendTimeFormat) {
		this.sendTimeFormat = sendTimeFormat;
	}
	public Long getReadTimeFormat() {
		return readTimeFormat;
	}
	public void setReadTimeFormat(Long readTimeFormat) {
		this.readTimeFormat = readTimeFormat;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getReadTime() {
		return readTime;
	}
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
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
	public Long getTopicCommentId() {
		return topicCommentId;
	}
	public void setTopicCommentId(Long topicCommentId) {
		this.topicCommentId = topicCommentId;
	}
	public Long getTopicReplyId() {
		return topicReplyId;
	}
	public void setTopicReplyId(Long topicReplyId) {
		this.topicReplyId = topicReplyId;
	}
	public Long getFriendTopicCommentId() {
		return friendTopicCommentId;
	}
	public void setFriendTopicCommentId(Long friendTopicCommentId) {
		this.friendTopicCommentId = friendTopicCommentId;
	}
	public Long getFriendTopicReplyId() {
		return friendTopicReplyId;
	}
	public void setFriendTopicReplyId(Long friendTopicReplyId) {
		this.friendTopicReplyId = friendTopicReplyId;
	}
	public String getSenderNickname() {
		return senderNickname;
	}
	public void setSenderNickname(String senderNickname) {
		this.senderNickname = senderNickname;
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
	public Long getQuestionAnswerId() {
		return questionAnswerId;
	}
	public void setQuestionAnswerId(Long questionAnswerId) {
		this.questionAnswerId = questionAnswerId;
	}
	public Long getQuestionReplyId() {
		return questionReplyId;
	}
	public void setQuestionReplyId(Long questionReplyId) {
		this.questionReplyId = questionReplyId;
	}
	public Long getFriendQuestionAnswerId() {
		return friendQuestionAnswerId;
	}
	public void setFriendQuestionAnswerId(Long friendQuestionAnswerId) {
		this.friendQuestionAnswerId = friendQuestionAnswerId;
	}
	public Long getFriendQuestionReplyId() {
		return friendQuestionReplyId;
	}
	public void setFriendQuestionReplyId(Long friendQuestionReplyId) {
		this.friendQuestionReplyId = friendQuestionReplyId;
	}

	
}
