package cms.bean.payment;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 支付日志
 *
 */
@Entity
@Table(name="paymentlog_0",indexes = {@Index(name="paymentlog_idx", columnList="userName,times")})
public class PaymentLog extends PaymentLogEntity implements Serializable{

	private static final long serialVersionUID = 9188764573185110999L;
	
	
	
	
	
}
