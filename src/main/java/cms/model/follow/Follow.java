package cms.model.follow;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 关注
 *
 */
@Entity
@Table(name="follow_0",indexes = {@Index(name="follow_1_idx", columnList="userName,addtime")})
public class Follow extends FollowEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -6073764011318403096L;
	

}
