package cms.controller.question;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.service.question.QuestionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 问题列表控制器
 *
 */
@RestController
public class QuestionController {
    @Resource
    QuestionService questionService;
    @Resource
    FileComponent fileComponent;

    /**
     * 问题列表
     * @param pageForm 页码
     * @param visible 是否可见
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/question/list")
    public RequestResult queryQuestionList(PageForm pageForm,Boolean visible,HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Question> pageView = questionService.getQuestionList(pageForm.getPage(),visible,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }


    /**
     * 搜索问题
     * @param pageForm 分页
     * @param dataSource 数据源
     * @param keyword 关键词
     * @param tagId 标签Id
     * @param tagName 标签名称
     * @param account 账号
     * @param start_postTime 起始发贴时间
     * @param end_postTime 结束发贴时间
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/question/search")
    public RequestResult search(PageForm pageForm, Integer dataSource,String keyword,String tagId,String tagName,String account,
                                String start_postTime,String end_postTime, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Question> pageView = questionService.getSearch(pageForm.getPage(), dataSource, keyword, tagId, tagName, account,
                start_postTime, end_postTime,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 全部待审核问题
     * @param pageForm 分页
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/question/allAuditQuestion")
    public RequestResult allAuditQuestion(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Question> pageView = questionService.getPendingQuestions(pageForm.getPage(), fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 全部待审核答案
     * @param pageForm 分页
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/question/allAuditAnswer")
    public RequestResult  allAuditAnswer(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Answer> pageView = questionService.getAllPendingAnswers(pageForm.getPage(), fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 全部待审核回复
     * @param pageForm 分页
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/question/allAuditAnswerReply")
    public RequestResult allAuditAnswerReply(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<AnswerReply> pageView = questionService.getAllPendingReplies(pageForm.getPage(), fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

}
