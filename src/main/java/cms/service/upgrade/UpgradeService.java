package cms.service.upgrade;


import cms.model.upgrade.UpgradeSystem;

import java.util.Map;

/**
 * 升级服务
 */
public interface UpgradeService {

    /**
     * 获取升级列表
     * @return
     */
    public Map<String,Object> getUpgradeSystemList();
    /**
     * 获取升级明细
     * @param upgradeSystemId 升级Id
     * @return
     */
    public UpgradeSystem getUpgradeDetails(String upgradeSystemId);
    /**
     * 立即升级
     * @param updatePackageFirstDirectory 升级包目录
     */
    public void upgradeNow(String updatePackageFirstDirectory);
    /**
     * 继续升级
     * @param upgradeId 升级Id
     */
    public void continueUpgrade(String upgradeId);
}
