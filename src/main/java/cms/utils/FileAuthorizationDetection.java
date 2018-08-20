package cms.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;




/**
 * 文件权限
 *
 */
public class FileAuthorizationDetection {
	private static final Logger logger = LogManager.getLogger(FileAuthorizationDetection.class);
	
	/**
	 * 检测文件权限
	 * @return (返回不可读或不可写的绝对路径)
	 */
	public static String detection(){
		
		final String[] info = {null};//如果需要内部类修改局部变量，并且能传到外部类。因为final只是不能改变它的指向,但是可以改变它的属性,所以可以用数组来代替
		//检测路径
		String path = PathUtil.path()+File.separator;
		//排除目录路径
		String excludeDirectoryPath = PathUtil.path()+File.separator+"file"+File.separator;

		try {
			
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>(){

				//访问子目录之前触发该方法
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					//排除路径
			    	if(StringUtils.startsWithIgnoreCase(dir.toString(), excludeDirectoryPath)){	
			    		return FileVisitResult.SKIP_SIBLINGS;//代表“继续访问”，但不访问该文件或目录的兄弟文件或目录
		    		}
					return FileVisitResult.CONTINUE;//代表“继续访问”的后续行为
				}
				//访问子目录之后触发该方法
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if(!Files.isHidden(dir)){//如果不是隐藏文件
						//检测文件夹权限
						if(!Files.isReadable(dir) || !Files.isWritable(dir)){
							//Files.isWritable判断文件夹是否可写方法不可靠，对于匿名(只读)访问一个网络共享文件夹,isWritable返回是true (匿名用户只有读取权限的共享文件夹Files.isWritable(...)总是返回true)
							
							
							info[0] = dir+(Files.isReadable(dir) == false ? " [目录不可读]":"")+(Files.isWritable(dir) == false ? " [目录不可写]":"");
							return FileVisitResult.TERMINATE;//代表“终止访问”的后续行为
						}
					}
					
					return FileVisitResult.CONTINUE;//代表“继续访问”的后续行为
				}
				
				//访问文件时触发该方法
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(!Files.isHidden(file)){//如果不是隐藏文件
						//检测文件夹权限
						if(!Files.isReadable(file) || !Files.isWritable(file)){
								info[0] = file+(Files.isReadable(file) == false ? " [文件不可读]":"")+(Files.isWritable(file) == false ? " [文件不可写]":"");
							return FileVisitResult.TERMINATE;//代表“终止访问”的后续行为
						}
					}
					return FileVisitResult.CONTINUE;//代表“继续访问”的后续行为
				}
				
				//访问文件失败时触发该方法
				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
				
				
				
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("检测文件权限错误",e);
	        }
		}
		return info[0];
	}
}
