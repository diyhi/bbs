package cms.model.membershipCard;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * 限制条件组
 * @author Gao
 *
 */
@Getter
@Setter
public class RestrictionGroup  implements Serializable{
	@Serial
    private static final long serialVersionUID = 7410981714140155734L;


	/** 用户注册时间范围起始  **/
	private LocalDateTime registrationTime_start;
	/** 用户注册时间范围结束  **/
	private LocalDateTime registrationTime_end;
	
	/** 活动期间积分达到数量  **/
	private Long totalPoint;
	
	/** 活动期间发话题达到数量  **/
	//private Long totalTopic;
	
	/** 活动期间完成浏览任务达到数量  **/
	//private Long totalTopic;
	

}
