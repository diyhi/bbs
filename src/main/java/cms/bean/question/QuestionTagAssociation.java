package cms.bean.question;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;



/**
 * 问题标签关联
 *
 */
@Entity
@Table(indexes = {@Index(name="questionTagAssociation_1_idx", columnList="questionId"),@Index(name="questionTagAssociation_2_idx", columnList="questionTagId")})
public class QuestionTagAssociation implements Serializable{
	private static final long serialVersionUID = 7948414425873700267L;

	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 问题标签Id **/
	private Long questionTagId;
	/** 问题标签名称 **/
	@Transient
	private String questionTagName;
	/** 问题Id **/
	private Long questionId;
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getQuestionTagId() {
		return questionTagId;
	}
	public void setQuestionTagId(Long questionTagId) {
		this.questionTagId = questionTagId;
	}
	public Long getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}
	public String getQuestionTagName() {
		return questionTagName;
	}
	public void setQuestionTagName(String questionTagName) {
		this.questionTagName = questionTagName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}

