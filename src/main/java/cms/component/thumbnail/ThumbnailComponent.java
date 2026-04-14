package cms.component.thumbnail;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

import cms.component.fileSystem.localImpl.LocalFileComponent;
import cms.dto.topic.ImageInfo;
import cms.model.thumbnail.Thumbnail;
import jakarta.annotation.Resource;
import net.coobird.thumbnailator.Thumbnails;
import cms.utils.FileUtil;
import cms.utils.PathUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


import javax.imageio.ImageIO;


/**
 * 缩略图组件
 * @author Administrator
 *
 */
@Component("thumbnailComponent")
public class ThumbnailComponent {
	private static final Logger logger = LogManager.getLogger(ThumbnailComponent.class);
	
	@Resource LocalFileComponent localFileComponent;
	
	/**
	 * 取得缩略图标记
	 */
	private Map<String,String> getThumbnailMarker(){
		Map<String,String> thumbnailMarker = new HashMap<String,String>();//key:缩略图规格  value:增删标记
		
		String dir = PathUtil.defaultExternalDirectory()+File.separator+"file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator;
		
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
		    		 if(!value.toString().trim().isEmpty()){
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
		String path = PathUtil.defaultExternalDirectory()+File.separator+"file"+File.separator+"topic"+File.separator;

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
		String marker_dir = PathUtil.defaultExternalDirectory()+File.separator+"file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator;
		
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
		    			localFileComponent.deleteFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+entry.getKey()+".txt");
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
		    		if("-".equals(value.toString().trim())){//如果标记文件状态不变，则增加一行-标记
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
		    		if("--".equals(value.toString().trim())){//如果标记文件状态不变，则删除标记
		    			localFileComponent.deleteFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+entry.getKey()+".txt");
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
		if(files.size() >0){
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
			if("gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourcePath)
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}else if("png".equalsIgnoreCase(extension)) {
                inStream = new FileInputStream(sourcePath);

                // 使用 TwelveMonkeys 增强的 ImageIO.read() 来读取PNG文件
                BufferedImage sourceImage = ImageIO.read(inStream);
                if (!sourceImage.getColorModel().hasAlpha()) {//解决某些png图片压缩后变白的问题
                    // 如果原图没有Alpha通道，创建一个新的ARGB类型的BufferedImage
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    // 将原图的内容绘制到新的图像中
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }
                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight)
                        // outputQuality只对JPEG格式生效，PNG格式该方法无效
                        .toFile(outputPath);

            }else if("webp".equalsIgnoreCase(extension)) {
                inStream = new FileInputStream(sourcePath);

                // 使用 TwelveMonkeys 增强的 ImageIO.read() 读取 WebP 图片
                BufferedImage sourceImage = ImageIO.read(inStream);

                // 如果原图有alpha通道，确保输出的BufferedImage类型也支持alpha。
                // WebP支持有损和无损带alpha通道的图片。
                // 如果原图是RGB类型（无透明度），则这一步没有影响。
                if (sourceImage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }

                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight) // 缩放
                        .outputQuality(1) // 设置压缩质量，范围为 0.0 ~ 1.0
                        .toFile(outputPath);

            }else{//jpg或bmp格式
                inStream = new FileInputStream(sourcePath);

                //使用 TwelveMonkeys解决某些jpg图片压缩后变红的问题
                // ImageIO.read() 会自动使用 TwelveMonkeys 的插件来读取 JPEG、BMP 和 WebP 格式
                BufferedImage sourceImage = ImageIO.read(inStream);
                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight)
                        // outputQuality只对JPEG格式生效，PNG格式该方法无效
                        .outputQuality(1)
                        .toFile(outputPath);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} finally {
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
	 * @param sourceInputStream 源图片文件流
	 * @param outputPath 输出图片路径
	 * @param extension 后缀名
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public void createImage(InputStream sourceInputStream,String outputPath,String extension,int scaleWidth,int scaleHeight) {
		try {
            if("gif".equalsIgnoreCase(extension)){
                Thumbnails.of(sourceInputStream)
                        .size(scaleWidth,scaleHeight)
                        .outputQuality(1)
                        .toFile(outputPath);
            }else if("png".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 来读取PNG文件
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                if (!sourceImage.getColorModel().hasAlpha()) {//解决某些png图片压缩后变白的问题
                    // 如果原图没有Alpha通道，创建一个新的ARGB类型的BufferedImage
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    // 将原图的内容绘制到新的图像中
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }
                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight)
                        .outputFormat("png")
                        .toFile(outputPath);

            }else if("webp".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 读取 WebP 图片
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);

                // 如果原图有alpha通道，确保输出的BufferedImage类型也支持alpha。
                // WebP支持有损和无损带alpha通道的图片。
                // 如果原图是RGB类型（无透明度），则这一步没有影响。
                if (sourceImage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }

                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight) // 缩放
                        .outputQuality(1) // 设置压缩质量，范围为 0.0 ~ 1.0
                        .outputFormat("webp") // 确保输出格式为 webp
                        .toFile(outputPath);

            }else{//jpg或bmp格式
                //使用 TwelveMonkeys解决某些jpg图片压缩后变红的问题
                // ImageIO.read() 会自动使用 TwelveMonkeys 的插件来读取 JPEG、BMP 和 WebP 格式
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight)
                        .outputQuality(1) // outputQuality只对JPEG格式生效，PNG格式该方法无效
                        .toFile(outputPath);
            }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} /**catch (SimpleImageException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图",e);
	        }
		}**/
         finally {
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
	 * @param sourceInputStream 源图片文件流
	 * @param outputPath 输出图片路径
	 * @param extension 后缀名
	 * @param x 坐标X轴
	 * @param y 坐标Y轴
	 * @param width 剪裁区域宽
	 * @param height 剪裁区域高
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public void createImage(InputStream sourceInputStream,String outputPath,String extension,int x,int y,int width,int height,int scaleWidth,int scaleHeight) {
		try {
			if("gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourceInputStream)
				.sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.toFile(outputPath);
			}else if("png".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 来读取PNG文件
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                if (!sourceImage.getColorModel().hasAlpha()) {//解决某些png图片压缩后变白的问题
                    // 如果原图没有Alpha通道，创建一个新的ARGB类型的BufferedImage
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    // 将原图的内容绘制到新的图像中
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }
                Thumbnails.of(sourceImage)
                        .sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
                        .size(scaleWidth, scaleHeight)
                        .outputFormat("png")
                        .toFile(outputPath);

            }else if("webp".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 读取 WebP 图片
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);

                // 如果原图有alpha通道，确保输出的BufferedImage类型也支持alpha。
                // WebP支持有损和无损带alpha通道的图片。
                // 如果原图是RGB类型（无透明度），则这一步没有影响。
                if (sourceImage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }

                Thumbnails.of(sourceImage)
                        .sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
                        .size(scaleWidth, scaleHeight) // 缩放
                        .outputQuality(1) // 设置压缩质量，范围为 0.0 ~ 1.0
                        .outputFormat("webp") // 确保输出格式为 webp
                        .toFile(outputPath);

            }else{//jpg或bmp格式
                //使用 TwelveMonkeys解决某些jpg图片压缩后变红的问题
                // ImageIO.read() 会自动使用 TwelveMonkeys 的插件来读取 JPEG、BMP 和 WebP 格式
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                Thumbnails.of(sourceImage)
                        .sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
                        .size(scaleWidth, scaleHeight)
                        // outputQuality只对JPEG格式生效，PNG格式该方法无效
                        .outputQuality(1)
                        .toFile(outputPath);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} finally {
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
	 * @param sourceInputStream 源图片文件流
	 * @param extension 后缀名
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public byte[] createImage(InputStream sourceInputStream,String extension,int scaleWidth,int scaleHeight) {
		//创建储存图片二进制流的输出流
		ByteArrayOutputStream outputStream = null;
		byte[] picture = null;
		try {
			outputStream = new ByteArrayOutputStream();
			
			if("gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourceInputStream)
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.outputFormat(extension.toLowerCase())
				.toOutputStream(outputStream);
			}else if("png".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 来读取PNG文件
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                if (!sourceImage.getColorModel().hasAlpha()) {//解决某些png图片压缩后变白的问题
                    // 如果原图没有Alpha通道，创建一个新的ARGB类型的BufferedImage
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    // 将原图的内容绘制到新的图像中
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }
                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight)
                        .outputQuality(1)
                        .outputFormat(extension.toLowerCase())
                        .toOutputStream(outputStream);

            }else if("webp".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 读取 WebP 图片
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);

                // 如果原图有alpha通道，确保输出的BufferedImage类型也支持alpha。
                // WebP支持有损和无损带alpha通道的图片。
                // 如果原图是RGB类型（无透明度），则这一步没有影响。
                if (sourceImage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }

                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight) // 缩放
                        .outputQuality(1) // 设置压缩质量，范围为 0.0 ~ 1.0
                        .outputFormat(extension.toLowerCase())
                        .toOutputStream(outputStream);

            }else{//jpg或bmp格式
                //使用 TwelveMonkeys解决某些jpg图片压缩后变红的问题
                // ImageIO.read() 会自动使用 TwelveMonkeys 的插件来读取 JPEG、BMP 和 WebP 格式
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                Thumbnails.of(sourceImage)
                        .size(scaleWidth, scaleHeight)
                        // outputQuality只对JPEG格式生效，PNG格式该方法无效
                        .outputQuality(1)
                        .outputFormat(extension.toLowerCase())
                        .toOutputStream(outputStream);
            }
			picture = outputStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
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
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("生成缩略图关闭输出流错误",e);
			        }
				}
			}
		}
		
		return picture;
	}
	
	
	/**
	 * 生成缩略图
	 * @param sourceInputStream 源图片文件流
	 * @param extension 后缀名
	 * @param x 坐标X轴
	 * @param y 坐标Y轴
	 * @param width 剪裁区域宽
	 * @param height 剪裁区域高
	 * @param scaleWidth 缩放宽
	 * @param scaleHeight 缩放高
	**/
	public byte[] createImage(InputStream sourceInputStream,String extension,int x,int y,int width,int height,int scaleWidth,int scaleHeight) {
		//创建储存图片二进制流的输出流
		ByteArrayOutputStream outputStream = null;
		byte[] picture = null;
		
		
		try {
			outputStream = new ByteArrayOutputStream();
			
			if("gif".equalsIgnoreCase(extension)){
				Thumbnails.of(sourceInputStream)
				.sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
				.size(scaleWidth,scaleHeight)
				.outputQuality(1)
				.outputFormat(extension.toLowerCase())
				.toOutputStream(outputStream);
			}else if("png".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 来读取PNG文件
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                if (!sourceImage.getColorModel().hasAlpha()) {//解决某些png图片压缩后变白的问题
                    // 如果原图没有Alpha通道，创建一个新的ARGB类型的BufferedImage
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    // 将原图的内容绘制到新的图像中
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }
                Thumbnails.of(sourceImage)
                        .sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
                        .size(scaleWidth, scaleHeight)
                        .outputQuality(1)
                        .outputFormat(extension.toLowerCase())
                        .toOutputStream(outputStream);

            }else if("webp".equalsIgnoreCase(extension)) {
                // 使用 TwelveMonkeys 增强的 ImageIO.read() 读取 WebP 图片
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);

                // 如果原图有alpha通道，确保输出的BufferedImage类型也支持alpha。
                // WebP支持有损和无损带alpha通道的图片。
                // 如果原图是RGB类型（无透明度），则这一步没有影响。
                if (sourceImage.getType() != BufferedImage.TYPE_INT_ARGB) {
                    BufferedImage newImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    newImage.getGraphics().drawImage(sourceImage, 0, 0, null);
                    sourceImage = newImage;
                }

                Thumbnails.of(sourceImage)
                        .sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
                        .size(scaleWidth, scaleHeight) // 缩放
                        .outputQuality(1) // 设置压缩质量，范围为 0.0 ~ 1.0
                        .outputFormat(extension.toLowerCase())
                        .toOutputStream(outputStream);

            }else{//jpg或bmp格式
                //使用 TwelveMonkeys解决某些jpg图片压缩后变红的问题
                // ImageIO.read() 会自动使用 TwelveMonkeys 的插件来读取 JPEG、BMP 和 WebP 格式
                BufferedImage sourceImage = ImageIO.read(sourceInputStream);
                Thumbnails.of(sourceImage)
                        .sourceRegion(x, y, width, height)//指定坐标(0, 0)和(400, 400)区域
                        .size(scaleWidth, scaleHeight)
                        // outputQuality只对JPEG格式生效，PNG格式该方法无效
                        .outputQuality(1)
                        .outputFormat(extension.toLowerCase())
                        .toOutputStream(outputStream);
            }
			picture = outputStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (logger.isErrorEnabled()) {
	            logger.error("生成缩略图IO错误",e);
	        }
		} finally {
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
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					if (logger.isErrorEnabled()) {
			            logger.error("生成缩略图关闭输出流错误",e);
			        }
				}
			}
		}
		return picture;
	}
	
	
	
	
	
	
	
	
	

	/**
	 * 异步增加缩略图
	 * @param thumbnailList 缩略图对象集合
	 * @param imageInfoList 图片信息集合
	*/
	@Async
	public void addThumbnail(List<Thumbnail> thumbnailList, List<ImageInfo> imageInfoList){
		
		
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
				String path =  PathUtil.defaultExternalDirectory()+File.separator+FileUtil.toSystemPath(imageInfo.getPath());
				
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
				localFileComponent.deleteFile(path+thumbnail.getSpecificationGroup()+File.separator+imageInfo.getName());
			}
			
		}	
	}
	
	
}
