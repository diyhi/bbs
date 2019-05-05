package cms.bean.favorite;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 话题收藏
 *
 */
@Entity
@Table(name="topicfavorite_0",indexes = {@Index(name="topicFavorite_1_idx", columnList="topicId,addtime")})
public class TopicFavorite extends TopicFavoriteEntity implements Serializable{

	private static final long serialVersionUID = 6369571392400904412L;
	
	

}
