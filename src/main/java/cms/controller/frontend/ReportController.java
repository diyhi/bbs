package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.ReportDTO;
import cms.model.report.Report;
import cms.service.frontend.ReportClientService;
import cms.utils.IpAddress;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 前台举报控制器
 */
@RestController("frontendReportController")
public class ReportController {
    @Resource
    ReportClientService reportClientService;
    @Resource
    FileComponent fileComponent;

    /**
     * 添加举报表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6130100)
    public Map<String,Object> addReportUI(){
        return reportClientService.getAddReportViewModel();
    }

    /**
     * 举报  添加
     * @param reportDTO 举报表单
     * @param result 存储校验信息
     * @param imageFile 图片文件
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1130100)
    @RequestMapping(value="/user/control/report/add", method= RequestMethod.POST)
    public Map<String,Object> add(@ModelAttribute ReportDTO reportDTO, BindingResult result,MultipartFile[] imageFile,
                      HttpServletRequest request, HttpServletResponse response){
        String ip = IpAddress.getClientIpAddress(request);
        return reportClientService.addReport(reportDTO,ip,imageFile);
    }

    /**
     * 举报列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1130200)
    @RequestMapping(value="/user/control/reportList",method=RequestMethod.GET)
    public PageView<Report> reportUI(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return reportClientService.getReportList(pageForm.getPage(),fileServerAddress);
    }
}
