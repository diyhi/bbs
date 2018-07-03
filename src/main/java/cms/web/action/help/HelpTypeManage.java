package cms.web.action.help;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.stereotype.Component;

/**
 * 帮助
 *
 */
@Component("helpTypeManage")
public class HelpTypeManage {
	private AtomicInteger number = new AtomicInteger(new Random().nextInt(88888));//AtomicInteger 的最大值是2147483647，超过这个数字在递增的话就变成-2147483648
	private DateTime begin = new DateTime(2010,01,01,01,01,01,0);   
	
	/**
     * 取得下一个Id
     * 商品分类Id组成(2010年后的年月日时分秒+本机Id五位)
     */
    public Long nextNumber(){
    	//这里是atoNum到MAX_VALUE=9999的时候重新设成0
    	int MAX_VALUE = 99999;
    	number.compareAndSet(MAX_VALUE, 0);
    	DateTime end = new DateTime();  
		//计算区间毫秒数   
		Duration d = new Duration(begin, end);  
		long second = d.getStandardSeconds();//秒	
    
    	return Long.parseLong(second+(String.format("%05d", number.incrementAndGet())));
    }
}
