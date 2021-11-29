package cms.bean.membershipCard;

import java.io.Serializable;
import java.util.Date;


/**
 * 限制条件组
 * @author Gao
 *
 */
public class RestrictionGroup  implements Serializable{
	private static final long serialVersionUID = 7410981714140155734L;


	/** 用户注册时间范围起始  **/
	private Date registrationTime_start;
	/** 用户注册时间范围结束  **/
	private Date registrationTime_end;
	
	/** 活动期间积分达到数量  **/
	private Long totalPoint;
	
	/** 活动期间发话题达到数量  **/
	//private Long totalTopic;
	
	/** 活动期间完成浏览任务达到数量  **/
	//private Long totalTopic;
	
	
	public Date getRegistrationTime_start() {
		return registrationTime_start;
	}

	public void setRegistrationTime_start(Date registrationTime_start) {
		this.registrationTime_start = registrationTime_start;
	}

	public Date getRegistrationTime_end() {
		return registrationTime_end;
	}

	public void setRegistrationTime_end(Date registrationTime_end) {
		this.registrationTime_end = registrationTime_end;
	}

	public Long getTotalPoint() {
		return totalPoint;
	}

	public void setTotalPoint(Long totalPoint) {
		this.totalPoint = totalPoint;
	}

	
}
