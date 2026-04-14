package cms.controller.filePackage;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.filePackage.FilePackage;
import cms.service.filePackage.FilePackageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文件打包控制器
 *
 */
@RestController
public class FilePackageController {
    @Resource
    FilePackageService filePackageService;
    /**
     * 文件打包列表
     * @return
     */
    @RequestMapping("/control/filePackage/list")
    public RequestResult filePackageList(){
        List<FilePackage> list = filePackageService.getFilePackageList();
        return new RequestResult(ResultCode.SUCCESS, list);
    }
}
