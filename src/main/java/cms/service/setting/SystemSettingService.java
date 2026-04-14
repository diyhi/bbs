package cms.service.setting;


import cms.model.setting.SystemSetting;
import java.util.Map;

/**
 * 系统设置服务
 */
public interface SystemSettingService {

    /**
     * 获取修改系统设置界面信息
     * @return
     */
    public Map<String, Object> getEditSystemSettingViewModel();
    /**
     * 修改系统设置
     * @param systemSettingForm 系统设置表单
     */
    public void editSystemSetting(SystemSetting systemSettingForm);
    /**
     * 重建话题全文索引
     */
    public void rebuildTopicIndex();
    /**
     * 重建问题全文索引
     */
    public void rebuildQuestionIndex();

    /**
     * 删除浏览量数据
     * @param beforeTime 删除指定时间之前的数据
     */
    public void deletePageViewData(String beforeTime);
    /**
     * 删除用户登录日志数据
     * @param beforeTime 删除指定时间之前的数据
     */
    public void deleteUserLoginLogData(String beforeTime);
    /**
     * 获取节点参数界面信息
     */
    public Map<String,Object> getNodeParameterViewModel();
}