package cms.service.report;

import cms.dto.report.ReportTypeRequest;
import cms.model.report.ReportType;

import java.util.List;
import java.util.Map;

/**
 * 举报分类服务
 */
public interface ReportTypeService {

    /**
     * 获取举报分类列表
     * @param page 页码
     * @param parentId 父Id
     */
    public Map<String,Object> getReportTypeList(int page,String parentId);
    /**
     * 获取添加举报分类界面信息
     * @param parentId 父Id
     * @return
     */
    public Map<String,Object> getAddReportTypeViewModel(String parentId);
    /**
     * 添加举报分类
     * @param reportTypeRequest 举报分类表单
     */
    public void addReportType(ReportTypeRequest reportTypeRequest);
    /**
     * 获取修改举报分类界面信息
     * @param reportTypeId 举报分类Id
     * @return
     */
    public Map<String,Object> getEditReportTypeViewModel(String reportTypeId);
    /**
     * 修改举报分类
     * @param reportTypeRequest 举报分类表单
     */
    public void editReportType(ReportTypeRequest reportTypeRequest);
    /**
     * 删除举报分类
     * @param reportTypeId 举报分类Id
     */
    public void deleteReportType(String reportTypeId);
    /**
     * 查询所有举报分类
     * @return
     */
    public List<ReportType> getAllType();
}
