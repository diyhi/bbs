package cms.web.action.template;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 显示生成模块引用代码
 *
 */
@Controller
public class ReferenceCodeAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	/**
	 * 显示引用代码(目前没用上)
	 */
	@ResponseBody
	@RequestMapping("/control/template/referenceCode") 
	public String execute(ModelMap model,Integer forumId,String templeteName,String layoutName,Integer returnData) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(forumId != null && forumId >0){	
			Forum forum = templateService.find(Forum.class,forumId);	
			if(forum != null){
				Layout layout = templateService.findLayoutByLayoutId(forum.getLayoutId());	
				if(layout != null){
					returnValue.put("forum",forum);
					returnValue.put("layout",layout);
					
					//代码
					if(forum.getInvokeMethod().equals(2)){
						String code = forum.getModule().substring(0,StringUtils.lastIndexOfIgnoreCase(forum.getModule(), "_"));//含有后缀
						returnValue.put("code",code);
					}
				}else{
					error.put("layout", "布局不存在");
				}
				
			}else{
				error.put("forumId", "版块不存在");
			}	
		}else{
			error.put("forumId", "版块Id不能为空");
		}	
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
