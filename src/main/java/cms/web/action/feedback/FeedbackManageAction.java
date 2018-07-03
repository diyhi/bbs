package cms.web.action.feedback;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import cms.bean.feedback.Feedback;
import cms.service.feedback.FeedbackService;
import cms.utils.IpAddress;
import cms.web.action.SystemException;

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
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(ModelMap model,Long feedbackId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(feedbackId != null && feedbackId >0L){
			Feedback feedback = feedbackService.findById(feedbackId);
			if(feedback == null){
				throw new SystemException("在线留言不存在");
			}else{
				if(feedback.getIp() != null && !"".equals(feedback.getIp().trim())){
					feedback.setIpAddress(IpAddress.queryAddress(feedback.getIp()));
				}
				model.addAttribute("feedback", feedback);
			}
		}else{
			throw new SystemException("缺少参数");
		}
		return "jsp/feedback/view_feedback";
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
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long feedbackId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(feedbackId != null && feedbackId >0L){
			int i = feedbackService.deleteFeedback(feedbackId);	
			return "1";
		}
		return "0";
	}
}
