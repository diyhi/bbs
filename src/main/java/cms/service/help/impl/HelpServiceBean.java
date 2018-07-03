package cms.service.help.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.help.Help;
import cms.service.besa.DaoSupport;
import cms.service.help.HelpService;
import cms.service.help.HelpTypeService;;

/**
 * 帮助管理
 *
 */
@Service
@Transactional
public class HelpServiceBean extends DaoSupport<Help> implements HelpService{
	@Resource HelpTypeService helpTypeService;
	/**
	 * 根据Id查询帮助
	 * @param helpId 帮助Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Help findById(Long helpId){
		Query query = em.createQuery("select o from Help o where o.id=?1")
		.setParameter(1, helpId);
		List<Help> list = query.getResultList();
		for(Help p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据Id集合查询帮助
	 * @param helpIdList 帮助Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Help> findByIdList(List<Long> helpIdList){
		Query query = em.createQuery("select o from Help o where o.id in(:helpIdList)")
		.setParameter("helpIdList", helpIdList);
		return query.getResultList();
	}
	/**
	 * 根据分类Id查询帮助
	 * @param helpTypeId 帮助分类Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Help> findByTypeId(Long helpTypeId){
		Query query = em.createQuery("select o from Help o where o.helpTypeId=?1 and o.visible=?2")
		.setParameter(1, helpTypeId)
		.setParameter(2, true);
		return query.getResultList();
	}
	
	
	/**
	 * 保存帮助
	 * @param help
	 */
	public Integer saveHelp(Help help){
		
		
		//增加帮助数量
		int i = helpTypeService.addHelpQuantity(help.getHelpTypeId());
		this.save(help);
		return i;
	}
	/**
	 * 修改帮助
	 * @param help
	 * @return
	 */
	public Integer updateHelp(Help help){
		Query query = em.createQuery("update Help o set o.name=?1, o.content=?2,o.helpTypeId=?3 where o.id=?4")
		.setParameter(1, help.getName())
		.setParameter(2, help.getContent())
		.setParameter(3, help.getHelpTypeId())
		.setParameter(4, help.getId());
		int i = query.executeUpdate();
		return i;
	}
	/**
	 * 修改帮助
	 * @param helpList 帮助集合
	 * @param new_helpTypeId 新帮助分类Id
	 * @return
	 */
	public Integer updateHelp(List<Help> helpList,Long new_helpTypeId){
		int i = 0;
		if(helpList != null && helpList.size() >0){
			for(Help help : helpList){
				//旧帮助分类Id
				Long old_helpTypeId = help.getHelpTypeId();
				help.setHelpTypeId(new_helpTypeId);
				Query query = em.createQuery("update Help o set o.helpTypeId=?1, o.content=?2 where o.id=?3")
				.setParameter(1, help.getHelpTypeId())
				.setParameter(2, help.getContent())
				.setParameter(3, help.getId());
				i += query.executeUpdate();
				
				//减少旧帮助数量
				helpTypeService.minusHelpQuantity(old_helpTypeId,1L);
				//增加新帮助数量
				helpTypeService.addHelpQuantity(new_helpTypeId);
			}
		}
		return i;
	}

	
	/**
	 * 还原帮助
	 * @param helpList 帮助集合
	 * @return
	 */
	public Integer reductionHelp(List<Help> helpList){
		int i = 0;
		if(helpList != null && helpList.size() >0){
			for(Help help : helpList){
				Query query = em.createQuery("update Help o set o.visible=?1 where o.id=?2")
				.setParameter(1, true)
				.setParameter(2, help.getId());
				int j = query.executeUpdate();
				i += j;
				if(j >0){
					helpTypeService.mark_addHelpQuantity(help.getHelpTypeId());
				}
				
			}
		}
		return i;
	}
	
	/**
	 * 标记删除帮助
	 * @param helpTypeId 帮助分类Id
	 * @param helpId 帮助Id
	 * @return
	 */
	public Integer markDelete(Long helpTypeId,Long helpId){
		int i = 0;
		Query query = em.createQuery("update Help o set o.visible=?1 where o.id=?2")
		.setParameter(1, false)
		.setParameter(2, helpId);
		i= query.executeUpdate();
		if(i >0){
			helpTypeService.mark_minusHelpQuantity(helpTypeId);
		}
		return i;
		
	}
	
	
	
	/**
	 * 删除帮助
	 * @param helpTypeId 帮助分类Id
	 * @param helpId 帮助Id
	 * @param helpQuantity 减少帮助数量(显示)
	 * @return
	 */
	public Integer deleteHelp(Long helpTypeId,Long helpId,Long helpQuantity){
		int i = 0;
		Query delete = em.createQuery("delete from Help o where o.id=?1")
		.setParameter(1, helpId);
		i = delete.executeUpdate();
		if(i >0){
			helpTypeService.minusHelpQuantity(helpTypeId,helpQuantity);
		}
		return i;
	}
	/**
	 * 修改帮助分类Id
	 * @param old_HelpTypeId 旧帮助分类Id
	 * @param new_HelpTypeId 新帮助分类Id
	 * @return
	 */
	public Integer updateHelpTypeId(Long old_HelpTypeId,Long new_HelpTypeId){
		Query query = em.createQuery("update Help o set o.helpTypeId=?1 where o.helpTypeId=?2")
		.setParameter(1, new_HelpTypeId)
		.setParameter(2, old_HelpTypeId);
		int i = query.executeUpdate();
		return i;
	}
}
