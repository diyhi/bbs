package cms.controller.report;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.report.ReportRequest;
import cms.dto.report.ReportTypeRequest;
import cms.model.report.ReportType;
import cms.service.report.ReportService;
import cms.service.report.ReportTypeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 举报管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/report/manage")
public class ReportManageController {
    @Resource
    ReportService reportService;
    @Resource
    MessageSource messageSource;

    /**
     * 举报处理
     * @param reportRequest 举报表单
     * @return
     */
     @RequestMapping(params="method=reportHandle",method=RequestMethod.POST)
     public RequestResult reportHandle(@ModelAttribute ReportRequest reportRequest, BindingResult result) {
         reportService.reportHandle(reportRequest);
         return new RequestResult(ResultCode.SUCCESS, null);
     }

    /**
     * 举报 显示修改
     * @param reportId 举报Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Long reportId, HttpServletRequest request) {
        Map<String,Object> returnValue = reportService.getEditReportViewModel(reportId,request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 举报 修改
     * @param reportRequest 举报表单
     * @param result 存储校验信息
     * @param imageFile 图片文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute ReportRequest reportRequest, BindingResult result, MultipartFile[] imageFile, HttpServletRequest request) {
        reportService.editReport(reportRequest,imageFile,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 举报 删除
     * @param reportId 举报Id集合
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Long[] reportId) {
        reportService.deleteReport(reportId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 还原
     * @param reportId 举报Id集合
     * @return
     */
    @RequestMapping(params="method=reduction",method=RequestMethod.POST)
    public RequestResult reduction(Long[] reportId) {
        reportService.reductionReport(reportId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

}
