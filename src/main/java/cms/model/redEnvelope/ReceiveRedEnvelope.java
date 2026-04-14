package cms.model.redEnvelope;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * 收红包
 * @author Gao
 *
 */
@Entity
@Table(name="receiveredenvelope_0",indexes = {@Index(name="receiveRedEnvelope_1_idx", columnList="receiveUserId,receiveTime")})
public class ReceiveRedEnvelope extends ReceiveRedEnvelopeEntity implements Serializable{
	private static final long serialVersionUID = 2312569666444084709L;

}
