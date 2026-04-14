package cms.dto.payment;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 付款表单
 */
@Getter
@Setter
public class PaymentRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -998972590836960547L;

    /** 用户Id  **/
    private Long id;
    /** 支付类型  1:支付流水号  2:预存款 3:积分 **/
    private Integer type;

    /** 金额 **/
    private BigDecimal paymentRunningNumberAmount;
    /** 流水号 **/
    private String paymentRunningNumber;
    /** 支付流水号充值备注 **/
    private String paymentRunningNumber_remark;

    /** 预存款支付符号 **/
    private String deposit_symbol;
    /** 预存款 **/
    private BigDecimal deposit;
    /** 预存款支付备注 **/
    private String deposit_remark;

    /** 增减积分符号 **/
    private String point_symbol;
    /** 积分 **/
    private Long point;
    /** 增减积分备注 **/
    private String point_remark;

}
