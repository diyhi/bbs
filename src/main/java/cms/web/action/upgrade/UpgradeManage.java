package cms.web.action.upgrade;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SHA;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 升级管理
 *
 */
@Component("upgradeManage")
public class UpgradeManage {
	private static final Logger logger = LogManager.getLogger(UpgradeManage.class);
	@Resource UpgradeService upgradeService;
	
	/**
	 * 处理数据
	 * @param upgradeId 升级Id
	 */
	@Async
	public void manipulationData(String upgradeId){
		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
		//执行升级数据处理
		if(upgradeSystem.getRunningStatus() >= 100){
			String oldVersion = upgradeSystem.getOldSystemVersion().replaceAll("\\.", "_");//点替换为下划线
			String newVersion = upgradeSystem.getId().replaceAll("\\.", "_");//点替换为下划线
	
			boolean error = false;
			//反射调用升级数据处理类
			try {
				Class<?> cls = Class.forName("cms.web.action.upgrade.impl.Upgrade"+oldVersion.trim()+"to"+newVersion.trim());
				Method m = cls.getDeclaredMethod("run",new Class[]{String.class});  
				m.invoke(cls,upgradeId);
			} catch (ClassNotFoundException e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			} catch (NoSuchMethodException e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			} catch (SecurityException e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			} catch (IllegalAccessException e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			} catch (IllegalArgumentException e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			} catch (InvocationTargetException e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			} catch (Exception e) {
				error = true;
				if (logger.isErrorEnabled()) {
		            logger.error("升级处理数据错误",e);
		        }
			//	e.printStackTrace();
			}finally{
				if(error){
					upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"调用升级数据处理类出错",2))+",");
				}
			}
				
		}
	}
	
	/**
	 * 获取所有文件夹的签名
	 * @param directoryPath 目录路径
	 * @return
	 */
	public String getFileSignature(String directoryPath){
		StringBuffer signature = new StringBuffer("");
		
		String[] extensions = null;//后缀名{"doc", "pdf"}
		boolean recursive = true;//是否递归
		
		
		//文件名称签名集合
		Set<String> fileNameSignList = new TreeSet<String>();
				
		
		
		//排除路径
        String excludeDirectoryPath  = new File(directoryPath+"signature.pem").getAbsolutePath();
        
		
		
		Collection<File> files = FileUtils.listFiles(new File(directoryPath), extensions, recursive);
		// 迭代输出
		for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
		    File file = iterator.next();
		    if (file.exists() && file.isFile()) {
		    	if(file.getAbsolutePath() != null && !"".equals(file.getAbsolutePath().trim())){
		    		//排除signature.pem文件
		    		if(!StringUtils.startsWithIgnoreCase(file.getAbsolutePath(), excludeDirectoryPath)){//判断开始部分是否与二参数相同。不区分大小写
		    			fileNameSignList.add(SHA.sha256Hex(new File(FileUtil.normalize(file.getAbsolutePath()))));
		    		}
		    		
		    	}
		    }
		}
		if(fileNameSignList != null && fileNameSignList.size() >0){
			for(String fileNameSign :fileNameSignList){
				signature.append(fileNameSign);
			}
		}
		
		if(signature.toString() != null && !"".equals(signature.toString())){
			//文件SHA-256信息摘要
			return SHA.sha256Hex(signature.toString());
			
		}
		
		return null;
	}
	
	/**
	 * 查询/添加任务运行标记
	 * @param count 次数  -1为查询方式
	 * @return
	 */
	@Cacheable(value="upgradeManage_cache_taskRunMark",key="'taskRunMark'")
	public Long taskRunMark_add(Long count){
		return count;
	}
	/**
	 * 删除任务运行标记
	 * @return
	 */
	@CacheEvict(value="upgradeManage_cache_taskRunMark",key="'taskRunMark'")
	public void taskRunMark_delete(){
	}
	
	
	
	
}
