package cms.web.action.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import cms.bean.data.FieldProperties;
import cms.bean.data.TableInfoObject;
import cms.bean.data.TableProperty;
import cms.service.data.DataService;
import cms.utils.FileSize;
import cms.utils.FileUtil;
import cms.utils.PathUtil;

/**
 * MySQL备份/还原管理
 *
 */
@Component("mySqlDataManage")
public class MySqlDataManage {
	 private static final Logger logger = LogManager.getLogger(MySqlDataManage.class);
	
	@Resource DataRunMarkManage dataRunMarkManage;
	
	@Resource(name="mySqlDataServiceBean")
	private DataService dataService;//通过接口引用代理返回的对象
	@Resource TaskExecutor taskExecutor;//多线程
	
	//备份进度
	private String backupProgress = "";
	//未备份数量
	private int noBackupCount = 0;
	
	//还原进度
	private String resetProgress = "";
	//未还原数量
	private int noResetCount = 0;
	
	//MySQL版本
	private String databaseVersion = null;
	
	
	/**
	 * 查询MySQL版本信息
	 */
	public String showVersion(){
		if(databaseVersion == null){
			databaseVersion = dataService.findDatabaseVersion();
		}
		return databaseVersion;
	}
	/**
	 * 查询表信息
	 */
	public List<TableInfoObject> showTable(){
		return dataService.findTable();
	}
	
