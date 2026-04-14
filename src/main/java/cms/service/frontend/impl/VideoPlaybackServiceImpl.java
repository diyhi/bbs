package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.setting.SettingCacheManager;
import cms.component.user.UserRoleComponent;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.setting.SystemSetting;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.VideoPlaybackService;
import cms.utils.*;
import cms.utils.Base64;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.*;

/**
 * 视频播放服务
 */
@Service
public class VideoPlaybackServiceImpl implements VideoPlaybackService {
    private static final Logger logger = LogManager.getLogger(VideoPlaybackServiceImpl.class);


    @Resource UserRoleComponent userRoleComponent;
    @Resource SettingRepository settingRepository;
    @Resource JsonComponent jsonComponent;
    @Resource SettingCacheManager settingCacheManager;
    @Resource FileComponent fileComponent;
    @Resource TextFilterComponent textFilterComponent;



    /**
     * 获取视频重定向界面信息
     * @param jump 重定向参数
     * @return
     */
    public Map<String,Object> getVideoRedirectViewModel(String jump){
        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("isPermission", true); // 默认有权限
        returnValue.put("redirect", "");//重定向地址

        if (jump == null || jump.trim().isEmpty()) {
            return returnValue;
        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        String ciphertext = Base64.decodeBase64URL(jump.trim());
        String parameter_json = AES.decrypt(ciphertext, systemSetting.getFileSecureLinkSecret(), null);

        Map<String,String> parameterMap = jsonComponent.toGenericObject(parameter_json, new TypeReference< Map<String,String>>(){});

        String link = parameterMap.get("link");	//文件链接
        String tagId = parameterMap.get("tagId");//话题标签Id
        Long _tagId = Long.parseLong(tagId);

        if(_tagId .equals(-1L)){//如果为-1,则是后台管理页面下载，不检查权限
            returnValue.put("redirect", fileComponent.createSignLink(link,"",systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire()));
        }else{
            //检查权限,允许查看话题内容的角色则允许下载
            boolean flag = userRoleComponent.isPermission(ResourceEnum._1001000,_tagId);
            if(flag){
                returnValue.put("redirect", fileComponent.createSignLink(link,"",systemSetting.getFileSecureLinkSecret(),systemSetting.getFileSecureLinkExpire()));
            }else{
                returnValue.put("isPermission", false);//是否有权限查看
            }
        }
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            returnValue.put("isLogin", true);//是否登录用户
        }else{
            returnValue.put("isLogin", false);
        }

        return returnValue;
    }



}