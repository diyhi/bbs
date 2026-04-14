package cms.controller.frontend;


import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.CaptchaComponent;
import cms.component.fileSystem.FileComponent;
import cms.dto.*;
import cms.dto.frontendModule.AppendQuestionDTO;
import cms.dto.frontendModule.QuestionDTO;
import cms.dto.user.AccessUser;
import cms.model.question.Question;
import cms.service.frontend.QuestionClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台问题控制器
 */
@RestController("frontendQuestionController")
public class QuestionController {
    @Resource
    QuestionClientService questionClientService;
    @Resource
    FileComponent fileComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    MessageSource messageSource;
    /**
     * 问题分页
     * @param pageForm 页码
     * @param questionTagId 标签Id
     * @param filterCondition 过滤条件
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020100)
    public PageView<Question> questionPage(PageForm pageForm, Long questionTagId, Integer filterCondition,HttpServletRequest request){
        return questionClientService.getQuestionPage(pageForm.getPage(),questionTagId,filterCondition,request);
    }

    /**
     * 相似问题
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6021100)
    public List<Question> similarQuestion(Long questionId,HttpServletRequest request) {
        return questionClientService.getSimilarQuestion(questionId,request);
    }

    /**
     * 问题明细
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020200)
    public Question questionDetail(Long questionId,HttpServletRequest request){
        return questionClientService.getQuestionDetail(questionId,request);
    }


    /**
     * 添加问题表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020500)
    public Map<String,Object> addQuestionUI(){
        return questionClientService.getAddQuestionViewModel();
    }


    /**
     * 问题  添加
     * @param questionDTO 问题表单
     * @param result  存储校验信息
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020100)
    @RequestMapping(value="/user/control/question/add", method= RequestMethod.POST)
    public ClientRequestResult add(@ModelAttribute QuestionDTO questionDTO, BindingResult result,
                                   HttpServletRequest request) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            AccessUser accessUser = AccessUserThreadLocal.get();
            //验证码
            boolean isCaptcha = captchaComponent.question_isCaptcha(accessUser.getUserName());
            return ClientRequestResult.fail(errors).addIf(isCaptcha,"captchaKey", UUIDUtil.getUUID32());
        }
        questionClientService.addQuestion(questionDTO,request);
        return ClientRequestResult.success();
    }

    /**
     * 追加问题表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020600)
    public Map<String,Object> appendQuestionUI(){
        return questionClientService.getAppendQuestionViewModel();
    }

    /**
     * 问题  追加
     * @param appendQuestionDTO 追加问题表单
     * @param result  存储校验信息
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020200)
    @RequestMapping(value="/user/control/question/appendQuestion", method=RequestMethod.POST)
    public ClientRequestResult appendQuestion(@ModelAttribute AppendQuestionDTO appendQuestionDTO, BindingResult result,
                                 HttpServletRequest request) {
        questionClientService.appendQuestion(appendQuestionDTO, request);
        return ClientRequestResult.success();
    }

    /**
     * 文件上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param dir: 上传类型，分别为image、file
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020300)
    @RequestMapping(value="/user/control/question/upload", method=RequestMethod.POST)
    public Map<String,Object> upload(String dir, String fileName,
                         MultipartFile file, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return questionClientService.uploadFile("image", fileName,file, fileServerAddress);
    }

    /**
     * 我的问题
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1021200)
    @RequestMapping(value="/user/control/questionList",method=RequestMethod.GET)
    public PageView<Question> questionListUI(PageForm pageForm){
        return questionClientService.getQuestionList(pageForm.getPage());
    }
}
