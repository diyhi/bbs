package cms.web.action.report;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.report.ReportType;
import cms.service.report.ReportTypeService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 举报分类管理列表
 *
 */
@Controller
public class ReportTypeAction {
	@Resource ReportTypeService reportTypeService;
	@Resource SettingService settingService;
	/**
	 * 举报分类管理分页显示
	 * parentId 父Id
	 * name 分类名称
	 */
	@ResponseBody
	@RequestMapping("/control/reportType/list") 
	public String execute(ModelMap model,PageForm pageForm,String parentId,String name,
			HttpServletRequest request, HttpServletResponse response)throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		

		//如果所属父类有值
		if(parentId != "" && !"0".equals(parentId.trim())){
			jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(parentId);//设置o.parentId=?2参数
		}else{//如果没有父类
			jpql.append(" and o.parentId=?"+ (params.size()+1));
			params.add("0");
		}
		
		PageView<ReportType> pageView = new PageView<ReportType>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		orderby.put("id", "desc");
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//调用分页算法类
		QueryResult<ReportType> qr = reportTypeService.getScrollData(ReportType.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		

		pageView.setQueryResult(qr);
		returnValue.put("pageView", pageView);
		
		//分类路径
		if(parentId != "" && !"".equals(parentId.trim()) && !"0".equals(parentId.trim())){
			Map<String,String> navigation = new LinkedHashMap<String,String>();
			ReportType t = reportTypeService.findById(parentId);
			if(t != null){
				List<ReportType> parentTagList = reportTypeService.findAllParentById(t);
				for(ReportType p : parentTagList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(t.getId(), t.getName());
			}
			
			returnValue.put("navigation", navigation);//标签导航
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
