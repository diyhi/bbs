package cms.web.action.upgrade.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import cms.bean.QueryResult;
import cms.bean.data.TableInfoObject;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.bean.user.DisableUserName;
import cms.bean.user.User;
import cms.service.data.impl.MySqlDataServiceBean;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 5.9升级到6.0版本执行程序
 *
 */
public class Upgrade5_9to6_0 {
	private static final Logger logger = LogManager.getLogger(Upgrade5_9to6_0.class);
	
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
    			updateSQL_commentReply(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改reply表字段成功",1))+",");
    			
				updateSQL_answerReply(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改answerreply表字段成功",1))+",");
    			
				updateSQL_systemsetting(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改systemsetting表字段成功",1))+",");
    			
				
				insertSQL_forum(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
				
				insertSQL_layout(upgradeService);
				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
				
				
				
    			insertSQL_syspermission(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
    			
    			insertSQL_syspermissionresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
    				
    			insertSQL_sysresources(upgradeService);
    			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
    			
    			
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
   	 * 修改reply表字段
   	 * @param upgradeService
   	 */
       private static void updateSQL_commentReply(UpgradeService upgradeService){
       	String updateSQL = "UPDATE reply SET friendReplyIdGroup = ',0,', isFriendStaff = false;";
       	upgradeService.insertNativeSQL(updateSQL);
       }
       
       /**
   	 * 修改answerreply表字段
   	 * @param upgradeService
   	 */
       private static void updateSQL_answerReply(UpgradeService upgradeService){
       	String updateSQL = "UPDATE answerreply SET friendReplyIdGroup = ',0,', isFriendStaff = false;";
       	upgradeService.insertNativeSQL(updateSQL);
       }
       
       /**
   	 * 修改systemsetting表字段
   	 * @param upgradeService
   	 */
       private static void updateSQL_systemsetting(UpgradeService upgradeService){
       	String updateSQL = "UPDATE systemsetting SET allowReport = true, report_submitQuantity=5, showIpAddress=false, reportMaxImageUpload=3;";
       	upgradeService.insertNativeSQL(updateSQL);
       }
       
       /**
      	 * 插入升级SQL
      	 * @param upgradeService
      	 */
       private static void insertSQL_forum(UpgradeService upgradeService){
          	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'添加举报','举报',1,'blank_19.html','52432df32db44bd9b37f97bfd68f5dfd',4,'reportRelated_addReport_collection_default','添加举报',0,'reportRelated_addReport_1'),(NULL,'default','collection',NULL,'添加举报','举报',1,NULL,'2b91af2b82c24f568e8ce88708343023',4,'reportRelated_addReport_collection_default','添加举报',0,'reportRelated_addReport_2'),(NULL,'api','collection',NULL,'添加举报','举报',1,NULL,'c677cbc3806e4cd191523eb758c538b0',4,'reportRelated_addReport_collection_default','添加举报',0,'reportRelated_addReport_1');";
          	upgradeService.insertNativeSQL(sql);
       }
      	
      /**
     	 * 插入升级SQL
     	 * @param upgradeService
     	 */
       private static void insertSQL_layout(UpgradeService upgradeService){
       	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`,`accessRequireLogin`) VALUES ('2b91af2b82c24f568e8ce88708343023','default',-1,NULL,'添加举报(移动端)','user/queryAddReport',1,2490,4,b'0'),('52432df32db44bd9b37f97bfd68f5dfd','default',-1,'blank_19.html','添加举报','user/addReport',0,2500,4,b'0'),('7fa8d0be2433401289eddf315eda75b4','default',-1,'reportList.html','举报列表','user/control/reportList',0,2500,1,b'0'),('8033f5686cf7445cbb65fb6a0c184657','api',-1,'reportList.html','举报列表','user/control/reportList',0,2500,1,b'0'),('c677cbc3806e4cd191523eb758c538b0','api',-1,NULL,'查询举报表单','user/queryAddReport',1,2870,4,b'0');";
           upgradeService.insertNativeSQL(sql);
       }  
       
       
       /**
     	 * 插入升级SQL
     	 * @param upgradeService
     	 */
   	private static void insertSQL_syspermission(UpgradeService upgradeService){
   		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('1f0ae8efec9840f09a8b34e7acd16f5f','POST','AUTH_39adfb4206024d14ab38822555314e7a_POST_UPDATE',2,'修改举报'),('2bc126b7b8034d899bb6bf2887c86aad','POST','AUTH_4a47d671d3294386a2ac9871d4f1425d_POST_DELETE',1,'删除举报'),('4474d3586efb45278d6ede01e03865c2','GET','AUTH_f0eeb58d820b48cba5368da6b80184bf_GET_READ',1,'举报分类列表'),('563c1db3575241deb84e5209af4d9df9','GET','AUTH_0faa6ccdb1d741a99f45d8ad80d7d984_GET_READ',1,'举报列表'),('7607f3a5a4cf436e8186aefc5e49622d','POST','AUTH_d38488d83afa42b8a53e530ed870b2b5_POST_UPDATE',1,'还原举报'),('7dd2f764c9dd4af7aca79c1ac4c7d6ef','POST','AUTH_72f0a0a4b44f4f21a0edd0858a6bbcc5_POST_UPDATE',1,'举报处理'),('857de602ce344890822d00925e094269','GET','AUTH_19d55187b06946ceac2a6c929935fa68_GET_READ',1,'问答举报'),('8d30d9070cd24b5c8c42c01ab072fa47','GET','AUTH_13bc9bdcb780479bbc5cb6a43b316b27_GET_READ',1,'添加举报分类页'),('90b572963b2747ec960c020fa46a8694','GET','AUTH_a9f67a215b034107952d40f592cf1056_GET_READ',1,'话题举报'),('a39091a91e8146ba89b69577970cabfe','POST','AUTH_13bc9bdcb780479bbc5cb6a43b316b27_POST_ADD',2,'添加举报分类'),('ae95e0c103e7461f980bd9ab9fe50535','GET','AUTH_c5d0d01bf7b544828e1fbf3d61b9dd8a_GET_READ',1,'用户举报'),('afd21f54283045338293c83b9d287603','POST','AUTH_38c92008f5754e348302c79498961747_POST_DELETE',1,'删除举报分类'),('e3f8ebac87ad41559185caeaab6b5f3f','POST','AUTH_3507e6049b6843c39d8d31b6e384818f_POST_UPDATE',2,'修改举报分类'),('e9e06c93c05e4ccb9ad29c8eb7872905','GET','AUTH_3507e6049b6843c39d8d31b6e384818f_GET_READ',1,'修改举报分类页'),('ed9b869a6fec4b63a8ab2b6ad181e301','GET','AUTH_39adfb4206024d14ab38822555314e7a_GET_READ',1,'修改举报页');";
   		
   		upgradeService.insertNativeSQL(sql);
   	}
   		
   	
   	      
   	/**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
   	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
   		
   		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'90b572963b2747ec960c020fa46a8694','a9f67a215b034107952d40f592cf1056'),(NULL,'857de602ce344890822d00925e094269','19d55187b06946ceac2a6c929935fa68'),(NULL,'ae95e0c103e7461f980bd9ab9fe50535','c5d0d01bf7b544828e1fbf3d61b9dd8a'),(NULL,'4474d3586efb45278d6ede01e03865c2','f0eeb58d820b48cba5368da6b80184bf'),(NULL,'8d30d9070cd24b5c8c42c01ab072fa47','13bc9bdcb780479bbc5cb6a43b316b27'),(NULL,'a39091a91e8146ba89b69577970cabfe','13bc9bdcb780479bbc5cb6a43b316b27'),(NULL,'e9e06c93c05e4ccb9ad29c8eb7872905','3507e6049b6843c39d8d31b6e384818f'),(NULL,'e3f8ebac87ad41559185caeaab6b5f3f','3507e6049b6843c39d8d31b6e384818f'),(NULL,'afd21f54283045338293c83b9d287603','38c92008f5754e348302c79498961747'),(NULL,'563c1db3575241deb84e5209af4d9df9','0faa6ccdb1d741a99f45d8ad80d7d984'),(NULL,'7dd2f764c9dd4af7aca79c1ac4c7d6ef','72f0a0a4b44f4f21a0edd0858a6bbcc5'),(NULL,'2bc126b7b8034d899bb6bf2887c86aad','4a47d671d3294386a2ac9871d4f1425d'),(NULL,'7607f3a5a4cf436e8186aefc5e49622d','d38488d83afa42b8a53e530ed870b2b5'),(NULL,'ed9b869a6fec4b63a8ab2b6ad181e301','39adfb4206024d14ab38822555314e7a'),(NULL,'1f0ae8efec9840f09a8b34e7acd16f5f','39adfb4206024d14ab38822555314e7a'),(NULL,'ed9b869a6fec4b63a8ab2b6ad181e301','f4f131913fe64f07b212bccedd2852a1');";
   		
   		upgradeService.insertNativeSQL(sql);
   		
   	}
   	
   	/**
   		 * 插入升级SQL
   		 * @param upgradeService
   		 */
   	private static void insertSQL_sysresources(UpgradeService upgradeService){
   		  	
   		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('0faa6ccdb1d741a99f45d8ad80d7d984','举报管理','举报列表',48572,'','/control/report/list*',NULL,NULL),('13bc9bdcb780479bbc5cb6a43b316b27','举报管理','添加举报分类',48563,'','/control/reportType/manage?method=add*',NULL,NULL),('19d55187b06946ceac2a6c929935fa68','问答管理','问答举报',20630,'','/control/questionReport/list*',NULL,NULL),('3507e6049b6843c39d8d31b6e384818f','举报管理','修改举报分类',48566,'','/control/reportType/manage?method=edit*',NULL,NULL),('38c92008f5754e348302c79498961747','举报管理','删除举报分类',48569,'','/control/reportType/manage?method=delete*',NULL,NULL),('39adfb4206024d14ab38822555314e7a','举报管理','修改举报',48578,'','/control/report/manage?method=edit*',NULL,NULL),('4a47d671d3294386a2ac9871d4f1425d','举报管理','删除举报',48581,'','/control/report/manage?method=delete*',NULL,NULL),('72f0a0a4b44f4f21a0edd0858a6bbcc5','举报管理','举报处理',48575,'','/control/report/manage?method=reportHandle*',NULL,NULL),('a9f67a215b034107952d40f592cf1056','话题管理','话题举报',12650,'','/control/topicReport/list*',NULL,NULL),('c5d0d01bf7b544828e1fbf3d61b9dd8a','会员管理','用户举报',27595,'','/control/userReport/list*',NULL,NULL),('d38488d83afa42b8a53e530ed870b2b5','举报管理','还原举报',48583,'','/control/report/manage?method=reduction*',NULL,NULL),('f0eeb58d820b48cba5368da6b80184bf','举报管理','举报分类列表',48560,'','/control/reportType/list*',NULL,NULL),('f4f131913fe64f07b212bccedd2852a1','举报管理','查询所有举报分类',0,NULL,'/control/reportType/manage?method=allType*','39adfb4206024d14ab38822555314e7a',1);";
   		
   		
   		upgradeService.insertNativeSQL(sql);
   	}
}
