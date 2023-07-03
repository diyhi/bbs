package cms.web.action.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.PageForm;
import cms.bean.PageView;
import cms.bean.QueryResult;
import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.question.Question;
import cms.bean.report.Report;
import cms.bean.report.ReportType;
import cms.bean.topic.ImageInfo;
import cms.bean.topic.Topic;
import cms.bean.user.User;
import cms.service.question.QuestionService;
import cms.service.report.ReportService;
import cms.service.report.ReportTypeService;
import cms.service.setting.SettingService;
import cms.service.topic.TopicService;
import cms.service.user.UserService;
import cms.utils.IpAddress;
import cms.utils.JsonUtils;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.user.UserManage;

/**
 * 举报管理列表
 *
 */
@Controller
public class ReportAction {
	@Resource SettingService settingService;
	@Resource ReportService reportService;
	@Resource ReportTypeService reportTypeService;
	@Resource UserManage userManage;
	@Resource FileManage fileManage;
	@Resource TopicService topicService;
	@Resource QuestionService questionService;
	@Resource UserService userService;
	
	@ResponseBody
	@RequestMapping("/control/report/list") 
	public String execute(PageForm pageForm,ModelMap model,Boolean visible,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		StringBuffer jpql = new StringBuffer("");
		//存放参数值
		List<Object> params = new ArrayList<Object>();

		if(visible != null && visible == false){
			jpql.append(" and o.status>?"+ (params.size()+1));
			params.add(1000);
		}else{
			jpql.append(" and o.status<?"+ (params.size()+1));
			params.add(1000);
		}
		//删除第一个and
		String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
		
		PageView<Report> pageView = new PageView<Report>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
		//当前页
		int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
		//排序
		LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
		
		orderby.put("id", "desc");//根据id字段降序排序
		
		
		//调用分页算法类
		QueryResult<Report> qr = reportService.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
		
		
		if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
			List<ReportType> reportTypeList = reportTypeService.findAllReportType();
			if(reportTypeList != null && reportTypeList.size() >0){
				for(Report report : qr.getResultlist()){
					for(ReportType reportType : reportTypeList){
						if(report.getReportTypeId().equals(reportType.getId())){
							report.setReportTypeName(reportType.getName());
							break;
						}
					}
					
				}
			}
			
			
			for(Report report : qr.getResultlist()){
			//	User user = userService.findUserByUserName(report.getUserName());
				User user = userManage.query_cache_findUserByUserName(report.getUserName());
				if(user != null){
					report.setUserId(user.getId());
					report.setAccount(user.getAccount());
					report.setNickname(user.getNickname());
					if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
						report.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
						report.setAvatarName(user.getAvatarName());
					}		
				}
				
				if(report.getImageData() != null && !"".equals(report.getImageData().trim())){
					List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
					if(imageInfoList != null && imageInfoList.size() >0){
						for(ImageInfo imageInfo : imageInfoList){
							imageInfo.setPath(fileManage.fileServerAddress(request)+imageInfo.getPath());
						}
						report.setImageInfoList(imageInfoList);
					}
				}
				
				if(report.getIp() != null && !"".equals(report.getIp().trim())){
					report.setIpAddress(IpAddress.queryAddress(report.getIp()));
				}
			}
			
			
		}

		pageView.setQueryResult(qr);
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,pageView));
	}
	
	/**
	 * 话题举报列表
	 * @param pageForm
	 * @param model
	 * @param parameterId 参数Id
	 * @param module 模块
	 * @param topicId 话题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/topicReport/list") 
	public String topicReportList(PageForm pageForm,ModelMap model,String parameterId,Integer module,Long topicId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(topicId != null && topicId >0L){
			Topic topic = topicService.findById(topicId);
			if(topic != null){
				returnValue.put("currentTopic", topic);
			}
			
			if(parameterId != null && !"".equals(parameterId.trim())){
				StringBuffer jpql = new StringBuffer("");
				//存放参数值
				List<Object> params = new ArrayList<Object>();

				PageView<Report> pageView = new PageView<Report>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
				
				
				jpql.append(" and o.parameterId=?"+ (params.size()+1));
				params.add(parameterId);
				
				jpql.append(" and o.module=?"+ (params.size()+1));
				params.add(module);
				
				//删除第一个and
				String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
				
				//当前页
				int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
				//排序
				LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
				
				orderby.put("id", "desc");//根据id字段降序排序
				
				
				//调用分页算法类
				QueryResult<Report> qr = reportService.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
				
				
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					
					List<ReportType> reportTypeList = reportTypeService.findAllReportType();
					if(reportTypeList != null && reportTypeList.size() >0){
						for(Report report : qr.getResultlist()){
							for(ReportType reportType : reportTypeList){
								if(report.getReportTypeId().equals(reportType.getId())){
									report.setReportTypeName(reportType.getName());
									break;
								}
							}
							
						}
					}
					
					
					for(Report report : qr.getResultlist()){
					//	User user = userService.findUserByUserName(report.getUserName());
						User user = userManage.query_cache_findUserByUserName(report.getUserName());
						if(user != null){
							report.setUserId(user.getId());
							report.setAccount(user.getAccount());
							report.setNickname(user.getNickname());
							if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
								report.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
								report.setAvatarName(user.getAvatarName());
							}		
						}
						
						if(report.getImageData() != null && !"".equals(report.getImageData().trim())){
							List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
							if(imageInfoList != null && imageInfoList.size() >0){
								for(ImageInfo imageInfo : imageInfoList){
									imageInfo.setPath(fileManage.fileServerAddress(request)+imageInfo.getPath());
								}
								report.setImageInfoList(imageInfoList);
							}
						}
						
						if(report.getIp() != null && !"".equals(report.getIp().trim())){
							report.setIpAddress(IpAddress.queryAddress(report.getIp()));
						}
					}
					
					
				}

				pageView.setQueryResult(qr);
				
				returnValue.put("pageView", pageView);
			}else{
				error.put("topicId", "参数Id不能为空");
			}
		}else{
			error.put("topicId", "话题Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 问答举报列表
	 * @param pageForm
	 * @param model
	 * @param parameterId 参数Id
	 * @param module 模块
	 * @param questionId 问题Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/questionReport/list") 
	public String questionReportList(PageForm pageForm,ModelMap model,String parameterId,Integer module,Long questionId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(questionId != null && questionId >0L){
			Question question= questionService.findById(questionId);
			if(question != null){
				returnValue.put("currentQuestion", question);
			}
			
			if(parameterId != null && !"".equals(parameterId.trim())){
				StringBuffer jpql = new StringBuffer("");
				//存放参数值
				List<Object> params = new ArrayList<Object>();

				PageView<Report> pageView = new PageView<Report>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
				
				
				jpql.append(" and o.parameterId=?"+ (params.size()+1));
				params.add(parameterId);
				
				jpql.append(" and o.module=?"+ (params.size()+1));
				params.add(module);
				
				//删除第一个and
				String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
				
				//当前页
				int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
				//排序
				LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
				
				orderby.put("id", "desc");//根据id字段降序排序
				
				
				//调用分页算法类
				QueryResult<Report> qr = reportService.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
				
				
				if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
					
					List<ReportType> reportTypeList = reportTypeService.findAllReportType();
					if(reportTypeList != null && reportTypeList.size() >0){
						for(Report report : qr.getResultlist()){
							for(ReportType reportType : reportTypeList){
								if(report.getReportTypeId().equals(reportType.getId())){
									report.setReportTypeName(reportType.getName());
									break;
								}
							}
							
						}
					}
					
					
					for(Report report : qr.getResultlist()){
					//	User user = userService.findUserByUserName(report.getUserName());
						User user = userManage.query_cache_findUserByUserName(report.getUserName());
						if(user != null){
							report.setUserId(user.getId());
							report.setAccount(user.getAccount());
							report.setNickname(user.getNickname());
							if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
								report.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
								report.setAvatarName(user.getAvatarName());
							}		
						}
						
						if(report.getImageData() != null && !"".equals(report.getImageData().trim())){
							List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
							if(imageInfoList != null && imageInfoList.size() >0){
								for(ImageInfo imageInfo : imageInfoList){
									imageInfo.setPath(fileManage.fileServerAddress(request)+imageInfo.getPath());
								}
								report.setImageInfoList(imageInfoList);
							}
						}
						
						if(report.getIp() != null && !"".equals(report.getIp().trim())){
							report.setIpAddress(IpAddress.queryAddress(report.getIp()));
						}
					}
					
					
				}

				pageView.setQueryResult(qr);
				
				returnValue.put("pageView", pageView);
			}else{
				error.put("questionId", "参数Id不能为空");
			}
		}else{
			error.put("questionId", "问题Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 用户举报列表
	 * @param pageForm
	 * @param model
	 * @param userName 用户名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/userReport/list") 
	public String userReportList(PageForm pageForm,ModelMap model,String userName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(userName != null && !"".equals(userName.trim())){
			StringBuffer jpql = new StringBuffer("");
			//存放参数值
			List<Object> params = new ArrayList<Object>();

			PageView<Report> pageView = new PageView<Report>(settingService.findSystemSetting_cache().getBackstagePageNumber(),pageForm.getPage(),10);
			
			
			jpql.append(" and o.userName=?"+ (params.size()+1));
			params.add(userName);
			
			//删除第一个and
			String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
			
			//当前页
			int firstindex = (pageForm.getPage()-1)*pageView.getMaxresult();
			//排序
			LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
			
			orderby.put("id", "desc");//根据id字段降序排序
			
			
			//调用分页算法类
			QueryResult<Report> qr = reportService.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);
			
			
			if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
				
				List<ReportType> reportTypeList = reportTypeService.findAllReportType();
				if(reportTypeList != null && reportTypeList.size() >0){
					for(Report report : qr.getResultlist()){
						for(ReportType reportType : reportTypeList){
							if(report.getReportTypeId().equals(reportType.getId())){
								report.setReportTypeName(reportType.getName());
								break;
							}
						}
						
					}
				}
				
				
				for(Report report : qr.getResultlist()){
				//	User user = userService.findUserByUserName(report.getUserName());
					User user = userManage.query_cache_findUserByUserName(report.getUserName());
					if(user != null){
						report.setUserId(user.getId());
						report.setAccount(user.getAccount());
						report.setNickname(user.getNickname());
						if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
							report.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
							report.setAvatarName(user.getAvatarName());
						}		
					}
					
					if(report.getImageData() != null && !"".equals(report.getImageData().trim())){
						List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
						if(imageInfoList != null && imageInfoList.size() >0){
							for(ImageInfo imageInfo : imageInfoList){
								imageInfo.setPath(fileManage.fileServerAddress(request)+imageInfo.getPath());
							}
							report.setImageInfoList(imageInfoList);
						}
					}
					
					if(report.getIp() != null && !"".equals(report.getIp().trim())){
						report.setIpAddress(IpAddress.queryAddress(report.getIp()));
					}
				}
				
				
			}

			pageView.setQueryResult(qr);
			
			User user = userService.findUserByUserName(userName);
			if(user != null){
				User currentUser = new User();
				currentUser.setId(user.getId());
				currentUser.setAccount(user.getAccount());
				currentUser.setNickname(user.getNickname());
				if(user.getAvatarName() != null && !"".equals(user.getAvatarName().trim())){
					currentUser.setAvatarPath(fileManage.fileServerAddress(request)+user.getAvatarPath());
					currentUser.setAvatarName(user.getAvatarName());
				}
				returnValue.put("currentUser", currentUser);
			}
			
			
			returnValue.put("pageView", pageView);
		}else{
			error.put("userName", "用户名称不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
}
