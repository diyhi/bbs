package cms.web.action.feedback;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.feedback.Feedback;
import cms.service.feedback.FeedbackService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;

/**
 * 在线留言管理
 *
 */
@Controller
@RequestMapping("/control/feedback/manage") 
public class FeedbackManageAction {

	@Resource FeedbackService feedbackService;

	/**
	 * 在线留言管理  查看
	 */
	@ResponseBody
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(ModelMap model,Long feedbackId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(feedbackId != null && feedbackId >0L){
			Feedback feedback = feedbackService.findById(feedbackId);
			if(feedback == null){
				error.put("feedbackId", "在线留言不存在");
			}else{
				if(feedback.getIp() != null && !"".equals(feedback.getIp().trim())){
					feedback.setIpAddress(IpAddress.queryAddress(feedback.getIp()));
				}
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,feedback));
			}
		}else{
			error.put("feedbackId", "在线留言Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	
	/**
	 * 在线留言   删除
	 * @param model
	 * @param feedbackId 在线留言Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long feedbackId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(feedbackId != null && feedbackId >0L){
			int i = feedbackService.deleteFeedback(feedbackId);	
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("feedbackId", "在线留言Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
