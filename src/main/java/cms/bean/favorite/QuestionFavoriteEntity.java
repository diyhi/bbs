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
 * 问题收藏 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class QuestionFavoriteEntity implements Serializable{
	private static final long serialVersionUID = -4141083671828837197L;
	/** ID 格式(问题Id_用户Id)**/
	@Id @Column(length=40)
	protected String id;
	/** 问题收藏的用户名称 **/
	@Column(length=30)
	protected String userName;
	
	/** 发布问题的用户名称 **/
	@Column(length=30)
	protected String postUserName;
	/** 加入时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date addtime = new Date();
	
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

	public String getQuestionTitle() {
		return questionTitle;
	}

	public void setQuestionTitle(String questionTitle) {
		this.questionTitle = questionTitle;
	}

	
	
}
