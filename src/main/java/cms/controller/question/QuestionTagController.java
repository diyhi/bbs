package cms.controller.question;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.question.QuestionTagService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 问答标签列表控制器
 * @author Administrator
 *
 */
@RestController
public class QuestionTagController {
	private static final Logger logger = LogManager.getLogger(QuestionTagController.class);

    @Resource QuestionTagService questionTagService;
    @Resource FileComponent fileComponent;


    /**
     * 标签列表
     * @param parentId 父Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/questionTag/list")
    public RequestResult tagList(PageForm pageForm,Long parentId, HttpServletRequest request){
        Map<String,Object> returnValue = new LinkedHashMap<>();
        returnValue.put("navigation", questionTagService.getTabNavigation(parentId));
        String fileServerAddress = fileComponent.fileServerAddress(request);
        returnValue.put("pageView", questionTagService.getTagList(parentId,fileServerAddress,pageForm.getPage()));
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }



}
