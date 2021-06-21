package cms.web.action.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.template.Forum;
import cms.bean.template.Layout;
import cms.bean.template.Templates;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 版块管理 分页显示 查询结果显示
 *
 */
@Controller
public class ForumAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	@Resource SettingService settingService;
	
	@ResponseBody
	@RequestMapping("/control/forum/list") 
	public String execute(@RequestParam("layoutId") String layoutId,
			@RequestParam("dirName") String dirName,PageForm pageForm,ModelMap model)
			throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new LinkedHashMap<String,Object>();

		
		StringBuffer jpql = new StringBuffer();
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		

		Layout layout = null;
		if(layoutId != null && !"".equals(layoutId)){
			layout = templateService.findLayoutByLayoutId(layoutId);
			if(layout == null){
				error.put("layoutId", "布局不存在");
			}
			
			jpql.append(" o.layoutId=?").append((params.size()+1));
			params.add(layoutId);//加上查询参数
		}else {
			error.put("layoutId", "布局Id不能为空");
		}
		
		if(dirName == null || "".equals(dirName.trim())){
			error.put("dirName", "目录不能为空");
		}
		
		
		if(error.size() ==0){
			returnValue.put("layout", layout);
			
			//调用分页算法代码
			PageView<Forum> pageView = new PageView<Forum>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据code字段降序排序
			QueryResult<Forum> qr = templateService.getScrollData(Forum.class,firstindex,pageView.getMaxresult(), jpql.toString(),params.toArray(),orderby);
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			returnValue.put("pageView", pageView);
			
			boolean status = false;//状态   true:已有一个以上版块   false:还未有版块
			
			//公共页(引用版块值)只允许添加一个版块
			if(templateService.getForumThere(dirName,layoutId,6) == true){//6.公共页(引用版块值)
				status = true;
			}
			//空白页(json)只允许添加一个版块
			List<Forum> forumList = templateService.findForumByLayoutId(dirName, layoutId);
			if(forumList != null && forumList.size() >0){
				for(Forum forum : forumList){
					if(forum.getLayoutType().equals(4) && layout.getReturnData().equals(1)){
						status = true;
					}
				}
			}
			returnValue.put("publicForum", status);
			
			//根据模板目录名称查询模板
			
			Templates templates = templateService.findTemplatebyDirName(dirName);
			returnValue.put("templates", templates);
			
			
		}
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
