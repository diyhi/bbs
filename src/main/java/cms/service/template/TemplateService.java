package cms.service.template;

import java.util.List;

import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.template.TemplateData;
import cms.bean.template.Templates;
import cms.service.besa.DAO;

/**
 * 模板管理接口
 *
 */
public interface TemplateService extends DAO<Layout> {

	/**
	 * 查询所有已导入的模板
	 * @return
	 */
	public List<Templates> findAllTemplates();
	
	/**
	 * 查询系统当前使用的模板目录(目录为空时返回default)
	 * @return
	 */
	public String findTemplateDir();
	/**
	 * 查询系统当前使用的模板目录(目录为空时返回default) - 缓存
	 * @return
	 */
	public String findTemplateDir_cache();
	/**
	 * 查询系统当前使用的模板
	 * @return
	 */
	public Templates findUseTemplate();
	
	/**
	 * 查询系统当前使用的模板(目录为空时返回null) - 缓存
	 * @return
	 */
	public Templates findUseTemplate_cache();
	/**
	 * 保存模板
	 * @param templates
	 * @param layoutList 默认布局数据
	 */
	public void saveTemplate(Templates templates,List<Layout> layoutList);
	/**
	 * 修改模板
	 * @param templates
	 */
	public Integer updateTemplate(Templates templates);
	/**
	 * 删除模板
	 * @param dirName 模板目录
	 */
	public Integer deleteTemplate(String dirName);
	/**
	 * 设置当前模板为使用
	 * @param dirName 模板目录
	 */
	public Integer useTemplate(String dirName);
	/**
	 * 根据模板目录查询模板
	 * @param dirName 模板目录名称
	 * @return
	*/
	public Templates findTemplatebyDirName(String dirName); 
	/**
	 * 根据模板目录查询模板 - 缓存
	 * @param dirName 模板目录名称
	 * @return
	 */
	public Templates findTemplatebyDirName_cache(String dirName);
	
	/**-----------------------------------------  站点栏目    ---------------------------------------------**/
	
	/**
	 * 修改站点栏目
	 * @param columnList_json 站点栏目JSON格式数据
	 * @param dirName 模板目录名称
	 */
	public Integer updateColumn(String columnList_json,String dirName);
	/**-------------------------------------------  布局   ---------------------------------------------**/
	/**
	 * 根据布局Id查询布局
	 * @param layoutId 布局Id
	 * @return
	 */
	public Layout findLayoutByLayoutId(String layoutId);
	/**
	 * 根据模板目录查询最大布局排序号
	 * @param dirName 模板目录名称
	 * @return
	 */
	public Integer findMaxLayoutSortBydirName(String dirName);
	/**
	 * 根据布局文件查询布局
	 * @param dirName 模板目录
	 * @param layoutFile 布局模板
	 * @return
	 */
	public List<Layout> findLayoutByLayoutFile(String dirName,String layoutFile);
	/**
	 *  根据模板目录查询布局
	 * @param dirName 模板目录名称
	 * @return
	 */
	public List<Layout> findLayout(String dirName);
	/**
	 * 根据模板目录名称和布局类型查询布局
	 * @param dirName 模板目录名称
	 * @param type 布局类型
	 * @return
	*/
	public List<Layout> findLayout(String dirName, Integer type); 
	/**
	 * 根据模板目录名称和布局类型查询布局 - 缓存
	 * @param dirName 模板目录名称
	 * @param type 布局类型
	 * @return
	 */
	public List<Layout> findLayout_cache(String dirName, Integer type);
	/**
	 * 根据模板目录名称和引用代码前查询布局
	 * @param dirName 模板目录
	 * @param referenceCodePrefix 模块引用代码前缀  如:Brand_Show_
	 * @return
	 */
	public List<Layout> findLayoutByReferenceCode(String dirName,String referenceCodePrefix);
	
