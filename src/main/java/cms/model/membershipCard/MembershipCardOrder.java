package cms.model.membershipCard;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * 会员卡订单
 *
 */
@Getter
@Setter
@Entity
@Table(indexes = {@Index(name="membershipCardOrder_1_idx", columnList="userName,createDate")})
public class MembershipCardOrder implements Serializable{
	@Serial
    private static final long serialVersionUID = -4530223105268525029L;

	/** 订单号 **/
	@Id
	private Long orderId;

	/** 用户名称 **/
	@Column(length=30)
	private String userName;
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
	
	/** 订单创建时间 **/
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createDate = LocalDateTime.now();
	
	/** 应付款(实际需要支付的费用) **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal accountPayable = new BigDecimal("0.00");
	
	/** 应付积分 **/
	private Long accountPoint = 0L;
	
	/** 已支付金额 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal paymentAmount = new BigDecimal("0.00");
	/** 已支付积分 **/
	private Long paymentPoint = 0L;
	
	/** 购买的会员卡用户角色Id **/
	@Column(length=32)
	private String userRoleId;
	/** 购买的会员卡用户角色名称 **/
	@Column(length=192)
	private String roleName;
	
	/** 购买的会员卡Id **/
	private Long membershipCardId;
	/** 购买的会员卡规格Id **/
	private Long specificationId;
	/** 购买的会员卡规格名称 **/
	@Column(length=192)
	private String specificationName;
	/** 购买的会员卡数量 **/
	private Integer quantity;
	
	/** 购买的会员卡有效期时长 **/
	private Integer duration;
	/** 购买的会员卡时长单位 10.小时 20.日 30.月 40.年 **/
	private Integer unit;
	
	
	/** 订单版本号 **/
	private Integer version = 0;



}
