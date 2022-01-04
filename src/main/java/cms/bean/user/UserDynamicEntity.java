package cms.bean.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

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
	private static final long serialVersionUID = 1121098848019423561L;
	
	/** Id  Id的后4位为用户Id的后4位**/
	@Id @Column(length=36)
	protected String id;
	/** 用户名称 **/
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
	/** 模块 100.话题  200.评论 300.引用评论 400.评论回复  500.问题 600.答案 700.答案回复 **/
	protected Integer module;
	
	/** 功能Id组 格式：,话题Id,评论Id,回复Id,  或者 ,问题Id,答案Id,答案回复Id   **/
	@Column(length=100)
	protected String functionIdGroup;
	
	
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
	/** 话题允许查看的角色名称集合(默认角色除外) **/
	@Transient
	protected List<String> allowRoleViewList = new ArrayList<String>();
	
	/** key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁 **/
	@Transient
	protected LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();
	
	/** 评论内容 **/
	@Transient
	protected String commentContent;
	/** 引用评论内容 **/
	@Transient
	protected String quoteCommentContent;
	/** 回复内容 **/
	@Transient
	protected String replyContent;
	
	
	/** 问题Id -1表示默认空值 **/
	protected Long questionId = -1L;
	/** 答案Id -1表示默认空值 **/
	protected Long answerId = -1L;
	/** 答案回复Id -1表示默认空值 **/
	protected Long answerReplyId = -1L;
	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	/** 问题内容 **/
	@Transient
	protected String questionContent;
	/** 问题查看总数 **/
	@Transient
	protected Long questionViewTotal;
	/** 问题回答总数 **/
	@Transient
	protected Long questionAnswerTotal = 0L;
	/** 答案内容 **/
	@Transient
	protected String answerContent;
	/** 答案回复内容 **/
	@Transient
	protected String answerReplyContent;
	

	/** 发表时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date postTime = new Date();
	
	/** 状态 10.待审核 20.已发布 110.待审核删除 120.已发布删除 100010.待审核员工删除 100020.已发布员工删除 **/
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

	public List<String> getAllowRoleViewList() {
		return allowRoleViewList;
	}

	public void setAllowRoleViewList(List<String> allowRoleViewList) {
		this.allowRoleViewList = allowRoleViewList;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
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

	public String getFunctionIdGroup() {
		return functionIdGroup;
	}

	public void setFunctionIdGroup(String functionIdGroup) {
		this.functionIdGroup = functionIdGroup;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public String getQuestionContent() {
		return questionContent;
	}

	public void setQuestionContent(String questionContent) {
		this.questionContent = questionContent;
	}

	public Long getQuestionViewTotal() {
		return questionViewTotal;
	}

	public void setQuestionViewTotal(Long questionViewTotal) {
		this.questionViewTotal = questionViewTotal;
	}

	public Long getQuestionAnswerTotal() {
		return questionAnswerTotal;
	}

	public void setQuestionAnswerTotal(Long questionAnswerTotal) {
		this.questionAnswerTotal = questionAnswerTotal;
	}

	public String getAnswerContent() {
		return answerContent;
	}

	public void setAnswerContent(String answerContent) {
		this.answerContent = answerContent;
	}

	public String getAnswerReplyContent() {
		return answerReplyContent;
	}

	public void setAnswerReplyContent(String answerReplyContent) {
		this.answerReplyContent = answerReplyContent;
	}

	public LinkedHashMap<Integer, Boolean> getHideTagTypeMap() {
		return hideTagTypeMap;
	}

	public void setHideTagTypeMap(LinkedHashMap<Integer, Boolean> hideTagTypeMap) {
		this.hideTagTypeMap = hideTagTypeMap;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
}
