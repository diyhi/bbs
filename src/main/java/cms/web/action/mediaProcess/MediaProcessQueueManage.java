package cms.web.action.mediaProcess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import cms.bean.MediaInfo;
import cms.bean.mediaProcess.MediaProcessQueue;

import cms.bean.topic.Topic;
import cms.service.mediaProcess.MediaProcessService;
import cms.service.topic.TopicService;
import cms.web.action.TextFilterManage;
import cms.web.action.topic.TopicManage;

/**
 * 媒体处理队列管理
 * @author Gao
 *
 */
@Component("mediaProcessQueueManage")
public class MediaProcessQueueManage {
	
	@Resource MediaProcessService mediaProcessService;
	@Resource TopicService topicService;
	@Resource TextFilterManage textFilterManage;
	@Resource MediaProcessQueueManage mediaProcessQueueManage;
	@Resource TopicManage topicManage;
	
	
	
	 /**
     * 生成处理'视频播放器'Id
     * @param topicId 话题Id
     * @param topicContentUpdateMark 话题内容修改标记
     * @return
     */
    public String createProcessVideoPlayerId(Long topicId,Integer topicContentUpdateMark){
    	String id = topicId+"";
    	if(topicContentUpdateMark != null){
			id+="|"+topicContentUpdateMark;
		}else{
			id+="|";
		}
    	return id;
    }
	
    /**
	 * 查询缓存 处理视频播放器标签(缓存不做删除处理，到期自动失效，由话题'随机数'做参数,确保查询为最新值)
	 * @param localUrl 获取网站URL Configuration.getUrl(request)
	 * @param html 富文本内容
	 * @param processVideoPlayerId 处理'视频播放器'Id
	 * @param tagId 标签Id  -1表示管理后台打开链接，不校验权限
	 * @param secret 密钥 16位字符
	 * @return
	 */
	@Cacheable(value="mediaProcessQueueManage_cache_processVideoPlayer",key="#processVideoPlayerId")
	public String query_cache_processVideoPlayer(String localUrl,String html,String processVideoPlayerId,Long tagId,String secret){
		
		return textFilterManage.processVideoPlayer(localUrl,html,tagId,secret);
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
	@Cacheable(value="mediaProcessQueueManage_cache_processVideoInfo",key="#processVideoPlayerId")
	public List<MediaInfo> query_cache_processVideoInfo(String localUrl,String html,String processVideoPlayerId,Long tagId,String secret){
		
		return textFilterManage.processVideoInfo(localUrl,html,tagId,secret);
	}
	
	
	/**
	 * 查询缓存 根据文件名称查询媒体处理
	 * @param fileName 文件名称
	 * @return
	 */
	@Cacheable(value="mediaProcessQueueManage_cache_findMediaProcessQueueByFileName",key="#fileName")
	public MediaProcessQueue query_cache_findMediaProcessQueueByFileName(String fileName){
		return mediaProcessService.findMediaProcessQueueByFileName(fileName);
	}
	/**
	 * 删除缓存 根据文件名称查询媒体处理
	 * @param fileName 文件名称
	 * @return
	 */
	@CacheEvict(value="mediaProcessQueueManage_cache_findMediaProcessQueueByFileName",key="#fileName")
	public void delete_cache_findMediaProcessQueueByFileName(String fileName){
		
	}

	
	/**
	 * 添加请求的唯一标识
	 * @param nonce 请求的唯一标识
	 */
	@CachePut(value="mediaProcessQueueManage_cache_nonce",key="#nonce")
    public String add_cache_nonce(String nonce) {
		return nonce;
    }
	/**
	 * 查询请求的唯一标识
	 * @param nonce 请求的唯一标识
	 * @return
	 */
	@Cacheable(value="mediaProcessQueueManage_cache_nonce",key="#nonce")
	public String query_cache_nonce(String nonce){
		return null;
	}
	
	
	
}
