package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.LikeCountDTO;
import cms.dto.frontendModule.UserLikeStatusDTO;
import cms.dto.user.ResourceEnum;
import cms.model.like.*;
import cms.service.frontend.LikeClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台点赞控制器
 */
@RestController("frontendLikeController")
public class LikeController {

    @Resource
    LikeClientService likeClientService;
    @Resource
    FileComponent fileComponent;
    /**
     * 点赞总数
     * @param itemId 项Id
     * @param module 模块 10:话题 20:评论 30:评论回复 40:问题 50:答案 60:答案回复
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6040100)
    public LikeCountDTO getTotalLikes(Long itemId, Integer module){
        return likeClientService.getTotalLikes(itemId, module);
    }


    /**
     * 用户是否已经点赞该项
     * @param itemId 项Id
     * @param module 模块 10:话题 20:评论 30:评论回复 40:问题 50:答案 60:答案回复
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6040200)
    public UserLikeStatusDTO hasUserLiked(Long itemId, Integer module){
        return likeClientService.hasUserLiked(itemId, module);
    }

    /**
     * 点赞   添加
     * @param itemId   项目Id 例如：话题Id
     * @param module   模块
     * @return
     */
    @RoleAnnotation(resourceCode = ResourceEnum._4001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1040100)
    @RequestMapping(value = "/user/control/like/add", method = RequestMethod.POST)
    public Map<String,Object> addLike(Long itemId, Integer module) {
        return likeClientService.addLike(itemId, module);
    }
    /**
     * 删除点赞
     * @param likeId  点赞Id 只用本参数或下面两个参数
     * @param module  模块  10:话题  20:评论  30:评论回复  40:问题  50:答案  60:答案回复
     * @param itemId  项Id  话题Id、评论Id、评论回复Id、问题Id、答案Id、答案回复Id
     */
    @RoleAnnotation(resourceCode = ResourceEnum._4002000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1040400)
    @RequestMapping(value = "/user/control/deleteLike", method = RequestMethod.POST)
    public Map<String,Object> deleteLike(String likeId, Integer module, Long itemId){
        return likeClientService.deleteLike(likeId,module,itemId);
    }

    /**
     * 话题点赞列表
     * @param pageForm 页码
     * @param topicId  话题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1040200)
    @RequestMapping(value = "/user/control/topicLikeList", method = RequestMethod.GET)
    public PageView<Like> topicLikeList(PageForm pageForm, Long topicId, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return likeClientService.getTopicLikeList(pageForm.getPage(),topicId,fileServerAddress);
    }

    /**
     * 点赞列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1040300)
    @RequestMapping(value = "/user/control/likeList", method = RequestMethod.GET)
    public PageView<Like> likeList(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return likeClientService.getLikeList(pageForm.getPage(),fileServerAddress);
    }


}