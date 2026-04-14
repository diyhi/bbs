package cms.controller.platformShare;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.service.platformShare.QuestionRewardPlatformShareService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问答悬赏平台分成控制器
 *
 */
@RestController
public class QuestionRewardPlatformShareController {
    @Resource
    QuestionRewardPlatformShareService questionRewardPlatformShareService;
    /**
     * 问答悬赏平台分成列表
     * @param pageForm 页码
     * @param start_times 超始时间
     * @param end_times 结束时间
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/questionRewardPlatformShare/list")
    public RequestResult questionRewardPlatformShareList(PageForm pageForm,String start_times,String end_times, HttpServletRequest request){
        PageView<QuestionRewardPlatformShare> pageView = questionRewardPlatformShareService.getQuestionRewardPlatformShareList(pageForm.getPage(),start_times,end_times,request);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
