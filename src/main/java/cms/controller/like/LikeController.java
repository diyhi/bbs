package cms.controller.like;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.like.LikeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 点赞列表控制器
 *
 */
@RestController
public class LikeController {
    @Resource
    LikeService likeService;
    @Resource
    FileComponent fileComponent;

    /**
     * 收藏夹列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/like/list")
    public RequestResult likeList(PageForm pageForm, Long id, String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = likeService.getLikeList(pageForm.getPage(),id,userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 话题点赞列表
     * @param pageForm 页码
     * @param itemId 项目Id 例如：话题Id
     * @param topicId 话题Id
     * @param likeModule 点赞模块 10:话题 20:评论 30:回复
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/topicLike/list")
    public RequestResult topicLikeList(PageForm pageForm,Long itemId,Long topicId,Integer likeModule,HttpServletRequest request){
         String fileServerAddress = fileComponent.fileServerAddress(request);
         Map<String,Object> returnValue = likeService.getTopicLikes(pageForm.getPage(),itemId, topicId, likeModule,fileServerAddress);
         return new RequestResult(ResultCode.SUCCESS, returnValue);
     }

    /**
     * 问题点赞列表
     * @param pageForm 页码
     * @param itemId 项目Id 例如：问题Id
     * @param questionId 问题Id
     * @param likeModule 点赞模块 40:问题 50:评论 60:回复
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/questionLike/list")
    public RequestResult questionLikeList(PageForm pageForm,Long itemId,Long questionId,Integer likeModule,HttpServletRequest request){
         String fileServerAddress = fileComponent.fileServerAddress(request);
         Map<String,Object> returnValue  = likeService.getQuestionLikes(pageForm.getPage(),itemId,questionId, likeModule,fileServerAddress);
         return new RequestResult(ResultCode.SUCCESS, returnValue);
     }
}
