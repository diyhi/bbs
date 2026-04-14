package cms.repository.help;

import cms.model.help.HelpType;
import cms.repository.besa.DAO;

import java.util.List;



/**
 * 帮助分类管理接口
 *
 */
public interface HelpTypeRepository extends DAO<HelpType> {
	/**
	 * 根据Id查询帮助分类
	 * @param helpTypeId 帮助分类Id
	 * @return
	 */
	public HelpType findById(Long helpTypeId);
	/**
	 * 根据Id查询帮助分类
	 * @param helpTypeIdList 帮助分类Id集合
	 * @return
	 */
	public List<HelpType> findByIdList(List<Long> helpTypeIdList);
	/**
	 * 查询所有帮助分类
	 * @return
	 */
	public List<HelpType> findAllHelpType();
	
	/**
	 * 根据Id组查询子分类
	 * @param idGroup 帮助分类Id组
	 * @return
	 */
	public List<HelpType> findChildHelpTypeByIdGroup(String idGroup);
	
	/**
	 * 增加帮助数量
	 * @param helpTypeId 帮助分类Id
	 */
	public Integer addHelpQuantity(Long helpTypeId);
	/**
	 * 减少帮助数量
	 * @param helpTypeId 帮助分类Id
	 * @param helpQuantity 帮助数量(显示) 
	 */
	public Integer minusHelpQuantity(Long helpTypeId,Long helpQuantity);
	/**
	 * 标记删除增加帮助数量
	 * @param helpTypeId 帮助分类Id
	 */
	public Integer mark_addHelpQuantity(Long helpTypeId);
	/**
	 * 标记删除减少帮助数量
	 * @param helpTypeId 帮助分类Id
	 */
	public Integer mark_minusHelpQuantity(Long helpTypeId);

	/**
	 * 保存帮助分类
	 * @param helpType 帮助分类
	 */
	public void saveType(HelpType helpType);
	
	/**
	 * 修改帮助分类
	 * @param helpType 分类
	 */
	public Integer updateHelpType(HelpType helpType);
	
	/**
	 * 删除帮助分类
	 * @param helpType 帮助分类
	 */
	public Integer deleteHelpType(HelpType helpType);
	
	/**
	 * 根据分类查询所有父类帮助分类
	 * @param helpType 分类
	 * @return
	 */
	public List<HelpType> findAllParentById(HelpType helpType);

    /**
     * 合并分类
     * @param helpTypeId 原分类Id
     * @param mergerHelpType 待合并分类
     * @return
     */
	public Integer mergerHelpType(Long helpTypeId,HelpType mergerHelpType);
}
