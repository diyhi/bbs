package cms.model.user;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 用户动态
 *
 */
@Entity
@Table(name="userdynamic_0",indexes = {@Index(name="userDynamic_1_idx", columnList="userName,status,postTime"),@Index(name="userDynamic_5_idx", columnList="functionIdGroup,userName,module")})
public class UserDynamic extends UserDynamicEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 8676510375787469569L;

	
}
