package cms.web.action.filePackage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cms.bean.ZipPack;
import cms.utils.PathUtil;
import cms.utils.UUIDUtil;
import cms.utils.ZipUtil;
import cms.web.action.FileManage;

/**
 * 打包管理
 *
 */
@Component("filePackageManage")
public class FilePackageManage {
	@Resource FileManage fileManage;
	
	/**
	 * 文件打包
	 */
	@Async
	public void filePack(ConcurrentSkipListSet<String> compressList){
		if(compressList.size() >0){
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			
			//压缩文件名称
			String zipName = dateformat.format(new Date())+"_"+UUIDUtil.getUUID22()+".zip";
			
			if(compressList.size() ==1){//如果只有一个目录
				for(String id : compressList){
					if("|".equals(id)){//如果压缩根目录
						//压缩文件
						ZipUtil.pack(PathUtil.path()+File.separator, 
								PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator+zipName,
								PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator,
								new File(PathUtil.path()).getName()
								);//第一个参数：待压缩目录  第二个参数：输出文件  第三个参数：排除目录 第四个参数：压缩内文件逻辑路径
					
					}else{
						List<ZipPack> zipPackList = new ArrayList<ZipPack>();
						
						
						String path = PathUtil.path()+File.separator+fileManage.toRelativePath(fileManage.toSystemPath(id));
						File file = new File(PathUtil.path()+File.separator+fileManage.toRelativePath(fileManage.toSystemPath(id)));
						 // 判断此文件是否是一个文件夹
		                if (file.isDirectory()) {	
		                	ZipPack zipPack = new ZipPack();
		                	zipPack.setSource(path);
		                	zipPack.setEntryPath(new File(PathUtil.path()).getName()+ File.separator+fileManage.toRelativePath(fileManage.toSystemPath(id)));
		                	zipPack.setDirectory(true);
		                	zipPackList.add(zipPack);
		                }else{
		                	//删除文件名
		                	String _id = StringUtils.substringBeforeLast(id, "/");//从右往左截取到相等的字符
		                	if(id.length() == _id.length()){//如果没有目录，则删除文件名
		                		_id = "";
		                	}
		                	
		                	ZipPack zipPack = new ZipPack();
		                	zipPack.setSource(path);
		                	zipPack.setEntryPath(new File(PathUtil.path()).getName()+ File.separator+(_id != null && !"".equals(_id) ? fileManage.toRelativePath(fileManage.toSystemPath(_id)) : ""));
		                	zipPack.setDirectory(false);
		                	zipPackList.add(zipPack);
		                }
							
		              //压缩文件
						ZipUtil.pack(zipPackList, 
								PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator+zipName,
								PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator
								);//第一个参数：待压缩目录集合  第二个参数：输出文件  第三个参数：排除目录	
					}
				}
			}
			
			
			if(compressList.size() >1){//如果有多个目录
				List<ZipPack> zipPackList = new ArrayList<ZipPack>();
				
				for(String id : compressList){
					String path = PathUtil.path()+File.separator+fileManage.toRelativePath(fileManage.toSystemPath(id));
					File file = new File(PathUtil.path()+File.separator+fileManage.toRelativePath(fileManage.toSystemPath(id)));
					 // 判断此文件是否是一个文件夹
	                if (file.isDirectory()) {	
	                	ZipPack zipPack = new ZipPack();
	                	zipPack.setSource(path);
	                	zipPack.setEntryPath(new File(PathUtil.path()).getName()+ File.separator+fileManage.toRelativePath(fileManage.toSystemPath(id)));
	                	zipPack.setDirectory(true);
	                	zipPackList.add(zipPack);
	                }else{
	                	//删除文件名
	                	String _id = StringUtils.substringBeforeLast(id, "/");//从右往左截取到相等的字符
	                	if(id.length() == _id.length()){//如果没有目录，则删除文件名
	                		_id = "";
	                	}
	                	
	                	ZipPack zipPack = new ZipPack();
	                	zipPack.setSource(path);
	                	zipPack.setEntryPath(new File(PathUtil.path()).getName()+ File.separator+(_id != null && !"".equals(_id) ? fileManage.toRelativePath(fileManage.toSystemPath(_id)) : ""));
	                	zipPack.setDirectory(false);
	                	zipPackList.add(zipPack);
	                }
					
					
				}
				
				//压缩文件
				ZipUtil.pack(zipPackList, 
						PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator+zipName,
						PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator
						);//第一个参数：待压缩目录集合  第二个参数：输出文件  第三个参数：排除目录
			}
			
		}
	}
}
