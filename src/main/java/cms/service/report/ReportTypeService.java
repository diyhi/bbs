package cms.service.report;


import java.util.List;
import cms.bean.report.ReportType;
import cms.service.besa.DAO;

/**
 * 举报分类管理接口
 *
 */
public interface ReportTypeService extends DAO<ReportType>{
	
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
	 * @param reportType
	 */
	public void saveReportType(ReportType reportType);
	

	/**
	 * 修改分类
	 * @param reportType
	 * @return
	 */
	public Integer updateReportType(ReportType reportType);
	
	/**
	 * 删除分类
	 * @param reportType 分类
	 */
	public Integer deleteReportType(ReportType reportType);
}
