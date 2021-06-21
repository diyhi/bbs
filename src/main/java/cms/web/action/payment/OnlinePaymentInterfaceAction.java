package cms.web.action.payment;


import java.util.LinkedHashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.service.payment.PaymentService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.bean.payment.OnlinePaymentInterface;
/**
 * 在线支付接口管理显示
 *
 */
@Controller
public class OnlinePaymentInterfaceAction{
	//注入业务bean
	@Resource PaymentService paymentService;//通过接口引用代理返回的对象
	
	@Resource SettingService settingService;

	@ResponseBody
	@RequestMapping("/control/onlinePaymentInterface/list")  
	public String execute(ModelMap model, PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//调用分页算法代码
		PageView<OnlinePaymentInterface> pageView = new PageView<OnlinePaymentInterface>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		QueryResult<OnlinePaymentInterface> qr = paymentService.getScrollData(OnlinePaymentInterface.class,firstindex, pageView.getMaxresult(),orderby);
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}

}
