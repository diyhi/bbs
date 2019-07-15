package cms.bean.user;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 用户动态
 *
 */
@Entity
@Table(name="userdynamic_0",indexes = {@Index(name="userDynamic_1_idx", columnList="userName,status,postTime"),@Index(name="userDynamic_2_idx", columnList="topicId,userName,module"),@Index(name="userDynamic_3_idx", columnList="commentId,userName,module"),@Index(name="userDynamic_4_idx", columnList="replyId,userName,module")})
public class UserDynamic extends UserDynamicEntity implements Serializable{

	private static final long serialVersionUID = 8676510375787469569L;

	
}
