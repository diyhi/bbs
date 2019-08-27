package cms.service.upgrade.impl;


import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cms.bean.upgrade.UpgradeSystem;
import cms.service.besa.DaoSupport;
import cms.service.upgrade.UpgradeService;

/**
 * 升级管理实现类
 *
 */
@Service
@Transactional
public class UpgradeServiceBean extends DaoSupport<UpgradeSystem> implements UpgradeService {
	
	
	/**
	 * 根据Id查询升级
	 * @param upgradeSystemId
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public UpgradeSystem findUpgradeSystemById(String upgradeSystemId){
		Query query = em.createQuery("select o from UpgradeSystem o where o.id=?1")
				.setParameter(1,  upgradeSystemId);
		List<UpgradeSystem> upgradeSystemList = query.getResultList();
		if(upgradeSystemList != null && upgradeSystemList.size() >0){
			for(UpgradeSystem upgradeSystem : upgradeSystemList){
				return upgradeSystem;
			}
		}
		return null;
	}
	/**
	 * 查询所有升级
	 * @return
	 */
	@Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
	public List<UpgradeSystem> findAllUpgradeSystem(){
		Query query = em.createQuery("select o from UpgradeSystem o ORDER BY o.sort DESC");
		return query.getResultList();
	}
	
	/**
	 * 保存升级
	 * @param upgradeSystem
	 */
	public void saveUpgradeSystem(UpgradeSystem upgradeSystem){
		this.save(upgradeSystem);
	}

	/**
	 * 修改运行状态(中断状态设为正常)
	 * @param upgradeId 升级Id
	 * @param runningStatus 运行状态
	 * @param upgradeLog 日志
	 * @return
	 */
	public Integer updateRunningStatus(String upgradeId ,Integer runningStatus,String upgradeLog){
		Query query_refund = em.createQuery("update UpgradeSystem o set o.runningStatus=?1,o.interruptStatus=?2,o.upgradeLog=CONCAT(o.upgradeLog,?3), o.version=o.version+1 where o.id=?4")
			.setParameter(1, runningStatus)
			.setParameter(2, 0)
			.setParameter(3, upgradeLog)
			.setParameter(4, upgradeId);
		return query_refund.executeUpdate();
	}
	
	/**
	 * 修改中断状态
	 * @param upgradeId 升级Id
	 * @param interruptStatus 中断状态
	 * @param upgradeLog 日志
	 * @return
	 */
	public Integer updateInterruptStatus(String upgradeId ,Integer interruptStatus,String upgradeLog){
		Query query_refund = em.createQuery("update UpgradeSystem o set o.interruptStatus=?1,o.upgradeLog=CONCAT(o.upgradeLog,?2), o.version=o.version+1 where o.id=?3")
			.setParameter(1, interruptStatus)
			.setParameter(2, upgradeLog)
			.setParameter(3, upgradeId);
		return query_refund.executeUpdate();
	}
	/**
	 * 添加日志
	 * @param upgradeId 升级Id
	 * @param upgradeLog 日志
	 * @return
	 */
	public Integer addLog(String upgradeId ,String upgradeLog){
		Query query_refund = em.createQuery("update UpgradeSystem o set o.upgradeLog=CONCAT(o.upgradeLog,?1), o.version=o.version+1 where o.id=?2")
			.setParameter(1, upgradeLog)
			.setParameter(2, upgradeId);
		return query_refund.executeUpdate();
	}
	
	/**
	 * 查询原生SQL
	 * @param sql
	 * @return
	 */
	public List<Object[]> queryNativeSQL(String sql){
		Query query = em.createNativeQuery(sql);
		
		return query.getResultList();
	}
	
	/**
	 * 插入原生SQL
	 * @param sql
	 * @return
	 */
	public Integer insertNativeSQL(String sql){
		Query query = em.createNativeQuery(sql);
		
		return query.executeUpdate();
	}
	
}
