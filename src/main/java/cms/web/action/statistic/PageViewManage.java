package cms.web.action.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;


import com.google.common.collect.Queues;

import cms.bean.statistic.AccessInfo;
import cms.bean.statistic.PV;
import cms.service.statistic.PageViewService;
import cms.utils.IpAddress;
import cms.utils.UUIDUtil;
import cms.utils.UserAgentAnalysis;
import cms.web.taglib.Configuration;

/**
 * 访问量管理
 *
 */
@Component("pageViewManage")
public class PageViewManage implements InitializingBean{
	private static final Logger logger = LogManager.getLogger(PageViewManage.class);
	
	@Resource PageViewService pageViewService;
	
	//创建一个可重用固定线程数的线程池  
	private ExecutorService pool = Executors.newFixedThreadPool(1);  
    
	private static final BlockingQueue<PV> blockingQueue = new ArrayBlockingQueue<PV>(1000000);
	
	//线程活动
	private volatile boolean threadActivity = true;
	
	/**
	 * 添加访问量
	 * @param request
	 * @param url
	 * @param referrer
	 */
	public void addPV(HttpServletRequest request,String url,String referrer){
		PV pv = new PV();
		pv.setId(UUIDUtil.getUUID32());
		pv.setIp(IpAddress.getClientIpAddress(request));
		pv.setReferrer(referrer == null ? "" :referrer);
		pv.setUrl(url);
		
		AccessInfo accessInfo = UserAgentAnalysis.analysis(request.getHeader("User-Agent"));
		if(accessInfo != null){
			if(accessInfo.getBrowserName() != null && accessInfo.getBrowserName().length() <90 && accessInfo.getBrowserVersion() != null && accessInfo.getBrowserVersion().length() <90){
				pv.setBrowserName(accessInfo.getBrowserName()+" "+accessInfo.getBrowserVersion());
			}
			if(accessInfo.getOperatingSystem() != null && accessInfo.getOperatingSystem().length() <90){
				pv.setOperatingSystem(accessInfo.getOperatingSystem());
			}
			pv.setDeviceType(accessInfo.getDeviceType());
		}
		
		//add(anObject):添加元素到队列里，添加成功返回true，容量满了添加失败会抛出IllegalStateException异常
		//offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.（本方法不阻塞当前执行方法的线程）
		//offer(E o, long timeout, TimeUnit unit),可以设定等待的时间，如果在指定的时间内，还不能往队列中加入BlockingQueue，则返回失败。
		//put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断直到BlockingQueue里面有空间再继续.
		blockingQueue.offer(pv);//添加一个元素并返回true 如果队列已满，则返回false
	}
	
	/**
	 * 添加访问量
	 * @param pv 页面访问量
	 * @return
	 */
	public void addPV(HttpServletRequest request){
		
		PV pv = new PV();
		pv.setId(UUIDUtil.getUUID32());
		pv.setIp(IpAddress.getClientIpAddress(request));
		pv.setReferrer(request.getHeader("Referer"));
		pv.setUrl(Configuration.getUrl(request)+Configuration.baseURI(request.getRequestURI(), request.getContextPath())+(request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :""));
		
		AccessInfo accessInfo = UserAgentAnalysis.analysis(request.getHeader("User-Agent"));
		if(accessInfo != null){
			if(accessInfo.getBrowserName() != null && accessInfo.getBrowserName().length() <90 && accessInfo.getBrowserVersion() != null && accessInfo.getBrowserVersion().length() <90){
				pv.setBrowserName(accessInfo.getBrowserName()+" "+accessInfo.getBrowserVersion());
			}
			if(accessInfo.getOperatingSystem() != null && accessInfo.getOperatingSystem().length() <90){
				pv.setOperatingSystem(accessInfo.getOperatingSystem());
			}
			pv.setDeviceType(accessInfo.getDeviceType());
		}
		
		//add(anObject):添加元素到队列里，添加成功返回true，容量满了添加失败会抛出IllegalStateException异常
		//offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.（本方法不阻塞当前执行方法的线程）
		//offer(E o, long timeout, TimeUnit unit),可以设定等待的时间，如果在指定的时间内，还不能往队列中加入BlockingQueue，则返回失败。
		//put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断直到BlockingQueue里面有空间再继续.
		blockingQueue.offer(pv);//添加一个元素并返回true 如果队列已满，则返回false
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
                    	List<PV> data = new ArrayList<PV>();
                    	
                    	//每次到1000条数据才进行入库，或者等待1分钟，没达到1000条也继续入库
                	    Queues.drain(blockingQueue, data, 1000, 1, TimeUnit.MINUTES);//第三个参数：数量; 第四个参数：时间; 第五个参数：时间单位
                	    pageViewService.savePageView(data);
 
                    } catch (InterruptedException e) {  
                    //    e.printStackTrace();  
                        if (logger.isErrorEnabled()) {
				            logger.error("访问量消费队列中断异常",e);
				        }
                    } catch (Exception e) {  
                    //    e.printStackTrace();  
                        if (logger.isErrorEnabled()) {
				            logger.error("访问量消费队列错误",e);
				        }
                    }
                }
            }});
		
		
		/**
		//启动一个线程消费队列
		new Thread(new Runnable(){   
            public void run() {  
            	while (threadActivity) { //如果系统关闭，则不再运行
            		
            	
                    try {  
                    	List<PV> data = new ArrayList<PV>();
                    	
                    	//每次到1000条数据才进行入库，或者等待1分钟，没达到1000条也继续入库
                	    Queues.drain(blockingQueue, data, 1000, 1, TimeUnit.MINUTES);//第三个参数：数量; 第四个参数：时间; 第五个参数：时间单位
                	    pageViewService.savePageView(data);
 
                    } catch (InterruptedException e) {  
                    //    e.printStackTrace();  
                        if (logger.isErrorEnabled()) {
				            logger.error("访问量消费队列错误",e);
				        }
                    }
                }
            }}).start();  
	**/
		
	}
	
	
	@PreDestroy
	public void destroy() {
		threadActivity = false;
		pool.shutdownNow();
		
	}

	
	//@PostConstruct
//	public void init() {
//	   System.out.println("初始化数据");
//	}
	
}
