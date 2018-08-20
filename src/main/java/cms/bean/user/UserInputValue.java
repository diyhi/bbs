package cms.bean.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * 用户自定义注册功能项用户输入值
 *
 */
@Entity
@Table(name="userinputvalue",indexes = {@Index(name="userinputvalue_idx", columnList="userId,options")})
public class UserInputValue implements Serializable{
	private static final long serialVersionUID = 1793854183249250607L;

	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** 用户Id **/
	private Long userId;
	
	/** 用户自定义注册功能项表Id **/
	@Column
	private Integer userCustomId;
	/** 用户输入内容 **/
	@Lob
	private String content;
	/** 选项值 **/
	@Column(length = 32)
	private String options = "-1";
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getUserCustomId() {
		return userCustomId;
	}
	public void setUserCustomId(Integer userCustomId) {
		this.userCustomId = userCustomId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getOptions() {
		return options;
	}
	public void setOptions(String options) {
		this.options = options;
	}

	
}
