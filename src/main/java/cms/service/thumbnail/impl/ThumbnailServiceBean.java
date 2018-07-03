package cms.service.thumbnail.impl;

import java.util.List;

import javax.persistence.Query;

import cms.bean.thumbnail.Thumbnail;
import cms.service.besa.DaoSupport;
import cms.service.thumbnail.ThumbnailService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缩略图管理
 *
 */

@Service
@Transactional
public class ThumbnailServiceBean extends DaoSupport<Thumbnail> implements ThumbnailService {
	
	/**
	 * 查询所有缩略图
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Thumbnail> findAllThumbnail(){
		Query query =  em.createQuery("select o from Thumbnail o ");
		return query.getResultList();
	}
	/**
	 * 查询所有缩略图 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="thumbnailServiceBean_cache",key="'default'")
	public List<Thumbnail> findAllThumbnail_cache(){
		return this.findAllThumbnail();
	}
	/**
	 * 根据缩略图Id查询缩略图
	 * @param thumbnailId
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Thumbnail findByThumbnailId(Integer thumbnailId){
		Query query =  em.createQuery("select o from Thumbnail o where o.id=?1");
		query.setParameter(1, thumbnailId);
		List<Thumbnail> thumbnailList = query.getResultList();
		if(thumbnailList != null && thumbnailList.size() >0){
			for(Thumbnail thumbnail : thumbnailList){
				return thumbnail;
			}
		}
		return null;
	}
	/**
	 * 根据规格组查询缩略图
	 * @param specificationGroup 规格组
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Thumbnail findThumbnailBySpecificationGroup(String specificationGroup){
		Query query =  em.createQuery("select o from Thumbnail o where o.specificationGroup=?1");
		query.setParameter(1, specificationGroup);
		List<Thumbnail> thumbnailList = query.getResultList();
		if(thumbnailList != null && thumbnailList.size() >0){
			for(Thumbnail thumbnail : thumbnailList){
				return thumbnail;
			}
		}
		return null;
	}
	
	/**
	 * 保存缩略图
	 * @param thumbnail
	 */
	@CacheEvict(value="thumbnailServiceBean_cache",allEntries=true)
	public void saveThumbnail(Thumbnail thumbnail){
		this.save(thumbnail);
	}
	/**
	 * 删除缩略图
	 * @param thumbnailId 缩略图Id
	 */
	@CacheEvict(value="thumbnailServiceBean_cache",allEntries=true)
	public int deleteThumbnail(Integer thumbnailId){	
		Query delete = em.createQuery("delete from Thumbnail o where o.id=?1")
				.setParameter(1, thumbnailId);
		return  delete.executeUpdate();
	}
}
