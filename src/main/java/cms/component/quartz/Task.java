package cms.component.quartz;


import cms.component.fileSystem.FileComponent;
import cms.component.lucene.QuestionIndexComponent;
import cms.component.lucene.TopicIndexComponent;
import cms.component.thumbnail.ThumbnailComponent;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务组件
 *
 */
@Component("task")
public class Task {
	
	@Resource FileComponent fileComponent;
	@Resource ThumbnailComponent thumbnailComponent;
	@Resource TopicIndexComponent topicIndexComponent;
	@Resource QuestionIndexComponent questionIndexComponent;




    /**
     * 话题全文索引
     */
    @Async("taskExecutor_topicIndexComponent_topicIndex")
    @Scheduled(cron = "0 0/1 * * * ?")//每隔1分钟执行一次
    public void topicIndex() {
        topicIndexComponent.updateTopicIndex();
    }
    /**
     * 问题全文索引
     */
    @Async("taskExecutor_questionIndexComponent_questionIndex")
    @Scheduled(cron = "0 0/1 * * * ?")//每隔1分钟执行一次
    public void questionIndex() {
        questionIndexComponent.updateQuestionIndex();
    }

    /**
     * 处理缩略图
     */
    @Async("taskExecutor_thumbnailComponent_treatmentThumbnail")
    @Scheduled(cron = "0 0/10 * * * ?")//每隔10分钟运行一次
    public void treatmentThumbnail() {
        //本地文件系统
        thumbnailComponent.treatmentThumbnail();
    }

    /**
     * 删除无效的上传临时文件
     */
    @Async("taskExecutor_fileComponent_deleteInvalidFile")
    @Scheduled(cron = "0 50 0/2 * * ?")//每隔两小时的50分运行一次
    public void deleteInvalidFile() {
        fileComponent.deleteInvalidFile();
    }

	
}
