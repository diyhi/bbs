package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cms.bean.links.Links;
import cms.bean.template.Forum;
import cms.service.links.LinksService;
import cms.web.action.fileSystem.FileManage;

/**
 * 友情链接 -- 模板方法实现
 *
 */
@Component("links_TemplateManage")
public class Links_TemplateManage {
	@Resource LinksService linksService; 
	@Resource FileManage fileManage;
	
	/**
	 * 友情链接列表 -- 集合
	 * @param forum
	 */
	public List<Links> links_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
		List<Links> linksList = linksService.findAllLinks_cache();
		if(linksList != null && linksList.size() >0){
			for(Links links : linksList){
				if(links.getImage() != null && !"".equals(links.getImage().trim())){
					links.setImage(fileManage.fileServerAddress(request)+links.getImage());
				}
			}
		}
		return linksList;
	}
}
