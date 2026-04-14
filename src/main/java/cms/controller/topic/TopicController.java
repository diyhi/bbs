package cms.controller.topic;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import cms.service.payment.PaymentLogService;
import cms.service.topic.TopicService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 话题列表控制器
 * @author Administrator
 *
 */
@RestController
public class TopicController {

    @Resource
    TopicService topicService;
    @Resource
    FileComponent fileComponent;

    /**
     * 话题列表
     * @param pageForm 分页
     * @param visible 是否可见
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topic/list")
    public RequestResult topicList(PageForm pageForm, Boolean visible, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Topic> pageView = topicService.getTopicList(pageForm.getPage(), visible,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 搜索话题
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
    @RequestMapping("/control/topic/search")
    public RequestResult search(PageForm pageForm, Integer dataSource,String keyword,String tagId,String tagName,String account,
                                   String start_postTime,String end_postTime, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Topic> pageView = topicService.getSearch(pageForm.getPage(), dataSource, keyword, tagId, tagName, account,
                 start_postTime, end_postTime,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 全部待审核话题
     * @param pageForm 分页
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topic/allAuditTopic")
    public RequestResult allAuditTopic(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Topic> pageView = topicService.getPendingTopics(pageForm.getPage(), fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 全部待审核评论
     * @param pageForm 分页
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topic/allAuditComment")
    public RequestResult allAuditComment(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Comment> pageView = topicService.getAllPendingComments(pageForm.getPage(), fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 全部待审核回复
     * @param pageForm 分页
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topic/allAuditReply")
    public RequestResult allAuditReply(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<Reply> pageView = topicService.getAllPendingReplies(pageForm.getPage(), fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 话题取消隐藏列表
     * @param pageForm 分页
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topic/topicUnhideList")
    public RequestResult topicUnhideList(PageForm pageForm, Long topicId,HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = topicService.getUnhiddenTopics(pageForm.getPage(), topicId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

}
