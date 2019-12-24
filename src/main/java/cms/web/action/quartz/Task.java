package cms.web.action.quartz;

import javax.annotation.Resource;

import cms.web.action.fileSystem.FileManage;
import cms.web.action.lucene.QuestionIndexManage;
import cms.web.action.lucene.TopicIndexManage;
import cms.web.action.thumbnail.ThumbnailManage;

import org.springframework.stereotype.Component;

/**
 * 定时任务类
 *
 */
@Component("task")
public class Task {
	
	@Resource FileManage fileManage;
	
	@Resource ThumbnailManage thumbnailManage;
	@Resource TopicIndexManage topicIndexManage;
	@Resource QuestionIndexManage questionIndexManage;
	
	/**
	 * 话题全文索引
	 */
	public void topicIndex() {
		topicIndexManage.updateTopicIndex();
	}
	/**
	 * 问题全文索引
	 */
	public void questionIndex() {
		questionIndexManage.updateQuestionIndex();
	}
	
	/**
	 * 处理缩略图
	 */
	public void treatmentThumbnail() {
		thumbnailManage.treatmentThumbnail();
	}
	
	/**
	 * 删除无效的上传临时文件
	 */
	public void deleteInvalidFile() {
		fileManage.deleteInvalidFile();
	}
	
	
}
