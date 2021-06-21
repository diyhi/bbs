package cms.web.action.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.user.UserGrade;
import cms.service.user.UserGradeService;
import cms.utils.JsonUtils;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户等级管理
 *
 */
@Controller
@RequestMapping("/control/userGrade/manage") 
public class UserGradeManageAction {
	
	@Resource UserGradeService userGradeService;
	@Resource(name = "gradeValidator") 
	private Validator validator; 
	@Resource MessageSource messageSource;
	
	/**
	 * 用户等级管理 添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 添加 用户等级
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.POST)
	public String add(UserGrade formbean,BindingResult result,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			List<FieldError> fieldErrorList = result.getFieldErrors();
			if(fieldErrorList != null && fieldErrorList.size() >0){
				for(FieldError fieldError : fieldErrorList){
					error.put(fieldError.getField(), messageSource.getMessage(fieldError, null));
				}
			}
		}
		if(error.size() == 0){
			UserGrade userGrade = new UserGrade();
			userGrade.setName(formbean.getName());
			userGrade.setNeedPoint(formbean.getNeedPoint());
			userGradeService.saveUserGrade(userGrade);
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 用户等级管理 修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.GET)
	public String editUI(ModelMap model,Integer id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(id != null){
			UserGrade userGrade = userGradeService.findGradeById(id);
			if(userGrade != null){
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,userGrade));
			}else{
				error.put("id", "用户等级不存在");
			}
		}else{
			error.put("id", "Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 修改 用户等级
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,UserGrade formbean,BindingResult result,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {	

		Map<String,String> error = new HashMap<String,String>();
		
		
		
		UserGrade userGrade = userGradeService.findGradeById(formbean.getId());
		if(userGrade != null){
			//数据校验
			if(formbean.getName() != null && !"".equals(formbean.getName().trim())){
				if(formbean.getName().length() >50){
					error.put("name", "等级名称不能大于50个字符");
				}
			}else{
				error.put("name", "等级名称不能为空");
			}
			
			if(formbean.getNeedPoint() != null){
				if(formbean.getNeedPoint() <0L){
					error.put("needPoint", "需要积分不能小于0");
				}
				if(formbean.getNeedPoint() >999999999999999L){
					error.put("needPoint", "需要积分不能超过999999999999999");
				}
				UserGrade _userGrade = userGradeService.findGradeByNeedPoint(formbean.getNeedPoint());
				if(_userGrade != null){
					if(!userGrade.getId().equals(_userGrade.getId())){
						error.put("needPoint", "需要积分已存在");
					}
				}
			}else{
				error.put("needPoint", "需要积分不能为空");
			}
		}else{
			error.put("userGrade", "用户等级不存在");	
		}
		
		
		if (error.size() == 0) { 
			userGrade.setName(formbean.getName());
			userGrade.setNeedPoint(formbean.getNeedPoint());
			userGradeService.updateUserGrade(userGrade);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 用户等级管理 删除
	 */
	@ResponseBody
	@RequestMapping(params="method=delete",method=RequestMethod.POST)
	public String delete(ModelMap model,Integer id,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(id != null && id >0){
			userGradeService.deleteUserGrade(id);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
