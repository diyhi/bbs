package cms.utils;


import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cms.bean.ZipPack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
/**
 * ZIP工具
 *
 */
public class ZipUtil {
	private static final Logger logger = LogManager.getLogger(ZipUtil.class);
	
	public static final int BUFFER_SIZE = 1024;
	
	/**
	 * 遍历zip文件内容
	 * @param zipFile
	 * @param callback
	 */
	public static void iterate(File zipFile, ZipCallback callback) {
        ZipArchiveInputStream is = null;
        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;

            while ((entry = is.getNextZipEntry()) != null) {
                callback.process(entry);
            }
        } catch(Exception e) {
           // e.printStackTrace();
            if (logger.isErrorEnabled()) {
	            logger.error("遍历zip文件内容",e);
	        }
        } finally {
            IOUtils.closeQuietly(is);
        }
	}
	
	/**
	 * 遍历文件名称
	 * @param zipFile
	 */
	public static List<String> iterateFileName(File zipFile) {
        ZipArchiveInputStream is = null;
        List<String> fileNames = new ArrayList<String>();

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;
            while ((entry = is.getNextZipEntry()) != null) {
                fileNames.add(entry.getName());
            }
        } catch(Exception e) {
            //e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("遍历文件名称",e);
	        }
        } finally {
            IOUtils.closeQuietly(is);
        }
        return fileNames;
	}

    
	/********************************************************************************/
	

    /**
	 * 压缩目录
	 * @param sourceDir 待压缩目录  
	 * @param targetZip 输出文件
	 */
	public static void pack(String sourceDir, String targetZip) {
		pack(sourceDir, targetZip,null,"");
	}
	
	/**
	 * 压缩目录
	 * @param sourceDir 待压缩目录  
	 * @param targetZip 输出文件
	 * @param excludeDirectory 排除目录下的文件
	 */
	public static void pack(String sourceDir, String targetZip,String excludeDirectory) {
		pack(sourceDir, targetZip,excludeDirectory,"");
	}
    /**
     *  把一个目录打包到一个指定的zip文件中
     * @param sourceDir 待压缩目录  
     * @param targetZip 输出文件
     * @param excludeDirectory 排除目录下的文件
     * @param entryPath 压缩内文件逻辑路径。如static/
     */
    public static void pack(String sourceDir, String targetZip,String excludeDirectory,String entryPath){
        if(!entryPath.endsWith(File.separator)&&entryPath != null && !entryPath.isEmpty()){
            entryPath+=File.separator;
        }
        ZipArchiveOutputStream out = null;
        try {
            out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(new File(targetZip))));
            out.setEncoding("UTF-8");
            pack(out, sourceDir,excludeDirectory, entryPath);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
          //  e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("压缩",e);
	        }
        } finally {
            IOUtils.closeQuietly(out);
        }
    }
    
    /**
     *  把多个目录打包到一个指定的zip文件中
     * @param zipPackList 待压缩目录集合
     * @param targetZip 输出文件
     * @param excludeDirectory 排除目录下的文件
     */
    public static void pack(List<ZipPack> zipPackList, String targetZip,String excludeDirectory){
    	
    	ZipArchiveOutputStream out = null;
        try {
            out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(new File(targetZip))));
            out.setEncoding("UTF-8");
            for (ZipPack zipPack : zipPackList) {  
            	String sourceDir = zipPack.getSource();
            	String entryPath = zipPack.getEntryPath();
            	
            	if(!entryPath.endsWith(File.separator)&&entryPath != null && !entryPath.isEmpty()){
                    entryPath+=File.separator;
                }
            
            	
            	// 判断此文件是否是一个文件夹
                if (zipPack.isDirectory()) {
                	pack(out, sourceDir,excludeDirectory, entryPath);
                	
                }else{
                	File file = new File(sourceDir);
                	addFileToZip(file, out, entryPath);
                }
            	
            	
    		}
            
            

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
          //  e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("压缩",e);
	        }
        } finally {
            IOUtils.closeQuietly(out);
        }
    	
    	
    }
    
    

    /**
     * 把一个目录打包到一个指定的zip文件中
     * 
     * @param out
     * @param sourceDir 待压缩目录
     * @param excludeDirectory 排除目录下的文件
     * @param entryPath zip中文件的逻辑路径
     */
    private static void pack(ZipArchiveOutputStream out, String sourceDir,String excludeDirectory, String entryPath) {
        InputStream ins = null;
        File dir = new File(sourceDir);
        File[] files = dir.listFiles();
        if (files == null || files.length < 1) {
            return;
        }
        
        //排除路径
        String excludeDirectoryPath = null;
        if(excludeDirectory != null){
        	excludeDirectoryPath = new File(excludeDirectory).getAbsolutePath();
        }
        try {
 
            for (int i = 0; i < files.length; i++) {
            	
            	//判断开始部分是否与二参数相同。不区分大小写
	    		//StringUtils.startsWithIgnoreCase("中国共和国人民", "中国")
	    		if(excludeDirectoryPath != null && StringUtils.startsWithIgnoreCase(files[i].getAbsolutePath(), excludeDirectoryPath)){
	    			//添加目录
	    			addDirectoryToZip(files[i], out, entryPath);
	    			continue;
	    		}
            	
                // 判断此文件是否是一个文件夹
                if (files[i].isDirectory()) {
                    if(files[i].listFiles().length>0){
                    	pack(out, files[i].getAbsolutePath(), excludeDirectory,entryPath + files[i].getName() + File.separator);
                    }else{
                        addFileToZip(files[i], out, entryPath);
                    }
                } else {
                	
                    addFileToZip(files[i], out, entryPath);
                }
            }
            out.flush();
        } catch (FileNotFoundException e) {
           // e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("把一个目录打包到一个指定的zip文件中",e);
	        }
        } catch (IOException e) {
         //   e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("把一个目录打包到一个指定的zip文件中",e);
	        }
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }
    
	
	/**
     * 把一个文件打包到一个指定的zip文件中
     * @param source 待压缩文件
     * @param targetZip 输出文件
     * @param entryPath 压缩内文件逻辑路径。如static/
    
    public static void pack(File source, String targetZip,String entryPath){
        if(!entryPath.endsWith(File.separator)&&entryPath != null && !entryPath.isEmpty()){
            entryPath+=File.separator;
        }
        ZipArchiveOutputStream out = null;
        try {
            out = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(new File(targetZip))));
            out.setEncoding("UTF-8");
            addFileToZip(source, out, entryPath);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
          //  e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("压缩",e);
	        }
        } finally {
            IOUtils.closeQuietly(out);
        }
    } */
	
    
    /**
     * 添加目录到Zip
     * @param file
     * @param out
     * @param entryPath
     */
    private static void addDirectoryToZip(File file, ZipArchiveOutputStream out, String entryPath) {
        
        try {
             
            String path=entryPath + file.getName();
            if(file.isDirectory()){
                path=formatDirPath(path); //为了在压缩文件中包含空文件夹
            }
            ZipArchiveEntry entry = new ZipArchiveEntry(path);
            entry.setTime(file.lastModified());
            // entry.setSize(files[i].length());
            out.putArchiveEntry(entry);

            out.closeArchiveEntry();
        } catch (IOException e) {
          //  e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("添加目录到Zip",e);
	        }
        } finally {
           
        }
    }
    
    
    /**
     * 添加文件到Zip
     * @param file
     * @param out
     * @param entryPath
     */
    private static void addFileToZip(File file, ZipArchiveOutputStream out, String entryPath) {
        InputStream ins = null;
        try {
             
            String path=entryPath + file.getName();
            if(file.isDirectory()){
                path=formatDirPath(path); //为了在压缩文件中包含空文件夹
            }
            ZipArchiveEntry entry = new ZipArchiveEntry(path);
            entry.setTime(file.lastModified());
            // entry.setSize(files[i].length());
            out.putArchiveEntry(entry);
            if(!file.isDirectory()){
                ins = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                IOUtils.copy(ins, out);
            }
            out.closeArchiveEntry();
        } catch (IOException e) {
          //  e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("添加文件到Zip",e);
	        }
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }

    private static String formatDirPath(String dir){
        if(!dir.endsWith(File.separator)){
            dir+=File.separator;
        }
        return dir;
    }

	/********************************************************************************/
    
    /**
     * 解压zip文件到指定目录
     * @param zipPath 压缩文件
     * @param destDir 压缩文件解压后保存的目录
     */
    public static void unZip(String zipPath, String destDir) {
        ZipArchiveInputStream ins = null;
        OutputStream os = null;
        File zip = new File(zipPath);
        if (!zip.exists()) {
            return;
        }
        File dest = new File(destDir);
        if (!dest.exists()) {
            dest.mkdirs();
        }
        destDir=formatDirPath(destDir);
        try {
            ins = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipPath)), "UTF-8");
            ZipArchiveEntry entry = null;
            while ((entry = ins.getNextZipEntry()) != null) {
            	//执行目录检查，过滤文件路径名称包含"../"这种特殊字符
            	String name = FileUtil.toRelativePath(entry.getName());
                if (entry.isDirectory()) {
                    File directory = new File(destDir, name);
                    directory.mkdirs();
                    directory.setLastModified(entry.getTime());
                } else {
                    String absPath=formatPath(destDir+name);
                    mkdirsForFile(absPath);
                    File tmpFile=new File(absPath);
                    os=new BufferedOutputStream(new FileOutputStream(tmpFile));
                    IOUtils.copy(ins, os);
                    IOUtils.closeQuietly(os);
                    tmpFile.setLastModified(entry.getTime());
                }
            }
 
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        //    e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("解压zip文件到指定目录",e);
	        }
        } catch (IOException e) {
            // TODO Auto-generated catch block
        //    e.printStackTrace();
        	if (logger.isErrorEnabled()) {
	            logger.error("解压zip文件到指定目录",e);
	        }
        } finally {
            IOUtils.closeQuietly(ins);
        }
    }
    private static String formatPath(String path){
        path=path.replace('\\', File.separatorChar);
        path=path.replace('/', File.separatorChar);
        return path;
    }
    private static void mkdirsForFile(String filePath){
        String absPath=filePath;
        String tmpPath=absPath.substring(0,absPath.lastIndexOf(File.separator));
        File tmp=new File(tmpPath);
        if(!tmp.exists()){
            tmp.mkdirs();
        }
    }
    

    
}
