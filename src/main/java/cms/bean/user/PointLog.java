package cms.bean.user;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 积分日志
 *
 */
@Entity
@Table(name="pointlog_0",indexes = {@Index(name="pointlog_idx", columnList="userName,times")})
public class PointLog extends PointLogEntity implements Serializable{

	private static final long serialVersionUID = 662427782785168699L;

	
	
	
	
	
	
}
