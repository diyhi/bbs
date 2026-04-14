package cms.service.frontend;


import cms.dto.PageView;
import cms.dto.fulltext.SearchResult;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 前台搜索服务接口
 */
public interface SearchClientService {
    /**
     * 获取热门搜索词
     * @param request 请求信息
     * @return
     */
    public List<String> getHotSearchWords(HttpServletRequest request);
    /**
     * 获取搜索
     * @param page 页码
     * @param keyword 关键词
     * @param request 请求信息
     * @return
     */
    public PageView<SearchResult> getSearch(int page, String keyword,HttpServletRequest request);
}
