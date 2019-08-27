package cms.service.upgrade;

import java.util.List;

import cms.bean.upgrade.UpgradeSystem;
import cms.service.besa.DAO;

/**
 * 升级管理接口
 *
 */
public interface UpgradeService extends DAO<UpgradeSystem> {
	/**
	 * 根据Id查询升级
	 * @param upgradeSystemId
	 * @return
	 */
	public UpgradeSystem findUpgradeSystemById(String upgradeSystemId);
	/**
	 * 查询所有升级
	 * @return
	 */
	public List<UpgradeSystem> findAllUpgradeSystem();
	
	/**
	 * 保存升级
	 * @param upgradeSystem
	 */
	public void saveUpgradeSystem(UpgradeSystem upgradeSystem);

	/**
	 * 修改运行状态
	 * @param upgradeId 升级Id
	 * @param runningStatus 运行状态
	 * @param upgradeLog 日志
	 * @return
	 */
	public Integer updateRunningStatus(String upgradeId ,Integer runningStatus,String upgradeLog);
	/**
	 * 修改中断状态
	 * @param upgradeId 升级Id
	 * @param interruptStatus 中断状态
	 * @param upgradeLog 日志
	 * @return
	 */
	public Integer updateInterruptStatus(String upgradeId ,Integer interruptStatus,String upgradeLog);
	/**
	 * 添加日志
	 * @param upgradeId 升级Id
	 * @param upgradeLog 日志
	 * @return
	 */
	public Integer addLog(String upgradeId ,String upgradeLog);
	/**
	 * 查询原生SQL
	 * @param sql
	 * @return
	 */
	public List<Object[]> queryNativeSQL(String sql);
	/**
	 * 插入原生SQL
	 * @param sql
	 * @return
	 */
	public Integer insertNativeSQL(String sql);
}
