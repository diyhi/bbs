package cms.service.platformShare;

import cms.dto.PageView;
import cms.model.platformShare.TopicUnhidePlatformShare;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 解锁话题隐藏内容平台分成服务
 */
public interface TopicUnhidePlatformShareService {

    /**
     * 获取解锁话题隐藏内容平台分成列表
     * @param page 页码
     * @param start_times 超始时间
     * @param end_times 结束时间
     * @param request 请求信息
     */
    public PageView<TopicUnhidePlatformShare> getTopicUnhidePlatformShareList(int page, String start_times, String end_times, HttpServletRequest request);
}
