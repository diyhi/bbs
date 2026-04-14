package cms.service.payment.impl;


import cms.component.JsonComponent;
import cms.component.payment.OnlinePaymentInterfaceComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.payment.OnlinePaymentInterfaceRequest;
import cms.model.payment.Alipay;
import cms.model.payment.OnlinePaymentInterface;
import cms.repository.payment.PaymentRepository;
import cms.repository.setting.SettingRepository;
import cms.service.payment.OnlinePaymentInterfaceService;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 在线支付接口服务
 */
@Service
public class OnlinePaymentInterfaceServiceImpl implements OnlinePaymentInterfaceService {

    @Resource
    PaymentRepository paymentRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    OnlinePaymentInterfaceComponent onlinePaymentInterfaceComponent;
    @Resource
    JsonComponent jsonComponent;

    private final Map<Integer,String> paymentInterfaceProductParameter = ImmutableMap.of(1, "支付宝即时到账", 4, "支付宝手机网站(alipay.trade.wap.pay)");//支付接口产品

    /**
     * 获取在线支付接口列表
     * @param page 页码
     */
    public PageView<OnlinePaymentInterface> getOnlinePaymentInterfaceList(int page){
        //调用分页算法代码
        PageView<OnlinePaymentInterface> pageView = new PageView<OnlinePaymentInterface>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据sort字段降序排序
        QueryResult<OnlinePaymentInterface> qr = paymentRepository.getScrollData(OnlinePaymentInterface.class,firstindex, pageView.getMaxresult(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取添加在线支付接口界面信息
     * @return
     */
    public Map<String,Object> getAddOnlinePaymentInterfaceViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        //可添加的接口产品
        LinkedHashMap<Integer,String> paymentInterfaceProductMap = new LinkedHashMap<Integer,String>();
        paymentInterfaceProductMap.putAll(paymentInterfaceProductParameter);

        List<OnlinePaymentInterface> onlinePaymentInterfaceList =  paymentRepository.findAllOnlinePaymentInterface();
        if(onlinePaymentInterfaceList != null && onlinePaymentInterfaceList.size() >0){

            for(OnlinePaymentInterface paymentInterface : onlinePaymentInterfaceList){
                if(paymentInterfaceProductMap.containsKey(paymentInterface.getInterfaceProduct())){
                    paymentInterfaceProductMap.remove(paymentInterface.getInterfaceProduct());
                }
            }
        }
        returnValue.put("paymentInterfaceProductMap",paymentInterfaceProductMap);
        return returnValue;
    }

    /**
     * 添加在线支付接口
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     */
    public void addOnlinePaymentInterface(OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest){
        // 接口产品
        Integer interfaceProduct = onlinePaymentInterfaceRequest.getInterfaceProduct();

        // 检查产品Id是否有效
        if (interfaceProduct == null || !paymentInterfaceProductParameter.containsKey(interfaceProduct)) {
            throw new BusinessException(Map.of("interfaceProduct", "接口产品参数错误或不存在"));
        }

        List<OnlinePaymentInterface> onlinePaymentInterfaceList =  paymentRepository.findAllOnlinePaymentInterface();
        if(onlinePaymentInterfaceList != null && !onlinePaymentInterfaceList.isEmpty()){
            for(OnlinePaymentInterface paymentInterface : onlinePaymentInterfaceList){
                if(paymentInterface.getInterfaceProduct().equals(interfaceProduct)){
                    throw new BusinessException(Map.of("interfaceProduct", "该接口产品已配置，请勿重复添加"));
                }
            }
        }

        Map<String, String> errors = new HashMap<String, String>();


        OnlinePaymentInterface onlinePaymentInterface = new OnlinePaymentInterface();
        if (interfaceProduct.equals(1)) { // 支付宝即时到账
            validateAlipayDirectConfig(onlinePaymentInterfaceRequest, errors);

        } else if (interfaceProduct.equals(4)) { // 支付宝手机网站
            validateAlipayMobileConfig(onlinePaymentInterfaceRequest, errors);

        } else {
            errors.put("interfaceProduct", "不支持的接口产品类型");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        if (interfaceProduct.equals(1)) { // 支付宝即时到账
            Alipay alipay_direct = new Alipay();
            alipay_direct.setApp_id(onlinePaymentInterfaceRequest.getDirect_app_id());
            alipay_direct.setRsa_private_key(onlinePaymentInterfaceRequest.getDirect_rsa_private_key());
            alipay_direct.setAlipay_public_key(onlinePaymentInterfaceRequest.getDirect_alipay_public_key());

            onlinePaymentInterface.setDynamicParameter(jsonComponent.toJSONString(alipay_direct));
            onlinePaymentInterface.setSupportEquipment(onlinePaymentInterfaceComponent.setSupportEquipment(true, false, false));

        }else if(interfaceProduct.equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
            Alipay alipay_mobile = new Alipay();
            alipay_mobile.setApp_id(onlinePaymentInterfaceRequest.getMobile_app_id());
            alipay_mobile.setRsa_private_key(onlinePaymentInterfaceRequest.getMobile_rsa_private_key());
            alipay_mobile.setAlipay_public_key(onlinePaymentInterfaceRequest.getMobile_alipay_public_key());
            onlinePaymentInterface.setDynamicParameter(jsonComponent.toJSONString(alipay_mobile));
            onlinePaymentInterface.setSupportEquipment(onlinePaymentInterfaceComponent.setSupportEquipment(false, true, false));
        }

        onlinePaymentInterface.setName(onlinePaymentInterfaceRequest.getName());
        onlinePaymentInterface.setInterfaceProduct(onlinePaymentInterfaceRequest.getInterfaceProduct());
        onlinePaymentInterface.setEnable(onlinePaymentInterfaceRequest.isEnable());
        onlinePaymentInterface.setSort(onlinePaymentInterfaceRequest.getSort());

        paymentRepository.saveOnlinePaymentInterface(onlinePaymentInterface);

    }


    /**
     * 获取修改在线支付接口界面信息
     * @param onlinePaymentInterfaceId 在线支付接口Id
     * @return
     */
    public Map<String,Object> getEditOnlinePaymentInterfaceViewModel(Integer onlinePaymentInterfaceId){
        if(onlinePaymentInterfaceId == null || onlinePaymentInterfaceId <=0){
            throw new BusinessException(Map.of("onlinePaymentInterfaceId", "在线支付接口Id不能为空"));
        }
        //根据Id查询要修改的数据
        OnlinePaymentInterface paymentInterface = paymentRepository.findOnlinePaymentInterfaceById(onlinePaymentInterfaceId);

        if(paymentInterface == null){
            throw new BusinessException(Map.of("onlinePaymentInterfaceId", "在线支付接口不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        returnValue.put("onlinePaymentInterface", paymentInterface);

        if(paymentInterface.getDynamicParameter() != null && !paymentInterface.getDynamicParameter().trim().isEmpty()){
            if(paymentInterface.getInterfaceProduct().equals(1)){//支付宝即时到账
                Alipay alipay_direct = jsonComponent.toObject(paymentInterface.getDynamicParameter(), Alipay.class);
                returnValue.put("alipayDirect", alipay_direct);

            }else if(paymentInterface.getInterfaceProduct().equals(4)){//支付宝手机网站(alipay.trade.wap.pay接口)
                Alipay alipay_bank = jsonComponent.toObject(paymentInterface.getDynamicParameter(), Alipay.class);
                returnValue.put("alipayMobile", alipay_bank);
            }

        }
        return returnValue;
    }
    /**
     * 修改在线支付接口
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     */
    public void editOnlinePaymentInterface(OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest){

        if(onlinePaymentInterfaceRequest.getOnlinePaymentInterfaceId() == null || onlinePaymentInterfaceRequest.getOnlinePaymentInterfaceId() <=0){
            throw new BusinessException(Map.of("onlinePaymentInterfaceId", "在线支付接口Id不能为空"));
        }
        OnlinePaymentInterface oldOnlinePaymentInterface = paymentRepository.findOnlinePaymentInterfaceById(onlinePaymentInterfaceRequest.getOnlinePaymentInterfaceId());
        if(oldOnlinePaymentInterface == null){
            throw new BusinessException(Map.of("onlinePaymentInterfaceId", "在线支付接口不存在"));
        }

        Map<String, String> errors = new HashMap<String, String>();


        OnlinePaymentInterface onlinePaymentInterface = new OnlinePaymentInterface();
        if (oldOnlinePaymentInterface.getInterfaceProduct().equals(1)) { // 支付宝即时到账
            validateAlipayDirectConfig(onlinePaymentInterfaceRequest, errors);

        } else if (oldOnlinePaymentInterface.getInterfaceProduct().equals(4)) { // 支付宝手机网站
            validateAlipayMobileConfig(onlinePaymentInterfaceRequest, errors);

        } else {
            errors.put("interfaceProduct", "不支持的接口产品类型");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        if (oldOnlinePaymentInterface.getInterfaceProduct().equals(1)) { // 支付宝即时到账
            Alipay alipay_direct = new Alipay();//支付宝即时到账
            alipay_direct.setApp_id(onlinePaymentInterfaceRequest.getDirect_app_id());
            alipay_direct.setRsa_private_key(onlinePaymentInterfaceRequest.getDirect_rsa_private_key());
            alipay_direct.setAlipay_public_key(onlinePaymentInterfaceRequest.getDirect_alipay_public_key());
            onlinePaymentInterface.setDynamicParameter(jsonComponent.toJSONString(alipay_direct));

        }else if(oldOnlinePaymentInterface.getInterfaceProduct().equals(4)){//4.支付宝手机网站(alipay.trade.wap.pay接口)
            Alipay alipay_mobile = new Alipay();//支付宝手机网站(alipay.trade.wap.pay接口)
            alipay_mobile.setApp_id(onlinePaymentInterfaceRequest.getMobile_app_id());
            alipay_mobile.setRsa_private_key(onlinePaymentInterfaceRequest.getMobile_rsa_private_key());
            alipay_mobile.setAlipay_public_key(onlinePaymentInterfaceRequest.getMobile_alipay_public_key());
            onlinePaymentInterface.setDynamicParameter(jsonComponent.toJSONString(alipay_mobile));
        }

        onlinePaymentInterface.setId(oldOnlinePaymentInterface.getId());
        onlinePaymentInterface.setName(onlinePaymentInterfaceRequest.getName());
        onlinePaymentInterface.setInterfaceProduct(oldOnlinePaymentInterface.getInterfaceProduct());
        onlinePaymentInterface.setEnable(onlinePaymentInterfaceRequest.isEnable());
        onlinePaymentInterface.setSort(onlinePaymentInterfaceRequest.getSort());
        onlinePaymentInterface.setSupportEquipment(oldOnlinePaymentInterface.getSupportEquipment());
        onlinePaymentInterface.setVersion(ThreadLocalRandom.current().nextInt(10000, 100000));//5位随机数

        paymentRepository.updateOnlinePaymentInterface(onlinePaymentInterface);
    }

    /**
     * 删除在线支付接口
     * @param onlinePaymentInterfaceId 在线支付接口Id
     */
    public void deleteOnlinePaymentInterface(Integer onlinePaymentInterfaceId){
        if(onlinePaymentInterfaceId == null || onlinePaymentInterfaceId <=0){
            throw new BusinessException(Map.of("onlinePaymentInterfaceId", "在线支付接口Id不能为空"));
        }
        paymentRepository.deleteOnlinePaymentInterface(onlinePaymentInterfaceId);
    }

    /**
     * 校验支付宝即时到账配置
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     * @param errors 错误集合
     * @return
     */
    private void validateAlipayDirectConfig(OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest, Map<String, String> errors) {
        if(onlinePaymentInterfaceRequest.getDirect_app_id() == null || onlinePaymentInterfaceRequest.getDirect_app_id().trim().isEmpty()){
            errors.put("direct_app_id", "APPID不能为空");
        }
        if(onlinePaymentInterfaceRequest.getDirect_rsa_private_key() == null || onlinePaymentInterfaceRequest.getDirect_rsa_private_key().trim().isEmpty()){
            errors.put("direct_rsa_private_key", "商户的私钥不能为空");
        }
        if(onlinePaymentInterfaceRequest.getDirect_alipay_public_key() == null || onlinePaymentInterfaceRequest.getDirect_alipay_public_key().trim().isEmpty()){
            errors.put("direct_alipay_public_key", "支付宝公钥不能为空");
        }
    }


    /**
     * 校验支付宝手机网站配置
     * @param onlinePaymentInterfaceRequest 在线支付接口表单
     * @param errors 错误集合
     * @return
     */
    private void validateAlipayMobileConfig(OnlinePaymentInterfaceRequest onlinePaymentInterfaceRequest, Map<String, String> errors) {
        if(onlinePaymentInterfaceRequest.getMobile_app_id() == null || onlinePaymentInterfaceRequest.getMobile_app_id().trim().isEmpty()){
            errors.put("mobile_app_id", "APPID不能为空");
        }

        if(onlinePaymentInterfaceRequest.getMobile_rsa_private_key() == null || onlinePaymentInterfaceRequest.getMobile_rsa_private_key().trim().isEmpty()){
            errors.put("mobile_rsa_private_key", "商户的私钥不能为空");
        }

        if(onlinePaymentInterfaceRequest.getMobile_alipay_public_key() == null || onlinePaymentInterfaceRequest.getMobile_alipay_public_key().trim().isEmpty()){
            errors.put("mobile_alipay_public_key", "支付宝公钥不能为空");
        }
    }
}
