package cms.service.help.impl;

import cms.component.fileSystem.FileComponent;
import cms.component.help.HelpTypeComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.help.HelpType;
import cms.repository.help.HelpTypeRepository;
import cms.repository.setting.SettingRepository;
import cms.service.help.HelpTypeService;
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
 * 帮助分类服务
 */
@Service
public class HelpTypeServiceImpl implements HelpTypeService {
    private static final Logger logger = LogManager.getLogger(HelpTypeServiceImpl.class);


    @Resource HelpTypeRepository helpTypeRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    HelpTypeComponent helpTypeComponent;

    //完整路径
    private record FullPath(String imagePath, String fileName) {
    }

    /**
     * 获取标签列表
     *
     * @param parentId          父标签
     * @param fileServerAddress 文件服务器地址
     * @param page              页码
     * @return
     */
    public PageView<HelpType> getHelpTypeList(Long parentId, String fileServerAddress, int page) {
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

        PageView<HelpType> pageView = new PageView<HelpType>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据typeid字段降序排序


        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<HelpType> qr = helpTypeRepository.getScrollData(HelpType.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

        pageView.setQueryResult(qr);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(HelpType helpType :pageView.getRecords()){
                if(helpType.getImage() != null && !helpType.getImage().trim().isEmpty()){
                    helpType.setImage(fileServerAddress+helpType.getImage());
                }

            }
        }

        return pageView;

    }

    /**
     * 获取分类导航
     *
     * @param parentId 父标签
     * @return
     */
    public Map<Long, String> getTypeNavigation(Long parentId) {
        Map<Long,String> navigation = new LinkedHashMap<Long,String>();
        if(parentId != null && parentId >0L){
            HelpType helpType = helpTypeRepository.findById(parentId);
            if(helpType != null){
                List<HelpType> parentHelpTypeList = helpTypeRepository.findAllParentById(helpType);
                for(HelpType p : parentHelpTypeList){
                    navigation.put(p.getId(), p.getName());
                }
                navigation.put(helpType.getId(), helpType.getName());
            }

        }
        return navigation;
    }

    /**
     * 获取添加帮助分类界面信息
     * @param parentId 父标签
     * @return
     */
    public Map<String,Object> getAddHelpTypeViewModel(Long parentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(parentId != null && parentId >0L){
            HelpType parentHelpType = helpTypeRepository.findById(parentId);
            if(parentHelpType == null) {
                throw new BusinessException(Map.of("parentId", "父类不存在"));
            }
            returnValue.put("parentHelpType",parentHelpType);//返回消息

            Map<Long,String> navigation = new LinkedHashMap<Long,String>();

            List<HelpType> allParentHelpTypeList = helpTypeRepository.findAllParentById(parentHelpType);
            for(HelpType p : allParentHelpTypeList){
                navigation.put(p.getId(), p.getName());
            }
            navigation.put(parentHelpType.getId(), parentHelpType.getName());
            returnValue.put("navigation", navigation);//分类导航

        }
        return returnValue;
    }

