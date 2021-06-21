package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.template.Forum;
import cms.bean.template.Forum_SystemRelated_SearchWord;
import cms.bean.thirdParty.SupportLoginInterface;
import cms.bean.thirdParty.ThirdPartyLoginInterface;
import cms.service.thirdParty.ThirdPartyLoginService;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.thirdParty.ThirdPartyManage;

/**
 * 系统部分 -- 模板方法实现
 *
 */
@Component("system_TemplateManage")
public class System_TemplateManage {
	@Resource ThirdPartyLoginService thirdPartyLoginService;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	@Resource ThirdPartyManage thirdPartyManage;
	
	
	/**
	 * 热门搜索词
	 * @param forum
	 */
	public List<String> searchWord_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		List<String> searchWordList = new ArrayList<String>();

		String formValueJSON = forum.getFormValue();//表单值
		if(formValueJSON != null && !"".equals(formValueJSON)){
			Forum_SystemRelated_SearchWord forum_SystemRelated_SearchWord = JsonUtils.toGenericObject(formValueJSON, new TypeReference<Forum_SystemRelated_SearchWord>(){});
			if(forum_SystemRelated_SearchWord != null){
				if(forum_SystemRelated_SearchWord.getSearchWordList() != null && forum_SystemRelated_SearchWord.getSearchWordList().size() >0){
					searchWordList.addAll(forum_SystemRelated_SearchWord.getSearchWordList());
				}
			}
		}
		return searchWordList;
	}
	
	/**
	 * 第三方登录
	 * @param forum
	 */
	public List<SupportLoginInterface> thirdPartyLogin_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
        //HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();  

		List<SupportLoginInterface> supportLoginInterfaceList = new ArrayList<SupportLoginInterface>();

		String accessPath = accessSourceDeviceManage.accessDevices(request);
		
		//显示所有有效的第三方登录接口
		List<ThirdPartyLoginInterface> thirdPartyLoginInterfaceList = thirdPartyLoginService.findAllValidThirdPartyLoginInterface_cache();
		//设置银行
		if(thirdPartyLoginInterfaceList != null && thirdPartyLoginInterfaceList.size() >0){
			for(ThirdPartyLoginInterface thirdPartyLoginInterface : thirdPartyLoginInterfaceList){
				if(accessPath.equals("pc")){
					if(thirdPartyManage.isSupportEquipment(thirdPartyLoginInterface.getSupportEquipment(), 1)){
						SupportLoginInterface supportLoginInterface = new SupportLoginInterface();
						supportLoginInterface.setName(thirdPartyLoginInterface.getName());
						supportLoginInterface.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
						supportLoginInterface.setThirdPartyLoginInterfaceId(thirdPartyLoginInterface.getId());
						supportLoginInterfaceList.add(supportLoginInterface);
					}
				}else if(accessPath.equals("wap")){
					if(WebUtil.isWeChatBrowser(request)){//微信浏览器
						if(thirdPartyManage.isSupportEquipment(thirdPartyLoginInterface.getSupportEquipment(), 4)){
							SupportLoginInterface supportLoginInterface = new SupportLoginInterface();
							supportLoginInterface.setName(thirdPartyLoginInterface.getName());
							supportLoginInterface.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
							supportLoginInterface.setThirdPartyLoginInterfaceId(thirdPartyLoginInterface.getId());
							supportLoginInterfaceList.add(supportLoginInterface);
						}
					}else{
						if(thirdPartyManage.isSupportEquipment(thirdPartyLoginInterface.getSupportEquipment(), 2)){
							SupportLoginInterface supportLoginInterface = new SupportLoginInterface();
							supportLoginInterface.setName(thirdPartyLoginInterface.getName());
							supportLoginInterface.setInterfaceProduct(thirdPartyLoginInterface.getInterfaceProduct());
							supportLoginInterface.setThirdPartyLoginInterfaceId(thirdPartyLoginInterface.getId());
							supportLoginInterfaceList.add(supportLoginInterface);
						}
						
					}
				}
				
			}
		}
		
		return supportLoginInterfaceList;
	}
	
}

