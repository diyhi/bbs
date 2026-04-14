package cms.component.mediaProcess;

import cms.component.TextFilterComponent;
import cms.dto.MediaInfo;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 媒体处理队列缓存管理
 */
@Component("mediaProcessQueueCacheManager")
public class MediaProcessQueueCacheManager {
    @Resource
    TextFilterComponent textFilterComponent;
    /**
     * 查询缓存 处理视频播放器标签(缓存不做删除处理，到期自动失效，由话题'随机数'做参数,确保查询为最新值)
     * @param localUrl 获取网站URL Configuration.getUrl(request)
     * @param html 富文本内容
     * @param processVideoPlayerId 处理'视频播放器'Id
     * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
     * @param secret 密钥 16位字符
     * @return
     */
    @Cacheable(value="mediaProcessQueueCacheManager_cache_processVideoPlayer",key="#processVideoPlayerId")
    public String query_cache_processVideoPlayer(String localUrl,String html,String processVideoPlayerId,Long tagId,String secret){

        return textFilterComponent.processVideoPlayer(localUrl,html,tagId,secret);
    }

    /**
     * 查询缓存 处理视频信息(缓存不做删除处理，到期自动失效，由话题'随机数'做参数,确保查询为最新值)
     * @param localUrl 获取网站URL Configuration.getUrl(request)
     * @param html 富文本内容
     * @param processVideoPlayerId 处理'视频播放器'Id
     * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
     * @param secret 密钥 16位字符
     * @return
     */
    @Cacheable(value="mediaProcessQueueCacheManager_cache_processVideoInfo",key="#processVideoPlayerId")
    public List<MediaInfo> query_cache_processVideoInfo(String localUrl, String html, String processVideoPlayerId, Long tagId, String secret){

        return textFilterComponent.processVideoInfo(localUrl,html,tagId,secret);
    }


    /**
     * 删除缓存 根据文件名称查询媒体处理
     * @param fileName 文件名称
     * @return
     */
    @CacheEvict(value="mediaProcessQueueCacheManager_cache_findMediaProcessQueueByFileName",key="#fileName")
    public void delete_cache_findMediaProcessQueueByFileName(String fileName){

    }


    /**
     * 添加请求的唯一标识
     * @param nonce 请求的唯一标识
     */
    @CachePut(value="mediaProcessQueueCacheManager_cache_nonce",key="#nonce")
    public String add_cache_nonce(String nonce) {
        return nonce;
    }
    /**
     * 查询请求的唯一标识
     * @param nonce 请求的唯一标识
     * @return
     */
    @Cacheable(value="mediaProcessQueueCacheManager_cache_nonce",key="#nonce")
    public String query_cache_nonce(String nonce){
        return null;
    }

}
