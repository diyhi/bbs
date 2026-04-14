package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.fulltext.SearchResult;
import cms.service.frontend.SearchClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台搜索控制器
 */
@RestController
public class SearchController {
    @Resource
    SearchClientService searchClientService;
    /**
     * 获取热门搜索词
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6150200)
    public List<String> hotSearchWords(HttpServletRequest request){
        return searchClientService.getHotSearchWords(request);
    }

    /**
     * 搜索
     * @param keyword 关键词
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1150800)
    @RequestMapping(value="/search",method = RequestMethod.GET)
    public PageView<SearchResult> search(PageForm pageForm,String keyword,
                                         HttpServletRequest request){
        return searchClientService.getSearch(pageForm.getPage(),keyword,request);
    }
}
