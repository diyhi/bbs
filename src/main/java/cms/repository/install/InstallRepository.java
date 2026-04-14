package cms.repository.install;

import cms.dto.install.Install;
import cms.repository.besa.DAO;



/**
 * 安装系统管理接口
 *
 */
public interface InstallRepository extends DAO<Install> {
    /**
     * 查询总页数
     * @param tableName 表名
     * @return
     */
    public Long findCount(String tableName);

}
