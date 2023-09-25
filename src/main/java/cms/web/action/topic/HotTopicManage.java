package cms.web.action.topic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;

import cms.bean.setting.SystemSetting;
import cms.bean.topic.Topic;
import cms.service.setting.SettingService;
import cms.service.topic.TopicService;
import cms.utils.HotUtil;
import cms.utils.UniqueBlockingQueue;
import cms.web.action.like.LikeManage;

/**
 * 热门话题
 * @author Gao
 *
 */
@Component("hotTopicManage")
public class HotTopicManage implements InitializingBean{
	private static final Logger logger = LogManager.getLogger(HotTopicManage.class);
	
	@Resource SettingService settingService;
	@Resource TopicManage topicManage;
	@Resource LikeManage likeManage;
	@Resource TopicService topicService;
	
	
	
	//创建一个可重用固定线程数的线程池  
	private ExecutorService pool = Executors.newFixedThreadPool(1);  
    
	private static final UniqueBlockingQueue<Long> uniqueQueue = new UniqueBlockingQueue<Long>(1000000);
	//线程活动
	private volatile boolean threadActivity = true;
	
	/**
	 * 添加热门话题
	 * @param topic
	 */
	public void addHotTopic(Topic topic){
		if(topic == null){
			return;
		}
		if(topic.getWeight() != null && topic.getWeight() <0d){//已强制下沉的话题不再添加
			return;
		}
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		if(systemSetting.getTopicHotRecommendedTime() != null){
			 DateTime current = new DateTime();
			 DateTime allowTime = current.minusHours(systemSetting.getTopicHotRecommendedTime());//最早允许时间
			 
			 DateTime postTime = new DateTime(topic.getPostTime());
			 if(postTime.compareTo(allowTime) >=0){
				 uniqueQueue.offer(topic.getId());
			 }else{//超出时间限制的热门话题权重在重新计算时需要设置为0
				 if(topic.getWeight() != null && topic.getWeight() >0d){
					 uniqueQueue.offer(topic.getId());
				 }
			 }
		}else{
			uniqueQueue.offer(topic.getId());
		}
	}
	
	
	/**
	 * 保存热门话题
	 * @param topicIdList 话题Id集合
	 */
	private void saveHotTopic(List<Long> topicIdList){
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		Map<Long,Double> batchWeight = new HashMap<Long,Double>();
		for(Long topicId : topicIdList){
			Topic topic = topicManage.queryTopicCache(topicId);//查询缓存 
			if(topic != null){
				
				if(!topic.getStatus().equals(20)){
					batchWeight.put(topicId, 0d);
					continue;
				}
				
				if(systemSetting.getTopicHotRecommendedTime() != null){
					if(topic.getWeight() != null && topic.getWeight() >0d){
						DateTime current = new DateTime();
						DateTime allowTime = current.minusHours(systemSetting.getTopicHotRecommendedTime());//最早允许时间
						 
						DateTime postTime = new DateTime(topic.getPostTime());
						if(postTime.compareTo(allowTime) <0){//超出时间限制的热门话题权重在重新计算时需要设置为0
							batchWeight.put(topicId, 0d);
							continue;
						}
					}
					
				}
				
				double distanceTime = HotUtil.computeDistanceTime(topic.getPostTime());
				
				Long commentCount = topic.getCommentTotal();//评论数
				
				Long likeCount = likeManage.query_cache_findLikeCountByTopicId(topicId);//点赞数
				
				Long viewCount = topic.getViewTotal();//浏览数
				
				Double G = 1.8d;//重力因子
				
				Map<String,Object> heatMap = HotUtil.parseTopicHeatFactor(systemSetting.getTopicHeatFactor());//话题热度因子分数
				for(Map.Entry<String,Object> entry : heatMap.entrySet()){
					if(entry.getKey().equals("评论")){
						commentCount = commentCount * (Long)entry.getValue();
					}else if(entry.getKey().equals("点赞")){
						likeCount = likeCount * (Long)entry.getValue();
					}else if(entry.getKey().equals("浏览量")){
						viewCount = viewCount * (Long)entry.getValue();
					}else if(entry.getKey().equals("重力因子")){
						G = (Double)entry.getValue();
					}
					
				}
				
				Long count = commentCount+likeCount+viewCount;
				
				batchWeight.put(topicId, HotUtil.hackerNews(count, distanceTime,G));
			}
		}
		if(batchWeight.size() >0){
			topicService.addWeightCount(batchWeight);
		}
	}
	
	/**
	 * 批量消费（调用不删除唯一标记API）
	 * 
	 * 来自 https://github.com/google/guava/blob/master/guava/src/com/google/common/collect/Queues.java
	 * @param q
	 * @param buffer
	 * @param numElements
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	@GwtIncompatible // BlockingQueue
	@SuppressWarnings("GoodTime") // should accept a java.time.Duration
	private <E> int drain(UniqueBlockingQueue<E> q,Collection<? super E> buffer,int numElements,long timeout,TimeUnit unit)
		      throws InterruptedException {
		Preconditions.checkNotNull(buffer);
	    /*
	     * This code performs one System.nanoTime() more than necessary, and in return, the time to
	     * execute Queue#drainTo is not added *on top* of waiting for the timeout (which could make
	     * the timeout arbitrarily inaccurate, given a queue that is slow to drain).
	     */
		long deadline = System.nanoTime() + unit.toNanos(timeout);
		int added = 0;
		while (threadActivity && added < numElements) {
			// we could rely solely on #poll, but #drainTo might be more efficient when there are multiple
		    // elements already available (e.g. LinkedBlockingQueue#drainTo locks only once)
		    added += q.drainTo2(buffer, numElements - added);
		    if (added < numElements) { // not enough elements immediately available; will have to poll
		        E e = q.poll2(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
		        if (e == null) {
		        	break; // we already waited enough, and there are no more elements in sight
		        }
		        buffer.add(e);
		        added++;
		    }
		}
		return added;
	}
	
	/**
	 * 初始化数据
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		//TimeUnit.DAYS         日的工具类  
		//TimeUnit.HOURS        时的工具类  
		//TimeUnit.MINUTES      分的工具类  
		//TimeUnit.SECONDS      秒的工具类  
		//TimeUnit.MILLISECONDS 毫秒的工具类 
		
		pool.execute(new Runnable(){   
            public void run() {  
            	while (threadActivity) { //如果系统关闭，则不再运行
                    try {  
                    	List<Long> data = new ArrayList<Long>();
                    	
                    	//每次到1000条数据才进行入库，或者等待1分钟，没达到1000条也继续入库
                    	drain(uniqueQueue, data, 1000, 10, TimeUnit.MINUTES);//第三个参数：数量; 第四个参数：时间; 第五个参数：时间单位
                	    
                    	for(Long l: data){
                    		uniqueQueue.deleteUnique(l);//删除唯一标记
                    	}
                    	
                	    saveHotTopic(data);
                	    
                    } catch (InterruptedException e) {  
                    //    e.printStackTrace();  
                        if (logger.isErrorEnabled()) {
				            logger.error("热门话题消费队列中断异常",e);
				        }
                    } catch (Exception e) {  
                    //    e.printStackTrace();  
                        if (logger.isErrorEnabled()) {
				            logger.error("热门话题消费队列错误",e);
				        }
                    }
                }
            }});
	}
	
	
	@PreDestroy
	public void destroy() {
		threadActivity = false;
		pool.shutdownNow();
		
	}
}
