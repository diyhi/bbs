package cms.web.action.payment;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.payment.PaymentLog;
import cms.bean.user.User;
import cms.service.payment.PaymentService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	@Resource FileManage fileManage;

	@ResponseBody
	@RequestMapping("/control/paymentLog/list")  
	public String execute(ModelMap model, PageForm pageForm,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
				
		if(userName != null && !"".equals(userName.trim())){
			//调用分页算法代码
			PageView<PaymentLog> pageView = new PageView<PaymentLog>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
			
			User user = userService.findUserByUserName(userName);
			if(user != null){
				QueryResult<PaymentLog> qr =  paymentService.findPaymentLogPage(user.getId(),user.getUserName(),firstIndex, pageView.getMaxresult());
				
				//将查询结果集传给分页List
				pageView.setQueryResult(qr);
				
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);

				returnValue.put("pageView", pageView);
			}
		}else{
			error.put("userName", "用户名称不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
}

