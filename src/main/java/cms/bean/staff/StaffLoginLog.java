package cms.bean.staff;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 员工登录日志
 *
 */
@Entity
@Table(name="staffloginlog_0",indexes = {@Index(name="staffLoginLog_idx", columnList="staffId,logonTime")})
public class StaffLoginLog extends StaffLoginLogEntity implements Serializable{
	private static final long serialVersionUID = -6929531301029127372L;


	
}
