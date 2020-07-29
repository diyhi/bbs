package cms.service.redEnvelope.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.QueryResult;
import cms.bean.payment.PaymentLog;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.service.besa.DaoSupport;
import cms.service.redEnvelope.RedEnvelopeService;
import cms.service.user.UserService;
import cms.web.action.SystemException;
import cms.web.action.payment.PaymentManage;
import cms.web.action.redEnvelope.ReceiveRedEnvelopeConfig;
import net.sf.cglib.beans.BeanCopier;

/**
 * 红包管理实现类
 *
 */
@Service
@Transactional
public class RedEnvelopeServiceBean extends DaoSupport<GiveRedEnvelope> implements RedEnvelopeService {
	private static final Logger logger = LogManager.getLogger(RedEnvelopeServiceBean.class);
	
	@Resource PaymentManage paymentManage;
	@Resource UserService userService;
	@Resource ReceiveRedEnvelopeConfig receiveRedEnvelopeConfig;
	
	/**
	 * 根据Id查询发红包
	 * @param giveRedEnvelopeId
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public GiveRedEnvelope findById(String giveRedEnvelopeId){
		Query query = em.createQuery("select o from GiveRedEnvelope o where o.id=?1")
				.setParameter(1,  giveRedEnvelopeId);
		List<GiveRedEnvelope> giveRedEnvelopeList = query.getResultList();
		if(giveRedEnvelopeList != null && giveRedEnvelopeList.size() >0){
			for(GiveRedEnvelope giveRedEnvelope : giveRedEnvelopeList){
				return giveRedEnvelope;
			}
		}
		return null;
	}
	
	
	/**
	 * 保存发红包
	 * @param giveRedEnvelope 发红包
	 * @param userName 用户名称
	 * @param amount 扣减用户预存款
	 * @param paymentLog 支付日志
	 */
	public void saveGiveRedEnvelope(GiveRedEnvelope giveRedEnvelope,String userName,BigDecimal amount,PaymentLog paymentLog){
		
		if(amount != null && amount.compareTo(new BigDecimal("0")) >0){//余额
			this.save(giveRedEnvelope);
			
			paymentLog.setSourceParameterId(giveRedEnvelope.getId());
			Object paymentLogObject = paymentManage.createPaymentLogObject(paymentLog);
			//扣减用户预存款
			int i = userService.subtractUserDeposit(userName, amount, paymentLogObject);
			if(i ==0){
				throw new SystemException("扣减预存款失败");
			}
		}
	}
	
	/**
	 * 返还红包
	 * @param giveRedEnvelope 发红包
	 * @param userName 用户名称
	 * @param amount 返还用户金额
	 * @param paymentLogObject 支付日志
	 */
	public void refundRedEnvelope(GiveRedEnvelope giveRedEnvelope,String userName,BigDecimal amount,Object paymentLogObject){
		if(amount != null && amount.compareTo(new BigDecimal("0")) >0){//余额
			
			Query query = em.createQuery("update GiveRedEnvelope o set o.refundAmount=?1, o.version=o.version+1 where o.id=?2 and o.version=?3")
					.setParameter(1, amount)
					.setParameter(2, giveRedEnvelope.getId())
					.setParameter(3, giveRedEnvelope.getVersion());
			int i =  query.executeUpdate();
			
			if(i >0){
				
				//扣减用户预存款
				int j = userService.addUserDeposit(userName, amount, paymentLogObject);
				if(j ==0){
					throw new SystemException("增加预存款失败");
				}
			}else{
				throw new SystemException("返还红包失败");
			}
		}
	}
	
	/**
	 * 删除发红包
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteGiveRedEnvelope(List<Long> userIdList){
		int i = 0;
		Query delete = em.createQuery("delete from GiveRedEnvelope o where o.userId in(:userId)")
		.setParameter("userId",userIdList);
		i = delete.executeUpdate();
		return i;
	}
	
	/**
	 * 保存收红包
	 * 先由redEnvelopeManage.createReceiveRedEnvelopeObject();方法生成对象再保存
	 * @param receiveRedEnvelope 收红包
	 * @param giveRedEnvelopeId 发红包Id
	 * @param grabRedEnvelopeUserId 抢到红包的用户Id
	 */
	public int saveReceiveRedEnvelope(Object receiveRedEnvelope,String giveRedEnvelopeId,String grabRedEnvelopeUserId){
		this.save(receiveRedEnvelope);	

		Query query = em.createQuery("update GiveRedEnvelope o set o.grabRedEnvelopeUserIdGroup=CONCAT(o.grabRedEnvelopeUserIdGroup,?1), o.remainingQuantity=o.remainingQuantity-1, o.version=o.version+1 where o.id=?2 and o.remainingQuantity>0")
				.setParameter(1, grabRedEnvelopeUserId)
				.setParameter(2, giveRedEnvelopeId);
		return query.executeUpdate();
	}
	
	/**
	 * 删除收红包
	 * @param userIdList 收红包用户Id集合
	 */
	public Integer deleteReceiveRedEnvelope(List<Long> userIdList){
		int i = 0;
		for(Long userId :userIdList){
			i += this.deleteReceiveRedEnvelope(userId);
		}
		return i;
	}
	
	/**
	 * 删除收红包
	 * @param userId 收红包用户Id
	 */
	public Integer deleteReceiveRedEnvelope(Long userId){
		//表编号
		int tableNumber = receiveRedEnvelopeConfig.userIdRemainder(userId);
		int i = 0;
		if(tableNumber == 0){//默认对象
			Query delete = em.createQuery("delete from ReceiveRedEnvelope o where o.receiveUserId=?1")
			.setParameter(1,userId);
			i += delete.executeUpdate();
		}else{//带下划线对象
			Query delete = em.createQuery("delete from ReceiveRedEnvelope_"+tableNumber+" o where o.receiveUserId=?1")
					.setParameter(1,userId);
			i += delete.executeUpdate();
		}
		return i;
	}
	
