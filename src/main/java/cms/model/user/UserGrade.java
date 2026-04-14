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
 * 会员等级
 *
 */
@Getter
@Setter
@Entity
public class UserGrade implements Serializable{
	@Serial
    private static final long serialVersionUID = 526330864606455854L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 等级名称 **/
	@Column(length=50)
	private String name;
	/** 需要积分 **/
	private Long needPoint;
	

}
