package cms.service.mediaProcess.impl;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.mediaProcess.MediaProcessQueue;
import cms.bean.mediaProcess.MediaProcessSetting;
import cms.service.besa.DaoSupport;
import cms.service.mediaProcess.MediaProcessService;

/**
 * 没有实现
 * @author Gao
 *
 */
@Service
@Transactional
public class MediaProcessServiceBean extends DaoSupport<MediaProcessQueue> implements MediaProcessService{
	/**
	 * 没有实现
	 * @return
	 */
	public MediaProcessSetting findMediaProcessSetting(){
		
		return null;
	}
	/**
	 * 没有实现
	 * @return
	 */
	public MediaProcessSetting findMediaProcessSetting_cache(){
		return this.findMediaProcessSetting();
	}
	
	/**
	 * 没有实现
	 * @param mediaProcessSetting
	 * @return
	 */
	public void updateMediaProcessSetting(MediaProcessSetting mediaProcessSetting){
		
	}
	
	
	
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理队列Id
	 */
	public MediaProcessQueue findMediaProcessQueueById(Long mediaProcessQueueId){
		
		return null;
	}
	
	/**
	 * 没有实现
	 * @param mediaProcessQueue 媒体处理队列
	 */
	public void saveMediaProcessQueueList(List<MediaProcessQueue> mediaProcessQueueList){
		
	}
	
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理Id
	 * @param ip 申请任务的客户端IP
	 * @return
	 */
	public Integer updateMediaProcessQueue(Long mediaProcessQueueId,String ip){
		return null;
	}
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理Id
	 * @param processProgress 处理进度
	 * @return
	 */
	public Integer updateMediaProcessQueue(Long mediaProcessQueueId,Double processProgress){
		return null;
	}
	
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理Id
	 * @param errorInfo 错误信息
	 * @return
	 */
	public Integer addMediaErrorInfo(Long mediaProcessQueueId,String errorInfo){
		return null;
	}
	
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理队列Id
	 */
	public Integer deleteMediaProcessQueue(Long mediaProcessQueueId){
		return null;
	}
	
	/**
	 * 没有实现
	 * @param fileNameList 文件名称集合
	 */
	public Integer deleteMediaProcessQueue(List<String> fileNameList){
		return null;
	}
	
	/**
	 * 没有实现
	 * @return
	 */
	public MediaProcessQueue findMediaProcessQueueByFileName(String fileName){
		return null;
	}
	/**
	 * 没有实现
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public List<MediaProcessQueue> findPendingMedia(int firstIndex, int maxResult){
		return null;
		
	}
	/**
	 * 没有实现
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public List<MediaProcessQueue> findPendingMedia_cache(int firstIndex, int maxResult){
		return null;
	}
	
}
