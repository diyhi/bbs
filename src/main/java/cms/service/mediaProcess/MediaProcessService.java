package cms.service.mediaProcess;

import java.util.List;


import cms.bean.mediaProcess.MediaProcessQueue;
import cms.bean.mediaProcess.MediaProcessSetting;
import cms.service.besa.DAO;

/**
 * 没有实现
 * @author Gao
 *
 */
public interface MediaProcessService extends DAO<MediaProcessQueue>{
	/**
	 * 没有实现
	 * @return
	 */
	public MediaProcessSetting findMediaProcessSetting();
	/**
	 * 没有实现
	 * @return
	 */
	public MediaProcessSetting findMediaProcessSetting_cache();
	
	/**
	 * 没有实现
	 * @param mediaProcessSetting
	 * @return
	 */
	public void updateMediaProcessSetting(MediaProcessSetting mediaProcessSetting);
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理队列Id
	 */
	public MediaProcessQueue findMediaProcessQueueById(Long mediaProcessQueueId);
	/**
	 * 没有实现
	 * @param mediaProcessQueue 媒体处理队列
	 */
	public void saveMediaProcessQueueList(List<MediaProcessQueue> mediaProcessQueueList);
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理Id
	 * @param ip 申请任务的客户端IP
	 * @return
	 */
	public Integer updateMediaProcessQueue(Long mediaProcessQueueId,String ip);
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理Id
	 * @param processProgress 处理进度
	 * @return
	 */
	public Integer updateMediaProcessQueue(Long mediaProcessQueueId,Double processProgress);
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理Id
	 * @param errorInfo 错误信息
	 * @return
	 */
	public Integer addMediaErrorInfo(Long mediaProcessQueueId,String errorInfo);
	/**
	 * 没有实现
	 * @param mediaProcessQueueId 媒体处理队列Id
	 */
	public Integer deleteMediaProcessQueue(Long mediaProcessQueueId);
	/**
	 * 没有实现
	 * @param fileNameList 文件名称集合
	 */
	public Integer deleteMediaProcessQueue(List<String> fileNameList);
	/**
	 * 没有实现
	 * @return
	 */
	public MediaProcessQueue findMediaProcessQueueByFileName(String fileName);
	/**
	 * 没有实现
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public List<MediaProcessQueue> findPendingMedia(int firstIndex, int maxResult);
	/**
	 * 没有实现
	 * @param firstIndex 索引开始,即从哪条记录开始
	 * @param maxResult 获取多少条数据
	 * @return
	 */
	public List<MediaProcessQueue> findPendingMedia_cache(int firstIndex, int maxResult);
}
