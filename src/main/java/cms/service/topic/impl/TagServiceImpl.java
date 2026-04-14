package cms.service.topic.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.topic.TagComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.topic.Tag;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TagRepository;
import cms.service.topic.TagService;
import cms.utils.FileUtil;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 话题标签服务
 */
@Service
public class TagServiceImpl implements TagService {
    private static final Logger logger = LogManager.getLogger(TagServiceImpl.class);


    @Resource TagRepository tagRepository;
    @Resource SettingRepository settingRepository;
    @Resource JsonComponent jsonComponent;
    @Resource TextFilterComponent textFilterComponent;
    @Resource FileComponent fileComponent;
    @Resource TagComponent tagComponent;

    //完整路径
    private record FullPath(String imagePath, String fileName) {}

    /**
     * 获取标签列表
     * @param parentId 父标签
     * @param fileServerAddress 文件服务器地址
     * @param page 页码
     * @return
     */
    public PageView<Tag> getTagList(Long parentId,String fileServerAddress,int page){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();


        //如果所属父类有值
        if(parentId != null && parentId >0L){
            jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
            params.add(parentId);//设置o.parentId=?2参数
        }else{//如果没有父类
            //	jpql.append(" and o.parent is null");
            jpql.append(" and o.parentId=?"+ (params.size()+1));
            params.add(0L);
        }

        PageView<Tag> pageView = new PageView<Tag>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据sort字段降序排序


        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<Tag> qr = tagRepository.getScrollData(Tag.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

        pageView.setQueryResult(qr);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Tag tag :pageView.getRecords()){
                if(tag.getImage() != null && !tag.getImage().trim().isEmpty()){
                    tag.setImage(fileServerAddress+tag.getImage());
                }
            }
        }
        return pageView;
    }


    /**
     * 获取标签导航
     * @param parentId 父标签Id
     * @return
     */
    public Map<Long,String> getTabNavigation(Long parentId){
        Map<Long,String> navigation = new LinkedHashMap<Long,String>();
        if(parentId == null || parentId <=0L) {
            return navigation;
        }
        Tag tag = tagRepository.findById(parentId);
        if(tag != null){
            List<Tag> parentTagList = tagRepository.findAllParentById(tag);
            for(Tag t : parentTagList){
                navigation.put(t.getId(), t.getName());
            }
            navigation.put(tag.getId(), tag.getName());
        }
        return navigation;
    }

    /**
     * 获取父标签
     * @param parentId 父标签Id
     * @return
     */
    public Tag getParentTag(Long parentId){
        return tagRepository.findById(parentId);
    }


    /**
     * 添加标签
     * @param tagForm 标签表单
     * @param parentId 父标签
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    public void addTag(Tag tagForm, Long parentId, Map<String,String> multiLanguageExtensionMap,String imagePath, MultipartFile images){
        String parentIdGroup = ",0,";
        parentId = 0L;

        FullPath fullPath = handleFileUpload(imagePath, images);


        Tag tag = new Tag();
        tag.setId(tagComponent.nextNumber());
        tag.setName(tagForm.getName());
        tag.setImage(fullPath.imagePath+fullPath.fileName);
        tag.setParentId(parentId);
        tag.setParentIdGroup(parentIdGroup);

        tag.setSort(tagForm.getSort());
        tag.setMultiLanguageExtension(jsonComponent.toJSONString(multiLanguageExtensionMap));
        tagRepository.saveTag(tag);

        //删除图片锁
        if(!fullPath.imagePath.trim().isEmpty()){
            fileComponent.deleteLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,fullPath.fileName);
        }
    }


    /**
     * 获取修改标签界面信息
     * @param tagId 标签Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditTagViewModel(Long tagId, String fileServerAddress) {
        if(tagId == null || tagId <0L){
            throw new BusinessException(Map.of("tag", "标签Id不能为空"));
        }
        Tag tag = tagRepository.findById(tagId);
        if(tag == null){
            throw new BusinessException(Map.of("tag", "标签不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();


        if(tag.getImage() != null && !tag.getImage().isEmpty()){
            returnValue.put("imagePath",fileServerAddress+tag.getImage());
        }
        returnValue.put("tag",tag);
        return returnValue;
    }

    /**
     * 修改标签
     * @param tagForm 标签表单
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    public void editTag(Tag tagForm,Map<String,String> multiLanguageExtensionMap,String imagePath, MultipartFile images) {
        if(tagForm.getId() == null || tagForm.getId() <0L){
            throw new BusinessException(Map.of("tag", "标签Id不能为空"));
        }
        Tag tag = tagRepository.findById(tagForm.getId());
        if(tag == null){
            throw new BusinessException(Map.of("tag", "标签不存在"));
        }
        FullPath fullPath = handleFileUpload(imagePath, images);

        Tag new_tag = new Tag();
        new_tag.setId(tagForm.getId());
        new_tag.setImage(fullPath.imagePath+fullPath.fileName);
        new_tag.setName(tagForm.getName());
        new_tag.setSort(tagForm.getSort());
        new_tag.setMultiLanguageExtension(jsonComponent.toJSONString(multiLanguageExtensionMap));
        tagRepository.updateTag(new_tag);

        if(tag.getImage() != null && !tag.getImage().trim().isEmpty()){
            if(!(fullPath.imagePath+fullPath.fileName).equals(tag.getImage())){//如果图片有变化
                //删除旧图片
                //替换路径中的..号
                String oldPathFile = FileUtil.toRelativePath(tag.getImage());
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
            }
        }

        //删除图片锁
        if(fullPath.imagePath != null && !fullPath.imagePath.trim().isEmpty()){
            fileComponent.deleteLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,fullPath.fileName);
        }
    }

    /**
     * 删除标签
     * @param tagId 标签Id
     */
    public void deleteTag(Long tagId){
        if(tagId == null || tagId <0L){
            throw new BusinessException(Map.of("tag", "标签Id不能为空"));
        }
        Tag tag = tagRepository.findById(tagId);
        if(tag == null){
            throw new BusinessException(Map.of("tag", "标签不存在"));
        }

        if(tag.getImage() != null && !tag.getImage().trim().isEmpty()){

            //删除旧图片
            //替换路径中的..号
            String oldPathFile = FileUtil.toRelativePath(tag.getImage());
            //删除旧文件
            fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));

        }
        tagRepository.deleteTag(tag);
    }




    /**
     * 处理文件上传
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    private FullPath handleFileUpload(String imagePath, MultipartFile images){
        String _imagePath = "";
        String _fileName = "";
        if (images == null || images.isEmpty()) {
            if(imagePath != null && !imagePath.trim().isEmpty()){
                String fileName = FileUtil.getName(imagePath);
                //取得路径名称
                String pathName = FileUtil.getFullPath(imagePath);

                //旧路径必须为file/topicTag/开头
                if(imagePath.substring(0, 14).equals("file/topicTag/")){
                    //新路径名称
                    String newPathName = "file/topicTag/";

                    //如果新旧路径不一致
                    if(!newPathName.equals(pathName)){

                        //复制文件到新路径
                        try {
                            fileComponent.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
                            //新建文件锁到新路径
                            //添加文件锁
                            fileComponent.addLock("file"+ File.separator+"topicTag"+File.separator+"lock"+File.separator,fileName);
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("上传图片错误",e);
                            }
                            throw new BusinessException(Map.of("images", "上传图片错误"));
                        }
                    }
                    _imagePath = "file/topicTag/";
                    _fileName = fileName;
                }
            }
        }else{
            //验证文件类型
            List<String> formatList = new ArrayList<String>();
            formatList.add("gif");
            formatList.add("jpg");
            formatList.add("jpeg");
            formatList.add("bmp");
            formatList.add("png");
            formatList.add("svg");
            formatList.add("webp");
            boolean isFileTypeValid = FileUtil.validateFileSuffix(images.getOriginalFilename(),formatList);
            if(!isFileTypeValid) {
                throw new BusinessException(Map.of("images", "图片格式错误"));
            }
            //取得文件后缀
            String ext = FileUtil.getExtension(images.getOriginalFilename());
            //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
            String pathDir = "file"+File.separator+"topicTag"+File.separator;
            //构建文件名称
            String fileName = UUIDUtil.getUUID32()+ "." + ext;
            _imagePath = "file/topicTag/";
            _fileName = fileName;

            //生成文件保存目录
            FileUtil.createFolder(pathDir);

            //添加文件锁
            try {
                fileComponent.addLock("file"+File.separator+"topicTag"+File.separator+"lock"+File.separator,fileName);
                //保存文件
                fileComponent.writeFile(pathDir, fileName,images.getBytes());
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("上传图片错误",e);
                }
                throw new BusinessException(Map.of("images", "上传图片错误"));
            }
        }
        return new FullPath(_imagePath,_fileName);
    }




    /**
     * 获取所有标签
     * @return
     */
    public List<Tag> getAllTag(){
        List<Tag> tagList = tagRepository.findAllTag();


        List<Tag> new_tagList = new ArrayList<Tag>();//排序后标签

        if(tagList != null && tagList.size() >0){

            //组成排序数据
            Iterator<Tag> tagList_iter = tagList.iterator();
            while(tagList_iter.hasNext()){
                Tag tag = tagList_iter.next();

                //如果是根节点
                if(tag.getParentId().equals(0L)){

                    new_tagList.add(tag);
                    tagList_iter.remove();
                }
            }
            //组合子标签
            for(Tag tag :new_tagList){
                tagComponent.childTag(tag,tagList);
            }
            //排序
            tagComponent.tagSort(new_tagList);
        }
        return new_tagList;
    }


}