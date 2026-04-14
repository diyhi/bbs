package cms.service.topic.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.setting.SettingComponent;
import cms.component.topic.CommentCacheManager;
import cms.component.topic.CommentComponent;
import cms.component.topic.TagComponent;
import cms.component.topic.TopicComponent;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.CorrectionResult;
import cms.dto.HtmlProcessingResult;
import cms.dto.topic.CommentRequest;
import cms.dto.topic.ReplyRequest;
import cms.model.message.Remind;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.topic.Comment;
import cms.model.topic.Quote;
import cms.model.topic.Reply;
import cms.model.user.User;
import cms.repository.message.RemindRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserRepository;
import cms.service.topic.CommentService;
import cms.utils.FileUtil;
import cms.utils.HtmlEscape;
import cms.utils.IpAddress;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务
 */
@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LogManager.getLogger(CommentServiceImpl.class);


    @Resource
    CommentRepository commentRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    TagComponent tagComponent;
    @Resource
    UserCacheManager userCacheManager;
    @Resource
    CommentCacheManager commentCacheManager;
    @Resource
    UserRepository userRepository;
    @Resource
    TopicComponent topicComponent;
    @Resource
    SettingComponent settingComponent;
    @Resource
    TopicRepository topicRepository;
    @Resource
    CommentComponent commentComponent;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    RemindRepository remindRepository;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;

    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {
    }
    //允许文件格式
    private record AllowFormat(List<String> formatList, long fileSize) {
    }

    /**
     * 添加评论
     * @param commentRequest 评论表单
     * @param request        请求信息
     */
    public void addComment(CommentRequest commentRequest, HttpServletRequest request) {
        if(commentRequest.getTopicId() == null || commentRequest.getTopicId() <=0L){
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }

        // 处理内容过滤和文件路径
        CommentServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, commentRequest.getContent(), commentRequest.getIsMarkdown(), commentRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();


        //不含标签内容
        String text = textFilterComponent.filterText(htmlFilterResult.content);

        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();


        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }

        Comment comment = new Comment();
        comment.setTopicId(commentRequest.getTopicId());
        comment.setContent(value);
        comment.setIsMarkdown(commentRequest.getIsMarkdown() == null ? false:commentRequest.getIsMarkdown());
        comment.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        comment.setIsStaff(true);
        comment.setUserName(username);
        comment.setIp(IpAddress.getClientIpAddress(request));
        comment.setStatus(20);
        //保存评论
        commentRepository.saveComment(comment);

        //修改话题最后回复时间
        topicRepository.updateTopicReplyTime(commentRequest.getTopicId(),LocalDateTime.now());


        //上传文件编号
        String fileNumber = "a"+userId;

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }

    }

    /**
     * 获取修改评论界面信息
     * @param commentId 评论Id
     * @return
     */
    public Map<String, Object> getEditCommentViewModel(Long commentId){
        if(commentId == null || commentId <=0L){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentId);
        if(comment == null){
            throw new BusinessException(Map.of("commentId", "评论不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();


        returnValue.put("availableTag",commentComponent.availableTag());

        if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
            //处理富文本路径
            comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
        }


        if(comment.getQuote() != null && !comment.getQuote().trim().isEmpty()){
            //引用
            List<Quote> customQuoteList = jsonComponent.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
            comment.setQuoteList(customQuoteList);
        }
        returnValue.put("comment", comment);
        String username = "";
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        returnValue.put("supportEditor", systemSetting.getSupportEditor());

        return returnValue;
    }

    /**
     * 修改评论
     * @param commentRequest 评论表单
     * @param request        请求信息
     */
    public void editComment(CommentRequest commentRequest, HttpServletRequest request) {
        if(commentRequest.getStatus() == null){
            throw new BusinessException(Map.of("status", "状态参数不能为空"));
        }
        if(commentRequest.getCommentId() == null || commentRequest.getCommentId() <=0L){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentRequest.getCommentId());
        if(comment == null){
            throw new BusinessException(Map.of("commentId", "评论不存在"));
        }
        Integer old_status = comment.getStatus();
        String old_content = comment.getContent();

        // 处理内容过滤和文件路径
        CommentServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, commentRequest.getContent(), commentRequest.getIsMarkdown(), commentRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

        //不含标签内容
        String text = textFilterComponent.filterText(htmlFilterResult.content);

        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();


        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }

        comment.setStatus(commentRequest.getStatus());

        //下级引用Id组
        String lowerQuoteIdGroup = ","+comment.getId()+comment.getQuoteIdGroup();

        String username = comment.getUserName();//用户名称

        //修改评论
        int i = commentRepository.updateComment(comment.getId(),value,commentRequest.getIsMarkdown() == null ? false:commentRequest.getIsMarkdown(),htmlFilterResult.formatterMarkdown,commentRequest.getStatus(),LocalDateTime.now(),lowerQuoteIdGroup,username);


        if(i >0 && !old_status.equals(commentRequest.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
            if(user != null){
                //修改评论状态
                userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),comment.getStatus());
            }

        }


        //删除缓存
        commentCacheManager.delete_cache_findByCommentId(comment.getId());

        //上传文件编号
        String fileNumber = topicComponent.generateFileNumber(comment.getUserName(), comment.getIsStaff());


        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }

        //旧图片名称
        List<String> old_ImageName = textFilterComponent.readImageName(comment.getContent(),"comment");
        if(old_ImageName != null && old_ImageName.size() >0){
            Iterator<String> iter = old_ImageName.iterator();
            while (iter.hasNext()) {
                String imageName = iter.next();//含有路径
                String old_name = FileUtil.getName(imageName);

                for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                    String new_name = FileUtil.getName(new_imageName);
                    if(old_name.equals(new_name)){
                        iter.remove();
                        break;
                    }
                }
            }
            if(old_ImageName.size() >0){
                for(String imagePath : old_ImageName){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
                        continue;
                    }
                    //替换路径中的..号
                    imagePath = FileUtil.toRelativePath(imagePath);
                    //替换路径分割符
                    imagePath = Strings.CS.replace(imagePath, "/", File.separator);

                    Boolean state = fileComponent.deleteFile(imagePath);
                    if(state != null && !state){
                        //替换指定的字符，只替换第一次出现的
                        imagePath = Strings.CS.replaceOnce(imagePath, "file"+File.separator+"comment"+File.separator, "");
                        imagePath = Strings.CS.replace(imagePath, File.separator, "_");//替换所有出现过的字符

                        //创建删除失败文件
                        fileComponent.failedStateFile("file"+File.separator+"comment"+File.separator+"lock"+File.separator+imagePath);

                    }
                }

            }
        }



    }

    /**
     * 删除评论
     * @param commentId 评论Id集合
     */
    public void deleteComment(Long[] commentId){
        if(commentId == null || commentId.length ==0){
            throw new BusinessException(Map.of("commentId", "请选择评论"));
        }

        List<Long> commentIdList = Arrays.stream(commentId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (commentIdList.isEmpty()) {
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }

        List<Comment> commentList = commentRepository.findByCommentIdList(commentIdList);
        if(commentList == null || commentList.isEmpty()) {
            throw new BusinessException(Map.of("commentId", "评论不存在"));
        }

        for(Comment comment : commentList) {
            if (comment.getStatus() < 100) {//标记删除
                Integer constant = 100000;
                int i = commentRepository.markDeleteComment(comment.getId(), constant);

                if (i > 0) {
                    User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
                    if (user != null) {
                        //修改评论状态
                        userRepository.updateUserDynamicCommentStatus(user.getId(), comment.getUserName(), comment.getTopicId(), comment.getId(), comment.getStatus() + constant);
                    }
                    //删除缓存
                    commentCacheManager.delete_cache_findByCommentId(comment.getId());
                }

            } else {//物理删除
                int i = commentRepository.deleteComment(comment.getTopicId(), comment.getId());
                if (i > 0) {
                    //根据评论Id删除用户动态(评论下的回复也同时删除)
                    userRepository.deleteUserDynamicByCommentId(comment.getTopicId(), comment.getId());


                    //删除缓存
                    commentCacheManager.delete_cache_findByCommentId(comment.getId());

                    String fileNumber = topicComponent.generateFileNumber(comment.getUserName(), comment.getIsStaff());

                    //删除图片
                    List<String> imageNameList = textFilterComponent.readImageName(comment.getContent(), "comment");
                    if (imageNameList != null && imageNameList.size() > 0) {
                        for (String imagePath : imageNameList) {
                            //如果验证不是当前用户上传的文件，则不删除锁
                            if (!topicComponent.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)) {
                                continue;
                            }


                            //替换路径中的..号
                            imagePath = FileUtil.toRelativePath(imagePath);
                            //替换路径分割符
                            imagePath = Strings.CS.replace(imagePath, "/", File.separator);

                            Boolean state = fileComponent.deleteFile(imagePath);

                            if (state != null && !state) {
                                //替换指定的字符，只替换第一次出现的
                                imagePath = Strings.CS.replaceOnce(imagePath, "file" + File.separator + "comment" + File.separator, "");
                                imagePath = Strings.CS.replace(imagePath, File.separator, "_");//替换所有出现过的字符

                                //创建删除失败文件
                                fileComponent.failedStateFile("file" + File.separator + "comment" + File.separator + "lock" + File.separator + imagePath);

                            }
                        }
                    }
                }

            }
        }

    }


    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param topicId 话题Id
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir,Long topicId, String userName,Boolean isStaff, String fileName, String fileServerAddress, MultipartFile file) {

        String number = topicComponent.generateFileNumber(userName, isStaff);

        Map<String,Object> returnValue = new HashMap<String,Object>();
        if (dir == null || number == null || number.trim().isEmpty() || topicId == null || topicId<0L) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }

        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, topicId, number, returnValue);
        } else { // 本地系统
            return handleLocalUpload(dir, file, topicId, number, fileServerAddress, returnValue);
        }
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param topicId 话题Id
     * @param number 上传文件编号
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, Long topicId, String number, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }
        CommentServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
        if (allowFormat.formatList == null || allowFormat.formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),allowFormat.formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,topicId +"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/comment/"+topicId+"/"+newFileName,allowFormat.fileSize);//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }

    /**
     * 根据文件类型获取允许上传的格式列表。
     */
    private CommentServiceImpl.AllowFormat getAllowFormats(String dir,Map<String, Object> result) {
        List<String> formatList = new ArrayList<>();
        long fileSize = 0;
        switch (dir) {
            case "image":
                EditorTag editorSiteObject = settingComponent.readEditorTag();
                if(editorSiteObject != null && editorSiteObject.isImage()){//允许上传图片
                    formatList = editorSiteObject.getImageFormat();
                    fileSize = editorSiteObject.getImageSize();;
                }else{
                    result.put("error", 1);
                    result.put("message", "不允许上传图片");
                }
                break;
            default:
                result.put("error", 1);
                result.put("message", "缺少dir参数");
        }
        return new CommentServiceImpl.AllowFormat(formatList,fileSize);
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param topicId 话题Id
     * @param number  上传文件编号
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, Long topicId, String number, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        CommentServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
        if (allowFormat.formatList == null || allowFormat.formatList.isEmpty()) {
            return result;
        }
        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),allowFormat.formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }

        if(file.getSize()/1024 > allowFormat.fileSize){
            result.put("error", 1);
            result.put("message", "文件大小超出"+allowFormat.fileSize+"K");
            return result;
        }

        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
        String pathDir = "file"+File.separator+"comment"+File.separator + topicId +File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"comment"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,topicId +"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/comment/"+topicId+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }

    /**
     * 恢复评论
     * @param commentId 评论Id
     */
    public void recoveryComment(Long commentId){
        if(commentId == null || commentId <=0){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentId);
        if(comment == null || comment.getStatus() <=100){
            throw new BusinessException(Map.of("commentId", "评论不存在或未标记删除"));
        }
        int originalState = this.parseInitialValue(comment.getStatus());
        int i = commentRepository.updateCommentStatus(commentId, originalState);
        if(i >0){
            User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
            if(user != null){
                //修改评论状态
                userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),originalState);
            }
        }

        commentCacheManager.delete_cache_findByCommentId(commentId);
    }


    /**
     * 获取引用评论界面信息
     * @param commentId 评论Id
     * @return
     */
    public Map<String, Object> getQuoteCommentViewModel(Long commentId){
        if(commentId == null || commentId <=0){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentId);
        if(comment == null){
            throw new BusinessException(Map.of("commentId", "评论不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(comment.getContent() != null && !comment.getContent().trim().isEmpty()){
            //处理富文本路径
            comment.setContent(fileComponent.processRichTextFilePath(comment.getContent(),"comment"));
        }
        if(comment.getQuote() != null && !comment.getQuote().trim().isEmpty()){
            //引用
            List<Quote> customQuoteList = jsonComponent.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
            comment.setQuoteList(customQuoteList);
        }
        returnValue.put("comment", comment);
        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 添加引用评论
     * @param commentRequest 评论表单
     * @param request        请求信息
     * @return
     */
    public void addQuote(CommentRequest commentRequest, HttpServletRequest request){
        if(commentRequest.getCommentId() == null || commentRequest.getCommentId() <=0){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(commentRequest.getCommentId());
        if(comment == null){
            throw new BusinessException(Map.of("commentId", "评论不存在"));
        }

        // 处理内容过滤和文件路径
        CommentServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, commentRequest.getContent(), commentRequest.getIsMarkdown(), commentRequest.getMarkdownContent());

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }

        String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();


        //不含标签内容
        String text = textFilterComponent.filterText(htmlFilterResult.content);

        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();


        if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }

        comment.setStatus(20);

        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }
        //旧引用
        List<Quote> old_quoteList = new ArrayList<Quote>();
        //旧引用Id组
        String old_quoteId = ","+comment.getId()+comment.getQuoteIdGroup();
        if(comment.getQuote() != null && !comment.getQuote().trim().isEmpty()){
            //旧引用
            old_quoteList = jsonComponent.toGenericObject(comment.getQuote(), new TypeReference< List<Quote> >(){});
        }


        //自定义引用
        Quote quote = new Quote();
        quote.setCommentId(comment.getId());
        quote.setIsStaff(comment.getIsStaff());
        quote.setUserName(comment.getUserName());
        quote.setContent(textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(comment.getContent())));
        old_quoteList.add(quote);

        //自定义评论
        Comment newComment = new Comment();
        newComment.setTopicId(comment.getTopicId());
        newComment.setContent(value);
        newComment.setIsMarkdown(commentRequest.getIsMarkdown() == null ? false:commentRequest.getIsMarkdown());
        newComment.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        newComment.setIsStaff(true);
        newComment.setQuoteIdGroup(old_quoteId);
        newComment.setUserName(username);
        newComment.setIp(IpAddress.getClientIpAddress(request));
        newComment.setQuote(jsonComponent.toJSONString(old_quoteList));
        newComment.setStatus(20);
        //保存评论
        commentRepository.saveComment(newComment);

        //修改话题最后回复时间
        topicRepository.updateTopicReplyTime(comment.getTopicId(), LocalDateTime.now());

        //上传文件编号
        String fileNumber = "a"+userId;

        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"comment"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }
    }


    /**
     * 审核评论
     * @param commentId 评论Id
     */
    public void moderateComment(Long commentId){
        if(commentId == null || commentId <=0){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        int i = commentRepository.updateCommentStatus(commentId, 20);

        Comment comment = commentCacheManager.query_cache_findByCommentId(commentId);
        if(i >0 && comment != null){
            User user = userCacheManager.query_cache_findUserByUserName(comment.getUserName());
            if(user != null){
                //修改话题状态
                userRepository.updateUserDynamicCommentStatus(user.getId(),comment.getUserName(),comment.getTopicId(),comment.getId(),20);
            }
        }

        //删除缓存
        commentCacheManager.delete_cache_findByCommentId(commentId);
    }


    /**
     * 获取添加回复界面信息
     * @return
     */
    public Map<String, Object> getAddReplyViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();


        returnValue.put("availableTag",commentComponent.availableTag());

        String username = "";
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        return returnValue;
    }

    /**
     * 添加回复
     * @param replyRequest 回复表单
     * @param request        请求信息
     * @return
     */
    public void addReply(ReplyRequest replyRequest, HttpServletRequest request){
        if(replyRequest.getCommentId() == null || replyRequest.getCommentId() <=0){
            throw new BusinessException(Map.of("commentId", "评论Id不能为空"));
        }
        Comment comment = commentRepository.findByCommentId(replyRequest.getCommentId());
        if(comment == null){
            throw new BusinessException(Map.of("commentId", "评论不存在"));
        }
        Reply friendReply = null;
        if(replyRequest.getFriendReplyId() != null && replyRequest.getFriendReplyId() >0){
            friendReply = commentRepository.findReplyByReplyId(replyRequest.getFriendReplyId());
            if(friendReply != null){
                if(!friendReply.getCommentId().equals(replyRequest.getCommentId())){
                    throw new BusinessException(Map.of("friendReplyId", "对方回复Id和评论Id不对应"));
                }
            }else{
                throw new BusinessException(Map.of("friendReplyId", "对方回复Id不存在"));
            }
        }
        if(replyRequest.getContent() == null || replyRequest.getContent().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        String content = textFilterComponent.filterReplyTag(request,replyRequest.getContent().trim(),settingComponent.readEditorTag());
        CorrectionResult correctionResult = textFilterComponent.correctionReplyTag(request,content);
        if(correctionResult.getCorrectedHtml() == null || correctionResult.getCorrectedHtml().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        //不含标签内容
        String text = textFilterComponent.filterText(content);
        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        if(!correctionResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }

        String username = "";//用户名称

        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }

        Reply reply = new Reply();//回复

        reply.setCommentId(comment.getId());
        reply.setIsStaff(true);
        reply.setUserName(username);
        reply.setContent(correctionResult.getCorrectedHtml());
        reply.setTopicId(comment.getTopicId());
        reply.setStatus(20);
        reply.setIp(IpAddress.getClientIpAddress(request));

        if(friendReply != null){
            reply.setFriendReplyId(replyRequest.getFriendReplyId());
            reply.setFriendReplyIdGroup(friendReply.getFriendReplyIdGroup()+replyRequest.getFriendReplyId()+",");
            reply.setIsFriendStaff(friendReply.getIsStaff());
            reply.setFriendUserName(friendReply.getUserName());
        }

        //保存评论
        commentRepository.saveReply(reply);


        //修改话题最后回复时间
        topicRepository.updateTopicReplyTime(comment.getTopicId(),LocalDateTime.now());
    }

    /**
     * 获取修改回复界面信息
     * @param replyId 回复Id
     * @return
     */
    public Map<String, Object> getEditReplyViewModel(Long replyId){
        if(replyId == null || replyId <=0){
            throw new BusinessException(Map.of("replyId", "回复Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Reply reply = commentRepository.findReplyByReplyId(replyId);
        if(reply != null){
            if(reply.getIp() != null && !reply.getIp().trim().isEmpty()){

                reply.setIpAddress(IpAddress.queryAddress(reply.getIp()));
            }
            returnValue.put("reply", reply);
        }
        returnValue.put("availableTag",commentComponent.availableTag());

        String username = "";
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        return returnValue;
    }

    /**
     * 修改回复
     * @param replyRequest 回复表单
     * @param request        请求信息
     * @return
     */
    public void editReply(ReplyRequest replyRequest, HttpServletRequest request){
        if(replyRequest.getStatus() == null){
            throw new BusinessException(Map.of("status", "回复状态不能为空"));
        }
        if(replyRequest.getReplyId() == null || replyRequest.getReplyId() <=0){
            throw new BusinessException(Map.of("replyId", "回复Id不能为空"));
        }
        Reply reply = commentRepository.findReplyByReplyId(replyRequest.getReplyId());
        if(reply == null){
            throw new BusinessException(Map.of("replyId", "回复不存在"));
        }

        Integer old_status = reply.getStatus();
        String old_content = reply.getContent();
        reply.setStatus(replyRequest.getStatus());

        String content = textFilterComponent.filterReplyTag(request,replyRequest.getContent().trim(),settingComponent.readEditorTag());
        CorrectionResult correctionResult = textFilterComponent.correctionReplyTag(request,content);
        if(correctionResult.getCorrectedHtml() == null || correctionResult.getCorrectedHtml().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        //不含标签内容
        String text = textFilterComponent.filterText(content);
        //清除空格&nbsp;
        String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

        if(!correctionResult.isHasImage()
                && (text.trim().isEmpty() || trimSpace.isEmpty())){
            throw new BusinessException(Map.of("content", "过滤标签内容后不能为空"));
        }
        String username = reply.getUserName();//用户名称
        //修改回复
        int i = commentRepository.updateReply(replyRequest.getReplyId(),correctionResult.getCorrectedHtml(),username,replyRequest.getStatus(),LocalDateTime.now());

        if(i >0 && !old_status.equals(replyRequest.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
            if(user != null){
                //修改回复状态
                userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus());
            }

        }


        //删除缓存
        commentCacheManager.delete_cache_findReplyByReplyId(replyRequest.getReplyId());
    }

    /**
     * 删除回复
     * @param replyId 回复Id集合
     */
    public void deleteReply(Long[] replyId){
        if(replyId == null || replyId.length ==0){
            throw new BusinessException(Map.of("replyId", "请选择回复"));
        }

        List<Long> replyIdList = Arrays.stream(replyId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (replyIdList.isEmpty()) {
            throw new BusinessException(Map.of("replyId", "回复Id不能为空"));
        }

        List<Reply> replyList = commentRepository.findByReplyIdList(replyIdList);
        if(replyList == null || replyList.isEmpty()) {
            throw new BusinessException(Map.of("replyId", "回复不存在"));
        }

        for(Reply reply : replyList){
            if(reply.getStatus() <100){//标记删除
                Integer constant = 100000;
                int i = commentRepository.markDeleteReply(reply.getId(),constant);

                if(i >0){
                    User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
                    if(user != null){
                        //修改回复状态
                        userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),reply.getStatus()+constant);
                    }
                    //删除缓存
                    commentCacheManager.delete_cache_findReplyByReplyId(reply.getId());
                }
            }else{//物理删除
                int i = commentRepository.deleteReply(reply.getId());
                if(i >0){
                    User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
                    if(user != null){
                        userRepository.deleteUserDynamicByReplyId(user.getId(),reply.getTopicId(),reply.getCommentId(),reply.getId());
                    }
                }
                //删除缓存
                commentCacheManager.delete_cache_findReplyByReplyId(reply.getId());
            }

        }

    }

    /**
     * 恢复回复
     * @param replyId 回复Id
     */
    public void recoveryReply(Long replyId){
        if(replyId == null || replyId <=0){
            throw new BusinessException(Map.of("replyId", "回复Id不能为空"));
        }
        Reply reply = commentRepository.findReplyByReplyId(replyId);
        if(reply == null || reply.getStatus() <=100){
            throw new BusinessException(Map.of("replyId", "回复不存在或未标记删除"));
        }
        int originalState = this.parseInitialValue(reply.getStatus());
        int i = commentRepository.updateReplyStatus(replyId, originalState);

        User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
        if(i >0 && user != null){
            //修改回复状态
            userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),originalState);
        }
        //删除缓存
        commentCacheManager.delete_cache_findReplyByReplyId(replyId);
    }

    /**
     * 审核回复
     * @param replyId 回复Id
     */
    public void moderateReply(Long replyId){
        if(replyId == null || replyId <=0){
            throw new BusinessException(Map.of("replyId", "回复Id不能为空"));
        }
        int i = commentRepository.updateReplyStatus(replyId, 20);

        Reply reply = commentCacheManager.query_cache_findReplyByReplyId(replyId);
        if(reply != null){
            User user = userCacheManager.query_cache_findUserByUserName(reply.getUserName());
            if(i >0 && user != null){
                //修改回复状态
                userRepository.updateUserDynamicReplyStatus(user.getId(),reply.getUserName(),reply.getTopicId(),reply.getCommentId(),reply.getId(),20);
            }
        }

        //删除缓存
        commentCacheManager.delete_cache_findReplyByReplyId(replyId);
    }


    /**
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @return HtmlFilterResult Html过滤结果
     */
    private CommentServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content,settingComponent.readEditorTag());
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"comment",settingComponent.readEditorTag());
        return new CommentServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);

    }

    /**
     * 解析初始值
     * @param status 状态
     * @return
     */
    private int parseInitialValue(Integer status){
        int tens  = status%100/10;//十位%100/10
        int units  = status%10;//个位直接%10

        return Integer.parseInt(tens+""+units);
    }
}