package cms.web.action.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.setting.SystemSetting;
import cms.bean.user.AccessUser;
import cms.bean.user.ResourceEnum;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.AES;
import cms.utils.Base64;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.utils.threadLocal.AccessUserThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.user.UserRoleManage;

/**
 * 视频播放
 * @author Gao
 *
 */
@Controller
public class VideoPlaybackAction {
	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	
	@Resource SettingService settingService;
	@Resource UserRoleManage userRoleManage;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource TemplateService templateService;
	@Resource FileManage fileManage;
	
	
	/**
	 * 视频重定向
	 * @param model
	 * @param jump 重定向参数
	 * @param redirectAttrs
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/videoRedirect")
	public String videoRedirect(ModelMap model,String jump,
			RedirectAttributes redirectAttrs,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		boolean isAjax = WebUtil.submitDataMode(request);//是否以Ajax方式提交数据
		Map<String,Object> returnValue = new HashMap<String,Object>();//返回值
		returnValue.put("isPermission", true);//是否有权限查看
		returnValue.put("redirect", "");//重定向地址
		
		
		if(jump != null && !"".equals(jump.trim())){
			SystemSetting systemSetting = settingService.findSystemSetting_cache();
			
			
			String ciphertext = Base64.decodeBase64URL(jump.trim());
			String parameter_json = AES.decrypt(ciphertext, systemSetting.getFileSecureLinkSecret(), null);
			
			Map<String,String> parameterMap = JsonUtils.toGenericObject(parameter_json, new TypeReference< Map<String,String>>(){});
			
			String link = parameterMap.get("link");	//文件链接	
			String tagId = parameterMap.get("tagId");//话题标签Id
			Long _tagId = Long.parseLong(tagId);
			
			
			if(_tagId .equals(-1L)){//如果为-1,则是后台管理页面下载，不检查权限
				if(isAjax == true){
					returnValue.put("redirect", fileManage.createSignLink(link,"",systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire()));
				}else{
					return "redirect:"+fileManage.createSignLink(link,"",systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire());
				}
				
			}else{
				//检查权限,允许查看话题内容的角色则允许下载
				boolean flag = userRoleManage.isPermission(ResourceEnum._1001000,_tagId);
				if(flag){
					if(isAjax == true){
						returnValue.put("redirect", fileManage.createSignLink(link,"",systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire()));
						
					}else{
						return "redirect:"+fileManage.createSignLink(link,"",systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire());
						
					}
					
				}else{
					returnValue.put("isPermission", false);//是否有权限查看
				}
				
			}
			
			
		}
		
		if(isAjax == true){
			//获取登录用户
		  	AccessUser accessUser = AccessUserThreadLocal.get();
		  	if(accessUser != null){
		  		returnValue.put("isLogin", true);//是否登录用户
		  	}else{
		  		returnValue.put("isLogin", false);
		  	}
    		
    		WebUtil.writeToWeb(JsonUtils.toJSONString(returnValue), "json", response);
			return null;
		}else{
			//当前模板使用的目录
			String dirName = templateService.findTemplateDir_cache();
			
			model.addAttribute("message","视频重定向失败");//返回消息  
			return "templates/"+dirName+"/"+accessSourceDeviceManage.accessDevices(request)+"/message";
			
		//	return "redirect:/index";
		}
	}
	
}
