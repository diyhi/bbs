package cms.web.action.sms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.sms.Alidayu;
import cms.bean.sms.SendService;
import cms.bean.sms.SmsInterface;
import cms.service.sms.SmsService;
import cms.utils.JsonUtils;
import cms.utils.RedirectPath;
import cms.web.action.SystemException;

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
	private List<Integer> smsInterfaceProductParameter = Arrays.asList(1);//1.阿里大于 10.云片
	/**
	 * 短信接口 添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,SmsInterface smsInterface,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//可添加的接口产品
		List<Integer> smsInterfaceProductList = new ArrayList<Integer>();
		smsInterfaceProductList.addAll(smsInterfaceProductParameter);
		
		List<SmsInterface> smsInterfaceList =  smsService.findAllSmsInterface();
		if(smsInterfaceList != null && smsInterfaceList.size() >0){
			
			for(SmsInterface _smsInterface : smsInterfaceList){
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
		
	
		model.addAttribute("sendServiceList", all_sendServiceList);
		
		
		
		model.addAttribute("smsInterfaceProductList",smsInterfaceProductList);
		return "jsp/sms/add_smsInterface";
	}
	
	/**
	 * 在线支付接口 添加
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,SmsInterface formbean,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.validator.validate(formbean, result); 
		
		Alidayu alidayu = new Alidayu();//阿里大于
		
		String dynamicParameter = "";
		String sendService_json = "";
		Map<String,String> error = new HashMap<String,String>();
		
		
		//可添加的接口产品
		List<Integer> smsInterfaceProductList = new ArrayList<Integer>();
		smsInterfaceProductList.addAll(smsInterfaceProductParameter);
		
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

		if(formbean.getInterfaceProduct() == null ||!smsInterfaceProductParameter.contains(formbean.getInterfaceProduct())){
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
						
						new_sendServiceList.add(sendService);
					}
					
				}
				sendService_json = JsonUtils.toJSONString(new_sendServiceList);
			}else if(formbean.getInterfaceProduct().equals(10)){//云片
				
			}else{
				result.rejectValue("interfaceProduct","errors.required", new String[]{"请选择接口产品"},"");
			}
			
		}
		
		if (result.hasErrors() || error.size() >0) {  
			model.addAttribute("error", error);
			model.addAttribute("smsInterfaceProductList",smsInterfaceProductList);
			model.addAttribute("sendServiceList", all_sendServiceList);
			model.addAttribute("alidayu", alidayu);
			return "jsp/sms/add_smsInterface";
		} 
		smsInterface.setName(formbean.getName());
		smsInterface.setInterfaceProduct(formbean.getInterfaceProduct());
		smsInterface.setDynamicParameter(dynamicParameter);
		smsInterface.setSendService(sendService_json);
		smsInterface.setEnable(enable);
		smsInterface.setSort(formbean.getSort());
		

		smsService.saveSmsInterface(smsInterface);
		model.addAttribute("message", "添加短信接口成功");
		model.addAttribute("urladdress", RedirectPath.readUrl("control.smsInterface.list"));
		return "jsp/common/message";
	}
	
	/**
	 * 在线支付接口 显示修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {	
	
		if(smsInterfaceId != null && smsInterfaceId >0){
			//根据ID查询要修改的数据
			SmsInterface smsInterface = smsService.findSmsInterfaceById(smsInterfaceId);
			
			if(smsInterface != null){
				model.addAttribute("smsInterface", smsInterface);
				
				if(smsInterface.getDynamicParameter() != null && !"".equals(smsInterface.getDynamicParameter().trim())){
					if(smsInterface.getInterfaceProduct().equals(1)){//阿里大于
						Alidayu alidayu= JsonUtils.toObject(smsInterface.getDynamicParameter(), Alidayu.class);
						model.addAttribute("alidayu", alidayu);
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
									break;
								}
							}
						}
						
					}
				}
				model.addAttribute("sendServiceList", sendServiceList);
			}
		}else{
			throw new SystemException("参数不能为空");
		}
		
		

		return "jsp/sms/edit_smsInterface";
	}
	/**
	 * 短信接口 修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,SmsInterface formbean,BindingResult result,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		if(smsInterfaceId == null || smsInterfaceId <=0){
			throw new SystemException("参数不能为空");
		}
	
		this.validator.validate(formbean, result); 
		
		String dynamicParameter = "";
		String sendService_json = "";
		Map<String,String> error = new HashMap<String,String>();
		Alidayu alidayu = new Alidayu();//阿里大于
		
		List<SendService> sendServiceList = null;
		SmsInterface smsInterface = smsService.findSmsInterfaceById(smsInterfaceId);
		
		if(smsInterface == null){
			throw new SystemException("短信接口不存在");
		}
		
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
			}
			sendService_json = JsonUtils.toJSONString(sendServiceList);
		}
			
		smsInterface.setId(smsInterfaceId);
		smsInterface.setName(formbean.getName());
		smsInterface.setDynamicParameter(dynamicParameter);
		smsInterface.setSendService(sendService_json);
		smsInterface.setSort(formbean.getSort());	
		smsInterface.setVersion(Integer.parseInt(RandomStringUtils.randomNumeric(5)));//5位随机数
		
		formbean.setInterfaceProduct(smsInterface.getInterfaceProduct());//回显需要
		
		if (result.hasErrors() || error.size() >0) {  
			model.addAttribute("error", error);
			model.addAttribute("alidayu", alidayu);
			model.addAttribute("sendServiceList", sendServiceList);
			return "jsp/sms/edit_smsInterface";
		}

		smsService.updateSmsInterface(smsInterface);
		model.addAttribute("message", "修改短信接口成功");
		model.addAttribute("urladdress", RedirectPath.readUrl("control.smsInterface.list"));
		return "jsp/common/message";
	}
	
	/**
	 * 短信接口 删除
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(smsInterfaceId != null && smsInterfaceId >0){
			smsService.deleteSmsInterface(smsInterfaceId);
			return "1";
		}
		return "0";
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
	@RequestMapping(params="method=enableInterface",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String enableInterface(ModelMap model,Integer smsInterfaceId,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		if(smsInterfaceId != null && smsInterfaceId >0){
			smsService.updateEnableInterface(smsInterfaceId,Integer.parseInt(RandomStringUtils.randomNumeric(5)));
			return "1";
		}
		return "0";
	}
	
	

	
	
	
	
}
