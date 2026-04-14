package cms.component.frontendModule;

import cms.model.favorite.QuestionFavorite;
import cms.model.favorite.TopicFavorite;
import cms.model.frontendModule.Section;
import cms.repository.favorite.FavoriteRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 站点栏目缓存管理
 */
@Component("sectionCacheManager")
public class SectionCacheManager {
    @Resource SectionComponent sectionComponent;

    /**
     * 查询缓存 获取站点栏目列表
     * @return
     */
    @Cacheable(value="sectionCacheManager_cache_getSectionList",key="'default'")
    public List<Section> query_cache_getSectionList(){
        return sectionComponent.getSectionList();
    }
    /**
     * 删除缓存 获取站点栏目列表
     * @return
     */
    @CacheEvict(value="sectionCacheManager_cache_getSectionList",key="'default'")
    public void delete_cache_getSectionList(){
    }


}
