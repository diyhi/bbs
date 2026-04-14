package cms.service.frontend;



import java.util.Map;


/**
 * 视频播放服务接口
 */
public interface VideoPlaybackService {
    /**
     * 获取视频重定向界面信息
     * @param jump 重定向参数
     * @return
     */
    public Map<String,Object> getVideoRedirectViewModel(String jump);


}
