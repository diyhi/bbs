package cms.repository.report;


import cms.model.report.ReportType;
import cms.repository.besa.DAO;

import java.util.List;

/**
 * 举报分类管理接口
 *
 */
public interface ReportTypeRepository extends DAO<ReportType> {
	
	/** 
	 * 根据Id查询分类
	 * @param reportTypeId 分类Id
	 * @return
	 */
	public ReportType findById(String reportTypeId);
	/**
	 * 根据分类查询所有父分类
	 * @param reportType 分类
	 * @return
	 */
	public List<ReportType> findAllParentById(ReportType reportType);
	
	/**
	 * 根据分类Id查询子分类(下一节点)
	 * @param reportTypeId 分类Id
	 * @return
	 */
	public List<ReportType> findChildTypeById(String reportTypeId);
	
	/**
	 * 查询所有举报分类
	 * @return
	 */
	public List<ReportType> findAllReportType();
	
	/**
	 * 查询所有举报分类 - 缓存
	 * @return
	 */
	public List<ReportType> findAllReportType_cache();

	
	/**
	 * 保存分类
	 * @param reportType 举报分类
	 */
	public void saveReportType(ReportType reportType);
	

	/**
	 * 修改分类
	 * @param reportType 举报分类
	 * @return
	 */
	public Integer updateReportType(ReportType reportType);
	
	/**
	 * 删除分类
	 * @param reportType 分类
	 */
	public Integer deleteReportType(ReportType reportType);
}
