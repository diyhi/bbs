package cms.controller.payment;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.payment.OnlinePaymentInterfaceService;
import cms.service.payment.PaymentLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付日志列表控制器
 * @author Administrator
 *
 */
@RestController
public class PaymentLogController {
    @Resource
    PaymentLogService paymentLogService;
    @Resource
    FileComponent fileComponent;
    /**
     * 支付日志列表
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/paymentLog/list")
    public RequestResult paymentLogList(PageForm pageForm, String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = paymentLogService.getPaymentLogList(pageForm.getPage(), userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}
