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
 * 5.5升级到5.6版本执行程序
 *
 */
public class Upgrade5_5to5_6 {
	private static final Logger logger = LogManager.getLogger(Upgrade5_5to5_6.class);
	
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
    			
    			boolean flag = updateSQL_user_isAccount(upgradeService);
    			if(flag){
    				updateSQL_user_copyToAccount(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改用户表字段,将userName列的值复制到account列成功",1))+",");
        			
    				
    				updateSQL_user_cancelAccountTime(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"修改用户表字段,设置cancelAccountTime字段默认值成功",1))+",");
        			
    				
    				String indexName = updateSQL_user_queryConstraint(upgradeService);
    				if(indexName != null && !"".equals(indexName.trim())){
    
    				    updateSQL_user_removeConstraint(upgradeService,indexName);
    				    upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"用户表(user)用户名字段删除唯一性约束成功",1))+",");
            			
    				}
    				
    				insertSQL_syspermission(upgradeService);
        			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermission插入SQL成功",1))+",");
        			
        			insertSQL_syspermissionresources(upgradeService);
        			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表syspermissionresources插入SQL成功",1))+",");
        				
        			insertSQL_sysresources(upgradeService);
        			upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表sysresources插入SQL成功",1))+",");
        				
        			
    				
    			}else{
    				//添加错误中断
					upgradeService.updateInterruptStatus(upgradeId, 1, JsonUtils.toJSONString(new UpgradeLog(new Date(),"用户表(user)的account列不为空，不能继续升级",2))+",");
					
    			}
    			
    			
    			UpgradeSystem upgradeSystem_2 = upgradeService.findUpgradeSystemById(upgradeId);
    			if(upgradeSystem_2.getInterruptStatus() != 1){
    				//更改运行状态
    				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");
    			}
    			
    			
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
   	 * 用户表(user)用户名字段删除唯一性约束
   	 * @param upgradeService
   	 */
    private static int updateSQL_user_removeConstraint(UpgradeService upgradeService,String indexName){
    	return upgradeService.insertNativeSQL("alter table user drop index "+indexName); //删除成功返回0
    }
    
    /**
   	 * 查询用户表(user)用户名字段删除唯一性约束
   	 * @param upgradeService
   	 */
    private static String updateSQL_user_queryConstraint(UpgradeService upgradeService){
    	
    	
    	
    	DataSource dataSource = (DataSource)SpringConfigTool.getContext().getBean("dataSource");
    	
    	Connection conn = null;
		String databaseName = "";//数据库名称
		try {
			conn = DataSourceUtils.getConnection(dataSource);//spring的jdbc template中的datasource 
			databaseName = conn.getCatalog();
    	
		} catch (CannotGetJdbcConnectionException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("查询用户表(user)用户名字段删除唯一性约束",e);
		        }
			} catch (SQLException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("查询用户表(user)用户名字段删除唯一性约束",e);
		        }
			}finally {
				DataSourceUtils.releaseConnection(conn,dataSource);
			}
    	
    	List<Object[]> list =  upgradeService.queryNativeSQL("select `NON_UNIQUE`,`INDEX_NAME`,`COLUMN_NAME` from information_schema.statistics where (`TABLE_SCHEMA` = '"+databaseName+"') and (`TABLE_NAME` = 'user') and (`COLUMN_NAME` = 'userName')");
    //	List<Object[]> list =  upgradeService.queryNativeSQL("select `NON_UNIQUE`,`INDEX_NAME`,`COLUMN_NAME` from `information_schema`.`statistics` where (`TABLE_SCHEMA` = 'cms') and (`TABLE_NAME` = 'user') and (`COLUMN_NAME` = 'userName')");
    
    	if(list != null && list.size() >0){
    		for(Object[] fieldValue : list){
    			
    			String indexUnique = String.valueOf(fieldValue[0]);//Non_unique: 是否唯一，0是，1否。
    			String indexName =  String.valueOf(fieldValue[1]);//索引名称
    			String indexColume =  String.valueOf(fieldValue[2]);//对应列名称
    			if(indexUnique.equals("0")){
    				return indexName;
    			}
    			
    		}
    	}
    	return "";
    }
	
    /**
   	 * 判断账号字段是否为空
   	 * @param upgradeService
   	 */
    private static boolean updateSQL_user_isAccount(UpgradeService upgradeService){
    	//排序
    	LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
    	orderby.put("id", "desc");//降序排序
		QueryResult<User> qr = upgradeService.getScrollData(User.class,0, 10,orderby);
    	if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
    		for(User user : qr.getResultlist()){
    			if(user.getAccount() != null && !"".equals(user.getAccount().trim())){
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    /**
   	 * 修改用户表字段,将userName列的值复制到account列
   	 * 要判断账号字段为空才进行复制
   	 * @param upgradeService
   	 */
    private static void updateSQL_user_copyToAccount(UpgradeService upgradeService){
    	String sql = "UPDATE user SET account=userName";
       	upgradeService.insertNativeSQL(sql);
    }
    
    
    /**
   	 * 修改用户表字段,设置cancelAccountTime字段默认值
   	 * @param upgradeService
   	 */
    private static void updateSQL_user_cancelAccountTime(UpgradeService upgradeService){
    	String sql = "UPDATE user SET cancelAccountTime=-1";
       	upgradeService.insertNativeSQL(sql);
    }
    
    
    /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
	private static void insertSQL_syspermission(UpgradeService upgradeService){
		String sql = "INSERT INTO `syspermission` (`id`,`methods`,`name`,`priority`,`remarks`) VALUES ('dfe6741a3fce4ba0832d21843099a5ea','POST','AUTH_0d91dbfe06594b8c82e72d525d9dc229_POST_UPDATE',1,'注销账号');";
		
		upgradeService.insertNativeSQL(sql);
	}
		
	
	      
	/**
	 * 插入升级SQL
	 * @param upgradeService
	 */
	private static void insertSQL_syspermissionresources(UpgradeService upgradeService){
		
		String sql = "INSERT INTO `syspermissionresources` (`id`,`permissionId`,`resourceId`) VALUES (NULL,'dfe6741a3fce4ba0832d21843099a5ea','0d91dbfe06594b8c82e72d525d9dc229');";
		
		upgradeService.insertNativeSQL(sql);
		
	}
	
	/**
		 * 插入升级SQL
		 * @param upgradeService
		 */
	private static void insertSQL_sysresources(UpgradeService upgradeService){
		  	
		String sql = "INSERT INTO `sysresources` (`id`,`module`,`name`,`priority`,`remarks`,`url`,`urlParentId`,`urlType`) VALUES ('0d91dbfe06594b8c82e72d525d9dc229','会员管理','注销账号',27750,'','/control/user/manage?method=cancelAccount*',NULL,NULL);";
		
		
		upgradeService.insertNativeSQL(sql);
	}
    
}
