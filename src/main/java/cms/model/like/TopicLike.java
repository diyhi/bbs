package cms.model.like;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 话题点赞
 *
 */
@Entity
@Table(name="topiclike_0",indexes = {@Index(name="topicLike_1_idx", columnList="topicId,addtime")})
public class TopicLike extends TopicLikeEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -855451993015782474L;
	

	

}
