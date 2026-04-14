package cms.service.feedback;

import cms.dto.PageView;
import cms.model.feedback.Feedback;

/**
 * 在线留言服务
 */
public interface FeedbackService {

    /**
     * 获取在线留言列表
     * @param page 页码
     * @param start_createDate 起始时间
     * @param end_createDate 结束时间
     * @return
     */
    public PageView<Feedback> getFeedbackList(int page, String start_createDate, String end_createDate);
    /**
     * 获取在线留言明细
     * @param feedbackId 在线留言Id
     * @return
     */
    public Feedback getFeedbackDetail(Long feedbackId);


    /**
     * 删除在线留言
     * @param feedbackId 在线留言Id
     */
    public void deleteFeedback(Long feedbackId);
}
