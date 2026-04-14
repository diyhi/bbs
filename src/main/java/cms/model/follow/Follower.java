package cms.model.follow;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 粉丝
 *
 */
@Entity
@Table(name="follower_0",indexes = {@Index(name="follower_1_idx", columnList="userName,addtime")})
public class Follower extends FollowEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = -8930887708827331743L;
	

}
