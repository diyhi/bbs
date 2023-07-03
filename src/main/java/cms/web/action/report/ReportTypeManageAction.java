package cms.web.action.report;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.report.ReportType;
import cms.service.report.ReportTypeService;
import cms.service.setting.SettingService;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.utils.Verification;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * 举报分类管理
 *
 */
@Controller
@RequestMapping("/control/reportType/manage") 
public class ReportTypeManageAction {
	@Resource ReportTypeManage reportTypeManage;
	@Resource ReportTypeService reportTypeService; 
	
	
	@Resource SettingService settingService;
	
	/**
	 * 举报分类   添加界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=add",method=RequestMethod.GET)
	public String addUI(ModelMap model,ReportType reportType,String parentId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(parentId != null && !"".equals(parentId.trim())){//判断父类ID是否存在;
			ReportType _type = reportTypeService.findById(parentId);
			if(_type != null){
				returnValue.put("parentType", _type);//返回消息
				
				
				Map<String,String> navigation = new LinkedHashMap<String,String>();
				
				List<ReportType> parentProductTypeList = reportTypeService.findAllParentById(_type);
				for(ReportType p : parentProductTypeList){
					navigation.put(p.getId(), p.getName());
				}
				navigation.put(_type.getId(),_type.getName());
				returnValue.put("navigation", navigation);//分类导航
			}else{
				error.put("parentId", "父类不存在");
			}

			
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 举报分类  添加
	 */
	@ResponseBody
	@RequestMapping(params="method=add", method=RequestMethod.POST)
	public String add(ModelMap model,String name,String sort,String parentId,Boolean giveReason,
		HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String,String> error = new HashMap<String,String>();//错误
		
		
		
		if(name == null || "".equals(name.trim())){
			error.put("name", "分类名称不能为空");
		}else{
			if(name.length() >190){
				error.put("name", "不能大于190个字符");
			}
		}
		if(giveReason == null){
			giveReason = false;
		}
		if(sort != null && !"".equals(sort.trim())){
			boolean verification = Verification.isPositiveIntegerZero(sort.trim());//数字
			if(verification){
				if(sort.trim().length()>8){
					error.put("sort", "不能大于8位数字");
				}
			}else{
				error.put("sort", "排序必须为大于或等于0的数字");
			}
		}else{
			error.put("sort", "排序不能为空");
		}
		String parentIdGroup = ",0,";
		if(parentId != null && !"".equals(parentId.trim())){//判断父类ID是否存在;
			if(!"0".equals(parentId.trim())){
				//取得父对象
				ReportType t = reportTypeService.findById(parentId.trim());
				if(t != null){
					//根据分类查询所有父分类
					List<ReportType> allParentType = reportTypeService.findAllParentById(t);
					if(allParentType.size() >=1){
						error.put("type", "分类已达到最大层数,添加失败");
					}
					parentIdGroup = t.getParentIdGroup()+parentId.trim()+",";
				}else{
					error.put("type", "父分类不存在");
				}
			}
		}else{
			parentId = "0";
		}
		
		if(error.size() == 0){
			ReportType reportType = new ReportType(); 
			reportType.setId(UUIDUtil.getUUID32());
			reportType.setName(name.trim());
			reportType.setGiveReason(giveReason);
			reportType.setParentId(parentId.trim());
			reportType.setParentIdGroup(parentIdGroup);
			reportType.setSort(Integer.parseInt(sort));
			reportTypeService.saveReportType(reportType);
			
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	
	/**
	 * 举报分类   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,String reportTypeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		if(reportTypeId != null && !"".equals(reportTypeId.trim())){//判断ID是否存在;
			ReportType reportType = reportTypeService.findById(reportTypeId);
			if(reportType != null){
				returnValue.put("reportType",reportType);//返回消息

				int i=0;
				Map<String,String> navigation = new LinkedHashMap<String,String>();
				List<ReportType> parentTypeList = reportTypeService.findAllParentById(reportType);
				for(ReportType r : parentTypeList){
					navigation.put(r.getId(), r.getName());
					i++;
					if(i ==1){
						returnValue.put("parentType", r);
						
					}
					
				}
				returnValue.put("navigation", navigation);//分类导航
			}
		}else{
			error.put("reportTypeId", "分类Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	/**
	 * 举报分类   修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.POST)
	public String edit(ModelMap model,String reportTypeId,String name,String sort,Boolean giveReason,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,String> error = new HashMap<String,String>();//错误
		
		
		
		if(name == null || "".equals(name.trim())){
			error.put("name", "分类名称不能为空");
		}else{
			if(name.length() >190){
				error.put("name", "不能大于190个字符");
			}
		}
		if(giveReason == null){
			giveReason = false;
		}
		if(sort != null && !"".equals(sort.trim())){
			boolean verification = Verification.isPositiveIntegerZero(sort.trim());//数字
			if(verification){
				if(sort.trim().length()>8){
					error.put("sort", "不能大于8位数字");
				}
			}else{
				error.put("sort", "排序必须为大于或等于0的数字");
			}
		}else{
			error.put("sort", "排序不能为空");
		}
		
		
		ReportType type = null;
		if(reportTypeId != null && !"".equals(reportTypeId.trim())){
			//取得对象
			type = reportTypeService.findById(reportTypeId);
			if(type == null){
				error.put("type", "分类不存在");
			}
		}else{
			error.put("type", "分类Id不能为空");
		}

		
		if(error.size() == 0){
			ReportType new_type = new ReportType(); 
			new_type.setId(reportTypeId);
			new_type.setName(name.trim());
			new_type.setSort(Integer.parseInt(sort));
			new_type.setGiveReason(giveReason);
			
			reportTypeService.updateReportType(new_type);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 举报分类 删除
	*/
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,String reportTypeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		if(reportTypeId != null && !"".equals(reportTypeId.trim())){
			ReportType type = reportTypeService.findById(reportTypeId);
			
			//根据分类Id查询子分类(下一节点)
			List<ReportType> childTypeList = reportTypeService.findChildTypeById(reportTypeId);

			List<String> typeIdList = new ArrayList<String>();
			typeIdList.add(type.getId());
			if(childTypeList != null && childTypeList.size() >0){
				for(ReportType t : childTypeList){
					typeIdList.add(t.getId());
				}
			}
			
			int i = reportTypeService.deleteReportType(type);
			if(i >0){
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}
			
		}else{
			error.put("reportTypeId", "分类Id不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 举报分类 查询所有分类
	*/
	@ResponseBody
	@RequestMapping(params="method=allType", method=RequestMethod.GET)
	public String queryAllType(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<ReportType> reportTypeList =  reportTypeService.findAllReportType();
		List<ReportType> new_reportTypeList = new ArrayList<ReportType>();//排序后分类
		
		if(reportTypeList != null && reportTypeList.size() >0){
			
			//组成排序数据
			Iterator<ReportType> reportType_iter = reportTypeList.iterator();   
			while(reportType_iter.hasNext()){   
				ReportType reportType = reportType_iter.next();
				
				//如果是根节点
				if(reportType.getParentId().equals("0")){
					
					new_reportTypeList.add(reportType);
					reportType_iter.remove();   
			    }  
			}
			//组合子分类
			for(ReportType reportType :new_reportTypeList){
				reportTypeManage.childReportType(reportType,reportTypeList);
			}
			//排序
			reportTypeManage.reportTypeSort(new_reportTypeList);
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,new_reportTypeList));
	}
	
	
}
