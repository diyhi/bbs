package cms.service.topic;

import java.util.List;

import cms.bean.topic.Tag;
import cms.service.besa.DAO;

/**
 * 标签
 *
 */
public interface TagService extends DAO<Tag>{
	/**
	 * 根据Id查询标签
	 * @param tagId 标签Id
	 * @return
	 */
	public Tag findById(Long tagId);
	/**
	 * 查询所有标签
	 * @return
	 */
	public List<Tag> findAllTag();
	/**
	 * 查询所有标签 - 缓存
	 * @return
	 */
	public List<Tag> findAllTag_cache();
	/**
	 * 保存标签
	 * @param tag
	 */
	public void saveTag(Tag tag);
	/**
	 * 修改标签
	 * @param tag
	 * @return
	 */
	public Integer updateTag(Tag tag);
	/**
	 * 删除标签
	 * @param tagId 标签Id
	 */
	public Integer deleteTag(Long tagId);
}
