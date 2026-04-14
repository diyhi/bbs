package cms.model.topic;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


/**
 * 话题取消隐藏
 *
 */
@Getter
@Setter
@Entity
@Table(name="topicunhide_0",indexes = {@Index(name="topicUnhide_1_idx", columnList="topicId,cancelTime")})
public class TopicUnhide extends UnhideEntity implements Serializable{

	@Serial
    private static final long serialVersionUID = 6455873305843959259L;

}
