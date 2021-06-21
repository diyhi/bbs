package cms.web.action.payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.payment.Alipay;
import cms.bean.payment.OnlinePaymentInterface;
import cms.service.payment.PaymentService;
import cms.utils.JsonUtils;

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

import com.google.common.collect.ImmutableMap;

/**
 * 在线支付接口管理
 * @author gao
 *
 */
@Controller
@RequestMapping("/control/onlinePaymentInterface/manage") 
public class OnlinePaymentInterfaceManageAction{

	@Resource PaymentService paymentService;//通过接口引用代理返回的对象
	@Resource(name = "onlinePaymentInterfaceValidator") 
	private Validator validator; 
	@Resource MessageSource messageSource;
	
	
	@Resource OnlinePaymentInterfaceManage onlinePaymentInterfaceManage;
	private Map<Integer,String> paymentInterfaceProductParameter = ImmutableMap.of(1, "支付宝即时到账", 4, "支付宝手机网站(alipay.trade.wap.pay)");//支付接口产品
	
	
	/**
	 * 在线支付接口 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,OnlinePaymentInterface onlinePaymentInterface,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//可添加的接口产品
		LinkedHashMap<Integer,String> paymentInterfaceProductMap = new LinkedHashMap<Integer,String>();
		paymentInterfaceProductMap.putAll(paymentInterfaceProductParameter);
		
		List<OnlinePaymentInterface> onlinePaymentInterfaceList =  paymentService.findAllOnlinePaymentInterface();
		if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){

			for(OnlinePaymentInterface paymentInterface : onlinePaymentInterfaceList){
				if(paymentInterfaceProductMap.containsKey(paymentInterface.getInterfaceProduct())){
					paymentInterfaceProductMap.remove(paymentInterface.getInterfaceProduct());
				}
			}
		}
		returnValue.put("paymentInterfaceProductMap",paymentInterfaceProductMap);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 在线支付接口 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,OnlinePaymentInterface formbean,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		this.validator.validate(formbean, result); 
		
		Alipay alipay_direct = new Alipay();//支付宝即时到账
		Alipay alipay_mobile = new Alipay();//支付宝手机网站(alipay.trade.wap.pay接口)
		
		String dynamicParameter = "";
		
		
		
		//可添加的接口产品
		List<Integer> paymentInterfaceProductList = new ArrayList<Integer>();
		paymentInterfaceProductList.addAll(paymentInterfaceProductParameter.keySet());
		
		OnlinePaymentInterface onlinePaymentInterface = new OnlinePaymentInterface();
		
		List<OnlinePaymentInterface> onlinePaymentInterfaceList =  paymentService.findAllOnlinePaymentInterface();
		if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){

			for(OnlinePaymentInterface paymentInterface : onlinePaymentInterfaceList){
				if(paymentInterfaceProductList.contains(paymentInterface.getInterfaceProduct())){
					paymentInterfaceProductList.remove(paymentInterface.getInterfaceProduct());
				}
			}
		}
		if(formbean.getInterfaceProduct() == null ||!paymentInterfaceProductParameter.containsKey(formbean.getInterfaceProduct())){
			result.rejectValue("interfaceProduct","errors.required", new String[]{"接口产品参数错误"},"");
		}
		if(!paymentInterfaceProductList.contains(formbean.getInterfaceProduct())){
			result.rejectValue("interfaceProduct","errors.required", new String[]{"请选择接口产品"},"");
		}
		
		if(formbean.getInterfaceProduct() != null){
			if(formbean.getInterfaceProduct().equals(1)){//1.支付宝即时到账
				String direct_app_id = request.getParameter("direct_app_id");
				String direct_rsa_private_key = request.getParameter("direct_rsa_private_key");
				String direct_alipay_public_key = request.getParameter("direct_alipay_public_key");
				
				if(direct_app_id != null && !"".equals(direct_app_id.trim())){
					alipay_direct.setApp_id(direct_app_id);
				}else{
					error.put("direct_app_id", "APPID不能为空");
				}
				
				if(direct_rsa_private_key != null && !"".equals(direct_rsa_private_key.trim())){
					alipay_direct.setRsa_private_key(direct_rsa_private_key);
				}else{
					error.put("direct_rsa_private_key", "商户的私钥不能为空");
				}
				if(direct_alipay_public_key != null && !"".equals(direct_alipay_public_key.trim())){
					alipay_direct.setAlipay_public_key(direct_alipay_public_key);
				}else{
					error.put("direct_alipay_public_key", "支付宝公钥不能为空");
				}
				
				
				dynamicParameter = JsonUtils.toJSONString(alipay_direct);
				onlinePaymentInterface.setSupportEquipment(onlinePaymentInterfaceManage.setSupportEquipment(true, false, false));
				
			}else if(formbean.getInterfaceProduct().equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
				String mobile_app_id = request.getParameter("mobile_app_id");
				String mobile_rsa_private_key = request.getParameter("mobile_rsa_private_key");
				String mobile_alipay_public_key = request.getParameter("mobile_alipay_public_key");
				
				if(mobile_app_id != null && !"".equals(mobile_app_id.trim())){
					alipay_mobile.setApp_id(mobile_app_id);
				}else{
					error.put("mobile_app_id", "APPID不能为空");
				}
				
				if(mobile_rsa_private_key != null && !"".equals(mobile_rsa_private_key.trim())){
					alipay_mobile.setRsa_private_key(mobile_rsa_private_key);
				}else{
					error.put("mobile_rsa_private_key", "商户的私钥不能为空");
				}
				
				if(mobile_alipay_public_key != null && !"".equals(mobile_alipay_public_key.trim())){
					alipay_mobile.setAlipay_public_key(mobile_alipay_public_key);
				}else{
					error.put("mobile_alipay_public_key", "支付宝公钥不能为空");
				}
				dynamicParameter = JsonUtils.toJSONString(alipay_mobile);
				
				onlinePaymentInterface.setSupportEquipment(onlinePaymentInterfaceManage.setSupportEquipment(false, true, false));
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
			onlinePaymentInterface.setName(formbean.getName());
			onlinePaymentInterface.setInterfaceProduct(formbean.getInterfaceProduct());
			onlinePaymentInterface.setEnable(formbean.isEnable());
			onlinePaymentInterface.setDynamicParameter(dynamicParameter);
			
			onlinePaymentInterface.setSort(formbean.getSort());
			

			paymentService.saveOnlinePaymentInterface(onlinePaymentInterface);
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
	public String editUI(ModelMap model,Integer onlinePaymentInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {	
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(onlinePaymentInterfaceId != null && onlinePaymentInterfaceId >0){
			//根据ID查询要修改的数据
			OnlinePaymentInterface paymentInterface = paymentService.findOnlinePaymentInterfaceById(onlinePaymentInterfaceId);
			
			if(paymentInterface != null){
				returnValue.put("onlinePaymentInterface", paymentInterface);
				
				if(paymentInterface.getDynamicParameter() != null && !"".equals(paymentInterface.getDynamicParameter().trim())){
					if(paymentInterface.getInterfaceProduct().equals(1)){//支付宝即时到账
						Alipay alipay_direct = JsonUtils.toObject(paymentInterface.getDynamicParameter(), Alipay.class);
						returnValue.put("alipayDirect", alipay_direct);
						
					}else if(paymentInterface.getInterfaceProduct().equals(4)){//支付宝手机网站(alipay.trade.wap.pay接口)
						Alipay alipay_bank = JsonUtils.toObject(paymentInterface.getDynamicParameter(), Alipay.class);
						returnValue.put("alipayMobile", alipay_bank);
					}
					
				}
				
			}else{
				error.put("onlinePaymentInterfaceId", "在线支付接口不存在");
			}
		}else{
			error.put("onlinePaymentInterfaceId", "在线支付接口Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 在线支付接口 修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,OnlinePaymentInterface formbean,BindingResult result,Integer onlinePaymentInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> error = new HashMap<String,Object>();

		if(onlinePaymentInterfaceId != null && onlinePaymentInterfaceId >0){
			OnlinePaymentInterface onlinePaymentInterface = paymentService.findOnlinePaymentInterfaceById(onlinePaymentInterfaceId);
			if(onlinePaymentInterface != null){
				this.validator.validate(formbean, result); 
				
				String dynamicParameter = "";
				
				Alipay alipay_direct = new Alipay();//支付宝即时到账
				Alipay alipay_mobile = new Alipay();//支付宝手机网站(alipay.trade.wap.pay接口)
				
				
				
				if(onlinePaymentInterface.getInterfaceProduct().equals(1)){//1.支付宝即时到账
					String direct_app_id = request.getParameter("direct_app_id");
					String direct_rsa_private_key = request.getParameter("direct_rsa_private_key");
					String direct_alipay_public_key = request.getParameter("direct_alipay_public_key");
					
					if(direct_app_id != null && !"".equals(direct_app_id.trim())){
						alipay_direct.setApp_id(direct_app_id);
					}else{
						error.put("direct_app_id", "APPID不能为空");
					}
					
					if(direct_rsa_private_key != null && !"".equals(direct_rsa_private_key.trim())){
						alipay_direct.setRsa_private_key(direct_rsa_private_key);
					}else{
						error.put("direct_rsa_private_key", "商户的私钥不能为空");
					}
					if(direct_alipay_public_key != null && !"".equals(direct_alipay_public_key.trim())){
						alipay_direct.setAlipay_public_key(direct_alipay_public_key);
					}else{
						error.put("direct_alipay_public_key", "支付宝公钥不能为空");
					}
					dynamicParameter = JsonUtils.toJSONString(alipay_direct);
					
				}else if(onlinePaymentInterface.getInterfaceProduct().equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
					String mobile_app_id = request.getParameter("mobile_app_id");
					String mobile_rsa_private_key = request.getParameter("mobile_rsa_private_key");
					String mobile_alipay_public_key = request.getParameter("mobile_alipay_public_key");
					
					if(mobile_app_id != null && !"".equals(mobile_app_id.trim())){
						alipay_mobile.setApp_id(mobile_app_id);
					}else{
						error.put("mobile_app_id", "APPID不能为空");
					}
					
					if(mobile_rsa_private_key != null && !"".equals(mobile_rsa_private_key.trim())){
						alipay_mobile.setRsa_private_key(mobile_rsa_private_key);
					}else{
						error.put("mobile_rsa_private_key", "商户的私钥不能为空");
					}
					if(mobile_alipay_public_key != null && !"".equals(mobile_alipay_public_key.trim())){
						alipay_mobile.setAlipay_public_key(mobile_alipay_public_key);
					}else{
						error.put("mobile_alipay_public_key", "支付宝公钥不能为空");
					}
					dynamicParameter = JsonUtils.toJSONString(alipay_mobile);

				}
				
				onlinePaymentInterface.setId(onlinePaymentInterfaceId);
				onlinePaymentInterface.setName(formbean.getName());
				onlinePaymentInterface.setEnable(formbean.isEnable());
				onlinePaymentInterface.setDynamicParameter(dynamicParameter);
				onlinePaymentInterface.setSort(formbean.getSort());	
				onlinePaymentInterface.setVersion(Integer.parseInt(RandomStringUtils.randomNumeric(5)));//5位随机数
				
				if (result.hasErrors()) {  
					List<FieldError> fieldErrorList = result.getFieldErrors();
					if(fieldErrorList != null && fieldErrorList.size() >0){
						for(FieldError fieldError : fieldErrorList){
							error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
						}
					}
				}
				if(error.size() ==0){
					paymentService.updateOnlinePaymentInterface(onlinePaymentInterface);
				}
				
			}else{
				error.put("onlinePaymentInterfaceId", "在线支付接口不存在");
			}
		}else{
			error.put("onlinePaymentInterfaceId", "在线支付接口Id不能为空");
		}
		
			
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 在线支付接口 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Integer onlinePaymentInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(onlinePaymentInterfaceId != null && onlinePaymentInterfaceId >0){
			paymentService.deleteOnlinePaymentInterface(onlinePaymentInterfaceId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("onlinePaymentInterfaceId", "在线支付接口Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
}

