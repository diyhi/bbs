package cms.web.action.sms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.setting.SystemSetting;
import cms.bean.sms.Alidayu;
import cms.bean.sms.SendService;
import cms.bean.sms.SendSmsLog;
import cms.bean.sms.SmsInterface;
import cms.service.setting.SettingService;
import cms.service.sms.SmsService;
import cms.utils.JsonUtils;
import darabonba.core.client.ClientOverrideConfiguration;

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
			variable_1.put("${code}", "短信验证码");
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
	 * @param countryCode 区号 国外号码含有+号，国内号码为空
	 * @param mobile 手机号
	 * @param code 验证码
	 * @return
	 */
	public String sendSms_code(String platformUserId,String countryCode,String mobile,String code){
		String errorInfo = null;

		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getUserSentSmsCount() != null && systemSetting.getUserSentSmsCount() >0){
			int count = smsManage.addSentSmsCount(platformUserId, 0);//先查询发送次数
			if(count >= systemSetting.getUserSentSmsCount()){
				errorInfo = "超出每用户每24小时内发送短信限制次数";//超出每用户每24小时内发送短信限制次数
			}
		}
		if(errorInfo == null){//如果没有错误
			SmsInterface smsInterface = smsService.findEnableInterface_cache();
			if(smsInterface != null){
				if(smsInterface.getInterfaceProduct().equals(1)){//1.阿里大于
					String accessKeyId=null;
					String accessKeySecret=null;
					//阿里云国内短信签名
					String aliyun_signName=null;
					//阿里云国内短信模板代码
					String aliyun_templateCode=null;
					//阿里云国际短信签名
					String aliyun_internationalSignName=null;
					//阿里云国际短信模板代码
					String aliyun_internationalTemplateCode=null;
					
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
								if(_sendService.getServiceId().equals(1) && smsInterface.isEnable()){//1.绑定手机服务
									aliyun_signName = _sendService.getAlidayu_signName();
									aliyun_templateCode = _sendService.getAlidayu_templateCode();
									aliyun_internationalSignName = _sendService.getAlidayu_internationalSignName();
									aliyun_internationalTemplateCode = _sendService.getAlidayu_internationalTemplateCode();
									
									serviceId = _sendService.getServiceId();
								}
							}
						}
					}
					
					if(accessKeyId == null || "".equals(accessKeyId)){
						errorInfo = "请配置用户密钥";//请配置用户密钥
					}
					if(accessKeySecret == null || "".equals(accessKeySecret)){
						errorInfo = "请配置用户密钥";//请配置用户密钥
					}
					if(countryCode == null || "".equals(countryCode.trim())){//国内
						if(aliyun_signName == null || "".equals(aliyun_signName.trim())){
							errorInfo = "请配置国内短信签名";//请配置国内短信签名
						}
						if(aliyun_templateCode == null || "".equals(aliyun_templateCode.trim())){
							errorInfo = "请配置国内短信模板代码";//请配置国内短信模板代码
						}
					}else{//国际
						if(aliyun_internationalSignName == null || "".equals(aliyun_internationalSignName.trim())){
							errorInfo = "请配置国际短信签名";//请配置国际短信签名
						}
						if(aliyun_internationalTemplateCode == null || "".equals(aliyun_internationalTemplateCode.trim())){
							errorInfo = "请配置国际短信模板代码";//请配置国际短信模板代码
						}
					}
					
					
					if(errorInfo == null){//如果没有错误
						if(countryCode == null || "".equals(countryCode.trim())){//国内
							StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
					                .accessKeyId(accessKeyId)
					                .accessKeySecret(accessKeySecret)
					                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
					                .build());
						
							AsyncClient client = AsyncClient.builder()
					                //.httpClient(httpClient) // 使用配置的HttpClient，否则使用默认的HttpClient（Apache HttpClient）
					                .credentialsProvider(provider)
					                //.serviceConfiguration(Configuration.create()) // 服务级别配置
					                // 客户端级配置重写，可设置Endpoint、Http请求参数等。
					                .overrideConfiguration(
					                        ClientOverrideConfiguration.create()
					                                  // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
					                                //.setEndpointOverride("dysmsapi.ap-southeast-1.aliyuncs.com")//新加坡
					                        .setEndpointOverride("dysmsapi.aliyuncs.com")//国内
					                        //.setConnectTimeout(Duration.ofSeconds(30))
					                )
					                .build();
							// API请求参数设置
							SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
					                .phoneNumbers(countryCode+mobile)//必填:待发送手机号
					                .signName(aliyun_signName)//必填:短信签名-可在短信控制台中找到
					                .templateCode(aliyun_templateCode)//必填:短信模板-可在短信控制台中找到
					                .templateParam("{\"code\":\""+code+"\"}")//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
					                .outId(platformUserId)//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
					             
					                // 请求级别配置重写，可以设置Http请求参数等
					                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
					                .build();
							try {
						        // 异步获取API请求的返回值
						        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
						        // 同步获取API请求的返回值
								SendSmsResponse resp = response.get();
								if(resp.getStatusCode().equals(200)){
									if("OK".equals(resp.getBody().getCode())){
										int original = smsManage.addSentSmsCount(platformUserId, 0);//原来总次数
										smsManage.deleteSentSmsCount(platformUserId);
								    	//增加发送短信次数记录
										smsManage.addSentSmsCount(platformUserId,original+1);
									}else{
										errorInfo = "短信发送错误";//短信发送错误
										
										//错误日志
										SendSmsLog sendSmsLog = new SendSmsLog();
										sendSmsLog.setInterfaceProduct(smsInterface.getInterfaceProduct());//接口产品
										sendSmsLog.setServiceId(serviceId);//服务Id
										sendSmsLog.setPlatformUserId(platformUserId);;//平台用户Id
										sendSmsLog.setMobile(countryCode+mobile);//手机
										sendSmsLog.setCode(resp.getBody().getCode());//状态码-返回OK代表请求成功,其他错误码详见错误码列表
										sendSmsLog.setMessage(resp.getBody().getMessage());//状态码描述
									//	sendSmsLog.setAlidayu_requestId(sendSmsResponse.getRequestId());//请求ID
									//	sendSmsLog.setAlidayu_bizId(sendSmsResponse.getBizId());//发送回执ID,可根据该ID查询具体的发送状态
										smsService.saveSendSmsLog(sendSmsLog);
									}
									
								}else{
									errorInfo = "短信发送状态错误";//短信发送状态错误
								}
								
							} catch (InterruptedException e) {
								errorInfo = "发送服务中断";//发送服务中断
								if (logger.isErrorEnabled()) {
						            logger.error("阿里云国内短信发送服务中断",e);
						        }
								// TODO Auto-generated catch block
								//e.printStackTrace();
							} catch (ExecutionException e) {
								errorInfo = "发送服务执行错误";//发送服务执行错误
								if (logger.isErrorEnabled()) {
						            logger.error("阿里云国内短信发送服务执行错误",e);
						        }
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}finally{
						        client.close();
							}
					        
					        // Asynchronous processing of return values
					        //response.thenAccept(resp -> {
					        //    System.out.println(new Gson().toJson(resp));
					        //}).exceptionally(throwable -> { // Handling exceptions
					        //    System.out.println(throwable.getMessage());
					        //    return null;
					        //});
						}else{//国际
							StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
					                .accessKeyId(accessKeyId)
					                .accessKeySecret(accessKeySecret)
					                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
					                .build());
						
							AsyncClient client = AsyncClient.builder()
					                //.httpClient(httpClient) // 使用配置的HttpClient，否则使用默认的HttpClient（Apache HttpClient）
					                .credentialsProvider(provider)
					                //.serviceConfiguration(Configuration.create()) // 服务级别配置
					                // 客户端级配置重写，可设置Endpoint、Http请求参数等。
					                .overrideConfiguration(
					                        ClientOverrideConfiguration.create()
					                                  // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
					                                //.setEndpointOverride("dysmsapi.ap-southeast-1.aliyuncs.com")//新加坡
					                        .setEndpointOverride("dysmsapi.aliyuncs.com")//国内
					                        //.setConnectTimeout(Duration.ofSeconds(30))
					                )
					                .build();
							
							// API请求参数设置
							SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
					                .phoneNumbers(StringUtils.removeStart(countryCode, "+")+mobile)//必填:待发送手机号  删除区号开头的+号
					                .signName(aliyun_internationalSignName)//必填:短信签名-可在短信控制台中找到
					                .templateCode(aliyun_internationalTemplateCode)//必填:短信模板-可在短信控制台中找到
					                .templateParam("{\"code\":\""+code+"\"}")//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
					                .outId(platformUserId)//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
					             
					                // 请求级别配置重写，可以设置Http请求参数等
					                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
					                .build();
							try {
						        // 异步获取API请求的返回值
						        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
						        // 同步获取API请求的返回值
								SendSmsResponse resp = response.get();
								if(resp.getStatusCode().equals(200)){
									if("OK".equals(resp.getBody().getCode())){
										int original = smsManage.addSentSmsCount(platformUserId, 0);//原来总次数
										smsManage.deleteSentSmsCount(platformUserId);
								    	//增加发送短信次数记录
										smsManage.addSentSmsCount(platformUserId,original+1);
									}else{
										errorInfo = "短信发送错误";//短信发送错误
										
										//错误日志
										SendSmsLog sendSmsLog = new SendSmsLog();
										sendSmsLog.setInterfaceProduct(smsInterface.getInterfaceProduct());//接口产品
										sendSmsLog.setServiceId(serviceId);//服务Id
										sendSmsLog.setPlatformUserId(platformUserId);;//平台用户Id
										sendSmsLog.setMobile(countryCode+mobile);//手机
										sendSmsLog.setCode(resp.getBody().getCode());//状态码-返回OK代表请求成功,其他错误码详见错误码列表
										sendSmsLog.setMessage(resp.getBody().getMessage());//状态码描述
									//	sendSmsLog.setAlidayu_requestId(sendSmsResponse.getRequestId());//请求ID
									//	sendSmsLog.setAlidayu_bizId(sendSmsResponse.getBizId());//发送回执ID,可根据该ID查询具体的发送状态
										smsService.saveSendSmsLog(sendSmsLog);
									}
									
								}else{
									errorInfo = "短信发送状态错误";//短信发送状态错误
								}
								
							} catch (InterruptedException e) {
								errorInfo = "发送服务中断";//发送服务中断
								if (logger.isErrorEnabled()) {
						            logger.error("阿里云国内短信发送服务中断",e);
						        }
								// TODO Auto-generated catch block
								//e.printStackTrace();
							} catch (ExecutionException e) {
								errorInfo = "发送服务执行错误";//发送服务执行错误
								if (logger.isErrorEnabled()) {
						            logger.error("阿里云国内短信发送服务执行错误",e);
						        }
								// TODO Auto-generated catch block
								//e.printStackTrace();
							}finally{
						        client.close();
							}
						}
					}
				}
			}else{
				errorInfo = "没有配置短信接口";//没有配置短信接口
			}
			
		}
		return errorInfo;
	}
	
	
	/**
	 * 是否开启国际短信服务
	 * @return
	 */
	public boolean isEnableInternationalSMS(){
		SmsInterface smsInterface = smsService.findEnableInterface_cache();
		if(smsInterface != null){
			if(smsInterface.getInterfaceProduct().equals(1) && smsInterface.isEnable()){//1.阿里云短信
				if(smsInterface.getSendService() != null && !"".equals(smsInterface.getSendService().trim())){
					List<SendService> _sendServiceList = JsonUtils.toGenericObject(smsInterface.getSendService(), new TypeReference< List<SendService> >(){});
					if(_sendServiceList != null && _sendServiceList.size() >0){
						for(SendService _sendService : _sendServiceList){
							if(_sendService.getServiceId().equals(1)){//1.绑定手机服务
								String aliyun_internationalSignName = _sendService.getAlidayu_internationalSignName();
								String aliyun_internationalTemplateCode = _sendService.getAlidayu_internationalTemplateCode();
								if(aliyun_internationalSignName != null && !"".equals(aliyun_internationalSignName.trim())
										&& aliyun_internationalTemplateCode != null && !"".equals(aliyun_internationalTemplateCode.trim())){
									return true;
								}
							}
						}
					}
				}
				
			}
		}
		
		return false;
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
