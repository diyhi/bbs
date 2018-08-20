package cms.bean.message;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;


/**
 * 私信
 *
 */
@Entity
@Table(name="privatemessage_0",indexes = {@Index(name="privateMessage_1_idx", columnList="userId,status,sendTimeFormat"),@Index(name="privateMessage_2_idx", columnList="userId,friendUserId,status,sendTimeFormat")})
public class PrivateMessage extends PrivateMessageEntity implements Serializable{
	private static final long serialVersionUID = -3323100153035020584L;

}
