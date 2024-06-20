package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.like.LikeConfig;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 6.2升级到6.3版本执行程序
 *
 */
public class Upgrade6_2to6_3 {
	private static final Logger logger = LogManager.getLogger(Upgrade6_2to6_3.class);
	
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
    			insertSQL_disableusername(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表disableusername插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    			
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			
    			
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			updateSQL_like(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表like修改SQL成功",1))+",");
    			
    			
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
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
	private static void insertSQL_disableusername(UpgradeService upgradeService){
		String sql = "INSERT INTO `disableusername` (`id`,`name`) VALUES (NULL,'*@*'),(NULL,'*#*'),(NULL,'*!*'),(NULL,'*\\\\*');";
		
		upgradeService.insertNativeSQL(sql);
	}
		
	/**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
	private static void insertSQL_syspermission(UpgradeService upgradeService){
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('eaa9d9d6a58b4e999ced0cd59194968a','GET','AUTH_7c539b8a982e45339ce7fb108e7b67ad_GET_READ',1,'问答点赞用户');";
		
		upgradeService.insertNativeSQL(sql);
		
	}
	
	
	
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'74e6343d6b144a24a219185e6fab2f4f','faa4855e9fbd4e208da026cfdfffd4f0'),(NULL,'eaa9d9d6a58b4e999ced0cd59194968a','7c539b8a982e45339ce7fb108e7b67ad');";
		
		upgradeService.insertNativeSQL(sql);

	}
	
	
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('faa4855e9fbd4e208da026cfdfffd4f0','会员管理','查询用户',3,NULL,'/control/user/queryUser*','c459ab7aa99e4f528c961dd45df4d8e5',1),('7c539b8a982e45339ce7fb108e7b67ad','问答管理','问答点赞用户',20680,'','/control/questionLike/list*',NULL,NULL);";
		
		
		upgradeService.insertNativeSQL(sql);
	
	}
	
	/**
	 * 修改like表字段
	 * @param upgradeService
	 */
	private static void updateSQL_like(UpgradeService upgradeService){
    	LikeConfig likeConfig = (LikeConfig)SpringConfigTool.getContext().getBean("likeConfig");
    	//表编号
		int like_tableNumber = likeConfig.getTableQuantity();
		for(int i = 0; i<like_tableNumber; i++){
			String updateSQL = "UPDATE like_"+i+" SET module = 10;";
	    	upgradeService.insertNativeSQL(updateSQL);
		}
    }
}
