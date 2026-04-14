package cms.service.report;

import cms.dto.PageView;
import cms.dto.report.ReportRequest;
import cms.model.report.Report;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 举报服务
 */
public interface ReportService {

    /**
     * 获取举报列表
     * @param page 页码
     * @param visible 是否可见
     * @param request 请求信息
     */
    public PageView<Report> getReportList(int page, Boolean visible, HttpServletRequest request);
    /**
     * 获取话题举报列表
     * @param page 页码
     * @param parameterId 参数Id
     * @param module 模块
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getTopicReportList(int page,String parameterId,Integer module,Long topicId, HttpServletRequest request);
    /**
     * 获取问题举报列表
     * @param page 页码
     * @param parameterId 参数Id
     * @param module 模块
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getQuestionReportList(int page,String parameterId,Integer module,Long questionId, HttpServletRequest request);
    /**
     * 获取用户举报列表
     * @param page 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getUserReportList(int page,String userName, HttpServletRequest request);
    /**
     * 举报处理
     * @param reportRequest 举报表单
     */
    public void reportHandle(ReportRequest reportRequest);
    /**
     * 获取修改举报界面信息
     * @param reportId 举报Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getEditReportViewModel(Long reportId, HttpServletRequest request);
    /**
     * 修改举报
     * @param reportRequest 举报表单
     * @param imageFile 图片
     * @param request 请求信息
     */
    public void editReport(ReportRequest reportRequest, MultipartFile[] imageFile, HttpServletRequest request);
    /**
     * 删除举报
     * @param reportId 举报Id集合
     */
    public void deleteReport(Long[] reportId);
    /**
     * 还原举报
     * @param reportId 举报Id集合
     */
    public void reductionReport(Long[] reportId);
}
