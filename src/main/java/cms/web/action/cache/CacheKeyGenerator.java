package cms.web.action.cache;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.ClassUtils;

import com.google.common.hash.Hashing;

/**
 * 缓存key生成策略
 * Spring默认的SimpleKeyGenerator是不会将函数名组合进key中的,此外，原子类型的数组，直接作为key使用也是不会生效的
 * 本类解决：多参数、原子类型数组、方法名识别 等问题
 */
public class CacheKeyGenerator implements KeyGenerator{
	private static final Logger logger = LogManager.getLogger(CacheKeyGenerator.class);
	
	// custom cache key  
    public static final int NO_PARAM_KEY = 0;  
    public static final int NULL_PARAM_KEY = 53;  
      
    @Override  
    public Object generate(Object target, Method method, Object... params) {  
  
        StringBuilder key = new StringBuilder();  
        key.append(target.getClass().getSimpleName()).append(".").append(method.getName()).append(":");  
        if (params.length == 0) {  
            return key.append(NO_PARAM_KEY).toString();  
        }  
        for (Object param : params) {  
            if (param == null) {  
            //	logger.warn("Spring缓存输入空参数, 使用默认key={}"+NULL_PARAM_KEY);  
                key.append(NULL_PARAM_KEY);  
            } else if (ClassUtils.isPrimitiveArray(param.getClass())) {  
                int length = Array.getLength(param);  
                for (int i = 0; i < length; i++) {  
                    key.append(Array.get(param, i));  
                    key.append(',');  
                }  
            } else if (ClassUtils.isPrimitiveOrWrapper(param.getClass()) || param instanceof String) {  
                key.append(param);  
            } else {  
           // 	logger.warn("使用对象作为缓存可能会导致意外的结果. " +  
            //            "要么使用 @Cacheable(key=..) 或实现CacheKey. 方法是 " + target.getClass() + "#" + method.getName());  
                key.append(param.hashCode());  
            }  
            key.append('-');  
        }  
  
        String finalKey = key.toString();  
        long cacheKeyHash = Hashing.murmur3_128().hashString(finalKey, Charset.defaultCharset()).asLong();  
        if(logger.isDebugEnabled()){
        	 logger.debug("using cache key={} hashCode={}"+ finalKey+" "+ cacheKeyHash);  
        }
        return key.toString();  
    }  
}
