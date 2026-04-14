package cms.model.redEnvelope;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

/**
 * 发红包
 * @author Gao
 *
 */
@Getter
@Setter
@Entity
@Table(name="giveredenvelope",indexes = {@Index(name="giveRedEnvelope_1_idx", columnList="bindTopicId"),@Index(name="giveRedEnvelope_2_idx", columnList="userId,giveTime")})
public class GiveRedEnvelope implements Serializable{
	@Serial
    private static final long serialVersionUID = -3738039930994089472L;

	/** Id **/
	@Id @Column(length=32)
	private String id;
	/** 发红包的用户Id **/
	private Long userId;
	/** 发红包的用户名称 **/
	@Transient
	private String userName;
	/** 发红包的账号 **/
	@Transient
	private String account;
	/** 发红包的用户呢称 **/
	@Transient
	private String nickname;
	/** 发红包的用户头像路径 **/
	@Transient
	private String avatarPath;
	/** 发红包的用户头像名称 **/
	@Transient
	private String avatarName;

	/** 类型 10.个人定向红包、20.公共随机红包(随机金额)、30.公共定额红包(固定金额) **/
	private Integer type = 10;
	/** 总金额 **/
	@Column(precision=12, scale=2)
	private BigDecimal totalAmount;
	/** 单个红包金额 type=30时有值 **/
	@Column(precision=12, scale=2)
	private BigDecimal singleAmount;
	/** 发放数量 **/
	private Integer giveQuantity;
	/** 祝福语 **/
	@Column(length=150)
	private String wishes;
	
	
	
	/** 绑定话题Id  -1表示空值 **/
	private Long bindTopicId = -1L;
	/** 绑定话题标题 **/
	@Transient
	private String bindTopicTitle;
	
	/** 分配金额组 结构：List<BigDecimal>的JSON格式 **/
	@Lob
	private String distributionAmountGroup;
	/** 抢到红包的用户Id组  结构：删除最后一个逗号再加上中括号可组成List<Long>的JSON格式 **/
	@Lob
	private String grabRedEnvelopeUserIdGroup="[";
	/** 剩余数量 **/
	private Integer remainingQuantity;
	/** 中止领取红包后返还金额 **/
	@Column(precision=12, scale=2)
	private BigDecimal refundAmount;
	
	/** 发放时间 **/
    @Column(columnDefinition = "DATETIME")
    protected LocalDateTime giveTime = LocalDateTime.now();

	/** 访问用户是否已拆本红包 **/
	@Transient
	private boolean accessUserUnwrap =false;
	
	/** 版本号 **/
	private Integer version = 0;
	


}
