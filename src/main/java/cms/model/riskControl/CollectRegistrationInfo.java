package cms.model.riskControl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 采集注册信息
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="collectRegistrationInfo_1_idx", columnList="ip,registrationTime")})
public class CollectRegistrationInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -8226765273281211347L;

	/** Id **/
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 用户名称**/
	@Column(length=30)
	private String userName;
	/** 用户Id**/
	@Transient
	private Long userId;
	/** 账号 **/
	@Transient
	private String account;
	/** 呢称 **/
	@Transient
	private String nickname;
	/** 头像路径 **/
	@Transient
	private String avatarPath;
	/** 头像名称 **/
	@Transient
	private String avatarName;
	
	/** IP地址**/
	@Column(length=45)
	private String ip;
	/** 注册时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime registrationTime;

	
}
