package cms.service.thirdParty.impl;


import cms.component.JsonComponent;
import cms.component.sms.SmsComponent;
import cms.component.thirdParty.ThirdPartyComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.sms.SmsInterfaceRequest;
import cms.dto.thirdParty.OtherConfig;
import cms.dto.thirdParty.ThirdPartyLoginInterfaceRequest;
import cms.dto.thirdParty.WeChatConfig;
import cms.model.sms.Alidayu;
import cms.model.sms.SendService;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import cms.model.thirdParty.ThirdPartyLoginInterface;
import cms.repository.setting.SettingRepository;
import cms.repository.sms.SmsRepository;
import cms.repository.thirdParty.ThirdPartyLoginRepository;
import cms.service.sms.SmsInterfaceService;
import cms.service.thirdParty.ThirdPartyLoginInterfaceService;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 第三方登录接口服务
 */
@Service
public class ThirdPartyLoginInterfaceServiceImpl implements ThirdPartyLoginInterfaceService {

    @Resource
    ThirdPartyLoginRepository thirdPartyLoginRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    ThirdPartyComponent thirdPartyComponent;
    @Resource
    JsonComponent jsonComponent;

    private final Map<Integer,String> interfaceProductParameter = ImmutableMap.of(10, "微信");//接口产品
    /**
     * 获取第三方登录接口列表
     * @param page 页码
     */
    public PageView<ThirdPartyLoginInterface> getThirdPartyLoginInterfaceList(int page){
        //调用分页算法代码
        PageView<ThirdPartyLoginInterface> pageView = new PageView<ThirdPartyLoginInterface>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据sort字段降序排序
        QueryResult<ThirdPartyLoginInterface> qr = thirdPartyLoginRepository.getScrollData(ThirdPartyLoginInterface.class,firstindex, pageView.getMaxresult(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取添加第三方登录接口界面信息
     * @return
     */
    public Map<String,Object> getAddThirdPartyLoginInterfaceViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //可添加的接口产品
        LinkedHashMap<Integer,String> interfaceProductMap = new LinkedHashMap<Integer,String>();
        interfaceProductMap.putAll(interfaceProductParameter);

        List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList =  thirdPartyLoginRepository.findAllThirdPartyLoginInterface();
        if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){

            for(ThirdPartyLoginInterface loginInterface : thirdPartyLoginInterfaceList){
                if(interfaceProductMap.containsKey(loginInterface.getInterfaceProduct())){
                    interfaceProductMap.remove(loginInterface.getInterfaceProduct());
                }
            }
        }
        returnValue.put("interfaceProductMap",interfaceProductMap);
        return returnValue;
    }

    /**
     * 添加第三方登录接口
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     */
    public void addThirdPartyLoginInterface(ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest){

        //可添加的接口产品
        List<Integer> interfaceProductList = new ArrayList<Integer>();
        interfaceProductList.addAll(interfaceProductParameter.keySet());



        List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList =  thirdPartyLoginRepository.findAllThirdPartyLoginInterface();
        if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){

            for(ThirdPartyLoginInterface loginInterface : thirdPartyLoginInterfaceList){
                if(interfaceProductList.contains(loginInterface.getInterfaceProduct())){
                    interfaceProductList.remove(loginInterface.getInterfaceProduct());
                }
            }
        }
        if(thirdPartyLoginInterfaceRequest.getInterfaceProduct() == null ||!interfaceProductParameter.containsKey(thirdPartyLoginInterfaceRequest.getInterfaceProduct())){
            throw new BusinessException(Map.of("interfaceProduct", "接口产品参数错误"));
        }
        if(!interfaceProductList.contains(thirdPartyLoginInterfaceRequest.getInterfaceProduct())){
            throw new BusinessException(Map.of("interfaceProduct", "请选择接口产品"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        validateInput(thirdPartyLoginInterfaceRequest,errors);



        WeChatConfig weChatConfig = new WeChatConfig();//微信配置信息

        OtherConfig otherConfig = new OtherConfig();//其他开放平台

        String dynamicParameter = "";
        ThirdPartyLoginInterface thirdPartyLoginInterface = new ThirdPartyLoginInterface();

        if (thirdPartyLoginInterfaceRequest.getInterfaceProduct().equals(10)){//10.微信配置信息
            weChatConfig.setOp_appID(thirdPartyLoginInterfaceRequest.getWeixin_op_appID().trim());
            weChatConfig.setOp_appSecret(thirdPartyLoginInterfaceRequest.getWeixin_op_appSecret().trim());
            weChatConfig.setOa_appID(thirdPartyLoginInterfaceRequest.getWeixin_oa_appID().trim());
            weChatConfig.setOa_appSecret(thirdPartyLoginInterfaceRequest.getWeixin_oa_appSecret().trim());
            dynamicParameter = jsonComponent.toJSONString(weChatConfig);
            thirdPartyLoginInterface.setSupportEquipment(thirdPartyComponent.setSupportEquipment(true, false, false,true));
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        thirdPartyLoginInterface.setName(thirdPartyLoginInterfaceRequest.getName());
        thirdPartyLoginInterface.setInterfaceProduct(thirdPartyLoginInterfaceRequest.getInterfaceProduct());
        thirdPartyLoginInterface.setEnable(thirdPartyLoginInterfaceRequest.isEnable());
        thirdPartyLoginInterface.setDynamicParameter(dynamicParameter);

        thirdPartyLoginInterface.setSort(thirdPartyLoginInterfaceRequest.getSort());


        thirdPartyLoginRepository.saveThirdPartyLoginInterface(thirdPartyLoginInterface);
    }


    /**
     * 获取修改第三方登录接口界面信息
     * @param thirdPartyLoginInterfaceId 第三方登录接口Id
     * @return
     */
    public Map<String,Object> getEditThirdPartyLoginInterfaceViewModel(Integer thirdPartyLoginInterfaceId){
        if(thirdPartyLoginInterfaceId == null || thirdPartyLoginInterfaceId <=0){
            throw new BusinessException(Map.of("thirdPartyLoginInterfaceId", "第三方登录接口Id不能为空"));
        }
        ThirdPartyLoginInterface loginInterface = thirdPartyLoginRepository.findThirdPartyLoginInterfaceById(thirdPartyLoginInterfaceId);

        if(loginInterface == null){
            throw new BusinessException(Map.of("thirdPartyLoginInterfaceId", "第三方登录接口不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        returnValue.put("thirdPartyLoginInterface", loginInterface);

        if(loginInterface.getDynamicParameter() != null && !loginInterface.getDynamicParameter().trim().isEmpty()){
            if(loginInterface.getInterfaceProduct().equals(10)){//微信公众号
                WeChatConfig weChatConfig = jsonComponent.toObject(loginInterface.getDynamicParameter(), WeChatConfig.class);
                returnValue.put("weChatConfig",weChatConfig);

            }

        }

        return returnValue;
    }
    /**
     * 修改第三方登录接口
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     */
    public void editThirdPartyLoginInterface(ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest){
        if(thirdPartyLoginInterfaceRequest.getThirdPartyLoginInterfaceId() == null || thirdPartyLoginInterfaceRequest.getThirdPartyLoginInterfaceId() <=0){
            throw new BusinessException(Map.of("thirdPartyLoginInterfaceId", "第三方登录接口Id不能为空"));
        }
        ThirdPartyLoginInterface thirdPartyLoginInterface = thirdPartyLoginRepository.findThirdPartyLoginInterfaceById(thirdPartyLoginInterfaceRequest.getThirdPartyLoginInterfaceId());

        if(thirdPartyLoginInterface == null){
            throw new BusinessException(Map.of("thirdPartyLoginInterfaceId", "第三方登录接口不存在"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        thirdPartyLoginInterfaceRequest.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
        validateInput(thirdPartyLoginInterfaceRequest,errors);



        String dynamicParameter = "";

        WeChatConfig weChatConfig = new WeChatConfig();//微信配置信息
        OtherConfig otherConfig = new OtherConfig();//其他开放平台

        if(thirdPartyLoginInterface.getInterfaceProduct().equals(10)){//10.微信配置信息
            weChatConfig.setOp_appID(thirdPartyLoginInterfaceRequest.getWeixin_op_appID().trim());
            weChatConfig.setOp_appSecret(thirdPartyLoginInterfaceRequest.getWeixin_op_appSecret().trim());
            weChatConfig.setOa_appID(thirdPartyLoginInterfaceRequest.getWeixin_oa_appID().trim());
            weChatConfig.setOa_appSecret(thirdPartyLoginInterfaceRequest.getWeixin_oa_appSecret().trim());
            dynamicParameter = jsonComponent.toJSONString(weChatConfig);
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        thirdPartyLoginInterface.setId(thirdPartyLoginInterfaceRequest.getThirdPartyLoginInterfaceId());
        thirdPartyLoginInterface.setName(thirdPartyLoginInterfaceRequest.getName());
        thirdPartyLoginInterface.setEnable(thirdPartyLoginInterfaceRequest.isEnable());
        thirdPartyLoginInterface.setDynamicParameter(dynamicParameter);
        thirdPartyLoginInterface.setSort(thirdPartyLoginInterfaceRequest.getSort());
        thirdPartyLoginInterface.setVersion(ThreadLocalRandom.current().nextInt(10000, 100000));//5位随机数


        thirdPartyLoginRepository.updateThirdPartyLoginInterface(thirdPartyLoginInterface);
    }

    /**
     * 删除第三方登录接口
     * @param thirdPartyLoginInterfaceId 第三方登录接口Id
     */
    public void deleteThirdPartyLoginInterface(Integer thirdPartyLoginInterfaceId){
        if(thirdPartyLoginInterfaceId == null || thirdPartyLoginInterfaceId <=0){
            throw new BusinessException(Map.of("thirdPartyLoginInterfaceId", "短信接口Id不能为空"));
        }
        thirdPartyLoginRepository.deleteThirdPartyLoginInterface(thirdPartyLoginInterfaceId);
    }


    /**
     * 校验表单字段
     * @param thirdPartyLoginInterfaceRequest 第三方登录接口表单
     * @param errors 错误信息
     */
    private void validateInput(ThirdPartyLoginInterfaceRequest thirdPartyLoginInterfaceRequest,Map<String, String> errors) {

        if(thirdPartyLoginInterfaceRequest.getInterfaceProduct().equals(10)){//10.微信配置信息
            if(thirdPartyLoginInterfaceRequest.getWeixin_op_appID() == null || thirdPartyLoginInterfaceRequest.getWeixin_op_appID().trim().isEmpty()){
                errors.put("weixin_op_appID", "开放平台应用唯一标识不能为空");
            }

            if(thirdPartyLoginInterfaceRequest.getWeixin_op_appSecret() == null || thirdPartyLoginInterfaceRequest.getWeixin_op_appSecret().trim().isEmpty()){
                errors.put("weixin_op_appSecret", "开放平台应用密钥不能为空");
            }

            if(thirdPartyLoginInterfaceRequest.getWeixin_oa_appID() == null || thirdPartyLoginInterfaceRequest.getWeixin_oa_appID().trim().isEmpty()){
                errors.put("weixin_oa_appID", "公众号应用唯一标识不能为空");
            }

            if(thirdPartyLoginInterfaceRequest.getWeixin_oa_appSecret() == null || thirdPartyLoginInterfaceRequest.getWeixin_oa_appSecret().trim().isEmpty()){
                errors.put("weixin_oa_appSecret", "公众号应用密钥不能为空");
            }
        }else{
            errors.put("interfaceProduct", "不支持的接口产品类型");
        }

    }

}