	/**
	 * 根据模板目录名称和布局类型和引用代码查询布局
	 * @param dirName 模板目录
	 * @param type 布局类型
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	public Layout findLayoutByReferenceCode(String dirName,Integer type,String referenceCode);
	/**
	 * 根据模板目录名称和布局类型和引用代码查询布局 - 缓存
	 * @param dirName 模板目录
	 * @param type 布局类型
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	public Layout findLayoutByReferenceCode_cache(String dirName,Integer type,String referenceCode);
	/**
	 * 根据模板目录名称查询'更多'
	 * @param dirName 模板目录
	 * @param forumData 版块数据
	 * @return
	 */
	public List<Layout> findMore(String dirName,Integer forumData);
	/**
	 * 根据商品分类Id查询布局
	 * @param dirName 模板目录名称
	 * @param productTypeId 商品分类Id
	 * @return
	 */
	public Layout findLayoutByProductTypeId(String dirName, Long productTypeId);
	/**
	 * 根据商品分类Id查询布局 - 缓存
	 * @param dirName 模板目录名称
	 * @param productTypeId 商品分类Id
	 * @return
	 */
	public Layout findLayoutByProductTypeId_cache(String dirName, Long productTypeId);
	/**
	 * 添加布局
	 * @param layout 布局
	 */
	public void saveLayout(Layout layout);
	/**
	 * 修改布局
	 * @param layout 布局
	 */
	public Integer updateLayoutById(Layout layout);
	/**
	 * 修改布局名称
	 * @param dirName 模板目录
	 * @param newSort 排序
	 * @param type 布局类型
	 * @param referenceCode 模块引用代码
	 */
	public void updateLayoutName(String newName,Integer newSort,String dirName,Integer type,String referenceCode);
	/**
	 * 根据布局Id删除布局
	 * @param dirName 模板目录名称
	 * @param layoutId 布局Id
	 * @return
	 */
	public Integer deleteLayoutByLayoutId(String dirName,String layoutId);
	/**-----------------------------------------  版块    ---------------------------------------------**/
	/**
	 * 查询模板版块
	 * @param dirName 模板目录名称
	 * @param layoutType 布局类型
	 * @param layoutFile 布局文件
	 * @return
	 */
	public List<Forum> findForum(String dirName, Integer layoutType,String layoutFile);
	/**
	 * 查询模板版块 - 缓存
	 * @param dirName 模板目录名称
	 * @param layoutType 布局类型
	 * @param layoutFile 布局文件
	 * @return
	 */
	public List<Forum> findForum_cache(String dirName, Integer layoutType,String layoutFile);
	/**
	 * 根据布局Id查询模板版块
	 * @param dirName 模板目录名称
	 * @param layoutId布局Id
	 * @return
	 */
	public List<Forum> findForumByLayoutId(String dirName, String layoutId);
	/**
	 * 根据布局Id查询模板版块 - 缓存
	 * @param dirName 模板目录名称
	 * @param layoutId布局Id
	 * @return
	 */
	public List<Forum> findForumByLayoutId_cache(String dirName, String layoutId);
	/**
	 * 根据模板目录名称和引用代码查询版块
	 * @param dirName 模板目录
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	public Forum findForum(String dirName,String referenceCode);
	/**
	 * 根据模板目录名称和引用代码查询版块 - 缓存
	 * @param dirName 模板目录
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	public Forum findForum_cache(String dirName,String referenceCode);
	/**
	 * 根据版块Id查询版块
	 * @param forumId 版块Id
	 * @return
	 */
	public Forum findForumById(Integer forumId);
	/**
	 * 根据版块Id查询版块 - 缓存
	 * @param forumId 版块Id
	 * @return
	 */
	public Forum findForumById_cache(Integer forumId);
	/**
	 * 根据模板目录名称和模板字段查询版块是否存在
	 * @param dirname 模板目录名称
	 * @param layoutId 布局Id
	 * @param layoutType 布局类型
	 * @return
	*/
	public boolean getForumThere(String dirName, String layoutId,Integer layoutType); 
	/**
	 * 根据模块引用代码前缀查询模块引用代码当前类别的所有值
	 * @param dirName 模板目录
	 * @param referenceCodePrefix 模块引用代码前缀  如:Brand_Show_
	 * @return
	 */
	public List<Forum> findForumByReferenceCodePrefix(String dirName,String referenceCodePrefix);
	/**
	 * 添加版块
	 * @param forum 版块
	 * @return
	 */
	public void saveForum(Forum forum);
	/**
	 * 修改版块
	 * @param forum 版块
	 * @return
	 */
	public Integer updateForum(Forum forum);
	/**
	 * 根据版块Id删除版块
	 * @param forumId 版块Id
	 * @return
	 */
	public Integer deleteForumByForumId(Integer forumId);
	/**
	 * 根据模板目录查询版块
	 * @param dirName 模板目录名称
	 * @return
	 */
	public List<Forum> findForumByDirName(String dirName);
	/**
	 * 保存模板数据
	 * @param templateData 模板数据
	 */
	public void saveTemplateData(TemplateData templateData);
}
