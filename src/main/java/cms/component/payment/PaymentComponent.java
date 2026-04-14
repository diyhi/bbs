package cms.component.payment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


import cms.model.payment.PaymentLog;
import jakarta.annotation.Resource;
import org.springframework.cglib.beans.BeanCopier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 支付组件
 * @author Gao
 *
 */
@Component("paymentComponent")
public class PaymentComponent {
	private static final Logger logger = LogManager.getLogger(PaymentComponent.class);
	
	private final AtomicInteger number = new AtomicInteger(new Random().nextInt(88888888));//AtomicInteger 的最大值是2147483647，超过这个数字在递增的话就变成-2147483648
	/** 年月日时分秒毫秒 yyyyMMddHHmmssSSS */
    public static final String dtLong = "yyyyMMddHHmmssSSS";
    
	
    @Resource
    PaymentLogConfig paymentLogConfig;
   
	
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
				c = Class.forName("cms.model.payment.PaymentLog_"+tableNumber);
                //获取无参数构造函数
                Constructor<?> constructor = c.getDeclaredConstructor();

                //使用构造函数创建新实例
                Object object = constructor.newInstance();
				
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
			} catch (InvocationTargetException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成支付日志对象",e);
                }
            } catch (NoSuchMethodException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("生成支付日志对象",e);
                }
            }
		}
		return null;
    }
    
}
