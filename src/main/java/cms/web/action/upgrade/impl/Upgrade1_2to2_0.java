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
 * 1.2升级到2.0版本执行程序
 *
 */
public class Upgrade1_2to2_0 {
	
	
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
    				insertSQL_layout(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
    			}
    			
    			
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			
    			insertSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表systemsetting修改字段成功",1))+",");
    			
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
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String layout_sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('a4a0399a222f48039c84acd45e4df52f','default',-1,'addPrivateMessage.html','添加私信','user/control/addPrivateMessage',0,450,1),('acbffd6b80804f58a3457edf57b5ee1d','default',-1,'privateMessageChatList.html','私信对话列表','user/control/privateMessageChatList',0,400,1),('b5b0e4b3c19145b497bc7cb529bf2519','default',-1,'privateMessageList.html','私信列表','user/control/privateMessageList',0,350,1),('ba85aac8b3924ea980ad39087d0a722f','default',-1,'systemNotifyList.html','系统通知列表','user/control/systemNotifyList',0,500,1);";
    	
    	upgradeService.insertNativeSQL(layout_sql);
		
    	
    }
    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermission(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('694588ee74f9488ab0d647e6e722ccc4','GET','AUTH_01fe645ae71e4925892c159e948063e9_GET_READ',1,'私信列表'),('13f7169f2a0b48a591e701d5f5a034d0','GET','AUTH_2bf084e1e8d84e89a1c2da9c5f6c8f5d_GET_READ',1,'私信对话列表'),('da0dad28fa434e9190fe67619c28bf59','POST','AUTH_c418c587feb14ecfabd9690d9f52d440_POST_UPDATE',1,'还原私信'),('4174d434346d4043935285874652531e','POST','AUTH_a5cb52cd59a040a1b34a7f401361fe31_POST_DELETE',1,'删除私信'),('958dd7d75ef547ea8ea0af5ed2a816c1','GET','AUTH_dcd7b0f0c5e44566b116694b96dac0e1_GET_READ',1,'系统通知列表'),('36001fdc50254da8a40de770517d148e','POST','AUTH_7caf26294048452cafbb5972ef502d88_POST_UPDATE',1,'还原系统通知'),('cd62c57b27af4b95bf3e9b868c318e98','GET','AUTH_399207e1956d465a8eb536e80ee5f5f0_GET_READ',1,'系统通知列表'),('87af232710634e8ebab31d8d432c51da','GET','AUTH_afa39379327e4461b3444cf9b416efae_GET_READ',1,'添加系统通知页'),('657d2b2dcbd44934aad101f1e304e465','POST','AUTH_afa39379327e4461b3444cf9b416efae_POST_ADD',2,'添加系统通知'),('447a7075ee9241b4b39e8976aa24709d','GET','AUTH_aa355abf953e42078517a3dded03e785_GET_READ',1,'修改系统通知页'),('3d795b3a0b1849fb8da486151cba9b8b','POST','AUTH_aa355abf953e42078517a3dded03e785_POST_UPDATE',2,'修改系统通知'),('b343696f562c437facda8fb136de09c9','POST','AUTH_c370e4821b0e45b6946a7ab3181b3844_POST_DELETE',1,'删除系统通知');";
    	
    	upgradeService.insertNativeSQL(sql);
    }
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
    	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (1409,'694588ee74f9488ab0d647e6e722ccc4','01fe645ae71e4925892c159e948063e9'),(1410,'13f7169f2a0b48a591e701d5f5a034d0','2bf084e1e8d84e89a1c2da9c5f6c8f5d'),(1411,'da0dad28fa434e9190fe67619c28bf59','c418c587feb14ecfabd9690d9f52d440'),(1412,'4174d434346d4043935285874652531e','a5cb52cd59a040a1b34a7f401361fe31'),(1413,'958dd7d75ef547ea8ea0af5ed2a816c1','dcd7b0f0c5e44566b116694b96dac0e1'),(1414,'36001fdc50254da8a40de770517d148e','7caf26294048452cafbb5972ef502d88'),(1415,'cd62c57b27af4b95bf3e9b868c318e98','399207e1956d465a8eb536e80ee5f5f0'),(1416,'87af232710634e8ebab31d8d432c51da','afa39379327e4461b3444cf9b416efae'),(1417,'657d2b2dcbd44934aad101f1e304e465','afa39379327e4461b3444cf9b416efae'),(1418,'447a7075ee9241b4b39e8976aa24709d','aa355abf953e42078517a3dded03e785'),(1419,'3d795b3a0b1849fb8da486151cba9b8b','aa355abf953e42078517a3dded03e785'),(1420,'b343696f562c437facda8fb136de09c9','c370e4821b0e45b6946a7ab3181b3844');";
    	
    	upgradeService.insertNativeSQL(sql);
    }
    
    
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
    	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('01fe645ae71e4925892c159e948063e9','会员管理','私信列表',27000,'','/control/privateMessage/manage.htm?method=privateMessageList&*',NULL,NULL),('2bf084e1e8d84e89a1c2da9c5f6c8f5d','会员管理','私信对话列表',27100,'','/control/privateMessage/manage.htm?method=privateMessageChatList&*',NULL,NULL),('c418c587feb14ecfabd9690d9f52d440','会员管理','还原私信',27150,'','/control/privateMessage/manage.htm?method=reductionPrivateMessage&*',NULL,NULL),('a5cb52cd59a040a1b34a7f401361fe31','会员管理','删除私信',27200,'','/control/privateMessage/manage.htm?method=deletePrivateMessageChat&*',NULL,NULL),('dcd7b0f0c5e44566b116694b96dac0e1','会员管理','系统通知列表',27300,'','/control/systemNotify/manage.htm?method=subscriptionSystemNotifyList&*',NULL,NULL),('7caf26294048452cafbb5972ef502d88','会员管理','还原系统通知',27350,'','/control/systemNotify/manage.htm?method=reductionSubscriptionSystemNotify&*',NULL,NULL),('399207e1956d465a8eb536e80ee5f5f0','系统通知管理','系统通知列表',46000,'','/control/systemNotify/list.htm*',NULL,NULL),('afa39379327e4461b3444cf9b416efae','系统通知管理','添加系统通知',46100,'','/control/systemNotify/manage.htm?method=add*',NULL,NULL),('aa355abf953e42078517a3dded03e785','系统通知管理','修改系统通知',46200,'','/control/systemNotify/manage.htm?method=edit*',NULL,NULL),('c370e4821b0e45b6946a7ab3181b3844','系统通知管理','删除系统通知',46300,'','/control/systemNotify/manage.htm?method=delete*',NULL,NULL);";
 	
    	upgradeService.insertNativeSQL(sql);
    }
	
    /**
	 * 插入升级SQL
	 * @param upgradeService
	 */
    private static void insertSQL_systemsetting(UpgradeService upgradeService){
    	String sql = "UPDATE systemsetting SET privateMessage_submitQuantity=10 WHERE id=1;";
    	upgradeService.insertNativeSQL(sql);
    }
	
	
   
	
	
	
	
	
	
	
	
	/**
	INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('a4a0399a222f48039c84acd45e4df52f','default',-1,'addPrivateMessage.html','添加私信','user/control/addPrivateMessage',0,450,1),('acbffd6b80804f58a3457edf57b5ee1d','default',-1,'privateMessageChatList.html','私信对话列表','user/control/privateMessageChatList',0,400,1),('b5b0e4b3c19145b497bc7cb529bf2519','default',-1,'privateMessageList.html','私信列表','user/control/privateMessageList',0,350,1),('ba85aac8b3924ea980ad39087d0a722f','default',-1,'systemNotifyList.html','系统通知列表','user/control/systemNotifyList',0,500,1);

	
	INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES 
	('694588ee74f9488ab0d647e6e722ccc4','GET','AUTH_01fe645ae71e4925892c159e948063e9_GET_READ',1,'私信列表'),
	('13f7169f2a0b48a591e701d5f5a034d0','GET','AUTH_2bf084e1e8d84e89a1c2da9c5f6c8f5d_GET_READ',1,'私信对话列表'),
	('da0dad28fa434e9190fe67619c28bf59','POST','AUTH_c418c587feb14ecfabd9690d9f52d440_POST_UPDATE',1,'还原私信'),
	('4174d434346d4043935285874652531e','POST','AUTH_a5cb52cd59a040a1b34a7f401361fe31_POST_DELETE',1,'删除私信'),
	('958dd7d75ef547ea8ea0af5ed2a816c1','GET','AUTH_dcd7b0f0c5e44566b116694b96dac0e1_GET_READ',1,'系统通知列表'),
	('36001fdc50254da8a40de770517d148e','POST','AUTH_7caf26294048452cafbb5972ef502d88_POST_UPDATE',1,'还原系统通知'),
	('cd62c57b27af4b95bf3e9b868c318e98','GET','AUTH_399207e1956d465a8eb536e80ee5f5f0_GET_READ',1,'系统通知列表'),
	('87af232710634e8ebab31d8d432c51da','GET','AUTH_afa39379327e4461b3444cf9b416efae_GET_READ',1,'添加系统通知页'),
	('657d2b2dcbd44934aad101f1e304e465','POST','AUTH_afa39379327e4461b3444cf9b416efae_POST_ADD',2,'添加系统通知'),
	('447a7075ee9241b4b39e8976aa24709d','GET','AUTH_aa355abf953e42078517a3dded03e785_GET_READ',1,'修改系统通知页'),
	('3d795b3a0b1849fb8da486151cba9b8b','POST','AUTH_aa355abf953e42078517a3dded03e785_POST_UPDATE',2,'修改系统通知'),
	('b343696f562c437facda8fb136de09c9','POST','AUTH_c370e4821b0e45b6946a7ab3181b3844_POST_DELETE',1,'删除系统通知');
	
	
	
	
	INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (1409,'694588ee74f9488ab0d647e6e722ccc4','01fe645ae71e4925892c159e948063e9'),(1410,'13f7169f2a0b48a591e701d5f5a034d0','2bf084e1e8d84e89a1c2da9c5f6c8f5d'),(1411,'da0dad28fa434e9190fe67619c28bf59','c418c587feb14ecfabd9690d9f52d440'),(1412,'4174d434346d4043935285874652531e','a5cb52cd59a040a1b34a7f401361fe31'),(1413,'958dd7d75ef547ea8ea0af5ed2a816c1','dcd7b0f0c5e44566b116694b96dac0e1'),(1414,'36001fdc50254da8a40de770517d148e','7caf26294048452cafbb5972ef502d88'),(1415,'cd62c57b27af4b95bf3e9b868c318e98','399207e1956d465a8eb536e80ee5f5f0'),(1416,'87af232710634e8ebab31d8d432c51da','afa39379327e4461b3444cf9b416efae'),(1417,'657d2b2dcbd44934aad101f1e304e465','afa39379327e4461b3444cf9b416efae'),(1418,'447a7075ee9241b4b39e8976aa24709d','aa355abf953e42078517a3dded03e785'),(1419,'3d795b3a0b1849fb8da486151cba9b8b','aa355abf953e42078517a3dded03e785'),(1420,'b343696f562c437facda8fb136de09c9','c370e4821b0e45b6946a7ab3181b3844');
	
	
	
	INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES 
	('01fe645ae71e4925892c159e948063e9','会员管理','私信列表',27000,'','/control/privateMessage/manage.htm?method=privateMessageList&*',NULL,NULL),
	('2bf084e1e8d84e89a1c2da9c5f6c8f5d','会员管理','私信对话列表',27100,'','/control/privateMessage/manage.htm?method=privateMessageChatList&*',NULL,NULL),
	('c418c587feb14ecfabd9690d9f52d440','会员管理','还原私信',27150,'','/control/privateMessage/manage.htm?method=reductionPrivateMessage&*',NULL,NULL),
	('a5cb52cd59a040a1b34a7f401361fe31','会员管理','删除私信',27200,'','/control/privateMessage/manage.htm?method=deletePrivateMessageChat&*',NULL,NULL),
	('dcd7b0f0c5e44566b116694b96dac0e1','会员管理','系统通知列表',27300,'','/control/systemNotify/manage.htm?method=subscriptionSystemNotifyList&*',NULL,NULL),
	('7caf26294048452cafbb5972ef502d88','会员管理','还原系统通知',27350,'','/control/systemNotify/manage.htm?method=reductionSubscriptionSystemNotify&*',NULL,NULL),
	('399207e1956d465a8eb536e80ee5f5f0','系统通知管理','系统通知列表',46000,'','/control/systemNotify/list.htm*',NULL,NULL),
	('afa39379327e4461b3444cf9b416efae','系统通知管理','添加系统通知',46100,'','/control/systemNotify/manage.htm?method=add*',NULL,NULL),
	('aa355abf953e42078517a3dded03e785','系统通知管理','修改系统通知',46200,'','/control/systemNotify/manage.htm?method=edit*',NULL,NULL),
	('c370e4821b0e45b6946a7ab3181b3844','系统通知管理','删除系统通知',46300,'','/control/systemNotify/manage.htm?method=delete*',NULL,NULL);
	
	**/
	
	
	
}
