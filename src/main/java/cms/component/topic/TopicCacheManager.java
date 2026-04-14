package cms.component.topic;

import cms.cache.CacheApiManager;
import cms.component.TextFilterComponent;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 话题缓存管理
 */
@Component("topicCacheManager")
public class TopicCacheManager {

    @Resource TextFilterComponent textFilterComponent;
    @Resource TopicRepository topicRepository;
    @Resource CommentRepository commentRepository;
    @Resource CacheApiManager cacheApiManager;


    /**
     * 话题展示IP记录(每话题每IP 24小时内只统计一次点击次数)
     * @param key 话题Id_用户IP
     * @param time 当前时间
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_ipRecord",key="#key")
    public Long ipRecord(String key,Long time){
        return time;
    }


    /**
     * 查询话题缓存
     * @param topicId 话题Id
     */
    public Topic queryTopicCache(Long topicId){

        Topic topic = (Topic)cacheApiManager.getCache("topicCacheManager_cache_topic", String.valueOf(topicId));
        if(topic == null){
            topic = topicRepository.findById(topicId);
            if(topic != null){
                this.addTopicCache(topic);//添加到缓存
            }
        }
        return topic;
    }

    /**
     * 添加话题缓存
     * @param topic 话题
     */
    public void addTopicCache(Topic topic){
        if(topic != null){
            cacheApiManager.addCache("topicCacheManager_cache_topic",  String.valueOf(topic.getId()), topic);
        }
    }
    /**
     * 删除话题缓存
     * @param topicId 话题Id
     */
    public void deleteTopicCache(Long topicId){
        cacheApiManager.deleteCache("topicCacheManager_cache_topic",String.valueOf(topicId));
    }

    /**
     * 查询缓存 查询'话题取消隐藏'
     * @param topicUnhideId 话题取消隐藏Id
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_findTopicUnhideById",key="#topicUnhideId")
    public TopicUnhide query_cache_findTopicUnhideById(String topicUnhideId){
        return topicRepository.findTopicUnhideById(topicUnhideId);
    }
    /**
     * 删除缓存 话题取消隐藏
     * @param topicUnhideId 话题取消隐藏Id
     * @return
     */
    @CacheEvict(value="topicCacheManager_cache_findTopicUnhideById",key="#topicUnhideId")
    public void delete_cache_findTopicUnhideById(String topicUnhideId){
    }

    /**
     * 查询缓存 查询用户是否评论话题
     * @param topicId 话题Id
     * @param userName 用户名称
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_findWhetherCommentTopic",key="#topicId+'_'+#userName")
    public Boolean query_cache_findWhetherCommentTopic(Long topicId,String userName){
        return commentRepository.findWhetherCommentTopic(topicId, userName);
    }
    /**
     * 删除缓存 查询用户是否评论话题
     * @param topicId 话题Id
     * @param userName 用户名称
     * @return
     */
    @CacheEvict(value="topicCacheManager_cache_findWhetherCommentTopic",key="#topicId+'_'+#userName")
    public void delete_cache_findWhetherCommentTopic(Long topicId,String userName){
    }

    /**
     * 查询缓存 解析隐藏标签
     * @param html 富文本内容
     * @param topicId 话题Id
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_analysisHiddenTag",key="#topicId")
    public Map<Integer,Object> query_cache_analysisHiddenTag(String html, Long topicId){
        return textFilterComponent.analysisHiddenTag(html);
    }
    /**
     * 删除缓存 解析隐藏标签
     * @param topicId 话题Id
     * @return
     */
    @CacheEvict(value="topicCacheManager_cache_analysisHiddenTag",key="#topicId")
    public void delete_cache_analysisHiddenTag(Long topicId){
    }


    /**
     * 查询缓存 处理隐藏标签(缓存不做删除处理，到期自动失效，由话题'随机数'做参数,确保查询为最新值)
     * @param html 富文本内容
     * @param visibleTagList 允许可见的隐藏标签
     * @param processHideTagId 处理'隐藏标签'Id
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_processHiddenTag",key="#processHideTagId")
    public String query_cache_processHiddenTag(String html, List<Integer> visibleTagList, String processHideTagId){

        return textFilterComponent.processHiddenTag(html,visibleTagList);
    }



    /**
     * 查询缓存 解析上传的文件完整路径名称
     * @param html 富文本内容
     * @param topicId 话题Id
     * @param baseUrlList 基础URL集合
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_analysisFullFileName",key="#topicId")
    public Map<String,String> query_cache_analysisFullFileName(String html,Long topicId,List<String> baseUrlList){
        return textFilterComponent.analysisFullFileName(html,"topic",baseUrlList);
    }
    /**
     * 删除缓存 解析上传的文件完整路径名称
     * @param topicId 话题Id
     * @return
     */
    @CacheEvict(value="topicCacheManager_cache_analysisFullFileName",key="#topicId")
    public void delete_cache_analysisFullFileName(Long topicId){
    }

    /**
     * 查询缓存 处理上传的文件完整路径名称(缓存不做删除处理，到期自动失效，由话题'最后修改时间'做参数,确保查询为最新值)
     * @param html 富文本内容
     * @param item 项目
     * @param newFullFileNameMap 新的完整路径名称 key: 完整路径名称 value: 重定向接口
     * @param processFullFileNameId 处理'上传的文件完整路径名称'Id
     * @param baseUrlList 基础URL集合
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_processFullFileName",key="#processFullFileNameId")
    public String query_cache_processFullFileName(String html,String item,Map<String,String> newFullFileNameMap,String processFullFileNameId, List<String> baseUrlList){
        return textFilterComponent.processFullFileName(html,item,newFullFileNameMap,baseUrlList);
    }


    /**
     * 查询缓存 标记修改话题状态
     * @param topicId 话题Id
     * @param randomNumber 随机数
     * @return
     */
    @Cacheable(value="topicCacheManager_cache_markUpdateTopicStatus",key="#topicId")
    public Integer query_cache_markUpdateTopicStatus(Long topicId, Integer randomNumber){
        return randomNumber;
    }
    /**
     * 删除缓存 标记修改话题状态
     * @param topicId 话题Id
     * @return
     */
    @CacheEvict(value="topicCacheManager_cache_markUpdateTopicStatus",key="#topicId")
    public void delete_cache_markUpdateTopicStatus(Long topicId){
    }
}
