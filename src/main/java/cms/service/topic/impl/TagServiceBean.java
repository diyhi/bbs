package cms.service.topic.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.topic.Tag;
import cms.service.besa.DaoSupport;
import cms.service.topic.TagService;

/**
 * 标签
 *
 */
@Service
@Transactional
public class TagServiceBean extends DaoSupport<Tag> implements TagService{
	/**
	 * 根据Id查询标签
	 * @param tagId 标签Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Tag findById(Long tagId){
		Query query = em.createQuery("select o from Tag o where o.id=?1")
		.setParameter(1, tagId);
		List<Tag> list = query.getResultList();
		for(Tag p : list){
			return p;
		}
		return null;
	}
	/**
	 * 查询所有标签
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Tag> findAllTag(){
		Query query = em.createQuery("select o from Tag o order by o.sort desc");
		return query.getResultList();
	}
	
	/**
	 * 查询所有标签 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="findAllTag_cache",key="'findAllTag_default'")
	public List<Tag> findAllTag_cache(){
		return this.findAllTag();
	}

	
	/**
	 * 保存标签
	 * @param topic
	 */
	@CacheEvict(value="findAllTag_cache",allEntries=true)
	public void saveTag(Tag tag){
		this.save(tag);
	}
	

	/**
	 * 修改标签
	 * @param tag
	 * @return
	 */
	@CacheEvict(value="findAllTag_cache",allEntries=true)
	public Integer updateTag(Tag tag){
		Query query = em.createQuery("update Tag o set o.name=?1, o.sort=?2,o.image=?3 where o.id=?4")
		.setParameter(1, tag.getName())
		.setParameter(2, tag.getSort())
		.setParameter(3, tag.getImage())
		.setParameter(4, tag.getId());
		int i = query.executeUpdate();
		return i;
	}
	
	/**
	 * 删除标签
	 * @param tagId 标签Id
	 */
	@CacheEvict(value="findAllTag_cache",allEntries=true)
	public Integer deleteTag(Long tagId){
		int i = 0;
		Query delete = em.createQuery("delete from Tag o where o.id=?1")
		.setParameter(1,tagId);
		i = delete.executeUpdate();
		return i;
	}
	
}
