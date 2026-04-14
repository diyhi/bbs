package cms.repository.topic;

import cms.model.topic.Tag;
import cms.repository.besa.DAO;

import java.util.List;


/**
 * 标签管理接口
 *
 */
public interface TagRepository extends DAO<Tag> {
	/**
	 * 根据Id查询标签
	 * @param tagId 标签Id
	 * @return
	 */
	public Tag findById(Long tagId);
	/**
	 * 根据Id查询标签
	 * @param tagId 标签Id
	 * @return
	 */
	public Tag findById_cache(Long tagId);
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
	 * 根据标签查询所有父标签
	 * @param tag 标签
	 * @return
	 */
	public List<Tag> findAllParentById(Tag tag);
	/**
	 * 保存标签
	 * @param tag 标签
	 */
	public void saveTag(Tag tag);
	/**
	 * 修改标签
	 * @param tag 标签
	 * @return
	 */
	public Integer updateTag(Tag tag);
	/**
	 * 删除标签
	 * @param tag 标签
	 */
	public Integer deleteTag(Tag tag);
}
