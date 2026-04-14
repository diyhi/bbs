package cms.service.thumbnail.impl;


import cms.component.JsonComponent;
import cms.component.thumbnail.ThumbnailComponent;
import cms.config.BusinessException;
import cms.dto.thumbnail.ThumbnailRequest;
import cms.model.thumbnail.Thumbnail;
import cms.repository.setting.SettingRepository;
import cms.repository.thumbnail.ThumbnailRepository;
import cms.service.thumbnail.ThumbnailService;
import cms.utils.FileUtil;
import cms.utils.PathUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * 缩略图服务
 */
@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    @Resource
    ThumbnailRepository thumbnailRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    ThumbnailComponent thumbnailComponent;
    @Resource
    JsonComponent jsonComponent;


    /**
     * 获取缩略图列表
     */
    public List<Thumbnail> getThumbnailList(){
        return thumbnailRepository.findAllThumbnail();
    }

    /**
     * 添加缩略图
     * @param thumbnailRequest 缩略图表单
     */
    public void addThumbnail(ThumbnailRequest thumbnailRequest){
        StringBuffer specificationGroup = new StringBuffer("");

        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setName(thumbnailRequest.getName());
        thumbnail.setWidth(thumbnailRequest.getWidth());
        thumbnail.setHigh(thumbnailRequest.getHigh());

        specificationGroup.append(thumbnailRequest.getWidth() == null ?"" : thumbnailRequest.getWidth());
        specificationGroup.append("x");
        specificationGroup.append(thumbnailRequest.getHigh() == null ? "" : thumbnailRequest.getHigh());
        thumbnail.setSpecificationGroup(specificationGroup.toString());

        Thumbnail t = thumbnailRepository.findThumbnailBySpecificationGroup(specificationGroup.toString());
        if(t != null){
            throw new BusinessException(Map.of("specificationGroup", "当前规格已存在"));
        }

        File file = new File(PathUtil.defaultExternalDirectory()+File.separator+"file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+specificationGroup.toString()+".txt");
        if(file.exists()){
            throw new BusinessException(Map.of("specificationGroup", "当前规格运行中，请稍后再添加"));
        }

        //添加样式增加缩略图标记
        FileUtil.writeStringToFile("file"+ File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+specificationGroup.toString()+".txt","+","utf-8", false);

        thumbnailRepository.saveThumbnail(thumbnail);
    }


    /**
     * 删除缩略图
     * @param thumbnailId 缩略图Id
     */
    public void deleteThumbnail(Integer thumbnailId){
        if(thumbnailId == null || thumbnailId <=0){
            throw new BusinessException(Map.of("thumbnailId", "缩略图Id不能为空"));
        }
        Thumbnail thumbnail = thumbnailRepository.findByThumbnailId(thumbnailId);
        if(thumbnail == null){
            throw new BusinessException(Map.of("thumbnailId", "缩略图不存在"));
        }

        //添加样式删除缩略图标记
        FileUtil.writeStringToFile("file"+File.separator+"topic"+File.separator+"thumbnailMarker"+File.separator+thumbnail.getSpecificationGroup()+".txt","-","utf-8", false);

        int i = thumbnailRepository.deleteThumbnail(thumbnailId);
        if(i ==0){
            throw new BusinessException(Map.of("thumbnailId", "缩略图删除错误"));
        }
    }


}
