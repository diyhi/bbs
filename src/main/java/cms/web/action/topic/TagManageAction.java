package cms.web.action.topic;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.topic.Tag;
import cms.service.setting.SettingService;
import cms.service.topic.TagService;
import cms.utils.RedirectPath;
import cms.web.action.FileManage;
import cms.web.action.SystemException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
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
	
	@Resource FileManage fileManage;
	
	@Resource SettingService settingService;
	
	
	/**
	 * 标签   添加界面显示
	 */
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(Tag tag,ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "jsp/topic/add_tag";
	}
	
	/**
	 * 标签  添加
	 */
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,Tag formbean,BindingResult result
			) throws Exception {

		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) { 
			return "jsp/topic/add_tag";
		} 
		Tag tag = new Tag(); 
		tag.setId(tagManage.nextNumber());
		tag.setName(formbean.getName());
		
		tag.setSort(formbean.getSort());
			
		tagService.saveTag(tag);
		
		model.addAttribute("message","添加标签成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.tag.list"));
		return "jsp/common/message";
	}
	
	
	/**
	 * 标签   修改界面显示
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		if(tagId != null){//判断ID是否存在;
			Tag tag = tagService.findById(tagId);
			if(tag != null){
				model.addAttribute("tag",tag);//返回消息
			}
		}
		return "jsp/topic/edit_tag";
	}
	/**
	 * 标签   修改
	 */
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,Tag formbean,BindingResult result,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Tag tag = null;
		if(tagId != null && tagId >0L){
			//取得对象
			tag = tagService.findById(tagId);
			if(tag == null){
				throw new SystemException("标签不存在");
			}
		}
		
		//数据校验
		this.validator.validate(formbean, result); 
		if (result.hasErrors()) {  	
			return "jsp/topic/edit_tag";
		} 
		
		Tag new_tag = new Tag(); 
		new_tag.setId(tagId);
		new_tag.setName(formbean.getName());
		new_tag.setSort(formbean.getSort());
		
		tagService.updateTag(new_tag);
		
		model.addAttribute("message","修改标签成功");//返回消息
		model.addAttribute("urladdress", RedirectPath.readUrl("control.tag.list"));
		return "jsp/common/message";
	}
	/**
	 * 标签   删除
	*/
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String delete(ModelMap model,Long tagId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		if(tagId != null && tagId >0L){
			int i = tagService.deleteTag(tagId);
			if(i >0){
				return"1";
			}
			
		}
		return"0";
	}
	
	
	
	
	/**
	 * 标签 查询所有标签
	 */
	@RequestMapping(params="method=allTag", method=RequestMethod.GET)
	public String queryAllTag(ModelMap model,String module,String tableName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		List<Tag> tagList = tagService.findAllTag();
		model.addAttribute("tagList", tagList);
		
		
		if(module != null && "forum".equals(module)){
			model.addAttribute("tableName", tableName);
			return "jsp/topic/ajax_forum_allTag";
		}else{
			return "jsp/topic/ajax_allTag";
		}
		
		
		
	}

}
