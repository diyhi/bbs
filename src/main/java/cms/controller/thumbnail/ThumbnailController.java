package cms.controller.thumbnail;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.thumbnail.Thumbnail;
import cms.service.thumbnail.ThumbnailService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 缩略图列表控制器
 *
 */
@RestController
public class ThumbnailController {
    @Resource
    ThumbnailService thumbnailService;
    /**
     * 缩略图列表
     * @return
     */
    @RequestMapping("/control/thumbnail/list")
    public RequestResult thumbnailList(){
        List<Thumbnail> list = thumbnailService.getThumbnailList();
        return new RequestResult(ResultCode.SUCCESS, list);
    }
}
