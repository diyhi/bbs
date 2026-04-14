package cms.service.thirdParty;

import cms.dto.PageView;
import cms.dto.thirdParty.ThirdPartyLoginInterfaceRequest;
import cms.model.sms.SmsInterface;
import cms.model.thirdParty.ThirdPartyLoginInterface;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 第三方登录接口服务
 */
public interface ThirdPartyLoginInterfaceService {

    /**
     * 获取第三方登录接口列表
     * @param page 页码
     */
    public PageView<ThirdPartyLoginInterface> getThirdPartyLoginInterfaceList(int page);

    /**
     * 获取添加第三方登录接口界面信息
     * @return
     */
    public Map<String,Object> getAddThirdPartyLoginInterfaceViewModel();
    /**
     * 添加第三方登录接口
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     */
    public void addThirdPartyLoginInterface(ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest);
    /**
     * 获取修改第三方登录接口界面信息
     * @param thirdPartyLoginInterfaceId 第三方登录接口Id
     * @return
     */
    public Map<String,Object> getEditThirdPartyLoginInterfaceViewModel(Integer thirdPartyLoginInterfaceId);
    /**
     * 修改第三方登录接口
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     */
    public void editThirdPartyLoginInterface(ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest);
    /**
     * 删除第三方登录接口
     * @param thirdPartyLoginInterfaceId 第三方登录接口Id
     */
    public void deleteThirdPartyLoginInterface(Integer thirdPartyLoginInterfaceId);
}
