package cms.web.action.thirdParty;

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
import cms.bean.thirdParty.ThirdPartyLoginInterface;
import cms.service.setting.SettingService;
import cms.service.thirdParty.ThirdPartyLoginService;
import cms.utils.JsonUtils;

/**
 * 第三方登录接口管理显示
 *
 */
@Controller
public class ThirdPartyLoginInterfaceAction {
	@Resource SettingService settingService;
	@Resource ThirdPartyLoginService thirdPartyLoginService;

	@ResponseBody
	@RequestMapping("/control/thirdPartyLoginInterface/list")  
	public String execute(ModelMap model, PageForm pageForm,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//调用分页算法代码
		PageView<ThirdPartyLoginInterface> pageView = new PageView<ThirdPartyLoginInterface>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		QueryResult<ThirdPartyLoginInterface> qr = thirdPartyLoginService.getScrollData(ThirdPartyLoginInterface.class,firstindex, pageView.getMaxresult(),orderby);
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
}
