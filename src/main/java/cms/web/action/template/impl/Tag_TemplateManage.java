package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cms.bean.template.Forum;
import cms.bean.topic.Tag;
import cms.service.topic.TagService;
import cms.web.action.user.UserRoleManage;

/**
 * 标签 -- 模板方法实现
 *
 */
@Component("tag_TemplateManage")
public class Tag_TemplateManage {
	@Resource TagService tagService; 
	@Resource UserRoleManage userRoleManage;
	
	/**
	 * 标签列表 -- 集合
	 * @param forum
	 */
	public List<Tag> tag_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<Tag> tagList = tagService.findAllTag_cache();
		if(tagList != null && tagList.size() >0){
			for(Tag tag : tagList){
				List<String> roleNameList = userRoleManage.queryAllowViewTopicRoleName(tag.getId());
				if(roleNameList != null && roleNameList.size() >0){
					tag.setAllowRoleViewList(roleNameList);
				}
			}
		}
		return tagList;
	}
}
