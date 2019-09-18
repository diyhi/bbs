package cms.web.action.payment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import cms.bean.payment.PaymentLog;

import net.sf.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 支付管理
 * @author Gao
 *
 */
@Component("paymentManage")
public class PaymentManage {
	private static final Logger logger = LogManager.getLogger(PaymentManage.class);
	
	private AtomicInteger number = new AtomicInteger(new Random().nextInt(88888888));//AtomicInteger 的最大值是2147483647，超过这个数字在递增的话就变成-2147483648
	/** 年月日时分秒毫秒 yyyyMMddHHmmssSSS */
    public static final String dtLong = "yyyyMMddHHmmssSSS";
    
	
    @Resource PaymentLogConfig paymentLogConfig;
   
	
	/**
	 * 生成流水号
	 * @param userId 用户Id
	 * @return
	 */
	public String createRunningNumber(Long userId){
		
		//这里是atoNum到MAX_VALUE=9999999的时候重新设成0
    	int MAX_VALUE = 99999999;
    	number.compareAndSet(MAX_VALUE, 0);
    	
    	
    	//userId%10000 取后4位
		String userId_after = (String.format("%04d", userId%10000));
		
		Date date=new Date();
		DateFormat df=new SimpleDateFormat(dtLong);
		
		
		return df.format(date)+userId_after+(String.format("%08d", number.incrementAndGet()));
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
    
    
    /**
     * 生成支付日志对象
     * @return
     */
    public Object createPaymentLogObject(PaymentLog paymentLog){
    	//表编号
		int tableNumber = paymentLogConfig.runningNumberRemainder(paymentLog.getPaymentRunningNumber());
		if(tableNumber == 0){//默认对象为PaymentLog
			return paymentLog;
		}else{//带下划线对象
			Class<?> c;
			try {
				c = Class.forName("cms.bean.payment.PaymentLog_"+tableNumber);
				Object object = c.newInstance();
				
				BeanCopier copier = BeanCopier.create(PaymentLog.class,object.getClass(), false); 
			
				copier.copy(paymentLog,object, null);
				return object;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成支付日志对象",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成支付日志对象",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("生成支付日志对象",e);
		        }
			}	
		}
		return null;
    }
    
}
