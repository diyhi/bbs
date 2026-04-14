package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.model.question.QuestionTag;
import cms.service.frontend.QuestionTagClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台问答标签控制器
 */
@RestController("frontendQuestionTagController")
public class QuestionTagController {
    @Resource
    QuestionTagClientService questionTagClientService;

    /**
     * 问答标签列表
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020400)
    public List<QuestionTag> questionTagList(HttpServletRequest request){
        return questionTagClientService.getAllQuestionTagList(request);
    }
}
