package cms.controller.topic;

import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.topic.TopicRequest;
import cms.model.user.User;
import cms.service.topic.TopicService;
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
 * 话题管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/topic/manage")
public class TopicManageController {
    @Resource
    TopicService topicService;
    @Resource
    FileComponent fileComponent;

    /**
     * 话题   查看
     * @param topicId 话题Id
     * @param commentId 评论Id
     * @param page 页码
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=view",method=RequestMethod.GET)
    public RequestResult view(Long topicId,Long commentId,Integer page,
                       HttpServletRequest request) {
        Map<String,Object> returnValue = topicService.getTopicDetail(topicId,commentId,page,request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 话题   添加界面显示
     */
    @RequestMapping(params="method=add",method=RequestMethod.GET)
    public RequestResult addUI() {
        Map<String,Object> returnValue = topicService.getAddTopicViewModel();

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 话题  添加
     * @param topicRequest 话题表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add", method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute TopicRequest topicRequest, BindingResult result,
                             HttpServletRequest request) {
        topicService.addTopic(topicRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     *  文件上传
     *
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param dir 上传类型，分别为image、flash、media、file
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=upload",method=RequestMethod.POST)
    public Map<String,Object> upload(String dir, String userName, Boolean isStaff, String fileName,
                         MultipartFile file, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return topicService.uploadFile(dir, userName, isStaff,fileName, fileServerAddress, file);
    }

    /**
     * 话题   修改界面显示
     * @param topicId 话题Id
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.GET)
    public RequestResult editUI(Long topicId) {
        Map<String,Object> returnValue = topicService.getEditTopicViewModel(topicId);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 话题   修改
     * @param topicRequest 话题表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit", method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute TopicRequest topicRequest, BindingResult result,
                              HttpServletRequest request) {
        topicService.editTopic(topicRequest,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 话题   删除
     * @param topicId 话题Id集合
     * @return
     */
    @RequestMapping(params="method=delete", method=RequestMethod.POST)
    public RequestResult delete(Long[] topicId) {
        topicService.deleteTopic(topicId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 还原
     * @param topicId 话题Id集合
     * @return
     */
    @RequestMapping(params="method=reduction",method=RequestMethod.POST)
    public RequestResult reduction(Long[] topicId) {
        topicService.reductionTopic(topicId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 审核话题
     * @param topicId 话题Id
     * @return
     */
    @RequestMapping(params="method=auditTopic",method=RequestMethod.POST)
    public RequestResult auditTopic(Long topicId) {
        topicService.auditTopic(topicId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }




}
