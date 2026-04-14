package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.ClientRequestResult;
import cms.service.frontend.PaymentClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台支付控制器
 */
@RestController
public class PaymentController {
    @Resource
    PaymentClientService paymentClientService;


    /**
     * 付款表单
     * @param paymentModule 支付模块 5.用户充值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1160100)
    @RequestMapping(value="/user/control/payment", method= RequestMethod.GET)
    public Map<String,Object> paymentUI(Integer paymentModule,
                                        HttpServletRequest request){
        return paymentClientService.getPaymentViewModel(paymentModule,request);
    }

    /**
     * 支付校验
     * @param paymentModule 支付模块 5.用户充值
     * @param rechargeAmount 充值金额
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1160200)
    @RequestMapping(value="/user/control/paymentVerification", method={RequestMethod.GET})
    public ClientRequestResult paymentVerification(Integer paymentModule,String rechargeAmount) {
        paymentClientService.paymentVerification(paymentModule,rechargeAmount);
        return ClientRequestResult.success();
    }


    /**
     * 付款
     * @param paymentModule 支付模块 5.用户充值
     * @param rechargeAmount 充值金额
     * @param paymentBank 支付银行 由 接口产品_银行简码 组成
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1160300)
    @RequestMapping(value="/user/control/payment", method=RequestMethod.POST)
    public ClientRequestResult payment(Integer paymentModule,String rechargeAmount, String paymentBank,
                          HttpServletRequest request,HttpServletResponse response) {
        Map<String,Object> returnValue = paymentClientService.payment(paymentModule,rechargeAmount,paymentBank,request);
        return ClientRequestResult.success().addAll(returnValue);
    }


    /**
     * 支付回调通知
     * @param interfaceProduct 接口产品
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1160400)
    @RequestMapping(value="/notify/{interfaceProduct}", method={RequestMethod.POST})
    public String notify(@PathVariable Integer interfaceProduct,
                         HttpServletRequest request, HttpServletResponse response) {
        return paymentClientService.notify(interfaceProduct,request);
    }

    /**
     * 支付完成通知
     * @param interfaceProduct 接口产品 0:账户支付
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1160500)
    @RequestMapping(value="/paymentCompleted/{interfaceProduct}/{paymentModule}/{parameterId}", method={RequestMethod.GET})
    public ClientRequestResult paySuccess(@PathVariable Integer interfaceProduct,
                             HttpServletRequest request){
        paymentClientService.paySuccess(interfaceProduct,request);
        return ClientRequestResult.success();
    }

}
