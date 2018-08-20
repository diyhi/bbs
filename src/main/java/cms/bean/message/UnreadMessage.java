package cms.bean.message;

import java.io.Serializable;

/**
 * 未读消息
 *
 */
public class UnreadMessage implements Serializable{
	private static final long serialVersionUID = -4072100473599042179L;

	/** 未读私信总数 **/
	private Long privateMessageCount = 0L;
	
	/** 未读系统通知总数 **/
	private Long systemNotifyCount = 0L;
	
	/** 未读提醒总数 **/
	private Long remindCount = 0L;

	public Long getPrivateMessageCount() {
		return privateMessageCount;
	}

	public void setPrivateMessageCount(Long privateMessageCount) {
		this.privateMessageCount = privateMessageCount;
	}

	public Long getSystemNotifyCount() {
		return systemNotifyCount;
	}

	public void setSystemNotifyCount(Long systemNotifyCount) {
		this.systemNotifyCount = systemNotifyCount;
	}

	public Long getRemindCount() {
		return remindCount;
	}

	public void setRemindCount(Long remindCount) {
		this.remindCount = remindCount;
	}
	

	
	
	
}
