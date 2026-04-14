package cms.controller.feedback;

import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.link.LinkRequest;
import cms.model.feedback.Feedback;
import cms.service.feedback.FeedbackService;
import cms.service.link.LinkService;
import cms.validator.link.LinkValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 在线留言管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/feedback/manage")
public class FeedbackManageController {
    @Resource
    FeedbackService feedbackService;
    @Resource
    MessageSource messageSource;


    /**
     * 在线留言 查看
     * @param feedbackId 在线留言Id
     * @return
     */
    @RequestMapping(params="method=view",method=RequestMethod.GET)
    public RequestResult view(Long feedbackId){
        Feedback feedback = feedbackService.getFeedbackDetail(feedbackId);
        return new RequestResult(ResultCode.SUCCESS, feedback);
    }



    /**
     * 在线留言 删除
     * @param feedbackId 在线留言Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
