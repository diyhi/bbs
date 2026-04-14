package cms.service.frontend;

import cms.model.topic.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 前台话题标签服务接口
 */
public interface TopicTagClientService {
    /**
     * 获取全部话题标签
     * @param request   请求信息
     * @return
     */
    public List<Tag> getAllTopicTagList(HttpServletRequest request);
}

