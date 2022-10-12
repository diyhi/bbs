package cms.service.template.impl;


import java.util.List;
import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.template.TemplateData;
import cms.bean.template.Templates;
import cms.service.besa.DaoSupport;
import cms.service.template.TemplateService;

/**
 * 模板管理实现类
 *
 */
@Service
@Transactional
public class TemplateServiceBean extends DaoSupport<Layout> implements TemplateService {
	
	
	
	/**
	 * 查询所有已导入的模板
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Templates> findAllTemplates(){
		Query query = em.createQuery("select o from Templates o");
		return query.getResultList();
	}
	
	/**
	 * 查询系统当前使用的模板目录(目录为空时返回default)
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public String findTemplateDir(){
		
		Query query = em.createQuery("select o.dirName from Templates o where o.uses=?1")
		.setParameter(1, true);
		
		List<String> templatesList = query.getResultList();
		if(templatesList != null && templatesList.size() >0){
			for(String dirName :templatesList){
				return dirName;
			}
		}
		//如果没设置模板目录为使用,则默认第一个
		Query all_query = em.createQuery("select o.dirName from Templates o");
		//索引开始,即从哪条记录开始
		all_query.setFirstResult(0);
		//获取多少条数据
		all_query.setMaxResults(1);
		List<String> all_templatesList = all_query.getResultList();
		if(all_templatesList != null && all_templatesList.size() >0){
			for(String dirName :all_templatesList){
				return dirName;
			}
		}
		
		return "default";
	}
	
	
	/**
	 * 查询系统当前使用的模板目录(目录为空时返回default) - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="'findTemplateDir_default'")
	public String findTemplateDir_cache(){
		return this.findTemplateDir();
	}

	
	/**
	 * 保存模板
	 * @param templates
	 * @param layoutList 默认布局数据
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public void saveTemplate(Templates templates,List<Layout> layoutList){
		this.save(templates);
		//生成模板文件
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout : layoutList){
				this.save(layout);
			}
			
		}
	}
	/**
	 * 修改模板
	 * @param templates
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer updateTemplate(Templates templates){
		Query query = em.createQuery("update Templates o set o.name=?1,o.introduction=?2,o.thumbnailSuffix=?3,o.verifyCSRF=?4 where o.dirName=?5");
		//给SQL语句设置参数
		query.setParameter(1, templates.getName());
		query.setParameter(2, templates.getIntroduction());
		query.setParameter(3, templates.getThumbnailSuffix());
		query.setParameter(4, templates.getVerifyCSRF());
		query.setParameter(5, templates.getDirName());
		return query.executeUpdate();	
	}
	
	/**
	 * 删除模板
	 * @param dirName 模板目录
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer deleteTemplate(String dirName){
		int i = 0;
		//删除模板目录
		Query delete_template = em.createQuery("delete from Templates o where o.dirName=?1");
		//给SQL语句设置参数
		delete_template.setParameter(1, dirName);
		i= i+delete_template.executeUpdate();
		//删除布局
		Query delete_layout = em.createQuery("delete from Layout o where o.dirName=?1");
		//给SQL语句设置参数
		delete_layout.setParameter(1, dirName);
		i= i+delete_layout.executeUpdate();
		//删除版块
		Query delete_forum = em.createQuery("delete from Forum o where o.dirName=?1");
		//给SQL语句设置参数
		delete_forum.setParameter(1, dirName);
		i= i+delete_forum.executeUpdate();
		return i;
	}
	
	/**
	 * 设置当前模板为使用
	 * @param dirName 模板目录
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer useTemplate(String dirName){
		Query query = em.createQuery("update Templates o set o.uses=?1 ");
		//给SQL语句设置参数
		query.setParameter(1, false);
		query.executeUpdate();
		
		
		query = em.createQuery("update Templates o set o.uses=?1 where o.dirName=?2");
		//给SQL语句设置参数
		query.setParameter(1, true);
		query.setParameter(2, dirName);
		return query.executeUpdate();
	}
	/**
	 * 根据模板目录查询模板
	 * @param dirName 模板目录名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Templates findTemplatebyDirName(String dirName){
		Query query = em.createQuery("select o from Templates o where o.dirName=?1");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		List<Templates> templatesList = query.getResultList();
		for(Templates templates : templatesList){
			return templates;
		}
		return null;
	}
	/**
	 * 根据模板目录查询模板 - 缓存
	 * @param dirName 模板目录名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="'findTemplatebyDirName_'+#dirName")
	public Templates findTemplatebyDirName_cache(String dirName){
		return this.findTemplatebyDirName(dirName);
	}
	/**-----------------------------------------  站点栏目    ---------------------------------------------**/
	
