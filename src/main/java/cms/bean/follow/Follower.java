package cms.bean.follow;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 粉丝
 *
 */
@Entity
@Table(name="follower_0",indexes = {@Index(name="follower_1_idx", columnList="userName,addtime")})
public class Follower extends FollowEntity implements Serializable{

	private static final long serialVersionUID = -8930887708827331743L;
	

}
