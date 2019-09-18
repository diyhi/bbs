package cms.web.action.payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.payment.Alipay;
import cms.bean.payment.OnlinePaymentInterface;
import cms.service.payment.PaymentService;
import cms.utils.JsonUtils;
import cms.utils.RedirectPath;
import cms.web.action.SystemException;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@Resource OnlinePaymentInterfaceManage onlinePaymentInterfaceManage;
	private List<Integer> paymentInterfaceProductParameter = Arrays.asList(1,4);//支付接口产品
	
	
	/**
	 * 在线支付接口 添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,OnlinePaymentInterface onlinePaymentInterface,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//可添加的接口产品
		List<Integer> paymentInterfaceProductList = new ArrayList<Integer>();
		paymentInterfaceProductList.addAll(paymentInterfaceProductParameter);
		
		List<OnlinePaymentInterface> onlinePaymentInterfaceList =  paymentService.findAllOnlinePaymentInterface();
		if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){

			for(OnlinePaymentInterface paymentInterface : onlinePaymentInterfaceList){
				if(paymentInterfaceProductList.contains(paymentInterface.getInterfaceProduct())){
					paymentInterfaceProductList.remove(paymentInterface.getInterfaceProduct());
				}
			}
		}
		model.addAttribute("paymentInterfaceProductList",paymentInterfaceProductList);
		
		return "jsp/payment/add_onlinePaymentInterface";
	}
	
	/**
	 * 在线支付接口 添加
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,OnlinePaymentInterface formbean,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		this.validator.validate(formbean, result); 
		
		Alipay alipay_direct = new Alipay();//支付宝即时到账
		Alipay alipay_mobile = new Alipay();//支付宝手机网站(alipay.trade.wap.pay接口)
		
		String dynamicParameter = "";
		
		Map<String,String> error = new HashMap<String,String>();
		
		
		//可添加的接口产品
		List<Integer> paymentInterfaceProductList = new ArrayList<Integer>();
		paymentInterfaceProductList.addAll(paymentInterfaceProductParameter);
		
		OnlinePaymentInterface onlinePaymentInterface = new OnlinePaymentInterface();
		
		List<OnlinePaymentInterface> onlinePaymentInterfaceList =  paymentService.findAllOnlinePaymentInterface();
		if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){

			for(OnlinePaymentInterface paymentInterface : onlinePaymentInterfaceList){
				if(paymentInterfaceProductList.contains(paymentInterface.getInterfaceProduct())){
					paymentInterfaceProductList.remove(paymentInterface.getInterfaceProduct());
				}
			}
		}
		if(formbean.getInterfaceProduct() == null ||!paymentInterfaceProductParameter.contains(formbean.getInterfaceProduct())){
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
		
		if (result.hasErrors() || error.size() >0) {  
			model.addAttribute("error", error);
			model.addAttribute("paymentInterfaceProductList",paymentInterfaceProductList);
			model.addAttribute("alipayDirect", alipay_direct);
			model.addAttribute("alipayMobile", alipay_mobile);
			return "jsp/payment/add_onlinePaymentInterface";
		} 
	
		
		onlinePaymentInterface.setName(formbean.getName());
		onlinePaymentInterface.setInterfaceProduct(formbean.getInterfaceProduct());
		onlinePaymentInterface.setEnable(formbean.isEnable());
		onlinePaymentInterface.setDynamicParameter(dynamicParameter);
		
		onlinePaymentInterface.setSort(formbean.getSort());
		

		paymentService.saveOnlinePaymentInterface(onlinePaymentInterface);
		model.addAttribute("message", "添加在线支付接口成功");
		model.addAttribute("urladdress", RedirectPath.readUrl("control.onlinePaymentInterface.list"));
		return "jsp/common/message";
	}

	/**
	 * 在线支付接口 显示修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer onlinePaymentInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {	
	
		if(onlinePaymentInterfaceId != null && onlinePaymentInterfaceId >0){
			//根据ID查询要修改的数据
			OnlinePaymentInterface paymentInterface = paymentService.findOnlinePaymentInterfaceById(onlinePaymentInterfaceId);
			
			if(paymentInterface != null){
				model.addAttribute("onlinePaymentInterface", paymentInterface);
				
				if(paymentInterface.getDynamicParameter() != null && !"".equals(paymentInterface.getDynamicParameter().trim())){
					if(paymentInterface.getInterfaceProduct().equals(1)){//支付宝即时到账
						Alipay alipay_direct = JsonUtils.toObject(paymentInterface.getDynamicParameter(), Alipay.class);
						model.addAttribute("alipayDirect", alipay_direct);
						
					}else if(paymentInterface.getInterfaceProduct().equals(4)){//支付宝手机网站(alipay.trade.wap.pay接口)
						Alipay alipay_bank = JsonUtils.toObject(paymentInterface.getDynamicParameter(), Alipay.class);
						model.addAttribute("alipayMobile", alipay_bank);
					}
					
				}
				
			}
			
			
		}else{
			throw new SystemException("参数不能为空");
		}
		return "jsp/payment/edit_onlinePaymentInterface";
	}
	/**
	 * 在线支付接口 修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,OnlinePaymentInterface formbean,BindingResult result,Integer onlinePaymentInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		if(onlinePaymentInterfaceId == null || onlinePaymentInterfaceId <=0){
			throw new SystemException("参数不能为空");
		}
	
		this.validator.validate(formbean, result); 
		
		String dynamicParameter = "";
		Map<String,String> error = new HashMap<String,String>();
		Alipay alipay_direct = new Alipay();//支付宝即时到账
		Alipay alipay_mobile = new Alipay();//支付宝手机网站(alipay.trade.wap.pay接口)
		
		
		OnlinePaymentInterface onlinePaymentInterface = paymentService.findOnlinePaymentInterfaceById(onlinePaymentInterfaceId);
		
		if(onlinePaymentInterface == null){
			throw new SystemException("在线支付接口不存在");
		}
		
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
		
		formbean.setInterfaceProduct(onlinePaymentInterface.getInterfaceProduct());//回显需要
		
		if (result.hasErrors() || error.size() >0) {  
			model.addAttribute("error", error);
			model.addAttribute("alipayDirect", alipay_direct);
			model.addAttribute("alipayMobile", alipay_mobile);
			return "jsp/payment/edit_onlinePaymentInterface";
		}
		paymentService.updateOnlinePaymentInterface(onlinePaymentInterface);
		model.addAttribute("message", "修改在线支付接口成功");
		model.addAttribute("urladdress", RedirectPath.readUrl("control.onlinePaymentInterface.list"));
		return "jsp/common/message";
	}
	/**
	 * 在线支付接口 删除
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Integer onlinePaymentInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(onlinePaymentInterfaceId != null && onlinePaymentInterfaceId >0){
			paymentService.deleteOnlinePaymentInterface(onlinePaymentInterfaceId);
			return "1";
		}
		return "0";
	}
	
}

