package cms.controller.feedback;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.feedback.Feedback;
import cms.service.feedback.FeedbackService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 在线留言列表控制器
 *
 */
@RestController
public class FeedbackController {
    @Resource
    FeedbackService feedbackService;
    @Resource
    FileComponent fileComponent;

    /**
     * 友情链接列表
     * @param pageForm 页码
     * @param start_createDate 起始时间
     * @param end_createDate 结束时间
     * @return
     */
    @RequestMapping("/control/feedback/list")
    public RequestResult feedbackList(PageForm pageForm, String start_createDate, String end_createDate){
        PageView<Feedback> pageView = feedbackService.getFeedbackList(pageForm.getPage(),start_createDate,end_createDate);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
