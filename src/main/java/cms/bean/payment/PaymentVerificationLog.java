package cms.bean.payment;

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

/**
 * 支付校验日志
 *
 */
@Entity
@Table(name="paymentverificationlog",indexes = {@Index(name="userName_idx", columnList="parameterId,userName")})
public class PaymentVerificationLog implements Serializable{
	private static final long serialVersionUID = 276638020371536856L;

	/** 支付流水号 **/
	@Id @Column(length=32)
	private String id;
	
	/** 支付模块 1.订单支付  5.用户充值  **/
	private Integer paymentModule;
	/** 参数Id    订单Id ,用户Id **/
	private Long parameterId;
	
	/** 用户名称 **/
	@Column(length=30)
	private String userName;
	
	/** 时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	private Date times = new Date();
	
	/** 支付金额 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal paymentAmount = new BigDecimal("0.00");
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPaymentModule() {
		return paymentModule;
	}
	public void setPaymentModule(Integer paymentModule) {
		this.paymentModule = paymentModule;
	}
	public Long getParameterId() {
		return parameterId;
	}
	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getTimes() {
		return times;
	}
	public void setTimes(Date times) {
		this.times = times;
	}
	public BigDecimal getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(BigDecimal paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	
	
	
}
