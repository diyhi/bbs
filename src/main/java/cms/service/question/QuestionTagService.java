package cms.service.question;

import cms.dto.PageView;
import cms.model.question.Question;
import cms.model.question.QuestionTag;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 问答标签服务
 */
public interface QuestionTagService {
     /**
     * 获取标签列表
     * @param parentId 父标签
     * @param fileServerAddress 文件服务器地址
     * @param page 页码
     * @return
     */
    public PageView<QuestionTag> getTagList(Long parentId, String fileServerAddress, int page);
    /**
     * 获取标签导航
     * @param parentId 父标签
     * @return
     */
    public Map<Long,String> getTabNavigation(Long parentId);
    /**
     * 获取父标签
     * @param parentId 父标签Id
     * @return
     */
    public QuestionTag getParentTag(Long parentId);
    /**
     * 添加标签
     * @param questionTagForm 标签表单
     * @param parentId 父标签
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    public void addTag(QuestionTag questionTagForm, Long parentId, Map<String,String> multiLanguageExtensionMap,String imagePath, MultipartFile images);

    /**
     * 获取修改标签界面信息
     * @param questionTagId 标签Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditTagViewModel(Long questionTagId, String fileServerAddress);
    /**
     * 修改标签
     * @param questionTagForm 标签表单
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    public void editTag(QuestionTag questionTagForm,Map<String,String> multiLanguageExtensionMap,String imagePath, MultipartFile images);

    /**
     * 删除标签
     * @param questionTagId 标签Id
     */
    public void deleteTag(Long questionTagId);
    /**
     * 获取所有标签
     * @return
     */
    public List<QuestionTag> getAllTag();

}
