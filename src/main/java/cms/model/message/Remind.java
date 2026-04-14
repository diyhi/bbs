package cms.model.message;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 提醒
 *
 */
@Entity
@Table(name="remind_0",indexes = {@Index(name="remind_1_idx", columnList="receiverUserId,status,sendTimeFormat"),@Index(name="remind_2_idx", columnList="topicId"),@Index(name="remind_3_idx", columnList="receiverUserId,typeCode,sendTimeFormat"),@Index(name="remind_4_idx", columnList="questionId")})
public class Remind extends RemindEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = 3141074310515107936L;
}
