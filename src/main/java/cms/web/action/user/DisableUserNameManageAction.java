package cms.web.action.user;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.user.DisableUserName;
import cms.service.user.UserService;
import cms.utils.RedirectPath;
import cms.utils.Verification;
import cms.web.action.SystemException;

/**
 * 禁止的用户名称管理
 *
 */
@Controller
@RequestMapping("/control/disableUserName/manage") 
public class DisableUserNameManageAction {
	//注入业务bean
	@Resource(name="userServiceBean")
	private UserService userService;
	/**
	 * 禁止的用户名称 添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,DisableUserName disableUserName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "jsp/user/add_disableUserName";
	}
	
	/**
	 * 禁止的用户名称 添加
	 */
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(ModelMap model,DisableUserName formbean,BindingResult result,	
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();
		DisableUserName disableUserName = new DisableUserName();
		
		
		if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
			if("*".equals(formbean.getName().trim())){
				error.put("name", "不能仅一个*号");
			}
			if(formbean.getName().trim().indexOf("**")!=-1){
				error.put("name", "不能连着*号");
			}
			if("?".equals(formbean.getName().trim())){
				error.put("name", "不能仅一个?号");
			}
			//移除所有相同的部分
			String _name = StringUtils.remove(formbean.getName().trim(), "?");
			
			
			if(_name == null || "".equals(_name.trim())){
				error.put("name", "不能仅用?号");
			}
			if(Verification.isString_1(formbean.getName().trim()) == false){
				error.put("name", "只能输入由数字、26个英文字母、星号、问号或者下划线组成");//只能输入由数字、26个英文字母、星号或者下划线组成
			}
			
			disableUserName.setName(formbean.getName().trim());
		}else{
			error.put("name", "不能为空");
		}
		
	
		if (error.size() >0) {  
			model.addAttribute("error", error);
			return "jsp/user/add_disableUserName";
		} 
	
		
		userService.saveDisableUserName(disableUserName);
		model.addAttribute("message", "添加禁止的用户名称成功");
		model.addAttribute("urladdress", RedirectPath.readUrl("control.disableUserName.list"));
		return "jsp/common/message";
		
	}
	
	/**
	 * 禁止的用户名称 显示修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer disableUserNameId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {	
	
		if(disableUserNameId != null && disableUserNameId >0){
			//根据ID查询要修改的数据
			DisableUserName disableUserName = userService.findDisableUserNameById(disableUserNameId);
			
			if(disableUserName != null){
				model.addAttribute("disableUserName", disableUserName);
			}
		}else{
			throw new SystemException("参数不能为空");
		}

		return "jsp/user/edit_disableUserName";
	}
	/**
	 * 禁止的用户名称 修改
	 */
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,DisableUserName formbean,BindingResult result,Integer disableUserNameId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		if(disableUserNameId == null || disableUserNameId <=0){
			throw new SystemException("参数不能为空");
		}
		Map<String,String> error = new HashMap<String,String>();
		DisableUserName disableUserName = new DisableUserName();
		disableUserName.setId(disableUserNameId);
		
		if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
			if("*".equals(formbean.getName().trim())){
				error.put("name", "不能仅一个*号");
			}
			if(formbean.getName().trim().indexOf("**")!=-1){
				error.put("name", "不能连着*号");
			}
			if("?".equals(formbean.getName().trim())){
				error.put("name", "不能仅一个?号");
			}
			//移除所有相同的部分
			String _name = StringUtils.remove(formbean.getName().trim(), "?");
			
			
			if(_name == null || "".equals(_name.trim())){
				error.put("name", "不能仅用?号");
			}
			if(Verification.isString_1(formbean.getName().trim()) == false){
				error.put("name", "只能输入由数字、26个英文字母、星号、问号或者下划线组成");//只能输入由数字、26个英文字母、星号或者下划线组成
			}
			
			
			disableUserName.setName(formbean.getName().trim());
		}else{
			error.put("name", "不能为空");
		}
		
	
		if (error.size() >0) {  
			model.addAttribute("error", error);
			return "jsp/user/edit_disableUserName";
		} 
	
		
		userService.updateDisableUserName(disableUserName);
		model.addAttribute("message", "修改禁止的用户名称成功");
		model.addAttribute("urladdress", RedirectPath.readUrl("control.disableUserName.list"));
		return "jsp/common/message";
	}
	
	/**
	 * 禁止的用户名称 删除
	 */
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Integer disableUserNameId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(disableUserNameId != null && disableUserNameId >0){
			userService.deleteDisableUserName(disableUserNameId);
			return "1";
		}
		return "0";
	}
	
	
	
}
