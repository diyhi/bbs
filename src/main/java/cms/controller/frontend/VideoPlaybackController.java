package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.service.frontend.VideoPlaybackService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 视频播放控制器
 */
@RestController
public class VideoPlaybackController {
    @Resource VideoPlaybackService videoPlaybackService;

    /**
     * 视频重定向
     * @param jump 重定向参数
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1151200)
    @RequestMapping("/videoRedirect")
    public Map<String,Object> videoRedirect(String jump){
        return videoPlaybackService.getVideoRedirectViewModel(jump);
    }

    /**
     * 动态播放列表(动态生成M3U8)
     * @param jump 重定向参数
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1151300)
    @RequestMapping("/dynamicPlaylist")
    public ResponseEntity<byte[]> dynamicPlaylist(String jump, HttpServletRequest request){
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 返回 404
    }
}
