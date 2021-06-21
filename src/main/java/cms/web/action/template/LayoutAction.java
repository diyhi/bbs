package cms.web.action.template;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import cms.bean.template.Layout;
import cms.bean.template.Templates;
import cms.service.setting.SettingService;
import cms.service.template.TemplateService;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 布局管理 分页显示 查询结果显示
 *
 */
@Controller
public class LayoutAction {
	//注入业务bean
	@Resource(name="templateServiceBean")
	private TemplateService templateService;//通过接口引用代理返回的对象
	
	@Resource SettingService settingService;
	
	@ResponseBody
	@RequestMapping("/control/layout/list") 
	public String execute(String dirName,PageForm pageForm,ModelMap model){

		StringBuffer jpql = new StringBuffer();
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(dirName != null && !"".equals(dirName.trim())){
			jpql.append(" o.dirName=?").append((params.size()+1));
			params.add(dirName);//加上查询参数
			
			//调用分页算法代码
			PageView<Layout> pageView = new PageView<Layout>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

			orderby.put("type", "asc");//排序
			orderby.put("sort", "asc");
			QueryResult<Layout> qr = templateService.getScrollData(Layout.class,firstindex,pageView.getMaxresult(), jpql.toString(),params.toArray(),orderby);
			//将查询结果集传给分页List
			pageView.setQueryResult(qr);
			returnValue.put("pageView", pageView);
			
			//电脑版式最后修改时间
			Map<String,Date> pc_lastModified = new HashMap<String,Date>();//key:布局文件 value:最后修改时间
			//移动版式最后修改时间
			Map<String,Date> wap_lastModified = new HashMap<String,Date>();//key:布局文件 value:最后修改时间
					
			
			List<Layout> layoutList = qr.getResultlist();
			if(layoutList != null && layoutList.size() >0){
				String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"templates"+File.separator+dirName+File.separator;
				
				for(Layout layout :layoutList){
					if(layout.getLayoutFile() != null && !"".equals(layout.getLayoutFile().trim())){
						if(layout.getType().equals(1)){//如果是默认页
							File pc_file = new File(path+"pc"+File.separator+layout.getLayoutFile());
							if(pc_file.exists()){
								pc_lastModified.put(layout.getLayoutFile(), new Date(pc_file.lastModified()));
								
							}
							
							File wap_file = new File(path+"wap"+File.separator+layout.getLayoutFile());
							if(wap_file.exists()){
								wap_lastModified.put(layout.getLayoutFile(), new Date(wap_file.lastModified()));
								
							}
							
						}else{
							File pc_file = new File(path+"pc"+File.separator+"public"+File.separator+layout.getLayoutFile());
							if(pc_file.exists()){
								pc_lastModified.put(layout.getLayoutFile(), new Date(pc_file.lastModified()));
								
							}
							
							File wap_file = new File(path+"wap"+File.separator+"public"+File.separator+layout.getLayoutFile());
							if(wap_file.exists()){
								wap_lastModified.put(layout.getLayoutFile(), new Date(wap_file.lastModified()));
							}
							
						}
						
						
					}
					
				}
			}
			returnValue.put("pc_lastModified", pc_lastModified);
			returnValue.put("wap_lastModified",wap_lastModified);
			
			Templates templates = templateService.findTemplatebyDirName(dirName);
			returnValue.put("templates", templates);
			
		}else{
			error.put("dirName", "目录名称不能为空");
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
