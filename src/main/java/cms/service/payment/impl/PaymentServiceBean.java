package cms.service.payment.impl;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cms.bean.QueryResult;
import cms.bean.payment.OnlinePaymentInterface;
import cms.bean.payment.PaymentLog;
import cms.bean.payment.PaymentVerificationLog;
import cms.service.besa.DaoSupport;
import cms.web.action.payment.PaymentLogConfig;

import cms.service.payment.PaymentService;
import net.sf.cglib.beans.BeanCopier;

/**
 * 支付管理实现类
 *
 */
@Service
@Transactional
public class PaymentServiceBean extends DaoSupport<OnlinePaymentInterface> implements PaymentService {
	private static final Logger logger = LogManager.getLogger(PaymentServiceBean.class);
	
	@Resource PaymentLogConfig paymentLogConfig;
	
	
	/**
	 * 根据Id查询在线支付接口
	 * @param onlinePaymentInterfaceId 在线支付接口Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public OnlinePaymentInterface findOnlinePaymentInterfaceById(Integer onlinePaymentInterfaceId){
		Query query =  em.createQuery("select o from OnlinePaymentInterface o where o.id=?1");
		//给SQL语句设置参数
		query.setParameter(1, onlinePaymentInterfaceId);
		List<OnlinePaymentInterface> onlinePaymentInterfaceList= query.getResultList();
		if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
			for(OnlinePaymentInterface o: onlinePaymentInterfaceList){
				return o;
			}
		}
		return null;
	}
	
	/**
	 * 查询所有有效的在线支付接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<OnlinePaymentInterface> findAllEffectiveOnlinePaymentInterface(){
		Query query =  em.createQuery("select o from OnlinePaymentInterface o where o.enable=?1 ORDER BY o.sort desc");
		
		//给SQL语句设置参数
		query.setParameter(1, true);
		return query.getResultList();
	}
	/**
	 * 查询所有有效的在线支付接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="paymentServiceBean_cache",key="'findAllEffectiveOnlinePaymentInterface_default'")
	public List<OnlinePaymentInterface> findAllEffectiveOnlinePaymentInterface_cache(){
		return this.findAllEffectiveOnlinePaymentInterface();
	}
	
	/**
	 * 查询所有在线支付接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<OnlinePaymentInterface> findAllOnlinePaymentInterface(){
		Query query =  em.createQuery("select o from OnlinePaymentInterface o ");
		return query.getResultList();
	}
	/**
	 * 保存在线支付接口
	 * @param onlinePaymentInterface 在线支付接口
	 */
	@CacheEvict(value="paymentServiceBean_cache",allEntries=true)
	public void saveOnlinePaymentInterface(OnlinePaymentInterface onlinePaymentInterface){
		this.save(onlinePaymentInterface);
	}
	
	/**
	 * 修改在线支付接口
	 * @param onlinePaymentInterface 在线支付接口
	 */
	@CacheEvict(value="paymentServiceBean_cache",allEntries=true)
	public void updateOnlinePaymentInterface(OnlinePaymentInterface onlinePaymentInterface){
		this.update(onlinePaymentInterface);
	}
	
	
	
	/**
	 * 删除在线支付接口
	 * @param onlinePaymentInterfaceId 在线支付接口Id
	 */
	@CacheEvict(value="paymentServiceBean_cache",allEntries=true)
	public Integer deleteOnlinePaymentInterface(Integer onlinePaymentInterfaceId){
		Query delete = em.createQuery("delete from OnlinePaymentInterface o where o.id=?1")
		.setParameter(1, onlinePaymentInterfaceId);
		int i = delete.executeUpdate();
		return i;
	}
	
