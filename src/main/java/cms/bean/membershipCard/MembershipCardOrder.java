package cms.bean.membershipCard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 会员卡订单
 *
 */
@Entity
@Table(indexes = {@Index(name="membershipCardOrder_1_idx", columnList="userName,createDate")})
public class MembershipCardOrder implements Serializable{
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate = new Date();
	
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


	public Long getOrderId() {
		return orderId;
	}


	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public BigDecimal getAccountPayable() {
		return accountPayable;
	}


	public void setAccountPayable(BigDecimal accountPayable) {
		this.accountPayable = accountPayable;
	}


	public Long getAccountPoint() {
		return accountPoint;
	}


	public void setAccountPoint(Long accountPoint) {
		this.accountPoint = accountPoint;
	}


	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}


	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}


	public Long getPaymentPoint() {
		return paymentPoint;
	}


	public void setPaymentPoint(Long paymentPoint) {
		this.paymentPoint = paymentPoint;
	}


	public String getUserRoleId() {
		return userRoleId;
	}


	public void setUserRoleId(String userRoleId) {
		this.userRoleId = userRoleId;
	}


	public String getRoleName() {
		return roleName;
	}


	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}


	public Long getMembershipCardId() {
		return membershipCardId;
	}


	public void setMembershipCardId(Long membershipCardId) {
		this.membershipCardId = membershipCardId;
	}

	public Long getSpecificationId() {
		return specificationId;
	}


	public void setSpecificationId(Long specificationId) {
		this.specificationId = specificationId;
	}


	public String getSpecificationName() {
		return specificationName;
	}


	public void setSpecificationName(String specificationName) {
		this.specificationName = specificationName;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public Integer getDuration() {
		return duration;
	}


	public void setDuration(Integer duration) {
		this.duration = duration;
	}


	public Integer getUnit() {
		return unit;
	}


	public void setUnit(Integer unit) {
		this.unit = unit;
	}


	public Integer getVersion() {
		return version;
	}


	public void setVersion(Integer version) {
		this.version = version;
	}


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getAvatarPath() {
		return avatarPath;
	}


	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}


	public String getAvatarName() {
		return avatarName;
	}


	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}
	
	
}
