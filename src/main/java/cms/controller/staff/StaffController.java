package cms.controller.staff;


import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.staff.StaffService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 员工列表控制器
 * @author Administrator
 *
 */
@RestController
public class StaffController {
	private static final Logger logger = LogManager.getLogger(StaffController.class);

    @Resource FileComponent fileComponent;
    @Resource StaffService staffService;

	/**
	 * 员工列表
     * @param pageForm 分页
	 * @param request 请求信息
	 * @return
	 */
    @RequestMapping("/control/staff/list")
    public RequestResult staffList(PageForm pageForm,
                                   HttpServletRequest request){

        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = staffService.getStaffList(pageForm.getPage(),fileServerAddress);

        return new RequestResult(ResultCode.SUCCESS,returnValue);
	}
	

}
