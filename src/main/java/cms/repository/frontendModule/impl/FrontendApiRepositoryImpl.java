package cms.repository.frontendModule.impl;

import cms.model.frontendModule.FrontendApi;
import cms.repository.besa.DaoSupport;
import cms.repository.frontendModule.FrontendApiRepository;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * 前台API接口实现类
 *
 */
@Repository
@Transactional
public class FrontendApiRepositoryImpl extends DaoSupport<FrontendApi> implements FrontendApiRepository {


    /**
     * 根据Id查询前台API
     * @param frontendApiId 前台ApiId
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public FrontendApi findById(Integer frontendApiId){
        TypedQuery<FrontendApi> query = em.createQuery("select o from FrontendApi o where o.id=?1", FrontendApi.class)
                .setParameter(1, frontendApiId);
        return query.getSingleResultOrNull();
    }
    /**
     * 查询所有前台API
     * @return
     */
	@Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
	public List<FrontendApi> findAllFrontendApi(){
		Query query = em.createQuery("select o from FrontendApi o order by o.id desc");
		return query.getResultList();
	}

    /**
     * 查询查询所有前台API数量
     * @return
     */
    @Transactional(readOnly=true,propagation=Propagation.NOT_SUPPORTED)
    public Long findAllFrontendApiCount(){
        Query query = em.createQuery("select count(o) from FrontendApi o");
        return (Long)query.getSingleResult();
    }

    /**
     * 保存前台API
     * @param frontendApi 前台API
     */
    public void saveFrontendApi(FrontendApi frontendApi){
        this.save(frontendApi);
    }

    /**
     * 根据URL路径查询前台API
     * @param url URL路径
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public List<FrontendApi> findFrontendApiByUrl(String url){
        Query query = em.createQuery("select o from FrontendApi o where o.url=?1")
        .setParameter(1,url);
        return query.getResultList();
    }

    /**
     * 根据映射路由枚举值查询前台API
     * @param routeEnumMapper 映射路由枚举值
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.NOT_SUPPORTED)
    public List<FrontendApi> findFrontendApiByMapper(String routeEnumMapper){
        Query query = em.createQuery("select o from FrontendApi o where o.routeEnumMapper=?1")
                .setParameter(1,routeEnumMapper);
        return query.getResultList();
    }

    /**
     * 修改前台API
     * @param frontendApi 前台API
     * @return
     */
    public Integer updateFrontendApi(FrontendApi frontendApi){
        Query query = em.createQuery("update FrontendApi o set o.name=?1,o.url=?2, o.httpMethod=?3,o.requiresLogin=?4,o.routeEnumMapper=?5,o.configData=?6 where o.id=?7")
                .setParameter(1, frontendApi.getName())
                .setParameter(2, frontendApi.getUrl())
                .setParameter(3, frontendApi.getHttpMethod())
                .setParameter(4, frontendApi.getRequiresLogin())
                .setParameter(5, frontendApi.getRouteEnumMapper())
                .setParameter(6, frontendApi.getConfigData())
                .setParameter(7, frontendApi.getId());
        return query.executeUpdate();
    }

    /**
     * 删除前台API
     * @param frontendApiId 前台ApiId
     */
    public Integer deleteFrontendApi(Integer frontendApiId){
        Query delete = em.createQuery("delete from FrontendApi o where o.id=?1")
                .setParameter(1,frontendApiId);
        return delete.executeUpdate();
    }
}

