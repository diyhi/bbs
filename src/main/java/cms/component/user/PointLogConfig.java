package cms.component.user;


import cms.utils.UUIDUtil;
import cms.utils.Verification;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 积分日志配置
 *
 */
@Component("pointLogConfig")
public class PointLogConfig {
	
	
	/** 支付日志分表数量 **/
	@Value("${bbs.sharding.pointLogConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	 /**
	  * 根据积分Id查询分配到表编号
	  * 根据积分Id和积分日志分表数量求余
	  * @param pointLogId 积分日志Id
	  * 注意：积分Id要先判断是否36位并且最后4位是数字
	  * pointManage.verificationPointLogId(?)
	  * @return
	 */
    public Integer pointLogIdRemainder(String pointLogId){
    	int userId = this.getPointLogUserId(pointLogId);
    	return userId % this.getTableQuantity();
    } 
    /**
     * 根据用户Id查询分配到表编号
     * 根据用户Id和积分日志分表数量求余(用户Id后四位)
     * @param userId 用户Id
     * @return
     */
    public Integer userIdRemainder(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	return Integer.parseInt(afterUserId) % this.getTableQuantity();
    }

    /**
     * 取得积分日志Id的用户Id(后N位)
     * @param pointLogId 积分日志Id
     * @return
     */
    public int getPointLogUserId(String pointLogId){
        String after_userId = pointLogId.substring(pointLogId.length()-4, pointLogId.length());
        return Integer.parseInt(after_userId);
    }

    /**
     * 生成积分Id
     * 积分Id由32位uuid+1位用户Id后4位组成
     * @param userId 用户Id
     * @return
     */
    public String createPointLogId(Long userId){
        //选取得后N位用户Id
        String afterUserId = String.format("%04d", userId%10000);
        String id = UUIDUtil.getUUID32()+afterUserId;
        return id;
    }
    /**
     * 校验积分Id
     * 积分Id要先判断是否36位并且最后4位是数字
     * @param pointLogId 积分Id
     * @return
     */
    public boolean verificationPointLogId(String pointLogId){
        if(pointLogId != null && !"".equals(pointLogId.trim())){
            if(pointLogId.length() == 36){
                String after_userId = pointLogId.substring(pointLogId.length()-4, pointLogId.length());
                boolean verification = Verification.isPositiveIntegerZero(after_userId);//数字
                if(verification){
                    return true;
                }
            }
        }
        return false;
    }

}
