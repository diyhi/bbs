package cms.component.frontendModule;

import cms.model.frontendModule.FrontendApi;
import cms.model.frontendModule.Section;
import cms.repository.frontendModule.FrontendApiRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 前台API缓存管理
 */
@Component("frontendApiCacheManager")
public class FrontendApiCacheManager {
    @Resource
    FrontendApiRepository frontendApiRepository;

    /**
     * 查询缓存 获取所有前台API
     * @return
     */
    @Cacheable(value="frontendApiCacheManager_cache_findAllFrontendApi",key="'default'")
    public List<FrontendApi> query_cache_findAllFrontendApi(){
        return frontendApiRepository.findAllFrontendApi();
    }
    /**
     * 删除缓存 获取所有前台API
     * @return
     */
    @CacheEvict(value="frontendApiCacheManager_cache_findAllFrontendApi",key="'default'")
    public void delete_cache_findAllFrontendApi(){
    }
}

