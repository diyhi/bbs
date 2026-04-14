package cms.repository.install.impl;

import cms.dto.install.Install;
import cms.repository.besa.DaoSupport;
import cms.repository.install.InstallRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * 安装系统管理接口实现类
 *
 */
@Repository
@Transactional
public class InstallRepositoryImpl extends DaoSupport<Install> implements InstallRepository {

    /**
     * 查询表总行数
     * @param tableName 表名
     * @return
     */
    public Long findCount(String tableName){
        Object result = em.createNativeQuery("SELECT COUNT(*) FROM " + tableName)
                .getSingleResult();
        // 原生查询返回的结果可能是 BigInteger，需要安全转换
        return (result != null) ? ((Number) result).longValue() : 0L;
    }


}
