package cms.web.action.topic;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.topic.Tag;
import cms.service.setting.SettingService;
import cms.service.topic.TagService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 标签
 *
 */
@Controller
public class TagAction {
	@Resource TagService tagService;
	@Resource SettingService settingService;
	
	@RequestMapping("/control/tag/list") 
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Tag> tagList = tagService.findAllTag();
		model.addAttribute("tagList", tagList);
		return "jsp/topic/tagList";
	}
}
