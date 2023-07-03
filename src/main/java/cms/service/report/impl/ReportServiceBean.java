package cms.service.report.impl;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.report.Report;
import cms.bean.topic.Topic;
import cms.bean.user.UserLoginLog;
import cms.service.besa.DaoSupport;
import cms.service.report.ReportService;

/**
 * 举报管理接口实现类
 *
 */
@Service
@Transactional
public class ReportServiceBean extends DaoSupport<Report> implements ReportService{
	private static final Logger logger = LogManager.getLogger(ReportServiceBean.class);
		
	
	/**
	 * 根据Id查询举报
	 * @param reportId 举报Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Report findById(Long reportId){
		Query query = em.createQuery("select o from Report o where o.id=?1")
		.setParameter(1, reportId);
		List<Report> list = query.getResultList();
		for(Report p : list){
			return p;
		}
		return null;
	}
	/**
	 * 根据Id集合查询举报
	 * @param reportIdList 举报Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Report> findByIdList(List<Long> reportIdList){
		Query query = em.createQuery("select o from Report o where o.id in(:reportIdList)")
		.setParameter("reportIdList", reportIdList);
		return query.getResultList();
	}
	
	/**
	 * 根据参数条件查询举报是否存在
	 * @param userName 用户名称
	 * @param parameterId 参数Id
	 * @param module 模块
	 * @param status 状态
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public boolean findByCondition(String userName,String parameterId,Integer module,Integer status){
		Query query = em.createQuery("select o from Report o where o.parameterId=?1 and o.userName=?2 and o.module=?3 and o.status=?4")
			.setParameter(1, parameterId)
			.setParameter(2, userName)
			.setParameter(3, module)
			.setParameter(4, status);
		//索引开始,即从哪条记录开始
		query.setFirstResult(0);
		//获取多少条数据
		query.setMaxResults(1);
		List<Report> list = query.getResultList();
		if(list != null && list.size() >0){
			for(Report report : list){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 待处理举报数量
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Long reportCount(){
		Query query = em.createQuery("select count(o) from Report o where o.status=?1");
		query.setParameter(1, 10);
		return (Long)query.getSingleResult();
	}
	
	/**
	 * 保存举报
	 * @param report
	 */
	public void saveReport(Report report){
		this.save(report);
	}
	
	/**
	 * 修改举报分类Id
	 * @param old_tagId 旧分类Id
	 * @param new_tagId 新分类Id
	 * @return
	 */
	public Integer updateTypeId(String old_typeId,String new_typeId){
		Query query = em.createQuery("update Report o set o.reportTypeId=?1 where o.reportTypeId=?2")
		.setParameter(1, old_typeId)
		.setParameter(2, new_typeId);
		return query.executeUpdate();
	}
	
	/**
	 * 修改举报信息
	 * @param reportId 举报Id
	 * @param status 状态
	 * @param processResult 处理结果
	 * @param remark 备注
	 * @param processCompleteTime 处理完成时间
	 * @param staffAccount 员工账号
	 * @param version 版本号
	 * @return
	 */
	public int updateReportInfo(Long reportId, Integer status, String processResult,String remark,Date processCompleteTime,String staffAccount,Integer version){
		Query query = em.createQuery("update Report o set o.status=?1,o.processResult=?2,o.remark=?3,o.processCompleteTime=?4,o.staffAccount=?5, o.version=o.version+1 where o.id=?6 and o.version=?7")
				.setParameter(1, status)
				.setParameter(2, processResult)
				.setParameter(3, remark)
				.setParameter(4, processCompleteTime)
				.setParameter(5, staffAccount)
				.setParameter(6, reportId)
				.setParameter(7, version);
		return query.executeUpdate();
		
	}
	
	/**
	 * 修改举报信息
	 * @param reportId 举报Id
	 * @param reportTypeId 举报分类Id
	 * @param status 状态
	 * @param processResult 处理结果
	 * @param reason 理由
	 * @param remark 备注
	 * @param processCompleteTime 处理完成时间 
	 * @param staffAccount 员工账号
	 * @param imageData 图片数据
	 * @param version 版本号
	 * @return
	 */
	public int updateReportInfo(Long reportId, String reportTypeId,Integer status, String processResult,String reason,String remark,Date processCompleteTime,String staffAccount,String imageData,Integer version){
		Query query = em.createQuery("update Report o set o.reportTypeId=?1, o.status=?2,o.processResult=?3,o.reason=?4,o.remark=?5,o.processCompleteTime=?6,o.staffAccount=?7, o.imageData=?8, o.version=o.version+1 where o.id=?9 and o.version=?10")
				.setParameter(1, reportTypeId)
				.setParameter(2, status)
				.setParameter(3, processResult)
				.setParameter(4, reason)
				.setParameter(5, remark)
				.setParameter(6, processCompleteTime)
				.setParameter(7, staffAccount)
				.setParameter(8, imageData)
				.setParameter(9, reportId)
				.setParameter(10, version);
		return query.executeUpdate();
		
	}

	
	/**
	 * 标记删除举报
	 * @param reportId 举报Id
	 * @param mark 标记  用户删除1000   员工删除100000
	 * @return
	 */
	public Integer markDelete(Long reportId,Integer mark){
		Query query = em.createQuery("update Report o set o.status=o.status+?1 where o.id=?2")
		.setParameter(1, mark)
		.setParameter(2, reportId);
		return query.executeUpdate();
		
	}
	/**
	 * 删除举报
	 * @param reportId 举报Id
	 * @return
	 */
	public Integer deleteReport(Long reportId){
		Query delete = em.createQuery("delete from Report o where o.id=?1")
		.setParameter(1, reportId);
		return delete.executeUpdate();
	}
	
	/**
	 * 还原举报
	 * @param reportId 举报Id
	 * @param status 状态
	 * @return
	 */
	public Integer reductionReport(Long reportId,Integer status){
		Query query = em.createQuery("update Report o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, reportId);
		return query.executeUpdate();
	}
}
