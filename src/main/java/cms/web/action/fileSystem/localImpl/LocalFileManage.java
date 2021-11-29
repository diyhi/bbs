package cms.web.action.fileSystem.localImpl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import cms.utils.FileUtil;
import cms.utils.PathUtil;

/**
 * 本地文件系统
 * @author Gao
 *
 */
@Component("localFileManage")
public class LocalFileManage {
	private static final Logger logger = LogManager.getLogger(LocalFileManage.class);
	
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
     * 复制文件
     *  
     * @param resFilePath 源文件路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public void copyFile(String resFilePath, String distFolder) throws IOException {   
    	
        File resFile = new File(PathUtil.path()+File.separator+resFilePath);   
        File distFile = new File(PathUtil.path()+File.separator+distFolder); 
		if (resFile.isFile()) {   
			FileUtils.copyFileToDirectory(resFile,distFile,true);   
        }

    }  
    /**  
     * 复制目录
     * @param resDirectory 源目录路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public void copyDirectory(String resDirectory, String distFolder) throws IOException {   
    	File resDir = new File(PathUtil.path()+File.separator+resDirectory);   
        File distDir = new File(PathUtil.path()+File.separator+distFolder); 
 		if (resDir.isDirectory()){
 			FileUtils.copyDirectoryToDirectory(resDir, distDir);
        }
    	
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
		File file = new File(FileUtil.toRelativePath(dir+fileName));   
	    // 路径为文件且不为空则进行删除   
	    if (file.isFile() && file.exists()) {   
	        file.delete();   
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
	 * 删除目录
	 * @param path 路径 (格式"file"+File.separator+"目录"+File.separator)
	 */
	public Boolean removeDirectory(String path){
		
		String dir = PathUtil.path()+File.separator+path;
		File f = new File(dir); 

		return FileUtil.removeDirectory(f);
		
	}
	
	
	/**
	 * 根据锁和标记文件删除
	 * @param dirName 目录名称
	 * @param maxDeleteTime 最大删除时间
	 */
	public void lockRemoveFile(String dirName,Long maxDeleteTime){
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
			
			String old_suffix = FileUtil.getExtension(resFilePath);
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

}
