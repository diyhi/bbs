package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


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
		List<Links> linksList = linksService.findAllLinks_cache();
		if(linksList != null && linksList.size() >0){
			for(Links links : linksList){
				links.setImage(fileManage.fileServerAddress()+links.getImage());
			}
		}
		return linksList;
	}
}
