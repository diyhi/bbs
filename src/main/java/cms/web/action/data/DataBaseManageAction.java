package cms.web.action.data;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.data.TableInfoObject;
import cms.utils.FileSize;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.PathUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 数据库备份/还原管理
 *
 */
@Controller
@RequestMapping("/control/dataBase/manage") 
public class DataBaseManageAction {
	@Resource MySqlDataManage mySqlDataManage;
	@Resource DataRunMarkManage dataRunMarkManage;
	
	/**
	 * 备份页面
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=backup",method=RequestMethod.GET)
	public String backupUI(ModelMap model)
			throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		
		long countIndexSize = 0L;
		long countDataSize = 0L;
		long countRow = 0L;
		List<TableInfoObject> tableInfoObjectList = mySqlDataManage.showTable();
		for(TableInfoObject tableInfoObject : tableInfoObjectList){
			countIndexSize += Long.parseLong(tableInfoObject.getIndexSize());
			countDataSize += Long.parseLong(tableInfoObject.getDataSize());
			tableInfoObject.setDataSize(FileSize.conversion(Long.parseLong(tableInfoObject.getDataSize())));
			tableInfoObject.setIndexSize(FileSize.conversion(Long.parseLong(tableInfoObject.getIndexSize())));
			countRow += tableInfoObject.getRows();
		}
		
		returnValue.put("showTable", tableInfoObjectList);
		returnValue.put("countIndexSize", FileSize.conversion(countIndexSize));
		returnValue.put("countDataSize",  FileSize.conversion(countDataSize));
		returnValue.put("countRow",  countRow);
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}	
	

	/**
	 * 备份
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=backup",method=RequestMethod.POST)
	public String backup(ModelMap model)
			throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		Long count = dataRunMarkManage.taskRunMark_add(-1L);
		
		if(count >=0L){
			error.put("backup", "任务正在运行");
		}else{
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd-HHmmss"); //格式化当前系统日期
			String dateTime = dateFm.format(new java.util.Date());
			String path = "WEB-INF"+File.separator+"data" +File.separator+"backup" + File.separator+dateTime;
			
			//读取当前系统版本
			String currentVersion = FileUtil.readFileToString("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt","utf-8");
			
			//写入备份数据库版本
			FileUtil.writeStringToFile(path+File.separator+"version.txt",currentVersion,"utf-8",false);
			mySqlDataManage.backup(path);
		
		}
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
	
	
	/**
	 * 查询备份进度
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=queryBackupProgress",method=RequestMethod.GET)
	public String queryBackupProgress(ModelMap model)
			throws Exception {
		
		String backupProgress = mySqlDataManage.getBackupProgress();
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,backupProgress));
	}
	/**
	 * 查询还原进度
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(params="method=queryResetProgress",method=RequestMethod.GET)
	public String queryResetProgress(ModelMap model)
			throws Exception {
		String resetProgress = mySqlDataManage.getResetProgress();
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,resetProgress));
	}
	
	/**
	 * 还原数据表
	 * @param dateName 还原数据名称
	 */
	@ResponseBody
	@RequestMapping(params="method=reset",method=RequestMethod.GET)
	public String resetUI(ModelMap model,String dateName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,Object> returnValue = new HashMap<String,Object>();
		//显示还原数据目录
		String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator + "backup" + File.separator+FileUtil.toRelativePath(dateName)+ File.separator;
		returnValue.put("fileMap", mySqlDataManage.getFile(path));
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,returnValue));
	}
	/**
	 * 数据还原
	 * @param dateName 还原数据名称   "2012-07-30-142738"
	 */
	@ResponseBody
	@RequestMapping(params="method=reset",method=RequestMethod.POST)
	public String reset(ModelMap model,String dateName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		//错误
		Map<String,Object> error = new HashMap<String,Object>();
		
		//显示还原数据目录
		String path = "WEB-INF"+File.separator+"data"+File.separator + "backup" + File.separator+FileUtil.toRelativePath(dateName);
				
		//读取备份的数据库版本
		String version = FileUtil.readFileToString(path+File.separator+"version.txt","utf-8");
		//读取当前BBS版本
		String currentVersion = FileUtil.readFileToString("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt","utf-8");
		
		if(!currentVersion.equals(version)){
			error.put("reset", "备份文件版本和当前BBS系统版本不匹配");
		}else{
			Long count = dataRunMarkManage.taskRunMark_add(-1L);
			
			if(count >=0L){
				error.put("reset", "任务正在运行");
			}else{
				mySqlDataManage.reduction(PathUtil.path()+File.separator+path);
			}
		}
		
		if(error.size() >0){
			return JsonUtils.toJSONString(new RequestResult(ResultCode.FAILURE,error));
		}else{
			return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,null));
		}
	}
}
