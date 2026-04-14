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
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 处罚IP信息
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="penaltyIpInfo_1_idx", columnList="ip,createTime"),@Index(name="penaltyIpInfo_2_idx", columnList="ip,banEndTime,createTime"),@Index(name="penaltyIpInfo_3_idx", columnList="banEndTime,createTime"),@Index(name="penaltyIpInfo_4_idx", columnList="createTime")})
public class PenaltyIpInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -4448679199984709029L;
	
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
	
	/** 风控模型Id **/
	private Integer riskControlModelId;
	/** 风控模型名称**/
	@Transient
	private String riskControlModelName;
	/** 风控模型类型 **/
	@Transient
	private Integer riskControlModelType;
	/** 封禁操作  10.系统  20.员工**/
	private Integer operator = 10;
	/** 封禁操作员工账号 **/
	@Column(length=30)
	private String staffAccount;
	
	/** IP**/
	@Column(length=45)
	private String ip;
	/** 创建时间**/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createTime;
	/** 封禁结束时间**/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime banEndTime;
	/** 状态 0.无  10.生效中(此状态不存储，由程序运行时设置) 20.已结束(此状态不存储，由程序运行时设置) **/
	@Transient
	private Integer status = 0;
	
	/** 备注 **/
	@Lob
	private String remark;
	

}
