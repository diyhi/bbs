package cms.web.action.common;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cms.bean.ErrorView;
import cms.bean.follow.Follow;
import cms.bean.follow.Follower;
import cms.bean.membershipCard.MembershipCard;
import cms.bean.membershipCard.MembershipCardOrder;
import cms.bean.membershipCard.Specification;
import cms.bean.message.Remind;
import cms.bean.payment.PaymentLog;
import cms.bean.user.AccessUser;
import cms.bean.user.PointLog;
import cms.bean.user.ResourceEnum;
import cms.bean.user.User;
import cms.bean.user.UserRole;
import cms.bean.user.UserRoleGroup;
import cms.service.follow.FollowService;
import cms.service.membershipCard.MembershipCardService;
import cms.service.message.RemindService;
import cms.service.template.TemplateService;
import cms.service.user.UserRoleService;
import cms.service.user.UserService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.RefererCompare;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.CSRFTokenManage;
import cms.web.action.SystemException;
import cms.web.action.follow.FollowManage;
import cms.web.action.follow.FollowerManage;
import cms.web.action.membershipCard.MembershipCardManage;
import cms.web.action.message.RemindManage;
import cms.web.action.payment.PaymentManage;
import cms.web.action.user.PointManage;
import cms.web.action.user.RoleAnnotation;
import cms.web.action.user.UserManage;
import cms.web.action.user.UserRoleManage;
import cms.web.taglib.Configuration;

/**
 * 会员卡接收表单
 *
 */
