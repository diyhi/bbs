package cms.web.action.statistic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.queryString.util.UrlEncoded;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.statistic.PV;
import cms.service.setting.SettingService;
import cms.service.statistic.PageViewService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.utils.Verification;

/**
 * 页面浏览量
 *
 */
@Controller
public class PageViewAction {

	//注入业务bean
	@Resource PageViewService pageViewService;//通过接口引用代理返回的对象
	@Resource SettingService settingService;
		
	/**
	 * 页面浏览量列表
	 * @param model
	 * @param pageForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/pageView/list")  
	public String execute(ModelMap model, PageForm pageForm,String start_times,String end_times,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		//错误
		Map<String,String> error = new HashMap<String,String>();		
		Date _start_times = null;
		Date _end_times= null;
		if(start_times != null && !"".equals(start_times.trim())){
			boolean start_createDateVerification = Verification.isTime_minute(start_times.trim());
			if(start_createDateVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
				_start_times = dd.parse(start_times.trim());
			}else{
				error.put("start_times", "请填写正确的日期");
			}
		}
		if(end_times != null && !"".equals(end_times.trim())){
			boolean end_createDateVerification = Verification.isTime_minute(end_times.trim());
			if(end_createDateVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
				_end_times = dd.parse(end_times.trim());
			}else{
				error.put("end_times", "请填写正确的日期");
			}
		}
		//比较时间
		Calendar start=Calendar.getInstance();//时间 起始  
        Calendar end=Calendar.getInstance();//时间 结束
        if(_start_times != null){
        	start.setTime(_start_times);   
        }
        if(_end_times != null){
        	end.setTime(_end_times);   
        }
		if(_start_times != null && _end_times != null){
        	int result =start.compareTo(end);//起始时间与结束时间比较
        	if(result > 0 ){//起始时间比结束时间大
        		error.put("start_times", "起始时间不能比结束时间大");
        	}
		}
		

		
		if(_start_times != null){//起始时间
			jpql.append(" and o.times >= ?"+ (params.size()+1));
			params.add(_start_times);
		}
		if(_end_times != null){//结束时间
			jpql.append(" and o.times <= ?"+ (params.size()+1));
			params.add(_end_times);
		}

		//调用分页算法代码
		PageView<PV> pageView = new PageView<PV>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		orderby.put("times", "desc");//根据sort字段降序排序
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
				
		QueryResult<PV> qr = pageViewService.getScrollData(PV.class,firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);		
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(PV pv : qr.getResultlist()){
				if(pv.getIp() != null && !"".equals(pv.getIp().trim())){
					pv.setIpAddress(IpAddress.queryAddress(pv.getIp()));
				}
				
				if(pv.getUrl() != null && !"".equals(pv.getUrl().trim())){
					pv.setUrl(org.springframework.web.util.UriUtils.decode(pv.getUrl().trim(), "utf-8"));
				}
				if(pv.getReferrer() != null && !"".equals(pv.getReferrer().trim())){
					pv.setReferrer(org.springframework.web.util.UriUtils.decode(pv.getReferrer().trim(), "utf-8"));
				}
			}
		}
		
		if(error.size() ==0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
