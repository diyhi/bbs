package cms.bean.user;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 会员等级
 *
 */
@Entity
public class UserGrade implements Serializable{
	private static final long serialVersionUID = 526330864606455854L;
	
	/** ID **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/** 等级名称 **/
	@Column(length=50)
	private String name;
	/** 需要积分 **/
	private Long needPoint;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getNeedPoint() {
		return needPoint;
	}
	public void setNeedPoint(Long needPoint) {
		this.needPoint = needPoint;
	}

}
