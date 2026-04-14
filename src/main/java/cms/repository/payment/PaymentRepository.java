package cms.repository.payment;

import cms.dto.QueryResult;
import cms.model.payment.OnlinePaymentInterface;
import cms.model.payment.PaymentLog;
import cms.model.payment.PaymentVerificationLog;
import cms.repository.besa.DAO;

import java.util.List;


/**
 * 支付管理接口
 *
 */
public interface PaymentRepository extends DAO<OnlinePaymentInterface> {
	/**
	 * 根据Id查询在线支付接口
	 * @param onlinePaymentInterfaceId 在线支付接口Id
	 * @return
	 */
	public OnlinePaymentInterface findOnlinePaymentInterfaceById(Integer onlinePaymentInterfaceId);
	/**
	 * 查询所有在线支付接口
	 * @return
	 */
	public List<OnlinePaymentInterface> findAllOnlinePaymentInterface();
	/**
	 * 查询所有有效的在线支付接口
	 * @return
	
	public List<OnlinePaymentInterface> findAllEffectiveOnlinePaymentInterface(); */
	/**
	 * 查询所有有效的在线支付接口
	 * @return
	 */
	public List<OnlinePaymentInterface> findAllEffectiveOnlinePaymentInterface_cache();
	/**
	 * 保存在线支付接口
	 * @param onlinePaymentInterface 在线支付接口
	 */
	public void saveOnlinePaymentInterface(OnlinePaymentInterface onlinePaymentInterface);
	/**
	 * 修改在线支付接口
	 * @param onlinePaymentInterface 在线支付接口
	 */
	public void updateOnlinePaymentInterface(OnlinePaymentInterface onlinePaymentInterface);
	
	
	
	/**
	 * 删除在线支付接口
	 * @param onlinePaymentInterfaceId 在线支付接口Id
	 */
	public Integer deleteOnlinePaymentInterface(Integer onlinePaymentInterfaceId);
	
	
	
	/**
	 * 根据Id查询支付校验日志
	 * @param paymentVerificationLogId 支付流水号
	 * @return
	 */
	public PaymentVerificationLog findPaymentVerificationLogById(String paymentVerificationLogId);
	
	/**
	 * 保存支付校验日志
	 * @param paymentVerificationLog 支付校验日志
	 */
	public void savePaymentVerificationLog(PaymentVerificationLog paymentVerificationLog);
	
	/**
	 * 根据Id删除支付校验日志
	 * @param paymentVerificationLogId 支付流水号
	 */
	public Integer deletePaymentVerificationLogById(String paymentVerificationLogId);

	/**
	 * 保存支付日志
	 * 先由paymentManage.createPaymentLogObject();方法生成对象再保存
	 * @param paymentLog 支付日志
	 */
	public void savePaymentLog(Object paymentLog);
	/**
	 * 根据支付流水号查询支付日志
	 * @param paymentRunningNumber 支付流水号
	 */
	public PaymentLog findPaymentLogByPaymentRunningNumber(String paymentRunningNumber);
	/**
	 * 支付日志分页
	 * @param userId 用户Id
	 * @param userName 用户名称
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 */
	public QueryResult<PaymentLog> findPaymentLogPage(Long userId, String userName, int firstIndex, int maxResult);
}
