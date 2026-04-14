package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.model.user.UserDynamic;
import cms.service.frontend.HomeClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台用户中心控制器
 */
@RestController
public class HomeController {
    @Resource
    HomeClientService homeClientService;


    /**
     * 用户中心页
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1200100)
    @RequestMapping(value="/user/control/home",method= RequestMethod.GET)
    public Map<String,Object> homeUI(String userName,
                                     HttpServletRequest request){
        return homeClientService.getUserInfo(userName,request);
    }

    /**
     * 用户动态列表
     * @param pageForm 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1200200)
    @RequestMapping(value="/user/control/userDynamicList",method=RequestMethod.GET)
    public PageView<UserDynamic> userDynamicList(PageForm pageForm, String userName,HttpServletRequest request){
        return homeClientService.getUserDynamicList(userName,pageForm.getPage(),request);
    }
}
