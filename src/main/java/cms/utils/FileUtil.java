package cms.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 文件工具
 * @author Gao
 *
 */
public class FileUtil {
	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	
	
	
	
	
	/**
	 * InputStream转byte
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static byte[] inputStreamToByteArray(InputStream input)
			  throws IOException{
	  ByteArrayOutputStream output = new ByteArrayOutputStream();
	  copy(input, output);
	  return output.toByteArray();
	}
	private static int copy(InputStream input, OutputStream output)throws IOException{
		byte[] buffer = new byte[4096];
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		    count += n;
		}
		if (count > 2147483647L) {
			return -1;
		}
		return (int)count;
	}
	
	/**
	 * 生成文件夹
	 * @param path路径
	 */
	public static Boolean createFolder(String path){
		
		//生成文件目录
		File dirFile  = new File(PathUtil.path()+File.separator+path);
	    if (!(dirFile.exists()) && !(dirFile.isDirectory())){
	    	dirFile.mkdirs();  
	    	return true;
	    }  
	    return false;
	}
	

    /**  
     * 读文件
     * @param path 路径
     * @param encoding 编码  UTF-8
     * @return 文本字符串
     */   
     public static String readFileToString(String path,String encoding){ 
    	 
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
     public static String readFileBomToString(String path,String encoding){ 
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
      public static List<String> readLines(File file,String encoding){ 
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
    public static void writeStringToFile(String path,String content,String encoding, boolean append){
    	
    	try {
			FileUtils.writeStringToFile(new File(PathUtil.path()+File.separator+path), content, encoding,append);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("写文件(如果目标文件不存在，FileUtils会自动创建)"+ path,e);
	        }
		}  
    } 
    /**
     * 去除字符串中的空格、回车、换行符、制表符
     * @param str
     * @return
     */
    public static String replaceBlank(String content) {
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
     * 验证文件后缀
     * @param fileName 文件名称(含后缀)
     * @param formatList 允许格式列表
     */
    public static boolean validateFileSuffix(String fileName,List<String> formatList){
    	//取得文件后缀
		String suffix = getExtension(fileName);
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
	 * 获取文件后缀名
	 * @param path 
	 */
	public static String getExtension(String path){
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
	 * 验证文件名
	 * @param fileName 文件名称
	 * @return
	 * @throws Exception
	 */
	public static boolean validateFileName(String fileName){
		return fileName.matches("^[^\\/\\<>\\*\\?\\:\"\\|\\%]+$");
	}
    /**
	 *  获取文件名,不包含后缀
	 * @param path 
	 */
	public static String getBaseName(String path){
		if(path != null && !"".equals(path.trim())){
			return FilenameUtils.getBaseName(path);
		}
		return "";
		
	}
	
	
	/**
	 *  获取文件名,含后缀
	 * @param path 
	 */
	public static String getName(String path){
		if(path != null && !"".equals(path.trim())){
			return FilenameUtils.getName(path);
		}
		return "";
		
	}
	
	
	/**
	 * 获取文件的目录,不忽略分割符
	 * @param fullFileName 完整路径名称
	 */
	public static String getFullPath(String fullFileName){
		if(fullFileName != null && !"".equals(fullFileName.trim())){
			return FilenameUtils.getFullPath(fullFileName);
			
		}
		return "";
		
	}

	
	/**
	 * 获取当前系统格式化路径
	 * windows下 D:/Hello../oo./h/World.txt 转为 D:\Hello..\oo.\h\World.txt
	 * @return
	 */
	public static String normalize(String path){
		return FilenameUtils.normalize(path);
		
	}
	
	/**
	 * 系统路径转为左斜杆路径(如:  file/f/f/)
	 * @param path 
	 */
	public static String toLeftSlant(String path){
		String systemPath = "";
		if(path != null && !"".equals(path.trim())){
			return path.replace(File.separator,"/");
			
			
		}
		return systemPath;
	}
	/**
	 * 左斜杆路径转为系统路径(如:  file/f/f/)
	 * @param path 
	 */
	public static String toSystemPath(String path){
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
	public static String toRelativePath(String path){
		String systemPath = "";
		if(path != null && !"".equals(path.trim())){
			systemPath = StringUtils.replace(path, "..", "-");
			systemPath = StringUtils.replace(systemPath, "%", "-");//百分号替换为横杆
		}
		return systemPath;
	}
	
	/**
	 * 系统路径转为下划线(如:  file/f/f/转为file_f_f_)
	 * @param path 
	 */
	public static String toUnderline(String path){
		String systemPath = "";
		if(path != null && !"".equals(path.trim())){
			return path.replace(File.separator,"_");
			
			
		}
		return systemPath;
	}
	/**  
     * 重命名文件或文件夹（本方法在windows系统下有可能不成功）
     *  
     * @param resFilePath 源文件路径  
     * @param newFileName 重命名  
     * @return 操作成功标识  
     */  
    public static boolean renameFile(String resFilePath, String newFileName) {
    	File resFile = new File(PathUtil.path()+File.separator+resFilePath);
        //源文件父路径
        String resParentPath = resFile.getParent();   
        File newFile = new File(resParentPath+File.separator+newFileName);   
        return resFile.renameTo(newFile);   
    } 
    /**  
     * 重命名文件夹  
     *  
     * @param resFilePath 源文件路径  
     * @param newFileName 重命名  
     * @return 操作成功标识  
     */   
    public static boolean renameDirectory(String resFilePath, String newFileName) {
    	File resFile = new File(PathUtil.path()+File.separator+resFilePath);
        //源文件父路径
        String resParentPath = resFile.getParent();   
        File newFile = new File(resParentPath+File.separator+newFileName);   
       
        //移动文件夹,并重新命名
        try {
			FileUtils.moveDirectory(resFile,newFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("移动文件错误。源路径："+resFile.getAbsoluteFile()+" 目标路径："+newFile.getAbsolutePath(),e);
	        }
			return false;
		} 
        return true;
    }
    /**  
     * 复制目录
     *  
     * @param resDirPath 源目录路径  
     * @param distFolder  目标文件夹  
     * @IOException 当操作发生异常时抛出  
     */   
    public static void copyDirectory(String resDirPath, String distFolder) throws IOException {  
    	
        File copyDir = new File(PathUtil.path()+File.separator+resDirPath);   
        File distFile = new File(PathUtil.path()+File.separator+(distFolder == null || "".equals(distFolder.trim()) ? "" :distFolder+File.separator)); 
   
		if (copyDir.isDirectory()) { 
			FileUtils.copyDirectoryToDirectory(copyDir, distFile);   
        }

    }
    
    /**
	 * 删除目录
	 * @param dir对象
	 */
	public static Boolean removeDirectory(File dir){
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
	
	
}
