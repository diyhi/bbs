package cms.controller.user;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.user.User;
import cms.service.user.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.Map;

/**
 * 用户列表控制器
 * @author Administrator
 *
 */
@RestController
public class UserController {
    @Resource
    UserService userService;
    @Resource
    FileComponent fileComponent;
    /**
	 * 用户列表
	 * @param pageForm 页码
	 * @param visible  null或true:正常页面  false:回收站
     * @param request 请求信息
	 * @return
	 */
    @RequestMapping("/control/user/list")
    public RequestResult userList(PageForm pageForm, Boolean visible, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        PageView<User> pageView = userService.getUserList(pageForm.getPage(),visible,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }

    /**
     * 搜索用户列表
     * @param pageForm 页码
     * @param searchType 查询类型
     * @param userTypeCode 查询用户类型
     * @param keyword 关键词
     * @param start_point 起始积分
     * @param end_point 结束积分
     * @param start_registrationDate 起始注册日期
     * @param end_registrationDate 结束注册日期
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/user/search")
    public RequestResult search(PageForm pageForm,
                         Integer searchType,Integer userTypeCode,String keyword,
                         String start_point,String end_point,
                         String start_registrationDate,String end_registrationDate,
                         HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String, Object> returnValue = userService.searchUset(pageForm.getPage(), searchType, userTypeCode, keyword,
                start_point, end_point,
                start_registrationDate, end_registrationDate,fileServerAddress, request);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 根据账号或呢称查询用户
     * @param keyword 关键字
     * @return
     */
    @RequestMapping("/control/user/queryUser")
    public RequestResult queryUser(String keyword,HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        User user = userService.queryUser(keyword, fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, user);
    }
}
