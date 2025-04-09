package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
 * 6.6升级到6.7版本执行程序
 *
 */
public class Upgrade6_6to6_7 {
	private static final Logger logger = LogManager.getLogger(Upgrade6_6to6_7.class);
	
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
    			
    			updateSQL_user(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表user修改SQL运行成功",1))+",");
    			
    			
    		    updateSQL_systemsetting(upgradeService);
    		    upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改systemsetting表字段allowRegisterAccountType和allowLoginAccountType",1))+",");
    			
    			
    			deleteSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除systemsetting表字段allowRegisterAccount",1))+",");
    			
    			
    			
    			updateSQL_tag(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改tag表字段childNodeNumber, parentId, parentIdGroup",1))+",");
    			
    			
    			updateSQL_topic(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改topic表字段tagIdGroup",1))+",");
    			
    			
    			
    			deleteIndex_topic(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除topic表topic_idx索引",1))+",");
    	    	
    			
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
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	
    	List<Object[]> objectList = upgradeService.queryNativeSQL("select allowRegisterAccount from systemsetting where id=1;");
    	if(objectList != null && objectList.size() >0){
 		   for(int o = 0; o<objectList.size(); o++){
 				Object object = objectList.get(o);
 				String allowRegisterAccount = ObjectConversion.conversion(object, ObjectConversion.STRING);
 				
 				//允许注册账号类型 
 				List<Integer> allowRegisterAccountType = new ArrayList<Integer>();
 				//允许登录账号类型 
 				List<Integer> allowLoginAccountType = new ArrayList<Integer>();
 				
 				
 				Map<String,Object> content_map = JsonUtils.toObject(allowRegisterAccount.trim(), HashMap.class);
				if(content_map != null && content_map.size() >0){
					for(Map.Entry<String, Object> entry : content_map.entrySet()) {
						if(entry.getKey().equalsIgnoreCase("local")){//本地账号密码用户
							if(entry.getValue().equals(true)){
								allowRegisterAccountType.add(10);
								allowLoginAccountType.add(10);
							}
						}else if(entry.getKey().equalsIgnoreCase("mobile")){//手机用户
							if(entry.getValue().equals(true)){
								allowRegisterAccountType.add(20);
								allowLoginAccountType.add(20);
							}
						}else if(entry.getKey().equalsIgnoreCase("weChat")){//微信用户
							if(entry.getValue().equals(true)){
								allowRegisterAccountType.add(40);
								allowLoginAccountType.add(40);
							}
						}else if(entry.getKey().equalsIgnoreCase("other")){//其他用户
							if(entry.getValue().equals(true)){
								allowRegisterAccountType.add(80);
								allowLoginAccountType.add(80);
							}
						}
					}
				}
 				
 				
 				
 					
 				String updateSQL = "UPDATE systemsetting SET allowRegisterAccountType = '"+JsonUtils.toJSONString(allowRegisterAccountType)+"',allowLoginAccountType = '"+JsonUtils.toJSONString(allowLoginAccountType)+"' WHERE id=1;";
 		    	upgradeService.insertNativeSQL(updateSQL);

 				break;
 		   }
 	   }
    	
    	
    }
    
    
    /**
   	 * 修改user表字段长度
   	 * @param upgradeService
   	 */
    private static void updateSQL_user(UpgradeService upgradeService){
    	// alter table 表名 modify column 列名 类型(要修改的长度);
    	String sql = "alter table user modify column email varchar(100);";
    	upgradeService.insertNativeSQL(sql);
    }
    
    
    /**
   	 * 删除systemsetting表字段
   	 * @param upgradeService
   	 */
    private static void deleteSQL_systemsetting(UpgradeService upgradeService){
    	 //删除字段
    	String deleteField = "alter table systemsetting drop allowRegisterAccount";
    	upgradeService.insertNativeSQL(deleteField);
    }
    
    
    
	
	 /**
   	 * 修改tag表字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_tag(UpgradeService upgradeService){
    	String updateSQL = "UPDATE tag SET childNodeNumber=0, parentId=0, parentIdGroup = ',0,'";
	    upgradeService.insertNativeSQL(updateSQL);

    }
 
    /**
   	 * 删除topic表topic_idx索引
   	 * @param upgradeService
   	 */
    private static void deleteIndex_topic(UpgradeService upgradeService){
    	
    	String sql_1 = "ALTER TABLE topic DROP INDEX topic_idx";
    	upgradeService.insertNativeSQL(sql_1);

    }
    
    /**
   	 * 修改topic表字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_topic(UpgradeService upgradeService){
    	String updateSQL = "UPDATE topic SET tagIdGroup= CONCAT(',0,', IFNULL(tagId,''), ',')";
	    upgradeService.insertNativeSQL(updateSQL);

    }
}
