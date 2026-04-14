package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户登录日志 实体类的抽象基类,定义基本属性
 *
 */
@Getter
@Setter
@MappedSuperclass
public class UserLoginLogEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -2196946312722895529L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	/** 用户Id **/
	protected Long userId;
	
	/** 用户名称 **/
	@Transient
	protected String userName;
	
	/** 类型编号 10:登录 20:续期 **/
	protected Integer typeNumber = 10;
	
	/** 登录时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime logonTime = LocalDateTime.now();
	
	/** 登录IP **/
	@Column(length=45)
	protected String ip;
	/** IP归属地 **/
	@Transient
	protected String ipAddress;


}
