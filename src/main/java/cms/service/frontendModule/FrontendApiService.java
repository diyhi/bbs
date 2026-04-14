package cms.service.frontendModule;

import cms.dto.frontendModule.ApiMetadata;
import cms.dto.frontendModule.DynamicRouteGroupDTO;
import cms.dto.frontendModule.FrontendApiForm;
import cms.model.frontendModule.FrontendApi;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * 前台API服务
 */
public interface FrontendApiService {


    /**
     * 添加前台API
     * @param frontendApiForm 前台API功能参数配置对象表单
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     */
    public void addFrontendApi(FrontendApiForm frontendApiForm, MultipartFile[] imageAdFile, HttpServletRequest request);

    /**
     * 获取修改前台API界面信息
     * @param frontendApiId 前台ApiId
     * @param request 请求信息
     * @return
     */
    public FrontendApi getEditFrontendApiViewModel(Integer frontendApiId, HttpServletRequest request);

    /**
     * 修改前台API
     * @param frontendApiForm 前台API表单
     * @param imageAdFile 图片广告文件
     * @param request 请求信息
     */
    public void editFrontendApi(FrontendApiForm frontendApiForm, MultipartFile[] imageAdFile, HttpServletRequest request);
    /**
     * 删除前台API
     * @param frontendApiId 前台ApiId
     * @param request 请求信息
     */
    public void deleteFrontendApi(Integer frontendApiId, HttpServletRequest request);

    /**
     * 文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileServerAddress 文件服务器随机地址
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     */
    public Map<String,Object> upload(String dir,String fileServerAddress,String fileName,MultipartFile file);

    /**
     * 校验URL路径
     * @param frontendApiId 前台ApiId
     * @param url URL路径
     * @param httpMethod 请求方法
     * @return
     */
    public boolean checkUrl(Integer frontendApiId,String url,String httpMethod);
    /**
     * 获取自定义动态路由组
     * @return
     */
    public List<DynamicRouteGroupDTO> getAllCustomRouteGroup();
}
