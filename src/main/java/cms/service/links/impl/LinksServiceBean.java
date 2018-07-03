package cms.service.links.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.links.Links;
import cms.service.besa.DaoSupport;
import cms.service.links.LinksService;

/**
 * 友情链接
 *
 */
@Service
@Transactional
public class LinksServiceBean extends DaoSupport<Links> implements LinksService{
	/**
	 * 根据Id查询友情链接
	 * @param linkId 友情链接Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Links findById(Integer linkId){
		Query query = em.createQuery("select o from Links o where o.id=?1")
		.setParameter(1, linkId);
		List<Links> list = query.getResultList();
		for(Links p : list){
			return p;
		}
		return null;
	}
	/**
	 * 查询所有友情链接
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Links> findAllLinks(){
		Query query = em.createQuery("select o from Links o order by o.sort desc");
		return query.getResultList();
	}
	
	/**
	 * 查询所有友情链接 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="findAllLinks_cache",key="'findAllLinks_default'")
	public List<Links> findAllLinks_cache(){
		return this.findAllLinks();
	}

	
	/**
	 * 保存友情链接
	 * @param links
	 */
	@CacheEvict(value="findAllLinks_cache",allEntries=true)
	public void saveLinks(Links links){
		this.save(links);
	}
	

	/**
	 * 修改友情链接
	 * @param links
	 * @return
	 */
	@CacheEvict(value="findAllLinks_cache",allEntries=true)
	public Integer updateLinks(Links links){
		Query query = em.createQuery("update Links o set o.name=?1,o.website=?2, o.sort=?3,o.image=?4 where o.id=?5")
			.setParameter(1, links.getName())
			.setParameter(2, links.getWebsite())
			.setParameter(3, links.getSort())
			.setParameter(4, links.getImage())
			.setParameter(5, links.getId());
			int i = query.executeUpdate();
		return i;
	}
	
	/**
	 * 删除友情链接
	 * @param linkId 友情链接Id
	 */
	@CacheEvict(value="findAllLinks_cache",allEntries=true)
	public Integer deleteLinks(Integer linkId){
		int i = 0;
		Query delete = em.createQuery("delete from Links o where o.id=?1")
		.setParameter(1,linkId);
		i = delete.executeUpdate();
		return i;
	}
	
}
