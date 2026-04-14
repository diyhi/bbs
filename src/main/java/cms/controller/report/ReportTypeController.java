package cms.controller.report;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.sms.SmsInterface;
import cms.service.report.ReportTypeService;
import cms.service.sms.SmsInterfaceService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 举报分类控制器
 *
 */
@RestController
public class ReportTypeController {
    @Resource
    ReportTypeService reportTypeService;
    /**
     * 举报分类列表
     * @param pageForm 页码
     * @param parentId 父Id
     * @return
     */
    @RequestMapping("/control/reportType/list")
    public RequestResult reportTypeList(PageForm pageForm,String parentId){
        Map<String,Object> returnValue = reportTypeService.getReportTypeList(pageForm.getPage(),parentId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}