	/** --------------------------------- 支付校验日志 -------------------------------------- **/
	
	
	/**
	 * 根据Id查询支付校验日志
	 * @param paymentVerificationLogId 支付流水号
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public PaymentVerificationLog findPaymentVerificationLogById(String paymentVerificationLogId){
		Query query =  em.createQuery("select o from PaymentVerificationLog o where o.id=?1");
		
		//给SQL语句设置参数
		query.setParameter(1, paymentVerificationLogId);
		List<PaymentVerificationLog> paymentVerificationLogList = query.getResultList();
		if(paymentVerificationLogList != null && paymentVerificationLogList.size() > 0){
			for(PaymentVerificationLog paymentVerificationLog : paymentVerificationLogList){
				return paymentVerificationLog;
			}
		}
		return null;
	}
	
	/**
	 * 保存支付校验日志
	 * @param paymentVerificationLog 支付校验日志
	 */
	public void savePaymentVerificationLog(PaymentVerificationLog paymentVerificationLog){
		this.save(paymentVerificationLog);
	}
	
	/**
	 * 根据Id删除支付校验日志
	 * @param paymentVerificationLogId 支付流水号
	 */
	public Integer deletePaymentVerificationLogById(String paymentVerificationLogId){
		Query delete = em.createQuery("delete from PaymentVerificationLog o where o.id=?1")
			.setParameter(1, paymentVerificationLogId);
			int i = delete.executeUpdate();
			return i;
	}
	
	
	/** --------------------------------- 支付日志 -------------------------------------- **/
	/**
	 * 保存支付日志
	 * 先由paymentManage.createPaymentLogObject();方法生成对象再保存
	 * @param paymentLog 支付日志
	 */
	public void savePaymentLog(Object paymentLog){
		this.save(paymentLog);	
	}
	
	/**
	 * 根据支付流水号查询支付日志
	 * @param paymentRunningNumber 支付流水号
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public PaymentLog findPaymentLogByPaymentRunningNumber(String paymentRunningNumber){
		Query query  = null;
		//表编号
		int tableNumber = paymentLogConfig.runningNumberRemainder(paymentRunningNumber);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from PaymentLog o where o.paymentRunningNumber=?1")
			.setParameter(1, paymentRunningNumber);
			
			List<PaymentLog> paymentLogList= query.getResultList();
			if(paymentLogList != null && paymentLogList.size() >0){
				for(PaymentLog paymentLog : paymentLogList){
					return paymentLog;
				}
			}
		}else{//带下划线对象
			query = em.createQuery("select o from PaymentLog_"+tableNumber+" o where o.paymentRunningNumber=?1")
			.setParameter(1, paymentRunningNumber);
			
			List<?> paymentLog_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.payment.PaymentLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),PaymentLog.class, false); 
				
				for(int j = 0;j< paymentLog_List.size(); j++) {  
					Object obj = paymentLog_List.get(j);
					PaymentLog paymentLog = new PaymentLog();
					copier.copy(obj,paymentLog, null);
					return paymentLog;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据支付流水号查询支付日志",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据支付流水号查询支付日志",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据支付流水号查询支付日志",e);
		        }
			}
		}
		
		return null;
	}
	
	/**
	 * 支付日志分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<PaymentLog> findPaymentLogPage(Long userId,String userName,int firstIndex, int maxResult){
		
		QueryResult<PaymentLog> qr = new QueryResult<PaymentLog>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = paymentLogConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from PaymentLog o where o.userName=?1 ORDER BY o.times desc")
			.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<PaymentLog> paymentLogList= query.getResultList();
			qr.setResultlist(paymentLogList);
			
			query = em.createQuery("select count(o) from PaymentLog o where o.userName=?1")
					.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from PaymentLog_"+tableNumber+" o where o.userName=?1 ORDER BY o.times desc")
			.setParameter(1, userName);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> paymentLog_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.payment.PaymentLog_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),PaymentLog.class, false); 
				List<PaymentLog> paymentLogList= new ArrayList<PaymentLog>();
				for(int j = 0;j< paymentLog_List.size(); j++) {  
					Object obj = paymentLog_List.get(j);
					PaymentLog paymentLog = new PaymentLog();
					copier.copy(obj,paymentLog, null);
					paymentLogList.add(paymentLog);
				}
				qr.setResultlist(paymentLogList);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("支付日志分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("支付日志分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("支付日志分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from PaymentLog_"+tableNumber+" o where o.userName=?1")
					.setParameter(1, userName);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
	}
}