	/**
	 * 备份
	 * @param path 目录
	 */
	@Async
	public void backup(String path){
		dataRunMarkManage.taskRunMark_delete();
		dataRunMarkManage.taskRunMark_add(1L);
		//生成文件夹
		FileUtil.createFolder(path);
		backupProgress = "开始备份";
		List<TableInfoObject> tableInfoObjectList = dataService.findTable();//数据库表名
		this.noBackupCount = tableInfoObjectList.size();
		for(TableInfoObject tableInfoObject :tableInfoObjectList){
			TableProperty tableProperty = dataService.findFieldBytableName(tableInfoObject.getName());
			
			//如果数据库表为quartz定时器的表，则表名为大写
			if(tableProperty.getTableName().startsWith("qrtz_")){
				tableProperty.setTableName(tableProperty.getTableName().toUpperCase());
			}
			
			taskExecutor.execute(new backupThread(tableProperty,PathUtil.path()+File.separator+path));
			
		}
		
    }  
	/**
	 * 备份线程
	 * @author Administrator
	 */
	private class backupThread implements Runnable { 
		TableProperty tableProperty;
		String path;
		public backupThread(TableProperty tableProperty,String path) {      
			this.tableProperty = tableProperty;
			this.path = path;
		}    
		public void run() {    	
			if(tableProperty != null){
				try {
					writeData(tableProperty,path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					 if (logger.isErrorEnabled()) {
			  	         logger.error("数据库备份线程",e);
			    	 }
				}
				
			}
		}
	}
	
	
	/**
	 * 写入数据到文件
	 * @param tableProperty 数据库表属性
	 * @param path 路径
	 */
	public void writeData(TableProperty tableProperty,String path ) {   
		
		backupProgress = "备份表-->"+tableProperty.getTableName();
		
		List<String> fieldName = new ArrayList<String>();
		List<FieldProperties> fieldPropertiesList = tableProperty.getFieldProperties();//取得字段属性
		for(FieldProperties fieldProperties : fieldPropertiesList){
			fieldName.add(fieldProperties.getFieldName());
			
		}
		
	//	CsvWriter csvWriter = null;
		BufferedWriter writer = null;
		CSVPrinter printer = null;
    	try {   
    		
    		
    		long count = dataService.findCount(tableProperty.getTableName());//查询总页数
    	
    		if(count >0){

    			long traverseCount = 0;//当前表要遍历次数
    			Long currentPage = 0L;
        		long maxResult = 2000L;//定义一次查询数据量
        		long div = count/maxResult;//除
        		long remainder = count%maxResult;//余数
    			if(div >0){
    				if(remainder >0){
    					traverseCount = (div+1);
    				}else{
    					traverseCount = div;
    				}
    			}else{//==0
    				if(remainder >0){
    					traverseCount = 1;
    				}
    			}
    			StringBuffer param = new StringBuffer();//按顺序组装返回值参数
    			for(String s : fieldName){
    				param.append("`").append(s).append("`,");//加上`防止字段名和关键字冲突
    			}
    			param.deleteCharAt(param.length()-1);   
    			
    			String csvFilePath = path+File.separator+tableProperty.getTableName()+".csv"; //路径
    			
    			
    			
    	        Path csvPath = Paths.get(csvFilePath);
    	        if(Files.notExists(csvPath)){//如果文件不存在
    	        	Files.createFile(csvPath);//创建文件
    	        }
    	        
    	        writer = Files.newBufferedWriter(csvPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE);

    	        CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(System.getProperty("line.separator"));//文件分割符
    	        
    	        printer = new CSVPrinter(writer, format);
        		//写入表头
                printer.printRecord(fieldName);
        		
        		
    			for(long t = 0; t<traverseCount; t++){
    				dataRunMarkManage.taskRunMark_delete();
    				dataRunMarkManage.taskRunMark_add(t);
    				
    				
    				List<List<String>> tablePageData = dataService.findPageByTableName(param.toString(),tableProperty.getTableName(),currentPage,maxResult,tableProperty);
    	    		if(tablePageData != null && tablePageData.size() >0){
    	    			for(int i=0;i<tablePageData.size();i++){//遍历   
    	    				List<String> field = tablePageData.get(i);
    	    				printer.printRecord(field);
    	    			}
    	    			printer.flush();

    	    		}
    	    		currentPage += maxResult;
    			}
    		}
  
    	   ;
  
    	 } catch (IOException e) {   
    	  //    e.printStackTrace(); 
    	      if (logger.isErrorEnabled()) {
  	            logger.error("写入数据到文件",e);
    	      }
    	 } finally {   
    		 if(writer != null){
    			 try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
		  	            logger.error("写入数据到文件关闭错误",e);
		    	      }
				}
    		 }
    		 if(printer != null){
    			 try {
					printer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					if (logger.isErrorEnabled()) {
		  	            logger.error("写入数据到文件关闭错误",e);
		    	      }
				}
    		 }
    		 this.subtractNoBackupCount();
         } 
	}
	
	
	
	/**
	 * 还原数据
	 */
	@Async
	public void reduction(String path){
		Threads.calculation(0);//线程数清零
		dataRunMarkManage.taskRunMark_delete();
		dataRunMarkManage.taskRunMark_add(1L);
		resetProgress = "开始还原";
		List<TableInfoObject> tableInfoObjectList = dataService.findTable();//数据库表名
		this.noResetCount = tableInfoObjectList.size();
		
		List<TableProperty> tablePropertyList = new ArrayList<TableProperty>();//数据库表属性
		List<String> tableName = new ArrayList<String>();//数据库表名
		for(TableInfoObject tableInfoObject :tableInfoObjectList){
			tableName.add(tableInfoObject.getName());
			TableProperty tableProperty = dataService.findFieldBytableName(tableInfoObject.getName());
			tablePropertyList.add(tableProperty);
		}
		Threads.tablePropertyList = tablePropertyList;//将数据库参数放在全局线程变量中
		
		
		//清空数据库
		dataService.deleteDatabase(tableName);
		resetProgress = "清空数据库";
		//还原数据
		for(TableProperty tableProperty :tablePropertyList){
			Threads.calculation(1);//线程数+1
			taskExecutor.execute(new reductionThread(tableProperty,path));
		}	
	}

	/**
	 * 还原线程
	 * @author Administrator
	 */
	private class reductionThread implements Runnable { 
		TableProperty tableProperty;
		String path;
		public reductionThread(TableProperty tableProperty,String path) {      
			this.tableProperty = tableProperty;
			this.path = path;
		}    
		public void run() {    	
			if(tableProperty != null){
				try {
					readData(tableProperty,path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					 if (logger.isErrorEnabled()) {
			  	         logger.error("数据库还原线程",e);
			    	 }
				}finally {
					//所有线程已结束
					int ss = Threads.calculation(-1);
					if(ss == 0){// 最后一个线程完成后启用约束  Threads.Calculation(-1)线程数-1 
						
						Threads.calculation(0);//重置线程数为0
						Threads.tablePropertyList.clear();//清除约束全局变量
					}
				}
				
			}
		}
	}
	/**
	 * 读取文件中的数据
	 * @param tableProperty 数据库表属性
	 * @param path 路径
	 */
	private void readData(TableProperty tableProperty,String path ) {  
		resetProgress = "还原表-->"+tableProperty.getTableName();
		path = path+File.separator+tableProperty.getTableName()+".csv";//文件路径
		//判断文件是否存在
		File file=new File(path);   
		if(file.exists()){  
			dataService.restoreData(tableProperty, path);	
		}  
		this.subtractNoResetCount();
	}
	
	
	
	/**
	 * 读取某个文件夹, 返回目录下一级所有文件夹名称和大小
	 */
	public Map<String, String> getFolder(String path){
		 File dir=new File(path);
		 File[] files=dir.listFiles();
		 Map<String, String> folder = new TreeMap<String, String>(new Comparator<String>(){ 
			 public int compare(String obj1,String obj2){ 
				 //降序排序 
				 return obj2.compareTo(obj1); 
			 } 
		 });
		 for(int i=0;i<files.length;i++){ 
		    if(files[i].isDirectory()){//判断是否是目录
		    	File file = new File(path+File.separator+files[i].getName());
		    	folder.put(files[i].getName(), FileSize.conversion(getDirSize(file)));
		    }
		 }
		 return folder;
	}
	/**
	 * 读取某个文件夹, 返回目录下一级所有文件名称和大小
	 */
	public Map<String, String> getFile(String path){
		 File dir=new File(path);
		 File[] files=dir.listFiles();
		 Map<String, String> folder = new TreeMap<String, String>();
		 for(int i=0;i<files.length;i++){ 
		    if(files[i].isFile()){//判断是否是文件  
		    	folder.put(files[i].getName(), FileSize.conversion(files[i].length()));
		    }
		 }
		 return folder;
	}
	/**
	 * 统计文件夹大小
	 * @param dir
	 * @return
	 */
	private static long getDirSize(File dir) {   
	    if (dir == null) {   
	        return 0;   
	    }   
	    if (!dir.isDirectory()) {   
	        return 0;   
	    }   
	    long dirSize = 0;   
	    File[] files = dir.listFiles();   
	    for (File file : files) {   
	        if (file.isFile()) {   
	            dirSize += file.length();   
	        } else if (file.isDirectory()) {   
	            dirSize += file.length();   
	            dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计   
	        }   
	    }   
	    return dirSize;   
	}

	public String getBackupProgress() {
		return backupProgress;
	}
	public String getResetProgress() {
		return resetProgress;
	}

	public int getNoBackupCount() {
		return noBackupCount;
	}

	/**
	 * 减少未备份数量
	 */
	private synchronized void subtractNoBackupCount() {
		this.noBackupCount --;
		if(this.noBackupCount == 0){
			backupProgress = "备份完成";
			dataRunMarkManage.taskRunMark_delete();
		}
	}

	public int getNoResetCount() {
		return noResetCount;
	}
	/**
	 * 减少未还原数量
	 */
	private synchronized void subtractNoResetCount() {
		this.noResetCount--;
		if(this.noResetCount == 0){
			resetProgress = "还原完成";
			dataRunMarkManage.taskRunMark_delete();
		}
	}
}
