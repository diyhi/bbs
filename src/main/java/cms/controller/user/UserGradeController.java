package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.user.UserGrade;
import cms.service.user.UserGradeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户等级控制器
 * @author Administrator
 *
 */
@RestController
public class UserGradeController {

    @Resource
    UserGradeService userGradeService;
    @Resource FileComponent fileComponent;


    /**
     * 用户等级列表
     * @return
     */
    @RequestMapping("/control/userGrade/list")
    public RequestResult userGradeList(){
        List<UserGrade> userGradeList = userGradeService.getAllUserGrade();
        return new RequestResult(ResultCode.SUCCESS, userGradeList);
    }



}
