package cms.controller.report;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.report.ReportTypeRequest;
import cms.model.report.ReportType;
import cms.service.report.ReportTypeService;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 举报分类管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/reportType/manage")
public class ReportTypeManageController {
    @Resource
    ReportTypeService reportTypeService;
    @Resource
    MessageSource messageSource;

    /**
     * 举报分类 添加界面显示
     * @param parentId 父Id
     * @return
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(String parentId){
        Map<String,Object> returnValue = reportTypeService.getAddReportTypeViewModel(parentId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 举报分类 添加
     * @param reportTypeRequest 举报分类表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute ReportTypeRequest reportTypeRequest, BindingResult result) {

        reportTypeService.addReportType(reportTypeRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 举报分类 显示修改
     * @param reportTypeId 举报分类Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(String reportTypeId) {
        Map<String,Object> returnValue = reportTypeService.getEditReportTypeViewModel(reportTypeId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 举报分类 修改
     * @param reportTypeRequest 举报分类表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute ReportTypeRequest reportTypeRequest, BindingResult result) {
        reportTypeService.editReportType(reportTypeRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 举报分类 删除
     * @param reportTypeId 举报分类Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(String reportTypeId) {
        reportTypeService.deleteReportType(reportTypeId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 查询所有举报分类
     * @return
     */
    @RequestMapping(params="method=allType", method=RequestMethod.GET)
    public RequestResult queryAllType(){
        List<ReportType> list = reportTypeService.getAllType();
        return new RequestResult(ResultCode.SUCCESS, list);
    }

}
