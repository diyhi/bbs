package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 点赞
 *
 */
@Entity
@Table(name="like_0",indexes = {@Index(name="like_1_idx", columnList="userName,addtime")})
public class Like extends LikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -3428288178109539216L;



}
