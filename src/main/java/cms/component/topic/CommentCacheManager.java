package cms.component.topic;

import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.repository.topic.CommentRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 评论缓存管理
 */
@Component("commentCacheManager")
public class CommentCacheManager {
    @Resource CommentRepository commentRepository;


    /**
     * 查询评论缓存
     * @param commentId 评论Id
     */
    @Cacheable(value="commentCacheManager_cache_findByCommentId",key="#commentId")
    public Comment query_cache_findByCommentId(Long commentId){
        return commentRepository.findByCommentId(commentId);
    }
    /**
     * 删除评论缓存
     * @param commentId 评论Id
     */
    @CacheEvict(value="commentCacheManager_cache_findByCommentId",key="#commentId")
    public void delete_cache_findByCommentId(Long commentId){

    }

    /**
     * 查询回复缓存
     * @param replyId 回复Id
     */
    @Cacheable(value="commentCacheManager_cache_findReplyByReplyId",key="#replyId")
    public Reply query_cache_findReplyByReplyId(Long replyId){
        return commentRepository.findReplyByReplyId(replyId);
    }
    /**
     * 删除回复缓存
     * @param replyId 回复Id
     */
    @CacheEvict(value="commentCacheManager_cache_findReplyByReplyId",key="#replyId")
    public void delete_cache_findReplyByReplyId(Long replyId){
    }
}
