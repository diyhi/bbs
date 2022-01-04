package cms.bean.redEnvelope;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 发红包
 * @author Gao
 *
 */
@Entity
@Table(name="giveredenvelope",indexes = {@Index(name="giveRedEnvelope_1_idx", columnList="bindTopicId"),@Index(name="giveRedEnvelope_2_idx", columnList="userId,giveTime")})
public class GiveRedEnvelope implements Serializable{
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
	@Temporal(TemporalType.TIMESTAMP)
	private Date giveTime = new Date();
	
	
	/** 访问用户是否已拆本红包 **/
	@Transient
	private boolean accessUserUnwrap =false;
	
	/** 版本号 **/
	private Integer version = 0;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Integer getGiveQuantity() {
		return giveQuantity;
	}
	public void setGiveQuantity(Integer giveQuantity) {
		this.giveQuantity = giveQuantity;
	}
	
	public String getDistributionAmountGroup() {
		return distributionAmountGroup;
	}
	public void setDistributionAmountGroup(String distributionAmountGroup) {
		this.distributionAmountGroup = distributionAmountGroup;
	}
	public String getGrabRedEnvelopeUserIdGroup() {
		return grabRedEnvelopeUserIdGroup;
	}
	public void setGrabRedEnvelopeUserIdGroup(String grabRedEnvelopeUserIdGroup) {
		this.grabRedEnvelopeUserIdGroup = grabRedEnvelopeUserIdGroup;
	}
	public Integer getRemainingQuantity() {
		return remainingQuantity;
	}
	public void setRemainingQuantity(Integer remainingQuantity) {
		this.remainingQuantity = remainingQuantity;
	}
	public String getWishes() {
		return wishes;
	}
	public void setWishes(String wishes) {
		this.wishes = wishes;
	}
	public Long getBindTopicId() {
		return bindTopicId;
	}
	public void setBindTopicId(Long bindTopicId) {
		this.bindTopicId = bindTopicId;
	}
	public Date getGiveTime() {
		return giveTime;
	}
	public void setGiveTime(Date giveTime) {
		this.giveTime = giveTime;
	}
	public BigDecimal getSingleAmount() {
		return singleAmount;
	}
	public void setSingleAmount(BigDecimal singleAmount) {
		this.singleAmount = singleAmount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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
	public boolean isAccessUserUnwrap() {
		return accessUserUnwrap;
	}
	public void setAccessUserUnwrap(boolean accessUserUnwrap) {
		this.accessUserUnwrap = accessUserUnwrap;
	}
	public String getBindTopicTitle() {
		return bindTopicTitle;
	}
	public void setBindTopicTitle(String bindTopicTitle) {
		this.bindTopicTitle = bindTopicTitle;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public BigDecimal getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(BigDecimal refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}

}
