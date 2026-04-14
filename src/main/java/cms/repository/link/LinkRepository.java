package cms.repository.link;

import cms.model.links.Links;
import cms.repository.besa.DAO;

import java.util.List;


/**
 * 友情链接管理接口
 *
 */
public interface LinkRepository extends DAO<Links> {
	/**
	 * 根据Id查询友情链接
	 * @param linkId 友情链接Id
	 * @return
	 */
	public Links findById(Integer linkId);
	/**
	 * 查询所有友情链接
	 * @return
	 */
	public List<Links> findAllLinks();
	
	/**
	 * 查询所有友情链接 - 缓存
	 * @return
	 */
	public List<Links> findAllLinks_cache();

	
	/**
	 * 保存友情链接
	 * @param links 友情链接
	 */
	public void saveLinks(Links links);
	

	/**
	 * 修改友情链接
	 * @param links 友情链接
	 * @return
	 */
	public Integer updateLinks(Links links);
	/**
	 * 删除友情链接
	 * @param linkId 友情链接Id
	 */
	public Integer deleteLinks(Integer linkId);
}
