package cms.repository.frontendModule.impl;

import cms.model.frontendModule.FrontendSettings;
import cms.repository.besa.DaoSupport;
import cms.repository.frontendModule.FrontendSettingsRepository;
import jakarta.persistence.Query;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



/**
 * 前台设置接口实现类
 *
 */
@Repository
@Transactional
public class FrontendSettingsRepositoryImpl extends DaoSupport<FrontendSettings> implements FrontendSettingsRepository {




    /**
     * 查询前台设置
     * @return
     */
    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public FrontendSettings findFrontendSettings(){
        return (FrontendSettings)this.find(FrontendSettings.class, 1);
    }




    /**-----------------------------------------  站点栏目    ---------------------------------------------**/

    /**
     * 修改站点栏目
     * @param sectionData 站点栏目JSON格式数据
     */
    public Integer updateSection(String sectionData){

        Query query = em.createQuery("update FrontendSettings o set o.sectionData=?1 where o.id=?2");
        //给SQL语句设置参数
        query.setParameter(1, sectionData);
        query.setParameter(2, 1);
        return query.executeUpdate();
    }

}

