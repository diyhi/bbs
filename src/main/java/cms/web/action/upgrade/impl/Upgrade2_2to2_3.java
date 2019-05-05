package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;

import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.FileManage;
import cms.web.action.upgrade.UpgradeManage;

/**
 * 2.2升级到2.3版本执行程序
 *
 */
public class Upgrade2_2to2_3 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	FileManage fileManage = (FileManage)SpringConfigTool.getContext().getBean("fileManage");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	
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
    			
    			updateSQL_userloginlog(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改登录日志(userloginlog)字段成功",1))+",");
    			
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			updateSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources更新SQL成功",1))+",");
    			
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成，需要再次重启服务器新增的权限功能才能正常使用",1))+",");
				//写入当前BBS版本
				fileManage.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt",upgradeSystem.getId(),"utf-8",false);
				
				//临时目录路径
				String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				//删除临时文件夹
				fileManage.removeDirectory(temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator);
				
    		}
    		
    		
    		
    		
    	}
    	upgradeManage.taskRunMark_delete();
    }
    
    /**
	 * 修改登录日志字段
	 * @param upgradeService
	 */
    private static void updateSQL_userloginlog(UpgradeService upgradeService){
    	String userloginlog_sql = "UPDATE userloginlog_0 SET typeNumber = 10;";
    	upgradeService.insertNativeSQL(userloginlog_sql);
    	String userloginlog_1_sql = "UPDATE userloginlog_1 SET typeNumber = 10;";
    	upgradeService.insertNativeSQL(userloginlog_1_sql);
    	String userloginlog_2_sql = "UPDATE userloginlog_2 SET typeNumber = 10;";
    	upgradeService.insertNativeSQL(userloginlog_2_sql);
    	String userloginlog_3_sql = "UPDATE userloginlog_3 SET typeNumber = 10;";
    	upgradeService.insertNativeSQL(userloginlog_3_sql);
    	
    	
    }
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
    	String sql = " INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (28,'default','entityBean',NULL,'话题会员收藏总数','收藏夹',1,NULL,'5b37a52dc0604914881d767664f39ca2',4,'favoriteRelated_favoriteCount_entityBean_default','查询话题会员收藏总数',0,'favoriteRelated_favoriteCount_1'),(29,'default','entityBean',NULL,'用户是否已经收藏话题','收藏夹',1,NULL,'4a9681cc36d54a88be3ca330c28d1675',4,'favoriteRelated_alreadyCollected_entityBean_default','查询用户是否已收藏话题',0,'favoriteRelated_alreadyCollected_1'),(30,'default','collection',NULL,'加入收藏夹','收藏夹',1,'blank_1.html','7747c7fa3dd6451783f94d01f1678a0b',4,'favoriteRelated_addFavorite_collection_default','加入收藏夹',0,'favoriteRelated_addFavorite_1');";
    	
    	upgradeService.insertNativeSQL(sql);
		
    	
    }
    
   
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
       private static void insertSQL_layout(UpgradeService upgradeService){
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('4a9681cc36d54a88be3ca330c28d1675','default',-1,NULL,'查询用户是否已收藏话题','queryAlreadyCollected',1,720,4),('2f0b565a37f24ed5a874ee587dda4394','default',-1,'topicFavoriteList.html','话题收藏列表','user/control/topicFavoriteList',0,700,1),('5b37a52dc0604914881d767664f39ca2','default',-1,NULL,'查询话题会员收藏总数','queryFavoriteCount',1,710,4),('7f92073f42c84b67bf89b64b3ba3f5d1','default',-1,'favoriteList.html','收藏夹列表','user/control/favoriteList',0,600,1);";
       	
       	upgradeService.insertNativeSQL(sql);
   		
       	
       }
    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('fb848a5fa3bf46919f54044aa3bb7c6f','GET','AUTH_4c891b4c92af4f6783e6b7d1b245e345_GET_READ',1,'收藏夹列表');";
    	
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (1430,'fb848a5fa3bf46919f54044aa3bb7c6f','4c891b4c92af4f6783e6b7d1b245e345');";
    	
    	upgradeService.insertNativeSQL(sql);
    }
    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
    	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('4c891b4c92af4f6783e6b7d1b245e345','会员管理','收藏夹列表',27460,'','/control/favorite/list*',NULL,NULL);";
 	
    	upgradeService.insertNativeSQL(sql);
    }
	
    
    /**
	 * 修改SQL
	 * @param upgradeService
	 */
    private static void updateSQL_sysresources(UpgradeService upgradeService){
    	//('77b55fcc33184a009e1376e20e294eca','模板管理','查询商品分页',6,NULL,'/control/product/manage.htm?method=ajax_searchProductInfoPage&*','ef3e2b9c32634f4088f4066880c70677',1)

    	String sql_1 = "UPDATE sysresources SET url = '/control/tag/manage.htm?method=allTag&*',name='查询话题标签' WHERE id = '77b55fcc33184a009e1376e20e294eca';";
    	upgradeService.insertNativeSQL(sql_1);
    	
    	
    	//('ec17504e564f4c958cb679377a39715d','模板管理','查询商品分页',7,NULL,'/control/product/manage.htm?method=ajax_searchProductInfoPage&*','922f6908c5a1434aba4b0f6f8f008c1c',1)   
    	String sql_2 = "UPDATE sysresources SET url = '/control/tag/manage.htm?method=allTag&*',name='查询话题标签' WHERE id = 'ec17504e564f4c958cb679377a39715d';";
    	upgradeService.insertNativeSQL(sql_2);

   	
    }
}
