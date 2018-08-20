package cms.service.setting.impl;

import cms.bean.setting.SystemSetting;
import cms.service.besa.DaoSupport;
import cms.service.setting.SettingService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统设置
 * @author Administrator
 *
 */
@Service
@Transactional
public class SettingServiceBean extends DaoSupport<SystemSetting> implements SettingService {
	
	/**
	 * 查询系统设置
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public SystemSetting findSystemSetting(){
		SystemSetting systemSetting = (SystemSetting)this.find(SystemSetting.class, 1);
		
		return systemSetting;
	}
	/**
	 * 查询系统设置 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="settingServiceBean_cache",key="'findSystemSetting_default'")
	public SystemSetting findSystemSetting_cache(){
		return this.findSystemSetting();
	}
	
	/**
	 * 修改系统设置
	 * @param systemSetting
	 * @return
	 */
	@CacheEvict(value="settingServiceBean_cache",allEntries=true)
	public void updateSystemSetting(SystemSetting systemSetting){
		this.update(systemSetting);
	}
	
}