    /**
     * 添加帮助分类
     * @param helpTypeForm 帮助分类表单
     * @param parentId 父Id
     * @param imagePath 图片路径
     * @param images 图片
     */
    public void addHelpType(HelpType helpTypeForm, Long parentId, String imagePath, MultipartFile images){
        HelpType parentHelpType = null;
        if(parentId != null && parentId >0L){
            parentHelpType = helpTypeRepository.findById(parentId);
            if(parentHelpType == null){
                throw new BusinessException(Map.of("parentId", "父类不存在"));
            }
            if(parentHelpType.getParentIdGroup().length() >180){
                throw new BusinessException(Map.of("helpType", "分类已达到最大层数,添加失败"));
            }
        }


       FullPath fullPath = handleFileUpload(imagePath, images);

        HelpType type = new HelpType();
        type.setId(helpTypeComponent.nextNumber());
        type.setName(helpTypeForm.getName());
        type.setDescription(helpTypeForm.getDescription());
        type.setImage(fullPath.imagePath+fullPath.fileName);
        type.setSort(helpTypeForm.getSort());
        if(parentHelpType != null){
            type.setParentId(helpTypeForm.getParentId());
            type.setParentIdGroup(parentHelpType.getParentIdGroup()+helpTypeForm.getParentId()+",");
        }

        helpTypeRepository.saveType(type);
        //删除图片锁
        if(fullPath.imagePath != null && !fullPath.imagePath.trim().isEmpty()){
            fileComponent.deleteLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,fullPath.fileName);
        }

    }

    /**
     * 获取修改帮助分类界面信息
     * @param typeId 帮助分类Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditHelpTypeViewModel(Long typeId, String fileServerAddress) {
        if (typeId == null || typeId < 0L) {
            throw new BusinessException(Map.of("typeId", "分类Id不能为空"));
        }
        HelpType helpType = helpTypeRepository.findById(typeId);
        if(helpType == null){
            throw new BusinessException(Map.of("type", "分类不存在"));
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(helpType.getImage() != null && !helpType.getImage().isEmpty()){
            returnValue.put("imagePath",fileServerAddress+helpType.getImage());
        }
        returnValue.put("helpType",helpType);//返回消息

        Map<Long,String> navigation = new LinkedHashMap<Long,String>();
        List<HelpType> allParentHelpTypeList = helpTypeRepository.findAllParentById(helpType);
        for(HelpType p : allParentHelpTypeList){
            navigation.put(p.getId(), p.getName());
        }
        returnValue.put("navigation", navigation);//分类导航
        return returnValue;
    }

    /**
     * 修改帮助分类
     *
     * @param helpTypeForm          帮助分类表单
     * @param imagePath                 图片路径 (已过滤域名)
     * @param images                    图片
     */
    public void editHelpType(HelpType helpTypeForm, String imagePath, MultipartFile images) {
        if (helpTypeForm.getId() == null || helpTypeForm.getId() < 0L) {
            throw new BusinessException(Map.of("typeId", "分类Id不能为空"));
        }
        HelpType helpType = helpTypeRepository.findById(helpTypeForm.getId());
        if(helpType == null){
            throw new BusinessException(Map.of("type", "分类不存在"));
        }
        FullPath fullPath = handleFileUpload(imagePath, images);

        HelpType type = new HelpType();
        type.setId(helpTypeForm.getId());
        type.setName(helpTypeForm.getName());
        type.setSort(helpTypeForm.getSort());
        type.setImage(fullPath.imagePath+fullPath.fileName);
        type.setDescription(helpTypeForm.getDescription());
        helpTypeRepository.updateHelpType(type);

        if(helpType.getImage() != null && !helpType.getImage().trim().isEmpty()){
            if(!(fullPath.imagePath+fullPath.fileName).equals(helpType.getImage())){//如果图片有变化
                //删除旧图片
                //替换路径中的..号
                String oldPathFile = FileUtil.toRelativePath(helpType.getImage());
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
            }
        }

        //删除图片锁
        if(fullPath.imagePath != null && !fullPath.imagePath.trim().isEmpty()){
            fileComponent.deleteLock("file"+File.separator+"helpType"+File.separator+"lock"+File.separator,fullPath.fileName);
        }
    }



    /**
     * 删除帮助分类
     *
     * @param typeId 分类Id
     */
    public void deleteHelpType(Long typeId) {
        if (typeId == null ||typeId < 0L) {
            throw new BusinessException(Map.of("typeId", "分类Id不能为空"));
        }
        HelpType helpType = helpTypeRepository.findById(typeId);
        if(helpType == null){
            throw new BusinessException(Map.of("type", "分类不存在"));
        }

        //删除目录组
        StringBuffer delete_dirGroup = new StringBuffer("");

        delete_dirGroup.append(","+helpType.getId()).append(helpType.getMergerTypeId());

        String idGroup = helpType.getParentIdGroup()+helpType.getId()+",";

        //读取当前id下所有分类
        List<HelpType> productTypeList = helpTypeRepository.findChildHelpTypeByIdGroup(idGroup);
        for(HelpType it : productTypeList){

            delete_dirGroup.append(","+it.getId()).append(it.getMergerTypeId());

            if(it.getImage() != null && !it.getImage().trim().isEmpty()){

                //删除旧图片
                //替换路径中的..号
                String oldPathFile = FileUtil.toRelativePath(it.getImage());
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));

            }

        }
        if(helpType.getImage() != null && !helpType.getImage().trim().isEmpty()){

            //删除旧图片
            //替换路径中的..号
            String oldPathFile = FileUtil.toRelativePath(helpType.getImage());
            //删除旧文件
            fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));

        }

        String[] old_typeId_array = delete_dirGroup.toString().split(",");
        if(old_typeId_array.length >0){
            for(String old_typeId :old_typeId_array){
                if(old_typeId != null && !old_typeId.isEmpty()){

                    //清空目录
                    Boolean state = fileComponent.removeDirectory("file"+File.separator+"help"+File.separator+old_typeId+File.separator);
                    if(state != null && !state){
                        //创建删除失败目录文件
                        fileComponent.failedStateFile("file"+File.separator+"help"+File.separator+"lock"+File.separator+"#"+old_typeId);
                    }


                }
            }
        }

        helpTypeRepository.deleteHelpType(helpType);
    }

    /**
     * 获取合并帮助分类界面信息
     * @param typeId 分类Id
     * @return
     */
    public Map<String,Object> getMergerHelpTypeViewModel(Long typeId){
        if(typeId == null || typeId < 0L){
            throw new BusinessException(Map.of("typeId", "分类Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        HelpType helpType = helpTypeRepository.findById(typeId);
        if(helpType == null){
            throw new BusinessException(Map.of("type", "分类不存在"));
        }
        returnValue.put("helpType",helpType);//返回消息
        Map<Long,String> navigation = new LinkedHashMap<Long,String>();
        List<HelpType> parentHelpTypeList = helpTypeRepository.findAllParentById(helpType);
        for(HelpType p : parentHelpTypeList){
            navigation.put(p.getId(), p.getName());
        }
        returnValue.put("navigation", navigation);//分类导航
        return returnValue;
    }

    /**
     * 合并帮助分类
     * @param typeId 主分类Id
     * @param mergerTypeId 合并分类Id
     */
    public void mergerHelpType(Long typeId,Long mergerTypeId){
        if(typeId == null || typeId < 0L){
            throw new BusinessException(Map.of("typeId", "分类Id不能为空"));
        }
        HelpType helpType = helpTypeRepository.findById(typeId);
        if(helpType == null){
            throw new BusinessException(Map.of("type", "分类不存在"));
        }
        if(!helpType.getChildNodeNumber().equals(0)){
            throw new BusinessException(Map.of("typeId", "分类最后一级节点才能合并"));
        }
        if(typeId.equals(mergerTypeId)){
            throw new BusinessException(Map.of("typeId", "不能选择同一节点"));
        }
        if(mergerTypeId == null || mergerTypeId <=0L){
            throw new BusinessException(Map.of("typeId", "合并Id不存在"));
        }
        HelpType merger_productType = helpTypeRepository.findById(mergerTypeId);
        if(merger_productType == null){
            throw new BusinessException(Map.of("typeId", "请选择分类"));
        }
        if(!merger_productType.getChildNodeNumber().equals(0)){
            throw new BusinessException(Map.of("typeId", "请选择分类最后一级节点"));
        }
        helpTypeRepository.mergerHelpType(typeId,merger_productType);
    }

    /**
     * 获取帮助分类分页
     * @param parentId 父标签
     * @param page 页码
     * @return
     */
    public PageView<HelpType> getHelpTypePage(Long parentId, int page){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        //如果所属父类有值
        if(parentId != null && parentId >0L){
            jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
            params.add(parentId);//设置o.parentId=?2参数
        }else{//如果没有父类
            jpql.append(" and o.parentId=?"+ (params.size()+1));
            params.add(0L);
        }

        PageView<HelpType> pageView = new PageView<HelpType>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据sort字段降序排序

        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());
        //调用分页算法类
        QueryResult<HelpType> qr = helpTypeRepository.getScrollData(HelpType.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取帮助分类导航
     * @param parentId 父标签
     * @return
     */
    public Map<Long,String> getHelpTypeNavigation(Long parentId){
        Map<Long,String> navigation = new LinkedHashMap<Long,String>();
        if(parentId != null && parentId >0L){

            HelpType helpType = helpTypeRepository.findById(parentId);
            if(helpType != null){
                List<HelpType> allParentHelpTypeList = helpTypeRepository.findAllParentById(helpType);
                for(HelpType p : allParentHelpTypeList){
                    navigation.put(p.getId(), p.getName());
                }
                navigation.put(helpType.getId(), helpType.getName());
            }
        }
        return navigation;
    }



    /**
     * 处理文件上传
     *
     * @param imagePath 图片路径 (已过滤域名)
     * @param images    图片
     */
    private FullPath handleFileUpload(String imagePath, MultipartFile images) {
        String _imagePath = "";
        String _fileName = "";
        if (images == null || images.isEmpty()) {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                String fileName = FileUtil.getName(imagePath);
                //取得路径名称
                String pathName = FileUtil.getFullPath(imagePath);

                //旧路径必须为file/helpType/开头
                if(imagePath.substring(0, 14).equals("file/helpType/")){
                    //新路径名称
                    String newPathName = "file/helpType/";

                    //如果新旧路径不一致
                    if (!newPathName.equals(pathName)) {

                        //复制文件到新路径
                        try {
                            fileComponent.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
                            //新建文件锁到新路径
                            //添加文件锁
                            fileComponent.addLock("file" + File.separator + "helpType" + File.separator + "lock" + File.separator, fileName);
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("上传图片错误", e);
                            }
                            throw new BusinessException(Map.of("images", "上传图片错误"));
                        }
                    }
                    _imagePath = "file/helpType/";
                    _fileName = fileName;
                }
            }
        } else {
            //验证文件类型
            List<String> formatList = new ArrayList<String>();
            formatList.add("gif");
            formatList.add("jpg");
            formatList.add("jpeg");
            formatList.add("bmp");
            formatList.add("png");
            formatList.add("svg");
            formatList.add("webp");
            boolean isFileTypeValid = FileUtil.validateFileSuffix(images.getOriginalFilename(), formatList);
            if (!isFileTypeValid) {
                throw new BusinessException(Map.of("images", "图片格式错误"));
            }
            //取得文件后缀
            String ext = FileUtil.getExtension(images.getOriginalFilename());
            //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
            String pathDir = "file" + File.separator + "helpType" + File.separator;
            //构建文件名称
            String fileName = UUIDUtil.getUUID32() + "." + ext;
            _imagePath = "file/helpType/";
            _fileName = fileName;

            //生成文件保存目录
            FileUtil.createFolder(pathDir);

            //添加文件锁
            try {
                fileComponent.addLock("file" + File.separator + "helpType" + File.separator + "lock" + File.separator, fileName);
                //保存文件
                fileComponent.writeFile(pathDir, fileName, images.getBytes());
            } catch (IOException e) {
                if (logger.isErrorEnabled()) {
                    logger.error("上传图片错误", e);
                }
                throw new BusinessException(Map.of("images", "上传图片错误"));
            }
        }
        return new FullPath(_imagePath, _fileName);
    }
}