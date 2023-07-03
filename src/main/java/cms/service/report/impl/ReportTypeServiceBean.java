package cms.service.report.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.report.ReportType;
import cms.service.besa.DaoSupport;
import cms.service.report.ReportService;
import cms.service.report.ReportTypeService;
import cms.web.action.SystemException;

/**
 * 举报分类管理接口实现类
 *
 */
@Service
@Transactional
public class ReportTypeServiceBean extends DaoSupport<ReportType> implements ReportTypeService{
	@Resource ReportService reportService;
	
	/** 
	 * 根据Id查询分类
	 * @param reportTypeId 分类Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public ReportType findById(String reportTypeId){
		Query query = em.createQuery("select o from ReportType o where o.id=?1")
		.setParameter(1, reportTypeId);
		List<ReportType> list = query.getResultList();
		for(ReportType p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据分类查询所有父分类
	 * @param reportType 分类
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<ReportType> findAllParentById(ReportType reportType){
		List<ReportType> typeList = new ArrayList<ReportType>();
		//查询所有父类
		if(!"0".equals(reportType.getParentId())){
			List<ReportType> list = this.findParentById(reportType.getParentId(),new ArrayList<ReportType>());
			typeList.addAll(list);
		}
		//倒转顺序
		Collections.reverse(typeList);
		return typeList;
	}
	
	/**
	 * 根据Id查询父分类 (递归)
	 * @param parentId 父分类Id
	 * @param reportTypeList 父分类集合
	 * @return
	*/
	private List<ReportType> findParentById(String parentId,List<ReportType> reportTypeList){
		ReportType parentType =this.findById(parentId);
		if(parentType != null){
			reportTypeList.add(parentType);
			if(!"0".equals(parentType.getParentId())){
				this.findParentById(parentType.getParentId(),reportTypeList);
			}
		}
		return reportTypeList;
	}
	
	/**
	 * 根据分类Id查询子分类(下一节点)
	 * @param reportTypeId 分类Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<ReportType> findChildTypeById(String reportTypeId){
		Query query = em.createQuery("select o from ReportType o where o.parentId=?1")
		.setParameter(1,reportTypeId);
		List<ReportType> list = query.getResultList();
		return list;
	}
	
	/**
	 * 查询所有举报分类
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<ReportType> findAllReportType(){
		Query query = em.createQuery("select o from ReportType o order by o.sort desc");
		return query.getResultList();
	}
	
	/**
	 * 查询所有举报分类 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="findAllReportType_cache",key="'findAllReportType_default'")
	public List<ReportType> findAllReportType_cache(){
		return this.findAllReportType();
	}

	
	/**
	 * 保存分类
	 * @param reportType
	 */
	@CacheEvict(value="findAllReportType_cache",allEntries=true)
	public void saveReportType(ReportType reportType){
		this.save(reportType);
		
		if(!"0".equals(reportType.getParentId())){//如果不是根节点
			//修改父节点叶子节点状态
			Query query = em.createQuery("update ReportType o set o.childNodeNumber=o.childNodeNumber+1 where o.id=?1")
			.setParameter(1, reportType.getParentId());
			int i = query.executeUpdate();
			if(i==0){
				throw new SystemException("父节点不存在");
			}
			
			//验证上级节点原来是根节点才执行
			Query query2 = em.createQuery("update ReportType o set o.childNodeNumber=?1 where o.id=?2 and o.childNodeNumber=?3")
			.setParameter(1, 1)
			.setParameter(2, reportType.getParentId())
			.setParameter(3, 1);
			int j = query2.executeUpdate();
			if(j >0){
				//将父节点下的举报转到本节点
				reportService.updateTypeId(reportType.getParentId(), reportType.getId());
			}
		}
		
	}
	

	/**
	 * 修改分类
	 * @param reportType
	 * @return
	 */
	@CacheEvict(value="findAllReportType_cache",allEntries=true)
	public Integer updateReportType(ReportType reportType){
		Query query = em.createQuery("update ReportType o set o.name=?1, o.sort=?2,o.giveReason=?3 where o.id=?4")
		.setParameter(1, reportType.getName())
		.setParameter(2, reportType.getSort())
		.setParameter(3, reportType.getGiveReason())
		.setParameter(4, reportType.getId());
		return query.executeUpdate();
	}
	
	/**
	 * 删除分类
	 * @param reportType 分类
	 */
	@CacheEvict(value="findAllReportType_cache",allEntries=true)
	public Integer deleteReportType(ReportType reportType){
		int i = 0;
		Query delete = em.createQuery("delete from ReportType o where o.id=?1")
		.setParameter(1,reportType.getId());
		i = delete.executeUpdate();
		if(i >0){
			
			if(!"0".equals(reportType.getParentId())){
				//将父节点计数减一
				Query query = em.createQuery("update ReportType o set o.childNodeNumber=childNodeNumber-1 where o.id=?1")
				.setParameter(1, reportType.getParentId());
				query.executeUpdate();
			}
			
			
			this.deleteChildNode(Arrays.asList(reportType.getId()));
		}
		
		return i;
	}
	
	
	/**
	 * 递归删除所有子节点
	 * @param reportTypeIdList
	 */
	private void deleteChildNode(List<String> reportTypeIdList){
		List<String> idList = new ArrayList<String>();
		for(String typeId : reportTypeIdList){
			Query query = em.createQuery("select o from ReportType o where o.parentId=?1")
			.setParameter(1, typeId);
			List<ReportType> typeList = query.getResultList();
			if(typeList != null && typeList.size() >0){

				for(ReportType t : typeList){
					//删除当前节点
					Query delete = em.createQuery("delete from ReportType o where o.id=?1")
						.setParameter(1,t.getId());
					Integer s = delete.executeUpdate();
					
					if(t.getChildNodeNumber() >0){
						idList.add(t.getId());
						
					}
					
				}	
			}
		}
		if(idList != null && idList.size() >0){
			this.deleteChildNode(idList);
		}
	}
	
}
