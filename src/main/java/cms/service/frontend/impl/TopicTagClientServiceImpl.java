package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.topic.TagComponent;
import cms.component.user.UserRoleComponent;
import cms.model.setting.SystemSetting;
import cms.model.topic.Tag;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TagRepository;
import cms.service.frontend.TopicTagClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 前台话题标签服务
 */
@Service
public class TopicTagClientServiceImpl implements TopicTagClientService {

    @Resource
    TagRepository tagRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    TagComponent tagComponent;
    @Resource
    UserRoleComponent userRoleComponent;

    /**
     * 获取全部话题标签
     * @param request   请求信息
     * @return
     */
    public List<Tag> getAllTopicTagList(HttpServletRequest request){
        List<Tag> tagList = tagRepository.findAllTag_cache();

        List<Tag> new_tagList = new ArrayList<Tag>();//排序后标签

        if(tagList != null && tagList.size() >0){
            //组成排序数据
            Iterator<Tag> tagList_iter = tagList.iterator();
            while(tagList_iter.hasNext()){
                Tag tag = tagList_iter.next();

                List<String> roleNameList = userRoleComponent.queryAllowViewTopicRoleName(tag.getId(),request);
                if(roleNameList != null && roleNameList.size() >0){
                    tag.setAllowRoleViewList(roleNameList);
                }

                if(tag.getImage() != null && !tag.getImage().trim().isEmpty()){
                    tag.setImage(fileComponent.fileServerAddress(request)+tag.getImage());
                }


                //如果是根节点
                if(tag.getParentId().equals(0L)){

                    new_tagList.add(tag);
                    tagList_iter.remove();
                }
            }
            //组合子标签
            for(Tag tag :new_tagList){
                tagComponent.childTag(tag,tagList);
            }
            //排序
            tagComponent.tagSort(new_tagList);
        }
        return new_tagList;
    }

}
