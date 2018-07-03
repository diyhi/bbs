package cms.web.action.upgrade;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.Resource;

import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.upgrade.UpgradeService;
import cms.utils.JsonUtils;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 升级管理
 *
 */
@Component("upgradeManage")
public class UpgradeManage {
	@Resource UpgradeService upgradeService;
	
	/**
	 * 处理数据
	 * @param upgradeId 升级Id
	 */
	@Async
	public void manipulationData(String upgradeId){
		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
		//执行升级数据处理
		if(upgradeSystem.getRunningStatus() >= 100){
			String oldVersion = upgradeSystem.getOldSystemVersion().replaceAll("\\.", "_");//点替换为下划线
			String newVersion = upgradeSystem.getId().replaceAll("\\.", "_");//点替换为下划线
	
			boolean error = false;
			//反射调用升级数据处理类
			try {
				Class<?> cls = Class.forName("cms.web.action.upgrade.impl.Upgrade"+oldVersion.trim()+"to"+newVersion.trim());
				Method m = cls.getDeclaredMethod("run",new Class[]{String.class});  
				m.invoke(cls,upgradeId);
			} catch (ClassNotFoundException e) {
				error = true;
			//	e.printStackTrace();
			} catch (NoSuchMethodException e) {
				error = true;
			//	e.printStackTrace();
			} catch (SecurityException e) {
				error = true;
			//	e.printStackTrace();
			} catch (IllegalAccessException e) {
				error = true;
			//	e.printStackTrace();
			} catch (IllegalArgumentException e) {
				error = true;
			//	e.printStackTrace();
			} catch (InvocationTargetException e) {
				error = true;
			//	e.printStackTrace();
			} catch (Exception e) {
				error = true;
			//	e.printStackTrace();
			}finally{
				if(error){
					upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"调用升级数据处理类出错",2))+",");
				}
			}
				
		}
	}
	
	
	
	/**
	 * 查询/添加任务运行标记
	 * @param count 次数  -1为查询方式
	 * @return
	 */
	@Cacheable(value="upgradeManage_cache_taskRunMark",key="'taskRunMark'")
	public Long taskRunMark_add(Long count){
		return count;
	}
	/**
	 * 删除任务运行标记
	 * @return
	 */
	@CacheEvict(value="upgradeManage_cache_taskRunMark",key="'taskRunMark'")
	public void taskRunMark_delete(){
	}
	
	
	
	
}
