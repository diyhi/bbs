package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.dto.ClientRequestResult;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.AnswerDTO;
import cms.dto.frontendModule.UserAnswerCountDTO;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.service.frontend.AnswerClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 前台答案控制器
 */
@RestController
public class AnswerController {
    @Resource
    AnswerClientService answerClientService;
    @Resource
    FileComponent fileComponent;

    /**
     * 答案分页
     * @param page 页码
     * @param questionId 问题Id
     * @param answerId 答案Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020300)
    public PageView<Answer> answerPage(Integer page, Long questionId, Long answerId,HttpServletRequest request){
        return answerClientService.getAnswerPage(page,questionId, answerId,request);
    }
    /**
     * 添加答案表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020700)
    public Map<String,Object> addAnswerUI(){
        return answerClientService.getAddAnswerViewModel();
    }
    /**
     * 答案   添加
     * @param answerDTO 答案表单
     * @param result  存储校验信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020500)
    @RequestMapping(value="/user/control/answer/add", method= RequestMethod.POST)
    public ClientRequestResult add(@ModelAttribute AnswerDTO answerDTO, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response){
        answerClientService.addAnswer(answerDTO,request);
        return ClientRequestResult.success();
    }

    /**
     * 修改答案表单
     * @param answerId 答案Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020900)
    public Map<String,Object> editAnswerUI(Long answerId){
        return answerClientService.getEditAnswerViewModel(answerId);
    }
    /**
     * 答案  修改
     * @param answerDTO 答案表单
     * @param result  存储校验信息
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020600)
    @RequestMapping(value="/user/control/answer/edit", method=RequestMethod.POST)
    public ClientRequestResult edit(@ModelAttribute AnswerDTO answerDTO, BindingResult result,
                       HttpServletRequest request, HttpServletResponse response) {
        answerClientService.editAnswer(answerDTO,request);
        return ClientRequestResult.success();
    }
    /**
     * 答案  删除
     * @param answerId 答案Id
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020700)
    @RequestMapping(value="/user/control/answer/delete", method=RequestMethod.POST)
    public ClientRequestResult delete(Long answerId,
                         HttpServletRequest request, HttpServletResponse response) {
        answerClientService.deleteAnswer(answerId);
        return ClientRequestResult.success();
    }

    /**
     * 采纳答案
     * @param answerId 答案Id
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020400)
    @RequestMapping(value="/user/control/answer/adoptionAnswer", method=RequestMethod.POST)
    public ClientRequestResult adoptionAnswer(Long answerId,
                                 HttpServletRequest request, HttpServletResponse response){
        answerClientService.adoptionAnswer(answerId);
        return ClientRequestResult.success();
    }

    /**
     * 添加答案回复表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6020800)
    public Map<String,Object> addReplyToAnswerUI(){
        return answerClientService.getAddReplyToAnswerViewModel();
    }

    /**
     * 答案回复  添加
     * @param answerId 答案回复Id
     * @param friendReplyId 对方回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020900)
    @RequestMapping(value="/user/control/answer/addAnswerReply", method=RequestMethod.POST)
    public ClientRequestResult addAnswerReply(Long answerId,Long friendReplyId,String content,
                                 String captchaKey,String captchaValue,
                                 HttpServletRequest request, HttpServletResponse response){
        answerClientService.addAnswerReply(answerId,friendReplyId,content,
                captchaKey,captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 修改答案回复表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6021000)
    public Map<String,Object> editReplyToAnswerUI(Long replyId) {
        return answerClientService.getEditReplyToAnswerViewModel(replyId);
    }

    /**
     * 答案回复  修改
     * @param replyId 回复Id
     * @param content 内容
     * @param captchaKey 验证码键
     * @param captchaValue 验证码值
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1021000)
    @RequestMapping(value="/user/control/answer/editReply", method=RequestMethod.POST)
    public ClientRequestResult editReply(Long replyId,String content,
                            String captchaKey,String captchaValue,
                            HttpServletRequest request, HttpServletResponse response){
        answerClientService.editAnswerReply(replyId,content,captchaKey,captchaValue,request);
        return ClientRequestResult.success();
    }

    /**
     * 答案回复  删除
     * @param replyId 回复Id
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1021100)
    @RequestMapping(value="/user/control/answer/deleteReply", method=RequestMethod.POST)
    public ClientRequestResult deleteReply(Long replyId,HttpServletRequest request, HttpServletResponse response){
        answerClientService.deleteAnswerReply(replyId);
        return ClientRequestResult.success();
    }

    /**
     * 用户回答总数
     * @param userName 用户名称
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6021200)
    public UserAnswerCountDTO userAnswerCount(String userName){
        return answerClientService.getUserAnswerCount(userName);
    }


    /**
     * 答案  图片上传
	 * @param questionId 问题Id
	 * @param fileName 文件名称 预签名时有值
	 * @param file 文件
     * @param request 请求信息
	 * @return
	 */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1020800)
    @RequestMapping(value="/user/control/answer/uploadImage", method=RequestMethod.POST)
    public Map<String, Object> uploadImage(Long questionId, String fileName,
                              MultipartFile file, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return answerClientService.uploadImage(questionId,fileName,file,fileServerAddress);
    }

    /**
     * 我的答案
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1021300)
    @RequestMapping(value="/user/control/answerList",method=RequestMethod.GET)
    public PageView<Answer> answerListUI(PageForm pageForm){
        return answerClientService.getAnswerList(pageForm.getPage());
    }

    /**
     * 我的答案回复
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1021400)
    @RequestMapping(value="/user/control/answerReplyList",method=RequestMethod.GET)
    public PageView<AnswerReply> answerReplyListUI(PageForm pageForm){
        return answerClientService.getAnswerReplyList(pageForm.getPage());
    }
}
