package cms.service.filterWord;

import cms.dto.PageView;
import cms.dto.fileSystem.FileSystemInterfaceRequest;
import cms.model.fileSystem.FileSystemInterface;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 过滤词服务
 */
public interface FilterWordService {


    /**
     * 获取过滤词展示信息
     * @return
     */
    public Map<String,Object> getFilterWordView();
    /**
     * 词库上传
     * @param file 文件
     */
    public void uploadFilterWord(MultipartFile file);

    /**
     * 删除词库
     */
    public void deleteFilterWord();

}
