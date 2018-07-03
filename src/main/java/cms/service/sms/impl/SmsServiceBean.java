package cms.service.sms.impl;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.sms.SendSmsLog;
import cms.bean.sms.SmsInterface;
import cms.service.besa.DaoSupport;
import cms.service.sms.SmsService;

/**
 * 短信
 *
 */
@Service
@Transactional
public class SmsServiceBean extends DaoSupport<SmsInterface> implements SmsService {
	private static final Logger logger = LogManager.getLogger(SmsServiceBean.class);
	
	
	/**
	 * 根据Id查询短信接口
	 * @param smsInterfaceId 短信接口Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public SmsInterface findSmsInterfaceById(Integer smsInterfaceId){
		Query query =  em.createQuery("select o from SmsInterface o where o.id=?1");
		//给SQL语句设置参数
		query.setParameter(1, smsInterfaceId);
		List<SmsInterface> smsInterfaceList= query.getResultList();
		if(smsInterfaceList != null && smsInterfaceList.size() >0){
			for(SmsInterface o: smsInterfaceList){
				return o;
			}
		}
		return null;
	}
	/**
	 * 查询所有短信接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<SmsInterface> findAllSmsInterface(){
		Query query =  em.createQuery("select o from SmsInterface o ");
		return query.getResultList();
	}
	/**
	 * 保存短信接口
	 * @param smsInterface 短信接口
	 */
	@CacheEvict(value="smsServiceBean_cache",allEntries=true)
	public void saveSmsInterface(SmsInterface smsInterface){
		this.save(smsInterface);
	}
	
	/**
	 * 修改短信接口
	 * @param smsInterface 短信接口
	 */
	@CacheEvict(value="smsServiceBean_cache",allEntries=true)
	public Integer updateSmsInterface(SmsInterface smsInterface){
		Query query = em.createQuery("update SmsInterface o set o.name=?1,o.dynamicParameter=?2,o.sort=?3,o.sendService=?4,o.version=?5 where o.id=?6")
				.setParameter(1, smsInterface.getName())
				.setParameter(2, smsInterface.getDynamicParameter())
				.setParameter(3, smsInterface.getSort())
				.setParameter(4, smsInterface.getSendService())
				.setParameter(5, smsInterface.getVersion())
				.setParameter(6, smsInterface.getId());
		int i = query.executeUpdate();
				
		return i;
	}
	
	
	/**
	 * 删除短信接口
	 * @param smsInterface 短信Id
	 */
	@CacheEvict(value="smsServiceBean_cache",allEntries=true)
	public Integer deleteSmsInterface(Integer smsInterfaceId){
		Query delete = em.createQuery("delete from SmsInterface o where o.id=?1")
		.setParameter(1, smsInterfaceId);
		int i = delete.executeUpdate();
		return i;
	}
	/**
	 * 修改使用的短信接口
	 * @param smsInterfaceId 短信接口Id
	 * @param version 版本
	 */
	@CacheEvict(value="smsServiceBean_cache",allEntries=true)
	public Integer updateEnableInterface(Integer smsInterfaceId,Integer version){
		Query query = em.createQuery("update SmsInterface o set o.enable=?1,o.version=?2");
		//给SQL语句设置参数
		query.setParameter(1, false);
		query.setParameter(2, version);;
		query.executeUpdate();
		
		
		query = em.createQuery("update SmsInterface o set o.enable=?1,o.version=?2 where o.id=?3");
		//给SQL语句设置参数
		query.setParameter(1, true);
		query.setParameter(2, version);
		query.setParameter(3, smsInterfaceId);
		return query.executeUpdate();
	}
	
	/**
	 * 查询启用的短信接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public SmsInterface findEnableInterface(){
		Query query =  em.createQuery("select o from SmsInterface o where o.enable=?1");
		
		//给SQL语句设置参数
		query.setParameter(1, true);
		List<SmsInterface> smsInterfaceList= query.getResultList();
		if(smsInterfaceList != null && smsInterfaceList.size() >0){
			for(SmsInterface smsInterface : smsInterfaceList){
				return smsInterface;
			}
		}
		return null;
	}
	/**
	 * 查询启用的短信接口 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="smsServiceBean_cache",key="'findEnableInterface_default'")
	public SmsInterface findEnableInterface_cache(){
		return this.findEnableInterface();
	}
	
	/**--------------------------------------------- 发送错误日志 -----------------------------------------------------**/
	
	/**
	 * 保存发送错误日志
	 * @param sendSmsLog 发送错误日志
	 */
	public void saveSendSmsLog(SendSmsLog sendSmsLog){
		this.save(sendSmsLog);
	}
	
	
	
}
