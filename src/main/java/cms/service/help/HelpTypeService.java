package cms.service.help;

import cms.dto.PageView;
import cms.model.help.HelpType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * 帮助分类服务
 */
public interface HelpTypeService {
     /**
     * 获取帮助分类列表
     * @param parentId 父标签
     * @param fileServerAddress 文件服务器地址
     * @param page 页码
     * @return
     */
    public PageView<HelpType> getHelpTypeList(Long parentId, String fileServerAddress, int page);
    /**
     * 获取分类导航
     *
     * @param parentId 父标签
     * @return
     */
    public Map<Long, String> getTypeNavigation(Long parentId);

    /**
     * 获取添加帮助分类界面信息
     * @param parentId 父标签
     * @return
     */
    public Map<String,Object> getAddHelpTypeViewModel(Long parentId);

    /**
     * 添加帮助分类
     * @param helpTypeForm 帮助分类表单
     * @param parentId 父Id
     * @param imagePath 图片路径
     * @param images 图片
     */
    public void addHelpType(HelpType helpTypeForm, Long parentId, String imagePath, MultipartFile images);
    /**
     * 获取修改帮助分类界面信息
     * @param typeId 帮助分类Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditHelpTypeViewModel(Long typeId, String fileServerAddress);


    /**
     * 修改帮助分类
     *
     * @param helpTypeForm          帮助分类表单
     * @param imagePath                 图片路径 (已过滤域名)
     * @param images                    图片
     */
    public void editHelpType(HelpType helpTypeForm, String imagePath, MultipartFile images);

    /**
     * 删除帮助分类
     *
     * @param typeId 分类Id
     */
    public void deleteHelpType(Long typeId);
    /**
     * 获取合并帮助分类界面信息
     * @param typeId 分类Id
     * @return
     */
    public Map<String,Object> getMergerHelpTypeViewModel(Long typeId);

    /**
     * 合并帮助分类
     * @param typeId 主分类Id
     * @param mergerTypeId 合并分类Id
     */
    public void mergerHelpType(Long typeId,Long mergerTypeId);

    /**
     * 获取帮助分类分页
     * @param parentId 父标签
     * @param page 页码
     * @return
     */
    public PageView<HelpType> getHelpTypePage(Long parentId, int page);
    /**
     * 获取帮助分类导航
     * @param parentId 父标签
     * @return
     */
    public Map<Long,String> getHelpTypeNavigation(Long parentId);
}
