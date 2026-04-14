package cms.controller.platformShare;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.platformShare.TopicUnhidePlatformShare;
import cms.service.platformShare.TopicUnhidePlatformShareService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 解锁话题隐藏内容平台分成控制器
 *
 */
@RestController
public class TopicUnhidePlatformShareController {
    @Resource
    TopicUnhidePlatformShareService topicUnhidePlatformShareService;
    /**
     * 解锁话题隐藏内容平台分成列表
     * @param pageForm 页码
     * @param start_times 超始时间
     * @param end_times 结束时间
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topicUnhidePlatformShare/list")
    public RequestResult topicUnhidePlatformShareList(PageForm pageForm,String start_times,String end_times, HttpServletRequest request){
        PageView<TopicUnhidePlatformShare> pageView = topicUnhidePlatformShareService.getTopicUnhidePlatformShareList(pageForm.getPage(),start_times,end_times,request);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
