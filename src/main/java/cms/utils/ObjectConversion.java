package cms.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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
    public static final String LOCALDATETIME = "LocalDateTime";

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
		}else if(LOCALDATETIME.equals(type)){
            if(value != null){
                // 如果 JPA 已经返回了 java.time.LocalDateTime
                if (value instanceof LocalDateTime) {
                    return (T) value;
                }
                //从 JPA/JDBC 返回的是 java.sql.Timestamp
                if (value instanceof java.sql.Timestamp) {
                    // java.sql.Timestamp 自带 toLocalDateTime() 方法，完美兼容
                    return (T) ((java.sql.Timestamp) value).toLocalDateTime();
                }

                if (value instanceof java.util.Date) {
                    // 通过 Instant 和系统时区进行安全转换
                    return (T) ((java.util.Date) value).toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
                }
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


    /**
     * 将 Joda-Time DateTime 转换为 Java 8 LocalDateTime。
     * 转换时会基于原始 DateTime 所携带的时区。
     * @param jodaDateTime Joda-Time DateTime 对象
     * @return Java 8 的 LocalDateTime 对象

    public static LocalDateTime convertJodaToJdk8(DateTime jodaDateTime) {

        //获取 Joda DateTime 的毫秒数
        long epochMilli = jodaDateTime.getMillis();

        //将 Joda 的时区 ID 转换为 Java 8 的 ZoneId
        ZoneId zoneId = ZoneId.of(jodaDateTime.getZone().getID());

        //基于毫秒数创建 Java 8 的 Instant
        Instant instant = Instant.ofEpochMilli(epochMilli);

        //将 Instant 应用到 ZoneId，得到 ZonedDateTime，再获取 LocalDateTime
        return LocalDateTime.ofInstant(instant, zoneId);
    }*/
	
}
