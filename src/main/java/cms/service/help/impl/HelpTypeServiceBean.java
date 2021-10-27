package cms.service.help.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.help.HelpType;
import cms.service.besa.DaoSupport;
import cms.service.help.HelpService;
import cms.service.help.HelpTypeService;
import cms.web.action.SystemException;

/**
 * 帮助管理
 *
 */
@Service
@Transactional
public class HelpTypeServiceBean extends DaoSupport<HelpType> implements HelpTypeService{
	@Resource HelpService helpService;
	
	/**
	 * 根据Id查询帮助分类
	 * @param helpTypeId 帮助分类Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public HelpType findById(Long helpTypeId){
		Query query = em.createQuery("select o from HelpType o where o.id=?1")
		.setParameter(1, helpTypeId);
		List<HelpType> list = query.getResultList();
		for(HelpType p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据Id查询帮助分类
	 * @param helpTypeIdList 帮助分类Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<HelpType> findByIdList(List<Long> helpTypeIdList){
		Query query = em.createQuery("select o from HelpType o where o.id in(:id)")
		.setParameter("id", helpTypeIdList);
		return query.getResultList();
	}
	/**
	 * 查询所有帮助分类
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<HelpType> findAllHelpType(){
		Query query = em.createQuery("select o from HelpType o ");
		return query.getResultList();
	}
	/**
	 * 根据Id组查询子分类
	 * @param idGroup 帮助分类Id组
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<HelpType> findChildHelpTypeByIdGroup(String idGroup){
		Query query = em.createQuery("select o from HelpType o where o.parentIdGroup like ?1")
		.setParameter(1,idGroup+'%' );
		List<HelpType> list = query.getResultList();
		return list;
	}
	
	
	
	/**
	 * 增加帮助数量
	 * @param helpType 帮助分类Id
	 */
	public Integer addHelpQuantity(Long helpTypeId){
		Query query = em.createQuery("update HelpType o set o.totalHelp=o.totalHelp+1,o.helpQuantity=o.helpQuantity+1 where o.id=?1 and o.childNodeNumber=?2")
		.setParameter(1, helpTypeId)
		.setParameter(2, 0);
		return query.executeUpdate();
	}
	/**
	 * 减少帮助数量
	 * @param helpType 帮助分类Id
	 * @param helpQuantity 帮助数量(显示) 
	 */
	public Integer minusHelpQuantity(Long helpTypeId,Long helpQuantity){
		Query query = em.createQuery("update HelpType o set o.totalHelp=o.totalHelp-1,o.helpQuantity=o.helpQuantity-?1 where o.id=?2 and o.totalHelp >0")
		.setParameter(1, helpQuantity)
		.setParameter(2, helpTypeId);
		return query.executeUpdate();
	}
	/**
	 * 标记删除增加帮助数量
	 * @param helpType 帮助分类Id
	 */
	public Integer mark_addHelpQuantity(Long helpTypeId){
		Query query = em.createQuery("update HelpType o set o.helpQuantity=o.helpQuantity+1 where o.id=?1 and o.childNodeNumber=?2")
		.setParameter(1, helpTypeId)
		.setParameter(2, 0);
		return query.executeUpdate();
	}
	/**
	 * 标记删除减少帮助数量
	 * @param helpType 帮助分类Id
	 */
	public Integer mark_minusHelpQuantity(Long helpTypeId){
		Query query = em.createQuery("update HelpType o set o.helpQuantity=o.helpQuantity-1 where o.id=?1 and o.helpQuantity >0")
		.setParameter(1, helpTypeId);
		return query.executeUpdate();
	}
	

	/**
	 * 保存帮助分类
	 * @param helpType
	 */
	public void saveType(HelpType helpType){
		this.save(helpType);
		if(!helpType.getParentId().equals(0L)){//如果不是根节点
			
			//修改父节点叶子节点状态
			Query query = em.createQuery("update HelpType o set o.childNodeNumber=o.childNodeNumber+1 where o.id=?1")
			.setParameter(1, helpType.getParentId());
			int i = query.executeUpdate();
			if(i==0){
				throw new SystemException("父节点不存在");
			}
			HelpType parentHelpType = this.findById(helpType.getParentId());
			//帮助总数量(显示+回收站)
			Long totalHelp = 0L;
			//帮助数量(显示)
			Long helpQuantity = 0L;
			//已合并分类Id
			String mergerTypeId = ",";
			if(parentHelpType != null){
				totalHelp = parentHelpType.getTotalHelp();
				helpQuantity = parentHelpType.getHelpQuantity();
				mergerTypeId = parentHelpType.getMergerTypeId();
			}
			
			//验证上级节点原来是根节点才执行
			
			Query query2 = em.createQuery("update HelpType o set o.childNodeNumber=?1,o.totalHelp=?2,o.helpQuantity=?3,o.mergerTypeId=?4 where o.id=?5 and o.childNodeNumber=?6")
			.setParameter(1, 1)
			.setParameter(2, 0L)
			.setParameter(3, 0L)
			.setParameter(4, ",")
			.setParameter(5, helpType.getParentId())
			.setParameter(6, 1);
			int j = query2.executeUpdate();
			if(j >0){
				
				
				//将父节点帮助数量转到本节点
				Query query3 = em.createQuery("update HelpType o set o.totalHelp=?1,o.helpQuantity=?2,o.mergerTypeId=?3 where o.id=?4")
				.setParameter(1, totalHelp)
				.setParameter(2, helpQuantity)
				.setParameter(3, mergerTypeId)
				.setParameter(4, helpType.getId());
				query3.executeUpdate();
				//将父节点下的分类帮助转到本节点
				helpService.updateHelpTypeId(helpType.getParentId(), helpType.getId());
				
			
			}
		
		}
	}
	
