package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.model.topic.Tag;
import cms.service.frontend.TopicTagClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台话题标签控制器
 */
@RestController
public class TopicTagController {
    @Resource
    TopicTagClientService topicTagClientService;

    /**
     * 标签列表
     * @param request   请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010400)
    public List<Tag> topicTagList(HttpServletRequest request){
        return topicTagClientService.getAllTopicTagList(request);
    }
}
