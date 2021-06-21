package cms.web.action.feedback;


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

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.feedback.Feedback;
import cms.service.feedback.FeedbackService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.utils.Verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 在线留言
 *
 */
@Controller
public class FeedbackAction {
	@Resource FeedbackService feedbackService;
	@Resource SettingService settingService;
	
	@ResponseBody
	@RequestMapping("/control/feedback/list") 
	public String execute(PageForm pageForm,ModelMap model,String start_createDate,String end_createDate,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		//错误
		Map<String,String> error = new HashMap<String,String>();		
		Date _start_createDate = null;
		Date _end_createDate= null;
		if(start_createDate != null && !"".equals(start_createDate.trim())){
			boolean start_createDateVerification = Verification.isTime_minute(start_createDate.trim());
			if(start_createDateVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
				_start_createDate = dd.parse(start_createDate.trim());
			}else{
				error.put("start_createDate", "请填写正确的日期");
			}
		}
		if(end_createDate != null && !"".equals(end_createDate.trim())){
			boolean end_createDateVerification = Verification.isTime_minute(end_createDate.trim());
			if(end_createDateVerification){
				DateFormat dd = new SimpleDateFormat("yyyy-MM-dd HH:mm");		
				_end_createDate = dd.parse(end_createDate.trim());
			}else{
				error.put("end_createDate", "请填写正确的日期");
			}
		}
		//比较时间
		Calendar start=Calendar.getInstance();//时间 起始  
        Calendar end=Calendar.getInstance();//时间 结束
        if(_start_createDate != null){
        	start.setTime(_start_createDate);   
        }
        if(_end_createDate != null){
        	end.setTime(_end_createDate);   
        }
		if(_start_createDate != null && _end_createDate != null){
        	int result =start.compareTo(end);//起始时间与结束时间比较
        	if(result > 0 ){//起始时间比结束时间大
        		error.put("start_createDate", "起始时间不能比结束时间大");
        	}
		}
		

		
		if(_start_createDate != null){//起始时间
			jpql.append(" and o.createDate >= ?"+ (params.size()+1));
			params.add(_start_createDate);
		}
		if(_end_createDate != null){//结束时间
			jpql.append(" and o.createDate <= ?"+ (params.size()+1));
			params.add(_end_createDate);
		}
		
		
		PageView<Feedback> pageView = new PageView<Feedback>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
		
		//调用分页算法类
		QueryResult<Feedback> qr = feedbackService.getScrollData(Feedback.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);		
		
		pageView.setQueryResult(qr);
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}
	}
}
