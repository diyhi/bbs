package cms.component.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import cms.utils.PathUtil;

import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 安装系统组件
 * @author Gao
 *
 */
@Component("installComponent")
public class InstallComponent {
	private static final Logger logger = LogManager.getLogger(InstallComponent.class);
	
	
	
	/**
	 * 需要生成的文件夹集合
	 * @return
	 */
	public List<String> folderList(){
		List<String> folderList = new ArrayList<String>();

		folderList.add("file/answer");
		folderList.add("file/answer/lock");
		folderList.add("file/avatar");
		folderList.add("file/comment");
		folderList.add("file/comment/lock");
		folderList.add("file/help");
		folderList.add("file/help/lock");
		folderList.add("file/helpType");
		folderList.add("file/helpType/lock");
		folderList.add("file/links");
		folderList.add("file/links/lock");
		folderList.add("file/mediaProcessSetting");
		folderList.add("file/mediaProcessSetting/lock");
		folderList.add("file/membershipCard");
		folderList.add("file/membershipCard/lock");
		folderList.add("file/question");
		folderList.add("file/question/lock");
		folderList.add("file/frontendApi");
		folderList.add("file/frontendApi/lock");
		folderList.add("file/topic");
		folderList.add("file/topic/lock");
		folderList.add("file/topic/thumbnailMarker");
		folderList.add("file/report");
		folderList.add("file/report/lock");
		folderList.add("file/staffAvatar");
		folderList.add("file/topicTag");
		folderList.add("file/topicTag/lock");
		folderList.add("file/questionTag");
		folderList.add("file/questionTag/lock");

		folderList.add("data/ffmpeg");
		//folderList.add("data/backup");
		folderList.add("data/filePackage");
		folderList.add("data/filterWord");
		folderList.add("data/install");
		folderList.add("data/questionIndex");
		folderList.add("data/temp");
		folderList.add("data/temp/media");
		//folderList.add("data/templateBackup");
		folderList.add("data/topicIndex");
		
		return folderList;
	}
	
	
	
	/**
	 * 生成文件夹
	 * @return
	 */
	public LinkedHashMap<String,Integer> createFolder(List<String> folderList){
		LinkedHashMap<String,Integer> folderInfoMap = new LinkedHashMap<String,Integer>();//key:文件路径  value: 1.文件夹已存在 2.当前路径为文件 3.创建失败  4.创建成功
		
		for(String folderPath : folderList){
			//生成文件目录
			Path path = Paths.get(PathUtil.defaultExternalDirectory()+File.separator+ Strings.CS.replace(folderPath, "/", File.separator));
			if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {//目录不存在
				try {
					Files.createDirectories(path);
					folderInfoMap.put(folderPath, 4);
				} catch (IOException e) {
					folderInfoMap.put(folderPath, 3);
					if (logger.isErrorEnabled()) {
				        logger.error("生成文件夹异常 "+folderPath,e);
				    }
				}
			}else{
				if(Files.isDirectory(path)){
					folderInfoMap.put(folderPath, 1);
				}else{
					folderInfoMap.put(folderPath, 2);
				}
				
			}
		}
		
		return folderInfoMap;
	}
	
	/**
	 * 判断文件权限
	 * @param filePathList 文件路径集合
	 * @return
	 */
	public Map<String,String> isFilePermission(List<String> filePathList){
		Map<String,String> errors = new HashMap<String,String>();//key:文件路径  value: 文件是否可读写
		
		for(String filePath : filePathList){
			Path path = Paths.get(PathUtil.defaultExternalDirectory()+File.separator+Strings.CS.replace(filePath, "/", File.separator));
			
			if (!Files.isReadable(path) || !Files.isWritable(path)){
				errors.put(filePath, (!Files.isReadable(path) ? "[不可读]":"")+(!Files.isWritable(path) ? "[不可写]":""));
					
		    }
		}
		return errors;
	}
	

	
	/**
	 * 创建安装状态文件
	 * @return
	 */
	public Map<String,String> createInstallStatus(){
		String filePath = "data/install/status.txt";
		
		Path path = Paths.get(PathUtil.defaultExternalDirectory()+File.separator+ Strings.CS.replace(filePath, "/", File.separator));
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){//文件不存在时自动创建
			    writer.write("0");
			}catch (IOException e) {
				if (logger.isErrorEnabled()) {
			        logger.error("创建安装状态文件异常 "+filePath,e);
			    }
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
        
		//Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)//判断文件是不是一个正常的文件。正常的文件是指没有特别的特性（例如，不是符号链接，不是目录等），包含真实的数据（例如二进制文件）

		return this.isFilePermission(Arrays.asList(filePath));
	}
    
}
