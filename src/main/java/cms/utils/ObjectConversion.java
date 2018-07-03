package cms.utils;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 对象转换
 *
 */
public class ObjectConversion {
	private static final DateTimeFormatter date_format = DateTimeFormat.forPattern("yyyy-MM-dd");
	private static final DateTimeFormatter time_format = DateTimeFormat.forPattern("HH:mm:ss");
	private static final DateTimeFormatter timestamp_format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S");
	
	
	public static final String INTEGER = "Integer";
	public static final String LONG = "Long";
	public static final String BIGDECIMAL = "BigDecimal";
	public static final String DATE = "Date";
	public static final String TIME = "Time";
	public static final String TIMESTAMP = "Timestamp";

	public static final String STRING = "String";
	public static final String BOOLEAN = "Boolean";
	
	
	@SuppressWarnings("unchecked")
	public static <T> T conversion(Object value,String type){
		if(INTEGER.equals(type)){
			if(value != null){
				return (T) Integer.valueOf(value.toString());
			}
		}else if(LONG.equals(type)){
			if(value != null){
				return (T) Long.valueOf(value.toString());
			}	
		}else if(BIGDECIMAL.equals(type)){
			if(value != null){
				return (T) new BigDecimal(value.toString());
			}	
		}else if(DATE.equals(type)){
			if(value != null){
				DateTime dateTime = DateTime.parse(value.toString(), date_format); 
				return (T)dateTime.toDate();
			}	
		}else if(TIME.equals(type)){
			if(value != null){
				DateTime dateTime = DateTime.parse(value.toString(), time_format); 
				return (T)dateTime.toDate();
			}	
		}else if(TIMESTAMP.equals(type)){
			if(value != null){
				  //时间解析  
		        DateTime dateTime = DateTime.parse(value.toString(), timestamp_format); 
				return (T)dateTime.toDate();
			}	
		}else if(STRING.equals(type)){
			if(value != null){
				return (T)value.toString();
			}	
		}else if(BOOLEAN.equals(type)){
			if(value != null){
				return (T)Boolean.valueOf(value.toString());
			}	
		}
		
		
		return null;
		
	}

}
