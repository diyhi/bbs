package cms.service.question.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.question.QuestionTagComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.question.QuestionTag;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.service.question.QuestionTagService;
import cms.utils.FileUtil;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 问答标签服务
 */
@Service
public class QuestionTagServiceImpl implements QuestionTagService {
    private static final Logger logger = LogManager.getLogger(QuestionTagServiceImpl.class);


    @Resource
    QuestionTagRepository questionTagRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    QuestionTagComponent questionTagComponent;

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
    public PageView<QuestionTag> getTagList(Long parentId, String fileServerAddress, int page) {
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();


        //如果所属父类有值
        if (parentId != null && parentId > 0) {
            jpql.append(" and o.parentId=?" + (params.size() + 1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
            params.add(parentId);//设置o.parentId=?2参数
        } else {//如果没有父类
            //	jpql.append(" and o.parent is null");
            jpql.append(" and o.parentId=?" + (params.size() + 1));
            params.add(0L);
        }

        PageView<QuestionTag> pageView = new PageView<QuestionTag>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);
        //当前页
        int firstindex = (page - 1) * pageView.getMaxresult();
        ;
        //排序
        LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();

        orderby.put("sort", "desc");//根据sort字段降序排序
        orderby.put("id", "desc");

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<QuestionTag> qr = questionTagRepository.getScrollData(QuestionTag.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(), orderby);


        pageView.setQueryResult(qr);

        if (qr.getResultlist() != null && qr.getResultlist().size() > 0) {
            for (QuestionTag tag : qr.getResultlist()) {
                if (tag.getImage() != null && !tag.getImage().trim().isEmpty()) {
                    tag.setImage(fileServerAddress + tag.getImage());
                }

            }
        }
        return pageView;
    }


    /**
     * 获取标签导航
     *
     * @param parentId 父标签
     * @return
     */
    public Map<Long, String> getTabNavigation(Long parentId) {
        Map<Long, String> navigation = new LinkedHashMap<Long, String>();
        if (parentId == null || parentId <= 0L) {
            return navigation;
        }
        QuestionTag t = questionTagRepository.findById(parentId);
        if (t != null) {
            List<QuestionTag> parentTagList = questionTagRepository.findAllParentById(t);
            for (QuestionTag p : parentTagList) {
                navigation.put(p.getId(), p.getName());
            }
            navigation.put(t.getId(), t.getName());
        }
        return navigation;
    }

    /**
     * 获取父标签
     *
     * @param parentId 父标签Id
     * @return
     */
    public QuestionTag getParentTag(Long parentId) {
        return questionTagRepository.findById(parentId);
    }

    /**
     * 添加标签
     *
     * @param questionTagForm           标签表单
     * @param parentId                  父标签
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath                 图片路径 (已过滤域名)
     * @param images                    图片
     */
    public void addTag(QuestionTag questionTagForm, Long parentId, Map<String, String> multiLanguageExtensionMap, String imagePath, MultipartFile images) {
        String parentIdGroup = ",0,";
        if (parentId != null && parentId > 0L) {
            //取得父对象
            QuestionTag parentTag = questionTagRepository.findById(parentId);
            if (parentTag == null) {
                throw new BusinessException(Map.of("questionTag", "父标签不存在"));
            }
            //根据标签查询所有父类标签
            List<QuestionTag> allParentTag = questionTagRepository.findAllParentById(parentTag);
            if (allParentTag.size() >= 1) {
                throw new BusinessException(Map.of("questionTag", "标签已达到最大层数,添加失败"));
            }
            parentIdGroup = parentTag.getParentIdGroup() + parentId + ",";
        } else {
            parentId = 0L;
        }

        FullPath fullPath = handleFileUpload(imagePath, images);


        QuestionTag tag = new QuestionTag();
        tag.setId(questionTagComponent.nextNumber());
        tag.setName(questionTagForm.getName());
        tag.setImage(fullPath.imagePath + fullPath.fileName);
        tag.setParentId(parentId);
        tag.setParentIdGroup(parentIdGroup);

        tag.setSort(questionTagForm.getSort());
        tag.setMultiLanguageExtension(jsonComponent.toJSONString(multiLanguageExtensionMap));
        questionTagRepository.saveQuestionTag(tag);

        //删除图片锁
        if (!fullPath.imagePath.trim().isEmpty()) {
            fileComponent.deleteLock("file" + File.separator + "questionTag" + File.separator + "lock" + File.separator, fullPath.fileName);
        }
    }


    /**
     * 获取修改标签界面信息
     *
     * @param tagId             标签Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String, Object> getEditTagViewModel(Long tagId, String fileServerAddress) {
        if (tagId == null || tagId < 0L) {
            throw new BusinessException(Map.of("questionTag", "标签Id不能为空"));
        }
        QuestionTag tag = questionTagRepository.findById(tagId);
        if (tag == null) {
            throw new BusinessException(Map.of("questionTag", "标签不存在"));
        }
        Map<String, Object> returnValue = new HashMap<String, Object>();

        if (tag.getMultiLanguageExtension() != null && !tag.getMultiLanguageExtension().trim().isEmpty()) {
            Map<String, String> multiLanguageExtensionMap = jsonComponent.toObject(tag.getMultiLanguageExtension(), HashMap.class);
            if (multiLanguageExtensionMap != null && multiLanguageExtensionMap.size() > 0) {
                tag.setMultiLanguageExtensionMap(multiLanguageExtensionMap);
            }
        }

        if (tag.getParentId() != null && tag.getParentId() > 0L) {
            QuestionTag parentTag = questionTagRepository.findById(tag.getParentId());
            if (parentTag != null) {
                returnValue.put("parentTag", parentTag);
            }
        }

        Map<Long, String> navigation = new LinkedHashMap<Long, String>();
        List<QuestionTag> parentTagList = questionTagRepository.findAllParentById(tag);
        for (QuestionTag p : parentTagList) {
            navigation.put(p.getId(), p.getName());
        }
        if (!parentTagList.isEmpty()) {
            returnValue.put("parentTag", parentTagList.getFirst());
        }
        returnValue.put("navigation", navigation);//分类导航


        if (tag.getImage() != null && !tag.getImage().isEmpty()) {
            returnValue.put("imagePath", fileServerAddress + tag.getImage());
        }
        returnValue.put("tag", tag);
        return returnValue;
    }

    /**
     * 修改标签
     *
     * @param questionTagForm           标签表单
     * @param multiLanguageExtensionMap 多语言扩展  key:字段-语言（例如：name-en_US） value:内容
     * @param imagePath                 图片路径 (已过滤域名)
     * @param images                    图片
     */
    public void editTag(QuestionTag questionTagForm, Map<String, String> multiLanguageExtensionMap, String imagePath, MultipartFile images) {
        if (questionTagForm.getId() == null || questionTagForm.getId() < 0L) {
            throw new BusinessException(Map.of("questionTag", "标签Id不能为空"));
        }
        QuestionTag tag = questionTagRepository.findById(questionTagForm.getId());
        if (tag == null) {
            throw new BusinessException(Map.of("tag", "标签不存在"));
        }
        FullPath fullPath = handleFileUpload(imagePath, images);

        QuestionTag new_tag = new QuestionTag();
        new_tag.setId(questionTagForm.getId());
        new_tag.setImage(fullPath.imagePath + fullPath.fileName);
        new_tag.setName(questionTagForm.getName());
        new_tag.setSort(questionTagForm.getSort());
        new_tag.setMultiLanguageExtension(jsonComponent.toJSONString(multiLanguageExtensionMap));
        questionTagRepository.updateQuestionTag(new_tag);

        if (tag.getImage() != null && !tag.getImage().trim().isEmpty()) {
            if (!(fullPath.imagePath + fullPath.fileName).equals(tag.getImage())) {//如果图片有变化
                //删除旧图片
                //替换路径中的..号
                String oldPathFile = FileUtil.toRelativePath(tag.getImage());
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
            }
        }

        //删除图片锁
        if (fullPath.imagePath != null && !fullPath.imagePath.trim().isEmpty()) {
            fileComponent.deleteLock("file" + File.separator + "questionTag" + File.separator + "lock" + File.separator, fullPath.fileName);
        }
    }

    /**
     * 删除标签
     *
     * @param questionTagId 标签Id
     */
    public void deleteTag(Long questionTagId) {
        if (questionTagId == null || questionTagId < 0L) {
            throw new BusinessException(Map.of("tag", "标签Id不能为空"));
        }
        QuestionTag tag = questionTagRepository.findById(questionTagId);
        if (tag == null) {
            throw new BusinessException(Map.of("tag", "标签不存在"));
        }

        //根据标签Id查询子标签(下一节点)
        List<QuestionTag> childTagList = questionTagRepository.findChildTagById(questionTagId);

        List<Long> tagIdList = new ArrayList<Long>();
        tagIdList.add(tag.getId());
        if (childTagList != null && childTagList.size() > 0) {
            for (QuestionTag t : childTagList) {
                tagIdList.add(t.getId());
            }
        }

        int i = questionTagRepository.deleteQuestionTag(tag);
        if (i > 0) {
            if (tag.getImage() != null && !tag.getImage().trim().isEmpty()) {

                //删除旧图片
                //替换路径中的..号
                String oldPathFile = FileUtil.toRelativePath(tag.getImage());
                //删除旧文件
                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));

            }

            if (childTagList != null && childTagList.size() > 0) {

                for (QuestionTag t : childTagList) {
                    if (t.getImage() != null && !t.getImage().trim().isEmpty()) {

                        //删除旧图片
                        //替换路径中的..号
                        String oldPathFile = FileUtil.toRelativePath(t.getImage());
                        //删除旧文件
                        fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));

                    }
                }
            }
        }
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

                ///旧路径必须为file/questionTag/开头
                if (imagePath.substring(0, 17).equals("file/questionTag/")) {
                    //新路径名称
                    String newPathName = "file/questionTag/";

                    //如果新旧路径不一致
                    if (!newPathName.equals(pathName)) {

                        //复制文件到新路径
                        try {
                            fileComponent.copyFile(FileUtil.toRelativePath(imagePath), newPathName);
                            //新建文件锁到新路径
                            //添加文件锁
                            fileComponent.addLock("file" + File.separator + "questionTag" + File.separator + "lock" + File.separator, fileName);
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("上传图片错误", e);
                            }
                            throw new BusinessException(Map.of("images", "上传图片错误"));
                        }
                    }
                    _imagePath = "file/questionTag/";
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
            String pathDir = "file" + File.separator + "questionTag" + File.separator;
            //构建文件名称
            String fileName = UUIDUtil.getUUID32() + "." + ext;
            _imagePath = "file/questionTag/";
            _fileName = fileName;

            //生成文件保存目录
            FileUtil.createFolder(pathDir);

            //添加文件锁
            try {
                fileComponent.addLock("file" + File.separator + "questionTag" + File.separator + "lock" + File.separator, fileName);
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


    /**
     * 获取所有标签
     *
     * @return
     */
    public List<QuestionTag> getAllTag() {
        List<QuestionTag> questionTagList = questionTagRepository.findAllQuestionTag();
        List<QuestionTag> new_questionTagList = new ArrayList<QuestionTag>();//排序后标签

        if (questionTagList != null && questionTagList.size() > 0) {

            //组成排序数据
            Iterator<QuestionTag> questionTag_iter = questionTagList.iterator();
            while (questionTag_iter.hasNext()) {
                QuestionTag questionTag = questionTag_iter.next();

                //如果是根节点
                if (questionTag.getParentId().equals(0L)) {

                    new_questionTagList.add(questionTag);
                    questionTag_iter.remove();
                }
            }
            //组合子标签
            for (QuestionTag questionTag : new_questionTagList) {
                questionTagComponent.childQuestionTag(questionTag, questionTagList);
            }
            //排序
            questionTagComponent.questionTagSort(new_questionTagList);


        }
        return new_questionTagList;
    }


}