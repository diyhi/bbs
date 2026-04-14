package cms.controller.filePackage;

import cms.config.BusinessException;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.filePackage.FileResource;
import cms.service.filePackage.FilePackageService;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 文件打包管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/filePackage/manage")
public class FilePackageManageController {
    @Resource
    FilePackageService filePackageService;
    @Resource
    MessageSource messageSource;

    /**
     * 下载压缩文件
     * @param fileName 文件名称
     * @param range    范围
     * @param request  请求信息
     * @param response 响应信息
     * @return
     */
    @RequestMapping(params = "method=download", method = RequestMethod.GET)
    public ResponseEntity<StreamingResponseBody> download(String fileName, @RequestHeader(required = false) String range,
                                                          HttpServletRequest request, HttpServletResponse response) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new BusinessException(Map.of("file", "文件不名称不能为空"));
        }
        //替换路径中的..号
        fileName = FileUtil.toRelativePath(fileName);

        String templateFile_path = "data" + File.separator + "filePackage" + File.separator + fileName;
        String filePackagePath = PathUtil.defaultExternalDirectory() + File.separator + templateFile_path;

        return WebUtil.rangeDownloadResponse(filePackagePath, fileName, range, request, response);
    }



    /**
     * 删除压缩文件
     *
     * @param fileName 打包好的文件名称
     * @return
     */
    @RequestMapping(params = "method=delete", method = RequestMethod.POST)
    public RequestResult deleteExport(String fileName) {
        filePackageService.deleteExport(fileName);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 打包界面
     */
    @RequestMapping(params = "method=package", method = RequestMethod.GET)
    public RequestResult packageUI() {
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 打包
     * @param idGroup 选中Id
     * @return
     */
    @RequestMapping(params = "method=package", method = RequestMethod.POST)
    public RequestResult packages(String[] idGroup) {
        filePackageService.packages(idGroup);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
    /**
     * 查询子目录
     * @param parentId 父Id
     */
    @RequestMapping(params = "method=querySubdirectory", method = RequestMethod.GET)
    public RequestResult querySubdirectory(String parentId) {
        List<FileResource> fileResourceList = filePackageService.querySubdirectory(parentId);
        return new RequestResult(ResultCode.SUCCESS, fileResourceList);
    }
}
