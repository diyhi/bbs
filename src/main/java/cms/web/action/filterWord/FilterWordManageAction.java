package cms.web.action.filterWord;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cms.bean.FilterWord.FilterWord;
import cms.utils.FileSize;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;
import cms.web.action.fileSystem.localImpl.LocalFileManage;

/**
 * 过滤词管理
 *
 */
@Controller
@RequestMapping("/control/filterWord/manage") 
public class FilterWordManageAction {
	@Resource LocalFileManage localFileManage;
	
	
	/**
	 * 过滤词展示
	 */
	@RequestMapping(params="method=view",method=RequestMethod.GET)
	public String view(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator+"filterWord"+File.separator;
		File file = new File(path+"word.txt");
		
		FilterWord filterWord = new FilterWord();
		
		
		if(file.exists()){
			
			List<String> wordList = FileUtil.readLines(file,"utf-8");
			if(wordList != null){
				filterWord.setWordNumber(wordList.size());
				for(int i=0; i<wordList.size(); i++){
					filterWord.addBeforeWord(wordList.get(i));
					if(i == 2){
						break;
					}
				}
			}
			
			filterWord.setSize(FileSize.conversion(file.length()));
			filterWord.setLastModified(new Date(file.lastModified()));
			model.addAttribute("filterWord", filterWord);
		}
		
		
		
		return "jsp/setting/view_filterWord";
	}
	
	
	/**
	 * 上传词库
	 */
	@RequestMapping(params="method=uploadFilterWord",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String uploadFilterWord(ModelMap model,
			MultipartHttpServletRequest request) throws Exception {
		Map<String,Object> returnJson = new HashMap<String,Object>();
		Map<String,String> error = new HashMap<String,String>();

		FileOutputStream fileoutstream = null;
		try {
			//获得文件： 
			MultipartFile file = request.getFile("file");
			if(file != null && !file.isEmpty()){
				//验证文件后缀
				List<String> flashFormatList = new ArrayList<String>();
				flashFormatList.add("txt");
				boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),flashFormatList);
				if(authentication){
					
					//文件保存目录
					String pathDir = "WEB-INF"+File.separator+"data"+File.separator+"filterWord"+File.separator;
					//生成文件保存目录
					FileUtil.createFolder(pathDir);
					//保存文件
					localFileManage.writeFile(pathDir, "word.txt",file.getBytes());
					
				}else{
					error.put("file", "文件格式错误");
				}
			}else{
				error.put("file", "请选择文件");
			}
			
		
			
		} catch (Exception e) {
			error.put("file", "上传错误");
		//	e.printStackTrace();
		}finally{
			if(fileoutstream != null){
				fileoutstream.close();
			}
		}
		
		if(error.size() >0){
			//上传失败
			returnJson.put("error", error);
			returnJson.put("success", false);
		}else{
			returnJson.put("success", true);
		}
		
		return JsonUtils.toJSONString(returnJson);
		
	
	}
	
	/**
	 * 删除词库
	 */
	@RequestMapping(params="method=deleteFilterWord",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String deleteFilterWord(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Boolean state = localFileManage.deleteFile("WEB-INF"+File.separator+"data"+File.separator+"filterWord"+File.separator+"word.txt");
		if(state != null && state == true){
			return "1";
		}
		
		return "0";
	}
	
	
	
    
}
