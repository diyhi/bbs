package cms.component.like;

import cms.model.like.*;
import cms.repository.like.LikeRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 点赞缓存管理
 */
@Component("likeCacheManager")
public class LikeCacheManager {
    @Resource LikeRepository likeRepository;

    /**
     * 查询缓存 查询话题点赞
     * @param topicLikeId 话题点赞Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findTopicLikeById",key="#topicLikeId")
    public TopicLike query_cache_findTopicLikeById(String topicLikeId){
        return likeRepository.findTopicLikeById(topicLikeId);
    }
    /**
     * 删除缓存 话题点赞
     * @param topicLikeId 话题点赞Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findTopicLikeById",key="#topicLikeId")
    public void delete_cache_findTopicLikeById(String topicLikeId){
    }


    /**
     * 查询缓存 根据话题Id查询被点赞数量
     * @param topicId 话题Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findLikeCountByTopicId",key="#topicId")
    public Long query_cache_findLikeCountByTopicId(Long topicId){
        return likeRepository.findLikeCountByTopicId(topicId);
    }
    /**
     * 删除缓存 根据话题Id查询被点赞数量
     * @param topicId 话题Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findLikeCountByTopicId",key="#topicId")
    public void delete_cache_findLikeCountByTopicId(Long topicId){
    }


    /**
     * 查询缓存 查询评论点赞
     * @param commentLikeId 评论点赞Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findCommentLikeById",key="#commentLikeId")
    public CommentLike query_cache_findCommentLikeById(String commentLikeId){
        return likeRepository.findCommentLikeById(commentLikeId);
    }
    /**
     * 删除缓存 评论点赞
     * @param commentLikeId 评论点赞Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findCommentLikeById",key="#commentLikeId")
    public void delete_cache_findCommentLikeById(String commentLikeId){
    }


    /**
     * 查询缓存 根据评论Id查询被点赞数量
     * @param commentId 评论Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findLikeCountByCommentId",key="#commentId")
    public Long query_cache_findLikeCountByCommentId(Long commentId){
        return likeRepository.findLikeCountByCommentId(commentId);
    }
    /**
     * 删除缓存 根据评论Id查询被点赞数量
     * @param commentId 评论Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findLikeCountByCommentId",key="#commentId")
    public void delete_cache_findLikeCountByCommentId(Long commentId){
    }


    /**
     * 查询缓存 查询评论回复点赞
     * @param commentReplyLikeId 评论回复点赞Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findCommentReplyLikeById",key="#commentReplyLikeId")
    public CommentReplyLike query_cache_findCommentReplyLikeById(String commentReplyLikeId){
        return likeRepository.findCommentReplyLikeById(commentReplyLikeId);
    }
    /**
     * 删除缓存 评论回复点赞
     * @param commentReplyLikeId 评论回复点赞Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findCommentReplyLikeById",key="#commentReplyLikeId")
    public void delete_cache_findCommentReplyLikeById(String commentReplyLikeId){
    }


    /**
     * 查询缓存 根据评论回复Id查询被点赞数量
     * @param commentReplyId 评论回复Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findLikeCountByCommentReplyId",key="#commentReplyId")
    public Long query_cache_findLikeCountByCommentReplyId(Long commentReplyId){
        return likeRepository.findLikeCountByCommentReplyId(commentReplyId);
    }
    /**
     * 删除缓存 根据评论回复Id查询被点赞数量
     * @param commentReplyId 评论回复Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findLikeCountByCommentReplyId",key="#commentReplyId")
    public void delete_cache_findLikeCountByCommentReplyId(Long commentReplyId){
    }


    /**
     * 查询缓存 查询问题点赞
     * @param questionLikeId 问题点赞Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findQuestionLikeById",key="#questionLikeId")
    public QuestionLike query_cache_findQuestionLikeById(String questionLikeId){
        return likeRepository.findQuestionLikeById(questionLikeId);
    }
    /**
     * 删除缓存 问题点赞
     * @param questionLikeId 问题点赞Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findQuestionLikeById",key="#questionLikeId")
    public void delete_cache_findQuestionLikeById(String questionLikeId){
    }


    /**
     * 查询缓存 根据问题Id查询被点赞数量
     * @param questionId 问题Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findLikeCountByQuestionId",key="#questionId")
    public Long query_cache_findLikeCountByQuestionId(Long questionId){
        return likeRepository.findLikeCountByQuestionId(questionId);
    }
    /**
     * 删除缓存 根据问题Id查询被点赞数量
     * @param questionId 问题Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findLikeCountByQuestionId",key="#questionId")
    public void delete_cache_findLikeCountByQuestionId(Long questionId){
    }


    /**
     * 查询缓存 查询答案点赞
     * @param answerLikeId 答案点赞Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findAnswerLikeById",key="#answerLikeId")
    public AnswerLike query_cache_findAnswerLikeById(String answerLikeId){
        return likeRepository.findAnswerLikeById(answerLikeId);
    }
    /**
     * 删除缓存 答案点赞
     * @param answerLikeId 答案点赞Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findAnswerLikeById",key="#answerLikeId")
    public void delete_cache_findAnswerLikeById(String answerLikeId){
    }


    /**
     * 查询缓存 根据答案Id查询被点赞数量
     * @param answerId 答案Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findLikeCountByAnswerId",key="#answerId")
    public Long query_cache_findLikeCountByAnswerId(Long answerId){
        return likeRepository.findLikeCountByAnswerId(answerId);
    }
    /**
     * 删除缓存 根据答案Id查询被点赞数量
     * @param answerId 答案Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findLikeCountByAnswerId",key="#answerId")
    public void delete_cache_findLikeCountByAnswerId(Long answerId){
    }


    /**
     * 查询缓存 查询答案回复点赞
     * @param answerReplyLikeId 答案回复点赞Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findAnswerReplyLikeById",key="#answerReplyLikeId")
    public AnswerReplyLike query_cache_findAnswerReplyLikeById(String answerReplyLikeId){
        return likeRepository.findAnswerReplyLikeById(answerReplyLikeId);
    }
    /**
     * 删除缓存 答案回复点赞
     * @param answerReplyLikeId 答案回复点赞Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findAnswerReplyLikeById",key="#answerReplyLikeId")
    public void delete_cache_findAnswerReplyLikeById(String answerReplyLikeId){
    }


    /**
     * 查询缓存 根据答案回复Id查询被点赞数量
     * @param answerReplyId 答案回复Id
     * @return
     */
    @Cacheable(value="likeCacheManager_cache_findLikeCountByAnswerReplyId",key="#answerReplyId")
    public Long query_cache_findLikeCountByAnswerReplyId(Long answerReplyId){
        return likeRepository.findLikeCountByAnswerReplyId(answerReplyId);
    }
    /**
     * 删除缓存 根据答案回复Id查询被点赞数量
     * @param answerReplyId 答案回复Id
     * @return
     */
    @CacheEvict(value="likeCacheManager_cache_findLikeCountByAnswerReplyId",key="#answerReplyId")
    public void delete_cache_findLikeCountByAnswerReplyId(Long answerReplyId){
    }

}
