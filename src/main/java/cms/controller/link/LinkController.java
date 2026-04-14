package cms.controller.link;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.links.Links;
import cms.service.link.LinkService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 友情链接列表控制器
 *
 */
@RestController
public class LinkController {
    @Resource
    LinkService linkService;
    @Resource
    FileComponent fileComponent;

    /**
     * 友情链接列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/links/list")
    public RequestResult linkList(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        List<Links> list = linkService.getLinkList(pageForm.getPage(),fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, list);
    }
}
