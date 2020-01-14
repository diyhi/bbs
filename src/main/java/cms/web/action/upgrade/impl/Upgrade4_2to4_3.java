package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cms.bean.setting.EditorTag;
import cms.bean.setting.SystemSetting;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.setting.SettingService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 4.2升级到4.3版本执行程序
 *
 */
public class Upgrade4_2to4_3 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	SettingService settingService = (SettingService)SpringConfigTool.getContext().getBean("settingServiceBean");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			
    			
    	    	updateSQL_systemsetting(upgradeService,settingService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改设置表(systemsetting)字段成功",1))+",");
    			
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");
				//写入当前BBS版本
				FileUtil.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt",upgradeSystem.getId(),"utf-8",false);
				
				//临时目录路径
				String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				//删除临时文件夹
				localFileManage.removeDirectory(temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator);
				
    		}
    		
    		
    		
    		
    	}
    	upgradeManage.taskRunMark_delete();
    }
    


    
    /**
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService,SettingService settingService){
    	SystemSetting systemSite = settingService.findSystemSetting();
    	
		if(systemSite.getEditorTag() != null && !"".equals(systemSite.getEditorTag().trim())){
			EditorTag editorTag = JsonUtils.toObject(systemSite.getEditorTag(), EditorTag.class);
			if(editorTag != null){
				//转为小写
				List<String> imageFormatList = editorTag.getImageFormat();
				
				List<String> lowerCase_imageFormatList = new ArrayList<String>();
				if(imageFormatList != null && imageFormatList.size() >0){
					for(String imageFormat :imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							lowerCase_imageFormatList.add(imageFormat.toLowerCase());
						}
					}
				}
				editorTag.setImageFormat(lowerCase_imageFormatList);
				systemSite.setEditorTagObject(editorTag);
			}
		}
		
		if(systemSite.getTopicEditorTag() != null && !"".equals(systemSite.getTopicEditorTag().trim())){
			EditorTag editorTag = JsonUtils.toObject(systemSite.getTopicEditorTag(), EditorTag.class);
			if(editorTag != null){
				//转为小写
				List<String> imageFormatList = editorTag.getImageFormat();
				
				List<String> lowerCase_imageFormatList = new ArrayList<String>();
				if(imageFormatList != null && imageFormatList.size() >0){
					for(String imageFormat :imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							lowerCase_imageFormatList.add(imageFormat.toLowerCase());
						}
					}
				}
				editorTag.setImageFormat(lowerCase_imageFormatList);
				systemSite.setTopicEditorTagObject(editorTag);
			}
		}
		if(systemSite.getQuestionEditorTag() != null && !"".equals(systemSite.getQuestionEditorTag().trim())){
			EditorTag editorTag = JsonUtils.toObject(systemSite.getQuestionEditorTag(), EditorTag.class);
			if(editorTag != null){
				//转为小写
				List<String> imageFormatList = editorTag.getImageFormat();
				
				List<String> lowerCase_imageFormatList = new ArrayList<String>();
				if(imageFormatList != null && imageFormatList.size() >0){
					for(String imageFormat :imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							lowerCase_imageFormatList.add(imageFormat.toLowerCase());
						}
					}
				}
				editorTag.setImageFormat(lowerCase_imageFormatList);
				systemSite.setQuestionEditorTagObject(editorTag);
			}
		}
		if(systemSite.getAnswerEditorTag() != null && !"".equals(systemSite.getAnswerEditorTag().trim())){
			EditorTag editorTag = JsonUtils.toObject(systemSite.getAnswerEditorTag(), EditorTag.class);
			if(editorTag != null){
				//转为小写
				List<String> imageFormatList = editorTag.getImageFormat();
				
				List<String> lowerCase_imageFormatList = new ArrayList<String>();
				if(imageFormatList != null && imageFormatList.size() >0){
					for(String imageFormat :imageFormatList){
						if(imageFormat != null && !"".equals(imageFormat.trim())){
							lowerCase_imageFormatList.add(imageFormat.toLowerCase());
						}
					}
				}
				editorTag.setImageFormat(lowerCase_imageFormatList);
				systemSite.setAnswerEditorTagObject(editorTag);
			}
		}
    	
    	
		systemSite.setTopicEditorTag(JsonUtils.toJSONString(systemSite.getTopicEditorTagObject()));
		systemSite.setEditorTag(JsonUtils.toJSONString(systemSite.getEditorTagObject()));
		systemSite.setQuestionEditorTag(JsonUtils.toJSONString(systemSite.getQuestionEditorTagObject()));
		systemSite.setAnswerEditorTag(JsonUtils.toJSONString(systemSite.getAnswerEditorTagObject()));
		
		systemSite.setId(1);
		systemSite.setVersion(new Date().getTime());
		settingService.updateSystemSetting(systemSite);
    }
}
