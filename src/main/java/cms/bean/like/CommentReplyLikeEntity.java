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
 * 评论回复点赞 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
public class CommentReplyLikeEntity implements Serializable{
	private static final long serialVersionUID = -1395965331167138351L;
	
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
	
	/** 话题Id **/
	protected Long topicId;
	/** 回复Id **/
	protected Long replyId;

	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	
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

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
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
