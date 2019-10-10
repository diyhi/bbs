package cms.web.action.common;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.web.action.statistic.PageViewManage;

/**
 * 访问量统计
 *
 */
@Controller
@RequestMapping("statistic") 
public class StatisticFormAction {
	@Resource PageViewManage pageViewManage;
	/**
	 * 添加统计数据(用来统计ajax访问的URL)
	 * @param model
	 * @param url 当前访问页面URL
	 * @param referrer 上一访问页面URL
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/add", method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String add(ModelMap model,String url,String referrer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(url != null && !"".equals(url.trim())){
			//统计访问量
			pageViewManage.addPV(request,url,referrer);
		}
		return "";
	}
}
