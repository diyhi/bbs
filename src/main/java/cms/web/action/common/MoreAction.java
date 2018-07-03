package cms.web.action.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cms.bean.template.Forum;
import cms.bean.template.Forum_HelpRelated_Help;
import cms.bean.template.TemplateRunObject;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;
import cms.utils.threadLocal.TemplateThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.template.TemplateMain;


/**
 * 更多管理
 *
 */
@Controller
public class MoreAction {
	@Resource TemplateService templateService;//通过接口引用代理返回的对象
	@Resource TemplateMain templateMain;
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	/**
	 * 更多显示
	 * @param forumIdGroup 版块Id组
	 * @param parameterGroup 版块Id-版块相关Id
	 */
	@RequestMapping(value="/more/{forumIdGroup}/{parameterGroup}",method = RequestMethod.GET) 
	public String execute(@PathVariable String forumIdGroup,@PathVariable String parameterGroup,Integer page,ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Integer forumId = null;//版块Id
		String forumRelatedId = "";//版块相关Id
		String more = "";//更多
		String referenceCode = "";//版块引用代码
		
		//URL位置参数  key:参数位置 value:参数值
		LinkedHashMap<Integer,String> url_parameter = new LinkedHashMap<Integer,String>();
	
		Map<String,Object> parameter = new HashMap<String,Object>();//参数
		if(forumIdGroup != null && !"".equals(forumIdGroup)){
			String[] forumIdGroupArray = forumIdGroup.split("-");
			if(forumIdGroupArray != null && forumIdGroupArray.length >0){
				for(int i = 0; i<forumIdGroupArray.length; i++){
					if(forumIdGroupArray[i] != null && !"".equals(forumIdGroupArray[i])){
						if(i == 0){
							forumId = Integer.parseInt(forumIdGroupArray[i]);
						}else if(i == 1){
							forumRelatedId = forumIdGroupArray[i];
						}
					}
				}
			}
		}
			
		if(parameterGroup != null && !"".equals(parameterGroup)){
			String[] parameterGroupArray = parameterGroup.split("-");
			if(parameterGroupArray != null && parameterGroupArray.length >0){
				for(int i = 0; i<parameterGroupArray.length; i++){
					url_parameter.put(i, parameterGroupArray[i]);
				}
			}
		}
		if(page == null){
			page = 1;
		}
		if(page <=0){
			page = 1;
		}
		//分页
		parameter.put("page", page);
		
		//当前模板使用的目录
		String dirName = templateService.findTemplateDir_cache();
		
		
		if(forumId != null && forumId >0){
			Forum forum = templateService.findForumById_cache(forumId);
			
			if(forum != null){
				if(url_parameter != null && url_parameter.size() >0){
					if("在线帮助".equals(forum.getForumType())){
						for(Map.Entry<Integer,String> paramIter : url_parameter.entrySet()) {
							if(paramIter.getKey().equals(0)){//在线帮助分类Id
								String helpTypeId = paramIter.getValue();
								if(helpTypeId != null && !"".equals(helpTypeId.trim())){
									parameter.put("helpTypeId", helpTypeId.trim());
								}
							}
						}
					}
				}
				boolean mark = false;//标记表单参数有赋值
				if(forum.getForumChildType().equals("在线帮助列表")){
					
					if(forum.getDisplayType().equals("monolayer")){//单层
						
						String formValueJSON = forum.getFormValue();//表单值
						if(formValueJSON != null && !"".equals(formValueJSON)){
							
							Forum_HelpRelated_Help forum_HelpRelated_Help = JsonUtils.toObject(formValueJSON,Forum_HelpRelated_Help.class);
							if(forum_HelpRelated_Help != null){
								if(forum_HelpRelated_Help.getHelp_id().equals(forumRelatedId)){
								
									forum.setDisplayType("page");//设置为分页
									forum.setFormValue(JsonUtils.toJSONString(forum_HelpRelated_Help));
									more = forum_HelpRelated_Help.getHelp_more();
									referenceCode = forum.getReferenceCode();
									forum.setModule("more_help_page");
									mark = true;
								}
							}
						}
					}
				}
				if(mark == true){
					//设进线程参数里
					//将当前正在运行的引入指令设进模板参数里面
					TemplateThreadLocal.setReferenceCode(referenceCode);
	
				}
				
				
				
				
				String accessPath = accessSourceDeviceManage.accessDevices(request);
		
				
				if(more != null && !"".equals(more.trim())){
					Map<String,Object> runtimeParameter = new HashMap<String,Object>();//运行时参数
    	    		TemplateRunObject templateRunObject = TemplateThreadLocal.get();
    	    		if(templateRunObject != null && templateRunObject.getRuntimeParameter() != null && templateRunObject.getRuntimeParameter().size() >0){
    		    		runtimeParameter.putAll(templateRunObject.getRuntimeParameter());
    		    	}
					
					more =more.substring(0, more.length()-5);//删除后缀名.html
					String[] more_arr = more.split("_");
					//调用内置方法
					Object obj = templateMain.templateObject(forum,parameter,runtimeParameter);
					model.addAttribute(forum.getModule(),obj);
					//判断是否是'默认页'类型还是'更多'类型
					if(more_arr.length<3){
						
						return "templates/"+dirName+"/"+accessPath+"/"+more;

					}else{
						
						return "templates/"+dirName+"/"+accessPath+"/public/"+more;
					}
					
				}
			}
		}

		return null;
	}
}
