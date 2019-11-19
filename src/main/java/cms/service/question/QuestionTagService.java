package cms.service.question;

import java.util.List;


import cms.bean.question.QuestionTag;
import cms.service.besa.DAO;
/**
 * 问题标签接口
 * @author Gao
 *
 */
public interface QuestionTagService extends DAO<QuestionTag>{
	/** 
	 * 根据Id查询标签
	 * @param questionTagId 标签Id
	 * @return
	 */
	public QuestionTag findById(Long questionTagId);
	/**
	 * 根据标签查询所有父类标签
	 * @param questionTag 标签
	 * @return
	 */
	public List<QuestionTag> findAllParentById(QuestionTag questionTag);
	
	/**
	 * 根据标签Id查询子标签(下一节点)
	 * @param questionTagId 标签Id
	 * @return
	 */
	public List<QuestionTag> findChildTagById(Long questionTagId);
	
	/**
	 * 查询所有问题标签
	 * @return
	 */
	public List<QuestionTag> findAllQuestionTag();
	
	/**
	 * 查询所有问题标签 - 缓存
	 * @return
	 */
	public List<QuestionTag> findAllQuestionTag_cache();
	
	/**
	 * 保存标签
	 * @param questionTag
	 */
	public void saveQuestionTag(QuestionTag questionTag);

	/**
	 * 修改标签
	 * @param questionTag
	 * @return
	 */
	public Integer updateQuestionTag(QuestionTag questionTag);
	
	/**
	 * 删除标签
	 * @param questionTag 标签
	 */
	public Integer deleteQuestionTag(QuestionTag questionTag);
}
