package cms.model.staff;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 员工登录日志
 *
 */
@Entity
@Table(name="staffloginlog_0",indexes = {@Index(name="staffLoginLog_idx", columnList="staffId,logonTime")})
public class StaffLoginLog extends StaffLoginLogEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -6929531301029127372L;


	
}
