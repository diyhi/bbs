package cms.component.payment;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 支付日志配置
 * @author Gao
 *
 */
@Component("paymentLogConfig")
public class PaymentLogConfig {
	
	/** 支付日志分表数量 **/
	@Value("${bbs.sharding.paymentLogConfig_tableQuantity}")
    @Getter
	private Integer tableQuantity = 1;

	
	 /**
	  * 根据流水号查询分配到表编号
	  * 根据流水号和支付日志分表数量求余
	  * @param runningNumber 流水号
	  * 注意：流水号要先判断是否大于9并且只能是正整数
	  * cms.utils.StringUtil.isPositiveNumber("")
	  * @return
	  */
    public Integer runningNumberRemainder(String runningNumber){
    	int userId = this.getRunningNumberUserId(runningNumber);
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

    /**
     * 取得支付日志流水号的用户Id(后N位)
     * @return
     */
    public int getRunningNumberUserId(String runningNumber){
        String _runningNumber = runningNumber.substring(runningNumber.length()-12, runningNumber.length());
        Long after_runningNumber = Long.parseLong(_runningNumber);
        Long l = after_runningNumber / 100000000L % 10000L;
        return Integer.parseInt(String.valueOf(l));
    }
}
