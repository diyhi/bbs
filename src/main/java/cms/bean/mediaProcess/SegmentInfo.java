package cms.bean.mediaProcess;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 切片信息
 * @author Gao
 *
 */
public class SegmentInfo implements Serializable{
	private static final long serialVersionUID = -4090315272558555838L;
	
	/** 状态 10:没有申请媒体处理任务 20:申请媒体处理任务成功 30:申请媒体处理任务错误 **/
	private Integer status = 10;
	
	/** 错误信息 **/
	private Map<String, String> errorMap = new HashMap<String, String>();
	
	/** 媒体处理设置 **/
	private MediaProcessSetting mediaProcessSetting;
	/** 媒体处理队列 **/
	private MediaProcessQueue mediaProcessQueue;


	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Map<String, String> getErrorMap() {
		return errorMap;
	}

	public void setErrorMap(Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}

	public MediaProcessSetting getMediaProcessSetting() {
		return mediaProcessSetting;
	}

	public void setMediaProcessSetting(MediaProcessSetting mediaProcessSetting) {
		this.mediaProcessSetting = mediaProcessSetting;
	}

	public MediaProcessQueue getMediaProcessQueue() {
		return mediaProcessQueue;
	}

	public void setMediaProcessQueue(MediaProcessQueue mediaProcessQueue) {
		this.mediaProcessQueue = mediaProcessQueue;
	}

	
}
