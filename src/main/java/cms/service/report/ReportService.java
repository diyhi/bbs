package cms.service.report;


import java.util.Date;
import java.util.List;

import cms.bean.report.Report;
import cms.service.besa.DAO;

/**
 * 举报管理接口
 *
 */
public interface ReportService extends DAO<Report> {
	
	/**
	 * 根据Id查询举报
	 * @param reportId 举报Id
	 * @return
	 */
	public Report findById(Long reportId);
	
	/**
	 * 根据Id集合查询举报
	 * @param reportIdList 举报Id集合
	 * @return
	 */
	public List<Report> findByIdList(List<Long> reportIdList);
	/**
	 * 根据参数条件查询举报是否存在
	 * @param userName 用户名称
	 * @param parameterId 参数Id
	 * @param module 模块
	 * @param status 状态
	 * @return
	 */
	public boolean findByCondition(String userName,String parameterId,Integer module,Integer status);
	/**
	 * 待处理举报数量
	 * @return
	 */
	public Long reportCount();
	
	/**
	 * 保存举报
	 * @param report
	 */
	public void saveReport(Report report);
	/**
	 * 修改举报分类Id
	 * @param old_tagId 旧分类Id
	 * @param new_tagId 新分类Id
	 * @return
	 */
	public Integer updateTypeId(String old_typeId,String new_typeId);
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
	public int updateReportInfo(Long reportId, Integer status, String processResult,String remark,Date processCompleteTime,String staffAccount,Integer version);
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
	public int updateReportInfo(Long reportId, String reportTypeId,Integer status, String processResult,String reason,String remark,Date processCompleteTime,String staffAccount,String imageData,Integer version);
	/**
	 * 标记删除举报
	 * @param reportId 举报Id
	 * @param mark 标记  用户删除1000   员工删除100000
	 * @return
	 */
	public Integer markDelete(Long reportId,Integer mark);
	/**
	 * 删除举报
	 * @param reportId 举报Id
	 * @return
	 */
	public Integer deleteReport(Long reportId);
	/**
	 * 还原举报
	 * @param reportId 举报Id
	 * @param status 状态
	 * @return
	 */
	public Integer reductionReport(Long reportId,Integer status);
}
