package cms.controller.user;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.payment.PaymentRequest;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.service.user.UserService;
import cms.validator.user.UserValidator;
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
import java.util.Map;

/**
 * 用户管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/user/manage")
public class UserManageController {
    @Resource
    UserService userService;
    @Resource
    FileComponent fileComponent;
    @Resource
    UserValidator userValidator;
    @Resource
    MessageSource messageSource;

    /**
     * 用户管理 查看
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params = "method=show", method = RequestMethod.GET)
    public RequestResult show(Long id, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String, Object> returnValue = userService.getUserDetails(id, fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 用户管理 添加界面显示
     */
    @RequestMapping(params = "method=add", method = RequestMethod.GET)
    public RequestResult addUI() {
        Map<String, Object> returnValue = userService.getAddUserViewModel();
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 用户管理 添加用户
     *
     * @param userForm    用户表单
     * @param userRolesId 角色Id
     * @param result      存储校验信息
     * @param request     请求信息
     * @return
     */
    @RequestMapping(params = "method=add", method = RequestMethod.POST)
    public RequestResult add(@ModelAttribute User userForm, String[] userRolesId, BindingResult result,
                             HttpServletRequest request) {
        //数据校验
        userValidator.validate(userForm, result);
        if (result.hasErrors()) {
            Map<String, Object> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        userService.addUser(userForm, userRolesId, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户管理 修改界面显示
     * @param id 用户Id
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Long id) {
        Map<String, Object> returnValue = userService.getEditUserViewModel(id);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 用户管理 修改
     * @param userForm    用户表单
     * @param userRolesId 角色Id
     * @param result      存储校验信息
     * @param request     请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute User userForm,String[] userRolesId,BindingResult result,
                       HttpServletRequest request){
        userService.editUser(userForm, userRolesId, request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户  删除
     * @param userId 用户Id集合
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Long[] userId) {
        userService.deleteUser(userId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 还原
     * @param userId 用户Id集合
     * @return
     */
    @RequestMapping(params="method=reduction",method=RequestMethod.POST)
    public RequestResult reduction(Long[] userId){
        userService.reductionUser(userId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 更新头像
     * @param file 文件
     * @param id 用户Id
     * @param width 图片宽
     * @param height 图片高
     * @param x X轴
     * @param y Y轴
     * @return
     */
    @RequestMapping(params="method=updateAvatar",method=RequestMethod.POST)
    public RequestResult updateAvatar(MultipartFile file, Long id,Integer width, Integer height, Integer x, Integer y){
        userService.updateAvatar(file, width, height ,x, y,id);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户管理 支付
     * @param paymentRequest 付款表单
     * @return
     */
    @RequestMapping(params="method=payment",method=RequestMethod.POST)
    public RequestResult payment(@ModelAttribute PaymentRequest paymentRequest, BindingResult result) {
        userService.payment(paymentRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户管理 注销账号
     * @param id 用户Id
     * @return
     */
    @RequestMapping(params="method=cancelAccount",method=RequestMethod.POST)
    public RequestResult cancelAccount(Long id) {
        userService.cancelAccount(id);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 用户管理 恢复注销的账号
     * @param id 用户Id
     * @return

     @RequestMapping(params="method=restoreAccount",method=RequestMethod.POST)
     public RequestResult restoreAccount(Long id) {
     Map<String,String> error = new HashMap<String,String>();//错误

     return new RequestResult(ResultCode.SUCCESS, null);
     }*/

    /**
     * 发表的话题
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=allTopic",method=RequestMethod.GET)
    public RequestResult allTopic(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Topic> pageView = userService.getUserTopic(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 发表的评论
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=allComment",method=RequestMethod.GET)
    public RequestResult allComment(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Comment> pageView = userService.getUserComment(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 发表的回复
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=allReply",method=RequestMethod.GET)
    public RequestResult allReply(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Reply> pageView = userService.getUserCommentReply(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }
    /**
     * 发表的问题
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=allQuestion",method=RequestMethod.GET)
    public RequestResult allQuestion(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Question> pageView = userService.getUserQuestion(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 发表的答案
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=allAnswer",method=RequestMethod.GET)
    public RequestResult allAuditAnswer(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Answer> pageView = userService.getUserAnswer(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 发表的答案回复
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=allAnswerReply",method=RequestMethod.GET)
    public RequestResult allAuditAnswerReply(PageForm pageForm,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<AnswerReply> pageView = userService.getUserAnswerReply(pageForm.getPage(),userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }



}