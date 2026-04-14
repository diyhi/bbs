package cms.service.topic;

import cms.dto.PageView;
import cms.model.topic.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 话题标签服务
 */
public interface TagService {
     /**
     * 获取标签列表
     * @param parentId 父标签
     * @param fileServerAddress 文件服务器地址
     * @param page 页码
     * @return
     */
    public PageView<Tag> getTagList(Long parentId,String fileServerAddress,int page);
    /**
     * 获取标签导航
     * @param parentId 父标签Id
     * @return
     */
    public Map<Long,String> getTabNavigation(Long parentId);
    /**
     * 获取父标签
     * @param parentId 父标签Id
     * @return
     */
    public Tag getParentTag(Long parentId);
    /**
     * 添加标签
     * @param tagForm 标签表单
     * @param parentId 父标签
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    public void addTag(Tag tagForm, Long parentId, Map<String,String> multiLanguageExtensionMap,String imagePath, MultipartFile images);

    /**
     * 获取修改标签界面信息
     * @param tagId 标签Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditTagViewModel(Long tagId, String fileServerAddress);
    /**
     * 修改标签
     * @param tagForm 标签表单
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    public void editTag(Tag tagForm,Map<String,String> multiLanguageExtensionMap,String imagePath, MultipartFile images);

    /**
     * 删除标签
     * @param tagId 标签Id
     */
    public void deleteTag(Long tagId);
    /**
     * 获取所有标签
     * @return
     */
    public List<Tag> getAllTag();

}
