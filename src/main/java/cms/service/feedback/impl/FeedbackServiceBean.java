package cms.service.feedback.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.feedback.Feedback;
import cms.service.besa.DaoSupport;
import cms.service.feedback.FeedbackService;

/**
 * 在线留言
 *
 */
@Service
@Transactional
public class FeedbackServiceBean extends DaoSupport<Feedback> implements FeedbackService{
	/**
	 * 根据Id查询留言
	 * @param feedbackId 留言Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Feedback findById(Long feedbackId){
		Query query = em.createQuery("select o from Feedback o where o.id=?1")
		.setParameter(1, feedbackId);
		List<Feedback> list = query.getResultList();
		for(Feedback p : list){
			return p;
		}
		return null;
	}

	/**
	 * 保存留言
	 * @param feedback
	 */
	public void saveFeedback(Feedback feedback){
		this.save(feedback);
	}
	

	/**
	 * 修改留言
	 * @param feedback
	 * @return
	 */
	public Integer updateFeedback(Feedback feedback){
		Query query = em.createQuery("update Feedback o set o.name=?1, o.contact=?2,o.content=?3 where o.id=?4")
		.setParameter(1, feedback.getName())
		.setParameter(2, feedback.getContact())
		.setParameter(2, feedback.getContent())
		.setParameter(4, feedback.getId());
		int i = query.executeUpdate();
		return i;
	}
	
	/**
	 * 删除留言
	 * @param feedbackId 留言Id
	 */
	public Integer deleteFeedback(Long feedbackId){
		int i = 0;
		Query delete = em.createQuery("delete from Feedback o where o.id=?1")
		.setParameter(1,feedbackId);
		i = delete.executeUpdate();
		return i;
	}
	/**
	 * 查询留言数量
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Long feedbackCount(){
		Query query = em.createQuery("select count(o) from Feedback o");
		return (Long)query.getSingleResult();
	}
}
