package cms.controller.topic;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.topic.TagService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 话题标签列表控制器
 * @author Administrator
 *
 */
@RestController
public class TagController {
	private static final Logger logger = LogManager.getLogger(TagController.class);

    @Resource TagService tagService;
    @Resource FileComponent fileComponent;


    /**
     * 标签列表
     * @param pageForm 页码
     * @param parentId 父Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/tag/list")
    public RequestResult tagList(PageForm pageForm,Long parentId, HttpServletRequest request){
        Map<String,Object> returnValue = new LinkedHashMap<>();
        returnValue.put("navigation", tagService.getTabNavigation(parentId));
        String fileServerAddress = fileComponent.fileServerAddress(request);
        returnValue.put("pageView", tagService.getTagList(parentId,fileServerAddress,pageForm.getPage()));
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }



}
