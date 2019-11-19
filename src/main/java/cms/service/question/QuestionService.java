package cms.service.question;

import java.util.Date;
import java.util.List;
import java.util.Map;


import cms.bean.question.Question;
import cms.bean.question.QuestionTagAssociation;
import cms.service.besa.DAO;

/**
 * 问题管理接口
 *
 */
public interface QuestionService extends DAO<Question>{
	/**
	 * 根据Id查询问题
	 * @param questionId 问题Id
	 * @return
	 */
	public Question findById(Long questionId);
	/**
	 * 根据Id集合查询问题
	 * @param questionIdList 问题Id集合
	 * @return
	 */
	public List<Question> findByIdList(List<Long> questionIdList);
	/**
	 * 根据Id集合查询问题标题
	 * @param questionIdList 问题Id集合
	 * @return
	 */
	public List<Question> findTitleByIdList(List<Long> questionIdList);
	/**
	 * 根据问题Id集合查询问题
	 * @param questionIdList 话题Id集合
	 * @return
	 */
	public List<Question> findQuestionByQuestionIdList(List<Long> questionIdList);
	/**
	 * 分页查询问题内容
	 * @param firstIndex
	 * @param maxResult
	 * @param userName 用户名称
	 * @param isStaff 是否为员工
	 * @return
	 */
	public Map<Long,String> findQuestionContentByPage(int firstIndex, int maxResult,String userName,boolean isStaff);
	/**
	 * 分页查询问题
	 * @param firstIndex 开始索引
	 * @param maxResult 需要获取的记录数
	 * @return
	 */
	public List<Question> findQuestionByPage(int firstIndex, int maxResult);
	/**
	 * 根据问题Id查询问题标签关联
	 * @param questionId 问题Id
	 * @return
	 */
	public List<QuestionTagAssociation> findQuestionTagAssociationByQuestionId(Long questionId);
	/**
	 * 保存问题
	 * @param question 问题
	 * @param questionTagAssociationList 问题标签关联集合
	 */
	public void saveQuestion(Question question,List<QuestionTagAssociation> questionTagAssociationList);
	
	/**
	 * 增加展示次数
	 * @param countMap key: 话题Id value:展示次数
	 * @return
	 */
	public int addViewCount(Map<Long,Long> countMap);
	/**
	 * 修改采纳答案Id
	 * @param questionId 问题Id
	 * @param answerId 答案Id
	 * @return
	 */
	public int updateAdoptionAnswerId(Long questionId, Long answerId);
	/**
	 * 修改问题
	 * @param question 问题
	 * @param questionTagAssociationList 问题标签关联集合
	 * @return
	 */
	public Integer updateQuestion(Question question,List<QuestionTagAssociation> questionTagAssociationList);
	
	/**
	 * 修改问题最后回答时间
	 * @param questionId 问题Id
	 * @param lastAnswerTime 最后回答时间
	 * @return
	 */
	public Integer updateQuestionAnswerTime(Long questionId,Date lastAnswerTime);
	/**
	 * 修改问题状态
	 * @param questionId 问题Id
	 * @param status 状态
	 * @return
	 */
	public int updateQuestionStatus(Long questionId,Integer status);
	/**
	 * 修改问题标签Id
	 * @param old_tagId 旧标签Id
	 * @param new_tagId 新标签Id
	 * @return
	 */
	public Integer updateTagId(Long old_tagId,Long new_tagId);
	/**
	 * 根据标签Id删除问题标签关联
	 * @param questionTagId 问题标签Id
	 * @return
	 */
	public Integer deleteQuestionTagAssociation(Long questionTagId);
	/**
	 * 根据用户名称集合删除问题标签关联
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteQuestionTagAssociationByUserId(List<String> userNameList);
	/**
	 * 还原问题
	 * @param questionList 问题集合
	 * @return
	 */
	public Integer reductionQuestion(List<Question> questionList);
	
	/**
	 * 标记删除问题
	 * @param questionId 问题Id
	 * @return
	 */
	public Integer markDelete(Long questionId);
	/**
	 * 删除问题
	 * @param questionId 问题Id
	 * @return
	 */
	public Integer deleteQuestion(Long questionId);
	/**
	 * 根据用户名称集合删除问题
	 * @param userNameList 用户名称集合
	 * @return
	 */
	public Integer deleteQuestion(List<String> userNameList);
	/**
	 * 增加总答案数
	 * @param questionId 问题Id
	 * @param quantity 数量
	 * @return
	 */
	public int addAnswerTotal(Long questionId,Long quantity);
	/**
	 * 减少总答案数
	 * @param questionId 问题Id
	 * @param quantity 数量
	 * @return
	 */
	public int subtractAnswerTotal(Long questionId,Long quantity);
}
