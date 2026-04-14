package cms.model.user;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户自定义注册功能项用户输入值
 *
 */
@Getter
@Setter
@Entity
@Table(name="userinputvalue",indexes = {@Index(name="userinputvalue_idx", columnList="userId,options")})
public class UserInputValue implements Serializable{
	@Serial
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

}
