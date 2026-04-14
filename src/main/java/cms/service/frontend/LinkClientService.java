package cms.service.frontend;

import cms.model.links.Links;

import java.util.List;

/**
 * 前台友情链接服务接口
 */
public interface LinkClientService {
    /**
     * 获取友情链接
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public List<Links> getLinksList(String fileServerAddress);
}
