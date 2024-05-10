package cms.web.action.question;


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
import cms.bean.question.QuestionTag;
import cms.bean.topic.Tag;
import cms.service.question.QuestionTagService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 问题标签管理列表
 *
 */
@Controller
public class QuestionTagAction {
	@Resource QuestionTagService questionTagService;
	@Resource SettingService settingService;
	@Resource FileManage fileManage;
	
	
	/**
	 * 问题标签管理分页显示
	 * parentId 父ID 
	 * name 标签名称
	 */
	@ResponseBody
	@RequestMapping("/control/questionTag/list") 
	public String execute(ModelMap model,PageForm pageForm,Long parentId,String name,
			HttpServletRequest request, HttpServletResponse response)throws Exception {	
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();
		

		//如果所属父类有值
		if(parentId != null && parentId >0){
			jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
			params.add(parentId);//设置o.parentId=?2参数
		}else{//如果没有父类
		//	jpql.append(" and o.parent is null");
			jpql.append(" and o.parentId=?"+ (params.size()+1));
			params.add(0L);
		}
		
		PageView<QuestionTag> pageView = new PageView<QuestionTag>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();;	
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("sort", "desc");//根据sort字段降序排序
		orderby.put("id", "desc");
		
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		//调用分页算法类
		QueryResult<QuestionTag> qr = questionTagService.getScrollData(QuestionTag.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		

		pageView.setQueryResult(qr);
		returnValue.put("pageView", pageView);
		
		//标签路径
		if(parentId != null && parentId >0){
			Map<Long,String> navigation = new LinkedHashMap<Long,String>();
			QuestionTag t = questionTagService.findById(parentId);
			if(t != null){
				List<QuestionTag> parentTagList = questionTagService.findAllParentById(t);
				for(QuestionTag p : parentTagList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(t.getId(), t.getName());
			}
			
			returnValue.put("navigation", navigation);//标签导航
		}
		
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			for(QuestionTag tag :qr.getResultlist()){
				if(tag.getImage() != null && !"".equals(tag.getImage().trim())){
					tag.setImage(fileManage.fileServerAddress(request)+tag.getImage());
				}
				
			}
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	
	
	
}
