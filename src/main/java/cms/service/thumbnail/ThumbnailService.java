package cms.service.thumbnail;

import cms.dto.thumbnail.ThumbnailRequest;
import cms.model.thumbnail.Thumbnail;

import java.util.List;

/**
 * 缩略图服务
 */
public interface ThumbnailService {

    /**
     * 获取缩略图列表
     */
    public List<Thumbnail> getThumbnailList();

    /**
     * 添加缩略图
     * @param thumbnailRequest 缩略图表单
     */
    public void addThumbnail(ThumbnailRequest thumbnailRequest);
    /**
     * 删除缩略图
     * @param thumbnailId 缩略图Id
     */
    public void deleteThumbnail(Integer thumbnailId);
}
