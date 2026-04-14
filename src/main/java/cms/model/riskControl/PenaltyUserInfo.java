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
 * 处罚用户信息
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="penaltyUserInfo_1_idx", columnList="banEndTime,status,createTime"),@Index(name="penaltyUserInfo_2_idx", columnList="createTime"),@Index(name="penaltyUserInfo_3_idx", columnList="userName,banEndTime,status")})
public class PenaltyUserInfo implements Serializable{
	@Serial
    private static final long serialVersionUID = -1672006042199299942L;
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
	/** 创建时间**/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createTime;
	/** 封禁结束时间**/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime banEndTime;
	/** 风控模型Id **/
	private Integer riskControlModelId;
	/** 风控模型名称**/
	@Transient
	private String riskControlModelName;
	/** 风控处罚参数(处罚用户部分) Penalty类型的json格式 **/
	@Lob
	private String penaltyParameter;
	/** 风控处罚 **/
	@Transient
	private Penalty penalty = new Penalty();
	
	/** 状态 0.无  10.生效中(此状态不存储，由程序运行时设置) 20.已结束(此状态不存储，由程序运行时设置) 30.已解除封禁 **/
	private Integer status = 0;
	/** 解除封禁时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime unblockTime;
	/** 解除封禁员工账号 **/
	@Column(length=30)
	private String staffAccount;

}
