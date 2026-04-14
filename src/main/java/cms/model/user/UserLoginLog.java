package cms.model.user;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 用户登录日志
 *
 */
@Entity
@Table(name="userloginlog_0",indexes = {@Index(name="userLoginLog_idx", columnList="userId,logonTime")})
public class UserLoginLog extends UserLoginLogEntity implements Serializable{
	
	@Serial
    private static final long serialVersionUID = 3201493519128327032L;


}
