package cms.controller.follow;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.follow.FollowService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 关注控制器
 *
 */
@RestController
public class FollowController {
    @Resource
    FollowService followService;
    @Resource
    FileComponent fileComponent;
    /**
     * 关注列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/follow/list")
    public RequestResult followList(PageForm pageForm, Long id,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = followService.getFollowList(pageForm.getPage(),id, userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
    /**
     * 粉丝列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/follower/list")
    public RequestResult followerList(PageForm pageForm, Long id,String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = followService.getFollowerList(pageForm.getPage(),id, userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }
}
