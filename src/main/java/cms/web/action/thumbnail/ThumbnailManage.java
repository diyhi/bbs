package cms.web.action.thumbnail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import net.coobird.thumbnailator.Thumbnails;
import cms.bean.thumbnail.Thumbnail;
import cms.bean.topic.ImageInfo;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.util.ImageReadHelper;


/**
 * 缩略图管理
 * @author Administrator
 *
 */
@Component("thumbnailManage")
public class ThumbnailManage {
	private static final Logger logger = LogManager.getLogger(ThumbnailManage.class);
	
	@Resource LocalFileManage localFileManage;
	
	/**
	 * 取得缩略图标记
	 */
	private Map<String,String> getThumbnailMarker(){
		Map<String,String> thumbnailMarker = new HashMap<String,String>();//key:缩略图规格  value:增删标记
		
		String dir = PathUtil.path()+File.separator+"file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator;
		
		File f = new File(dir);   
		//清空内容
		String[] extensions = null;//后缀名{"doc", "pdf"}
		boolean recursive = false;//是否递归

		Collection<File> files = FileUtils.listFiles(f, extensions, recursive);

		// 迭代输出
		for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
		    File file = iterator.next();
		    if(!file.isHidden()){//不是隐藏文件
		    	 List<String> markerList = FileUtil.readLines(file,"utf-8");
		    	 if(markerList != null && markerList.size() >0){
		    		 StringBuffer value = new StringBuffer("");
		    		 for(String marker : markerList){
		    			 value.append(marker);
		    		 }
		    		 if(value != null && !"".equals(value.toString().trim())){
		    			 thumbnailMarker.put(file.getName().substring(0,file.getName().lastIndexOf(".")), value.toString());
		    		 }
		    		
		    	 }	 
		    }
		
		}
		return thumbnailMarker;
	}
	
	/**
	 * 处理缩略图(由定时器调用)
	 */
	public void treatmentThumbnail(){
		String path = PathUtil.path()+File.separator+"file"+File.separator+"topic"+File.separator;

		final Map<String,String> thumbnailMarker = this.getThumbnailMarker(); 
		SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>(){ 
			
			@Override     
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) {  
				if("image".equals(dir.getFileName().toString())){
					for(Entry<String, String> entry : thumbnailMarker.entrySet()){
						
						if("+".equals(entry.getValue())){//生成缩略图
							generationThumbnail(dir,entry.getKey());
						}else if("-".equals(entry.getValue())){
							//删除目录
							deleteThumbnailDir(dir,entry.getKey());
						}else if("--".equals(entry.getValue())){
							//删除目录
							deleteThumbnailDir(dir,entry.getKey());
						}
					}	
					
				}
				return FileVisitResult.CONTINUE;    
			}

			
		}; 
		try {
			java.nio.file.Files.walkFileTree(Paths.get(path), finder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("处理缩略图(由定时器调用)",e);
	        }
		} 
		
		//标记文件路径
		String marker_dir = PathUtil.path()+File.separator+"file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator;
		
		//处理标记文件
		for(Entry<String, String> entry : thumbnailMarker.entrySet()){
			if("+".equals(entry.getValue())){//生成缩略图
				//读取标记文件
				List<String> markerList = FileUtil.readLines(new File(marker_dir+entry.getKey()+".txt"),"utf-8");
		    	if(markerList != null && markerList.size() >0){
		    		StringBuffer value = new StringBuffer("");
		    		for(String marker : markerList){
		    			value.append(marker);
		    		}
		    		if(value != null && "+".equals(value.toString().trim())){//如果标记文件状态不变，则删除标记
		    			localFileManage.deleteFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+entry.getKey()+".txt");
		    		}
		    		
		    	}
				
			}else if("-".equals(entry.getValue())){
				
				//读取标记文件
				List<String> markerList = FileUtil.readLines(new File(marker_dir+entry.getKey()+".txt"),"utf-8");
		    	if(markerList != null && markerList.size() >0){
		    		StringBuffer value = new StringBuffer("");
		    		for(String marker : markerList){
		    			value.append(marker);
		    		}
		    		if(value != null && "-".equals(value.toString().trim())){//如果标记文件状态不变，则增加一行-标记
		    			FileUtil.writeStringToFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+entry.getKey()+".txt","-","utf-8",true);
		    		}
	
		    	}
				
				
			}else if("--".equals(entry.getValue())){
				//读取标记文件
				List<String> markerList = FileUtil.readLines(new File(marker_dir+entry.getKey()+".txt"),"utf-8");
		    	if(markerList != null && markerList.size() >0){
		    		StringBuffer value = new StringBuffer("");
		    		for(String marker : markerList){
		    			value.append(marker);
		    		}
		    		if(value != null && "--".equals(value.toString().trim())){//如果标记文件状态不变，则删除标记
		    			localFileManage.deleteFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+entry.getKey()+".txt");
		    		}
	
		    	}
			}
		}	
		
	}
	
	/**
	 * 按规格组生成缩略图
	 * @param dir NIO路径对象
	 * @param specificationGroup 规格组
	 */
	private void generationThumbnail(Path dir,String specificationGroup){
		String[] extensions = null;//后缀名{"doc", "pdf"}
		boolean recursive = false;//是否递归
		Collection<File> files = FileUtils.listFiles(dir.toFile(), extensions, recursive);
		if(files != null && files.size() >0){
			//宽
			Integer width = 10;
			//高
			Integer high = 10;
			//解析规格组
			String[] specification = specificationGroup.split("x");
			width = Integer.parseInt(specification[0]);
			high = Integer.parseInt(specification[1]);
			//生成目录
			File file_dir = new File(dir.toAbsolutePath().toString()+File.separator+specificationGroup+File.separator);
			if (!file_dir.exists()) {//如果目录不存在
				file_dir.mkdirs();//生成目录
			}
			
			// 迭代输出
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			    File file = iterator.next();
			    
			    this.createImage(file,dir.toAbsolutePath().toString()+File.separator+specificationGroup+File.separator+file.getName(),width,high);
			    
			    /**
			    try {
					Thumbnails.of(file)        
					.size(width,high)        
					.toFile(new File(dir.toAbsolutePath().toString()+File.separator+specificationGroup+File.separator+file.getName()));		
			    } catch (IOException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
			    	if (logger.isErrorEnabled()) {
			            logger.error("生成缩略图",e);
			        }
				}**/
			}
		}
	}

	/**
	 * 删除缩略图目录
	 * @param dir NIO路径对象
	 * @param specificationGroup 规格组
	 */
	private void deleteThumbnailDir(Path dir,String specificationGroup){
		File f = new File(dir.toAbsolutePath()+File.separator+specificationGroup); 
		FileUtil.removeDirectory(f);
		
	}
	

	

	/**
	 * 生成缩略图
	 * @param sourcePath 源图片路径
	 * @param outputPath 输出图片路径
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	private void createImage(File sourcePath,String outputPath,int scaleWidth,int scaleHeight) {
		FileInputStream inStream = null;
		try {
			//获取文件后缀名
			String extension = FilenameUtils.getExtension(sourcePath.getAbsolutePath());
			if(extension != null && "gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourcePath)
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}else{//阿里巴巴工具读取某些gif图片会出错
				inStream = new FileInputStream(sourcePath);
				ImageWrapper imageWrapper = ImageReadHelper.read(inStream);
				//outputQuality：输出的图片质量，范围：0.0~1.0，1为最高质量。注意使用该方法时输出的图片格式必须为jpg（即outputFormat("jpg")）。否则若是输出png格式图片，则该方法作用无效
				//watermark指定了水印，通过Positions指定水印位置，BufferedImage指定水印图片，第三个参数指定了水印的不透明性，范围为(0~1.0f),1.0f为不透明。
			//	.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("watermark.png")), 0.5f)    
				Thumbnails.of(imageWrapper.getAsBufferedImage())        
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} catch (SimpleImageException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("生成缩略图异常"+sourcePath.getAbsolutePath(),e);
	        }
		}finally {	
			if(inStream != null){
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
			//		e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("生成缩略图关闭输入流错误",e);
			        }
				}
			}
			
		}
	}
	/**
	 * 生成缩略图
	 * @param sourcePath 源图片路径
	 * @param outputPath 输出图片路径
	 * @param extension 后缀名
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public void createImage(InputStream sourceInputStream,String outputPath,String extension,int scaleWidth,int scaleHeight) {
		try {
			if(extension != null && "gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourceInputStream)
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}else{//阿里巴巴工具读取某些gif图片会出错
				ImageWrapper imageWrapper = ImageReadHelper.read(sourceInputStream);
				//outputQuality：输出的图片质量，范围：0.0~1.0，1为最高质量。注意使用该方法时输出的图片格式必须为jpg（即outputFormat("jpg")）。否则若是输出png格式图片，则该方法作用无效
				//watermark指定了水印，通过Positions指定水印位置，BufferedImage指定水印图片，第三个参数指定了水印的不透明性，范围为(0~1.0f),1.0f为不透明。
			//	.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("watermark.png")), 0.5f)    
				Thumbnails.of(imageWrapper.getAsBufferedImage())        
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} catch (SimpleImageException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图",e);
	        }
		}finally {	
			if(sourceInputStream != null){
				try {
					sourceInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
			//		e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("生成缩略图关闭输入流错误",e);
			        }
				}
			}
			
		}
	}
	/**
	 * 生成缩略图
	 * @param sourcePath 源图片路径
	 * @param outputPath 输出图片路径
	 * @param extension 后缀名
	 * @param x 坐标X轴
	 * @param y 坐标Y轴
	 * @param width 剪裁区域宽
	 * @param high 剪裁区域高
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public void createImage(InputStream sourceInputStream,String outputPath,String extension,int x,int y,int width,int height,int scaleWidth,int scaleHeight) {
		try {
			if(extension != null && "gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourceInputStream)
				.sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}else{//阿里巴巴工具读取某些gif图片会出错
				ImageWrapper imageWrapper = ImageReadHelper.read(sourceInputStream);
				//outputQuality：输出的图片质量，范围：0.0~1.0，1为最高质量。注意使用该方法时输出的图片格式必须为jpg（即outputFormat("jpg")）。否则若是输出png格式图片，则该方法作用无效
				//watermark指定了水印，通过Positions指定水印位置，BufferedImage指定水印图片，第三个参数指定了水印的不透明性，范围为(0~1.0f),1.0f为不透明。
			//	.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File("watermark.png")), 0.5f)    
				Thumbnails.of(imageWrapper.getAsBufferedImage())   
				.sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} catch (SimpleImageException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图",e);
	        }
		}finally {	
			if(sourceInputStream != null){
				try {
					sourceInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
			//		e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("生成缩略图关闭输入流错误",e);
			        }
				}
			}
			
		}
		
	}

	/**
	 * 异步增加缩略图
	 * @param thumbnailList 缩略图对象集合
	 * @param imageInfoList 图片信息集合
	*/
	@Async
	public void addThumbnail(List<Thumbnail> thumbnailList,List<ImageInfo> imageInfoList){
		
		
		for(Thumbnail thumbnail : thumbnailList){
			//宽
			Integer width = 10;
			//高
			Integer high = 10;
			//解析规格组
			String[] specification = thumbnail.getSpecificationGroup().split("x");
			width = Integer.parseInt(specification[0]);
			high = Integer.parseInt(specification[1]);
			
			for(ImageInfo imageInfo : imageInfoList){
				String path =  PathUtil.path()+File.separator+FileUtil.toSystemPath(imageInfo.getPath());
				
				
				//生成目录
				File file_dir = new File(path+thumbnail.getSpecificationGroup()+File.separator);
				if (!file_dir.exists()) {//如果目录不存在
					file_dir.mkdirs();//生成目录
				}
				File file = new File(path+imageInfo.getName());
				if(file.exists()){
					this.createImage(file,file_dir+File.separator+imageInfo.getName(),width,high);
				}
			}
			
		}	
	} 
	
	/**
	 * 异步删除缩略图
	 * @param thumbnailList 缩略图对象集合
	 * @param imageInfoList 图片信息集合
	 */
	@Async
	public void deleteThumbnail(List<Thumbnail> thumbnailList,List<ImageInfo> imageInfoList){
		for(Thumbnail thumbnail : thumbnailList){
			for(ImageInfo imageInfo : imageInfoList){
				String path =  FileUtil.toSystemPath(imageInfo.getPath());
				localFileManage.deleteFile(path+thumbnail.getSpecificationGroup()+File.separator+imageInfo.getName());
			}
			
		}	
	}
	
	
}
