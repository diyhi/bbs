package cms.model.staff;

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
 * 员工登录日志 实体类的抽象基类,定义基本属性
 *
 */
@MappedSuperclass
@Getter
@Setter
public class StaffLoginLogEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -6977609383555316054L;
	
	/** ID **/
	@Id @Column(length=36)
	protected String id;
	/** 员工Id **/
	@Column(length=36)
	protected String staffId;
	
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
