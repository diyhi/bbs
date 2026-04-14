package cms.model.payment;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 支付日志
 *
 */
@Entity
@Table(name="paymentlog_0",indexes = {@Index(name="paymentlog_idx", columnList="userName,times")})
public class PaymentLog extends PaymentLogEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 9188764573185110999L;
	
	
	
	
	
}
