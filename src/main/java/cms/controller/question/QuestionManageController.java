package cms.controller.question;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.question.AppendQuestionRequest;
import cms.dto.question.QuestionRequest;
import cms.service.question.QuestionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 问题管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/question/manage")
public class QuestionManageController {
    @Resource
    QuestionService questionService;
    @Resource FileComponent fileComponent;


    /**
     * 问题   查看
     * @param questionId 问题Id
     * @param answerId 答案Id
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=view",method=RequestMethod.GET)
    public RequestResult view(Long questionId,Long answerId,Integer page,
                       HttpServletRequest request) {
        Map<String,Object> returnValue = questionService.getQuestionDetail(questionId,answerId,page,request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 问题   添加界面显示
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addUI() throws Exception {
        Map<String,Object> returnValue = questionService.getAddQuestionViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 问题  添加
     * @param questionRequest 问题表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute QuestionRequest questionRequest, BindingResult result,
                             HttpServletRequest request){
        questionService.addQuestion(questionRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 问题   追加界面显示
     * @param questionId 问题Id
     * @return
     */
    @RequestMapping(params="method=appendQuestion",method=RequestMethod.GET)
    public RequestResult appendQuestionUI(Long questionId) {
        Map<String,Object> returnValue = questionService.getAddAdditionalQuestionViewModel(questionId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 追加问题
     * @param appendQuestionRequest 追加问题表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=appendQuestion", method=RequestMethod.POST)
    public RequestResult appendQuestion(@ModelAttribute AppendQuestionRequest appendQuestionRequest, BindingResult result,
                             HttpServletRequest request){
        questionService.appendQuestion(appendQuestionRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 文件上传
     * 员工发问题 上传文件名为UUID + a + 员工Id
     * 用户发问题 上传文件名为UUID + b + 用户Id
     * @param dir 上传类型，分别为image、flash、media、file
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=upload",method=RequestMethod.POST)
    public Map<String,Object> upload(String dir,String userName, Boolean isStaff,String fileName,
                         MultipartFile file, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return questionService.uploadFile(dir, userName, isStaff,fileName, fileServerAddress, file);
    }

    /**
     * 问题   修改界面显示
     * @param questionId 问题Id
     * @return
     */
    @RequestMapping(params="method=editQuestion", method=RequestMethod.GET)
    public RequestResult editQuestionUI(Long questionId) {
        Map<String, Object> returnValue = questionService.getEditQuestionViewModel(questionId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 问题   修改
     * @param questionRequest 问题表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editQuestion", method=RequestMethod.POST)
    public RequestResult editQuestion(@ModelAttribute QuestionRequest questionRequest, BindingResult result,
                                      HttpServletRequest request) {
        questionService.editQuestion(questionRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 问题   修改追加界面显示
     * @param questionId 问题Id
     * @param appendQuestionItemId 追加问题项Id
     * @return
     */
    @RequestMapping(params="method=editAppendQuestion",method=RequestMethod.GET)
    public RequestResult editAppendQuestionUI(Long questionId,String appendQuestionItemId) {
        Map<String, Object> returnValue = questionService.getEditAdditionalQuestionViewModel(questionId,appendQuestionItemId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 问题   追加修改
     * @param appendQuestionRequest 追加问题表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=editAppendQuestion", method=RequestMethod.POST)
    public RequestResult editAppendQuestion(@ModelAttribute AppendQuestionRequest appendQuestionRequest, BindingResult result,
                                            HttpServletRequest request) {
        questionService.editAppendQuestion(appendQuestionRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 问题   删除
     * @param questionId 问题Id集合
     */
    @RequestMapping(params="method=deleteQuestion", method=RequestMethod.POST)
    public RequestResult deleteQuestion(Long[] questionId) {
        questionService.deleteQuestion(questionId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
    /**
     * 追加问题   删除
     * @param questionId 问题Id集合
     * @param appendQuestionItemId 追加问题项Id
     * @return
     */
    @RequestMapping(params="method=deleteAppendQuestion", method=RequestMethod.POST)
    public RequestResult deleteAppendQuestion(Long questionId,String appendQuestionItemId) {
        questionService.deleteAdditionalQuestion(questionId, appendQuestionItemId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 还原
     * @param questionId 问题Id集合
     * @return
     */
    @RequestMapping(params="method=reduction",method=RequestMethod.POST)
    public RequestResult reduction(Long[] questionId) {
        questionService.reductionQuestion(questionId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 审核问题
     * @param questionId 问题Id
     * @return
     */
    @RequestMapping(params="method=auditQuestion",method=RequestMethod.POST)
    public RequestResult auditQuestion(Long questionId) {
        questionService.auditQuestion(questionId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}