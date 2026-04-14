package cms.controller.payment;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.payment.PaymentVerificationLog;
import cms.service.payment.PaymentLogService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付日志管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/paymentLog/manage")
public class PaymentLogManageController {
    @Resource
    PaymentLogService paymentLogService;
    @Resource
    FileComponent fileComponent;

    /**
     * 支付日志管理 详细显示
     * @param paymentRunningNumber 支付流水号
     * @param id 用户Id
     * @return
     */
    @RequestMapping(params="method=show",method= RequestMethod.GET)
    public RequestResult show(String paymentRunningNumber,Long id, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = paymentLogService.getPaymentLogDetails(paymentRunningNumber,id,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 支付校验日志分页
     * @param pageForm 页码
     * @param paymentModule 支付模块 1.订单支付 3.售后服务换货/返修支付 5.用户充值
     * @param parameterId 参数Id
     * @param userName 用户名称
     * @return
     */
    @RequestMapping(params="method=ajax_paymentVerificationLogPage", method=RequestMethod.GET)
    public RequestResult paymentVerificationLogPage(PageForm pageForm,
                                             Integer paymentModule, Long parameterId, String userName) {
        PageView<PaymentVerificationLog> pageView =  paymentLogService.getPaymentVerificationLogPage(pageForm.getPage(),paymentModule, parameterId, userName);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
