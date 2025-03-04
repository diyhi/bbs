package cms.web.action.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.sms.Alidayu;
import cms.bean.sms.SendService;
import cms.bean.sms.SmsInterface;
import cms.service.sms.SmsService;
import cms.utils.JsonUtils;

/**
 * 短信接口管理
 *
 */
@Controller
@RequestMapping("/control/smsInterface/manage") 
public class SmsManageAction {

	@Resource(name = "smsInterfaceValidator") 
	private Validator validator; 
	
	@Resource SmsManage smsManage;
	@Resource SmsService smsService;//通过接口引用代理返回的对象
	@Resource MessageSource messageSource;
	
	private Map<Integer,String> smsInterfaceProductParameter = ImmutableMap.of(1, "阿里云短信");//ImmutableMap.of(1, "阿里云通信", 10, "云片");
	
	
	/**
	 * 短信接口 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//可添加的接口产品
		LinkedHashMap<Integer,String> smsInterfaceProductMap = new LinkedHashMap<Integer,String>();
		smsInterfaceProductMap.putAll(smsInterfaceProductParameter);
		
		List<SmsInterface> smsInterfaceList =  smsService.findAllSmsInterface();
		if(smsInterfaceList != null && smsInterfaceList.size() >0){
			
			for(SmsInterface _smsInterface : smsInterfaceList){
				if(smsInterfaceProductMap.containsKey(_smsInterface.getInterfaceProduct())){
					smsInterfaceProductMap.remove(_smsInterface.getInterfaceProduct());
				}
			}
		}
		List<SendService> all_sendServiceList = new ArrayList<SendService>();

		for (Map.Entry<Integer, String> entry : smsInterfaceProductMap.entrySet()) {
			List<SendService> _sendServiceList = smsManage.createSendService(entry.getKey());
			all_sendServiceList.addAll(_sendServiceList);
		}
		
		returnValue.put("sendServiceList", all_sendServiceList);

		returnValue.put("smsInterfaceProductMap",smsInterfaceProductMap);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 在线支付接口 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,SmsInterface formbean,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		this.validator.validate(formbean, result); 
		
		Alidayu alidayu = new Alidayu();//阿里大于
		
		String dynamicParameter = "";
		String sendService_json = "";
		
		
		//可添加的接口产品
		List<Integer> smsInterfaceProductList = new ArrayList<Integer>();
		smsInterfaceProductList.addAll(smsInterfaceProductParameter.keySet());
		
		SmsInterface smsInterface = new SmsInterface();
		
		//是否选择  true:启用 false: 禁用
		boolean enable = true;
		
		List<SmsInterface> smsInterfaceList =  smsService.findAllSmsInterface();
		if(smsInterfaceList != null && smsInterfaceList.size() >0){
			for(SmsInterface _smsInterface : smsInterfaceList){
				if(_smsInterface.isEnable()){
					enable = false;
				}
				if(smsInterfaceProductList.contains(_smsInterface.getInterfaceProduct())){
					smsInterfaceProductList.remove(_smsInterface.getInterfaceProduct());
				}
			}
		}
		
		List<SendService> all_sendServiceList = new ArrayList<SendService>();
		for(Integer smsInterfaceProduct: smsInterfaceProductList){
			List<SendService> _sendServiceList = smsManage.createSendService(smsInterfaceProduct);
			all_sendServiceList.addAll(_sendServiceList);
		}

		if(formbean.getInterfaceProduct() == null ||!smsInterfaceProductParameter.containsKey(formbean.getInterfaceProduct())){
			result.rejectValue("interfaceProduct","errors.required", new String[]{"接口产品参数错误"},"");
		}
		if(!smsInterfaceProductList.contains(formbean.getInterfaceProduct())){
			result.rejectValue("interfaceProduct","errors.required", new String[]{"请选择接口产品"},"");
		}
		
		if(formbean.getInterfaceProduct() != null){
			
			if(formbean.getInterfaceProduct().equals(1)){//1.阿里大于
				String alidayu_accessKeyId = request.getParameter("alidayu_accessKeyId");
				String alidayu_accessKeySecret = request.getParameter("alidayu_accessKeySecret");
				
				if(alidayu_accessKeyId != null && !"".equals(alidayu_accessKeyId.trim())){
					alidayu.setAccessKeyId(alidayu_accessKeyId);
				}else{
					error.put("alidayu_accessKeyId", "用户密钥Id不能为空");
				}
				
				if(alidayu_accessKeySecret != null && !"".equals(alidayu_accessKeySecret.trim())){
					alidayu.setAccessKeySecret(alidayu_accessKeySecret);
				}else{
					error.put("alidayu_accessKeySecret", "用户密钥不能为空");
				}
				dynamicParameter = JsonUtils.toJSONString(alidayu);
				
				
				List<SendService> new_sendServiceList = new ArrayList<SendService>();
				for(SendService sendService : all_sendServiceList){
					if(sendService.getInterfaceProduct().equals(formbean.getInterfaceProduct())){
						String signName = request.getParameter("alidayu_signName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						String templateCode = request.getParameter("alidayu_templateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						String internationalSignName = request.getParameter("alidayu_internationalSignName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						String internationalTemplateCode = request.getParameter("alidayu_internationalTemplateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						
						int count = 0;//统计有值字段
						if(signName != null && !"".equals(signName.trim())){
							sendService.setAlidayu_signName(signName.trim());
							count++;
						}
						if(templateCode != null && !"".equals(templateCode.trim())){
							sendService.setAlidayu_templateCode(templateCode.trim());
							count++;
						}
						if(count == 1){
							if(signName == null || "".equals(signName.trim())){
								error.put("alidayu_signName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信签名不能为空");
							}
							if(templateCode == null || "".equals(templateCode.trim())){
								error.put("alidayu_templateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信模板代码不能为空");
							}
						}
						
						int internationalCount = 0;//统计有值字段
						if(internationalSignName != null && !"".equals(internationalSignName.trim())){
							sendService.setAlidayu_internationalSignName(internationalSignName.trim());
							internationalCount++;
						}
						if(internationalTemplateCode != null && !"".equals(internationalTemplateCode.trim())){
							sendService.setAlidayu_internationalTemplateCode(internationalTemplateCode.trim());
							internationalCount++;
						}
						if(internationalCount == 1){
							if(internationalSignName == null || "".equals(internationalSignName.trim())){
								error.put("alidayu_internationalSignName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信签名不能为空");
							}
							if(internationalTemplateCode == null || "".equals(internationalTemplateCode.trim())){
								error.put("alidayu_internationalTemplateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信模板代码不能为空");
							}
						}
						
						new_sendServiceList.add(sendService);
					}
					
				}
				sendService_json = JsonUtils.toJSONString(new_sendServiceList);
			}else if(formbean.getInterfaceProduct().equals(10)){//云片
				
			}else{
				result.rejectValue("interfaceProduct","errors.required", new String[]{"请选择接口产品"},"");
			}
			
		}
		
		if (result.hasErrors()) {  
			List<FieldError> fieldErrorList = result.getFieldErrors();
			if(fieldErrorList != null && fieldErrorList.size() >0){
				for(FieldError fieldError : fieldErrorList){
					error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
				}
			}
		} 
		if(error.size() ==0){
			smsInterface.setName(formbean.getName());
			smsInterface.setInterfaceProduct(formbean.getInterfaceProduct());
			smsInterface.setDynamicParameter(dynamicParameter);
			smsInterface.setSendService(sendService_json);
			smsInterface.setEnable(enable);
			smsInterface.setSort(formbean.getSort());
			

			smsService.saveSmsInterface(smsInterface);
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 在线支付接口 显示修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {	
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(smsInterfaceId != null && smsInterfaceId >0){
			//根据ID查询要修改的数据
			SmsInterface smsInterface = smsService.findSmsInterfaceById(smsInterfaceId);
			
			if(smsInterface != null){
				returnValue.put("smsInterface", smsInterface);
				
				if(smsInterface.getDynamicParameter() != null && !"".equals(smsInterface.getDynamicParameter().trim())){
					if(smsInterface.getInterfaceProduct().equals(1)){//阿里大于
						Alidayu alidayu= JsonUtils.toObject(smsInterface.getDynamicParameter(), Alidayu.class);
						returnValue.put("alidayu", alidayu);
					}
				}
				
				List<SendService> sendServiceList = smsManage.createSendService(smsInterface.getInterfaceProduct());
				
				if(smsInterface.getInterfaceProduct().equals(1)){//阿里大于
					if(smsInterface.getSendService() != null && !"".equals(smsInterface.getSendService().trim())){
						List<SendService> _sendServiceList = JsonUtils.toGenericObject(smsInterface.getSendService(), new TypeReference< List<SendService> >(){});
						
						for(SendService sendService : sendServiceList){
							for(SendService _sendService : _sendServiceList){
								if(sendService.getServiceId().equals(_sendService.getServiceId())){
									sendService.setAlidayu_signName(_sendService.getAlidayu_signName());
									sendService.setAlidayu_templateCode(_sendService.getAlidayu_templateCode());
									sendService.setAlidayu_internationalSignName(_sendService.getAlidayu_internationalSignName());
									sendService.setAlidayu_internationalTemplateCode(_sendService.getAlidayu_internationalTemplateCode());
									break;
								}
							}
						}
						
					}
				}
				returnValue.put("sendServiceList", sendServiceList);
			}else{
				error.put("smsInterfaceId", "短信接口不存在");
			}
		}else{
			error.put("smsInterfaceId", "短信接口Id不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 短信接口 修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,SmsInterface formbean,BindingResult result,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> error = new HashMap<String,Object>();

		if(smsInterfaceId != null && smsInterfaceId >0){
			SmsInterface smsInterface = smsService.findSmsInterfaceById(smsInterfaceId);
			if(smsInterface != null){
				this.validator.validate(formbean, result); 
				
				String dynamicParameter = "";
				String sendService_json = "";
				
				Alidayu alidayu = new Alidayu();//阿里大于
				
				List<SendService> sendServiceList = null;
				
				
				if(smsInterface.getInterfaceProduct().equals(1)){//1.阿里大于
					String alidayu_accessKeyId = request.getParameter("alidayu_accessKeyId");
					String alidayu_accessKeySecret = request.getParameter("alidayu_accessKeySecret");
					
					if(alidayu_accessKeyId != null && !"".equals(alidayu_accessKeyId.trim())){
						alidayu.setAccessKeyId(alidayu_accessKeyId);
					}else{
						error.put("alidayu_accessKeyId", "用户密钥Id不能为空");
					}
					
					if(alidayu_accessKeySecret != null && !"".equals(alidayu_accessKeySecret.trim())){
						alidayu.setAccessKeySecret(alidayu_accessKeySecret);
					}else{
						error.put("alidayu_accessKeySecret", "用户密钥不能为空");
					}
					
					
					dynamicParameter = JsonUtils.toJSONString(alidayu);
					
					
					sendServiceList = smsManage.createSendService(smsInterface.getInterfaceProduct());
					
					for(SendService sendService : sendServiceList){
						String signName = request.getParameter("alidayu_signName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						String templateCode = request.getParameter("alidayu_templateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						String internationalSignName = request.getParameter("alidayu_internationalSignName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						String internationalTemplateCode = request.getParameter("alidayu_internationalTemplateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
						int count = 0;//统计有值字段
						if(signName != null && !"".equals(signName.trim())){
							sendService.setAlidayu_signName(signName.trim());
							count++;
						}
						if(templateCode != null && !"".equals(templateCode.trim())){
							sendService.setAlidayu_templateCode(templateCode.trim());
							count++;
						}
						if(count == 1){
							if(signName == null || "".equals(signName.trim())){
								error.put("alidayu_signName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信签名不能为空");
							}
							if(templateCode == null || "".equals(templateCode.trim())){
								error.put("alidayu_templateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信模板代码不能为空");
							}
						}
						
						int internationalCount = 0;//统计有值字段
						if(internationalSignName != null && !"".equals(internationalSignName.trim())){
							sendService.setAlidayu_internationalSignName(internationalSignName.trim());
							internationalCount++;
						}
						if(internationalTemplateCode != null && !"".equals(internationalTemplateCode.trim())){
							sendService.setAlidayu_internationalTemplateCode(internationalTemplateCode.trim());
							internationalCount++;
						}
						if(internationalCount == 1){
							if(internationalSignName == null || "".equals(internationalSignName.trim())){
								error.put("alidayu_internationalSignName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信签名不能为空");
							}
							if(internationalTemplateCode == null || "".equals(internationalTemplateCode.trim())){
								error.put("alidayu_internationalTemplateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信模板代码不能为空");
							}
						}
					}
					sendService_json = JsonUtils.toJSONString(sendServiceList);
				}
				
				smsInterface.setId(smsInterfaceId);
				smsInterface.setName(formbean.getName());
				smsInterface.setDynamicParameter(dynamicParameter);
				smsInterface.setSendService(sendService_json);
				smsInterface.setSort(formbean.getSort());	
				smsInterface.setVersion(Integer.parseInt(RandomStringUtils.randomNumeric(5)));//5位随机数
				
				if (result.hasErrors()) {  
					List<FieldError> fieldErrorList = result.getFieldErrors();
					if(fieldErrorList != null && fieldErrorList.size() >0){
						for(FieldError fieldError : fieldErrorList){
							error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
						}
					}
				}
				if(error.size() ==0){
					
					smsService.updateSmsInterface(smsInterface);
				}
			}else{
				error.put("smsInterfaceId", "短信接口不存在");
			}
		}else{
			error.put("smsInterfaceId", "短信接口Id不能为空");
		}
	
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 短信接口 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		if(smsInterfaceId != null && smsInterfaceId >0){
			smsService.deleteSmsInterface(smsInterfaceId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("smsInterfaceId", "短信接口Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 短信接口 启用当前接口
	 * @param model
	 * @param dirName 模板目录名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=enableInterface",method=RequestMethod.POST)
	public String enableInterface(ModelMap model,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(smsInterfaceId != null && smsInterfaceId >0){
			smsService.updateEnableInterface(smsInterfaceId,Integer.parseInt(RandomStringUtils.randomNumeric(5)));
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("smsInterfaceId", "短信接口Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
}
