package cms.model.riskControl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 风控处罚
 * @author Gao
 *
 */
@Getter
@Setter
public class Penalty implements Serializable{
	@Serial
    private static final long serialVersionUID = -8827866723558773561L;
	
	
	/** 禁止的功能集合 添加BlockedFeaturesEnum的值 **/
	private List<RiskControlBlockedFeatures> riskControlBlockedFeaturesList = new ArrayList<RiskControlBlockedFeatures>();
	
	/** 禁止时长 **/
	private Integer banDuration;
	/** 禁止时长单位  10.分钟 20.小时 30.日 40.月 50.年**/
	private Integer banDurationUnit;

	
	/** 执行贴子功能集合  添加RiskControlPostFeatures的值 **/
	private List<RiskControlPostFeatures> riskControlPostFeaturesList = new ArrayList<RiskControlPostFeatures>();
	
	
	
	/** 封禁IP **/
	private boolean blockIP = false;
	/** 封禁IP时长**/
	private Integer blockIpDuration;
	/** 封禁IP时间单位  10.分钟 20.小时 30.日 40.月 50.年**/
	private Integer blockIpDurationUnit;
	
}
