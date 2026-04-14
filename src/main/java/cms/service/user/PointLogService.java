package cms.service.user;


import java.util.Map;

/**
 * 积分日志服务
 */
public interface PointLogService {

    /**
     * 获取积分日志列表
     * @param page 页码
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     */
    public Map<String, Object> getPointLogList(int page, String userName,String fileServerAddress);

    /**
     * 积分日志明细
     * @param pointLogId 积分日志Id
     * @param userName 用户名称
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public Map<String, Object> getPointLogDetail(String pointLogId,String userName,String fileServerAddress);
}