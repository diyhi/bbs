package cms.web.action.platformShare;


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
import cms.bean.platformShare.TopicUnhidePlatformShare;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.platformShare.PlatformShareService;
import cms.service.setting.SettingService;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.utils.Verification;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.topic.TopicManage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 解锁话题隐藏内容平台分成
 *
 */
@Controller
public class TopicUnhidePlatformShareAction {
	@Resource PlatformShareService platformShareService;
	@Resource SettingService settingService;
	@Resource TopicManage topicManage;
	@Resource UserService userService;
	@Resource FileManage fileManage;
	
	@ResponseBody
	@RequestMapping("/control/topicUnhidePlatformShare/list") 
	public String execute(ModelMap model,PageForm pageForm,String start_times,String end_times,
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
			jpql.append(" and o.unlockTime >= ?"+ (params.size()+1));
			params.add(_start_times);
		}
		if(_end_times != null){//结束时间
			jpql.append(" and o.unlockTime <= ?"+ (params.size()+1));
			params.add(_end_times);
		}
		
		
		
		
		//调用分页算法代码
		PageView<TopicUnhidePlatformShare> pageView = new PageView<TopicUnhidePlatformShare>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		//删除第一个and
		String jpql_str = StringUtils.difference(" and", jpql.toString());
				
		QueryResult<TopicUnhidePlatformShare> qr = platformShareService.getScrollData(TopicUnhidePlatformShare.class,firstindex, pageView.getMaxresult(),jpql_str, params.toArray(),orderby);
		
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(TopicUnhidePlatformShare topicUnhidePlatformShare : qr.getResultlist()){
				
				
				if(topicUnhidePlatformShare.getTopicId() != null && topicUnhidePlatformShare.getTopicId() >0L){
					Topic topic = topicManage.queryTopicCache(topicUnhidePlatformShare.getTopicId());//查询缓存
					if(topic != null){
						topicUnhidePlatformShare.setTopicTitle(topic.getTitle());
					}
					
				}
				
				User post_user = userService.findUserByUserName(topicUnhidePlatformShare.getPostUserName());
				if(post_user != null){
					topicUnhidePlatformShare.setPostAccount(post_user.getAccount());
					topicUnhidePlatformShare.setPostNickname(post_user.getNickname());
					if(post_user.getAvatarName() != null && !"".equals(post_user.getAvatarName().trim())){
						topicUnhidePlatformShare.setPostAvatarPath(fileManage.fileServerAddress(request)+post_user.getAvatarPath());
						topicUnhidePlatformShare.setPostAvatarName(post_user.getAvatarName());
					}		
				}
				User unlock_user = userService.findUserByUserName(topicUnhidePlatformShare.getUnlockUserName());
				if(unlock_user != null){
					topicUnhidePlatformShare.setUnlockAccount(unlock_user.getAccount());
					topicUnhidePlatformShare.setUnlockNickname(unlock_user.getNickname());
					if(unlock_user.getAvatarName() != null && !"".equals(unlock_user.getAvatarName().trim())){
						topicUnhidePlatformShare.setUnlockAvatarPath(fileManage.fileServerAddress(request)+unlock_user.getAvatarPath());
						topicUnhidePlatformShare.setUnlockAvatarName(unlock_user.getAvatarName());
					}		
				}
				
				if(topicUnhidePlatformShare.isStaff()){//如果为员工
					topicUnhidePlatformShare.setPostAccount(topicUnhidePlatformShare.getPostUserName());//员工用户名和账号是同一个
					topicUnhidePlatformShare.setUnlockAccount(topicUnhidePlatformShare.getUnlockUserName());//员工用户名和账号是同一个
				}
			}
		}

		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		if(error.size() ==0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
