package cms.web.action.user;

import javax.annotation.Resource;

/**
 * 积分日志配置
 *
 */
public class PointLogConfig {
	@Resource PointManage pointManage;
	
	
	/** 支付日志分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	
	 /**
	  * 根据积分Id查询分配到表编号
	  * 根据积分Id和积分日志分表数量求余
	  * @param pointLogId 积分日志Id
	  * 注意：积分Id要先判断是否36位并且最后4位是数字
	  * pointManage.verificationPointLogId(?)
	  * @return
	 */
    public Integer pointLogIdRemainder(String pointLogId){
    	int userId = pointManage.getPointLogUserId(pointLogId);
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
}
