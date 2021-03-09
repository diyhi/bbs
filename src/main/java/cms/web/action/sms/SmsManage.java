package cms.web.action.sms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.setting.SystemSetting;
import cms.bean.sms.Alidayu;
import cms.bean.sms.SendService;
import cms.bean.sms.SendSmsLog;
import cms.bean.sms.SmsInterface;
import cms.service.setting.SettingService;
import cms.service.sms.SmsService;
import cms.utils.JsonUtils;

/**
 * 短信管理
 *
 */
@Component("smsManage")
public class SmsManage {
	private static final Logger logger = LogManager.getLogger(SmsManage.class);
	@Resource SmsService smsService;//通过接口引用代理返回的对象
	
	@Resource SmsManage smsManage;
	@Resource SettingService settingService;
	
	//产品名称:云通信短信API产品,开发者无需替换
	private static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
	private static final String domain = "dysmsapi.aliyuncs.com";
	
	/**
	 * 生成短信发送服务
	 * @param interfaceProduct 接口产品
	 * @return
	 */
	public List<SendService> createSendService(Integer interfaceProduct){
		List<SendService> sendServiceList = new ArrayList<SendService>();
		if(interfaceProduct.equals(1)){//阿里大于
			SendService sendService_1 = new SendService();
			sendService_1.setInterfaceProduct(interfaceProduct);
			sendService_1.setServiceId(1);
			sendService_1.setServiceName("绑定手机服务");
			/** 支持变量 key:变量字段  value:备注 **/
			Map<String,String> variable_1 = new LinkedHashMap<String,String>();
			variable_1.put("${number}", "短信验证码");
			sendService_1.setAlidayu_variable(variable_1);
			sendServiceList.add(sendService_1);
			/**
			SendService sendService_10 = new SendService();
			sendService_10.setInterfaceProduct(interfaceProduct);
			sendService_10.setServiceId(10);
			sendService_10.setServiceName("短信营销服务");
			sendServiceList.add(sendService_10);
			**/
		}else if(interfaceProduct.equals(10)){//云片
			
		}
		return sendServiceList;
	}
	
	
	/**
	 * 发送短信验证码
	 * @param platformUserId 平台用户Id
	 * @param mobile 手机号
	 * @param code 验证码
	 * @return
	 */
	public String sendSms_code(String platformUserId,String mobile,String code){

		String errorInfo = null;
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getUserSentSmsCount() != null && systemSetting.getUserSentSmsCount() >0){
			int count = smsManage.addSentSmsCount(platformUserId, 0);//先查询发送次数
			if(count >= systemSetting.getUserSentSmsCount()){
				errorInfo = "超出每用户每24小时内发送短信限制次数";
			}
		}
		if(errorInfo == null){//如果没有错误
			SmsInterface smsInterface = smsService.findEnableInterface_cache();
			if(smsInterface != null){
				if(smsInterface.getInterfaceProduct().equals(1)){//1.阿里大于
					String accessKeyId=null;
					String accessKeySecret=null;
					//阿里大于短信签名
					String alidayu_signName=null;
					//阿里大于短信模板代码
					String alidayu_templateCode=null;
					
					Integer serviceId = null;
					
					if(smsInterface.getDynamicParameter() != null && !"".equals(smsInterface.getDynamicParameter().trim())){
						Alidayu alidayu = JsonUtils.toObject(smsInterface.getDynamicParameter(), Alidayu.class);
						if(alidayu != null){
							accessKeyId = alidayu.getAccessKeyId();
							accessKeySecret=alidayu.getAccessKeySecret();
						}
						
						
					}
					if(smsInterface.getSendService() != null && !"".equals(smsInterface.getSendService().trim())){
						List<SendService> _sendServiceList = JsonUtils.toGenericObject(smsInterface.getSendService(), new TypeReference< List<SendService> >(){});
						if(_sendServiceList != null && _sendServiceList.size() >0){
							for(SendService _sendService : _sendServiceList){
								if(_sendService.getServiceId().equals(1)){//1.绑定手机服务
									alidayu_signName = _sendService.getAlidayu_signName();
									alidayu_templateCode = _sendService.getAlidayu_templateCode();
									serviceId = _sendService.getServiceId();
								}
							}
						}
					}
					
					if(accessKeyId == null || "".equals(accessKeyId)){
						errorInfo = "请配置用户密钥";
					}
					if(accessKeySecret == null || "".equals(accessKeySecret)){
						errorInfo = "请配置用户密钥";
					}
					if(alidayu_signName == null || "".equals(alidayu_signName)){
						errorInfo = "请配置短信签名";
					}
					if(alidayu_templateCode == null || "".equals(alidayu_templateCode)){
						errorInfo = "请配置短信模板代码";
					}
					
					if(errorInfo == null){//如果没有错误
						System.setProperty("sun.net.client.defaultConnectTimeout", "10000");//连接主机的超时时间（单位：毫秒)
				        System.setProperty("sun.net.client.defaultReadTimeout", "10000");//从主机读取数据的超时时间（单位：毫秒） 

				        //初始化acsClient,暂不支持region化
				        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
				        try {
							DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
						} catch (ClientException e) {
							errorInfo = "发送服务初始化错误";
							if (logger.isErrorEnabled()) {
					            logger.error("阿里大于初始化错误",e);
					        }
							// TODO Auto-generated catch block
						//	e.printStackTrace();
						}
				        IAcsClient acsClient = new DefaultAcsClient(profile);
						
				      //组装请求对象-具体描述见控制台-文档部分内容
				        SendSmsRequest request = new SendSmsRequest();
				        //必填:待发送手机号
				        request.setPhoneNumbers(mobile);
				        //必填:短信签名-可在短信控制台中找到
				        request.setSignName(alidayu_signName);
				        //必填:短信模板-可在短信控制台中找到
				        request.setTemplateCode(alidayu_templateCode);
				        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
				        request.setTemplateParam("{\"number\":\""+code+"\"}");

				        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
				        //request.setSmsUpExtendCode("90997");

				        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
				        request.setOutId(platformUserId);

				        //hint 此处可能会抛出异常，注意catch
				        try {
							SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
							if(sendSmsResponse != null){
								if(!"OK".equals(sendSmsResponse.getCode())){
									errorInfo = "短信发送错误";
									
									//错误日志
									SendSmsLog sendSmsLog = new SendSmsLog();
									sendSmsLog.setInterfaceProduct(smsInterface.getInterfaceProduct());//接口产品
									sendSmsLog.setServiceId(serviceId);//服务Id
									sendSmsLog.setPlatformUserId(platformUserId);;//平台用户Id
									sendSmsLog.setMobile(mobile);//手机
									sendSmsLog.setCode(sendSmsResponse.getCode());//状态码-返回OK代表请求成功,其他错误码详见错误码列表
									sendSmsLog.setMessage(sendSmsResponse.getMessage());//状态码描述
								//	sendSmsLog.setAlidayu_requestId(sendSmsResponse.getRequestId());//请求ID
								//	sendSmsLog.setAlidayu_bizId(sendSmsResponse.getBizId());//发送回执ID,可根据该ID查询具体的发送状态
									smsService.saveSendSmsLog(sendSmsLog);
								}else{
									int original = smsManage.addSentSmsCount(platformUserId, 0);//原来总次数
									smsManage.deleteSentSmsCount(platformUserId);
							    	//增加发送短信次数记录
									smsManage.addSentSmsCount(platformUserId,original+1);
								}
								
							}	
						} catch (ServerException e) {
							errorInfo = "发送服务错误";
							if (logger.isErrorEnabled()) {
					            logger.error("阿里大于错误",e);
					        }
					//		e.printStackTrace();
						} catch (ClientException e) {
							errorInfo = "发送服务错误";
							if (logger.isErrorEnabled()) {
					            logger.error("阿里大于错误",e);
					        }
							
						//	e.printStackTrace();
						}
						
					}
				}
			}else{
				errorInfo = "没有配置短信接口";
			}
			
		}

		
		
		
		return errorInfo;
	}
	
	/**
	 * 缓存增加用户发短信次数
	 * @param platformUserId 平台用户Id
	 * @param count 发送总数
	 */
	@Cacheable(value="smsManage_cache_sentSmsCount",key="#platformUserId")
	public Integer addSentSmsCount(String platformUserId,Integer count){
		return count;
	}
	/**
	 * 删除缓存用户发短信次数
	 * @param platformUserId 平台用户Id
	*/
	@CacheEvict(value="smsManage_cache_sentSmsCount",key="#platformUserId")
	public void deleteSentSmsCount(String platformUserId){
	} 
	
	
	
	/**
	 * 生成手机验证码标记
	 * @param module 模块 1.绑定手机  2.更换绑定手机第一步  3.更换绑定手机第二步   100.注册   200.登录 300.找回密码
	 * @param platformUserId 平台用户Id
	 * @param mobile 手机号
	 * @param mobile 验证码
	 * @return
	 */
	@Cacheable(value="smsManage_cache_smsCode",key="#module+'_'+#platformUserId+'_'+#mobile")
	public String smsCode_generate(Integer module,String platformUserId,String mobile,String smsCode){
		return smsCode;
	}
	/**
	 * 删除手机验证码标记
	 * @param module 模块
	 * @param platformUserId 平台用户Id
	 * @param mobile 手机号
	 * @return
	 */
	@CacheEvict(value="smsManage_cache_smsCode",key="#module+'_'+#platformUserId+'_'+#mobile")
	public void smsCode_delete(Integer module,String platformUserId,String mobile){
	}
	
	/**
	 * 更换绑定手机验证码标记
	 * @param userId 用户Id
	 * @param mobile 手机号
	 * @param allow 是否允许
	 * @return
	 */
	@Cacheable(value="smsManage_cache_replaceCode",key="#userId+'_'+#mobile")
	public boolean replaceCode_generate(Long userId,String mobile,boolean allow){
		return allow;
	}
	/**
	 * 删除绑定手机验证码标记
	 * @param module 模块
	 * @param userId 用户Id
	 * @param mobile 手机号
	 * @return
	 */
	@CacheEvict(value="smsManage_cache_replaceCode",key="#userId+'_'+#mobile")
	public void replaceCode_delete(Long userId,String mobile){
	}
}
