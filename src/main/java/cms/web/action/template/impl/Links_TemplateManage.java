package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


import cms.bean.links.Links;
import cms.bean.template.Forum;
import cms.service.links.LinksService;

/**
 * 友情链接 -- 模板方法实现
 *
 */
@Component("links_TemplateManage")
public class Links_TemplateManage {
	@Resource LinksService linksService; 
	/**
	 * 友情链接列表 -- 集合
	 * @param forum
	 */
	public List<Links> links_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<Links> linksList = linksService.findAllLinks_cache();
		return linksList;
	}
}
