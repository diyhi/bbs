package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 点赞
 *
 */
@Entity
@Table(name="like_0",indexes = {@Index(name="like_1_idx", columnList="userName,addtime")})
public class Like extends LikeEntity implements Serializable{
	private static final long serialVersionUID = -3428288178109539216L;



}
