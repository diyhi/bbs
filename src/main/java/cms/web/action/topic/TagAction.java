package cms.web.action.topic;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.help.HelpType;
import cms.bean.topic.Tag;
import cms.service.setting.SettingService;
import cms.service.topic.TagService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 标签管理列表
 *
 */
@Controller
public class TagAction {
	@Resource TagService tagService;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	
	@ResponseBody
	@RequestMapping("/control/tag/list") 
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Tag> tagList = tagService.findAllTag();
		
		
		
		if(tagList != null && tagList.size() >0){
			for(Tag tag :tagList){
				if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
					tag.setImage(fileManage.fileServerAddress(request)+tag.getImage());
				}
				
			}
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,tagList));
	}
}
