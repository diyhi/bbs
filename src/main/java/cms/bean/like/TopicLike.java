package cms.bean.like;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 话题点赞
 *
 */
@Entity
@Table(name="topiclike_0",indexes = {@Index(name="topicLike_1_idx", columnList="topicId,addtime")})
public class TopicLike extends TopicLikeEntity implements Serializable{
	private static final long serialVersionUID = -855451993015782474L;
	

	

}
