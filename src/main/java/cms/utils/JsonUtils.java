package cms.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.StdSerializerProvider;
import org.codehaus.jackson.type.TypeReference;
**/
/**
 * Jackson扩展封装
 *
 */
public class JsonUtils {
	private static final Logger logger = LogManager.getLogger(JsonUtils.class);
	
	final static ObjectMapper objectMapper;
	static {
		objectMapper = new ObjectMapper();

		//设置null转换""   
		//objectMapper.getSerializerProvider().setNullValueSerializer(new NullSerializer());
		
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //JSON转POJO时，若JSON中的某个字段在POJO中未定义，在默认情况下会抛异常转换失败，只要增加这个配置
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
     
        //序列换成json时,将所有的long变成string,因为js中的数字类型不能包含所有的java long值
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);

    //    jackson2HttpMessageConverter.setObjectMapper(objectMapper);
     //   converters.add(jackson2HttpMessageConverter);


    }
 
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
   
  //null的JSON序列   
    private static class NullSerializer extends JsonSerializer<Object> {   
        public void serialize(Object value, JsonGenerator jgen,   
                SerializerProvider provider) throws IOException,   
                JsonProcessingException {   
            jgen.writeString("");   
        }   
    } 
    /**
     * JSON串转换为Java泛型对象，可以是各种类型，此方法最为强大。用法看测试用例。
     * @param <T>
     * @param jsonString JSON字符串
    * @param tr TypeReference,例如: new TypeReference< List<FamousUser> >(){}
     * @return List对象列表
     */
    public static <T> T toGenericObject(String jsonString, TypeReference<T> tr) {
 
        if (jsonString == null || "".equals(jsonString)) {
            return null;
        } else {
            try {
                return objectMapper.readValue(jsonString, tr);
            } catch (Exception e) {
	            //	e.printStackTrace();
	            if (logger.isErrorEnabled()) {
	            	logger.error("JSON串转换为Java泛型对象",e);
	   		    }
            
            }
        }
        return null;
    }
 
    /**
     * Java对象转Json字符串
     * 本方法转换后字符串可以兼容各浏览器
     * @param object Java对象，可以是对象，数组，List,Map等
     * @return json 字符串
     */
    public static String toJSONString(Object object) {
        String jsonString = "";
        try {
            
             jsonString = objectMapper.writeValueAsString(object);
            
        } catch (Exception e) {
        	//e.printStackTrace();
        	if (logger.isErrorEnabled()) {
            	logger.error("Java对象转Json字符串",e);
   		    }
        }
        return jsonString;
 
    }
 
    /**
     * Json字符串转Java对象
     * @param jsonString
     * @param c
     * @return
     */
	public static <T> T toObject(String jsonString, Class<?> c) {
    	
        if (jsonString == null || "".equals(jsonString)) {
        	
            return (T) "";
        } else {
            try {
            	return (T)objectMapper.readValue(jsonString, c);	
            } catch (Exception e) {
            	if (logger.isErrorEnabled()) {
                	logger.error("Json字符串转Java对象",e);
       		    }
            }
 
        }
        return (T) "";
    }
 
	
}
