package cms.controller.staff;


import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.staff.SysResources;
import cms.model.staff.SysRoles;
import cms.service.staff.ACLService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 访问控制列表控制器
 * @author Administrator
 *
 */
@RestController
public class ACLController {
	private static final Logger logger = LogManager.getLogger(ACLController.class);

    @Resource ACLService aclService;


    /**
     * 角色列表
     * @param pageForm 分页
     * @return
     */
    @RequestMapping("/control/roles/list")
    public RequestResult roles(PageForm pageForm){

        PageView<SysRoles> pageView = aclService.getRolesList(pageForm.getPage());
        return new RequestResult(ResultCode.SUCCESS,pageView);
    }

}
