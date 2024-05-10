package cms.web.action.template.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cms.bean.template.Forum;
import cms.bean.topic.Tag;
import cms.service.topic.TagService;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.user.UserRoleManage;

/**
 * 标签 -- 模板方法实现
 *
 */
@Component("tag_TemplateManage")
public class Tag_TemplateManage {
	@Resource TagService tagService; 
	@Resource UserRoleManage userRoleManage;
	@Resource FileManage fileManage;
	
	/**
	 * 标签列表 -- 集合
	 * @param forum
	 */
	public List<Tag> tag_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<Tag> tagList = tagService.findAllTag_cache();
		if(tagList != null && tagList.size() >0){
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
			
			for(Tag tag : tagList){
				List<String> roleNameList = userRoleManage.queryAllowViewTopicRoleName(tag.getId());
				if(roleNameList != null && roleNameList.size() >0){
					tag.setAllowRoleViewList(roleNameList);
				}
				
				if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
					tag.setImage(fileManage.fileServerAddress(request)+tag.getImage());
				}
			}
		}
		return tagList;
	}
}
