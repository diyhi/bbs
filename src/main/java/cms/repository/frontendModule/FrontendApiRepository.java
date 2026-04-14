package cms.repository.frontendModule;

import cms.model.frontendModule.FrontendApi;
import cms.repository.besa.DAO;

import java.util.List;


/**
 * 前台API接口
 * @author Gao
 *
 */
public interface FrontendApiRepository extends DAO<FrontendApi> {

    /**
     * 根据Id查询前台API
     * @param frontendApiId 前台ApiId
     * @return
     */
    public FrontendApi findById(Integer frontendApiId);
	/**
	 * 查询所有前台API
	 * @return
	 */
	public List<FrontendApi> findAllFrontendApi();
    /**
     * 查询查询所有前台API数量
     * @return
     */
    public Long findAllFrontendApiCount();
    /**
     * 根据URL路径查询前台API
     * @param url URL路径
     * @return
     */
    public List<FrontendApi> findFrontendApiByUrl(String url);
    /**
     * 根据映射路由枚举值查询前台API
     * @param routeEnumMapper 映射路由枚举值
     * @return
     */
    public List<FrontendApi> findFrontendApiByMapper(String routeEnumMapper);
    /**
     * 保存前台API
     * @param frontendApi 前台API
     */
    public void saveFrontendApi(FrontendApi frontendApi);
    /**
     * 修改前台API
     * @param frontendApi 前台API
     * @return
     */
    public Integer updateFrontendApi(FrontendApi frontendApi);

    /**
     * 删除前台API
     * @param frontendApiId 前台ApiId
     */
    public Integer deleteFrontendApi(Integer frontendApiId);

}
