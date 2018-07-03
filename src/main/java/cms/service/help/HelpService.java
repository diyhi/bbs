package cms.service.help;

import java.util.List;

import cms.bean.help.Help;
import cms.service.besa.DAO;

/**
 * 帮助管理
 *
 */
public interface HelpService extends DAO<Help>{
	/**
	 * 根据Id查询帮助
	 * @param helpId 帮助Id
	 * @return
	 */
	public Help findById(Long helpId);
	/**
	 * 根据Id集合查询帮助
	 * @param helpIdList 帮助Id集合
	 * @return
	 */
	public List<Help> findByIdList(List<Long> helpIdList);
	/**
	 * 根据分类Id查询帮助
	 * @param helpTypeId 帮助分类Id
	 * @return
	 */
	public List<Help> findByTypeId(Long helpTypeId);
	/**
	 * 保存帮助
	 * @param help
	 */
	public Integer saveHelp(Help help);
	/**
	 * 修改帮助
	 * @param help
	 * @return
	 */
	public Integer updateHelp(Help help);
	/**
	 * 修改帮助
	 * @param helpList 帮助集合
	 * @param new_helpTypeId 新帮助分类Id
	 * @return
	 */
	public Integer updateHelp(List<Help> helpList,Long new_helpTypeId);

	
	/**
	 * 还原帮助
	 * @param helpList 帮助集合
	 * @return
	 */
	public Integer reductionHelp(List<Help> helpList);
	
	/**
	 * 标记删除帮助
	 * @param helpTypeId 帮助分类Id
	 * @param helpId 帮助Id
	 * @return
	 */
	public Integer markDelete(Long helpTypeId,Long helpId);
	
	
	
	/**
	 * 删除帮助
	 * @param helpTypeId 帮助分类Id
	 * @param helpId 帮助Id
	 * @param helpQuantity 减少帮助数量(显示)
	 * @return
	 */
	public Integer deleteHelp(Long helpTypeId,Long helpId,Long helpQuantity);
	/**
	 * 修改帮助分类Id
	 * @param old_HelpTypeId 旧帮助分类Id
	 * @param new_HelpTypeId 新帮助分类Id
	 * @return
	 */
	public Integer updateHelpTypeId(Long old_HelpTypeId,Long new_HelpTypeId);
}
