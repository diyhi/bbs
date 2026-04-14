package cms.service.payment;

import cms.dto.PageView;
import cms.dto.payment.OnlinePaymentInterfaceRequest;
import cms.model.payment.OnlinePaymentInterface;

import java.util.Map;

/**
 * 在线支付接口服务
 */
public interface OnlinePaymentInterfaceService {

    /**
     * 获取在线支付接口列表
     * @param page 页码
     */
    public PageView<OnlinePaymentInterface> getOnlinePaymentInterfaceList(int page);
    /**
     * 获取添加在线支付接口界面信息
     * @return
     */
    public Map<String,Object> getAddOnlinePaymentInterfaceViewModel();
    /**
     * 添加在线支付接口
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     */
    public void addOnlinePaymentInterface(OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest);
    /**
     * 获取修改在线支付接口界面信息
     * @param onlinePaymentInterfaceId 在线支付接口Id
     * @return
     */
    public Map<String,Object> getEditOnlinePaymentInterfaceViewModel(Integer onlinePaymentInterfaceId);
    /**
     * 修改在线支付接口
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     */
    public void editOnlinePaymentInterface(OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest);
    /**
     * 删除在线支付接口
     * @param onlinePaymentInterfaceId 在线支付接口Id
     */
    public void deleteOnlinePaymentInterface(Integer onlinePaymentInterfaceId);
}
