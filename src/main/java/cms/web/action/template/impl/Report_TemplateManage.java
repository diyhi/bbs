package cms.web.action.template.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cms.bean.report.ReportType;
import cms.bean.setting.SystemSetting;
import cms.bean.template.Forum;
import cms.bean.user.AccessUser;
import cms.service.report.ReportService;
import cms.service.report.ReportTypeService;
import cms.service.setting.SettingService;
import cms.utils.UUIDUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.common.CaptchaManage;
import cms.web.action.fileSystem.FileManage;
import cms.web.action.report.ReportTypeManage;
import cms.web.action.setting.SettingManage;

/**
 * 举报 -- 模板方法实现
 *
 */
@Component("report_TemplateManage")
public class Report_TemplateManage {
	
	@Resource ReportTypeService reportTypeService;
	@Resource ReportService reportService; 
	@Resource SettingService settingService;
	@Resource SettingManage settingManage;
	@Resource CaptchaManage captchaManage;
	@Resource FileManage fileManage;
	@Resource ReportTypeManage reportTypeManage;
	
	
	
	/**
	 * 举报  -- 添加
	 * @param forum
	 */
	public Map<String,Object> addReport_collection(Forum forum,Map<String,Object> parameter,Map<String,Object> runtimeParameter){
		Map<String,Object> value = new HashMap<String,Object>();
		
		AccessUser accessUser = null;
		//获取运行时参数
		if(runtimeParameter != null && runtimeParameter.size() >0){		
			for(Map.Entry<String,Object> paramIter : runtimeParameter.entrySet()) {
				if("accessUser".equals(paramIter.getKey())){
					accessUser = (AccessUser)paramIter.getValue();
				}
			}
		}
		if(accessUser != null){
			boolean captchaKey = captchaManage.report_isCaptcha(accessUser.getUserName());//验证码标记
			if(captchaKey ==true){
				value.put("captchaKey",UUIDUtil.getUUID32());//是否有验证码
			}
		}
		
		
		
		SystemSetting systemSetting = settingService.findSystemSetting_cache();
		
		//如果全局不允许提交问题
		if(systemSetting.isAllowReport() == false){
			value.put("allowReport",false);//不允许提交问题
		}else{
			value.put("allowReport",true);//允许提交问题
		}
		value.put("reportTypeList", reportTypeList());
		value.put("reportMaxImageUpload", systemSetting.getReportMaxImageUpload());//图片允许最大上传数量
		value.put("fileSystem", fileManage.getFileSystem());
		return value;
	}
	
	
	/**
	 * 分类列表
	 * @param forum
	 */
	private List<ReportType> reportTypeList(){
		List<ReportType> reportTypeList =  reportTypeService.findAllReportType_cache();
		List<ReportType> new_reportTypeList = new ArrayList<ReportType>();//排序后标签
		
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
		return new_reportTypeList;
	}
	
}
