package cms.component.redEnvelope;

import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.redEnvelope.ReceiveRedEnvelope;
import cms.repository.redEnvelope.RedEnvelopeRepository;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * 红包缓存管理
 *
 */
@Component("redEnvelopeCacheManager")
public class RedEnvelopeCacheManager {
    @Resource RedEnvelopeRepository redEnvelopeRepository;

    /**
     * 查询缓存 根据Id查询发红包
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    @Cacheable(value="redEnvelopeCacheManager_cache_findById",key="#giveRedEnvelopeId")
    public GiveRedEnvelope query_cache_findById(String giveRedEnvelopeId){
        return redEnvelopeRepository.findById(giveRedEnvelopeId);
    }
    /**
     * 删除缓存 根据Id查询发红包
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    @CacheEvict(value="redEnvelopeCacheManager_cache_findById",key="#giveRedEnvelopeId")
    public void delete_cache_findById(String giveRedEnvelopeId){
    }

    /**
     * 查询缓存 根据Id查询收红包
     * @param receiveRedEnvelopeId 收红包Id
     * @return
     */
    @Cacheable(value="redEnvelopeCacheManager_cache_findByReceiveRedEnvelopeId",key="#receiveRedEnvelopeId")
    public ReceiveRedEnvelope query_cache_findByReceiveRedEnvelopeId(String receiveRedEnvelopeId){
        return redEnvelopeRepository.findByReceiveRedEnvelopeId(receiveRedEnvelopeId);
    }
    /**
     * 删除缓存 根据Id查询收红包
     * @param receiveRedEnvelopeId 收红包Id
     * @return
     */
    @CacheEvict(value="redEnvelopeCacheManager_cache_findByReceiveRedEnvelopeId",key="#receiveRedEnvelopeId")
    public void delete_cache_findByReceiveRedEnvelopeId(String receiveRedEnvelopeId){
    }


}
