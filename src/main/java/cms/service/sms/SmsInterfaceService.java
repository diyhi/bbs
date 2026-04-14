package cms.service.sms;

import cms.dto.PageView;
import cms.dto.sms.SmsInterfaceRequest;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 短信接口服务
 */
public interface SmsInterfaceService {

    /**
     * 获取短信接口列表
     * @param page 页码
     */
    public PageView<SmsInterface> getSmsInterfaceList(int page);
    /**
     * 获取短信发送错误日志列表
     * @param page 页码
     */
    public PageView<SendSmsLog> getSendSmsLogList(int page);
    /**
     * 获取添加短信接口界面信息
     * @return
     */
    public Map<String,Object> getAddSmsInterfaceViewModel();
    /**
     * 添加短信接口
     * @param smsInterfaceRequest 短信接口表单
     * @param request 请求信息
     */
    public void addSmsInterface(SmsInterfaceRequest smsInterfaceRequest, HttpServletRequest request);
    /**
     * 获取修改短信接口界面信息
     * @param smsInterfaceId 短信接口Id
     * @return
     */
    public Map<String,Object> getEditSmsInterfaceViewModel(Integer smsInterfaceId);
    /**
     * 修改短信接口
     * @param smsInterfaceRequest 短信接口表单
     * @param request 请求信息
     */
    public void editSmsInterface(SmsInterfaceRequest smsInterfaceRequest,HttpServletRequest request);
    /**
     * 删除短信接口
     * @param smsInterfaceId 短信接口Id
     */
    public void deleteSmsInterface(Integer smsInterfaceId);
}
