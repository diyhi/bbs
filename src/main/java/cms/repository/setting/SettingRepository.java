package cms.repository.setting;


import cms.model.setting.SystemSetting;
import cms.repository.besa.DAO;

/**
 * 系统设置管理接口
 * @author Administrator
 *
 */
public interface SettingRepository extends DAO<SystemSetting> {
	/**
	 * 查询系统设置
	 * @return
	 **/
	public SystemSetting findSystemSetting();
	/**
	 * 查询系统设置 - 缓存
	 * @return
	 */
	public SystemSetting findSystemSetting_cache();
	
	/**
	 * 修改系统设置
	 * @param systemSetting 系统设置
	 * @return
	 */
	public void updateSystemSetting(SystemSetting systemSetting);
	
	
}
