package cms.service.user;

import cms.dto.PageView;
import cms.dto.user.DisableUserNameRequest;
import cms.model.user.DisableUserName;

import java.util.Map;

/**
 * 禁止的用户名称服务
 */
public interface DisableUserNameService {

    /**
     * 获取禁止的用户名称列表
     *
     * @param page              页码
     */
    public PageView<DisableUserName> getDisableUserNameList(int page);

    /**
     * 添加禁止的用户名称
     * @param disableUserNameRequest 禁止的用户名称表单
     */
    public void addDisableUserName(DisableUserNameRequest disableUserNameRequest);
    /**
     * 获取修改禁止的用户名称界面信息
     * @param disableUserNameId 禁止的用户名称Id
     * @return
     */
    public Map<String,Object> getEditDisableUserNameViewModel(Integer disableUserNameId);
    /**
     * 修改禁止的用户名称
     * @param disableUserNameRequest 禁止的用户名称表单
     */
    public void editDisableUserName(DisableUserNameRequest disableUserNameRequest);
    /**
     * 删除禁止的用户名称
     * @param disableUserNameId 禁止的用户名称Id
     */
    public void deleteUDisableUserName(Integer disableUserNameId);
}