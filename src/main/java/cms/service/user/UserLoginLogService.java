package cms.service.user;

import java.util.Map;

/**
 * 用户登录日志服务
 */
public interface UserLoginLogService {

    /**
     * 获取用户登录日志列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getUserLoginLogList(int page,Long id, String fileServerAddress);
}
