package cms.web.action.message;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.message.PrivateMessage;
import cms.bean.user.User;
import cms.service.message.PrivateMessageService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 私信管理
 *
 */
@Controller
@RequestMapping("/control/privateMessage/manage") 
public class PrivateMessageManageAction {
	private static final Logger logger = LogManager.getLogger(PrivateMessageManageAction.class);
	
	@Resource PrivateMessageService privateMessageService; 
	
	@Resource SettingService settingService;
	@Resource UserService userService;
	
	@Resource PrivateMessageManage privateMessageManage;
	/**
	 * 私信列表
	 * @param model
	 * @param id 用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=privateMessageList",method=RequestMethod.GET)
	public String privateMessageList(PageForm pageForm,ModelMap model,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(id != null && id >0L){
			//调用分页算法代码
			PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			
			
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

			//用户Id集合
			Set<Long> userIdList = new HashSet<Long>();
			//用户集合
			Map<Long,User> userMap = new HashMap<Long,User>();
			
			QueryResult<PrivateMessage> qr = privateMessageService.findPrivateMessageByUserId(id,firstIndex,pageView.getMaxresult());
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(PrivateMessage privateMessage : qr.getResultlist()){
					userIdList.add(privateMessage.getSenderUserId());//发送者用户Id 
					userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id

					privateMessage.setSendTime(new Timestamp(privateMessage.getSendTimeFormat()));
					if(privateMessage.getReadTimeFormat() != null){
						privateMessage.setReadTime(new Timestamp(privateMessage.getReadTimeFormat()));
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
					for(PrivateMessage privateMessage : qr.getResultlist()){
						User friend_user = userMap.get(privateMessage.getFriendUserId());
						if(friend_user != null){
							privateMessage.setFriendUserName(friend_user.getUserName());//私信对方用户名称
						}
						User sender_user = userMap.get(privateMessage.getSenderUserId());
						if(sender_user != null){
							privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
						}
						
					}
				}
				
				
				
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			
			model.addAttribute("pageView", pageView);
		}
		
		
		
		return "jsp/message/privateMessageList";
	}
	
	/**
	 * 私信对话列表
	 * @param model
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=privateMessageChatList",method=RequestMethod.GET)
	public String privateMessageChatList(PageForm pageForm,ModelMap model,Long id,Long friendUserId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//调用分页算法代码
		PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
			
				
		//用户Id集合
		Set<Long> userIdList = new HashSet<Long>();
		//用户集合
		Map<Long,User> userMap = new HashMap<Long,User>();
		
		if(id != null && id >0L && friendUserId !=null && friendUserId >0L){
			//对话用户
			User chatUser = userService.findUserById(friendUserId);
			if(chatUser != null){
				model.addAttribute("chatUser", chatUser);
			}
			
			
			QueryResult<PrivateMessage> qr = privateMessageService.findPrivateMessageChatByUserId(id,friendUserId,null,firstIndex,pageView.getMaxresult(),2);
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				for(PrivateMessage privateMessage : qr.getResultlist()){
					userIdList.add(privateMessage.getSenderUserId());//发送者用户Id 
					userIdList.add(privateMessage.getReceiverUserId());//接受者用户Id
					privateMessage.setSendTime(new Timestamp(privateMessage.getSendTimeFormat()));
					if(privateMessage.getReadTimeFormat() != null){
						privateMessage.setReadTime(new Timestamp(privateMessage.getReadTimeFormat()));
					}
				}
			}
			
			if(userIdList != null && userIdList.size() >0){
				for(Long _userId : userIdList){
					User user = userService.findUserById(_userId);
					if(user != null){
						userMap.put(_userId, user);
					}
				}
			}
			if(userMap != null && userMap.size() >0){
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					for(PrivateMessage privateMessage : qr.getResultlist()){
						User friend_user = userMap.get(privateMessage.getFriendUserId());
						
						if(friend_user != null){
							privateMessage.setFriendUserName(friend_user.getUserName());//私信对方用户名称
						}
						User sender_user = userMap.get(privateMessage.getSenderUserId());
						if(sender_user != null){
							privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
						}
						
					}
				}
				
				
				
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			model.addAttribute("pageView", pageView);
		}
		
		return "jsp/message/privateMessageChatList";
	}
	
	
	/**
	 * 删除私信对话
	 * @param model
	 * @param userId 用户Id
	 * @param friendUserId 对方用户Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=deletePrivateMessageChat", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long userId,Long friendUserId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(userId != null && userId >0L && friendUserId != null && friendUserId >0L){
			int i = privateMessageService.deletePrivateMessage(userId, friendUserId);
			
			//删除私信缓存
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(userId);
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(friendUserId);
			return "1";
		}
		return "0";
	}
	
	/**
	 * 还原私信
	 * @param model
	 * @param privateMessageId 私信Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=reductionPrivateMessage", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reductionPrivateMessage(ModelMap model,Long userId,String privateMessageId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(privateMessageId != null && !"".equals(privateMessageId.trim())){
			int i = privateMessageService.reductionPrivateMessage(privateMessageId);
			if(userId != null){
				//删除私信缓存
				privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(userId);
			}
			
			return "1";
		}
		return "0";
	}
	
	
}