@Controller
@RequestMapping("user/control/membershipCard") 
public class MembershipCardFormAction {
	@Resource TemplateService templateService;
	@Resource MembershipCardService membershipCardService;
	@Resource MembershipCardManage membershipCardManage;
	@Resource UserRoleService userRoleService;
	@Resource UserRoleManage userRoleManage;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource PointManage pointManage;
	@Resource PaymentManage paymentManage;
	@Resource CSRFTokenManage csrfTokenManage;
	@Resource UserService userService;
	/**
	 * 购买会员卡   添加
	 * @param model
	 * @param specificationId 规格Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public String add(ModelMap model,Long specificationId,String token,String jumpUrl,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		
		
		Map<String,String> error = new HashMap<String,String>();
		//购买数量
		int quantity =1;
		
		//判断令牌
		if(token != null && !"".equals(token.trim())){	
			String token_sessionid = csrfTokenManage.getToken(request);//获取令牌
			if(token_sessionid != null && !"".equals(token_sessionid.trim())){
				if(!token_sessionid.equals(token)){
					error.put("token", ErrorView._13.name());//令牌错误
				}
			}else{
				error.put("token", ErrorView._12.name());//令牌过期
			}
		}else{
			error.put("token", ErrorView._11.name());//令牌为空
		}
		
		//获取登录用户
	  	AccessUser accessUser = AccessUserThreadLocal.get();

	  	MembershipCard membershipCard = null;
	  	Specification specification = null;
	  	UserRole userRole = null;
	  	if(specificationId != null){
	  		specification = membershipCardService.findSpecificationBySpecificationId(specificationId);
	  		
	  		if(specification != null){
	  			membershipCard = membershipCardService.findById(specification.getMembershipCardId());
	  			if(membershipCard != null){
	  				if(membershipCard.getState() >1){
	  					error.put("membershipCard", ErrorView._1930.name());//会员卡已下架
	  				}
	  			}else{
	  				error.put("membershipCard", ErrorView._1920.name());//会员卡不存在
	  			}
	  			
	  			userRole = userRoleService.findRoleById(membershipCard.getUserRoleId());
	  			if(userRole == null){
	  				error.put("role", ErrorView._1950.name());//角色不存在
	  			}
	  			
	  		}else{
	  			error.put("specification", ErrorView._1910.name());//规格不存在
	  		}
	
		}else{
			error.put("specification", ErrorView._1900.name());//规格Id不能为空
		}
	  	
	  	MembershipCardOrder membershipCardOrder = new MembershipCardOrder();
	  	
	  	if(error.size() == 0){
	  		if(specification.getSellingPrice() != null){
				BigDecimal price = specification.getSellingPrice().multiply(new BigDecimal(String.valueOf(quantity)));//应付款 = 规格销售价*购买数量
				membershipCardOrder.setAccountPayable(price);//应付款
				membershipCardOrder.setPaymentAmount(price);//已支付金额
			}
			if(specification.getPoint() != null){
				Long point = specification.getPoint() * quantity;//应付积分 = 规格积分*购买数量
				membershipCardOrder.setAccountPoint(point);//应付积分
				membershipCardOrder.setPaymentPoint(point);//已支付积分
			}
	  		
		  	//规格库存
		  	if(specification.getStock() < quantity){
		  		error.put("stock", ErrorView._1960.name());//库存不足
		  	}
		  	User user = userService.findUserByUserName(accessUser.getUserName());
			if(membershipCardOrder.getPaymentPoint() >0L){
				if(user.getPoint() < membershipCardOrder.getPaymentPoint()){
					error.put("point", ErrorView._1970.name());//积分不足
				}
				
			}
			//扣除用户金额
			if(membershipCardOrder.getPaymentAmount().compareTo(new BigDecimal("0")) >0){	
				if(user.getDeposit().compareTo(membershipCardOrder.getPaymentAmount()) < 0){
					error.put("deposit", ErrorView._1980.name());//预存款不足
				}
			}
			
			if(!specification.isEnable()){//规格禁用
				error.put("specification", ErrorView._1990.name());//此规格已下架
				
			}
			if(membershipCard.getState() >1){
				error.put("membershipCard", ErrorView._2000.name());//此会员卡已下架
			}
	  	}
		if(error.size() == 0){
			UserRoleGroup add_userRoleGroup = null;
			UserRoleGroup update_userRoleGroup = null;
			
			UserRoleGroup userRoleGroup = userRoleService.findRoleGroupByUserRoleId(userRole.getId(), accessUser.getUserName());
			if(userRoleGroup == null){
				add_userRoleGroup = new UserRoleGroup();
				add_userRoleGroup.setUserName(accessUser.getUserName());
				add_userRoleGroup.setUserRoleId(userRole.getId());
				DateTime dateTime = new DateTime(new Date());
				
				if(specification.getUnit().equals(10)){//时长单位 10.小时
					dateTime = dateTime.plusHours(specification.getDuration());// 增加小时
				}else if(specification.getUnit().equals(20)){//20.日
					dateTime = dateTime.plusDays(specification.getDuration()); // 增加天 
				}else if(specification.getUnit().equals(30)){//30.月
					dateTime = dateTime.plusMonths(specification.getDuration()); // 增加月
				}else if(specification.getUnit().equals(40)){//40.年
					dateTime = dateTime.plusYears(specification.getDuration()); // 增加年
				}
				add_userRoleGroup.setValidPeriodEnd(dateTime.toDate());
				
				
			}else{
				update_userRoleGroup = new UserRoleGroup();
				update_userRoleGroup.setUserName(accessUser.getUserName());
				update_userRoleGroup.setUserRoleId(userRole.getId());
				
				DateTime dateTime = new DateTime(userRoleGroup.getValidPeriodEnd());
				if(specification.getUnit().equals(10)){//时长单位 10.小时
					dateTime = dateTime.plusHours(specification.getDuration());// 增加小时
				}else if(specification.getUnit().equals(20)){//20.日
					dateTime = dateTime.plusDays(specification.getDuration()); // 增加天 
				}else if(specification.getUnit().equals(30)){//30.月
					dateTime = dateTime.plusMonths(specification.getDuration()); // 增加月
				}else if(specification.getUnit().equals(40)){//40.年
					dateTime = dateTime.plusYears(specification.getDuration()); // 增加年
				}
				update_userRoleGroup.setValidPeriodEnd(dateTime.toDate());
			}
		
			
			
			
			membershipCardOrder.setOrderId(membershipCardManage.nextNumber(accessUser.getUserId()));//订单号
			membershipCardOrder.setUserName(accessUser.getUserName());//用户账号
			membershipCardOrder.setCreateDate(new Date());//订单创建时间
			
			membershipCardOrder.setUserRoleId(membershipCard.getUserRoleId());//购买的会员卡用户角色Id
			membershipCardOrder.setRoleName(userRole.getName());//购买的会员卡用户角色名称
			membershipCardOrder.setMembershipCardId(membershipCard.getId());//购买的会员卡Id
			membershipCardOrder.setSpecificationId(specification.getId());//购买的会员卡规格Id
			membershipCardOrder.setSpecificationName(specification.getSpecificationName());//购买的会员卡规格名称
			membershipCardOrder.setQuantity(quantity);//购买的会员卡数量
			membershipCardOrder.setDuration(specification.getDuration());//购买的会员卡有效期时长
			membershipCardOrder.setUnit(specification.getUnit());//购买的会员卡时长单位 10.小时 20.日 30.月 40.年
			
			//扣除用户积分
			Object new_pointLog = null;
			if(membershipCardOrder.getPaymentPoint() >0L){
				PointLog pointLog = new PointLog();
				pointLog.setId(pointManage.createPointLogId(accessUser.getUserId()));
				pointLog.setModule(500);//模块 500.会员卡支付
				pointLog.setParameterId(membershipCardOrder.getOrderId());//参数Id 
				pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
				pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
				
				pointLog.setPoint(membershipCardOrder.getPaymentPoint());//积分
				pointLog.setUserName(accessUser.getUserName());//用户名称
				pointLog.setRemark("");
				pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出 
				new_pointLog = pointManage.createPointLogObject(pointLog);
			}
			
			Object new_paymentLog = null;
			//扣除用户金额
			if(membershipCardOrder.getPaymentAmount().compareTo(new BigDecimal("0")) >0){
				
			
				PaymentLog paymentLog = new PaymentLog();
				paymentLog.setPaymentRunningNumber(paymentManage.createRunningNumber(accessUser.getUserId()));//支付流水号
				paymentLog.setPaymentModule(1);//支付模块 1.订单支付
				paymentLog.setParameterId(membershipCardOrder.getOrderId());//参数Id 
				paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
				paymentLog.setOperationUserName(accessUser.getUserName());
				
				paymentLog.setAmount(membershipCardOrder.getPaymentAmount());//金额
				paymentLog.setInterfaceProduct(-1);//接口产品
				paymentLog.setTradeNo("");//交易号
				paymentLog.setUserName(accessUser.getUserName());//用户名称
				paymentLog.setRemark("");//备注
				
				
				paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出 
				new_paymentLog = paymentManage.createPaymentLogObject(paymentLog);
					
			}
			
			

			
			try {
				//保存会员卡
				membershipCardService.saveMembershipCardOrder(membershipCardOrder,add_userRoleGroup,update_userRoleGroup,new_pointLog,new_paymentLog);
				
				//删除规格缓存
				membershipCardManage.delete_cache_findSpecificationByMembershipCardId(membershipCard.getId());
				userRoleManage.delete_cache_findRoleGroupByUserName(membershipCardOrder.getUserName());
				
			} catch (SystemException e) {
				error.put("membershipCard", e.getMessage());//创建会员卡订单错误
				
			}catch (org.springframework.orm.jpa.JpaSystemException e) {
				error.put("membershipCard", ErrorView._1940.name());//创建会员卡订单错误
				
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
		if(isAjax == true){
			
    		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
    		
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
				redirectAttrs.addFlashAttribute("error", returnError);//重定向传参
				

				String referer = request.getHeader("referer");	
				
				
				referer = StringUtils.removeStartIgnoreCase(referer,Configuration.getUrl(request));//移除开始部分的相同的字符,不区分大小写
				referer = StringUtils.substringBefore(referer, ".");//截取到等于第二个参数的字符串为止
				referer = StringUtils.substringBefore(referer, "?");//截取到等于第二个参数的字符串为止
				
				String queryString = request.getQueryString() != null && !"".equals(request.getQueryString().trim()) ? "?"+request.getQueryString() :"";

				return "redirect:/"+referer+queryString;
	
			}
			
			
			if(jumpUrl != null && !"".equals(jumpUrl.trim())){
				String url = Base64.decodeBase64URL(jumpUrl.trim());
				
				return "redirect:"+url;
			}else{//默认跳转
				model.addAttribute("message", "购买会员卡成功");
				String referer = request.getHeader("referer");
				if(RefererCompare.compare(request, "login")){//如果是登录页面则跳转到首页
					referer = Configuration.getUrl(request);
				}
				model.addAttribute("urlAddress", referer);
				
				String dirName = templateService.findTemplateDir_cache();
				
				
				return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/jump";	
			}
		}
	}
	
	

	
	
}
