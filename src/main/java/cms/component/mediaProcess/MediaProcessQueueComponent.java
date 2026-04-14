package cms.component.mediaProcess;

import org.springframework.stereotype.Component;



/**
 * 媒体处理队列组件
 * @author Gao
 *
 */
@Component("mediaProcessQueueComponent")
public class MediaProcessQueueComponent {

	
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
	

	
	
}
