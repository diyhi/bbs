package cms.component.question;

import cms.model.question.Question;
import cms.model.question.QuestionTagAssociation;
import cms.repository.question.QuestionRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 问答缓存管理
 */
@Component("questionCacheManager")
public class QuestionCacheManager {
    @Resource QuestionRepository questionRepository;


    /**
     * 查询缓存 根据Id查询问题
     * @param questionId 问题Id
     * @return
     */
    @Cacheable(value="questionCacheManager_cache_findById",key="#questionId")
    public Question query_cache_findById(Long questionId){
        return questionRepository.findById(questionId);
    }
    /**
     * 删除缓存 根据Id查询问题
     * @param questionId 问题Id
     * @return
     */
    @CacheEvict(value="questionCacheManager_cache_findById",key="#questionId")
    public void delete_cache_findById(Long questionId){
    }

    /**
     * 查询缓存 根据问题Id查询问题标签关联
     * @param questionId 问题Id
     * @return
     */
    @Cacheable(value="questionCacheManager_cache_findQuestionTagAssociationByQuestionId",key="#questionId")
    public List<QuestionTagAssociation> query_cache_findQuestionTagAssociationByQuestionId(Long questionId){
        return questionRepository.findQuestionTagAssociationByQuestionId(questionId);
    }
    /**
     * 删除缓存 根据问题Id查询问题标签关联
     * @param questionId 问题Id
     * @return
     */
    @CacheEvict(value="questionCacheManager_cache_findQuestionTagAssociationByQuestionId",key="#questionId")
    public void delete_cache_findQuestionTagAssociationByQuestionId(Long questionId){
    }

    /**
     * 问题展示IP记录(每问题每IP 30分钟内只统计一次点击次数)
     * @param key 话题Id_用户IP
     * @param time 当前时间
     * @return
     */
    @Cacheable(value="questionCacheManager_cache_ipRecord",key="#key")
    public Long ipRecord(String key,Long time){
        return time;
    }
}
