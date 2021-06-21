package cms.service.feedback;

import cms.bean.feedback.Feedback;
import cms.service.besa.DAO;

/**
 * 在线留言
 *
 */
public interface FeedbackService extends DAO<Feedback>{
	/**
	 * 根据Id查询留言
	 * @param feedbackId 留言Id
	 * @return
	 */
	public Feedback findById(Long feedbackId);
	/**
	 * 保存留言
	 * @param feedback
	 */
	public void saveFeedback(Feedback feedback);
	

	/**
	 * 修改留言
	 * @param feedback
	 * @return
	 */
	public Integer updateFeedback(Feedback feedback);
	
	/**
	 * 删除留言
	 * @param feedbackId 留言Id
	 */
	public Integer deleteFeedback(Long feedbackId);
	/**
	 * 查询留言数量
	 * @return
	 */
	public Long feedbackCount();
}
