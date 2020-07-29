package cms.bean.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 支付日志Entity
 *
 */

@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class PaymentLogEntity implements Serializable{
	private static final long serialVersionUID = 2532403539870249004L;

	/** 支付流水号 **/
	@Id @Column(length=32)
	protected String paymentRunningNumber;
	
	/** 支付模块 1.订单支付   5.用户充值 6.账户提现 70.余额购买话题隐藏内容 80.解锁话题隐藏内容分成 90.悬赏金额 100.采纳答案 110.调整赏金 120.话题发红包 130.话题收红包 140.话题返还红包 **/
	protected Integer paymentModule;
	/** 参数Id    用户Id 话题Id 评论Id 问题Id 答案Id 发红包Id
	protected Long parameterId;**/
	/** 来源参数Id    用户Id 话题Id 评论Id 问题Id 答案Id 发红包Id **/
	@Column(length=150)
	protected String sourceParameterId;
	
	/** 接口产品  -1:员工操作  0:预存款支付  1.支付宝即时到账  4.支付宝手机网站 **/
	protected Integer interfaceProduct;
	/** 交易号 **/
	protected String tradeNo;
	
	/**操作用户类型  0:系统  1: 员工  2:会员 **/ 
	protected Integer operationUserType = 0;
	/**操作用户名称 **/ 
	@Column(length=50)
	protected String operationUserName;
	/** 用户名称 **/
	@Column(length=30)
	protected String userName;
	/** 金额状态  1:账户存入  2:账户支出 **/
	protected int amountState = 1;
	
	
	/** 金额  精度为14位，小数点位数为4位 **/
	@Column(nullable=false,precision=14, scale=4)
	protected BigDecimal amount = new BigDecimal("0");

	
	/** 时间 **/
	@Temporal(TemporalType.TIMESTAMP)
	protected Date times = new Date();

	/** 备注 **/
	@Lob
	protected String remark = "";

	public String getPaymentRunningNumber() {
		return paymentRunningNumber;
	}

	public void setPaymentRunningNumber(String paymentRunningNumber) {
		this.paymentRunningNumber = paymentRunningNumber;
	}
	public Integer getPaymentModule() {
		return paymentModule;
	}

	public void setPaymentModule(Integer paymentModule) {
		this.paymentModule = paymentModule;
	}

	
	public String getSourceParameterId() {
		return sourceParameterId;
	}

	public void setSourceParameterId(String sourceParameterId) {
		this.sourceParameterId = sourceParameterId;
	}

	public Integer getInterfaceProduct() {
		return interfaceProduct;
	}

	public void setInterfaceProduct(Integer interfaceProduct) {
		this.interfaceProduct = interfaceProduct;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getAmountState() {
		return amountState;
	}

	public void setAmountState(int amountState) {
		this.amountState = amountState;
	}

	public Integer getOperationUserType() {
		return operationUserType;
	}

	public void setOperationUserType(Integer operationUserType) {
		this.operationUserType = operationUserType;
	}

	public String getOperationUserName() {
		return operationUserName;
	}

	public void setOperationUserName(String operationUserName) {
		this.operationUserName = operationUserName;
	}

	
}
