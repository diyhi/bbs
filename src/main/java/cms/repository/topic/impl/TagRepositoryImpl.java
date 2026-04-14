package cms.repository.topic.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import cms.config.BusinessException;
import cms.model.topic.Tag;
import cms.repository.besa.DaoSupport;
import cms.repository.topic.TagRepository;
import jakarta.persistence.Query;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



/**
 * 标签管理接口实现类
 *
 */
@Repository
@Transactional
public class TagRepositoryImpl extends DaoSupport<Tag> implements TagRepository {
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
	 * 根据Id查询标签
	 * @param tagId 标签Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="tagRepositoryImpl_cache",key="'findById_'+#tagId")
	public Tag findById_cache(Long tagId){
		return this.findById(tagId);
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
	@Cacheable(value="tagRepositoryImpl_cache",key="'findAllTag_default'")
	public List<Tag> findAllTag_cache(){
		return this.findAllTag();
	}
	
	/**
	 * 根据标签查询所有父标签
	 * @param tag 标签
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Tag> findAllParentById(Tag tag){
		List<Tag> tagList = new ArrayList<Tag>();
		//查询所有标签
		if(tag.getParentId() >0){
			List<Tag> list = this.findParentById(tag.getParentId(),new ArrayList<Tag>());
			tagList.addAll(list);
		}
		//倒转顺序
		Collections.reverse(tagList);
		return tagList;
	}
	
	/**
	 * 根据ID查询父标签 (递归)
	 * @param parentId 父Id
	 * @param tagList 父标签集合
	 * @return
	*/
	private List<Tag> findParentById(Long parentId,List<Tag> tagList){
		Tag parentTag =this.findById(parentId);
		if(parentTag != null){
			tagList.add(parentTag);
			if(parentTag.getParentId() >0L){
				this.findParentById(parentTag.getParentId(),tagList);
			}
		}
		return tagList;
	}

	
	/**
	 * 保存标签
	 * @param tag 标签
	 */
	@CacheEvict(value="tagRepositoryImpl_cache",allEntries=true)
	public void saveTag(Tag tag){
		this.save(tag);
		if(!tag.getParentId().equals(0L)){//如果不是根节点
			
			//修改父节点叶子节点状态
			Query query = em.createQuery("update Tag o set o.childNodeNumber=o.childNodeNumber+1 where o.id=?1")
			.setParameter(1, tag.getParentId());
			int i = query.executeUpdate();
			if(i==0){
				throw new BusinessException("父节点不存在");
			}
			
			
			//验证上级节点原来是根节点才执行
			Query query2 = em.createQuery("update Tag o set o.childNodeNumber=?1 where o.id=?2 and o.childNodeNumber=?3")
			.setParameter(1, 1)
			.setParameter(2, tag.getParentId())
			.setParameter(3, 1);
			int j = query2.executeUpdate();	
			
			
			if(j >0){
				/**
				//将父节点话题转移到本节点
				Query query3 = em.createQuery("update Topic o set o.tagId=?1,o.tagIdGroup=CONCAT(o.tagIdGroup, o.tagId, ',') where o.tagId=?2")
				.setParameter(1, tag.getId())
				.setParameter(2, tag.getParentId());
				query3.executeUpdate();**/
				
				//将父节点话题转移到本节点
				Query query3 = em.createQuery("update Topic o set o.tagId=?1,o.tagIdGroup=CONCAT(o.tagIdGroup, o.tagId, ',') where o.tagIdGroup like ?2 escape '/' ")
				.setParameter(1, tag.getId())
				.setParameter(2, cms.utils.StringUtil.escapeSQLLike(tag.getParentIdGroup())+"%");
				query3.executeUpdate();
				
			
			}
		}
	}
	

	/**
	 * 修改标签
	 * @param tag
	 * @return
	 */
	@CacheEvict(value="tagRepositoryImpl_cache",allEntries=true)
	public Integer updateTag(Tag tag){
		Query query = em.createQuery("update Tag o set o.name=?1, o.sort=?2,o.image=?3,o.multiLanguageExtension=?4 where o.id=?5")
		.setParameter(1, tag.getName())
		.setParameter(2, tag.getSort())
		.setParameter(3, tag.getImage())
		.setParameter(4, tag.getMultiLanguageExtension())
		.setParameter(5, tag.getId());
		int i = query.executeUpdate();
		return i;
	}
	
	/**
	 * 删除标签
	 * @param tag 标签
	 */
	@CacheEvict(value="tagRepositoryImpl_cache",allEntries=true)
	public Integer deleteTag(Tag tag){
		int i = 0;
		Query delete = em.createQuery("delete from Tag o where o.id=?1")
		.setParameter(1,tag.getId());
		i = delete.executeUpdate();
		if(i>0){
			
			if(tag.getParentId() >0L){
				//将父类计数减一
				Query query = em.createQuery("update Tag o set o.childNodeNumber=o.childNodeNumber-1 where o.id=?1")
				.setParameter(1, tag.getParentId());
				query.executeUpdate();
			}
		}
		return i;
	}
	
}
