package cms.controller.frontend;


import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.model.help.Help;
import cms.model.help.HelpType;
import cms.service.frontend.HelpClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 前台帮助控制器
 */
@RestController("frontendHelpController")
public class HelpController {
    @Resource
    HelpClientService helpClientService;
    @Resource
    FileComponent fileComponent;



    /**
     * 在线帮助分页
     * @param pageForm   页码
     * @param helpTypeId 分类Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6110100)
    public PageView<Help> helpPage(PageForm pageForm, Long helpTypeId, HttpServletRequest request) {
        return helpClientService.getHelpPage(pageForm.getPage(),helpTypeId,request);
    }

    /**
     * 在线帮助列表
     * @param helpTypeId 分类Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6110600)
    public List<Help> helpList(Long helpTypeId) {
        return helpClientService.getHelpList(helpTypeId);
    }

    /**
     * 推荐在线帮助
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6110200)
    public List<Help> recommendHelp(HttpServletRequest request) {
        return helpClientService.getRecommendHelp(request);
    }


    /**
     * 在线帮助分类
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6110300)
    public List<HelpType> type(HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return helpClientService.getType(fileServerAddress);
    }

    /**
     * 在线帮助导航
     * @param helpTypeId 分类Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6110400)
    public Map<Long,String> navigation(Long helpTypeId) {
        return helpClientService.getNavigation(helpTypeId);
    }

    /**
     * 在线帮助明细
     * @param helpId 在线帮助Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6110500)
    public Help helpDetail(Long helpId,HttpServletRequest request){
        return helpClientService.getHelpDetail(helpId,request);
    }
}