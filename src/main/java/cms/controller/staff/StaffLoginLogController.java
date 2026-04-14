package cms.controller.staff;


import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.staff.StaffLoginLogService;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


/**
 * 员工登录日志列表控制器
 * @author Administrator
 *
 */
@RestController
public class StaffLoginLogController {
	private static final Logger logger = LogManager.getLogger(StaffLoginLogController.class);

    @Resource StaffLoginLogService staffLoginLogService;

    /**
     * 员工登录日志列表
     * @param userId 员工Id
     * @param pageForm 分页
     * @return
     */
    @RequestMapping("/control/staffLoginLog/list")
    public RequestResult staffLoginLogList(String userId,PageForm pageForm){
        Map<String, Object> returnValue = staffLoginLogService.getStaffLoginLogList(userId,pageForm.getPage());

        return new RequestResult(ResultCode.SUCCESS,returnValue);
	}
	

}
