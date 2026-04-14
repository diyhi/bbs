package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 积分日志
 *
 */
@Entity
@Table(name="pointlog_0",indexes = {@Index(name="pointlog_idx", columnList="userName,times")})
public class PointLog extends PointLogEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 662427782785168699L;

	

}
