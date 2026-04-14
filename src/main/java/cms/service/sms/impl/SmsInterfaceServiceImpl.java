package cms.service.sms.impl;


import cms.component.JsonComponent;
import cms.component.sms.SmsComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.sms.SmsInterfaceRequest;
import cms.model.sms.Alidayu;
import cms.model.sms.SendService;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import cms.repository.setting.SettingRepository;
import cms.repository.sms.SmsRepository;
import cms.service.sms.SmsInterfaceService;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 短信接口服务
 */
@Service
public class SmsInterfaceServiceImpl implements SmsInterfaceService {

    @Resource
    SmsRepository smsRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    SmsComponent smsComponent;
    @Resource
    JsonComponent jsonComponent;

    private final Map<Integer,String> smsInterfaceProductParameter = ImmutableMap.of(1, "阿里云短信");//ImmutableMap.of(1, "阿里云短信", 10, "云片");

    /**
     * 获取短信接口列表
     * @param page 页码
     */
    public PageView<SmsInterface> getSmsInterfaceList(int page){
        //调用分页算法代码
        PageView<SmsInterface> pageView = new PageView<SmsInterface>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据sort字段降序排序
        QueryResult<SmsInterface> qr = smsRepository.getScrollData(SmsInterface.class,firstindex, pageView.getMaxresult(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取短信发送错误日志列表
     * @param page 页码
     */
    public PageView<SendSmsLog> getSendSmsLogList(int page){
        //调用分页算法代码
        PageView<SendSmsLog> pageView = new PageView<SendSmsLog>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据sort字段降序排序
        QueryResult<SendSmsLog> qr = smsRepository.getScrollData(SendSmsLog.class,firstindex, pageView.getMaxresult(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }



    /**
     * 获取添加短信接口界面信息
     * @return
     */
    public Map<String,Object> getAddSmsInterfaceViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //可添加的接口产品
        LinkedHashMap<Integer,String> smsInterfaceProductMap = new LinkedHashMap<Integer,String>();
        smsInterfaceProductMap.putAll(smsInterfaceProductParameter);

        List<SmsInterface> smsInterfaceList =  smsRepository.findAllSmsInterface();
        if(smsInterfaceList != null && smsInterfaceList.size() >0){

            for(SmsInterface _smsInterface : smsInterfaceList){
                if(smsInterfaceProductMap.containsKey(_smsInterface.getInterfaceProduct())){
                    smsInterfaceProductMap.remove(_smsInterface.getInterfaceProduct());
                }
            }
        }
        List<SendService> all_sendServiceList = new ArrayList<SendService>();

        for (Map.Entry<Integer, String> entry : smsInterfaceProductMap.entrySet()) {
            List<SendService> _sendServiceList = smsComponent.createSendService(entry.getKey());
            all_sendServiceList.addAll(_sendServiceList);
        }

        returnValue.put("sendServiceList", all_sendServiceList);

        returnValue.put("smsInterfaceProductMap",smsInterfaceProductMap);
        return returnValue;
    }

    /**
     * 添加短信接口
     * @param smsInterfaceRequest 短信接口表单
     * @param request 请求信息
     */
    public void addSmsInterface(SmsInterfaceRequest smsInterfaceRequest, HttpServletRequest request){

        //可添加的接口产品
        List<Integer> smsInterfaceProductList = new ArrayList<Integer>();
        smsInterfaceProductList.addAll(smsInterfaceProductParameter.keySet());

        SmsInterface smsInterface = new SmsInterface();

        //是否选择  true:启用 false: 禁用
        boolean enable = true;

        List<SmsInterface> smsInterfaceList =  smsRepository.findAllSmsInterface();
        if(smsInterfaceList != null && smsInterfaceList.size() >0){
            for(SmsInterface _smsInterface : smsInterfaceList){
                if(_smsInterface.isEnable()){
                    enable = false;
                }
                if(smsInterfaceProductList.contains(_smsInterface.getInterfaceProduct())){
                    smsInterfaceProductList.remove(_smsInterface.getInterfaceProduct());
                }
            }
        }

        List<SendService> all_sendServiceList = new ArrayList<SendService>();
        for(Integer smsInterfaceProduct: smsInterfaceProductList){
            List<SendService> _sendServiceList = smsComponent.createSendService(smsInterfaceProduct);
            all_sendServiceList.addAll(_sendServiceList);
        }

        if(smsInterfaceRequest.getInterfaceProduct() == null ||!smsInterfaceProductParameter.containsKey(smsInterfaceRequest.getInterfaceProduct())){
            throw new BusinessException(Map.of("interfaceProduct", "接口产品参数错误"));
        }
        if(!smsInterfaceProductList.contains(smsInterfaceRequest.getInterfaceProduct())){
            throw new BusinessException(Map.of("interfaceProduct", "请选择接口产品"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        validateInput(smsInterfaceRequest,errors);



        Alidayu alidayu = new Alidayu();//阿里大于

        String dynamicParameter = "";
        String sendService_json = "";
        if (smsInterfaceRequest.getInterfaceProduct().equals(1)) {//1.阿里大于
            alidayu.setAccessKeyId(smsInterfaceRequest.getAlidayu_accessKeyId());
            alidayu.setAccessKeySecret(smsInterfaceRequest.getAlidayu_accessKeySecret());
            dynamicParameter = jsonComponent.toJSONString(alidayu);

            List<SendService> new_sendServiceList = extractSendServiceData(all_sendServiceList, smsInterfaceRequest.getInterfaceProduct(), request, errors);
            sendService_json = jsonComponent.toJSONString(new_sendServiceList);
        }
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        smsInterface.setName(smsInterfaceRequest.getName());
        smsInterface.setInterfaceProduct(smsInterfaceRequest.getInterfaceProduct());
        smsInterface.setDynamicParameter(dynamicParameter);
        smsInterface.setSendService(sendService_json);
        smsInterface.setEnable(enable);
        smsInterface.setSort(smsInterfaceRequest.getSort());


        smsRepository.saveSmsInterface(smsInterface);

    }


    /**
     * 获取修改短信接口界面信息
     * @param smsInterfaceId 短信接口Id
     * @return
     */
    public Map<String,Object> getEditSmsInterfaceViewModel(Integer smsInterfaceId){
        if(smsInterfaceId == null || smsInterfaceId <=0){
            throw new BusinessException(Map.of("smsInterfaceId", "短信接口Id不能为空"));
        }
        SmsInterface smsInterface = smsRepository.findSmsInterfaceById(smsInterfaceId);

        if(smsInterface == null){
            throw new BusinessException(Map.of("smsInterfaceId", "短信接口不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        returnValue.put("smsInterface", smsInterface);

        if(smsInterface.getDynamicParameter() != null && !smsInterface.getDynamicParameter().trim().isEmpty()){
            if(smsInterface.getInterfaceProduct().equals(1)){//阿里大于
                Alidayu alidayu= jsonComponent.toObject(smsInterface.getDynamicParameter(), Alidayu.class);
                returnValue.put("alidayu", alidayu);
            }
        }

        List<SendService> sendServiceList = smsComponent.createSendService(smsInterface.getInterfaceProduct());

        if(smsInterface.getInterfaceProduct().equals(1)){//阿里大于
            if(smsInterface.getSendService() != null && !smsInterface.getSendService().trim().isEmpty()){
                List<SendService> _sendServiceList = jsonComponent.toGenericObject(smsInterface.getSendService(), new TypeReference< List<SendService> >(){});

                for(SendService sendService : sendServiceList){
                    for(SendService _sendService : _sendServiceList){
                        if(sendService.getServiceId().equals(_sendService.getServiceId())){
                            sendService.setAlidayu_signName(_sendService.getAlidayu_signName());
                            sendService.setAlidayu_templateCode(_sendService.getAlidayu_templateCode());

                            sendService.setAlidayu_internationalSignName(_sendService.getAlidayu_internationalSignName());
                            sendService.setAlidayu_internationalTemplateCode(_sendService.getAlidayu_internationalTemplateCode());
                            break;
                        }
                    }
                }

            }
        }
        returnValue.put("sendServiceList", sendServiceList);

        return returnValue;
    }
    /**
     * 修改短信接口
     * @param smsInterfaceRequest 短信接口表单
     * @param request 请求信息
     */
    public void editSmsInterface(SmsInterfaceRequest smsInterfaceRequest, HttpServletRequest request){
        if(smsInterfaceRequest.getSmsInterfaceId() == null || smsInterfaceRequest.getSmsInterfaceId() <=0){
            throw new BusinessException(Map.of("smsInterfaceId", "短信接口Id不能为空"));
        }
        SmsInterface smsInterface = smsRepository.findSmsInterfaceById(smsInterfaceRequest.getSmsInterfaceId());
        if(smsInterface == null){
            throw new BusinessException(Map.of("smsInterfaceId", "短信接口不存在"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        smsInterfaceRequest.setInterfaceProduct(smsInterface.getInterfaceProduct());
        validateInput(smsInterfaceRequest,errors);



        Alidayu alidayu = new Alidayu();//阿里大于

        String dynamicParameter = "";
        String sendService_json = "";
        if (smsInterface.getInterfaceProduct().equals(1)) {//1.阿里大于
            alidayu.setAccessKeyId(smsInterfaceRequest.getAlidayu_accessKeyId());
            alidayu.setAccessKeySecret(smsInterfaceRequest.getAlidayu_accessKeySecret());
            dynamicParameter = jsonComponent.toJSONString(alidayu);

            List<SendService> allSendServiceList = smsComponent.createSendService(smsInterface.getInterfaceProduct());

            List<SendService> new_sendServiceList = extractSendServiceData(allSendServiceList, smsInterface.getInterfaceProduct(), request, errors);
            sendService_json = jsonComponent.toJSONString(new_sendServiceList);
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        smsInterface.setId(smsInterfaceRequest.getSmsInterfaceId());
        smsInterface.setName(smsInterfaceRequest.getName());
        smsInterface.setDynamicParameter(dynamicParameter);
        smsInterface.setSendService(sendService_json);
        smsInterface.setSort(smsInterfaceRequest.getSort());
        smsInterface.setVersion(ThreadLocalRandom.current().nextInt(10000, 100000));//5位随机数


        smsRepository.updateSmsInterface(smsInterface);
    }

    /**
     * 删除短信接口
     * @param smsInterfaceId 短信接口Id
     */
    public void deleteSmsInterface(Integer smsInterfaceId){
        if(smsInterfaceId == null || smsInterfaceId <=0){
            throw new BusinessException(Map.of("smsInterfaceId", "短信接口Id不能为空"));
        }
        smsRepository.deleteSmsInterface(smsInterfaceId);
    }


    /**
     * 校验表单字段
     * @param smsInterfaceRequest 短信接口表单
     * @param errors 错误信息
     */
    private void validateInput(SmsInterfaceRequest smsInterfaceRequest,Map<String, String> errors) {
        if (smsInterfaceRequest.getInterfaceProduct().equals(1)) {//1.阿里大于

            if(smsInterfaceRequest.getAlidayu_accessKeyId() == null || smsInterfaceRequest.getAlidayu_accessKeyId().trim().isEmpty()){
                errors.put("alidayu_accessKeyId","用户密钥Id不能为空");
            }
            if(smsInterfaceRequest.getAlidayu_accessKeySecret() == null || smsInterfaceRequest.getAlidayu_accessKeySecret().trim().isEmpty()){
                errors.put("alidayu_accessKeySecret","用户密钥不能为空");
            }
        }else{
            errors.put("interfaceProduct", "不支持的接口产品类型");
        }

    }

    /**
     * 从 HttpServletRequest 中提取并整理短信发送服务相关数据
     * @param allSendServiceList 所有短信发送服务
     * @param interfaceProduct 接口产品
     * @param request HTTP 请求
     * @param errors 错误信息
     * @return 短信发送服务
     */
    private List<SendService> extractSendServiceData(List<SendService> allSendServiceList,Integer interfaceProduct, HttpServletRequest request,Map<String, String> errors) {
        List<SendService> new_sendServiceList = new ArrayList<SendService>();
        for(SendService sendService : allSendServiceList){
            if(sendService.getInterfaceProduct().equals(interfaceProduct)){
                String signName = request.getParameter("alidayu_signName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
                String templateCode = request.getParameter("alidayu_templateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
                String internationalSignName = request.getParameter("alidayu_internationalSignName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());
                String internationalTemplateCode = request.getParameter("alidayu_internationalTemplateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId());

                int count = 0;//统计有值字段
                if(signName != null && !signName.trim().isEmpty()){
                    sendService.setAlidayu_signName(signName.trim());
                    count++;
                }
                if(templateCode != null && !templateCode.trim().isEmpty()){
                    sendService.setAlidayu_templateCode(templateCode.trim());
                    count++;
                }
                if(count == 1){
                    if(signName == null || signName.trim().isEmpty()){
                        errors.put("alidayu_signName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信签名不能为空");
                    }
                    if(templateCode == null || templateCode.trim().isEmpty()){
                        errors.put("alidayu_templateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信模板代码不能为空");
                    }
                }

                int internationalCount = 0;//统计有值字段
                if(internationalSignName != null && !internationalSignName.trim().isEmpty()){
                    sendService.setAlidayu_internationalSignName(internationalSignName.trim());
                    internationalCount++;
                }
                if(internationalTemplateCode != null && !internationalTemplateCode.trim().isEmpty()){
                    sendService.setAlidayu_internationalTemplateCode(internationalTemplateCode.trim());
                    internationalCount++;
                }
                if(internationalCount == 1){
                    if(internationalSignName == null || internationalSignName.trim().isEmpty()){
                        errors.put("alidayu_internationalSignName_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信签名不能为空");
                    }
                    if(internationalTemplateCode == null || internationalTemplateCode.trim().isEmpty()){
                        errors.put("alidayu_internationalTemplateCode_"+sendService.getInterfaceProduct()+"_"+sendService.getServiceId(), "短信模板代码不能为空");
                    }
                }

                new_sendServiceList.add(sendService);
            }

        }
        return new_sendServiceList;
    }
}
