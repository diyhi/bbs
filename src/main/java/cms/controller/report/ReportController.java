package cms.controller.report;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.report.Report;
import cms.service.report.ReportService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 举报控制器
 *
 */
@RestController
public class ReportController {
    @Resource
    ReportService reportService;
    /**
     * 举报列表
     * @param pageForm 页码
     * @param visible 是否可见
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/report/list")
    public RequestResult reportTypeList(PageForm pageForm,Boolean visible, HttpServletRequest request){
        PageView<Report> pageView = reportService.getReportList(pageForm.getPage(),visible, request);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 话题举报列表
     * @param pageForm 页码
     * @param parameterId 参数Id
     * @param module 模块
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topicReport/list")
    public RequestResult topicReportList(PageForm pageForm,String parameterId,Integer module,Long topicId, HttpServletRequest request){
        Map<String,Object> returnValue = reportService.getTopicReportList(pageForm.getPage(),parameterId,module,topicId, request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 问题举报列表
     * @param pageForm 页码
     * @param parameterId 参数Id
     * @param module 模块
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/questionReport/list")
    public RequestResult questionReportList(PageForm pageForm,String parameterId,Integer module,Long questionId, HttpServletRequest request){
        Map<String,Object> returnValue = reportService.getQuestionReportList(pageForm.getPage(),parameterId,module,questionId, request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 用户举报列表
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/userReport/list")
    public RequestResult userReportList(PageForm pageForm,String userName, HttpServletRequest request){
        Map<String,Object> returnValue = reportService.getUserReportList(pageForm.getPage(),userName, request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}
