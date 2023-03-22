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
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.message.PrivateMessage;
import cms.bean.user.User;
import cms.service.message.PrivateMessageService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

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
	@Resource FileManage fileManage;
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
	@ResponseBody
	@RequestMapping(params="method=privateMessageList",method=RequestMethod.GET)
	public String privateMessageList(PageForm pageForm,ModelMap model,Long id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
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
							privateMessage.setFriendAccount(friend_user.getAccount());
							privateMessage.setFriendNickname(friend_user.getNickname());
							if(friend_user.getAvatarName() != null && !"".equals(friend_user.getAvatarName().trim())){
								privateMessage.setFriendAvatarPath(fileManage.fileServerAddress(request)+friend_user.getAvatarPath());//私信对方头像路径
								privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
							}
						}
						User sender_user = userMap.get(privateMessage.getSenderUserId());
						if(sender_user != null){
							privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
							privateMessage.setSenderAccount(sender_user.getAccount());
							privateMessage.setSenderNickname(sender_user.getNickname());
							if(sender_user.getAvatarName() != null && !"".equals(sender_user.getAvatarName().trim())){
								privateMessage.setSenderAvatarPath(fileManage.fileServerAddress(request)+sender_user.getAvatarPath());//发送者头像路径
								privateMessage.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
							}
						}
						
					}
				}
				
				
				
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			User user = userService.findUserById(id);
			if(user != null){
				if(user.getAvatarPath() != null && !"".equals(user.getAvatarPath())){
					user.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
				}
				returnValue.put("currentUser", user);
			}
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("id", "用户Id不能为空");
			
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
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
	@ResponseBody
	@RequestMapping(params="method=privateMessageChatList",method=RequestMethod.GET)
	public String privateMessageChatList(Integer page,ModelMap model,Long id,Long friendUserId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		

		
		if(id != null && id >0L && friendUserId !=null && friendUserId >0L){
			
			
			PageForm pageForm = new PageForm();
			pageForm.setPage(page);
			
			if(page == null){
				Long count = privateMessageService.findPrivateMessageChatCountByUserId(id,friendUserId,null);
				if(count != null && count >0L){
					Integer _page = Integer.parseInt(String.valueOf(count))/settingService.findSystemSetting_cache().getBackstagePageNumber();
					if(Integer.parseInt(String.valueOf(count))%settingService.findSystemSetting_cache().getBackstagePageNumber() >0){//余数大于0要加1
						_page = _page+1;
					}
					pageForm.setPage(_page);
				}
			}
			
			
			
			
			//调用分页算法代码
			PageView<PrivateMessage> pageView = new PageView<PrivateMessage>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();
				
					
			//用户Id集合
			Set<Long> userIdList = new HashSet<Long>();
			//用户集合
			Map<Long,User> userMap = new HashMap<Long,User>();
			
			
			//对话用户
			User chatUser = userService.findUserById(friendUserId);
			if(chatUser != null){
				returnValue.put("chatUser", chatUser);
			}
			
			
			QueryResult<PrivateMessage> qr = privateMessageService.findPrivateMessageChatByUserId(id,friendUserId,null,firstIndex,pageView.getMaxresult(),1);
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
							privateMessage.setFriendAccount(friend_user.getAccount());
							privateMessage.setFriendNickname(friend_user.getNickname());
							if(friend_user.getAvatarName() != null && !"".equals(friend_user.getAvatarName().trim())){
								privateMessage.setFriendAvatarPath(fileManage.fileServerAddress(request)+friend_user.getAvatarPath());//私信对方头像路径
								privateMessage.setFriendAvatarName(friend_user.getAvatarName());//私信对方头像名称
							}
						}
						User sender_user = userMap.get(privateMessage.getSenderUserId());
						if(sender_user != null){
							privateMessage.setSenderUserName(sender_user.getUserName());//私信发送者用户名称
							privateMessage.setSenderAccount(sender_user.getAccount());
							privateMessage.setSenderNickname(sender_user.getNickname());
							if(sender_user.getAvatarName() != null && !"".equals(sender_user.getAvatarName().trim())){
								privateMessage.setSenderAvatarPath(fileManage.fileServerAddress(request)+sender_user.getAvatarPath());//发送者头像路径
								privateMessage.setSenderAvatarName(sender_user.getAvatarName());//发送者头像名称
							}
						}
						
					}
				}
				
				
				
			}
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			returnValue.put("pageView", pageView);
		}else{
			error.put("id", "用户Id或对方用户Id不能为空");
			
		}
		if(error.size()==0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
	@ResponseBody
	@RequestMapping(params="method=deletePrivateMessageChat", method=RequestMethod.POST)
	public String delete(ModelMap model,Long userId,Long friendUserId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(userId != null && userId >0L && friendUserId != null && friendUserId >0L){
			int i = privateMessageService.deletePrivateMessage(userId, friendUserId);
			
			//删除私信缓存
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(userId);
			privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(friendUserId);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("id", "用户Id或对方用户Id不能为空");
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
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
	@ResponseBody
	@RequestMapping(params="method=reductionPrivateMessage", method=RequestMethod.POST)
	public String reductionPrivateMessage(ModelMap model,Long userId,String privateMessageId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(privateMessageId != null && !"".equals(privateMessageId.trim())){
			int i = privateMessageService.reductionPrivateMessage(privateMessageId);
			if(userId != null){
				//删除私信缓存
				privateMessageManage.delete_cache_findUnreadPrivateMessageByUserId(userId);
			}
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("privateMessageId", "私信Id不能为空");
			
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
}
