package cms.web.action.report;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.report.Report;
import cms.bean.report.ReportType;
import cms.bean.staff.SysUsers;
import cms.bean.topic.ImageInfo;
import cms.service.report.ReportService;
import cms.service.report.ReportTypeService;
import cms.service.setting.SettingService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.UUIDUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.FileManage;

import org.joda.time.DateTime;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;



/**
 * 举报管理
 *
 */
@Controller
@RequestMapping("/control/report/manage") 
public class ReportManageAction {
	
	@Resource ReportTypeService reportTypeService;
	@Resource ReportService reportService; 
	@Resource FileManage fileManage;
	@Resource TextFilterManage textFilterManage;
	
	@Resource SettingService settingService;
	//模块参数
	private List<Integer> statusList = Arrays.asList(40,50); 
	
	/**
	 * 举报处理
	 * @param model
	 * @param reportId
	 * @param status
	 * @param processResult
	 * @param remark
	 * @param version
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=reportHandle", method=RequestMethod.POST)
	public String reportHandle(ModelMap model,Long reportId,Integer status,String processResult,String remark,Integer version,
		HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String,String> error = new HashMap<String,String>();//错误
		
		String username = "";//用户名称
		String userId = "";//用户Id
		Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(principal instanceof SysUsers){
			userId =((SysUsers)principal).getUserId();
			username =((SysUsers)principal).getUserAccount();
		}
		
		if(reportId != null && reportId >0L){
			Report report = reportService.findById(reportId);
			if(report != null){
				if(!report.getStatus().equals(10)){
					error.put("report", "待处理状态才能执行举报流程");
				}else{
					if(!report.getVersion().equals(version)){
						error.put("report", "举报内容不是最新");
					}
				}
			}else{
				error.put("reportId", "举报不存在");
			}
		}else{
			error.put("reportId", "举报Id不能为空");
		}
		
		if(status == null || status <=0){
			error.put("status", "状态不能为空");
			
		}else{
			if(!statusList.contains(status)){
				error.put("status", "状态参数错误");
			}
		}
		
		
		
		if(error.size() == 0){
			int i = reportService.updateReportInfo(reportId, status, processResult, remark, new Date(),username, version);
			if(i ==0){
				error.put("report", "修改失败");
				return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
			}
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 举报   修改界面显示
	 */
	@ResponseBody
	@RequestMapping(params="method=edit", method=RequestMethod.GET)
	public String editUI(ModelMap model,Long reportId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		Map<String,Object> returnValue = new HashMap<String,Object>();
		if(reportId != null){//判断ID是否存在;
			Report report = reportService.findById(reportId);
			if(report != null){
				
				ReportType reportType = reportTypeService.findById(report.getReportTypeId());
				if(reportType != null){
					report.setReportTypeName(reportType.getName());
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
				returnValue.put("report",report);//返回消息
			}else{
				error.put("reportId", "举报不存在");
			}
		}else{
			error.put("reportId", "举报Id不能为空");
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
		}
	}
	
	/**
	 * 举报  修改
	 */
	@ResponseBody
	@RequestMapping(params="method=edit",method=RequestMethod.POST)
	public String edit(ModelMap model,Long reportId,String reportTypeId,Integer status,
			String reason,String processResult,String remark,MultipartFile[] imageFile,String[] imagePath,Integer version,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Report report = null;
		Integer old_status = -1;
		List<ImageInfo> old_imageInfoList = new ArrayList<ImageInfo>();
		List<ImageInfo> new_imageInfoList = new ArrayList<ImageInfo>();
		String date = "";
		Map<String,String> error = new HashMap<String,String>();
		if(reportId == null || reportId <=0){
			error.put("reportId", "举报Id不能为空");
		}else{
			report = reportService.findById(reportId);
			
			if(report != null && !report.getVersion().equals(version)){
				error.put("report", "举报内容不是最新");
			}
		}
		
		if(status != null && !statusList.contains(status)){
			error.put("status", "状态参数错误");
		}
		
		
		
		String username = "";//用户名称
		String userId = "";//用户Id
		Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
		if(principal instanceof SysUsers){
			userId =((SysUsers)principal).getUserId();
			username =((SysUsers)principal).getUserAccount();
		}
		
		
		if(error.size()==0){
			if(report != null){
				old_status = report.getStatus();
				
				if(status != null){
					report.setStatus(status);
					if(old_status.equals(10)){
						report.setProcessCompleteTime(new Date());
						report.setStaffAccount(username);
					}
				}
				
				report.setReportTypeId(reportTypeId);
				report.setReason(reason);
				report.setProcessResult(processResult);
				report.setRemark(remark);
				
				if(report.getImageData() != null && !"".equals(report.getImageData().trim())){
					old_imageInfoList = JsonUtils.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
				}
				
				DateTime dateTime = new DateTime(report.getPostTime()); 
				date = dateTime.toString("yyyy-MM-dd");
				
				
				if(imageFile != null){
					
					int imagePathCount = 0;//已上传图片文件上传总数
					for(MultipartFile file : imageFile) {
						if(file.isEmpty()){//如果图片已上传
							String image = imagePath[imagePathCount];
							if(image != null && !"".equals(image.trim())){
								image = textFilterManage.deleteBindURL(request, image);
								
							}
							//取得文件名称
							String fileName = FileUtil.getName(image);

							//取得路径名称
							String pathName = FileUtil.getFullPath(image);
							
							
							
							new_imageInfoList.add(new ImageInfo(pathName,fileName));
							
							imagePathCount++;
						}else{
							
							//验证文件类型
							List<String> formatList = new ArrayList<String>();
							formatList.add("gif");
							formatList.add("jpg");
							formatList.add("jpeg");
							formatList.add("bmp");
							formatList.add("png");
							boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
							if(authentication){
								//取得文件后缀		
								String ext = FileUtil.getExtension(file.getOriginalFilename());
								//文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
								String pathDir = "file"+File.separator+"report"+File.separator + date +File.separator;;
								//构建文件名称
								String fileName = UUIDUtil.getUUID32()+ "." + ext;
							
								new_imageInfoList.add(new ImageInfo("file/report/"+ date +"/",fileName));
								
								//生成文件保存目录
								FileUtil.createFolder(pathDir);
								 
								//生成锁文件名称
								String lockFileName = date +"_"+fileName;
								//添加文件锁
								fileManage.addLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,lockFileName);
								
								//保存文件
								fileManage.writeFile(pathDir, fileName,file.getBytes());
								
							}else{
								error.put("image", "图片格式错误");
							}	
						}
					}
				}
			}else{
				error.put("report", "举报不存在");
			}
		}

		
		if(error.size() == 0){
			
			String imageData = JsonUtils.toJSONString(new_imageInfoList);
			
			
			int i = reportService.updateReportInfo(report.getId(), report.getReportTypeId(),report.getStatus(), report.getProcessResult(), report.getReason(),report.getRemark(), report.getProcessCompleteTime(),report.getStaffAccount(),imageData, version);
			if(i ==0){
				error.put("report", "修改失败");
				return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
			}else{
				//删除旧文件
				A:for(ImageInfo old_imageInfo : old_imageInfoList){
					for(ImageInfo new_imageInfo : new_imageInfoList){
						if(old_imageInfo.getPath().equals(new_imageInfo.getPath()) && old_imageInfo.getName().equals(new_imageInfo.getName())){
							continue A;
						}
					}
					//删除旧图片
					//替换路径中的..号
					String oldPathFile = FileUtil.toRelativePath(old_imageInfo.getPath()+old_imageInfo.getName());
					//删除旧文件
					fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile));
				}
				
				//删除图片锁
	    		if(new_imageInfoList != null && new_imageInfoList.size() >0){
	    			for(ImageInfo imageInfo : new_imageInfoList){
	    				fileManage.deleteLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,date +"_"+imageInfo.getName());
	    			}
	    		}
			}
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 举报   删除
	 * @param model
	 * @param reportId 举报Id集合
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String delete(ModelMap model,Long[] reportId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Map<String,Object> error = new HashMap<String,Object>();
		
		if(reportId != null && reportId.length >0){
			List<Long> reportIdList = new ArrayList<Long>();
			for(Long l :reportId){
				if(l != null && l >0L){
					reportIdList .add(l);
				}
			}
			if(reportIdList != null && reportIdList.size() >0){

				List<Report> reportList = reportService.findByIdList(reportIdList);
				if(reportList != null && reportList.size() >0){
					for(Report report : reportList){
						if(report.getStatus() < 1000){//标记删除
							int i = reportService.markDelete(report.getId(),100000);
						}else{//物理删除
							int i = reportService.deleteReport(report.getId());
							if(i>0){
								//删除图片
								if(report.getImageData() != null && !"".equals(report.getImageData().trim())){
									List<ImageInfo> imageInfoList = JsonUtils.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
									if(imageInfoList != null && imageInfoList.size() >0){
										for(ImageInfo imageInfo : imageInfoList){
											//删除图片
											//替换路径中的..号
											String oldPathFile = FileUtil.toRelativePath(imageInfo.getPath());
											//删除文件
											fileManage.deleteFile(FileUtil.toSystemPath(oldPathFile)+imageInfo.getName());
										}
									}
								}
							}
						}
						
						
					}	
					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
				}
			}else{
				error.put("reportId", "举报Id不能为空");
			}
		}else{
			error.put("reportId", "举报Id不存在");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 还原
	 * @param model
	 * @param reportId 举报Id集合
	 * @return
	 * @throws Exception
	*/
	@RequestMapping(params="method=reduction",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reduction(ModelMap model,Long[] reportId,
			HttpServletResponse response) throws Exception {
		//错误
		Map<String,String> error = new HashMap<String,String>();
		
		if(reportId != null && reportId.length >0){
			List<Long> reportIdList = new ArrayList<Long>();
			for(Long l :reportId){
				if(l != null && l >0L){
					reportIdList .add(l);
				}
			}
			if(reportIdList != null && reportIdList.size() >0){

				List<Report> reportList = reportService.findByIdList(reportIdList);
				if(reportList != null && reportList.size() >0){
					for(Report report : reportList){
						if(report.getStatus() >1000){
							int originalState = this.parseInitialValue(report.getStatus());
							
							
							reportService.reductionReport(report.getId(), originalState);
						}
					}	
					return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
				}
			}else{
				error.put("reportId", "举报Id不能为空");
			}
		}else{
			error.put("reportId", "举报Id不存在");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 解析初始值
	 * @param status 状态
	 * @return
	 */
	private int parseInitialValue(Integer status){
		int tens  = status%100/10;//十位%100/10
        int units  = status%10;//个位直接%10
		
        return Integer.parseInt(tens+""+units);
	}
}
