package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.ObjectConversion;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 5.0升级到5.1版本执行程序
 *
 */
public class Upgrade5_0to5_1 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			
    			updateSQL_sysresources_url(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改sysresources表字段(将url字段的后缀删除)成功",1))+",");
    			
    			updateSQL_sysresources_framework(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改sysresources表字段(修改url字段的后台框架地址)成功",1))+",");
    			
    						
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
	 * 修改sysresources表字段(将url字段的后缀删除)
	 * @param upgradeService
	 */
    public static void updateSQL_sysresources_url(UpgradeService upgradeService){
    	List<Object[]> objectList = upgradeService.queryNativeSQL("select id,url from sysresources");
		   if(objectList != null && objectList.size() >0){
			   for(int o = 0; o<objectList.size(); o++){
					Object[] object = objectList.get(o);
					String id = ObjectConversion.conversion(object[0], ObjectConversion.STRING);
					String url = ObjectConversion.conversion(object[1], ObjectConversion.STRING);
					
					
					//将后缀.htm删除
					String newUrl = StringUtils.replaceOnce(url, ".htm", "");//替换指定的字符，只替换第一次出现的
					 	
					String updateSQL = "UPDATE sysresources SET url = '"+newUrl+"' WHERE id='"+id+"';";
				    upgradeService.insertNativeSQL(updateSQL);
			   }
		}
    }
    
    /**
	 * 修改sysresources表字段(修改url字段的后台框架地址)
	 * @param upgradeService
	 */
    public static void updateSQL_sysresources_framework(UpgradeService upgradeService){
    	String updateSQL_1 = "UPDATE sysresources SET url = '/control/manage/index*' WHERE url='/control/center/admin*';";
    	upgradeService.insertNativeSQL(updateSQL_1);
    	
    	String updateSQL_2 = "UPDATE sysresources SET url = '/control/manage/home*' WHERE url='/control/center/home*';";
    	upgradeService.insertNativeSQL(updateSQL_2);
    }
    
    
    
}