	/**
	 * 修改站点栏目
	 * @param columnList_json 站点栏目JSON格式数据
	 * @param dirName 模板目录名称
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer updateColumn(String columnList_json,String dirName){
		
		Query query = em.createQuery("update Templates o set o.columns=?1 where o.dirName=?2");
		//给SQL语句设置参数
		query.setParameter(1, columnList_json);
		query.setParameter(2, dirName);
		return query.executeUpdate();
	}
	
	
	
	
	
	
	
	
	/**-------------------------------------------  布局   ---------------------------------------------**/

	/**
	 * 根据布局Id查询布局
	 * @param layoutId 布局Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Layout findLayoutByLayoutId(String layoutId){
		Query query = em.createQuery("select o from Layout o where o.id=?1");
		
		//给SQL语句设置参数
		query.setParameter(1, layoutId);
		List<Layout> layoutList = query.getResultList();
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout :layoutList){
				return layout;
			}
		}
		return null;
	}
	/**
	 * 根据模板目录查询最大布局排序号
	 * @param dirName 模板目录名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Integer findMaxLayoutSortBydirName(String dirName){
		Query query = em.createQuery("select o.sort from Layout o where o.dirName=?1 order by o.sort desc");
		
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		//索引开始,即从哪条记录开始
		query.setFirstResult(0);
		//获取多少条数据
		query.setMaxResults(1);
		List<Integer> sortList = query.getResultList();
		if(sortList != null && sortList.size() >0){
			for(Integer sort :sortList){
				return sort;
			}
		}
		return null;
	}
	
	/**
	 * 根据布局文件查询布局
	 * @param dirName 模板目录
	 * @param layoutFile 布局模板
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Layout> findLayoutByLayoutFile(String dirName,String layoutFile){
		Query query = em.createQuery("select o from Layout o where o.dirName=?1 and o.layoutFile like ?2");
		
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, layoutFile+"%");
		return query.getResultList();
	}
	/**
	 *  根据模板目录查询布局
	 * @param dirName 模板目录名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Layout> findLayout(String dirName){
		Query query = em.createQuery("select o from Layout o where o.dirName=?1");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		return query.getResultList();
	}
	/**
	 * 根据模板目录名称和布局类型查询布局
	 * @param dirName 模板目录名称
	 * @param type 布局类型
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Layout> findLayout(String dirName, Integer type){
			Query query = em.createQuery("select o from Layout o where o.dirName=?1 and o.type=?2");
			//给SQL语句设置参数
			query.setParameter(1, dirName);
			query.setParameter(2, type);
			return query.getResultList();
	}
	/**
	 * 根据模板目录名称和布局类型查询布局 - 缓存
	 * @param dirName 模板目录名称
	 * @param type 布局类型
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
//	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findLayout_'+T(String).valueOf(#type)")
	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findLayout_'+#type")
	public List<Layout> findLayout_cache(String dirName, Integer type){
		return this.findLayout(dirName, type);
		
	}

	
	/**
	 * 根据模板目录名称和引用代码前缀查询布局
	 * @param dirName 模板目录
	 * @param referenceCodePrefix 模块引用代码前缀  如:Brand_Show_
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Layout> findLayoutByReferenceCode(String dirName,String referenceCodePrefix){
		Query query = em.createQuery("select o from Layout o where o.dirName=?1 and o.referenceCode like ?2");
		
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, referenceCodePrefix+"%");
		return query.getResultList();
	}

	/**
	 * 根据模板目录名称和布局类型和引用代码查询布局
	 * @param dirName 模板目录
	 * @param type 布局类型
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Layout findLayoutByReferenceCode(String dirName,Integer type,String referenceCode){
		Query query = em.createQuery("select o from Layout o where o.dirName=?1 and o.type=?2 and o.referenceCode=?3");
		
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, type);
		query.setParameter(3, referenceCode);
		List<Layout> layoutList = query.getResultList();
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout : layoutList){
				return layout;
			}
		}
		return null;
	}
	/**
	 * 根据模板目录名称和布局类型和引用代码查询布局 - 缓存
	 * @param dirName 模板目录
	 * @param type 布局类型
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findLayoutByReferenceCode_'+#type+'-'+#referenceCode")
	public Layout findLayoutByReferenceCode_cache(String dirName,Integer type,String referenceCode){
		return this.findLayoutByReferenceCode(dirName,type,referenceCode);
	}
	
	/**
	 * 根据模板目录名称查询'更多'
	 * @param dirName 模板目录
	 * @param forumData 版块数据
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Layout> findMore(String dirName,Integer forumData){
		Query query = em.createQuery("select o from Layout o where o.dirName=?1 and o.type=?2 and o.forumData=?3 ");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, 3);//3.更多 
		query.setParameter(3, forumData);
		return query.getResultList();
	}
	/**
	 * 根据商品分类Id查询布局
	 * @param dirName 模板目录名称
	 * @param productTypeId 商品分类Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Layout findLayoutByProductTypeId(String dirName, Long productTypeId){
		Query query = em.createQuery("select o from Layout o where o.dirName=?1 and o.productTypeId=?2 ");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, productTypeId);
		List<Layout> layoutList = query.getResultList();
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout : layoutList){
				return layout;
			}
		}
		return null;
	}
	/**
	 * 根据商品分类Id查询布局 - 缓存
	 * @param dirName 模板目录名称
	 * @param productTypeId 商品分类Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findLayoutByProductTypeId_'+#productTypeId")
	public Layout findLayoutByProductTypeId_cache(String dirName, Long productTypeId){
		return this.findLayoutByProductTypeId(dirName, productTypeId);
	}

	/**
	 * 添加布局
	 * @param layout 布局
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public void saveLayout(Layout layout){
		this.save(layout);
	}
	/**
	 * 修改布局
	 * @param layout 布局
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer updateLayoutById(Layout layout){
	
		Query query = em.createQuery("update Layout o set o.name=?1,o.referenceCode=?2, o.accessRequireLogin=?3 where o.id=?4");
		//给SQL语句设置参数
		query.setParameter(1, layout.getName());
		query.setParameter(2, layout.getReferenceCode());
		query.setParameter(3, layout.isAccessRequireLogin());
		query.setParameter(4, layout.getId());
		return query.executeUpdate();
	}
	/**
	 * 修改布局名称
	 * @param dirName 模板目录
	 * @param newSort 排序
	 * @param type 布局类型
	 * @param referenceCode 模块引用代码
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public void updateLayoutName(String newName,Integer newSort,String dirName,Integer type,String referenceCode){
		Query query = em.createQuery("update Layout o set o.name=?1,o.sort=?2 where o.dirName=?3 and o.type=?4 and o.referenceCode=?5");
		//给SQL语句设置参数
		query.setParameter(1, newName);
		query.setParameter(2, newSort);
		query.setParameter(3, dirName);
		query.setParameter(4, type);
		query.setParameter(5, referenceCode);
		query.executeUpdate();
	}
	
	/**
	 * 根据布局Id删除布局
	 * @param dirName 模板目录名称
	 * @param layoutId 布局Id
	 * @return
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer deleteLayoutByLayoutId(String dirName,String layoutId){
		Query delete = em.createQuery("delete from Layout o where o.id=?1 and o.dirName=?2");
		//给SQL语句设置参数
		delete.setParameter(1, layoutId);
		delete.setParameter(2, dirName);
		//删除版块
		this.deleteForumByLayoutId(dirName,layoutId);
		return delete.executeUpdate();
	}
	
	
	/**-----------------------------------------  版块    ---------------------------------------------**/
	/**
	 * 查询模板版块
	 * @param dirName 模板目录名称
	 * @param layoutType 布局类型
	 * @param layoutFile 布局文件
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Forum> findForum(String dirName, Integer layoutType,String layoutFile){
		Query query = em.createQuery("select o from Forum o where o.dirName=?1 and o.layoutType=?2 and o.layoutFile=?3");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, layoutType);
		query.setParameter(3, layoutFile);
		return query.getResultList();

	}
	/**
	 * 查询模板版块 - 缓存
	 * @param dirName 模板目录名称
	 * @param layoutType 布局类型
	 * @param layoutFile 布局文件
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findForum_'+#layoutType+'-'+#layoutFile")
	public List<Forum> findForum_cache(String dirName, Integer layoutType,String layoutFile){
		return this.findForum(dirName, layoutType,layoutFile);
	}
	
	
	
	/**
	 * 根据布局Id查询模板版块
	 * @param dirName 模板目录名称
	 * @param layoutId布局Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Forum> findForumByLayoutId(String dirName, String layoutId){
		Query query = em.createQuery("select o from Forum o where o.dirName=?1 and o.layoutId=?2 ");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, layoutId);
		return query.getResultList();
	}
	/**
	 * 根据布局Id查询模板版块 - 缓存
	 * @param dirName 模板目录名称
	 * @param layoutId布局Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findForumByLayoutId_'+#layoutId")
	public List<Forum> findForumByLayoutId_cache(String dirName, String layoutId){
		return this.findForumByLayoutId(dirName, layoutId);
	}
	
	
	
	/**
	 * 根据模板目录名称和引用代码查询版块
	 * @param dirName 模板目录
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Forum findForum(String dirName,String referenceCode){
		Query query = em.createQuery("select o from Forum o where o.dirName=?1 and o.referenceCode=?2");
		
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, referenceCode);
		List<Forum> forumList = query.getResultList();
		if(forumList != null && forumList.size() >0){
			for(Forum forum : forumList){
				return forum;
			}
		}
		return null;
	}
	/**
	 * 根据模板目录名称和引用代码查询版块 - 缓存
	 * @param dirName 模板目录
	 * @param referenceCode 模块引用代码
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="#dirName+'_findForum_'+#referenceCode")
	public Forum findForum_cache(String dirName,String referenceCode){
		return this.findForum(dirName,referenceCode);
	}
	
	/**
	 * 根据版块Id查询版块
	 * @param forumId 版块Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Forum findForumById(Integer forumId){
		Query query = em.createQuery("select o from Forum o where o.id=?1 ");
		//给SQL语句设置参数
		query.setParameter(1, forumId);
		List<Forum> forumList = query.getResultList();
		if(forumList != null && forumList.size() >0){
			for(Forum forum : forumList){
				return forum;
			}
		}
		return null;
	}
	/**
	 * 根据版块Id查询版块 - 缓存
	 * @param forumId 版块Id
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="templateServiceBean_cache",key="'findForumById_'+#dirName")
	public Forum findForumById_cache(Integer forumId){
		return this.findForumById(forumId);
	}
	/**
	 * 根据模板目录名称和模板字段查询版块是否存在
	 * @param dirname 模板目录名称
	 * @param layoutId 布局Id
	 * @param layoutType 布局类型
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public boolean getForumThere(String dirName, String layoutId,Integer layoutType){
		boolean b = false;
		Query query = em.createQuery("select o from Forum o where o.layoutId=?1 and o.dirName=?2 and o.layoutType=?3");
		//给SQL语句设置参数
		query.setParameter(1, layoutId);
		query.setParameter(2, dirName);
		query.setParameter(3, layoutType);
		List<Forum> list = query.getResultList();
		if(list != null && list.size()>0){
			b = true;
		}	
		return b;
	}
	/**
	 * 根据模块引用代码前缀查询模块引用代码当前类别的所有值
	 * @param dirName 模板目录
	 * @param referenceCodePrefix 模块引用代码前缀  如:Brand_Show_
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Forum> findForumByReferenceCodePrefix(String dirName,String referenceCodePrefix){
		Query query = em.createQuery("select o from Forum o where o.dirName=?1 and o.referenceCode like ?2 escape '/'");
		
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		query.setParameter(2, cms.utils.StringUtil.escapeSQLLike(referenceCodePrefix)+"%");
		return query.getResultList();

	}
	/**
	 * 添加版块
	 * @param forum 版块
	 * @return
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public void saveForum(Forum forum){
		this.save(forum);
	}

	/**
	 * 修改版块
	 * @param forum 版块
	 * @return
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer updateForum(Forum forum){
		Query query = em.createQuery("update Forum o set o.name=?1,o.queryMode=?2,o.invokeMethod=?3,o.formValue=?4 where o.id=?5");
		//给SQL语句设置参数
		query.setParameter(1, forum.getName());//版块名称
		query.setParameter(2, forum.getQueryMode());//查询方式
		query.setParameter(3, forum.getInvokeMethod());//调用方式
		query.setParameter(4, forum.getFormValue());//JSON格式扩展表单值
		query.setParameter(5, forum.getId());
		return query.executeUpdate();	
	}
	
	
	/**
	 * 根据布局Id删除版块
	 * @param dirName 模板目录名称
	 * @param layoutId 布局Id
	 * @return
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	private Integer deleteForumByLayoutId(String dirName,String layoutId){
		Query delete = em.createQuery("delete from Forum o where o.layoutId=?1 and o.dirName=?2");
		//给SQL语句设置参数
		delete.setParameter(1, layoutId);
		delete.setParameter(2, dirName);
		return delete.executeUpdate();
	}
	/**
	 * 根据版块Id删除版块
	 * @param forumId 版块Id
	 * @return
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public Integer deleteForumByForumId(Integer forumId){
		Query delete = em.createQuery("delete from Forum o where o.id=?1");
		//给SQL语句设置参数
		delete.setParameter(1, forumId);
		return delete.executeUpdate();
	}
	
	/**
	 * 根据模板目录查询版块
	 * @param dirName 模板目录名称
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Forum> findForumByDirName(String dirName){
		Query query = em.createQuery("select o from Forum o where o.dirName=?1");
		//给SQL语句设置参数
		query.setParameter(1, dirName);
		return query.getResultList();
	}
	/**-----------------------------------------  数据    ---------------------------------------------**/
	
	/**
	 * 保存模板数据
	 * @param templateData 模板数据
	 */
	@CacheEvict(value="templateServiceBean_cache",allEntries=true)
	public void saveTemplateData(TemplateData templateData){
		if(templateData.getTemplates() != null){
			this.save(templateData.getTemplates());
		}
		//布局数据
		List<Layout> layoutList = templateData.getLayoutList();
		if(layoutList != null && layoutList.size() >0){
			for(Layout layout : layoutList){
				this.save(layout);
			}
		}
		//版块数据
		List<Forum> forumList = templateData.getForumList();
		if(forumList != null && forumList.size() >0){
			for(Forum forum : forumList){
				this.save(forum);
			}
		}
	}
}
