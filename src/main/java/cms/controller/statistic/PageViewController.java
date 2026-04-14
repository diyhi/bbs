package cms.controller.statistic;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.statistic.PV;
import cms.service.statistic.PageViewService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 页面浏览量列表控制器
 *
 */
@RestController
public class PageViewController {
    @Resource
    PageViewService pageViewService;

    /**
     * 页面浏览量列表
     * @param pageForm 页码
     * @param start_times 起始时间
     * @param end_times 结束时间
     * @return
     */
    @RequestMapping("/control/pageView/list")
    public RequestResult pageViewList(PageForm pageForm, String start_times, String end_times){
        PageView<PV> pageView = pageViewService.getPageViewList(pageForm.getPage(),start_times,end_times);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
}
