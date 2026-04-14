package cms.model.user;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * 注册禁止的用户名称
 *
 */
@Getter
@Setter
@Entity
public class DisableUserName implements Serializable{
	@Serial
    private static final long serialVersionUID = -1209741644912978336L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 禁止的用户名称 **/
	@Column(length=30)
	private String name;
	

}
