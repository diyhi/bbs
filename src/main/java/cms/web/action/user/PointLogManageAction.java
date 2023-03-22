package cms.web.action.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.PointLog;
import cms.bean.user.User;
import cms.service.user.UserService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 积分日志管理
 *
 */
@Controller
@RequestMapping("/control/pointLog/manage") 
public class PointLogManageAction{
	@Resource PointManage pointManage;
	@Resource UserService userService;//通过接口引用代理返回的对象
	@Resource FileManage fileManage;
	@Resource UserManage userManage;
	
	/**
	 * 积分日志管理 详细显示
	 * @param model
	 * @param pointLogId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=show",method=RequestMethod.GET)
	public String addUI(ModelMap model,String pointLogId,String userName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(pointLogId != null && !"".equals(pointLogId.trim())){
			if(pointLogId.trim().length() == 36 && pointManage.verificationPointLogId(pointLogId)){
				PointLog pointLog = userService.findPointLogById(pointLogId);
				User user = userService.findUserByUserName(userName);
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
				
				if(pointLog.getOperationUserType().equals(1)){//员工
					pointLog.setOperationAccount(pointLog.getOperationUserName());//员工用户名和账号是同一个
				}else if(pointLog.getOperationUserType().equals(2)){//会员
					User _user = userManage.query_cache_findUserByUserName(pointLog.getOperationUserName());
					if(_user != null){
						pointLog.setOperationAccount(_user.getAccount());
					}
				}
				
				returnValue.put("pointLog", pointLog);
			}
		}else{
			error.put("pointLogId", "积分Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
}

