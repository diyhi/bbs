package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.user.UserRoleComponent;
import cms.dto.user.ResourceEnum;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.FileDownloadService;
import cms.utils.AES;
import cms.utils.Base64;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件下载服务
 */
@Service
public class FileDownloadServiceImpl implements FileDownloadService {
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    SettingRepository settingRepository;
    @Resource
    UserRoleComponent userRoleComponent;

    /**
     * 文件下载
     * @param jump 重定向参数
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    public Object fileDownload(String jump,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(jump)) {
            return buildErrorResponse("跳转参数不能为空");
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        String ciphertext = Base64.decodeBase64URL(jump.trim());
        String parameterJson = AES.decrypt(ciphertext, systemSetting.getFileSecureLinkSecret(), null);
        Map<String, String> parameterMap = jsonComponent.toGenericObject(parameterJson, new TypeReference<Map<String, String>>() {});

        String link = parameterMap.get("link");
        String fileName = parameterMap.get("fileName");
        Long tagId = Long.valueOf(parameterMap.getOrDefault("tagId", "-1"));

        //权限校验逻辑
        boolean hasPermission = (tagId.equals(-1L)) || userRoleComponent.isPermission(ResourceEnum._1001000, tagId);

        if (!hasPermission) {
            return buildErrorResponse("下载失败");
        }

        // 生成签名链接
        String signedLink = fileComponent.createSignLink(link, URLEncoder.encode(fileName,StandardCharsets.UTF_8 ),systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire());

        if (WebUtil.submitDataMode(request)) {
            // Ajax 方式：返回 JSON
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("redirect", signedLink);
            return result;
        } else {
            // 普通跳转方式：执行重定向
            response.sendRedirect(signedLink);
            return null;
        }

    }


    /**
     * 构建统一错误格式
     * @param messageValue 消息内容
     * @return
     */
    private Map<String, Object> buildErrorResponse(String messageValue) {
        Map<String, Object> res = new HashMap<>();
        Map<String, String> error = new HashMap<>();
        error.put("message", messageValue);
        res.put("success", false);
        res.put("error", error);
        return res;
    }
}
