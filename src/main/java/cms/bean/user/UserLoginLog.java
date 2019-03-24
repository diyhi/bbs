package cms.bean.user;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 用户登录日志
 *
 */
@Entity
@Table(name="userloginlog_0",indexes = {@Index(name="userLoginLog_idx", columnList="userId,logonTime")})
public class UserLoginLog extends UserLoginLogEntity implements Serializable{
	
	private static final long serialVersionUID = 3201493519128327032L;


}
