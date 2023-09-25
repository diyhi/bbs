package cms.web.action.common;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import cms.bean.ErrorView;
import cms.bean.payment.Bank;
import cms.bean.payment.OnlinePaymentInterface;
import cms.bean.payment.PaymentLog;
import cms.bean.payment.PaymentVerificationLog;
import cms.bean.setting.SystemSetting;
import cms.bean.user.AccessUser;
import cms.bean.user.User;
import cms.service.payment.PaymentService;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.service.user.UserGradeService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.payment.OnlinePaymentInterfaceManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.payment.impl.mobile.AlipayConfig_Mobile;
import cms.web.action.payment.impl.pc.AlipayConfig_PC;
import cms.web.action.user.UserManage;
import cms.web.taglib.Configuration;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;




/**
 * 默认付款页
 * @author GAO
 *
 */
@Controller
public class PaymentFormAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	@Resource UserService userService;
	@Resource UserGradeService userGradeService;
	@Resource PaymentService paymentService;
	@Resource SettingService siteService;
	@Resource SettingService settingService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;

	@Resource OnlinePaymentInterfaceManage onlinePaymentInterfaceManage;
	@Resource PaymentManage paymentManage;
	
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource UserManage userManage;
	
	@Resource AlipayConfig_Mobile alipayConfig_Mobile;
	@Resource AlipayConfig_PC alipayConfig_PC;
	private List<Integer> number = Arrays.asList(5);//1,3,5
	
	/**
	 * 付款显示页面
	 * @param model
	 * @param paymentModule 支付模块
	 * @param orderId 订单Id
	 * @param aftermarketId 售后服务Id
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/payment", method=RequestMethod.GET)
	public String paymentUI(ModelMap model,Integer paymentModule, 
			Long orderId,Long aftermarketId,RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,String> error = new HashMap<String,String>();//错误
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

		if(!number.contains(paymentModule)){
			error.put("message", "支付模块参数错误");	
		}

		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		
		String accessPath = accessSourceDeviceManage.accessDevices(request);
		
		if(error.size() == 0){
			//显示所有支付接口
			List<OnlinePaymentInterface> onlinePaymentInterfaceList = paymentService.findAllEffectiveOnlinePaymentInterface_cache();
			//设置银行
			if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
				Iterator<OnlinePaymentInterface> onlinePaymentInterface_iter = onlinePaymentInterfaceList.iterator();
				while (onlinePaymentInterface_iter.hasNext()) {
					OnlinePaymentInterface onlinePaymentInterface = onlinePaymentInterface_iter.next();
					if(accessPath.equals("pc")){
						if(!onlinePaymentInterfaceManage.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 1)){
							onlinePaymentInterface_iter.remove();
							continue;
						}
					}else if(accessPath.equals("wap")){
						if(!onlinePaymentInterfaceManage.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 2)){
							onlinePaymentInterface_iter.remove();
							continue;
						}
					}
					onlinePaymentInterface.setDynamicParameter(null);
					onlinePaymentInterface.setBankList(onlinePaymentInterfaceManage.getBankList(onlinePaymentInterface.getInterfaceProduct()));
					
				}
			}
			model.addAttribute("onlinePaymentInterfaceList",onlinePaymentInterfaceList);
			returnValue.put("onlinePaymentInterfaceList",onlinePaymentInterfaceList);
		}
		
		if(isAjax == true){
			if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", error);
    		}else{
    			returnValue.put("success", "true");
    			
    		}
			
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			if(error != null && error.size() >0){//如果有错误
				for (Map.Entry<String,String> entry : error.entrySet()) {
					if(entry.getKey().equals("message")){
						model.addAttribute("message",entry.getValue());//提示
			  			return "/templates/"+dirName+"/"+accessPath+"/message";
					}
					
				}
			}
			
			return "/templates/"+dirName+"/"+accessPath+"/payment";	
		}
		
		
	}
	
	
	/**
	 * 支付校验
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/paymentVerification", method={RequestMethod.GET})
	@ResponseBody//方式来做ajax,直接返回字符串
	public String paymentVerification(ModelMap model,Integer paymentModule,Long orderId,Long aftermarketId,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		
		//返回值
		Map<String,Object> returnValue = new HashMap<String,Object>();	
		//错误
		Map<String,String> error = new HashMap<String,String>();
	
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		User _user = userService.findUserByUserName(accessUser.getUserName());//查询用户数据
		if(_user != null){
			String _rechargeAmount = request.getParameter("rechargeAmount");//充值金额

			if(_rechargeAmount != null && !"".equals(_rechargeAmount.trim())){
				if(_rechargeAmount.trim().length()>12){
					error.put("rechargeAmount", "不能超过12位数字");	
				}else{
					boolean rechargeAmountVerification = _rechargeAmount.trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
					if(!rechargeAmountVerification){
						error.put("rechargeAmount", "请填写金额");	
					}
				}	
			}
		}else{
			error.put("message", "用户不存在");	
		}
		
		
		if(error != null && error.size() >0){
			returnValue.put("success", "false");
			returnValue.put("error", error);
			
		}else{
			returnValue.put("success", "true");
			
		}
		return JsonUtils.toJSONString(returnValue);
	}
	
	
	/**
	 * 付款
	 * @param model
	 * @param paymentModule 支付模块
	 * @param paymentBank 支付银行 由 接口产品_银行简码 组成
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/user/control/payment", method=RequestMethod.POST)
	public String payment(ModelMap model,RedirectAttributes redirectAttrs,Integer paymentModule, 
			String paymentBank,String token,
			HttpServletRequest request,HttpServletResponse response)throws Exception {

		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		
	    String accessPath = accessSourceDeviceManage.accessDevices(request);
	   
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		
		if(!number.contains(paymentModule)){
			error.put("message", "支付模块参数错误");
		}
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		if(systemSetting.getCloseSite().equals(2)){
			error.put("message", ErrorView._21.name());//只读模式不允许提交数据
		}
		
		//处理CSRF令牌
		csrfTokenManage.processCsrfToken(request, token,error);
				
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();
		
		User user = userService.findUserByUserName(accessUser.getUserName());//查询用户数据
		
		if(user == null){
			error.put("message", "用户不存在");
		}

		//充值金额
		BigDecimal rechargeAmount = new BigDecimal("0");
		

		String _rechargeAmount = request.getParameter("rechargeAmount");//充值金额
		
		//显示所有支付接口
		List<OnlinePaymentInterface> onlinePaymentInterfaceList = null;
		
		if(error.size() ==0){
			if(_rechargeAmount != null && !"".equals(_rechargeAmount.trim())){
				if(_rechargeAmount.trim().length()>12){
					error.put("rechargeAmount", "不能超过12位数字");	
				}else{
					boolean rechargeAmountVerification = _rechargeAmount.trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
					if(rechargeAmountVerification){
						rechargeAmount = new BigDecimal(_rechargeAmount.trim());
					}else{
						error.put("rechargeAmount", "请填写金额");	
					}
				}	
			}
			
			onlinePaymentInterfaceList = paymentService.findAllEffectiveOnlinePaymentInterface_cache();
			//设置银行
			if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
				Iterator<OnlinePaymentInterface> onlinePaymentInterface_iter = onlinePaymentInterfaceList.iterator();
				while (onlinePaymentInterface_iter.hasNext()) {
					OnlinePaymentInterface onlinePaymentInterface = onlinePaymentInterface_iter.next();
	
					if(accessPath.equals("pc")){
						if(!onlinePaymentInterfaceManage.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 1)){
							onlinePaymentInterface_iter.remove();
							continue;
						}
					}else if(accessPath.equals("wap")){
						if(!onlinePaymentInterfaceManage.isSupportEquipment(onlinePaymentInterface.getSupportEquipment(), 2)){
							onlinePaymentInterface_iter.remove();
							continue;
						}
					}
					onlinePaymentInterface.setDynamicParameter(null);
					onlinePaymentInterface.setBankList(onlinePaymentInterfaceManage.getBankList(onlinePaymentInterface.getInterfaceProduct()));
					
				}
			}
			
			
			//添加支付校验日志
			PaymentVerificationLog paymentVerificationLog = new PaymentVerificationLog();
					

			if(paymentModule.equals(5)){//5.用户充值
				Integer interfaceProduct = null;//接口产品
				String code = null;//银行简码
				boolean flag = false;//提交的接口银行简码存在,标记要跳转网银支付
				
				if(rechargeAmount.compareTo(new BigDecimal("0")) <=0){
					error.put("paymentBank", "没有充值金额");		
				}else{//如果'充值金额'大于0
					if(paymentBank != null && !"".equals(paymentBank.trim())){
						String[] p = paymentBank.trim().split("_");
						if(p != null && p.length >=2){
							
							if(p[0] != null && p[1] != null){
								interfaceProduct = Integer.parseInt(p[0]);
								code = p[1];
							}
							
						}
					}
					if(interfaceProduct == null){
						error.put("paymentBank", "支付接口不存在");
					}
					if(code == null || "".equals(code.trim())){
						error.put("paymentBank", "请选择银行");
					}
					
					if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){
						for(OnlinePaymentInterface onlinePaymentInterface : onlinePaymentInterfaceList){
							if(onlinePaymentInterface.getInterfaceProduct().equals(interfaceProduct)){
								List<Bank> bankList = onlinePaymentInterface.getBankList();
								if(bankList != null && bankList.size() >0){
									for(Bank bank : bankList){
										if(code.equals(bank.getCode())){
											//回显需要
											bank.setSelected(true);//选中
											flag = true;
										}
									}
								}
							
							}
						}
					}
					if(flag == false){
						error.put("paymentBank", "银行选择错误");	
					}
				}
				
				if(error.size() == 0){
					if(rechargeAmount.compareTo(new BigDecimal("0")) >0){
						if(flag == true){	
							String paymentRequestId = paymentManage.createRunningNumber(user.getId());
							paymentVerificationLog.setId(paymentRequestId);
							paymentVerificationLog.setParameterId(user.getId());
							paymentVerificationLog.setPaymentModule(5);
							paymentVerificationLog.setUserName(accessUser.getUserName());
							paymentVerificationLog.setTimes(new Date());
							paymentVerificationLog.setPaymentAmount(rechargeAmount);
							paymentService.savePaymentVerificationLog(paymentVerificationLog);

							//调用在线支付API,如果为默认提交方式，则直接跳转不再返回数据
						//	 return "redirect:/api";   String.
							String createHtmlText = this.jumpPayAPI(isAjax,interfaceProduct,paymentModule,paymentRequestId,user.getId(),"充值"+user.getId().toString(),String.valueOf(rechargeAmount),code,request, response);
							if(isAjax == true){
								returnValue.put("success", "true");
								returnValue.put("callbackData", createHtmlText);
								WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
							}
							
							return null;
						}
					}
				}
			}
			
		}
		Map<String,String> returnError = new HashMap<String,String>();//错误
		if(error.size() >0){
			//将枚举数据转为错误提示字符
    		for (Map.Entry<String,String> entry : error.entrySet()) {
    			if(ErrorView.get(entry.getValue()) != null){
    				returnError.put(entry.getKey(),  ErrorView.get(entry.getValue()));
    			}else{
    				returnError.put(entry.getKey(),  entry.getValue());
    			}
    			
			}
		}
		if(error.size() >0){
			
			redirectAttrs.addFlashAttribute("error", returnError);
			redirectAttrs.addFlashAttribute("onlinePaymentInterfaceList",onlinePaymentInterfaceList);
			
			returnValue.put("error", returnError);
			returnValue.put("onlinePaymentInterfaceList",onlinePaymentInterfaceList);
			if(paymentModule.equals(5)){//5.用户充值   
				redirectAttrs.addFlashAttribute("rechargeAmount",rechargeAmount);
				returnValue.put("rechargeAmount",rechargeAmount);
			}

		
		}
		
		
		
		if(isAjax == true){
			if(error != null && error.size() >0){
    			returnValue.put("success", "false");
    			returnValue.put("error", returnError);
    		}else{
    			returnValue.put("success", "true");
    			
    		}
			
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			
			if(error != null && error.size() >0){//如果有错误
				
				String queryString = "?paymentModule="+paymentModule;

				return "redirect:/user/control/payment"+queryString;			
			}
			
			model.addAttribute("message", "支付失败");
			return "/templates/"+dirName+"/"+accessPath+"/message";	
		}
	}
	
	
	
	
	/** -----------------------------------------  回调通知  -------------------------------------- **/
	
	
	
	/**
	 * 支付回调通知
	 * @param interfaceProduct 接口产品
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/notify/{interfaceProduct}", method={RequestMethod.POST})
	@ResponseBody//方式来做ajax,直接返回字符串
	public String notify(ModelMap model,@PathVariable Integer interfaceProduct,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		BigDecimal paymentAmount = new BigDecimal("0");
		
		if(interfaceProduct.equals(1)){//1.支付宝即时到账 
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
				params.put(name, valueStr);
			}
			
			boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig_PC.getAlipayPublicKey(interfaceProduct),alipayConfig_PC.CHARSET, alipayConfig_PC.SIGNTYPE); //调用SDK验证签名
			
			if(signVerified){
				//商户订单号
				String out_trade_no = request.getParameter("out_trade_no");
			
				//支付宝交易号
				String trade_no = request.getParameter("trade_no");
			
				//交易状态
				String trade_status = request.getParameter("trade_status");
				
				//交易金额
				String total_amount = request.getParameter("total_amount");
				if(total_amount != null && !"".equals(total_amount)){
					paymentAmount = new BigDecimal(total_amount);
				}
				
				
				if(trade_status.equals("TRADE_FINISHED")){
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				}else if (trade_status.equals("TRADE_SUCCESS")){
					
					//付款完成后，支付宝系统发送该交易状态通知
					String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no +" 系统支付流水号："+out_trade_no;
					this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);
					
					
					//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
					
					//注意：
					//付款完成后，支付宝系统发送该交易状态通知
				}
				return "success";
				
			}
		}else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
				params.put(name, valueStr);
			}
			
			//商户订单号
			String out_trade_no = request.getParameter("out_trade_no");

			//支付宝交易号
			String trade_no = request.getParameter("trade_no");
			//交易状态(同步没有这个参数)
			String trade_status = request.getParameter("trade_status");
			
			//交易金额
			String total_amount = request.getParameter("total_amount");
			if(total_amount != null && !"".equals(total_amount)){
				paymentAmount = new BigDecimal(total_amount);
			}
			if(params != null && params.size() >0){
				
				//计算得出通知验证结果
				boolean verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig_Mobile.getAlipayPublicKey(interfaceProduct), alipayConfig_Mobile.CHARSET, "RSA2");
				if(verify_result){//验证成功

					if(trade_status.equals("TRADE_FINISHED")){
						//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
					//如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
					} else if (trade_status.equals("TRADE_SUCCESS")){
						//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//如果签约的是可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
						
						String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no+" 系统支付流水号："+out_trade_no ;
						this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);
						
					}
					return "success";
				}
			}
		}	
		return "fail";
	}
	
	/**
	 * 支付完成通知
	 * @param interfaceProduct 接口产品 0:账户支付
	 * @param paymentModule 支付模块
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/paymentCompleted/{interfaceProduct}/{paymentModule}/{parameterId}", method={RequestMethod.GET})
	public String paySuccess(ModelMap model,@PathVariable Integer interfaceProduct,
			@PathVariable Integer paymentModule,@PathVariable Long parameterId,
			HttpServletRequest request, HttpServletResponse response)throws Exception {

		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		BigDecimal paymentAmount = new BigDecimal("0");
		
		PaymentVerificationLog paymentVerificationLog = null;
		
		if(interfaceProduct.equals(1)){//1.支付宝即时到账  
			//获取支付宝GET过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//	valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			
			boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig_PC.getAlipayPublicKey(interfaceProduct), alipayConfig_PC.CHARSET, alipayConfig_PC.SIGNTYPE); //调用SDK验证签名
			if(signVerified){
				//商户订单号
				String out_trade_no = request.getParameter("out_trade_no");
			
				//支付宝交易号
				String trade_no = request.getParameter("trade_no");
			
				
				//付款金额
				String total_amount = request.getParameter("total_amount");
				if(total_amount != null && !"".equals(total_amount)){
					paymentAmount = new BigDecimal(total_amount);
				}
				
				//判断该笔订单是否在商户网站中已经做过处理
				//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
				//如果有做过处理，不执行商户的业务程序
				String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no+" 系统支付流水号："+out_trade_no ;
				paymentVerificationLog = this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);
				
				
			}
		}else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay)
			//获取支付宝GET过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//	valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)
			
			//商户订单号
			String out_trade_no = request.getParameter("out_trade_no");

			//支付宝交易号
			String trade_no = request.getParameter("trade_no");
			
			//交易金额
			String total_amount = request.getParameter("total_amount");
			if(total_amount != null && !"".equals(total_amount)){
				paymentAmount = new BigDecimal(total_amount);
			}
			//计算得出通知验证结果
			boolean verify_result = AlipaySignature.rsaCheckV1(params, alipayConfig_Mobile.getAlipayPublicKey(interfaceProduct), alipayConfig_Mobile.CHARSET, "RSA2");
			
			if(verify_result){//验证成功
				
				String remark = "在线付款金额："+paymentAmount+"元；支付宝交易号："+trade_no+" 系统支付流水号："+out_trade_no ;
				
				paymentVerificationLog = this.systemPayment(out_trade_no,interfaceProduct,paymentAmount,remark,trade_no);

			
				
			}

		}
		
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();

		model.addAttribute("paymentModule",paymentModule);
		returnValue.put("paymentModule",paymentModule);

		if(accessUser != null){
			model.addAttribute("userId",accessUser.getUserId());
			returnValue.put("userId",accessUser.getUserId());
		}
		
		if(isAjax == true){
			WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			return "/templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/paymentCompleted";	
		}
	}
	
	/**
	 * 系统付款
	 * @param paymentRunningNumber 支付流水号
	 * @param interfaceProduct 接口产品
	 * @param paymentAmount 支付金额
	 * @param remark 备注
	 * @param tradeNo 交易号
	 */
	private PaymentVerificationLog systemPayment(String paymentRunningNumber,Integer interfaceProduct,BigDecimal paymentAmount,String remark,String tradeNo){
		//校验支付校验日志有数据存在
		PaymentVerificationLog paymentVerificationLog = paymentService.findPaymentVerificationLogById(paymentRunningNumber);
		if(paymentVerificationLog != null){
			
			//如果发起支付金额与通知时获取的金额不一致
			if(paymentAmount.compareTo(paymentVerificationLog.getPaymentAmount()) != 0){
				return null;
			}
			if(paymentVerificationLog.getPaymentModule().equals(5)){//用户充值
				//给用户充值并写入支付日志
				
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
				paymentLog.setPaymentModule(5);//支付模块 5.用户充值
				paymentLog.setSourceParameterId(String.valueOf(paymentVerificationLog.getParameterId()));//参数Id 
				paymentLog.setOperationUserType(2);//用户类型  0:系统  1: 员工  2:会员
				paymentLog.setOperationUserName(paymentVerificationLog.getUserName());//操作用户名称
				paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出 
				paymentLog.setAmount(paymentAmount);//金额
				paymentLog.setInterfaceProduct(interfaceProduct);//接口产品
				paymentLog.setTradeNo(tradeNo);//交易号
				paymentLog.setUserName(paymentVerificationLog.getUserName());//用户名称
				Object new_paymentLog = paymentManage.createPaymentLogObject(paymentLog);
				
				userService.onlineRecharge(paymentRunningNumber,paymentVerificationLog.getUserName(),paymentAmount,new_paymentLog);
				
				User user = userManage.query_cache_findUserByUserName(paymentVerificationLog.getUserName());
				if(user != null){
					//删除缓存
					userManage.delete_cache_findUserById(user.getId());
					userManage.delete_cache_findUserByUserName(user.getUserName());
				}
			}
			
		}
		return paymentVerificationLog;
	}
	
	
	/**
	 * 跳转支付API
	 * @param isAjax 是否以Ajax方式提交数据
	 * @param interfaceProduct 接口产口
	 * @param paymentModule 支付模块
	 * @param paymentRunningNumber 支付流水号
	 * @param parameterId参数Id    订单Id 售后服务服务Id 用户Id
	 * @param orderName 订单名称
	 * @param total 付款金额
	 * @param code 银行简码
	 * @param 
	 * @param 
	 * @param 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private String jumpPayAPI(boolean isAjax,Integer interfaceProduct,Integer paymentModule,String paymentRunningNumber,Long parameterId,String orderName,String total,String code,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		//支付接口生成的html,只有Ajax方式提交数据才返回
		String createHtmlText = null;

		if(interfaceProduct.equals(1)){//1.支付宝即时到账 
			String domain = WebUtil.getOriginDomain(request);
			
			if(domain == null || "".equals(domain.trim())){
				domain = Configuration.getUrl(request);
			}
			
			// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
			String notify_url = Configuration.getUrl(request)+"notify/"+interfaceProduct;

			// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
			String return_url = domain +"paymentCompleted/"+interfaceProduct+"/"+paymentModule+"/"+parameterId;
			
			AlipayClient client = alipayConfig_PC.getAlipayClient(interfaceProduct);
			
			if(client != null){
				//设置请求参数
				AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
				// 设置同步地址
				alipayRequest.setReturnUrl(return_url);
				// 设置异步通知地址
				alipayRequest.setNotifyUrl(notify_url);
				 
				//商户订单号，商户网站订单系统中唯一订单号，必填
				String out_trade_no = paymentRunningNumber;
				//付款金额，必填
				String total_amount = total;
				//订单名称，必填
				String subject = orderName;
				//商品描述，可空
			//	String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
				
				alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
						+ "\"total_amount\":\""+ total_amount +"\"," 
						+ "\"subject\":\""+ subject +"\"," 
						+ "\"timeout_express\":\"15m\"," //可空 该笔订单允许的最晚付款时间，逾期将关闭交易
			//			+ "\"body\":\""+ body +"\"," 
						+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
				//建立请求
				String form = client.pageExecute(alipayRequest).getBody();
				createHtmlText = form;
				if(isAjax ==false){//如果为默认表单提交
					response.setContentType("text/html;charset=UTF-8");
					response.setHeader("Cache-Control", "no-store"); //HTTP1.1	
					response.setHeader("Pragma", "no-cache"); //HTTP1.0
					response.setDateHeader("Expires", 0); 
					response.getWriter().print(form);	
					response.getWriter().close();
				}
			}
		}else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay)
			String domain = WebUtil.getOriginDomain(request);
			
			if(domain == null || "".equals(domain.trim())){
				domain = Configuration.getUrl(request);
			}
			
			// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
			String notify_url = Configuration.getUrl(request)+"notify/"+interfaceProduct;
			// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
			String return_url = domain+"paymentCompleted/"+interfaceProduct+"/"+paymentModule+"/"+parameterId;
		 
		    // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
		    //调用RSA签名方式
			AlipayClient client = alipayConfig_Mobile.getAlipayClient(interfaceProduct);
			if(client != null){
				AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();
			    
			    // 封装请求支付信息
			    AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
			    model.setOutTradeNo(paymentRunningNumber);// 商户订单号，商户网站订单系统中唯一订单号，必填
			    model.setSubject(orderName);// 订单名称，必填
			    model.setTotalAmount(total);// 付款金额，必填
			  //  model.setBody(body);// 商品描述，可空
			    model.setTimeoutExpress("15m");// 超时时间 可空  该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。注：若为空，则默认为15d。
			    model.setProductCode("QUICK_WAP_PAY");// 销售产品码 必填
			    alipay_request.setBizModel(model);
			    // 设置异步通知地址
			    alipay_request.setNotifyUrl(notify_url);
			    // 设置同步地址
			    alipay_request.setReturnUrl(return_url);   
			    
			    // form表单生产
			    // 调用SDK生成表单
			    String form = client.pageExecute(alipay_request).getBody();
			    createHtmlText = form;
			    if(isAjax ==false){//如果为默认表单提交
					response.setHeader("Cache-Control", "no-store"); //HTTP1.1	
					response.setHeader("Pragma", "no-cache"); //HTTP1.0
					response.setDateHeader("Expires", 0); 
			    	response.setContentType("text/html;charset=" + alipayConfig_Mobile.CHARSET); 
				    response.getWriter().write(form);//直接将完整的表单html输出到页面 
				    response.getWriter().flush(); 
				    response.getWriter().close();
			    	
			    }
			}
		}
	
		return createHtmlText;
	}	
}
