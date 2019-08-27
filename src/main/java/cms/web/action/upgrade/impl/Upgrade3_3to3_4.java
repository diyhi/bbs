package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.JsonUtils;
import cms.utils.ObjectConversion;
import cms.utils.SpringConfigTool;
import cms.web.action.FileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 3.3升级到3.4版本执行程序
 *
 */
public class Upgrade3_3to3_4 {
	
	
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

    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			
    			updateSQL_systemsetting(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改systemsetting表 topic_review comment_review reply_review 字段成功",1))+",");
    			
    			
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");
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
    private static void insertSQL_syspermission(UpgradeService upgradeService){
       	String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('1e5cbc88d0d8489cabf925e70094b882','POST','AUTH_838e778a22c0424ab11914d1e3b44379_POST_UPDATE',2,'修改角色'),('6716c5bd261d4808ba41700b69f061e8','POST','AUTH_7ed8aa6ff34241329b9183e2f114ca64_POST_ADD',2,'添加角色'),('6b0484a6e0a54374b82846816e108b46','GET','AUTH_838e778a22c0424ab11914d1e3b44379_GET_READ',1,'修改角色页'),('dbc37f628d514de99469585bb89d4a45','POST','AUTH_31f98ef57f0a48a69c0ebb36c920dd1f_POST_DELETE',1,'删除角色'),('ecd38983cce54e65b9b465c70c9c484e','GET','AUTH_7ed8aa6ff34241329b9183e2f114ca64_GET_READ',1,'添加角色页'),('edcfef9b3d58434cbccebb85cc192bf7','GET','AUTH_803dc508fa8542f1a2be86cdb85b19f1_GET_READ',1,'会员角色列表');";

       	
       	upgradeService.insertNativeSQL(sql);
    }
       
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
       	String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'edcfef9b3d58434cbccebb85cc192bf7','803dc508fa8542f1a2be86cdb85b19f1'),(NULL,'ecd38983cce54e65b9b465c70c9c484e','7ed8aa6ff34241329b9183e2f114ca64'),(NULL,'6716c5bd261d4808ba41700b69f061e8','7ed8aa6ff34241329b9183e2f114ca64'),(NULL,'6b0484a6e0a54374b82846816e108b46','838e778a22c0424ab11914d1e3b44379'),(NULL,'1e5cbc88d0d8489cabf925e70094b882','838e778a22c0424ab11914d1e3b44379'),(NULL,'dbc37f628d514de99469585bb89d4a45','31f98ef57f0a48a69c0ebb36c920dd1f');";
       	
       	upgradeService.insertNativeSQL(sql);
    }
       
       
    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_sysresources(UpgradeService upgradeService){
       	String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('31f98ef57f0a48a69c0ebb36c920dd1f','会员管理','删除角色',27830,'','/control/userRole/manage.htm?method=delete*',NULL,NULL),('7ed8aa6ff34241329b9183e2f114ca64','会员管理','添加角色',27810,'','/control/userRole/manage.htm?method=add*',NULL,NULL),('803dc508fa8542f1a2be86cdb85b19f1','会员管理','会员角色列表',27800,'','/control/userRole/list*',NULL,NULL),('838e778a22c0424ab11914d1e3b44379','会员管理','修改角色',27820,'','/control/userRole/manage.htm?method=edit*',NULL,NULL);";
       	
       	
       	upgradeService.insertNativeSQL(sql);
    }

    /**
  	 * 修改systemsetting表 topic_review comment_review reply_review 字段
  	 * @param upgradeService
  	 */
   private static void updateSQL_systemsetting(UpgradeService upgradeService){
   	
	   List<Object[]> objectList = upgradeService.queryNativeSQL("select topic_defaultState,comment_defaultState,reply_defaultState from systemsetting where id=1;");
	   if(objectList != null && objectList.size() >0){
		   for(int o = 0; o<objectList.size(); o++){
				Object[] object = objectList.get(o);
				Integer topic_defaultState = ObjectConversion.conversion(object[0], ObjectConversion.INTEGER);//前台发表话题默认状态  10.待审核  20.已发布
				Integer comment_defaultState = ObjectConversion.conversion(object[1], ObjectConversion.INTEGER);//前台发表评论默认状态  10.待审核  20.已发布
				Integer reply_defaultState = ObjectConversion.conversion(object[2], ObjectConversion.INTEGER);//前台发表回复默认状态  10.待审核  20.已发布
				
				
				
				//前台发表话题审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核
				Integer topic_review = 10;
				//前台发表评论审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核
				Integer comment_review = 10;
				//前台发表回复审核   10.全部审核 20.特权会员未触发敏感词免审核 30.特权会员免审核 40.触发敏感词需审核 50.无需审核
				Integer reply_review = 10;
				
				
				if(topic_defaultState != null){
					if(topic_defaultState.equals(10)){
						topic_review = 10;
					}else if(topic_defaultState.equals(20)){
						topic_review = 50;
					}
				}
				if(comment_defaultState != null){
					if(comment_defaultState.equals(10)){
						comment_review = 10;
					}else if(comment_defaultState.equals(20)){
						comment_review = 50;
					}				
				}
				if(reply_defaultState != null){
					if(reply_defaultState.equals(10)){
						reply_review = 10;
					}else if(reply_defaultState.equals(20)){
						reply_review = 50;
					}
				}
					
				String updateSQL = "UPDATE systemsetting SET topic_review = "+topic_review+",comment_review = "+comment_review+",reply_review = "+reply_review+" WHERE id=1;";
		    	upgradeService.insertNativeSQL(updateSQL);
				
				
		    	//删除字段
		    	String deleteField = "alter table systemsetting drop topic_defaultState,drop comment_defaultState,drop reply_defaultState";
		    	upgradeService.insertNativeSQL(deleteField);

				break;
		   }
	   }
   }
    
}
