package cms.model.message;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


/**
 * 私信
 *
 */
@Entity
@Table(name="privatemessage_0",indexes = {@Index(name="privateMessage_1_idx", columnList="userId,status,sendTimeFormat"),@Index(name="privateMessage_2_idx", columnList="userId,friendUserId,status,sendTimeFormat")})
public class PrivateMessage extends PrivateMessageEntity implements Serializable{
	@Serial
    private static final long serialVersionUID = -3323100153035020584L;

}
