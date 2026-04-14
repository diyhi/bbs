package cms.service.filePackage;

import cms.dto.PageView;
import cms.dto.filePackage.FilePackage;
import cms.dto.filePackage.FileResource;
import cms.dto.sms.SmsInterfaceRequest;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * 文件打包服务
 */
public interface FilePackageService {

    /**
     * 获取文件打包列表
     */
    public List<FilePackage> getFilePackageList();
    /**
     * 删除压缩文件
     * @param fileName 文件名称
     */
    public void deleteExport(String fileName);
    /**
     * 打包
     * @param idGroup 选中Id
     */
    public void packages(String[] idGroup);
    /**
     * 查询子目录
     * @param parentId 父Id
     * @return
     */
    public List<FileResource> querySubdirectory(String parentId);
}
