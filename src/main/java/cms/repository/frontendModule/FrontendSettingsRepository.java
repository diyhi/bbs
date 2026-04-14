package cms.repository.frontendModule;

import cms.model.frontendModule.FrontendSettings;
import cms.repository.besa.DAO;


/**
 * 前台设置接口
 * @author Gao
 *
 */
public interface FrontendSettingsRepository extends DAO<FrontendSettings> {

    /**
     * 查询前台设置
     * @return
     */
    public FrontendSettings findFrontendSettings();

    /**
     * 修改站点栏目
     * @param sectionData 站点栏目JSON格式数据
     */
    public Integer updateSection(String sectionData);
}
