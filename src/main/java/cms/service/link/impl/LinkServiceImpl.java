package cms.service.link.impl;


import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.config.BusinessException;
import cms.dto.link.LinkRequest;
import cms.model.links.Links;
import cms.repository.link.LinkRepository;
import cms.repository.setting.SettingRepository;
import cms.service.link.LinkService;
import cms.utils.FileUtil;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 友情链接服务
 */
@Service
public class LinkServiceImpl implements LinkService {
    private static final Logger logger = LogManager.getLogger(LinkServiceImpl.class);

    @Resource
    LinkRepository linkRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;


    //完整路径
    private record FullPath(String imagePath, String fileName) {}

    /**
     * 获取友情链接列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     */
    public  List<Links> getLinkList(int page,String fileServerAddress){
        List<Links> linksList = linkRepository.findAllLinks();
        if(linksList != null && linksList.size() >0){
            for(Links links : linksList){
                if(links.getImage() != null && !links.getImage().trim().isEmpty()){
                    links.setImage(fileServerAddress+links.getImage());
                }
            }
        }
        return linksList;
    }



    /**
     * 添加友情链接
     * @param linkRequest 友情链接表单
     * @param images 图片
     */
    public void addLink(LinkRequest linkRequest,MultipartFile images){

        Links links = new Links();

        LinkServiceImpl.FullPath fullPath = handleFileUpload(linkRequest.getImagePath(), images);


        links.setCreateDate(LocalDateTime.now());//创建时间
        links.setImage(fullPath.imagePath+fullPath.fileName);

        links.setName(linkRequest.getName());
        links.setWebsite(linkRequest.getWebsite());
        links.setSort(linkRequest.getSort());

        //写入数据库操作
        linkRepository.saveLinks(links);

        //删除图片锁
        if(fullPath.imagePath != null && !fullPath.imagePath.trim().isEmpty()){
            fileComponent.deleteLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,fullPath.fileName);
        }

    }


    /**
     * 获取修改友情链接界面信息
     * @param linksId 友情链接Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditLinkViewModel(Integer linksId,String fileServerAddress){
        if(linksId == null || linksId <=0){
            throw new BusinessException(Map.of("linksId", "友情链接Id不能为空"));
        }
        Links links = linkRepository.findById(linksId);
        if(links == null){
            throw new BusinessException(Map.of("linksId", "友情链接不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(links.getImage() != null && !links.getImage().isEmpty()){
            returnValue.put("imagePath",fileServerAddress+links.getImage());
        }
        returnValue.put("links",links);//返回消息

        return returnValue;
    }
    /**
     * 修改友情链接
     * @param linkRequest 友情链接表单
     * @param images 图片
     */
    public void editLink(LinkRequest linkRequest,MultipartFile images){
        if(linkRequest.getLinksId() == null || linkRequest.getLinksId() <=0){
            throw new BusinessException(Map.of("links", "友情链接Id不能为空"));
        }
        Links old_links = linkRepository.findById(linkRequest.getLinksId());
        if(old_links == null){
            throw new BusinessException(Map.of("linksId", "友情链接不存在"));
        }

        LinkServiceImpl.FullPath fullPath = handleFileUpload(linkRequest.getImagePath(), images);

        Links links = new Links();
        links.setId(linkRequest.getLinksId());
        links.setImage(fullPath.imagePath+fullPath.fileName);

        links.setName(linkRequest.getName());
        links.setWebsite(linkRequest.getWebsite());
        links.setSort(linkRequest.getSort());

        //写入数据库操作
        linkRepository.updateLinks(links);

        if(old_links.getImage() != null && !old_links.getImage().trim().isEmpty()){
            if(!(fullPath.imagePath+fullPath.fileName).equals(old_links.getImage())){//如果图片有变化
                //删除旧图片
                //替换路径中的..号
                String oldPathFile = FileUtil.toRelativePath(old_links.getImage());
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
            }
        }

        //删除图片锁
        if(fullPath.imagePath != null && !fullPath.imagePath.trim().isEmpty()){
            fileComponent.deleteLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,fullPath.fileName);
        }
    }

    /**
     * 删除友情链接
     * @param linksId 友情链接Id
     */
    public void deleteLink(Integer linksId){
        if(linksId == null || linksId <=0){
            throw new BusinessException(Map.of("linksId", "友情链接Id不能为空"));
        }
        Links links = linkRepository.findById(linksId);
        if(links == null){
            throw new BusinessException(Map.of("linksId", "友情链接不存在"));
        }
        int i = linkRepository.deleteLinks(linksId);
        if(links.getImage() != null && !links.getImage().trim().isEmpty()){

            //删除旧图片
            //替换路径中的..号
            String oldPathFile = FileUtil.toRelativePath(links.getImage());
            //删除旧文件
            fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));

        }
    }


    /**
     * 处理文件上传
     * @param imagePath 图片路径 (已过滤域名)
     * @param images 图片
     */
    private LinkServiceImpl.FullPath handleFileUpload(String imagePath, MultipartFile images){
        String _imagePath = "";
        String _fileName = "";
        if (images == null || images.isEmpty()) {
            if(imagePath != null && !imagePath.trim().isEmpty()){
                String fileName = FileUtil.getName(imagePath);
                //取得路径名称
                String pathName = FileUtil.getFullPath(imagePath);

                //旧路径必须为file/links/开头
                if(imagePath.substring(0, 11).equals("file/links/")){
                    //新路径名称
                    String newPathName = "file/links/";

                    //如果新旧路径不一致
                    if(!newPathName.equals(pathName)){

                        //复制文件到新路径
                        try {
                            fileComponent.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
                            //新建文件锁到新路径
                            //添加文件锁
                            fileComponent.addLock("file"+ File.separator+"links"+File.separator+"lock"+File.separator,fileName);
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("上传图片错误",e);
                            }
                            throw new BusinessException(Map.of("images", "上传图片错误"));
                        }
                    }
                    _imagePath = "file/links/";
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
            String pathDir = "file"+File.separator+"links"+File.separator;
            //构建文件名称
            String fileName = UUIDUtil.getUUID32()+ "." + ext;
            _imagePath = "file/links/";
            _fileName = fileName;

            //生成文件保存目录
            FileUtil.createFolder(pathDir);

            //添加文件锁
            try {
                fileComponent.addLock("file"+File.separator+"links"+File.separator+"lock"+File.separator,fileName);
                //保存文件
                fileComponent.writeFile(pathDir, fileName,images.getBytes());
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("上传图片错误",e);
                }
                throw new BusinessException(Map.of("images", "上传图片错误"));
            }
        }
        return new LinkServiceImpl.FullPath(_imagePath,_fileName);
    }

}
