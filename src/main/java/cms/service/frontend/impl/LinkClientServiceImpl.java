package cms.service.frontend.impl;

import cms.model.links.Links;
import cms.repository.link.LinkRepository;
import cms.service.frontend.LinkClientService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台友情链接服务
 */
@Service
public class LinkClientServiceImpl implements LinkClientService {
    @Resource
    LinkRepository linkRepository;

    /**
     * 获取友情链接
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public List<Links> getLinksList(String fileServerAddress){
        List<Links> linksList = linkRepository.findAllLinks_cache();
        if(linksList != null && linksList.size() >0){
            for(Links links : linksList){
                if(links.getImage() != null && !links.getImage().trim().isEmpty()){
                    links.setImage(fileServerAddress+links.getImage());
                }
            }
        }
        return linksList;
    }
}
