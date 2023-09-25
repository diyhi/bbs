package cms.web.action.links;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.links.Links;
import cms.service.links.LinksService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 友情链接
 *
 */
@Controller
public class LinksAction {
	@Resource LinksService linksService;
	@Resource FileManage fileManage;
	
	
	@ResponseBody
	@RequestMapping("/control/links/list") 
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Links> linksList = linksService.findAllLinks();
		if(linksList != null && linksList.size() >0){
			for(Links links : linksList){
				if(links.getImage() != null && !"".equals(links.getImage().trim())){
					links.setImage(fileManage.fileServerAddress(request)+links.getImage());
				}
			}
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,linksList));
	}
}
