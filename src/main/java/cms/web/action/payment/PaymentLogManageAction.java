package cms.web.action.payment;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.payment.PaymentLog;
import cms.bean.payment.PaymentVerificationLog;
import cms.service.payment.PaymentService;
import cms.service.setting.SettingService;
import cms.utils.Verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 支付日志管理
 * @author gao
 *
 */
@Controller
@RequestMapping("/control/paymentLog/manage") 
public class PaymentLogManageAction{

	@Resource PaymentService paymentService;//通过接口引用代理返回的对象
	@Resource SettingService settingService;
	
	/**
	 * 支付日志管理 详细显示
	 * @param model
	 * @param paymentRunningNumber
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=show",method=RequestMethod.GET)
	public String addUI(ModelMap model,String paymentRunningNumber,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if(paymentRunningNumber != null && !"".equals(paymentRunningNumber.trim())){
			if(paymentRunningNumber.trim().length() >9 && Verification.isPositiveInteger(paymentRunningNumber)){
				PaymentLog paymentLog = paymentService.findPaymentLogByPaymentRunningNumber(paymentRunningNumber);
				model.addAttribute("paymentLog", paymentLog);
			}
		}
		return "jsp/payment/show_paymentLog";
	}
	
	/**
	 * 支付日志分页
	 * @param paymentModule 支付模块 1.订单支付 3.售后服务换货/返修支付 5.用户充值
	 * @return
	 */
	@RequestMapping(params="method=ajax_paymentVerificationLogPage", method=RequestMethod.GET)
	public String paymentVerificationLogPage(ModelMap model,PageForm pageForm,
			Integer paymentModule,Long parameterId,String userName) {
		
		StringBuffer jpql = new StringBuffer("");
		
		List<Object> params = new ArrayList<Object>();
		//调用分页算法代码
		PageView<PaymentVerificationLog> pageView = new PageView<PaymentVerificationLog>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		if(paymentModule != null && paymentModule >0 && userName != null && !"".equals(userName.trim())){
			
			jpql.append(" and o.parameterId=?").append((params.size()+1));
			params.add(parameterId);
			
			jpql.append(" and o.userName=?").append((params.size()+1));
			params.add(userName);
			
			jpql.append(" and o.paymentModule=?").append((params.size()+1));//and o.code=?1
			params.add(paymentModule);
			
			
			
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			orderby.put("id", "desc");
			
			//删除第一个and
			String sql = StringUtils.difference(" and", jpql.toString());
			
			QueryResult<PaymentVerificationLog> qr = paymentService.getScrollData(PaymentVerificationLog.class,firstindex, pageView.getMaxresult(),sql, params.toArray(),orderby);
			
	
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
			
		}
		model.addAttribute("userName", userName);
		model.addAttribute("parameterId", parameterId);
		return "jsp/payment/ajax_paymentVerificationLogPage";
	}
}

