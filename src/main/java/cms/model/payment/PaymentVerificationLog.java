package cms.model.payment;

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

/**
 * 支付校验日志
 *
 */
@Getter
@Setter
@Entity
@Table(name="paymentverificationlog",indexes = {@Index(name="userName_idx", columnList="parameterId,userName")})
public class PaymentVerificationLog implements Serializable{
	@Serial
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
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime times = LocalDateTime.now();
	
	/** 支付金额 **/
	@Column(nullable=false,precision=12, scale=2)
	private BigDecimal paymentAmount = new BigDecimal("0.00");

}
