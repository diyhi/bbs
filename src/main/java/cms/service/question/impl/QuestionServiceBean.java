package cms.service.question.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import cms.bean.payment.PaymentLog;
import cms.bean.question.Question;
import cms.bean.question.QuestionTagAssociation;
import cms.bean.user.PointLog;
import cms.service.besa.DaoSupport;
import cms.service.favorite.FavoriteService;
import cms.service.message.RemindService;
import cms.service.question.QuestionService;
import cms.service.user.UserService;
import cms.utils.ObjectConversion;
import cms.web.action.SystemException;
import cms.web.action.payment.PaymentManage;
import cms.web.action.user.PointManage;

/**
 * 问题管理实现类
 *
 */
@Service
@Transactional
public class QuestionServiceBean extends DaoSupport<Question> implements QuestionService{
	
	@Resource RemindService remindService;
	@Resource FavoriteService favoriteService;
	@Resource UserService userService;
	@Resource PointManage pointManage;
	@Resource PaymentManage paymentManage;
	
	/**
	 * 根据Id查询问题
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public Question findById(Long questionId){
		Query query = em.createQuery("select o from Question o where o.id=?1")
		.setParameter(1, questionId);
		List<Question> list = query.getResultList();
		for(Question q : list){
			return q;
		}
		return null;
	}
	/**
	 * 根据Id集合查询问题
	 * @param questionIdList 问题Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Question> findByIdList(List<Long> questionIdList){
		Query query = em.createQuery("select o from Question o where o.id in(:questionIdList)")
		.setParameter("questionIdList", questionIdList);
		return query.getResultList();
	}
	/**
	 * 根据Id集合查询问题标题
	 * @param questionIdList 问题Id集合
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<Question> findTitleByIdList(List<Long> questionIdList){
		String sql ="select o.id, o.title from question o where o.id in(:questionIdList)";
		 
		Query query = em.createNativeQuery(sql);
		query.setParameter("questionIdList", questionIdList);

		List<Question> questionList = new ArrayList<Question>();
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				Question question = new Question();
				question.setId(ObjectConversion.conversion(object[0], ObjectConversion.LONG));
				question.setTitle(ObjectConversion.conversion(object[1], ObjectConversion.STRING));
				questionList.add(question);
			}
		}
		
		return questionList;
		
	}
	
	/**
	 * 根据问题Id集合查询问题
	 * @param questionIdList 话题Id集合
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Question> findQuestionByQuestionIdList(List<Long> questionIdList){
		List<Question> questionList = new ArrayList<Question>();
		
		Query query = em.createQuery("select o.id,o.title,o.content,o.appendContent," +
				"o.postTime,o.userName,o.isStaff,o.status " +
				" from Question o where o.id in(:questionId)");
		query.setParameter("questionId", questionIdList);	
		
		
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				Object[] obj = (Object[])objectList.get(i);
				Long id = (Long)obj[0];
				String title = (String)obj[1];
				String content = (String)obj[2];
				String appendContent = (String)obj[3];
				Date postTime = (Date)obj[4];
				String userName = (String)obj[5];
				Boolean isStaff = (Boolean)obj[6];
				Integer status = (Integer)obj[7];

				Question question = new Question();
				question.setId(id);
				question.setTitle(title);
				question.setContent(content);
				question.setAppendContent(appendContent);
				question.setPostTime(postTime);
				question.setUserName(userName);
				question.setIsStaff(isStaff);
				question.setStatus(status);
				questionList.add(question);
				
			}
		}
		return questionList;
	}
	
	/**
	 * 分页查询问题内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Question> findQuestionContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff){
		List<Question> questionContentList = new ArrayList<Question>();
		
		String sql = "select o.id,o.content,o.appendContent from Question o where o.userName=?1 and o.isStaff=?2";
		Query query = em.createQuery(sql);	
		query.setParameter(1, userName);
		query.setParameter(2, isStaff);
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
		
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				Object[] obj = (Object[])objectList.get(i);
				Long id = (Long)obj[0];
				String content = (String)obj[1];
				String appendContent = (String)obj[2];
				
				Question question = new Question();
				question.setId(id);
				question.setContent(content);
				question.setAppendContent(appendContent);
				questionContentList.add(question);
			}
		}
		
		return questionContentList;
	}
	/**
	 * 分页查询问题
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<Question> findQuestionByPage(int firstIndex, int maxResult){
		List<Question> questionList = new ArrayList<Question>();
		
		String sql = "select o.id,o.title,o.content,o.appendContent," +
				"o.postTime,o.userName,o.isStaff,o.status " +
				" from Question o ";

		Query query = em.createQuery(sql);	
		
		//索引开始,即从哪条记录开始
		query.setFirstResult(firstIndex);
		//获取多少条数据
		query.setMaxResults(maxResult);
			
		List<Object[]> objectList = query.getResultList();
		if(objectList != null && objectList.size() >0){
			for(int i = 0; i<objectList.size(); i++){
				Object[] obj = (Object[])objectList.get(i);
				Long id = (Long)obj[0];
				String title = (String)obj[1];
				String content = (String)obj[2];
				String appendContent = (String)obj[3];
				Date postTime = (Date)obj[4];
				String userName = (String)obj[5];
				Boolean isStaff = (Boolean)obj[6];
				Integer status = (Integer)obj[7];

				Question question = new Question();
				question.setId(id);
				question.setTitle(title);
				question.setContent(content);
				question.setAppendContent(appendContent);
				question.setPostTime(postTime);
				question.setUserName(userName);
				question.setIsStaff(isStaff);
				question.setStatus(status);
				questionList.add(question);
				
			}
		}
		return questionList;
	}
	
	/**
	 * 根据问题Id查询问题标签关联
	 * @param questionId 问题Id
	 * @return
	 */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<QuestionTagAssociation> findQuestionTagAssociationByQuestionId(Long questionId){
		Query query = em.createQuery("select o from QuestionTagAssociation o where o.questionId=?1")
		.setParameter(1, questionId);
		List<QuestionTagAssociation> list = query.getResultList();
		return list;
	}
	
	
	/**
	 * 保存问题
	 * @param question 问题
	 * @param questionTagAssociationList 问题标签关联集合
	 * @param point 扣减用户积分
	 * @param pointLog 积分日志
	 * @param amount 扣减用户预存款
	 * @param paymentLog 支付日志
	 */
	public void saveQuestion(Question question,List<QuestionTagAssociation> questionTagAssociationList,Long point,PointLog pointLog,BigDecimal amount,PaymentLog paymentLog){
		this.save(question);
		
		for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
			questionTagAssociation.setQuestionId(question.getId());
			this.save(questionTagAssociation);
		}
		if(point != null && point >0L){//积分
			pointLog.setParameterId(question.getId());
			Object pointLogObject = pointManage.createPointLogObject(pointLog);
			//扣减用户积分
			int i = userService.subtractUserPoint(question.getUserName(),point, pointLogObject);
			if(i == 0){
				throw new SystemException("扣减积分失败");
			}
		}
		if(amount != null && amount.compareTo(new BigDecimal("0")) >0){//余额
			paymentLog.setSourceParameterId(String.valueOf(question.getId()));
			Object paymentLogObject = paymentManage.createPaymentLogObject(paymentLog);
			//扣减用户预存款
			int i = userService.subtractUserDeposit(question.getUserName(), amount, paymentLogObject);
			if(i ==0){
				throw new SystemException("扣减预存款失败");
			}
		}
	}
	
	
	/**
	 * 保存追加问题
	 * @param questionId 问题Id
	 * @param appendContent 追加问题内容 AppendQuestionItem对象的JSON格式加上逗号
	 * @return
	 */
	public Integer saveAppendQuestion(Long questionId,String appendContent){
		
		Query query = em.createQuery("update Question o set o.appendContent=CONCAT(o.appendContent,?1) where o.id=?2")
				.setParameter(1, appendContent)
				.setParameter(2, questionId);
		return query.executeUpdate();
	}
	/**
	 * 修改追加问题
	 * @param questionId 问题Id
	 * @param appendContent 追加问题内容
	 * @return
	 */
	public Integer updateAppendQuestion(Long questionId,String appendContent){
		
		Query query = em.createQuery("update Question o set o.appendContent=?1 where o.id=?2")
				.setParameter(1, appendContent)
				.setParameter(2, questionId);
		return query.executeUpdate();
	}
	
	
	/**
	 * 增加展示次数
	 * @param countMap key: 话题Id value:展示次数
	 * @return
	 */
	public int addViewCount(Map<Long,Long> countMap){
		int i = 0;
		for (Map.Entry<Long, Long> entry : countMap.entrySet()) { 
			Query query = em.createQuery("update Question o set o.viewTotal=o.viewTotal+?1 where o.id=?2")
					.setParameter(1, entry.getValue())
					.setParameter(2, entry.getKey());
			i = i+query.executeUpdate();
		}
		return i;
	}
	
	/**
	 * 修改采纳答案Id
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @return
	 */
	public int updateAdoptionAnswerId(Long questionId, Long answerId){
		Query query = em.createQuery("update Question o set o.adoptionAnswerId=?1 where o.id=?2")
				.setParameter(1, answerId)
				.setParameter(2, questionId);
		return query.executeUpdate();
		
	}
	
	/**
	 * 修改问题
	 * @param question 问题
	 * @param questionTagAssociationList 问题标签关联集合
	 * @param changePointSymbol 变更积分符号 true：问题增加积分  false：问题减少积分
	 * @param changePoint 变更积分
	 * @param changeAmountSymbol 变更金额符号 true：问题增加金额  false：问题减少金额
	 * @param changeAmount 变更金额
	 * @param pointLogObject 用户悬赏积分日志
	 * @param paymentLogObject 用户悬赏金额日志
	 * @return
	 */
	public Integer updateQuestion(Question question,List<QuestionTagAssociation> questionTagAssociationList,
			boolean changePointSymbol,Long changePoint, boolean changeAmountSymbol, BigDecimal changeAmount,Object pointLogObject,Object paymentLogObject){
		
		Query query = em.createQuery("update Question o set o.title=?1, o.content=?2,o.summary=?3,o.allow=?4,o.status=?5,o.sort=?6,o.point=?7,o.amount=?8 where o.id=?9")
		.setParameter(1, question.getTitle())
		.setParameter(2, question.getContent())
		.setParameter(3, question.getSummary())
		.setParameter(4, question.isAllow())
		.setParameter(5, question.getStatus())
		.setParameter(6, question.getSort())
		.setParameter(7, question.getPoint())
		.setParameter(8, question.getAmount())
		.setParameter(9, question.getId());
		int i = query.executeUpdate();
		
		if(i >0){
			//先删除
			Query delete = em.createQuery("delete from QuestionTagAssociation o where o.questionId=?1")
				.setParameter(1, question.getId());
			delete.executeUpdate();
			
			//保存
			for(QuestionTagAssociation questionTagAssociation : questionTagAssociationList){
				questionTagAssociation.setQuestionId(question.getId());
				this.save(questionTagAssociation);
			}
			
			if(pointLogObject != null){
				if(changePointSymbol){
					//扣减用户积分
					int j = userService.subtractUserPoint(question.getUserName(),changePoint, pointLogObject);
					if(j == 0){
						throw new SystemException("扣减积分失败");
					}
				}else{
					//增加用户积分
					userService.addUserPoint(question.getUserName(),changePoint,pointLogObject);
					
				}
			}
			if(paymentLogObject != null){
				if(changeAmountSymbol){//增加
					//扣减用户预存款
					int j = userService.subtractUserDeposit(question.getUserName(), changeAmount, paymentLogObject);
					if(j ==0){
						throw new SystemException("扣减预存款失败");
					}
				}else{
					//增加用户预存款
					userService.addUserDeposit(question.getUserName(), changeAmount, paymentLogObject);	
				}
			}
		}
		
		return i;
	}
	
	/**
	 * 修改问题最后回答时间
	 * @param questionId 问题Id
	 * @param lastAnswerTime 最后回答时间
	 * @return
	 */
	public Integer updateQuestionAnswerTime(Long questionId,Date lastAnswerTime){
		int i = 0;
		Query query = em.createQuery("update Question o set o.lastAnswerTime=?1 where o.id=?2")
		.setParameter(1, lastAnswerTime)
		.setParameter(2, questionId);
		i= query.executeUpdate();
		return i;
		
	}
	/**
	 * 修改问题状态
	 * @param questionId 问题Id
	 * @param status 状态
	 * @return
	 */
	public int updateQuestionStatus(Long questionId,Integer status){
		Query query = em.createQuery("update Question o set o.status=?1 where o.id=?2")
				.setParameter(1, status)
				.setParameter(2, questionId);
		return query.executeUpdate();
	}
	
	/**
	 * 修改问题标签Id
	 * @param old_tagId 旧标签Id
	 * @param new_tagId 新标签Id
	 * @return
	 */
	public Integer updateTagId(Long old_tagId,Long new_tagId){
		Query query = em.createQuery("update QuestionTagAssociation o set o.questionTagId=?1 where o.questionTagId=?2")
		.setParameter(1, new_tagId)
		.setParameter(2, old_tagId);
		return query.executeUpdate();
	}
	/**
	 * 根据标签Id删除问题标签关联
	 * @param questionTagId 问题标签Id
	 * @return
	 */
	public Integer deleteQuestionTagAssociation(Long questionTagId){
		Query delete = em.createQuery("delete from QuestionTagAssociation o where o.questionTagId=?1")
		.setParameter(1, questionTagId);
		return delete.executeUpdate();
	}
	/**
	 * 根据用户名称集合删除问题标签关联
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteQuestionTagAssociationByUserId(List<String> userNameList){
		Query delete = em.createQuery("delete from QuestionTagAssociation o where o.userName in(:userNameList)")
				.setParameter("userNameList", userNameList);
		return 	delete.executeUpdate();
	}

	
	
	/**
	 * 还原问题
	 * @param questionList 问题集合
	 * @return
	 */
	public Integer reductionQuestion(List<Question> questionList){
		int i = 0;
		if(questionList != null && questionList.size() >0){
			for(Question question : questionList){
				Query query = em.createQuery("update Question o set o.status=o.status-100 where o.id=?1 and o.status >100")
				.setParameter(1, question.getId());
				int j = query.executeUpdate();
				i += j;
			}
		}
		return i;
	}
	
	/**
	 * 标记删除问题
	 * @param questionId 问题Id
	 * @return
	 */
	public Integer markDelete(Long questionId){
		int i = 0;
		Query query = em.createQuery("update Question o set o.status=o.status+100 where o.id=?1")
		.setParameter(1, questionId);
		i= query.executeUpdate();
		return i;
		
	}
	/**
	 * 删除问题
	 * @param questionId 问题Id
	 * @param userName 用户名称
	 * @param point 扣减用户积分
	 * @param pointLogObject 积分日志
	 * @param amount 扣减用户预存款
	 * @param paymentLogObject 支付日志
	 * @return
	 */
	public Integer deleteQuestion(Long questionId,String userName,Long point,Object pointLogObject,BigDecimal amount,Object paymentLogObject){
		int i = 0;
		Query delete = em.createQuery("delete from Question o where o.id=?1")
		.setParameter(1, questionId);
		i = delete.executeUpdate();
		
		//删除答案
		Query answer_delete = em.createQuery("delete from Answer o where o.questionId=?1");
		answer_delete.setParameter(1, questionId);
		answer_delete.executeUpdate();
		
		//删除答案回复
		Query delete_reply = em.createQuery("delete from AnswerReply o where o.questionId=?1");
		delete_reply.setParameter(1, questionId);
		delete_reply.executeUpdate();
				
		//删除提醒
		remindService.deleteRemindByQuestionId(questionId);
		
		//删除收藏
		favoriteService.deleteFavoriteByQuestionId(questionId);
		
		if(point != null && point >0L){//积分
			//增加用户积分
			userService.addUserPoint(userName,point,pointLogObject);

		}
		if(amount != null && amount.compareTo(new BigDecimal("0")) >0){//余额
			//增加用户预存款
			userService.addUserDeposit(userName, amount, paymentLogObject);
		}
		//删除标签关联
		Query deleteQuestionTagAssociation = em.createQuery("delete from QuestionTagAssociation o where o.questionId=?1")
			.setParameter(1, questionId);
		deleteQuestionTagAssociation.executeUpdate();
		return i;
	}
	
	/**
	 * 根据用户名称集合删除问题
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteQuestion(List<String> userNameList){
		Query delete = em.createQuery("delete from Question o where o.userName in(:userName) and o.isStaff=:isStaff")
				.setParameter("userName", userNameList)
				.setParameter("isStaff", false);
		return delete.executeUpdate();
	}
	
	
	/**
	 * 增加总答案数
	 * @param questionId 问题Id
	 * @param quantity 数量
	 * @return
	 */
	public int addAnswerTotal(Long questionId,Long quantity){
		Query query = em.createQuery("update Question o set o.answerTotal=o.answerTotal+?1 where o.id=?2")
				.setParameter(1, quantity)
				.setParameter(2, questionId);
		return query.executeUpdate();
	}
	/**
	 * 减少总答案数
	 * @param questionId 问题Id
	 * @param quantity 数量
	 * @return
	 */
	public int subtractAnswerTotal(Long questionId,Long quantity){
		Query query = em.createQuery("update Question o set o.answerTotal=o.answerTotal-?1 where o.id=?2")
				.setParameter(1, quantity)
				.setParameter(2, questionId);
		return query.executeUpdate();
	}
	
	/**
	 * 查询待审核问题数量
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public Long auditQuestionCount(){
		Query query = em.createQuery("select count(o) from Question o where o.status=?1");
		query.setParameter(1, 10);
		return (Long)query.getSingleResult();
	}
}
