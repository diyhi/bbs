package cms.web.action.thirdParty;

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
import cms.bean.thirdParty.ThirdPartyLoginInterface;
import cms.bean.thirdParty.WeChatConfig;
import cms.service.thirdParty.ThirdPartyLoginService;
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
 * 第三方登录接口管理
 * @author gao
 *
 */
@Controller
@RequestMapping("/control/thirdPartyLoginInterface/manage") 
public class ThirdPartyLoginInterfaceManageAction{

	@Resource ThirdPartyLoginService thirdPartyLoginService;//通过接口引用代理返回的对象
	@Resource(name = "thirdPartyLoginInterfaceValidator") 
	private Validator validator; 
	
	@Resource MessageSource messageSource;
	
	@Resource ThirdPartyManage thirdPartyManage;
	
	private Map<Integer,String> interfaceProductParameter = ImmutableMap.of(10, "微信");//接口产品
	
	
	/**
	 * 第三方登录接口 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,ThirdPartyLoginInterface thirdPartyLoginInterface,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//可添加的接口产品
		LinkedHashMap<Integer,String> interfaceProductMap = new LinkedHashMap<Integer,String>();
		interfaceProductMap.putAll(interfaceProductParameter);
		
		List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList =  thirdPartyLoginService.findAllThirdPartyLoginInterface();
		if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){

			for(ThirdPartyLoginInterface loginInterface : thirdPartyLoginInterfaceList){
				if(interfaceProductMap.containsKey(loginInterface.getInterfaceProduct())){
					interfaceProductMap.remove(loginInterface.getInterfaceProduct());
				}
			}
		}
		returnValue.put("interfaceProductMap",interfaceProductMap);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 第三方登录接口 添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,ThirdPartyLoginInterface formbean,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		this.validator.validate(formbean, result); 
		
		WeChatConfig weChatConfig = new WeChatConfig();//微信配置信息
		
		String dynamicParameter = "";
		
		
		
		
		//可添加的接口产品
		List<Integer> interfaceProductList = new ArrayList<Integer>();
		interfaceProductList.addAll(interfaceProductParameter.keySet());
		
		ThirdPartyLoginInterface thirdPartyLoginInterface = new ThirdPartyLoginInterface();
		
		List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList =  thirdPartyLoginService.findAllThirdPartyLoginInterface();
		if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){

			for(ThirdPartyLoginInterface loginInterface : thirdPartyLoginInterfaceList){
				if(interfaceProductList.contains(loginInterface.getInterfaceProduct())){
					interfaceProductList.remove(loginInterface.getInterfaceProduct());
				}
			}
		}
		if(formbean.getInterfaceProduct() == null ||!interfaceProductParameter.containsKey(formbean.getInterfaceProduct())){
			result.rejectValue("interfaceProduct","errors.required", new String[]{"接口产品参数错误"},"");
		}
		if(!interfaceProductList.contains(formbean.getInterfaceProduct())){
			result.rejectValue("interfaceProduct","errors.required", new String[]{"请选择接口产品"},"");
		}
		
		if(formbean.getInterfaceProduct() != null){
			if(formbean.getInterfaceProduct().equals(10)){//10.微信配置信息
				String weixin_op_appID = request.getParameter("weixin_op_appID");//开放平台
				String weixin_op_appSecret = request.getParameter("weixin_op_appSecret");
				String weixin_oa_appID = request.getParameter("weixin_oa_appID");//公众号
				String weixin_oa_appSecret = request.getParameter("weixin_oa_appSecret");
				
				if(weixin_op_appID != null && !"".equals(weixin_op_appID.trim())){
					weChatConfig.setOp_appID(weixin_op_appID.trim());
				}else{
					error.put("weixin_op_appID", "开放平台应用唯一标识不能为空");
				}
				
				if(weixin_op_appSecret != null && !"".equals(weixin_op_appSecret.trim())){
					weChatConfig.setOp_appSecret(weixin_op_appSecret.trim());
				}else{
					error.put("weixin_op_appSecret", "开放平台应用密钥不能为空");
				}
				
				if(weixin_oa_appID != null && !"".equals(weixin_oa_appID.trim())){
					weChatConfig.setOa_appID(weixin_oa_appID.trim());
				}else{
					error.put("weixin_oa_appID", "公众号应用唯一标识不能为空");
				}
				
				if(weixin_oa_appSecret != null && !"".equals(weixin_oa_appSecret.trim())){
					weChatConfig.setOa_appSecret(weixin_oa_appSecret.trim());
				}else{
					error.put("weixin_oa_appSecret", "公众号应用密钥不能为空");
				}
				
				
				
				
				dynamicParameter = JsonUtils.toJSONString(weChatConfig);
				thirdPartyLoginInterface.setSupportEquipment(thirdPartyManage.setSupportEquipment(true, false, false,true));
				
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
			thirdPartyLoginInterface.setName(formbean.getName());
			thirdPartyLoginInterface.setInterfaceProduct(formbean.getInterfaceProduct());
			thirdPartyLoginInterface.setEnable(formbean.isEnable());
			thirdPartyLoginInterface.setDynamicParameter(dynamicParameter);
			
			thirdPartyLoginInterface.setSort(formbean.getSort());
			

			thirdPartyLoginService.saveThirdPartyLoginInterface(thirdPartyLoginInterface);
		}
	
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}

	/**
	 * 第三方登录接口 显示修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer thirdPartyLoginInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {	
	
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(thirdPartyLoginInterfaceId != null && thirdPartyLoginInterfaceId >0){
			//根据ID查询要修改的数据
			ThirdPartyLoginInterface loginInterface = thirdPartyLoginService.findThirdPartyLoginInterfaceById(thirdPartyLoginInterfaceId);
			
			if(loginInterface != null){
				returnValue.put("thirdPartyLoginInterface", loginInterface);
				
				if(loginInterface.getDynamicParameter() != null && !"".equals(loginInterface.getDynamicParameter().trim())){
					if(loginInterface.getInterfaceProduct().equals(10)){//微信公众号
						WeChatConfig weChatConfig = JsonUtils.toObject(loginInterface.getDynamicParameter(), WeChatConfig.class);
						returnValue.put("weChatConfig",weChatConfig);
						
					}
					
				}
				
			}else{
				error.put("thirdPartyLoginInterfaceId", "第三方登录接口不存在");
			}
		}else{
			error.put("thirdPartyLoginInterfaceId", "第三方登录接口Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 第三方登录接口 修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,ThirdPartyLoginInterface formbean,BindingResult result,Integer thirdPartyLoginInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		if(thirdPartyLoginInterfaceId != null && thirdPartyLoginInterfaceId >0){
			ThirdPartyLoginInterface thirdPartyLoginInterface = thirdPartyLoginService.findThirdPartyLoginInterfaceById(thirdPartyLoginInterfaceId);
			if(thirdPartyLoginInterface != null){
				this.validator.validate(formbean, result); 
				
				String dynamicParameter = "";
				
				WeChatConfig weChatConfig = new WeChatConfig();//微信配置信息
				
				
				if(thirdPartyLoginInterface.getInterfaceProduct().equals(10)){//10.微信配置信息
					String weixin_op_appID = request.getParameter("weixin_op_appID");//开放平台
					String weixin_op_appSecret = request.getParameter("weixin_op_appSecret");
					String weixin_oa_appID = request.getParameter("weixin_oa_appID");//公众号
					String weixin_oa_appSecret = request.getParameter("weixin_oa_appSecret");
					
					if(weixin_op_appID != null && !"".equals(weixin_op_appID.trim())){
						weChatConfig.setOp_appID(weixin_op_appID.trim());
					}else{
						error.put("weixin_op_appID", "开放平台应用唯一标识不能为空");
					}
					
					if(weixin_op_appSecret != null && !"".equals(weixin_op_appSecret.trim())){
						weChatConfig.setOp_appSecret(weixin_op_appSecret.trim());
					}else{
						error.put("weixin_op_appSecret", "开放平台应用密钥不能为空");
					}
					
					if(weixin_oa_appID != null && !"".equals(weixin_oa_appID.trim())){
						weChatConfig.setOa_appID(weixin_oa_appID.trim());
					}else{
						error.put("weixin_oa_appID", "公众号应用唯一标识不能为空");
					}
					
					if(weixin_oa_appSecret != null && !"".equals(weixin_oa_appSecret.trim())){
						weChatConfig.setOa_appSecret(weixin_oa_appSecret.trim());
					}else{
						error.put("weixin_oa_appSecret", "公众号应用密钥不能为空");
					}
					dynamicParameter = JsonUtils.toJSONString(weChatConfig);
					
					
				}
					
				thirdPartyLoginInterface.setId(thirdPartyLoginInterfaceId);
				thirdPartyLoginInterface.setName(formbean.getName());
				thirdPartyLoginInterface.setEnable(formbean.isEnable());
				thirdPartyLoginInterface.setDynamicParameter(dynamicParameter);
				thirdPartyLoginInterface.setSort(formbean.getSort());	
				thirdPartyLoginInterface.setVersion(Integer.parseInt(RandomStringUtils.randomNumeric(5)));//5位随机数
				
				
				if (result.hasErrors()) {  
					List<FieldError> fieldErrorList = result.getFieldErrors();
					if(fieldErrorList != null && fieldErrorList.size() >0){
						for(FieldError fieldError : fieldErrorList){
							error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
						}
					}
				}
				if(error.size() ==0){
					thirdPartyLoginService.updateThirdPartyLoginInterface(thirdPartyLoginInterface);
				}
				
			}else{
				error.put("thirdPartyLoginInterfaceId", "第三方登录接口不存在");
			}
		}else{
			error.put("thirdPartyLoginInterfaceId", "第三方登录接口Id不能为空");
		}
	
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 第三方登录接口 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Integer thirdPartyLoginInterfaceId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(thirdPartyLoginInterfaceId != null && thirdPartyLoginInterfaceId >0){
			thirdPartyLoginService.deleteThirdPartyLoginInterface(thirdPartyLoginInterfaceId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("thirdPartyLoginInterfaceId", "第三方登录接口Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
}

