package cms.web.action.topic;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.topic.Tag;
import cms.service.setting.SettingService;
import cms.service.topic.TagService;
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
 * 标签
 *
 */
@Controller
@RequestMapping("/control/tag/manage") 
public class TagManageAction {

	@Resource(name = "tagValidator") 
	private Validator validator; 
	@Resource TagService tagService; 
	@Resource TagManage tagManage;
	
	
	@Resource SettingService settingService;
	@Resource MessageSource messageSource;
	
	/**
	 * 标签   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Tag tag,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 标签  添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	@ResponseBody
	public String add(ModelMap model,Tag formbean,BindingResult result
			) throws Exception {
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
		if(error.size() ==0){
			Tag tag = new Tag(); 
			tag.setId(tagManage.nextNumber());
			tag.setName(formbean.getName());
			
			tag.setSort(formbean.getSort());
				
			tagService.saveTag(tag);
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 标签   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(tagId != null){//判断ID是否存在;
			Tag tag = tagService.findById(tagId);
			if(tag != null){
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,tag));
			}else{
				error.put("tag", "标签不存在");
			}
		}else{
			error.put("tag", "标签Id不能为空");
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		
	}
	/**
	 * 标签   修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Tag formbean,BindingResult result,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		Tag tag = null;
		if(tagId != null && tagId >0L){
			//取得对象
			tag = tagService.findById(tagId);
			if(tag == null){
				error.put("tag", "标签不存在");
			}
		}else{
			error.put("tag", "标签Id不能为空");
		}
		
		
		
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
			Tag new_tag = new Tag(); 
			new_tag.setId(tagId);
			new_tag.setName(formbean.getName());
			new_tag.setSort(formbean.getSort());
			
			tagService.updateTag(new_tag);
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	/**
	 * 标签   删除
	 * @param model
	 * @param tagId 标签Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(tagId != null && tagId >0L){
			int i = tagService.deleteTag(tagId);
			if(i >0){
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}
			
		}else{
			error.put("tagId", " 标签Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	
	
	/**
	 * 标签 查询所有标签
	 */
	@ResponseBody
	@RequestMapping(params="method=allTag", method=RequestMethod.GET)
	public String queryAllTag(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		List<Tag> tagList = tagService.findAllTag();
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,tagList));
	}

}
