package cms.service.link;

import cms.dto.link.LinkRequest;
import cms.model.links.Links;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 友情链接服务
 */
public interface LinkService {

    /**
     * 获取友情链接列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     */
    public List<Links> getLinkList(int page, String fileServerAddress);
    /**
     * 添加友情链接
     * @param linkRequest 友情链接表单
     * @param images 图片
     */
    public void addLink(LinkRequest linkRequest, MultipartFile images);
    /**
     * 获取修改友情链接界面信息
     * @param linksId 友情链接Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditLinkViewModel(Integer linksId, String fileServerAddress);
    /**
     * 修改友情链接
     * @param linkRequest 友情链接表单
     * @param images 图片
     */
    public void editLink(LinkRequest linkRequest,MultipartFile images);
    /**
     * 删除友情链接
     * @param linksId 友情链接Id
     */
    public void deleteLink(Integer linksId);
}
