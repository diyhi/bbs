package cms.service.redEnvelope;

import java.math.BigDecimal;
import java.util.List;

import cms.bean.QueryResult;
import cms.bean.payment.PaymentLog;
import cms.bean.redEnvelope.GiveRedEnvelope;
import cms.bean.redEnvelope.ReceiveRedEnvelope;
import cms.service.besa.DAO;

/**
 * 红包管理接口
 *
 */
public interface RedEnvelopeService extends DAO<GiveRedEnvelope> {
	/**
	 * 根据Id查询发红包
	 * @param giveRedEnvelopeId
	 * @return
	 */
	public GiveRedEnvelope findById(String giveRedEnvelopeId);
	/**
	 * 保存发红包
	 * @param giveRedEnvelope 发红包
	 * @param userName 用户名称
	 * @param amount 扣减用户预存款
	 * @param paymentLog 支付日志
	 */
	public void saveGiveRedEnvelope(GiveRedEnvelope giveRedEnvelope,String userName,BigDecimal amount,PaymentLog paymentLog);
	/**
	 * 删除发红包
	 * @param userIdList 用户Id集合
	 */
	public Integer deleteGiveRedEnvelope(List<Long> userIdList);
	/**
	 * 保存收红包
	 * 先由redEnvelopeManage.createReceiveRedEnvelopeObject();方法生成对象再保存
	 * @param receiveRedEnvelope 收红包
	 * @param giveRedEnvelopeId 发红包Id
	 * @param grabRedEnvelopeUserId 抢到红包的用户Id
	 */
	public int saveReceiveRedEnvelope(Object receiveRedEnvelope,String giveRedEnvelopeId,String grabRedEnvelopeUserId);
	/**
	 * 删除收红包
	 * @param userId 收红包用户Id
	 */
	public Integer deleteReceiveRedEnvelope(Long userId);
	/**
	 * 删除收红包
	 * @param userIdList 收红包用户Id集合
	 */
	public Integer deleteReceiveRedEnvelope(List<Long> userIdList);
	/**
	 * 返还红包
	 * @param giveRedEnvelope 发红包
	 * @param userName 用户名称
	 * @param amount 返还用户金额
	 * @param paymentLogObject 支付日志
	 */
	public void refundRedEnvelope(GiveRedEnvelope giveRedEnvelope,String userName,BigDecimal amount,Object paymentLogObject);
	/**
    * 拆红包
    * @param receiveRedEnvelope 收红包Id
    * @param amount 红包金额
    * @param version 版本号
    * @param userName 拆红包的用户名称
    * @param paymentLogObject 支付日志
    */
    public Integer unwrapRedEnvelope(String receiveRedEnvelopeId,BigDecimal amount,Integer version,String userName,Object paymentLogObject);
	/**
	 * 根据Id查询收红包
	 * @param receiveRedEnvelopeId 收红包Id
	 * @return
	 */
	public ReceiveRedEnvelope findByReceiveRedEnvelopeId(String receiveRedEnvelopeId);
	/**
	 * 根据用户Id查询收红包分页
	 * @param userId 用户Id
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public QueryResult<ReceiveRedEnvelope> findReceiveRedEnvelopeByUserId(Long userId,int firstIndex, int maxResult);
}
