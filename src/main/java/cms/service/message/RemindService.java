package cms.service.message;

import cms.dto.PageView;
import cms.dto.sms.SmsInterfaceRequest;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 提醒服务
 */
public interface RemindService {

    /**
     * 获取提醒列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getRemindList(int page,Long id,String fileServerAddress);
    /**
     * 还原提醒
     * @param userId 用户Id
     * @param remindId 提醒Id
     */
    public void reductionRemind(Long userId,String remindId);
}
