package cms.web.action.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cms.bean.setting.SystemSetting;
import cms.bean.thirdParty.WeChatConfig;
import cms.bean.thirdParty.WeiXinOpenId;
import cms.bean.user.AccessUser;
import cms.bean.user.RefreshUser;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.Base64;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.thirdParty.ThirdPartyManage;
import cms.web.taglib.Configuration;

/**
 * 基本信息
 * @author Gao
 *
 */
@Controller
public class BaseInfoAction {
	
	@Resource ThirdPartyManage thirdPartyManage;
	@Resource TemplateService templateService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	@Resource OAuthManage oAuthManage;
	
	/**
	 * 查询cms.web.filter.TempletesInterceptor.java的postHandle方法返回的部分信息
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/baseInfo",method=RequestMethod.GET) 
	@ResponseBody
	public String execute(ModelMap model,String code,
			HttpServletRequest request,HttpServletResponse response)throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		

		String dirName = templateService.findTemplateDir_cache();	
    	AccessUser accessUser = AccessUserThreadLocal.get();
		
    	SystemSetting systemSetting = settingService.findSystemSetting_cache();
    	if(systemSetting != null){
    		returnValue.put("title",systemSetting.getTitle());//站点名称
    		returnValue.put("keywords",systemSetting.getKeywords());//站点关键词
    		returnValue.put("description",systemSetting.getDescription());//站点描述
    		returnValue.put("supportAccessDevice",systemSetting.getSupportAccessDevice());//支持访问设备 1.自动识别终端 2.电脑端 3.移动端
    	}
    	
		returnValue.put("baseURL", Configuration.getUrl(request));//系统路径
		returnValue.put("commonPath", "common/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/");//资源路径
		returnValue.put("contextPath",request.getContextPath());//系统虚拟目录
		returnValue.put("templateDir",dirName);//模板目录名称
		
		returnValue.put("systemUser",accessUser);//登录用户
		returnValue.put("baseURI",Configuration.baseURI(request.getRequestURI(), request.getContextPath()));//系统资源标识符
		returnValue.put("fileStorageSystem",fileManage.getFileSystem());//文件存储系统 0.本地系统 10.SeaweedFS 20.MinIO 30.阿里云OSS
		
		
		WeChatConfig weChatConfig = thirdPartyManage.queryWeChatConfig();
    	if(weChatConfig != null){
    		returnValue.put("weixin_oa_appid",weChatConfig.getOa_appID());//微信公众号appid
    	}
		
		return JsonUtils.toJSONString(returnValue);
	}
}
