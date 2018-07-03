package cms.web.action.links;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.links.Links;
import cms.service.links.LinksService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 友情链接
 *
 */
@Controller
public class LinksAction {
	@Resource LinksService linksService;
	
	@RequestMapping("/control/links/list") 
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Links> linksList = linksService.findAllLinks();
		model.addAttribute("linksList", linksList);
		return "jsp/links/linksList";
	}
}
