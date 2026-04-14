package cms.service.payment;

import cms.dto.PageView;
import cms.model.payment.PaymentVerificationLog;

import java.util.Map;

/**
 * 支付日志服务
 */
public interface PaymentLogService {
    /**
     * 获取支付日志列表
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getPaymentLogList(int page, String userName,String fileServerAddress);
    /**
     * 获取支付日志明细
     * @param paymentRunningNumber 支付流水号
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getPaymentLogDetails(String paymentRunningNumber,Long id,String fileServerAddress);

    /**
     * 支付校验日志分页
     * @param page 页码
     * @param paymentModule 支付模块 1.订单支付 3.售后服务换货/返修支付 5.用户充值
     * @param parameterId 参数Id
     * @param userName 用户名称
     * @return
     */
    public PageView<PaymentVerificationLog> getPaymentVerificationLogPage(int page, Integer paymentModule, Long parameterId, String userName);

}