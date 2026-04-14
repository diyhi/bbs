package cms.model.favorite;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 话题收藏
 *
 */
@Entity
@Table(name="topicfavorite_0",indexes = {@Index(name="topicFavorite_1_idx", columnList="topicId,addtime")})
public class TopicFavorite extends TopicFavoriteEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 6369571392400904412L;
	
	

}
