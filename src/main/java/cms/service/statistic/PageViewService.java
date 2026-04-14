package cms.service.statistic;

import cms.dto.PageForm;
import cms.dto.PageView;
import cms.model.statistic.PV;

/**
 * 页面浏览量服务
 */
public interface PageViewService {

    /**
     * 获取页面浏览量列表
     * @param page 页码
     * @param start_times 起始时间
     * @param end_times 结束时间
     */
    public PageView<PV> getPageViewList(int page, String start_times, String end_times);
}
