package cms.web.action;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import cms.bean.setting.SystemSetting;
import cms.service.setting.SettingService;
import cms.utils.PathUtil;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * 文件管理
 *
 */
@Component("fileManage")
public class FileManage {
	private static final Logger logger = LogManager.getLogger(FileManage.class);
	
	@Resource SettingService settingService;
	
	//读取配置文件类型
  	private static Properties fileFormat = new Properties();
  	//读取富文本编辑器配置
  	private static Properties richText = new Properties();
  	static{
		try {
			fileFormat.load(FileManage.class.getClassLoader().getResourceAsStream("fileFormat.properties"));
		} catch (IOException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读取配置文件fileFormat.properties错误",e);
	        }
		}
		try {
			richText.load(FileManage.class.getClassLoader().getResourceAsStream("richText.properties"));
		} catch (IOException e) {
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("读取配置文件richText.properties错误",e);
	        }
		}
		
	}
	
	
	
	/**
	 * 新建文件
	 * @param path 路径
	 * @return
	 */
	public void newFile(String path){
		File file = new File(PathUtil.path()+File.separator+path);
		if (!file.exists()) { 
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("新建文件",e);
		        }
			} 
		} 
	}
	
	/**
	 * 删除文件
	 * @param path 路径
	 * @return
	 */
	public Boolean deleteFile(String path){
		Boolean state = null;
		File file = new File(PathUtil.path()+File.separator+path); 
		
		// 路径为文件且不为空则进行删除   
		if (file.isFile() && file.exists()) {   
			state = file.delete();
			if(state == false){
				try {
					//清空内容
					org.apache.commons.io.FileUtils.writeStringToFile(file, "", "UTF-8");
					state = file.delete();
				} catch (IOException e) {
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("删除文件",e);
			        }
				}

			}
		}
		return state;
	}
	/**
	 * 删除失败状态文件
	 * @param path 路径
	 */
	public void failedStateFile(String path){
		File stateFile = new File(PathUtil.path()+File.separator+path);
		if (!stateFile.exists()) { 
			try {
				stateFile.createNewFile();
			} catch (IOException e) {
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("删除失败状态文件",e);
		        }
			} 
		} 
	}
	/**
	 * 生成文件夹
	 * @param path路径
	 */
	public Boolean createFolder(String path){
		//生成文件目录
		File dirFile  = new File(PathUtil.path()+File.separator+path);
	    if (!(dirFile.exists()) && !(dirFile.isDirectory())){
	    	dirFile.mkdirs();  
	    	return true;
	    }  
	    return false;
	}
	
	
	/**
	 * 删除目录
	 * @param path 路径 (格式"file"+File.separator+"目录"+File.separator)
	 */
	public Boolean removeDirectory(String path){
		String dir = PathUtil.path()+File.separator+path;
		File f = new File(dir); 
		
		return removeDirectory(f);
		
	}
	/**
	 * 删除目录
	 * @param dir对象
	 */
	public Boolean removeDirectory(File dir){
		Boolean state = null;
		if(dir.isDirectory()){
			state = org.apache.commons.io.FileUtils.deleteQuietly(dir);
			if(state == false){
				//清空内容
				String[] extensions = null;//后缀名{"doc", "pdf"}
				boolean recursive = true;//是否递归

				Collection<File> files = FileUtils.listFiles(dir, extensions, recursive);
				// 迭代输出
				for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				    File file = iterator.next();
				    if(!file.isHidden()){//不是隐蔽文件
				    	//清空内容
						try {
							org.apache.commons.io.FileUtils.writeStringToFile(file, "", "UTF-8");
							state = file.delete();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("删除目录",e);
					        }
						}
				    }
				}
			}
		}
		
		return state;
		
	}
	
	
	/**
	 * 添加锁
	 * @param path 路径(格式"file"+File.separator+"目录"+File.separator+"lock"+File.separator)
	 * @param lockFileName 锁文件名称
	 */
	public void addLock(String path,String lockFileName)throws IOException{
		
		 //文件锁目录
		String lockPathDir = File.separator+path;
		
	   //取得锁文件保存目录的真实路径
		String lockRealpathDir = PathUtil.path()+lockPathDir;
	   
		File lockFile = new File(lockRealpathDir+lockFileName);//生成目录
		if (!lockFile.exists()) { 
			lockFile.createNewFile(); 
		} 
	}
	
	/**
	 * 删除锁
	 * @param path 路径(格式"file"+File.separator+"目录"+File.separator+"lock"+File.separator)
	 * @param fileName 文件名称
	 */
	public void deleteLock(String path,String fileName){
		String dir = PathUtil.path()+File.separator+path;
		//替换路径中的..号 
		File file = new File(this.toRelativePath(dir+fileName));   
	    // 路径为文件且不为空则进行删除   
	    if (file.isFile() && file.exists()) {   
	        file.delete();   
	    }   
	}
	/**
	 * 左斜杆路径转为系统路径(如:  file/f/f/)
	 * @param path 
	 */
	public String toSystemPath(String path){
		String systemPath = "";
		if(path != null && !"".equals(path.trim())){
			return path.replace("/", File.separator);
			
			
		}
		return systemPath;
	}
	
	/**
	 * 替换路径中的相对路径为横杆(如:  file/../f/替换为file/-/f/)
	 * @param path 
	 */
	public String toRelativePath(String path){
		String systemPath = "";
		if(path != null && !"".equals(path.trim())){
			systemPath = StringUtils.replace(path, "..", "-");
			systemPath = StringUtils.replace(path, "%", "-");//百分号替换为横杆
		}
		return systemPath;
	}
	
	/**
	 *  获取文件名,含后缀
	 * @param path 
	 */
	public String getName(String path){
		if(path != null && !"".equals(path.trim())){
			return FilenameUtils.getName(path);
		}
		return "";
		
	}
	/**
	 *  获取文件名,不包含后缀
	 * @param path 
	 */
	public String getBaseName(String path){
		if(path != null && !"".equals(path.trim())){
			return FilenameUtils.getBaseName(path);
		}
		return "";
		
	}
	/**
	 * 获取文件后缀名
	 * @param path 
	 */
	public String getExtension(String path){
		if(path != null && !"".equals(path.trim())){
			String extension = FilenameUtils.getExtension(path);
			if(extension != null && !"".equals(extension.trim())){//后缀名改为小写
				extension = extension.toLowerCase();
			}
			return extension;
		}
		return "";
		
	}
	/**
	 * 获取文件路径,不忽略分割符
	 * @param path 
	 * /D:/aa/bb/cc/1.txt 转为 /D:/aa/bb/cc/     file/topic/2017-12-31/image/2.jpeg 转为 file/topic/2017-12-31/image/
	 */
	public String getFullPath(String path){
		if(path != null && !"".equals(path.trim())){
			return FilenameUtils.getFullPath(path);
			
		}
		return "";
		
	}
	/**
	 * 获取当前系统格式化路径
	 * windows下 D:/Hello../oo./h/World.txt 转为 D:\Hello..\oo.\h\World.txt
	 * @return
	 */
	public String normalize(String path){
		return FilenameUtils.normalize(path);
		
	}
	
	
	/**
	 * 根据锁和标记文件删除
	 * @param dirName 目录名称
	 * @param maxDeleteTime 最大删除时间
	 */
	private void lockRemoveFile(String dirName,Long maxDeleteTime){
		String dir = PathUtil.path()+File.separator+"file"+File.separator+dirName+File.separator+"lock"+File.separator;
		File f = new File(dir);   
		if(f.isDirectory()){
			String[] extensions = null;//后缀名{"doc", "pdf"}
			boolean recursive = false;//是否递归
			Collection<File> files = FileUtils.listFiles(f, extensions, recursive);
			//分隔符
			String separator = "";
			if("\\".equals(File.separator)){
				separator = "\\\\";
			}else{
				separator = "/";
			}
			// 迭代输出
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			    File file = iterator.next();
			    if(maxDeleteTime != null){
			    	if(maxDeleteTime < file.lastModified()){
			    		continue;
			    	}
			    	
			    }
			    
			    if(!file.isHidden()){//不是隐藏文件
			    	
			    	
			    	if(file.getName().startsWith("#")){//如果为文件夹标记开头
			    		
			    		String delete_dir = file.getName().substring(1,file.getName().length());
			    		
			    		File delete_file = new File(PathUtil.path()+File.separator+"file"+File.separator+dirName+File.separator+delete_dir+File.separator);
			    		
			    		if(delete_file.isDirectory()){
			    			
			    			boolean state = org.apache.commons.io.FileUtils.deleteQuietly(delete_file);
				    		if(state == true){
				    			file.delete();
				    		}
			    		}else{
			    			file.delete();
			    		}
			    	}else{
			    		String delete_dir = file.getName().replaceAll("_",separator);
			    	
			    		File delete_file = new File(PathUtil.path()+File.separator+"file"+File.separator+dirName+File.separator+delete_dir);
			    		if(delete_file.exists()){
			    			boolean state = delete_file.delete();
			    			if(state == true){
				    			file.delete();
				    		}
			    		}else{
			    			file.delete();
			    		}
			    	}
			    	
			    	
			    }
			}
		}
	}

	
	/**  
     * 复制文件
     *  
     * @param resFilePath 源文件路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public void copyFile(String resFilePath, String distFolder) throws IOException {   
        File resFile = new File(PathUtil.path()+File.separator+resFilePath);   
        File distFile = new File(PathUtil.path()+File.separator+distFolder+File.separator); 

		if (resFile.isFile()) {   
			FileUtils.copyFileToDirectory(resFile,distFile,true);   
        }

    }  
    
    /**  
     * 复制目录
     *  
     * @param resDirPath 源目录路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public void copyDirectory(String resDirPath, String distFolder) throws IOException {   
        File copyDir = new File(PathUtil.path()+File.separator+resDirPath);   
        File distFile = new File(PathUtil.path()+File.separator+(distFolder == null || "".equals(distFolder.trim()) ? "" :distFolder+File.separator)); 
   
		if (copyDir.isDirectory()) { 
			FileUtils.copyDirectoryToDirectory(copyDir, distFile);   
        }

    }
    /**  
     * 重命名文件或文件夹  
     *  
     * @param resFilePath 源文件路径  
     * @param newFileName 重命名  
     * @return 操作成功标识  
     */   
    public boolean renameFile(String resFilePath, String newFileName) {
    	File resFile = new File(PathUtil.path()+File.separator+resFilePath);
        //源文件父路径
        String resParentPath = resFile.getParent();   
        File newFile = new File(resParentPath+File.separator+newFileName);   
        return resFile.renameTo(newFile);   
    } 
    
    
    /**  
     * 读文件
     * @param path 路径
     * @param encoding 编码  UTF-8
     * @return 文本字符串
     */   
     public String readFileToString(String path,String encoding){ 
     	try {
     		File file = new File(PathUtil.path()+File.separator+path);
     		if(file != null && file.exists()){
     			return FileUtils.readFileToString(file, encoding);
     		}
     		
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 		//	e.printStackTrace();
 			if (logger.isErrorEnabled()) {
	            logger.error("读文件",e);
	        }
 		}
 		return null;
     }
     
     /**  
      * 读文件(读取的内容不包含UTF-8 BOM标志)
      * @param path 路径
      * @param encoding 编码  UTF-8
      * @return 文本字符串
      */   
     public String readFileBomToString(String path,String encoding){ 
    	File file = new File(PathUtil.path()+File.separator+path);
    	StringBuffer result = new StringBuffer("");
		if(file != null && file.exists()){
			BufferedReader br = null;
			String r;
			try {
				// 如果是UTF-8编码，需去掉BOM标志
		        if ("UTF-8".equalsIgnoreCase(encoding)) {

		        	//处理windows自动添加的BOM
		            BOMInputStream bis = new BOMInputStream(new FileInputStream(file),false,ByteOrderMark.UTF_8);
		           
		            //创建输入流
		            InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
		            
		            
		            br = new BufferedReader(isr);
		            
		            while ((r = br.readLine()) != null) {
		                if (!StringUtils.isEmpty(r)) {
		                    result.append(r);
		                }
		            }
		            
		        }else{  	
					return FileUtils.readFileToString(file, encoding);	
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("读文件(读取的内容不包含UTF-8 BOM标志)",e);
		        }
			}finally {
				if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                //	e.printStackTrace();
	                	if (logger.isErrorEnabled()) {
	    		            logger.error("读文件(读取的内容不包含UTF-8 BOM标志)",e);
	    		        }
	                }
	            }
			}
		} 
		return result.toString();
     }
     
     
     
     
     
     /**  
      * 读文件
      * @param path 路径
      * @param encoding 编码  UTF-8
      * @return 文本字符串
      */   
      public List<String> readLines(File file,String encoding){ 
      	try {
  			return FileUtils.readLines(file, encoding);
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  		//	e.printStackTrace();
  			if (logger.isErrorEnabled()) {
	            logger.error("读文件",e);
	        }
  		}
  		return null;
      }
    /**  
     * 写文件(如果目标文件不存在，FileUtils会自动创建)
     * @param path 路径
     * @param content 内容
     * @param encoding 编码  UTF-8
     * @param append 是否是追加模式
     */   
    public void writeStringToFile(String path,String content,String encoding, boolean append){
    	try {
			FileUtils.writeStringToFile(new File(PathUtil.path()+File.separator+path), content, encoding,append);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("写文件(如果目标文件不存在，FileUtils会自动创建)",e);
	        }
		}  
    } 
    
    /**  
     * 写文件
     * @param path 路径
     * @param newFileName 新文件名称
     * @param content 内容
     */   
    public void writeFile(String path,String newFileName,byte[] content){
    	FileOutputStream fileoutstream = null;
		try {
			//文件输出流
			fileoutstream = new FileOutputStream(new File(PathUtil.path()+File.separator+path, newFileName));
			//写入硬盘
			fileoutstream.write(content);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("写文件",e);
	        }
		}finally{
			
			if(fileoutstream != null){
				try {
					fileoutstream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("写文件",e);
			        }
				}
			}
			
		} 
    }
    
    /**
     * 去除字符串中的空格、回车、换行符、制表符
     * @param str
     * @return
     */
    public String replaceBlank(String content) {
        String dest = "";
        if (content != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(content);
            dest = m.replaceAll("");
        }
        return dest;
    }
    
    /**
     * 移除UTF-8BOM 标志
     * @param content 内容集合
     */
    private void removeBom(List<String> contentList) {
        final byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        if (contentList.size() > 0) {
            String str = contentList.get(0);
            if (str != null && str.length() > 0) {
                int index = str.indexOf(new String(bom));
                if (index != -1) {
                	contentList.remove(0);
                	contentList.add(0, str.substring(index + 1, str.length()));
                }
            }
        }
    }
    
   
    
    /**
     * 图片格式转换
     * @param resFilePath 原文件路径
     * @param newFilePath 生成文件路径
     * @param suffix 新文件后缀    jpg  bmp
     * @throws IOException
     */
    public void converterImage(String resFilePath,String newFilePath,String suffix)
            throws IOException{

		File file = new File(PathUtil.path()+File.separator+resFilePath);
		if(file.isFile() &&file.exists()){
			BufferedImage bIMG =ImageIO.read(file);
			
			String old_suffix = getExtension(resFilePath);
			if(old_suffix != null && "png".equalsIgnoreCase(old_suffix)){
				//下面两句解决png转jpg时图片发生颜色失真问题
				BufferedImage newBufferedImage = new BufferedImage(bIMG.getWidth(), bIMG.getHeight(), BufferedImage.TYPE_INT_RGB);
				newBufferedImage.createGraphics().drawImage(bIMG, 0, 0, Color.WHITE, null);
				 
			    ImageIO.write(newBufferedImage, suffix, new File(PathUtil.path()+File.separator+newFilePath));
			}else{
				if(old_suffix != null && old_suffix.equals(suffix)){//如果同后缀，则直接复制
					FileUtils.copyFile(file,new File(PathUtil.path()+File.separator+newFilePath));
				}else{
					ImageIO.write(bIMG, suffix, new File(PathUtil.path()+File.separator+newFilePath));
				}
			}  
		}   
    }

    /**
     * 验证文件后缀
     * @param fileName 文件名称(含后缀)
     * @param formatList 允许格式列表
     */
    public boolean validateFileSuffix(String fileName,List<String> formatList){
    	//取得文件后缀
		String suffix = this.getExtension(fileName);
		for(String format : formatList) {
			String[] fileType_array = format.split("/");
			if(fileType_array != null && fileType_array.length >0){
				for(String fileType : fileType_array){
					if(fileType.equalsIgnoreCase(suffix)){
						return true;
					}
				}
			}
		}
		
		return false;
    }
    
    
    /**
	 * 验证文件类型(不用，可删除)
	 * @param fileName 文件名称
	 * @param contentType 内容类型 file.getContentType()
	 * @param formatList 允许格式列表
	 * @return
	 * @throws Exception
	 */
	public boolean validateFileType(String fileName,String contentType,List<String> formatList){	
		//取得文件后缀
		String suffix = this.getExtension(fileName);
		for(String format : formatList){
			if(format.equalsIgnoreCase(suffix)){//如果允许格式列表含有当前后缀
				for(Object key : fileFormat.keySet()){
					if(key != null){
						if(String.valueOf(key).equalsIgnoreCase(suffix)){//如果Properties文件包含当前后缀
							String value = (String)fileFormat.get(key);
							String[] values = value.split(",");
							for(String s : values){//得到类型
								if(contentType.equalsIgnoreCase(s.trim())){//如果含有当前类型
									return true;
								}
							}
							
							break;
						}
					}
				}
				break;
			}
			
		}	
		
		return false;
	}
	
	/**
	 * 读取富文本编辑器允许文件上传格式
	 * @return
	 */
	public List<String> readRichTextAllowFileUploadFormat(){	
		//富文本文件上传格式
		List<String> fileUploadFormatList = new ArrayList<String>();
		
		for(Object key : richText.keySet()){
			if(key != null){
				if(String.valueOf(key).equals("fileUploadFormat")){
					String value = (String)richText.get(key);
					if(value != null && !"".equals(value.trim())){
						String[] values = value.split(",");
						if(values != null && values.length >0){
							for(String format : values){
								if(format != null && !"".equals(format.trim())){
									fileUploadFormatList.add(format.trim());
								}
								
							}
						}
					}
				}
			}
		}
		return fileUploadFormatList;
	}
	
	
	
	 /**
	 * 验证文件名
	 * @param fileName 文件名称
	 * @return
	 * @throws Exception
	 */
	public boolean validateFileName(String fileName){
		return fileName.matches("^[^\\/\\<>\\*\\?\\:\"\\|\\%]+$");
	}
    /**----------------------------------- 定时删除文件 ----------------------------------**/
    
	
	/**
     * 删除无效文件
     */
    public void deleteInvalidFile(){
    	SystemSetting systemSetting = settingService.findSystemSetting_cache();
    	if(systemSetting != null){
    		if(systemSetting.getTemporaryFileValidPeriod() != null && systemSetting.getTemporaryFileValidPeriod() >0){
    			//最大删除时间
    			Long maxDeleteTime = new Date().getTime()-systemSetting.getTemporaryFileValidPeriod()*60*1000;

    			this.lockRemoveFile("topic",maxDeleteTime);//删除话题文件
    			this.lockRemoveFile("template",maxDeleteTime);//删除模板 文件

    			this.lockRemoveFile("comment",maxDeleteTime);//评论文件
    			this.lockRemoveFile("help",maxDeleteTime);//删除帮助文件
    			this.lockRemoveFile("links",maxDeleteTime);//删除友情链接文件
    			this.lockRemoveFile("membershipCard",maxDeleteTime);//会员卡
    			
    		}
    	}
    	
    }
	

}
