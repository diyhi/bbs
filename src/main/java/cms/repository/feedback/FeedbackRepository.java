package cms.repository.feedback;


import cms.model.feedback.Feedback;
import cms.repository.besa.DAO;

/**
 * 在线留言管理接口
 *
 */
public interface FeedbackRepository extends DAO<Feedback> {
	/**
	 * 根据Id查询留言
	 * @param feedbackId 留言Id
	 * @return
	 */
	public Feedback findById(Long feedbackId);
	/**
	 * 保存留言
	 * @param feedback 留言
	 */
	public void saveFeedback(Feedback feedback);
	

	/**
	 * 修改留言
	 * @param feedback 留言
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
