package cms.controller.user;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.user.DisableUserName;
import cms.service.user.DisableUserNameService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 禁止的用户名称控制器
 * @author Administrator
 *
 */
@RestController
public class DisableUserNameController {
	private static final Logger logger = LogManager.getLogger(DisableUserNameController.class);

    @Resource
    DisableUserNameService disableUserNameService;
    @Resource FileComponent fileComponent;


    /**
     * 禁止的用户名称
     * @param pageForm 页码
     * @return
     */
    @RequestMapping("/control/disableUserName/list")
    public RequestResult disableUserNameList(PageForm pageForm){
        PageView<DisableUserName> pageView = disableUserNameService.getDisableUserNameList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS, pageView);
    }



}
