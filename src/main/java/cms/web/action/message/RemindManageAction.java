package cms.web.action.message;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.message.Remind;
import cms.bean.question.Question;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.message.RemindService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.question.QuestionManage;
import cms.web.action.topic.TopicManage;

/**
 * 提醒管理
 *
 */
@Controller
@RequestMapping("/control/remind/manage") 
public class RemindManageAction {

	@Resource RemindService remindService; 
	@Resource QuestionManage questionManage;
	@Resource SettingService settingService;
	@Resource UserService userService;
	@Resource TopicManage topicManage;
	
	@Resource RemindManage remindManage;
	@Resource FileManage fileManage;
	
	
	/**
	 * 提醒列表
	 * @param model
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=remindList",method=RequestMethod.GET)
	public String remindList(PageForm pageForm,ModelMap model,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(id != null && id >0L){
			//调用分页算法代码
			PageView<Remind> pageView = new PageView<Remind>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			
			
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

			//用户Id集合
			Set<Long> userIdList = new HashSet<Long>();
			//用户集合
			Map<Long,User> userMap = new HashMap<Long,User>();
			
			QueryResult<Remind> qr = remindService.findRemindByUserId(id,null,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(Remind remind : qr.getResultlist()){
					userIdList.add(remind.getSenderUserId());
					
					remind.setSendTime(new Timestamp(remind.getSendTimeFormat()));
					if(remind.getReadTimeFormat() != null){
						remind.setReadTime(new Timestamp(remind.getReadTimeFormat()));
					}
					
					if(remind.getTopicId() != null && remind.getTopicId() >0L){
						Topic topic = topicManage.queryTopicCache(remind.getTopicId());//查询缓存
						if(topic != null){
							remind.setTopicTitle(topic.getTitle());
						}
						
					}
					if(remind.getQuestionId() != null && remind.getQuestionId() >0L){
						Question question = questionManage.query_cache_findById(remind.getQuestionId());//查询缓存
						if(question != null){
							remind.setQuestionTitle(question.getTitle());
						}
						
					}
					
				}
			}
			
			if(userIdList != null && userIdList.size() >0){
				for(Long userId : userIdList){
					User user = userService.findUserById(userId);
					if(user != null){
						userMap.put(userId, user);
					}
				}
			}
			if(userMap != null && userMap.size() >0){
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					for(Remind remind : qr.getResultlist()){
						User sender_user = userMap.get(remind.getSenderUserId());
						if(sender_user != null){
							remind.setSenderUserName(sender_user.getUserName());
							remind.setSenderAccount(sender_user.getAccount());
							remind.setSenderNickname(sender_user.getNickname());
							if(sender_user.getAvatarName() != null && !"".equals(sender_user.getAvatarName().trim())){
								remind.setSenderAvatarPath(fileManage.fileServerAddress(request)+sender_user.getAvatarPath());//发送者头像路径
								remind.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
							}
						}
						
					}
				}
				
				
				
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			User user = userService.findUserById(id);
			if(user != null){
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("userId", "用户Id不能为空");
			
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}

	
	/**
	 * 删除提醒
	 * @param model
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 
	@RequestMapping(params="method=deleteRemind", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long userId,Long friendUserId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userId != null && userId >0L && friendUserId != null && friendUserId >0L){
			int i = remindService.deleteUserRemind(new ArrayList()<Long>);
			
			//删除提醒缓存
			remindManage.delete_cache_findUnreadRemindByUserId(userId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}*/
	
	
	/**
	 * 还原提醒
	 * @param model
	 * @param remindId 提醒Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=reductionRemind", method=RequestMethod.POST)
	public String reductionRemind(ModelMap model,Long userId,String remindId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(remindId != null && !"".equals(remindId.trim())){
			int i = remindService.reductionRemind(remindId);
			if(userId != null){
				//删除提醒缓存
				remindManage.delete_cache_findUnreadRemindByUserId(userId);
			}
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("remindId", "提醒Id不能为空");
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
}
