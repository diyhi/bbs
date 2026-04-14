package cms.repository.setting.impl;



import cms.model.setting.SystemSetting;
import cms.repository.besa.DaoSupport;
import cms.repository.setting.SettingRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统设置管理接口实现类
 * @author Administrator
 *
 */
@Repository
@Transactional
public class SettingRepositoryImpl extends DaoSupport<SystemSetting> implements SettingRepository {
	
	/**
	 * 查询系统设置
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public SystemSetting findSystemSetting(){

		return (SystemSetting)this.find(SystemSetting.class, 1);
	}
	/**
	 * 查询系统设置 - 缓存
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	@Cacheable(value="settingRepositoryImpl_cache",key="'findSystemSetting_default'")
	public SystemSetting findSystemSetting_cache(){
		return this.findSystemSetting();
	}
	
	/**
	 * 修改系统设置
	 * @param systemSetting 系统设置
	 * @return
	 */
	@CacheEvict(value="settingRepositoryImpl_cache",allEntries=true)
	public void updateSystemSetting(SystemSetting systemSetting){
		this.update(systemSetting);
	}
	
}
