package cms.service.sms;

import java.util.List;

import cms.bean.sms.SendSmsLog;
import cms.bean.sms.SmsInterface;
import cms.service.besa.DAO;

/**
 * 短信管理接口
 *
 */
public interface SmsService  extends DAO<SmsInterface>{
	/**
	 * 根据Id查询短信接口
	 * @param smsInterfaceId 短信接口Id
	 * @return
	 */
	public SmsInterface findSmsInterfaceById(Integer smsInterfaceId);
	/**
	 * 查询所有短信接口
	 * @return
	 */
	public List<SmsInterface> findAllSmsInterface();
	/**
	 * 保存短信接口
	 * @param smsInterface 短信接口
	 */
	public void saveSmsInterface(SmsInterface smsInterface);
	/**
	 * 修改短信接口
	 * @param smsInterface 短信接口
	 */
	public Integer updateSmsInterface(SmsInterface smsInterface);
	/**
	 * 删除短信接口
	 * @param smsInterface 短信Id
	 */
	public Integer deleteSmsInterface(Integer smsInterfaceId);
	/**
	 * 修改使用的短信接口
	 * @param smsInterfaceId 短信接口Id
	 * @param version 版本
	 */
	public Integer updateEnableInterface(Integer smsInterfaceId,Integer version);
	/**
	 * 查询启用的短信接口
	 * @return
	 */
	public SmsInterface findEnableInterface();
	/**
	 * 查询启用的短信接口 - 缓存
	 * @return
	 */
	public SmsInterface findEnableInterface_cache();
	/**
	 * 保存发送错误日志
	 * @param sendSmsLog 发送错误日志
	 */
	public void saveSendSmsLog(SendSmsLog sendSmsLog);
}
