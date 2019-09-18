package cms.web.action.payment;

import javax.annotation.Resource;

/**
 * 支付日志配置
 * @author Gao
 *
 */
public class PaymentLogConfig {
	@Resource PaymentManage paymentManage;
	
	
	/** 支付日志分表数量 **/
	private Integer tableQuantity = 1;
	
	public Integer getTableQuantity() {
		return tableQuantity;
	}
	public void setTableQuantity(Integer tableQuantity) {
		this.tableQuantity = tableQuantity;
	}
	
	
	 /**
	  * 根据流水号查询分配到表编号
	  * 根据流水号和支付日志分表数量求余
	  * @param runningNumber 流水号
	  * 注意：流水号要先判断是否大于9并且只能是正整数
	  * cms.utils.StringUtil.isPositiveNumber("")
	  * @return
	  */
    public Integer runningNumberRemainder(String runningNumber){
    	int userId = paymentManage.getRunningNumberUserId(runningNumber);
    	return userId % this.getTableQuantity();
    }
    /**
     * 根据用户Id查询分配到表编号
     * 根据用户Id和支付日志分表数量求余(用户Id后四位)
     * @param userId 用户Id
     * @return
     */
    public Integer userIdRemainder(Long userId){
    	//选取得后N位用户Id
    	String afterUserId = String.format("%04d", userId%10000);
    	return Integer.parseInt(afterUserId) % this.getTableQuantity();
    }
}
