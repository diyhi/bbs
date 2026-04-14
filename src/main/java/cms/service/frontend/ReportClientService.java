package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.frontendModule.ReportDTO;
import cms.model.report.Report;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 前台举报服务接口
 */
public interface ReportClientService {
    /**
     * 获取举报界面信息
     * @return
     */
    public Map<String,Object> getAddReportViewModel();

    /**
     * 添加举报
     * @param reportDTO 举报表单
     * @param ip IP地址
     * @param imageFile 图片文件
     * @return
     */
    public Map<String,Object> addReport(ReportDTO reportDTO,String ip, MultipartFile[] imageFile);
    /**
     * 获取举报列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Report> getReportList(int page,String fileServerAddress);
}
