package cms.web.action.data;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.data.TableInfoObject;
import cms.utils.FileSize;
import cms.utils.PathUtil;
import cms.web.action.FileManage;

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
	@Resource FileManage fileManage;
	
	/**
	 * 备份页面
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=backup",method=RequestMethod.GET)
	public String backupUI(ModelMap model)
			throws Exception {
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
		
		model.addAttribute("showTable", tableInfoObjectList);
		model.addAttribute("countIndexSize", FileSize.conversion(countIndexSize));
		model.addAttribute("countDataSize",  FileSize.conversion(countDataSize));
		model.addAttribute("countRow",  countRow);
		return "jsp/data/dataBackupList";
	}	
	

	/**
	 * 备份
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=backup",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String backup(ModelMap model)
			throws Exception {
		Long count = dataRunMarkManage.taskRunMark_add(-1L);
		
		if(count >=0L){
			return "2";//任务正在运行
		}else{
			SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd-HHmmss"); //格式化当前系统日期
			String dateTime = dateFm.format(new java.util.Date());
			String path = "WEB-INF"+File.separator+"data" +File.separator+"backup" + File.separator+dateTime;
			
			//读取当前商城版本
			String currentVersion = fileManage.readFileToString("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt","utf-8");
			
			//写入备份数据库版本
			fileManage.writeStringToFile(path+File.separator+"version.txt",currentVersion,"utf-8",false);
			mySqlDataManage.backup(path);
		
		}
		return "1";
	}
	
	
	/**
	 * 查询备份进度
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=queryBackupProgress",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryBackupProgress(ModelMap model)
			throws Exception {
		
		String backupProgress = mySqlDataManage.getBackupProgress();
		return backupProgress;
	}
	/**
	 * 查询还原进度
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(params="method=queryResetProgress",method=RequestMethod.GET)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String queryResetProgress(ModelMap model)
			throws Exception {
		String resetProgress = mySqlDataManage.getResetProgress();
		
		return resetProgress;
	}
	
	/**
	 * 还原数据表
	 * @param dateName 还原数据名称
	 */
	@RequestMapping(params="method=reset",method=RequestMethod.GET)
	public String resetUI(ModelMap model,String dateName,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//显示还原数据目录
		String path = PathUtil.path()+File.separator+"WEB-INF"+File.separator+"data"+File.separator + "backup" + File.separator+fileManage.toRelativePath(dateName)+ File.separator;
		model.addAttribute("file", mySqlDataManage.getFile(path));
		return "jsp/data/dataReset";
	}
	/**
	 * 数据还原
	 * @param dateName 还原数据名称   "2012-07-30-142738"
	 */
	@RequestMapping(params="method=reset",method=RequestMethod.POST)
	@ResponseBody//方式来做ajax,直接返回字符串
	public String reset(ModelMap model,String dateName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//显示还原数据目录
		String path = "WEB-INF"+File.separator+"data"+File.separator + "backup" + File.separator+fileManage.toRelativePath(dateName);
				
		//读取备份的数据库版本
		String version = fileManage.readFileToString(path+File.separator+"version.txt","utf-8");
		//读取当前商城版本
		String currentVersion = fileManage.readFileToString("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt","utf-8");
		if(!currentVersion.equals(version)){
			return "3";//备份文件版本和当前商城系统版本不匹配
			
		}
		
		
		Long count = dataRunMarkManage.taskRunMark_add(-1L);
		
		if(count >=0L){
			return "2";//任务正在运行
		}else{
			mySqlDataManage.reduction(PathUtil.path()+File.separator+path);
		}
		return "1";
	}
}
