package cms.controller.frontend;


import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.FollowStatusDTO;
import cms.dto.frontendModule.FollowerCountDTO;
import cms.dto.frontendModule.FollowingCountDTO;
import cms.dto.user.ResourceEnum;
import cms.model.follow.Follow;
import cms.model.follow.Follower;
import cms.service.frontend.FollowClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台关注控制器
 */
@RestController("frontendFollowController")
public class FollowController {

    @Resource
    FollowClientService followClientService;
    @Resource
    FileComponent fileComponent;

    /**
     * 关注总数
     * @param userName 用户名称
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6050100)
    public FollowingCountDTO followingCount(String userName){
        return followClientService.getFollowingCount(userName);
    }

    /**
     * 粉丝总数
     * @param userName 用户名称
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6050200)
    public FollowerCountDTO followerCount(String userName){
        return followClientService.getFollowerCount(userName);
    }

    /**
     * 是否已经关注该用户
     * @param userName 用户名称
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6050300)
    public FollowStatusDTO isFollowing(String userName){
        return followClientService.isFollowing(userName);
    }


    /**
     * 关注   添加
     * @param userName 用户名称
     * @return
     */
    @RoleAnnotation(resourceCode= ResourceEnum._5001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1050100)
    @RequestMapping(value="/user/control/follow/add", method= RequestMethod.POST)
    public Map<String,Object> add(String userName){
        return followClientService.addFollow(userName);
    }

    /**
     * 关注列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1050200)
    @RequestMapping(value="/user/control/followList",method=RequestMethod.GET)
    public PageView<Follow> followList(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return followClientService.getFollowList(pageForm.getPage(),fileServerAddress);
    }

    /**
     * 粉丝列表
     * @param pageForm 页码
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1050300)
    @RequestMapping(value="/user/control/followerList",method=RequestMethod.GET)
    public PageView<Follower> followerList(PageForm pageForm, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return followClientService.getFollowerList(pageForm.getPage(),fileServerAddress);
    }

    /**
     * 删除关注
     * @param followId 关注Id
     * @return
     */
    @RoleAnnotation(resourceCode=ResourceEnum._5002000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1050400)
    @RequestMapping(value="/user/control/deleteFollow", method=RequestMethod.POST)
    public Map<String,Object> deleteFollow(String followId){
        return followClientService.deleteFollow(followId);
    }
}
