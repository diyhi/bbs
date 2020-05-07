package cms.service.thirdParty.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cms.bean.thirdParty.ThirdPartyLoginInterface;
import cms.service.besa.DaoSupport;

import cms.service.thirdParty.ThirdPartyLoginService;


/**
 * 第三方登录管理实现类
 *
 */
@Service
@Transactional
public class ThirdPartyLoginServiceBean extends DaoSupport<ThirdPartyLoginInterface> implements ThirdPartyLoginService {
	
	
	/**
	 * 根据Id查询第三方登录接口
	 * @param thirdPartyLoginInterfaceId 第三方登录接口Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public ThirdPartyLoginInterface findThirdPartyLoginInterfaceById(Integer thirdPartyLoginInterfaceId){
		Query query =  em.createQuery("select o from ThirdPartyLoginInterface o where o.id=?1");
		//给SQL语句设置参数
		query.setParameter(1, thirdPartyLoginInterfaceId);
		List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList= query.getResultList();
		if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){
			for(ThirdPartyLoginInterface o: thirdPartyLoginInterfaceList){
				return o;
			}
		}
		return null;
	}
	
	/**
	 * 查询所有有效的第三方登录接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<ThirdPartyLoginInterface> findAllValidThirdPartyLoginInterface(){
		Query query =  em.createQuery("select o from ThirdPartyLoginInterface o where o.enable=?1 ORDER BY o.sort desc");
		
		//给SQL语句设置参数
		query.setParameter(1, true);
		return query.getResultList();
	}
	/**
	 * 查询所有有效的第三方登录接口(缓存)
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="thirdPartyLoginServiceBean_cache",key="'findAllValidThirdPartyLoginInterface_default'")
	public List<ThirdPartyLoginInterface> findAllValidThirdPartyLoginInterface_cache(){
		return this.findAllValidThirdPartyLoginInterface();
	}
	
	/**
	 * 查询所有第三方登录接口
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<ThirdPartyLoginInterface> findAllThirdPartyLoginInterface(){
		Query query =  em.createQuery("select o from ThirdPartyLoginInterface o ");
		return query.getResultList();
	}
	/**
	 * 保存第三方登录接口
	 * @param thirdPartyLoginInterface 第三方登录接口
	 */
	@CacheEvict(value="thirdPartyLoginServiceBean_cache",allEntries=true)
	public void saveThirdPartyLoginInterface(ThirdPartyLoginInterface thirdPartyLoginInterface){
		this.save(thirdPartyLoginInterface);
	}
	
	/**
	 * 修改第三方登录接口
	 * @param thirdPartyLoginInterface 第三方登录接口
	 */
	@CacheEvict(value="thirdPartyLoginServiceBean_cache",allEntries=true)
	public void updateThirdPartyLoginInterface(ThirdPartyLoginInterface thirdPartyLoginInterface){
		this.update(thirdPartyLoginInterface);
	}
	
	
	
	/**
	 * 删除第三方登录接口
	 * @param thirdPartyLoginInterfaceId 第三方登录接口Id
	 */
	@CacheEvict(value="thirdPartyLoginServiceBean_cache",allEntries=true)
	public Integer deleteThirdPartyLoginInterface(Integer thirdPartyLoginInterfaceId){
		Query delete = em.createQuery("delete from ThirdPartyLoginInterface o where o.id=?1")
		.setParameter(1, thirdPartyLoginInterfaceId);
		int i = delete.executeUpdate();
		return i;
	}
	

	
}