	/**
	 * 修改帮助分类
	 * @param helpType 帮助分类
	 */
	public Integer updateHelpType(HelpType helpType){
		Query query = em.createQuery("update HelpType o set o.name=?1, o.sort=?2,o.image=?3,o.description=?4 where o.id=?5")
		.setParameter(1, helpType.getName())
		.setParameter(2, helpType.getSort())
		.setParameter(3, helpType.getImage())
		.setParameter(4, helpType.getDescription())
		.setParameter(5, helpType.getId());
		int i = query.executeUpdate();
		return i;
	}
	
	
	
	/**
	 * 删除帮助分类
	 * @param helpType 帮助分类
	 * @param allMergerTypeId 所有合并分类Id
	 */
	public Integer deleteHelpType(HelpType helpType){
		int i = 0;
		Query delete = em.createQuery("delete from HelpType o where o.id=?1")
		.setParameter(1,helpType.getId());
		i = delete.executeUpdate();
		if(i>0){
			
			if(helpType.getParentId() >0L){
				//将父类计数减一
				Query query = em.createQuery("update HelpType o set o.childNodeNumber=childNodeNumber-1 where o.id=?1")
				.setParameter(1, helpType.getParentId());
				query.executeUpdate();
			}
			//删除分类下帮助
			Query delete_help = em.createQuery("delete from Help o where o.helpTypeId=?1")
			.setParameter(1,helpType.getId());
			i = delete_help.executeUpdate();
			
			this.deleteChildNode(Arrays.asList(helpType.getId()));
			
			
		}
		return i;
	}
	/**
	 * 递归删除子节点下帮助
	 * @param typeIdList
	 */
	private void deleteChildNode(List<Long> typeIdList){	
		List<Long> idList = new ArrayList<Long>();
		for(Long typeId : typeIdList){
			Query query = em.createQuery("select o from HelpType o where o.parentId=?1")
			.setParameter(1, typeId);
			List<HelpType> helpTypeList = query.getResultList();
			if(helpTypeList != null && helpTypeList.size() >0){

				for(HelpType p : helpTypeList){
					//删除当前节点
					Query delete = em.createQuery("delete from HelpType o where o.id=?1")
						.setParameter(1,p.getId());
					delete.executeUpdate();
					
					if(p.getChildNodeNumber() >0){
						idList.add(p.getId());
						
					}else{//如果是最后一个节点
						
						//删除分类下帮助
						Query delete_help = em.createQuery("delete from Help o where o.helpTypeId=?1")
						.setParameter(1,p.getId());
						delete_help.executeUpdate();
					
					}
					
				}	
			}
		}
		if(idList != null && idList.size() >0){
			deleteChildNode(idList);
		}
	}
	

	
	
	/**
	 * 根据分类查询所有父类帮助分类
	 * @param helpType 分类
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<HelpType> findAllParentById(HelpType helpType){
		List<HelpType> helpTypeList = new ArrayList<HelpType>();
		//查询所有父类
		if(helpType.getParentId() >0){
			List<HelpType> list = this.findParentById(helpType.getParentId(),new ArrayList<HelpType>());
			helpTypeList.addAll(list);
		}
		//倒转顺序
		Collections.reverse(helpTypeList);
		return helpTypeList;
	}
	
	/**
	 * 根据ID查询帮助父类 (递归)
	 * @param parentId 父分类ID
	 * @param productTypeList 父分类集合
	 * @return
	*/
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	private List<HelpType> findParentById(Long parentId,List<HelpType> helpTypeList){
		HelpType parentHelpType =this.findById(parentId);
		if(parentHelpType != null){
			helpTypeList.add(parentHelpType);
			if(parentHelpType.getParentId() >0L){
				this.findParentById(parentHelpType.getParentId(),helpTypeList);
			}
		}
		return helpTypeList;
	}
	
	/**
	 * 合并商品分类
	 * @param productType 原商品分类
	 * @param mergerProductType 待合并商品分类
	 * @return
	 */
	public Integer mergerHelpType(Long helpTypeId,HelpType mergerHelpType){
		int i= 0;
		Query query = em.createQuery("update HelpType o set o.totalHelp=o.totalHelp+?1,o.helpQuantity=o.helpQuantity+?2,o.mergerTypeId=CONCAT(o.mergerTypeId,?3) where o.id=?4")
				.setParameter(1, mergerHelpType.getTotalHelp())
				.setParameter(2, mergerHelpType.getHelpQuantity())
				.setParameter(3, mergerHelpType.getId()+mergerHelpType.getMergerTypeId())
				.setParameter(4, helpTypeId);
		i = query.executeUpdate();
		Query delete_helpType = em.createQuery("delete from HelpType o where o.id=?1")
		.setParameter(1,mergerHelpType.getId());
		int i_delete = delete_helpType.executeUpdate();
		i += i_delete;
		if(i_delete>0){
			if(mergerHelpType.getParentId() >0L){
				//将父类计数减一
				Query query_ = em.createQuery("update HelpType o set o.childNodeNumber=childNodeNumber-1 where o.id=?1")
				.setParameter(1, mergerHelpType.getParentId());
				query_.executeUpdate();
			}
		}
		
		//修改帮助分类参数
		
		Query query_help = em.createQuery("update Help o set o.helpTypeId=?1 where o.helpTypeId=?2")
		.setParameter(1, helpTypeId)
		.setParameter(2, mergerHelpType.getId());
		query_help.executeUpdate();

		return i;
	}
	
}
