package cms.service.platformShare;

import cms.dto.PageView;
import cms.model.platformShare.QuestionRewardPlatformShare;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 问答悬赏平台分成服务
 */
public interface QuestionRewardPlatformShareService {

    /**
     * 获取问答悬赏平台分成列表
     * @param page 页码
     * @param start_times 超始时间
     * @param end_times 结束时间
     * @param request 请求信息
     */
    public PageView<QuestionRewardPlatformShare> getQuestionRewardPlatformShareList(int page, String start_times, String end_times, HttpServletRequest request);
}
