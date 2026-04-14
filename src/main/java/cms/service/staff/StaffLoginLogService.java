package cms.service.staff;

import cms.model.staff.SysUsers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 员工登录日志服务
 */
public interface StaffLoginLogService {

    /**
     * 获取员工登录日志列表
     * @param userId 员工Id
     * @param page 页码
     * @return
     */
    public Map<String, Object> getStaffLoginLogList(String userId, int page);


}
