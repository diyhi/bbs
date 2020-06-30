package cms.web.action.common;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.queryString.util.MultiMap;
import org.queryString.util.UrlEncoded;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.template.TemplateRunObject;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;
import cms.utils.WebUtil;
import cms.utils.threadLocal.TemplateThreadLocal;
import cms.web.action.AccessSourceDeviceManage;
import cms.web.action.template.TemplateMain;



/**
 * 根据相应的URI读取相关'空白页'
 *
 */
@Controller
public class BlankAction {
	@Resource(name="templateServiceBean")
	private TemplateService templateService;
	
	@Resource TemplateMain templateMain;//模板管理入口
	@Resource AccessSourceDeviceManage accessSourceDeviceManage;
	
	@RequestMapping("/**")
	public String execute(ModelMap model,
			HttpServletRequest request,HttpServletResponse response)throws Exception {
		  // 判断Url并分发到不同的Action，并统一控制转向   
		    String currentURI=request.getRequestURI();//获取当前的全部URI
		    String dirName = templateService.findTemplateDir_cache();
		
			String accessPath = accessSourceDeviceManage.accessDevices(request);
		  
			
		    if(currentURI != null && !"".equals(currentURI)){
		    	String paths = request.getContextPath();//系统虚拟路径
		       	if(paths != null && !"".equals(paths)){
		       		currentURI = StringUtils.substringAfter(currentURI, paths);//删除系统虚拟路径
		       	}
		       	String URI = StringUtils.substringAfter(currentURI, "/");
		       	URI = StringUtils.substringBeforeLast(URI, ".");
		       	
		
		       	Map<String,Object> parameter = this.getQueryString(request.getQueryString());//获取URL参数
	
		        String templatesDir = templateService.findTemplateDir_cache();
		        List<Layout> layoutList = templateService.findLayout_cache(templatesDir,4);//4.空白页
		       	for(Layout layout : layoutList){
		       		
		        	if(URI != null && !"".equals(URI)){
		        		
		        		if(URI.toLowerCase().equals(layout.getReferenceCode().toLowerCase())){    
		        			
		        			if(layout.getReturnData().equals(0)){//返回数据为html方式
		        	    		response.setHeader("Pragma", "No-cache");   
			        		    response.setHeader("Cache-Control", "no-cache,no-store,max-age=0");   
			        		    response.setDateHeader("Expires", 1);

			        			//去掉.html后缀
			        			String layouts = StringUtils.substringBefore(layout.getLayoutFile(),".");//返回指定字符串之前的所有字符
			        			return "templates/"+layout.getDirName()+"/"+accessPath+"/public/"+layouts;
		        	    	}else{//返回数据为json方式
		        	    		
		        	    		
		        	    		Map<String,Object> runtimeParameter = new HashMap<String,Object>();//运行时参数
		        	    		TemplateRunObject templateRunObject = TemplateThreadLocal.get();
		        	    		if(templateRunObject != null && templateRunObject.getRuntimeParameter() != null && templateRunObject.getRuntimeParameter().size() >0){
		        		    		runtimeParameter.putAll(templateRunObject.getRuntimeParameter());
		        		    	}
		        	    		
		        	    		List<Forum> forumList = templateService.findForumByLayoutId_cache(templatesDir,layout.getId());
		        	    		for(Forum forum: forumList){	
		        	    			Object obj = templateMain.templateObject(forum,parameter,runtimeParameter);
		        	    			if(obj != null){
		        	    				WebUtil.writeToWeb(JsonUtils.toJSONString(obj), "json", response);
		        	    			}else{
		        	    				WebUtil.writeToWeb("", "json", response);
		        	    				
		        	    			}
		        	    			
				        			return null;
		        	    			//json方式只有一个版块有效
		        	    		//	break;
		        	    		}
		        	    		//如果没添加版块则返回null
			        			return null;
		        	    	}
		        		}		
		        	}	
		        } 	
		    }
		   
		   
		    //UserRoleManage类在检查权限时可能会返回message内容
		    Object parameter = request.getAttribute("message");
		    if(parameter == null){ //如果上一环节没有传递参数
		    	model.addAttribute("message","页面不存在");//返回消息  
		    }
		    return "templates/"+dirName+"/"+accessPath+"/message";
	}
	
	/**
	 * 获取url参数
	 * @param queryString
	 * @return
	 */
	private Map<String,Object> getQueryString(String queryString){
		Map<String,Object> parameter = new HashMap<String,Object>();//参数 

       	if(queryString != null && !"".equals(queryString)){
       		MultiMap<String> values = new MultiMap<String>();  
	       	UrlEncoded.decodeTo(queryString, values, "UTF-8");
	
	       	Iterator iter = values.entrySet().iterator();  
	       	while(iter.hasNext()){  
	       		Map.Entry e = (Map.Entry)iter.next();  
	       		if(e.getValue() != null){
	       			
	       			
	       			if(e.getValue() instanceof List){
	       				
	       				List<String> valueList = (List)e.getValue();
	       				
		       			if(valueList.size() >0){
		       				for(String value :valueList){
		       					if(value != null && !"".equals(value.trim())){
		       						parameter.put(e.getKey().toString(), value);
		       					}
		       					break;
		       				}
		       				/**
		       				if(valueList.size() ==1){//字符类型参数
		       					for(String value :valueList){
			       					if(value != null && !"".equals(value.trim())){
			       						parameter.put(e.getKey().toString(), value);
			       					}
			       				}
		       					
		       				}else{//集合类型参数
		       					List<String> parameterValueList = new ArrayList<String>();
		       					for(String value :valueList){
			       					if(value != null && !"".equals(value.trim())){
			       						parameterValueList.add(value);
			       					}
			       				}
		       					parameter.put(e.getKey().toString(), parameterValueList);
		       				}**/
		       				
			       		}
		       		}		
	       		}
	       	} 
       	}
		
		return parameter;
	}
	
	
	
}
