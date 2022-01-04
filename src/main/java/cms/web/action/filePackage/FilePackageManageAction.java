package cms.web.action.filePackage;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.filePackage.FileResource;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.utils.WebUtil;
import cms.web.action.SystemException;
import cms.web.action.fileSystem.localImpl.LocalFileManage;


/**
 * 文件打包管理
 *
 */
@Controller
@RequestMapping("/control/filePackage/manage") 
public class FilePackageManageAction {
	@Resource FilePackageManage filePackageManage;
	@Resource LocalFileManage localFileManage;
	/**
	 * 下载压缩文件
	 * @param model
	 * @param fileName 模板备份文件名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	*/
	@ResponseBody
	@RequestMapping(params="method=download", method=RequestMethod.GET)
	public ResponseEntity<StreamingResponseBody> download(ModelMap model,String fileName,@RequestHeader(required = false) String range,
			HttpServletRequest request, HttpServletResponse response)throws Exception {

		if(fileName == null || "".equals(fileName.trim())){
			throw new SystemException("文件不名称不能为空！");
		}
		//替换路径中的..号
		fileName = FileUtil.toRelativePath(fileName); 
		
		String templateFile_path = "WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator+fileName;
		String filePackagePath = PathUtil.path()+File.separator+templateFile_path;
		
		return WebUtil.rangeDownloadResponse(filePackagePath, fileName,range,request,response);
		
		
		//return WebUtil.rangeDownloadResponse(filePackagePath, fileName,range,request);
	}
	

	/**
	 * 删除压缩文件
	 * @param model
	 * @param fileName 打包好的文件名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=delete", method=RequestMethod.POST)
	public String deleteExport(ModelMap model,String fileName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,Object> error = new HashMap<String,Object>();
		if(fileName != null && !"".equals(fileName.trim())){
			//替换路径中的..号
			fileName = FileUtil.toRelativePath(fileName);
			//模板文件路径
			String filePath = "WEB-INF"+File.separator+"data"+ File.separator+"filePackage"+ File.separator+fileName;
			localFileManage.deleteFile(filePath);
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}else{
			error.put("fileName", "文件名称不能为空");
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
	}
	
	/**
	 * 打包界面
	 */
	@ResponseBody
	@RequestMapping(params="method=package",method=RequestMethod.GET)
	public String packageUI(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
	}
	
	/**
	 * 打包
	 * @param model
	 * @param id 选中Id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=package",method=RequestMethod.POST)
	public String packages(ModelMap model,String[] idGroup,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,String> error = new HashMap<String,String>();//错误
		
		//要压缩的目录或文件
		ConcurrentSkipListSet<String> compressList = new ConcurrentSkipListSet<String>(new StringLengthSort());//线程安全有序集合

		if(idGroup != null && idGroup.length >0){
			for(int i =0; i< idGroup.length; i++){
				String id = idGroup[i];
				if(id != null && !"".equals(id.trim())){
					if("|".equals(id.trim())){//如果根目录全选
						compressList.clear();//清空
						compressList.add(id.trim());
						break;
					}
					compressList.add(id.trim());
				}
			}

			if(compressList.size() >1){
				//如果父目录全选，则删除子目录
				Iterator<String> filterIter = compressList.iterator();  
			    while(filterIter.hasNext()){  
			    	String id = filterIter.next();
			    	
			    	Iterator<String> tempFilterIter = compressList.iterator();  
			    	while(tempFilterIter.hasNext()){  
			    		String tempId = tempFilterIter.next();
			    	
			    		//判断开始部分是否与二参数相同。不区分大小写
			    		//StringUtils.startsWithIgnoreCase("中国共和国人民", "中国")
			    		if(!id.equals(tempId) && StringUtils.startsWithIgnoreCase(tempId, id)){
			    			tempFilterIter.remove();  
			    		}
			    	}
			    }  	
			}	
		}else{
			error.put("package", "未选择目录或文件");
		}
		if(error.size() ==0){
			//打包
			filePackageManage.filePack(compressList);
		}
		

		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	
	
	/**
	 * 按照字符串长度排序(从小到大)
	 *
	 */
	private class StringLengthSort implements Comparator<Object>{
	    public int compare(Object o1,Object o2){
	        String s1 = (String)o1;
	        String s2 = (String)o2;
	        int num =     s1.length() - s2.length();
	        //判断字符串长度相同时，根据字典顺序排
	        if(num == 0){
	            return s1.compareTo(s2);    
	        }
	        else 
	            return num;
	    }    
	}
	
	
	/**
	 * 查询子目录
	 * @param parentId 父Id
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(params="method=querySubdirectory",method=RequestMethod.GET)
	public String querySubdirectory(String parentId,ModelMap model,
			HttpServletRequest request){
		List<FileResource> fileResourceList = new ArrayList<FileResource>();

		if(parentId == null || "".equals(parentId.trim())){//根目录
			FileResource fileResource = new FileResource();
			File file = new File(PathUtil.path());
			fileResource.setId("|");//|表示根目录
			fileResource.setParentId("");
			fileResource.setName(file.getName());
			if(file.isDirectory() == true){//是目录
				fileResource.setLeaf(false);//不是叶子节点
			}else{
				fileResource.setLeaf(true);//是叶子节点
			}
			fileResourceList.add(fileResource);
			
		}else{
			if("|".equals(parentId.trim())){//遍历根目录
				String path = PathUtil.path();
				
				File dir = new File(path);
				if(dir.isDirectory()){
					
					File[] fs=dir.listFiles(); 
					for(File file : fs){
						FileResource fileResource = new FileResource();
						fileResource.setId(file.getName());
						fileResource.setParentId(parentId);
						fileResource.setName(file.getName());
						if(file.isDirectory() == true){//是目录
							fileResource.setLeaf(false);//不是叶子节点
						}else{
							fileResource.setLeaf(true);//是叶子节点
						}
						fileResourceList.add(fileResource);
						
					}
				}
			}else{
				String path = PathUtil.path()+File.separator+(parentId == null || "".equals(parentId.trim()) ? "" :File.separator+FileUtil.toRelativePath(FileUtil.toSystemPath(parentId)));
				
				File dir = new File(path);
				if(dir.isDirectory()){
					
					File[] fs=dir.listFiles(); 
					for(File file : fs){
						FileResource fileResource = new FileResource();
						fileResource.setId(parentId+"/"+file.getName());
						fileResource.setParentId(parentId);
						fileResource.setName(file.getName());
						if(file.isDirectory() == true){//是目录
							fileResource.setLeaf(false);//不是叶子节点
						}else{
							fileResource.setLeaf(true);//是叶子节点
						}
						fileResourceList.add(fileResource);
						
					}
				}
				
			}
			
		}
		
		/**
		List<Map<String,Object>> parameterList = new ArrayList<Map<String,Object>>();
		//组装参数
		if(fileResourceList != null && fileResourceList.size() >0){
			
			
			for(FileResource fileResource : fileResourceList){
				Map<String,Object> parameter = new LinkedHashMap<String,Object>();
				
				parameter.put("id", fileResource.getId());
				parameter.put("pId", fileResource.getParentId());
				parameter.put("name", fileResource.getName());
				parameter.put("isParent", fileResource.isLeaf() == true ? false : true);//是否为父节点
				parameterList.add(parameter);
			}
		}
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,parameterList));	**/
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,fileResourceList));
	}
}
