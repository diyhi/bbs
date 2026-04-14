package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.user.UserCustom;
import cms.service.user.UserCustomService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 用户自定义注册功能项控制器
 * @author Administrator
 *
 */
@RestController
public class UserCustomController {

    @Resource
    UserCustomService userCustomService;
    @Resource FileComponent fileComponent;


    /**
     * 用户自定义注册功能项列表
     * @return
     */
    @RequestMapping("/control/userCustom/list")
    public RequestResult userCustomList(){
        List<UserCustom> userCustomList = userCustomService.getAllUserCustom();
        return new RequestResult(ResultCode.SUCCESS, userCustomList);
    }


}
