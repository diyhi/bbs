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
	
	private static final Cache<String,RateLimiter> cache_minutes = CacheBuilder.newBuilder()
			//.initialCapacity(1000)//设置缓存初始容量
			.maximumSize(10000)//设置缓存的最大容量数
			//.expireAfterWrite(10, TimeUnit.SECONDS)//缓存一定时间内直接失效   分TimeUnit.MINUTES  秒TimeUnit.SECONDS
            .expireAfterAccess(2L, TimeUnit.MINUTES)//缓存被访问后,一定时间后失效  分TimeUnit.MINUTES  秒TimeUnit.SECONDS
            .build();
	
	
	private static final Cache<String,RateLimiter> cache_seconds = CacheBuilder.newBuilder()
			//.initialCapacity(1000)//设置缓存初始容量
			.maximumSize(10000)//设置缓存的最大容量数
			//.expireAfterWrite(10, TimeUnit.SECONDS)//缓存一定时间内直接失效   分TimeUnit.MINUTES  秒TimeUnit.SECONDS
            .expireAfterAccess(60, TimeUnit.SECONDS)//缓存被访问后,一定时间后失效  分TimeUnit.MINUTES  秒TimeUnit.SECONDS
            .build();
	
	/**
	 * 限流应用
	 * @param id 应用Id  格式: 接口-识别Id  例如抢红包接口：giveRedEnvelope-default 或 giveRedEnvelope-抢红包Id
	 * @param permitsPerSecond 每分钟许可
	 * @param warmupPeriod 表示在从冷启动速率过渡到平均速率的时间间隔 单位/秒
	 * @return true:允许继续执行
	 */
	public static boolean apply_minutes(String id,double permitsPerSecond,Long warmupPeriod){
		
		RateLimiter rateLimiter = null;
		try {
			rateLimiter = cache_minutes.get(id, new Callable<RateLimiter>() {
			     @Override
			     public RateLimiter call() throws Exception {
			    	 if(warmupPeriod == null){
			    		 return RateLimiter.create(permitsPerSecond/60);//每秒不超过N个任务被提交    例如100/60表示 每秒约1.666个令牌
			    	 }else{
			    		//return RateLimiter.create(permitsPerSecond/60, 30, TimeUnit.SECONDS);  // 30秒内逐步达到稳定速率
				    	 return RateLimiter.create(permitsPerSecond/60, warmupPeriod, TimeUnit.SECONDS);  // warmupPeriod秒内逐步达到稳定速率
			    	 }
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
	
	/**
	 * 限流应用
	 * @param id 应用Id  格式: 接口-识别Id  例如抢红包接口：giveRedEnvelope-default 或 giveRedEnvelope-抢红包Id
	 * @param permitsPerSecond 每秒许可
	 * @return true:允许继续执行
	 */
	public static boolean apply_seconds(String id,double permitsPerSecond){
		
		RateLimiter rateLimiter = null;
		try {
			rateLimiter = cache_seconds.get(id, new Callable<RateLimiter>() {
			     @Override
			     public RateLimiter call() throws Exception {
			         return RateLimiter.create(permitsPerSecond);//每秒不超过N个任务被提交
			       //permitsPerSecond表示每秒新增的令牌数； warmupPeriod 表示在从冷启动速率过渡到平均速率的时间间隔；  TimeUnit.MILLISECONDS 毫秒
			         //return RateLimiter.create(permitsPerSecond, 1000, TimeUnit.MILLISECONDS);
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