	/**
    * 拆红包
    * @param receiveRedEnvelope 收红包Id
    * @param amount 红包金额
    * @param version 版本号
    * @param userName 拆红包的用户名称
    * @param paymentLogObject 支付日志
    */
    public Integer unwrapRedEnvelope(String receiveRedEnvelopeId,BigDecimal amount,Integer version,String userName,Object paymentLogObject){
    	//表编号
		int tableNumber = receiveRedEnvelopeConfig.receiveRedEnvelopeIdRemainder(receiveRedEnvelopeId);
		int i = 0;
		if(tableNumber == 0){//默认对象
			Query query = em.createQuery("update ReceiveRedEnvelope o set o.amount=?1, o.version=o.version+1 where o.id=?2 and o.version=?3")
					.setParameter(1, amount)
					.setParameter(2, receiveRedEnvelopeId)
					.setParameter(3, version);
				i=query.executeUpdate();
		}else{//带下划线对象
			Query query = em.createQuery("update ReceiveRedEnvelope_"+tableNumber+" o set o.amount=?1, o.version=o.version+1 where o.id=?2 and o.version=?3")
					.setParameter(1, amount)
					.setParameter(2, receiveRedEnvelopeId)
					.setParameter(3, version);
				i=query.executeUpdate();
			
		}
		int j = 0;
    	if(i >0){
			//增加用户预存款
			j = userService.addUserDeposit(userName, amount, paymentLogObject);
    	}
    	return j;
    }
	
	
	/**
	 * 根据Id查询收红包
	 * @param receiveRedEnvelopeId 收红包Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public ReceiveRedEnvelope findByReceiveRedEnvelopeId(String receiveRedEnvelopeId){
		Query query  = null;
		//表编号
		int tableNumber = receiveRedEnvelopeConfig.receiveRedEnvelopeIdRemainder(receiveRedEnvelopeId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from ReceiveRedEnvelope o where o.id=?1")
			.setParameter(1, receiveRedEnvelopeId);
			
			List<ReceiveRedEnvelope> receiveRedEnvelopeList= query.getResultList();
			if(receiveRedEnvelopeList != null && receiveRedEnvelopeList.size() >0){
				for(ReceiveRedEnvelope receiveRedEnvelope : receiveRedEnvelopeList){
					return receiveRedEnvelope;
				}
			}
		}else{//带下划线对象
			query = em.createQuery("select o from ReceiveRedEnvelope_"+tableNumber+" o where o.id=?1")
			.setParameter(1, receiveRedEnvelopeId);
			
			List<?> receiveRedEnvelope_List= query.getResultList();
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.redEnvelope.ReceiveRedEnvelope_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),ReceiveRedEnvelope.class, false); 
				for(int j = 0;j< receiveRedEnvelope_List.size(); j++) {  
					Object obj = receiveRedEnvelope_List.get(j);
					ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
					copier.copy(obj,receiveRedEnvelope, null);
					return receiveRedEnvelope;
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询收红包",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询收红包",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据Id查询收红包",e);
		        }
			}
		}
		
		return null;
	}

	
	/**
	 * 根据用户Id查询收红包分页
	 * @param userId 用户Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public QueryResult<ReceiveRedEnvelope> findReceiveRedEnvelopeByUserId(Long userId,int firstIndex, int maxResult){
		QueryResult<ReceiveRedEnvelope> qr = new QueryResult<ReceiveRedEnvelope>();
		Query query  = null;
		
		
		//表编号
		int tableNumber = receiveRedEnvelopeConfig.userIdRemainder(userId);
		if(tableNumber == 0){//默认对象为HistoryOrder
			query = em.createQuery("select o from ReceiveRedEnvelope o where o.receiveUserId=?1 ORDER BY o.receiveTime desc")
			.setParameter(1, userId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<ReceiveRedEnvelope> list= query.getResultList();
			qr.setResultlist(list);
			
			query = em.createQuery("select count(o) from ReceiveRedEnvelope o where o.receiveUserId=?1")
					.setParameter(1, userId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}else{//带下划线对象
			query = em.createQuery("select o from ReceiveRedEnvelope_"+tableNumber+" o where o.receiveUserId=?1 ORDER BY o.receiveTime desc")
			.setParameter(1, userId);
			//索引开始,即从哪条记录开始
			query.setFirstResult(firstIndex);
			//获取多少条数据
			query.setMaxResults(maxResult);
			List<?> receiveRedEnvelope_List= query.getResultList();
			
			
			
			try {
				//带下划线对象
				Class<?> c = Class.forName("cms.bean.redEnvelope.ReceiveRedEnvelope_"+tableNumber);
				Object object  = c.newInstance();
				BeanCopier copier = BeanCopier.create(object.getClass(),ReceiveRedEnvelope.class, false); 
				List<ReceiveRedEnvelope> list= new ArrayList<ReceiveRedEnvelope>();
				for(int j = 0;j< receiveRedEnvelope_List.size(); j++) {  
					Object obj = receiveRedEnvelope_List.get(j);
					ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
					copier.copy(obj,receiveRedEnvelope, null);
					list.add(receiveRedEnvelope);
				}
				qr.setResultlist(list);
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询收红包分页",e);
		        }
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询收红包分页",e);
		        }
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("根据用户Id查询收红包分页",e);
		        }
			}
			
			query = em.createQuery("select count(o) from ReceiveRedEnvelope_"+tableNumber+" o where o.receiveUserId=?1")
					.setParameter(1, userId);
			qr.setTotalrecord((Long)query.getSingleResult());
		}
		
		return qr;
		
		
	}
}
