package cms.service.help;

import cms.dto.PageView;
import cms.model.help.Help;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 帮助分类服务
 */
public interface HelpService {

    /**
     * 获取帮助
     * @param helpId 帮助Id
     * @param host 域名
     * @param serverAddressList 文件服务器地址集合
     * @return
     */
    public Help getHelp(Long helpId, String host,List<String> serverAddressList);
     /**
     * 获取帮助列表
     * @param visible 是否显示
     * @param page 页码
     * @return
     */
    public PageView<Help> getHelpList(Boolean visible, int page);
    /**
     * 添加帮助
     * @param helpTypeId 帮助分类Id
     * @param name 名称
     * @param content 内容
     * @param isMarkdown 是否为Markdown格式数据
     * @param markdownContent Markdown内容
     * @param request 请求信息
     */
    public void addHelp(Long helpTypeId,String name,String content,Boolean isMarkdown,String markdownContent, HttpServletRequest request);

    /**
     * 获取修改帮助界面信息
     *
     * @param helpId 帮助Id
     * @return
     */
    public Help getEditHelpViewModel(Long helpId);
    /**
     * 上传文件
     * @param helpTypeId 分类Id
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(Long helpTypeId,String dir,String fileName,String fileServerAddress,MultipartFile file);

    /**
     * 修改帮助
     * @param helpId 帮助Id
     * @param helpTypeId 帮助分类Id
     * @param name 名称
     * @param content 内容
     * @param isMarkdown 是否为Markdown格式数据
     * @param markdownContent Markdown内容
     * @param request 请求信息
     */
    public void editHelp(Long helpId, Long helpTypeId,String name,String content,Boolean isMarkdown,String markdownContent, HttpServletRequest request);

    /**
     * 删除帮助
     * @param helpId 帮助Id组
     */
    public void deleteHelp(Long[] helpId);
    /**
     * 还原帮助
     * @param helpId 帮助Id组
     */
    public void reductionHelp(Long[] helpId);

    /**
     * 移动帮助
     * @param helpId 帮助Id集合
     * @param helpTypeId 分类Id
     */
    public void moveHelp(Long[] helpId,Long helpTypeId);

    /**
     * 搜索帮助
     * @param keyword 关键调
     * @param page 页码
     * @return
     */
    public PageView<Help> searchHelp(String keyword,int page);
}
