package cms.bean.favorite;

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
 * 收藏夹 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class FavoritesEntity implements Serializable{
	private static final long serialVersionUID = 7804036863650809463L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	
	/** 模块 10:话题 20:问题 **/
	protected Integer module;
	
	/** 收藏夹的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	
	/** 发布 话题/问题 的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	
	/** 加入时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date addtime = new Date();
	
	/** 话题Id **/
	protected Long topicId;

	/** 话题标题 **/
	@Transient
	protected String topicTitle;
	
	/** 问题Id **/
	protected Long questionId;

	/** 问题标题 **/
	@Transient
	protected String questionTitle;

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

	public Integer getModule() {
		return module;
	}

	public void setModule(Integer module) {
		this.module = module;
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
	
	
	
}
