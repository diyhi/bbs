package cms.web.action.payment;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.payment.PaymentLog;
import cms.bean.user.User;
import cms.service.payment.PaymentService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 支付日志管理
 * @author gao
 *
 */
@Controller
public class PaymentLogAction{

	@Resource PaymentService paymentService;//通过接口引用代理返回的对象
	@Resource UserService userService;
	@Resource SettingService settingService;

	@RequestMapping("/control/paymentLog/list")  
	public String execute(ModelMap model, PageForm pageForm,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//调用分页算法代码
		PageView<PaymentLog> pageView = new PageView<PaymentLog>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
		
		if(userName != null && !"".equals(userName.trim())){
			User user = userService.findUserByUserName(userName);
			if(user != null){
				QueryResult<PaymentLog> qr =  paymentService.findPaymentLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());
				
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				request.setAttribute("pageView", pageView);
			}
			
		}
		
		

		return "jsp/payment/paymentLogList";
	}
	
}

