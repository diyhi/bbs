package cms.model.question;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;



/**
 * 问题标签关联
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="questionTagAssociation_1_idx", columnList="questionId"),@Index(name="questionTagAssociation_2_idx", columnList="questionTagId")})
public class QuestionTagAssociation implements Serializable{
	@Serial
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
	/** 用户名称 7.0版删除本字段
	@Column(length=30)
	private String userName; **/
	

}

