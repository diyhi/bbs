package cms.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;

/**
 * 限流工具
 * @author Gao
 *
 */
public class RateLimiterUtil {
	private static final Logger logger = LogManager.getLogger(RateLimiterUtil.class);
	
	private static Cache<String,RateLimiter> cache = CacheBuilder.newBuilder()
			//.initialCapacity(1000)//设置缓存初始容量
			.maximumSize(10000)//设置缓存的最大容量数
			//.expireAfterWrite(10, TimeUnit.SECONDS)//缓存一定时间内直接失效   分TimeUnit.MINUTES  秒TimeUnit.SECONDS
            .expireAfterAccess(10, TimeUnit.MINUTES)//缓存被访问后,一定时间后失效  分TimeUnit.MINUTES  秒TimeUnit.SECONDS
            .build();
	
	/**
	 * 限流应用
	 * @param id 应用Id  格式: 接口-识别Id  例如抢红包接口：giveRedEnvelope-default 或 giveRedEnvelope-抢红包Id
	 * @param 每秒许可
	 * @return true:允许继续执行
	 */
	public static boolean apply(String id,double permitsPerSecond){
		
		RateLimiter rateLimiter = null;
		try {
			rateLimiter = cache.get(id, new Callable<RateLimiter>() {
			     @Override
			     public RateLimiter call() throws Exception {
			         return RateLimiter.create(permitsPerSecond);//每秒不超过N个任务被提交
			     }
			 });
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("限流异常",e);
	        }
		}
		
		return rateLimiter.tryAcquire();
	}

}
