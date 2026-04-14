package cms.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 未读消息
 *
 */
@Getter
@Setter
public class UnreadMessage implements Serializable{
	@Serial
    private static final long serialVersionUID = -4072100473599042179L;

	/** 未读私信总数 **/
	private Long privateMessageCount = 0L;
	
	/** 未读系统通知总数 **/
	private Long systemNotifyCount = 0L;
	
	/** 未读提醒总数 **/
	private Long remindCount = 0L;

}
