package cms.web.action.staff;

import java.util.ArrayList;
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
import cms.bean.staff.SysUsers;
import cms.bean.topic.Tag;
import cms.bean.topic.Topic;
import cms.service.setting.SettingService;
import cms.service.staff.ACLService;
import cms.service.staff.StaffService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 员工列表
 * @author Administrator
 *
 */
@Controller
public class StaffAction {
	@Resource StaffService staffService;//通过接口引用代理返回的对象
	@Resource ACLService aclService;
	@Resource SettingService settingService;
	
	/**
	 * 员工列表
	 * @param pageForm
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/staff/list") 
	public String staff(PageForm pageForm,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//调用分页算法代码
		PageView<SysUsers> pageView = new PageView<SysUsers>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		orderby.put("id", "desc");//根据id字段降序排序
		QueryResult<SysUsers> qr = staffService.getScrollData(SysUsers.class,firstindex, pageView.getMaxresult());
		//仅显示指定字段
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<SysUsers> sysUserViewList = new ArrayList<SysUsers>();
			for(SysUsers sysUsers : qr.getResultlist()){
				SysUsers sysUserView = new SysUsers();
				sysUserView.setUserId(sysUsers.getUserId());//用户id
				sysUserView.setUserAccount(sysUsers.getUserAccount());//用户账号
				sysUserView.setFullName(sysUsers.getFullName());//姓名
				sysUserView.setUserDuty(sysUsers.getUserDuty());//用户的职位
				sysUserView.setIssys(sysUsers.isIssys());//是否是超级用户
				sysUserViewList.add(sysUserView);
			}
			qr.setResultlist(sysUserViewList);
		}
		//将查询结果集传给分页List
		pageView.setQueryResult(qr);
		
		List<SysUsers> sysUsersList = qr.getResultlist();
		if(sysUsersList != null && sysUsersList.size() >0){
			List<String> userAccountList = new ArrayList<String>();
			for(SysUsers sysUsers : sysUsersList){
				userAccountList.add(sysUsers.getUserAccount());
			}
			
			Map<String,List<String>> rolesNameMap = aclService.findRolesByUserAccount(userAccountList);
			returnValue.put("rolesNameMap", rolesNameMap);
		}
		returnValue.put("pageView", pageView);
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}

}
