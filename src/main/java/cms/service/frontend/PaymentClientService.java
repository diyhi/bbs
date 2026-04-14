package cms.service.frontend;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 前台支付服务接口
 */
public interface PaymentClientService {
    /**
     * 获取付款界面信息
     * @param paymentModule 支付模块 5.用户充值
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getPaymentViewModel(Integer paymentModule, HttpServletRequest request);

    /**
     * 支付校验
     * @param paymentModule 支付模块 5.用户充值
     * @param rechargeAmount 充值金额
     */
    public void paymentVerification(Integer paymentModule,String rechargeAmount);
    /**
     * 付款
     * @param paymentModule 支付模块 5.用户充值
     * @param rechargeAmount 充值金额
     * @param paymentBank 支付银行 由 接口产品_银行简码 组成
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> payment(Integer paymentModule,String rechargeAmount, String paymentBank, HttpServletRequest request);
    /**
     * 支付回调通知
     * @param interfaceProduct 接口产品
     * @param request 请求信息
     * @return
     */
    public String notify(Integer interfaceProduct,HttpServletRequest request);
    /**
     * 支付完成通知
     * @param interfaceProduct 接口产品 0:账户支付
     * @param request 请求信息
     * @return
     */
    public void paySuccess(Integer interfaceProduct,HttpServletRequest request);
}
