package cms.bean.like;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 答案回复点赞 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
public class AnswerReplyLikeEntity implements Serializable{
	private static final long serialVersionUID = 2393521333070029106L;
	
	/** ID 格式(回复Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 回复点赞的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布回复的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	/** 加入时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date addtime = new Date();
	
	/** 问题Id **/
	protected Long questionId;
	/** 回复Id **/
	protected Long replyId;

	/** 问题标题 **/
	@Transient
	protected String questionTitle;
	
	/** 回复内容摘要 **/
	@Transient
	protected String summary;
	/** 点赞Id **/
	@Column(length=36)
	protected String likeId;
	
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

	public String getPostUserName() {
		return postUserName;
	}

	public void setPostUserName(String postUserName) {
		this.postUserName = postUserName;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLikeId() {
		return likeId;
	}

	public void setLikeId(String likeId) {
		this.likeId = likeId;
	}

	
}
