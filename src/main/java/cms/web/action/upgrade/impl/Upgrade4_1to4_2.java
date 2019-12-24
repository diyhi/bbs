package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.favorite.FavoritesConfig;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 4.1升级到4.2版本执行程序
 *
 */
public class Upgrade4_1to4_2 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	FavoritesConfig favoritesConfig = (FavoritesConfig)SpringConfigTool.getContext().getBean("favoritesConfig");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			
    			
    			//插入升级SQL
    			Templates templates =templateService.findTemplatebyDirName("default");//查询模板
    			if(templates != null){
    				
    				insertSQL_forum(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
    				
    				insertSQL_layout(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
    			}
    			
    			
    			
    			updateSQL_question(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改question表字段",1))+",");
    	    	
    	    	deleteIndex_question(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除question表question_2_idx,question_3_idx索引",1))+",");
    	    	
    	    	deleteIndex_topic(upgradeService);
    	    	upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除topic表topic_4_idx索引",1))+",");
    	    	

    	    	insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    	    	
    	    	updateSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改设置表(systemsetting)字段成功",1))+",");
    			
    			
    			updateSQL_favorites(upgradeService,favoritesConfig);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改Favorites收藏夹字段",1))+",");
    		
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
    private static void insertSQL_forum(UpgradeService upgradeService){
       	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'加入收藏夹','收藏夹',1,'blank_9.html','d7e5fcb22e1b463cb2616616e42d3b63',4,'favoriteRelated_addFavorite_collection_question','加入收藏夹',0,'favoriteRelated_addFavorite_2'),(NULL,'default','entityBean',NULL,'用户是否已经收藏问题','收藏夹',1,NULL,'eccf8e7e476c4714845687037d6daf96',4,'favoriteRelated_alreadyFavoriteQuestion_entityBean_default','用户是否已收藏该问题',0,'favoriteRelated_alreadyFavoriteQuestion_1'),(NULL,'default','entityBean',NULL,'问题会员收藏总数','收藏夹',1,NULL,'843e3bf4f6b645dca42522ee2779d691',4,'favoriteRelated_questionFavoriteCount_entityBean_default','用户问题收藏总数',0,'favoriteRelated_questionFavoriteCount_1');";
       	upgradeService.insertNativeSQL(sql);

    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('843e3bf4f6b645dca42522ee2779d691','default',-1,NULL,'查询用户问题收藏总数','queryQuestionFavoriteCount',1,2120,4),('d40efbb845cc446ea520eec7e732cf4f','default',-1,'questionFavoriteList.html','问题收藏列表','user/control/questionFavoriteList',0,2100,1),('eccf8e7e476c4714845687037d6daf96','default',-1,NULL,'查询用户是否已收藏问题','queryAlreadyFavoriteQuestion',1,2110,4);";
        upgradeService.insertNativeSQL(sql);
   	
        
        
    }
    
  
       /**
      	 * 插入升级SQL
      	 * @param upgradeService
      	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('764fd678f9894d759a278734fb63d460','GET','AUTH_0e17c398c3dc46ef90657028e5a81b0f_GET_READ',1,'问答悬赏平台分成'),('d39647de8f5b4b2b844a4eb9fc089c8e','GET','AUTH_d8ad4df8213e491c96bd143e1eec7b4c_GET_READ',1,'收藏用户');";

        upgradeService.insertNativeSQL(sql);
    }
          
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
    	
    	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'d39647de8f5b4b2b844a4eb9fc089c8e','d8ad4df8213e491c96bd143e1eec7b4c'),(NULL,'764fd678f9894d759a278734fb63d460','0e17c398c3dc46ef90657028e5a81b0f');";
          	
        upgradeService.insertNativeSQL(sql);
    }
    
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
    	  	
    	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('0e17c398c3dc46ef90657028e5a81b0f','平台收益管理','问答悬赏平台分成',46450,'','/control/questionRewardPlatformShare/list.htm*',NULL,NULL),('d8ad4df8213e491c96bd143e1eec7b4c','问答管理','收藏用户',20620,'','/control/questionFavorite/list.htm*',NULL,NULL);";
       	
       	
       	upgradeService.insertNativeSQL(sql);
    }
    
    
    
    
    /**
   	 * 修改收藏夹字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_favorites(UpgradeService upgradeService,FavoritesConfig favoritesConfig){
    	
    	int tableQuantity = favoritesConfig.getTableQuantity();
    	for(int i =0; i<tableQuantity; i++){
    		String sql = "UPDATE favorites_"+i+" SET module = 10";
	    	upgradeService.insertNativeSQL(sql);
    	}
    }
    
    /**
   	 * 修改问题字段
   	 * @param upgradeService
   	 */
    private static void updateSQL_question(UpgradeService upgradeService){
    	String sql = "UPDATE question SET amount=0,point=0 ";
       	upgradeService.insertNativeSQL(sql);
    }
    
    /**
   	 * 删除question表question_2_idx,question_3_idx索引
   	 * @param upgradeService
   	 */
    private static void deleteIndex_question(UpgradeService upgradeService){
    	
    	String sql_1 = "ALTER TABLE question DROP INDEX question_2_idx";
    	upgradeService.insertNativeSQL(sql_1);
    	
    	String sql_2 = "ALTER TABLE question DROP INDEX question_3_idx";
    	upgradeService.insertNativeSQL(sql_2);

    }
    /**
   	 * 删除topic表topic_4_idx索引
   	 * @param upgradeService
   	 */
    private static void deleteIndex_topic(UpgradeService upgradeService){
    	
    	String sql_1 = "ALTER TABLE topic DROP INDEX topic_4_idx";
    	upgradeService.insertNativeSQL(sql_1);

    }
    
    /**
	 * 修改systemsetting表字段
	 * @param upgradeService
	 */
    private static void updateSQL_systemsetting(UpgradeService upgradeService){
    	String sql = "UPDATE systemsetting SET questionRewardPlatformShareProportion = 0;";
    	upgradeService.insertNativeSQL(sql);
    }
}
