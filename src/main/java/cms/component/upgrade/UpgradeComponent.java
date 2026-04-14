package cms.component.upgrade;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cms.component.JsonComponent;
import cms.model.upgrade.UpgradeLog;
import cms.model.upgrade.UpgradeSystem;
import cms.repository.upgrade.UpgradeRepository;
import cms.utils.FileUtil;
import cms.utils.PathUtil;

import jakarta.annotation.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * 升级组件
 *
 */
@Component("upgradeComponent")
public class UpgradeComponent {
	private static final Logger logger = LogManager.getLogger(UpgradeComponent.class);
	@Resource UpgradeRepository upgradeRepository;
    @Resource JsonComponent jsonComponent;

	/**
	 * 处理数据
	 * @param upgradeId 升级Id
	 */
	@Async
	public void manipulationData(String upgradeId){
		UpgradeSystem upgradeSystem = upgradeRepository.findUpgradeSystemById(upgradeId);
		//执行升级数据处理
		if(upgradeSystem.getRunningStatus() >= 40){
			String oldVersion = upgradeSystem.getOldSystemVersion().replaceAll("\\.", "_");//点替换为下划线
			String newVersion = upgradeSystem.getId().replaceAll("\\.", "_");//点替换为下划线
	
			boolean error = false;
			//反射调用升级数据处理类
			try {
				Class<?> cls = Class.forName("cms.infrastructure.upgrade.Upgrade"+oldVersion.trim()+"to"+newVersion.trim());
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
					upgradeRepository.addLog(upgradeId, jsonComponent.toJSONString(new UpgradeLog(new Date(),"调用升级数据处理类出错",2))+",");
				}
			}
				
		}
	}
	
	/**
	 * 查询下级目录列表
	 * @param path 路径 data/upgrade
	 */
	public List<String> querySubdirectoryList(String path){
		Set<String> folderList = new LinkedHashSet<String>();
		
		try {
			org.springframework.core.io.Resource[] resources = new PathMatchingResourcePatternResolver().
			        getResources(ResourceUtils.CLASSPATH_URL_PREFIX + path+"/**");
			
			String formatRootPath = FileUtil.toLeftSlant(PathUtil.rootPath());
			
			// 遍历文件内容
	        for(org.springframework.core.io.Resource resource : resources) {
	        	
	        	if("jar".equals(resource.getURL().getProtocol())){//jar:开头
	        		//jar:file:/D:/test2/test.jar!/BOOT-INF/classes!/WEB-INF/data/upgrade/5.1to5.2/cms/
	        		if (resource.getURL().getPath().endsWith("/")) { //如果是文件夹
	        			//源相对路径
	        			String sourceRelativePath = StringUtils.substringAfter(StringUtils.substringAfterLast(resource.getURL().getPath(), "!/"), path+"/");// 5.1to5.2/cms/
	        			//目标路径
	        			String distPath = StringUtils.substringBefore(sourceRelativePath, "/");//截取到等于第二个参数的字符串为止
	        			if(distPath != null && !distPath.trim().isEmpty()){
	        				folderList.add(distPath);
	        			}
	        			
	        		}
	        	}else{//file:开头
	        		//file:/F:/JAVA/cms-pro/target/classes/WEB-INF/data/upgrade/5.1to5.2/cms/
        			if (resource.getURL().getPath().endsWith("/")) { //如果是文件夹
        				String sourceRelativePath = StringUtils.substringAfter(resource.getURL().getPath(), formatRootPath+"/"+path+"/");//5.1to5.2/cms/
        				//目标路径
	        			String distPath = StringUtils.substringBefore(sourceRelativePath, "/");//截取到等于第二个参数的字符串为止
	        			if(distPath != null && !distPath.trim().isEmpty()){
	        				folderList.add(distPath);
	        			}
	        		}
	        	}
	        }
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
		        logger.error("查询下级目录列表异常 "+path,e);
		    }
		}
		
		
		
		return new ArrayList<>(folderList);
	}

	
	/**
	 * 读取初始论坛版本号
	 * @return
	 */
	public String readOriginalVersion(){
		
		return FileUtil.readFileToString("data"+File.separator+"install"+File.separator+"originalVersion.txt","UTF-8");
    	
	}
	
	/**
	 * 读取当前版本号
	 * @return
	 */
	public String readCurrentVersion(){
		String currentVersion = "";
		//读取当前系统版本
		ClassPathResource classPathResource = new ClassPathResource("data/systemVersion.txt");
		try (InputStream inputStream = classPathResource.getInputStream()){
			currentVersion = IOUtils.toString(inputStream, StandardCharsets.UTF_8); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读取当前系统版本IO异常",e);
	        }
		}
		return currentVersion;
	}
	
	
	
	

}
