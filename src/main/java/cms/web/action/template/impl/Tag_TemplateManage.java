package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cms.bean.template.Forum;
import cms.bean.topic.Tag;
import cms.service.topic.TagService;

/**
 * 标签 -- 模板方法实现
 *
 */
@Component("tag_TemplateManage")
public class Tag_TemplateManage {
	@Resource TagService tagService; 
	/**
	 * 标签列表 -- 集合
	 * @param forum
	 */
	public List<Tag> tag_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<Tag> tagList = tagService.findAllTag_cache();
		return tagList;
	}
}
