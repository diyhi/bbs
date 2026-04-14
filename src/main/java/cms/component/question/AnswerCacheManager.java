package cms.component.question;

import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 答案缓存管理
 */
@Component("answerCacheManager")
public class AnswerCacheManager {
    @Resource AnswerRepository answerRepository;

    /**
     * 查询答案缓存
     * @param answerId 答案Id
     */
    @Cacheable(value="answerCacheManager_cache_findByAnswerId",key="#answerId")
    public Answer query_cache_findByAnswerId(Long answerId){
        return answerRepository.findByAnswerId(answerId);
    }
    /**
     * 删除答案缓存
     * @param answerId 答案Id
     */
    @CacheEvict(value="answerCacheManager_cache_findByAnswerId",key="#answerId")
    public void delete_cache_findByAnswerId(Long answerId){

    }

    /**
     * 查询答案回复缓存
     * @param answerReplyId 答案回复Id
     */
    @Cacheable(value="answerCacheManager_cache_findReplyByReplyId",key="#answerReplyId")
    public AnswerReply query_cache_findReplyByReplyId(Long answerReplyId){
        return answerRepository.findReplyByReplyId(answerReplyId);
    }
    /**
     * 删除答案回复缓存
     * @param answerReplyId 答案回复Id
     */
    @CacheEvict(value="answerCacheManager_cache_findReplyByReplyId",key="#answerReplyId")
    public void delete_cache_findReplyByReplyId(Long answerReplyId){
    }

    /**
     * 查询缓存 查询回答数量
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="answerCacheManager_cache_answerCount",key="#userName")
    public Long query_cache_answerCount(String userName){

        return answerRepository.findAnswerCountByUserName(userName);
    }
    /**
     * 删除缓存 查询回答数量
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="answerCacheManager_cache_answerCount",key="#userName")
    public void delete_cache_answerCount(String userName){
    }
}
