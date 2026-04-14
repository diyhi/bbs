package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.thirdParty.ThirdPartyComponent;
import cms.dto.thirdParty.WeChatConfig;
import cms.dto.user.AccessUser;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.BaseInfoClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 前台基本信息服务
 */
@Service
public class BaseInfoClientServiceImpl implements BaseInfoClientService {
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    ThirdPartyComponent thirdPartyComponent;

    /**
     * 获取基本信息
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getBaseInfo(HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        Map<String,String> languageSwitchingMap = new LinkedHashMap<String,String>();

        AccessUser accessUser = AccessUserThreadLocal.get();

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting != null){
            returnValue.put("title",systemSetting.getTitle());//站点名称
            returnValue.put("keywords",systemSetting.getKeywords());//站点关键词
            returnValue.put("description",systemSetting.getDescription());//站点描述
            returnValue.put("supportAccessDevice",systemSetting.getSupportAccessDevice());//支持访问设备 1.自动识别终端 2.电脑端 3.移动端


        }

        returnValue.put("baseURL", WebUtil.getUrl(request));//系统路径
        returnValue.put("contextPath",request.getContextPath());//系统虚拟目录

        returnValue.put("systemUser",accessUser);//登录用户
        returnValue.put("baseURI",WebUtil.baseURI(request.getRequestURI(), request.getContextPath()));//系统资源标识符
        returnValue.put("fileStorageSystem",fileComponent.getFileSystem());//文件存储系统 0.本地系统 10.SeaweedFS 20.MinIO 30.阿里云OSS

        returnValue.put("languageSwitching",languageSwitchingMap);//多语言切换


        WeChatConfig weChatConfig = thirdPartyComponent.queryWeChatConfig();
        if(weChatConfig != null){
            returnValue.put("weixin_oa_appid",weChatConfig.getOa_appID());//微信公众号appid
        }
        return returnValue;
    }
    /**
     * 获取默认消息
     * @param response 响应信息
     * @return
     */
    public String getMessage(HttpServletResponse response){
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite() >1){//如果关闭网站
            response.setStatus(503);
        }
        return systemSetting.getCloseSitePrompt();
    }
}
