package cms.controller.help;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.help.HelpTypeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 帮助分类列表控制器
 * @author Administrator
 *
 */
@RestController
public class HelpTypeController {
	private static final Logger logger = LogManager.getLogger(HelpTypeController.class);

    @Resource HelpTypeService helpTypeService;
    @Resource FileComponent fileComponent;



    /**
     * 帮助分类列表
     * @param pageForm 页码
     * @param parentId 父Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/helpType/list")
    public RequestResult helpTypeList(PageForm pageForm, Long parentId, HttpServletRequest request){
        Map<String,Object> returnValue = new LinkedHashMap<>();
        returnValue.put("navigation", helpTypeService.getTypeNavigation(parentId));
        String fileServerAddress = fileComponent.fileServerAddress(request);
        returnValue.put("pageView", helpTypeService.getHelpTypeList(parentId,fileServerAddress,pageForm.getPage()));
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }



}
