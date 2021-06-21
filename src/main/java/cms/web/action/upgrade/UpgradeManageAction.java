package cms.web.action.upgrade;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.type.TypeReference;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradePackage;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.CommentedProperties;
import cms.utils.FileSize;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.RsaUtil;
import cms.utils.ZipCallback;
import cms.utils.ZipUtil;
import cms.web.action.TextFilterManage;
import cms.web.action.fileSystem.localImpl.LocalFileManage;


/**
 * 升级管理
 *
 */
@Controller
@RequestMapping("/control/upgrade/manage") 
public class UpgradeManageAction {
	private static final Logger logger = LogManager.getLogger(UpgradeManageAction.class);
	@Resource LocalFileManage localFileManage;
	@Resource UpgradeService upgradeService;
	@Resource TextFilterManage textFilterManage;
	@Resource UpgradeManage upgradeManage;
	//是否需要重启
	private boolean restart = false;
	/**
	 * 升级列表
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=upgradeSystemList",method=RequestMethod.GET)
	public String upgradeSystemList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		//读取当前系统版本
		String currentVersion = FileUtil.readFileToString("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt","utf-8");

		List<UpgradeSystem> upgradeSystemList = upgradeService.findAllUpgradeSystem();
		UpgradeSystem notCompletedUpgrade = null;//未完成升级
		
		if(upgradeSystemList != null && upgradeSystemList.size() >0){
			for(UpgradeSystem upgradeSystem : upgradeSystemList){
				//删除最后一个逗号
				String _upgradeLog = StringUtils.substringBeforeLast(upgradeSystem.getUpgradeLog(), ",");//从右往左截取到相等的字符,保留左边的
		
				List<UpgradeLog> upgradeLogList = JsonUtils.toGenericObject(_upgradeLog+"]", new TypeReference< List<UpgradeLog> >(){});
				upgradeSystem.setUpgradeLogList(upgradeLogList);
			}
			
			for(int i =0; i<upgradeSystemList.size(); i++ ){
				UpgradeSystem upgradeSystem = upgradeSystemList.get(i);

				if(upgradeSystem.getRunningStatus() <9999){
					//未完成升级
					notCompletedUpgrade = upgradeSystem;
					break;
				}
			}
		}
		if(notCompletedUpgrade == null){
			
			try {
				String[] extensions = {"zip","ZIP"};//后缀名{"doc", "pdf"}
				boolean recursive = false;//是否递归
				Collection<File> files = FileUtils.listFiles(new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"upgrade"+File.separator), extensions, recursive);
				
				// 迭代输出
				A:for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				    File file = iterator.next();

				    //读取新升级文件
				    ZipFile zip = null;
					try {
						zip = new ZipFile(file);
						
						Enumeration<ZipArchiveEntry> entry = zip.getEntries();
						while(entry.hasMoreElements()){//依次访问各条目
							ZipArchiveEntry ze = entry.nextElement();  
							String fileName = FileUtil.getName(ze.getName());//文件名称
							 //读取配置文件
						    if("config.properties".equals(fileName)){
						    	CommentedProperties props = new CommentedProperties();
								try {
									props.load(zip.getInputStream(ze),"utf-8");
									//旧版本
									String oldSystemVersion = props.getProperty("oldSystemVersion");
									//升级包版本
									String updatePackageVersion = props.getProperty("updatePackageVersion");
									//新版本
									String newSystemVersion = props.getProperty("newSystemVersion");
									//说明
									String explanation = props.getProperty("explanation");
									//排序
									String sort = props.getProperty("sort");
									
									if(currentVersion.equals(oldSystemVersion)){
										notCompletedUpgrade = new UpgradeSystem();
										notCompletedUpgrade.setId(newSystemVersion);
										notCompletedUpgrade.setOldSystemVersion(oldSystemVersion);
										notCompletedUpgrade.setUpdatePackageVersion(updatePackageVersion);
										notCompletedUpgrade.setSort(Long.parseLong(sort));
										notCompletedUpgrade.setExplanation(textFilterManage.filterTag_br(explanation));
										notCompletedUpgrade.setUpdatePackageName(file.getName());
										notCompletedUpgrade.setUpdatePackageTime(new Date(file.lastModified()));
										break A;
									}
									
									 
								} catch (IOException e) {
									// TODO Auto-generated catch block
								//	e.printStackTrace();
									if (logger.isErrorEnabled()) {
							            logger.error("读取配置文件config.properties错误",e);
							        }
								}
						    }
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
						if (logger.isErrorEnabled()) {
				            logger.error("解压新升级文件错误",e);
				        }
					}finally{
						if(zip != null){
							zip.close();
						}
						
					}
					
				    
				   
				    
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				if (logger.isErrorEnabled()) {
		            logger.error("读取新升级文件错误",e);
		        }
			}
		}
		
		returnValue.put("currentVersion", currentVersion);
		returnValue.put("notCompletedUpgrade", notCompletedUpgrade);
		returnValue.put("upgradeSystemList", upgradeSystemList);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	
	/**
	 * 根据Id查询升级
	 */
	@ResponseBody
	@RequestMapping(params="method=queryUpgrade",method=RequestMethod.GET)
	public String queryUpgrade(ModelMap model,String upgradeSystemId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(upgradeSystemId != null && !"".equals(upgradeSystemId.trim())){
			UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeSystemId);
			if(upgradeSystem != null){
				//删除最后一个逗号
				String _upgradeLog = StringUtils.substringBeforeLast(upgradeSystem.getUpgradeLog(), ",");//从右往左截取到相等的字符,保留左边的

				List<UpgradeLog> upgradeLogList = JsonUtils.toGenericObject(_upgradeLog+"]", new TypeReference< List<UpgradeLog> >(){});
				upgradeSystem.setUpgradeLogList(upgradeLogList);
			
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,upgradeSystem));
			}else{
				error.put("upgradeSystemId", "系统升级不存在");
			}
		}else{
			error.put("upgradeSystemId", "系统升级Id不能为空");
		}
		
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		
	}
	
	/**
	 * 立即升级
	 * @param model
	 * @param updatePackageName 升级包文件名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=upgradeNow",method=RequestMethod.POST)
	public String upgradeNow(ModelMap model,String updatePackageName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String,String> error = new HashMap<String,String>();
		
		Long count = upgradeManage.taskRunMark_add(-1L);
		if(count >=0L){
			error.put("upgradeNow", "任务正在运行,不能升级");
		}else{
			
			upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
			
			
			if(updatePackageName != null && !"".equals(updatePackageName.trim())){
				//升级包文件路径
				String updatePackage_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"upgrade"+File.separator+FileUtil.toRelativePath(updatePackageName);
				//临时目录路径
				String temp_path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				
				//读取升级包
				File updatePackage = new File(updatePackage_path);
				
				if (updatePackage.exists()) {//如果文件存在
					//解压到临时目录
					try {
						ZipUtil.unZip(updatePackage_path, temp_path);
					
					} catch (Exception e) {
						error.put("upgradeNow", "解压到临时目录失败");
						//e.printStackTrace();
						if (logger.isErrorEnabled()) {
				            logger.error("解压到临时目录失败",e);
				        }
					}
	
					//目录参数
					class DirectoryParameter { 
						//第一个目录
						private String firstDirectory = null;
						
						public String getFirstDirectory() { 
				            return firstDirectory;
				        }
				        public void setFirstDirectory(String firstDirectory) { 
				            this.firstDirectory = firstDirectory;
				        } 
				    }
					
					DirectoryParameter directoryParameter = new DirectoryParameter(); 

					ZipUtil.iterate(new File(updatePackage_path), new ZipCallback() {
						  public void process(ZipArchiveEntry zipEntry) throws Exception {
							  if(directoryParameter.getFirstDirectory() == null || "".equals(directoryParameter.getFirstDirectory().trim())){
								  directoryParameter.setFirstDirectory(StringUtils.substringBefore(zipEntry.getName(), "/"));
							  }
						  }
						});
					if(directoryParameter.getFirstDirectory() != null && !"".equals(directoryParameter.getFirstDirectory().trim())){
						//读取升级包信息
						CommentedProperties props = new CommentedProperties();
						try {
							props.load(new File(temp_path+directoryParameter.getFirstDirectory()+File.separator+"config.properties"),"utf-8");
							//旧版本
							String oldSystemVersion = props.getProperty("oldSystemVersion");
							//升级包版本
							String updatePackageVersion = props.getProperty("updatePackageVersion");
							//新版本
							String newSystemVersion = props.getProperty("newSystemVersion");
							//说明
							String explanation = props.getProperty("explanation");
							//排序
							String sort = props.getProperty("sort");
							
							UpgradeSystem upgradeSystem = new UpgradeSystem();

							upgradeSystem.setId(newSystemVersion);
							upgradeSystem.setOldSystemVersion(oldSystemVersion);
							upgradeSystem.setUpdatePackageVersion(updatePackageVersion);
							upgradeSystem.setSort(Long.parseLong(sort));
							upgradeSystem.setRunningStatus(1);
							upgradeSystem.setExplanation(textFilterManage.filterTag_br(explanation));
							upgradeSystem.setUpdatePackageName(updatePackage.getName());
							upgradeSystem.setUpdatePackageTime(new Date(updatePackage.lastModified()));
							upgradeSystem.setUpdatePackageFirstDirectory(directoryParameter.getFirstDirectory());
							
							
							List<String> deleteFilePathList = new ArrayList<String>();;
							
							Set<String> keyList = props.propertyNames();
							if(keyList != null && keyList.size() >0){
								for(String key : keyList){
									if(key != null && !"".equals(key.trim())){
										if(key.startsWith("delete_")){
											
											String value = props.getProperty(key);
											if(value != null && !"".equals(value.trim())){
												deleteFilePathList.add(value.trim());
											}
										}
										
									}
									
								}
							}
							upgradeSystem.setDeleteFilePath(JsonUtils.toJSONString(deleteFilePathList));
							
							UpgradeLog upgradeLog = new UpgradeLog();
							upgradeLog.setTime(new Date());
							upgradeLog.setGrade(1);
							upgradeLog.setContent("解压升级包到临时目录成功");
							String upgradeLog_json = JsonUtils.toJSONString(upgradeLog);
							upgradeSystem.setUpgradeLog("["+upgradeLog_json+",");
							
							
							if(error.size()==0){
								//验证升级包签名
								
								String publicKey_str = RsaUtil.readPublicKeyFile();//读取公钥文件
								if(publicKey_str != null && !"".equals(publicKey_str.trim())){//如果有公钥文件
									//签名字符
									String sign_str = "";
									File file = new File(temp_path+directoryParameter.getFirstDirectory()+File.separator+"signature.pem");
						     		if(file != null && file.exists()){
						     			sign_str = FileUtils.readFileToString(file, "utf-8");
						     		}
									if(sign_str != null && !"".equals(sign_str.trim())){
										PublicKey publicKey = RsaUtil.getPublicKey(publicKey_str.trim());
										//公钥解密
										String decrypt = RsaUtil.decryptData(sign_str.trim(), publicKey);
										String sign = upgradeManage.getFileSignature(temp_path+directoryParameter.getFirstDirectory()+File.separator);
										if(sign != null && decrypt != null && !"".equals(sign.trim()) && sign.equals(decrypt)){
											UpgradeLog upgradeLog2 = new UpgradeLog();
											upgradeLog2.setTime(new Date());
											upgradeLog2.setGrade(1);
											upgradeLog2.setContent("验证签名成功 "+sign);
											String upgradeLog_json2 = JsonUtils.toJSONString(upgradeLog2);
											upgradeSystem.setUpgradeLog(upgradeSystem.getUpgradeLog()+upgradeLog_json2+",");
										}else{
											error.put("upgradeNow", "验证签名失败");
										}
									}else{
										error.put("upgradeNow", "读取签名失败");
									}
									
								}
							}
							
							
							
							if(error.size()==0){
								try {
									upgradeService.save(upgradeSystem);
								} catch (Exception e) {
									error.put("upgradeNow", "升级错误");
									//e.printStackTrace();
									if (logger.isErrorEnabled()) {
							            logger.error("升级错误",e);
							        }
								}
							}
						} catch (IOException e) {
							error.put("upgradeNow", "读取配置文件失败");
						//	e.printStackTrace();
							if (logger.isErrorEnabled()) {
					            logger.error("读取配置文件失败",e);
					        }
						}
					}else{
						error.put("upgradeNow", "读取第一个目录失败");
					}
				}else{	
					error.put("upgradeNow", "升级包不存在");
				}
					
			}else{
				error.put("upgradeNow", "当前操作已完成");
			}
			
		}
		upgradeManage.taskRunMark_delete();
		
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 继续升级
	 * @param model
	 * @param upgradeId 升级Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=continueUpgrade",method=RequestMethod.POST)
	public String continueUpgrade(ModelMap model,String upgradeId,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String,String> error = new HashMap<String,String>();
		
		
		
		Long count = upgradeManage.taskRunMark_add(-1L);
		if(count >=0L){
			error.put("upgradeNow", "任务正在运行,不能升级");
		}else{
			
			upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
			if(upgradeId != null && !"".equals(upgradeId.trim())){
				UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
				if(upgradeSystem != null){
					
					//临时目录路径
					String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
					//复制文件
					if(upgradeSystem.getRunningStatus() <20){
						upgradeService.updateRunningStatus(upgradeId ,10,JsonUtils.toJSONString(new UpgradeLog(new Date(),"开始复制文件",1))+",");
						
						//源目录路径
						String resDirPath = temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator+"cms"+File.separator;
						
						//当前系统使用目录
						String current_dir = StringUtils.substringAfterLast(PathUtil.path(), File.separator);
						
						if(!"cms".equals(current_dir)){
							//重命名文件夹名称,和使用的目录名称一致
							boolean flag = FileUtil.renameDirectory(resDirPath, current_dir);
							if(flag){
								upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"重命名临时文件夹成功",1))+",");
								
							}else{
								upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"重命名临时文件夹失败",2))+",");
								//添加错误中断
								upgradeService.updateInterruptStatus(upgradeId, 1, JsonUtils.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");
								
							}
						}
						
						UpgradeSystem upgradeSystem_1 = upgradeService.findUpgradeSystemById(upgradeId);
						if(upgradeSystem_1.getInterruptStatus() != 1){
							//新源目录路径
							String new_resDirPath = temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator+current_dir+File.separator;
							
							//复制升级文件到目录
							try {
								FileUtil.copyDirectory(new_resDirPath, "..");
								//更改运行状态
								upgradeService.updateRunningStatus(upgradeId ,20,JsonUtils.toJSONString(new UpgradeLog(new Date(),"复制升级文件到目录完成",1))+",");
							} catch (Exception e) {
								upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"复制升级文件到目录失败",2))+",");
								//添加错误中断
								upgradeService.updateInterruptStatus(upgradeId, 1, JsonUtils.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");
								//e.printStackTrace();
								if (logger.isErrorEnabled()) {
						            logger.error("复制升级文件到目录失败",e);
						        }
							}
						}
						
					}
					
					UpgradeSystem upgradeSystem_2 = upgradeService.findUpgradeSystemById(upgradeId);
					//删除文件
					if(upgradeSystem_2.getInterruptStatus() != 1 && upgradeSystem_2.getRunningStatus() >=20 && upgradeSystem_2.getRunningStatus()<40){
						restart = true;
						boolean flag = true;
						if(upgradeSystem_2.getDeleteFilePath() != null && !"".equals(upgradeSystem_2.getDeleteFilePath().trim())){
							upgradeService.updateRunningStatus(upgradeId ,30,JsonUtils.toJSONString(new UpgradeLog(new Date(),"执行删除文件任务",1))+",");
							
							List<String> deleteFilePathList = JsonUtils.toGenericObject(upgradeSystem_2.getDeleteFilePath(), new TypeReference< List<String> >(){});
							if(deleteFilePathList != null && deleteFilePathList.size() >0){
								
								for(String deleteFilePath : deleteFilePathList){
									File file = new File(PathUtil.path()+File.separator+FileUtil.toSystemPath(deleteFilePath));
									if(file.exists()){
										if(file.isDirectory()){//目录
											try {
												localFileManage.removeDirectory(FileUtil.toSystemPath(deleteFilePath));
											} catch (Exception e) {
												flag = false;
												upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除目录失败--> "+deleteFilePath,1))+",");
												//添加错误中断
												upgradeService.updateInterruptStatus(upgradeId, 1, JsonUtils.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");
												
												if (logger.isErrorEnabled()) {
										            logger.error("删除目录失败"+deleteFilePath,e);
										        }
												break;
												
											}
											
										}else{//文件
											try {
												localFileManage.deleteFile(FileUtil.toSystemPath(deleteFilePath));
											} catch (Exception e) {
												flag = false;
												upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除文件失败--> "+deleteFilePath,1))+",");
												//添加错误中断
												upgradeService.updateInterruptStatus(upgradeId, 1, JsonUtils.toJSONString(new UpgradeLog(new Date(),"出现错误中断升级过程",2))+",");
												
												if (logger.isErrorEnabled()) {
										            logger.error("删除文件失败"+deleteFilePath,e);
										        }
												break;
												
											}
											
										}
										
									}
								}	
							}
						}
						if(flag){
							//更改运行状态
							upgradeService.updateRunningStatus(upgradeId ,40,JsonUtils.toJSONString(new UpgradeLog(new Date(),"删除文件任务结束",1))+",");
					
						}
					}
					
				//	upgradeService.updateInterruptStatus(upgradeId, 2, JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级过程中断,等待应用服务器重启",1))+",");
					
					
					UpgradeSystem upgradeSystem_3 = upgradeService.findUpgradeSystemById(upgradeId);
					//重启服务器
					if(upgradeSystem_3.getInterruptStatus() != 1 && upgradeSystem_3.getRunningStatus() >=40 && upgradeSystem_3.getRunningStatus()<100){
						if(restart == true){//未重启
							//添加重启服务器中断
							upgradeService.updateInterruptStatus(upgradeId, 2, JsonUtils.toJSONString(new UpgradeLog(new Date(),"需要重启应用服务器才能继续升级",1))+",");
							
						}else{//已重启
							//更改运行状态
							upgradeService.updateRunningStatus(upgradeId ,100,JsonUtils.toJSONString(new UpgradeLog(new Date(),"系统已重启完成",1))+",");
					
						}
					}
					

					UpgradeSystem upgradeSystem_4 = upgradeService.findUpgradeSystemById(upgradeId);
					if(upgradeSystem_4.getInterruptStatus() != 1 && upgradeSystem_4.getRunningStatus() >= 100){
						//执行处理数据
						upgradeManage.manipulationData(upgradeId);
					}
					
				}else{
					error.put("upgradeNow", "当前升级不存在");
				}
			}else{
				error.put("upgradeNow", "升级参数错误");
			}
			
		}

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	
	/**
	 * 查询升级包列表
	 */
	@ResponseBody
	@RequestMapping(params="method=queryUpgradePackageList",method=RequestMethod.GET)
	public String queryUpgradePackageList(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<UpgradePackage> upgradePackageList = new ArrayList<UpgradePackage>();
		
		try {
			String[] extensions = {"zip","ZIP"};//后缀名{"doc", "pdf"}
			boolean recursive = false;//是否递归
			Collection<File> files = FileUtils.listFiles(new File(PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"upgrade"+File.separator), extensions, recursive);
			
			// 迭代输出
			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
			    File file = iterator.next();
			    
			    UpgradePackage upgradePackage = new UpgradePackage();
			    upgradePackage.setName(file.getName());
			    upgradePackage.setSize(FileSize.conversion(file.length()));
			    upgradePackage.setLastModifiedTime(new Date(file.lastModified()));
			    upgradePackageList.add(upgradePackage);
			    
			    
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("查询升级包列表",e);
	        }
		}
		
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,upgradePackageList));
	}
	
	/**
	 * 上传升级包
	 */
	@ResponseBody
	@RequestMapping(params="method=uploadUpgradePackage",method=RequestMethod.POST)
	public String uploadUpgradePackage(ModelMap model,
			MultipartHttpServletRequest request) throws Exception {
		Map<String,String> error = new HashMap<String,String>();

		FileOutputStream fileoutstream = null;
		try {
			//获得文件： 
			MultipartFile file = request.getFile("file");
			if(file != null && !file.isEmpty()){
				//验证文件后缀
				List<String> flashFormatList = new ArrayList<String>();
				flashFormatList.add("zip");
				boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),flashFormatList);
				if(authentication){
					
					//文件保存目录
					String pathDir = "WEB-INF"+File.separator+"data"+File.separator+"upgrade"+File.separator;
					//生成文件保存目录
					FileUtil.createFolder(pathDir);
					//文件输出流
					fileoutstream = new FileOutputStream(new File(PathUtil.path()+File.separator+pathDir, file.getOriginalFilename()));
					//写入硬盘
					fileoutstream.write(file.getBytes());
				}else{
					error.put("file", "文件格式错误");
				}
			}else{
				error.put("file", "请选择文件");
			}
			
		
			
		} catch (Exception e) {
			error.put("file", "上传错误");
		//	e.printStackTrace();
			if (logger.isErrorEnabled()) {
	            logger.error("上传错误",e);
	        }
		}finally{
			if(fileoutstream != null){
				fileoutstream.close();
			}
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	/**
	 * 删除升级包
	 */
	@ResponseBody
	@RequestMapping(params="method=deleteUpgradePackage",method=RequestMethod.POST)
	public String deleteUpgradePackage(ModelMap model,String fileName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		if(fileName != null && !"".equals(fileName.trim())){
			
			Boolean state = localFileManage.deleteFile("WEB-INF"+File.separator+"data"+File.separator+"upgrade"+File.separator+FileUtil.toRelativePath(fileName.trim()));
			if(state != null && state == true){
				return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
			}else{
				error.put("fileName", "删除升级包失败");
			}
		}else{
			error.put("fileName", "文件名称不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
}
