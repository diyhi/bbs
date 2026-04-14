package cms.service.question.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.payment.PaymentComponent;
import cms.component.question.AnswerCacheManager;
import cms.component.question.AnswerComponent;
import cms.component.question.QuestionCacheManager;
import cms.component.question.QuestionComponent;
import cms.component.setting.SettingComponent;
import cms.component.user.PointComponent;
import cms.component.user.PointLogConfig;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.CorrectionResult;
import cms.dto.HtmlProcessingResult;
import cms.dto.QueryResult;
import cms.dto.question.AnswerReplyRequest;
import cms.dto.question.AnswerRequest;
import cms.model.message.Remind;
import cms.model.payment.PaymentLog;
import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.repository.message.RemindRepository;
import cms.repository.platformShare.PlatformShareRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.question.QuestionTagRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.question.AnswerService;
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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 答案服务
 */
@Service
public class AnswerServiceImpl implements AnswerService {
    private static final Logger logger = LogManager.getLogger(AnswerServiceImpl.class);


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
    AnswerRepository answerRepository;
    @Resource
    UserCacheManager userCacheManager;
    @Resource
    AnswerCacheManager answerCacheManager;
    @Resource
    QuestionCacheManager questionCacheManager;
    @Resource
    PlatformShareRepository platformShareRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource
    PointComponent pointComponent;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    QuestionComponent questionComponent;
    @Resource
    SettingComponent settingComponent;
    @Resource
    QuestionRepository questionRepository;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    RemindRepository remindRepository;
    @Resource
    RemindComponent remindComponent;
    @Resource
    RemindConfig remindConfig;
    @Resource
    AnswerComponent answerComponent;

    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {
    }
    //允许文件格式
    private record AllowFormat(List<String> formatList, long fileSize) {
    }


    /**
     * 添加答案
     * @param answerRequest 答案表单
     * @param request       请求信息
     */
    public void addAnswer(AnswerRequest answerRequest, HttpServletRequest request) {
        if(answerRequest.getQuestionId() == null || answerRequest.getQuestionId() <=0L){
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }

        // 处理内容过滤和文件路径
        AnswerServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, answerRequest.getContent(), answerRequest.getIsMarkdown(), answerRequest.getMarkdownContent());

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

        Answer answer= new Answer();
        answer.setQuestionId(answerRequest.getQuestionId());
        answer.setContent(value);
        answer.setIsMarkdown(answerRequest.getIsMarkdown() == null ? false:answerRequest.getIsMarkdown());
        answer.setMarkdownContent(htmlFilterResult.formatterMarkdown);
        answer.setIsStaff(true);
        answer.setUserName(username);
        answer.setIp(IpAddress.getClientIpAddress(request));
        answer.setStatus(20);
        //保存答案
        answerRepository.saveAnswer(answer);

        //修改问题最后回答时间
        questionRepository.updateQuestionAnswerTime(answerRequest.getQuestionId(),LocalDateTime.now());


        //上传文件编号
        String fileNumber = "a"+userId;

        //删除图片锁
        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除锁
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }
    }

    /**
     * 获取修改答案界面信息
     * @param answerId 答案Id
     * @return
     */
    public Map<String, Object> getEditAnswerViewModel(Long answerId){
        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answerId", "答案Id不能为空"));
        }
        Answer answer = answerRepository.findByAnswerId(answerId);
        if(answer == null){
            throw new BusinessException(Map.of("answerId", "答案不存在"));
        }

        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(answer.getContent() != null && !answer.getContent().trim().isEmpty()){
            //处理富文本路径
            answer.setContent(fileComponent.processRichTextFilePath(answer.getContent(),"answer"));
        }


        returnValue.put("answer", answer);
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
     * 修改答案
     * @param answerRequest 答案表单
     * @param request       请求信息
     */
    public void editAnswer(AnswerRequest answerRequest, HttpServletRequest request) {
        if(answerRequest.getAnswerId() == null || answerRequest.getAnswerId() <=0L){
            throw new BusinessException(Map.of("answerId", "问题Id不能为空"));
        }
        Answer answer = answerRepository.findByAnswerId(answerRequest.getAnswerId());
        if(answer == null){
            throw new BusinessException(Map.of("answerId", "答案不存在"));
        }
        // 处理内容过滤和文件路径
        AnswerServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, answerRequest.getContent(), answerRequest.getIsMarkdown(), answerRequest.getMarkdownContent());

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

        Integer old_status = answer.getStatus();
        String old_content = answer.getContent();
        answer.setStatus(answerRequest.getStatus());

        String username = answer.getUserName();//用户名称

        //修改答案
        int i = answerRepository.updateAnswer(answer.getId(),value,answerRequest.getIsMarkdown() == null ? false:answerRequest.getIsMarkdown(),htmlFilterResult.formatterMarkdown,answerRequest.getStatus(),username);


        if(i >0 && !old_status.equals(answerRequest.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
            if(user != null){
                //修改答案状态
                userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus());
            }

        }


        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answer.getId());
        answerCacheManager.delete_cache_answerCount(answer.getUserName());
        //上传文件编号
        String fileNumber = questionComponent.generateFileNumber(answer.getUserName(), answer.getIsStaff());


        if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
            for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){
                if(imageName != null && !imageName.trim().isEmpty()){
                    //如果验证不是当前用户上传的文件，则不删除
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                        continue;
                    }
                    fileComponent.deleteLock("file"+File.separator+"answer"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));
                }

            }
        }

        //旧图片名称
        List<String> old_ImageName = textFilterComponent.readImageName(answer.getContent(),"answer");
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
                    if(!questionComponent.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
                        continue;
                    }
                    //替换路径中的..号
                    imagePath = FileUtil.toRelativePath(imagePath);
                    //替换路径分割符
                    imagePath = Strings.CS.replace(imagePath, "/", File.separator);

                    Boolean state = fileComponent.deleteFile(imagePath);
                    if(state != null && !state){
                        //替换指定的字符，只替换第一次出现的
                        imagePath = Strings.CS.replaceOnce(imagePath, "file"+File.separator+"answer"+File.separator, "");
                        imagePath = Strings.CS.replace(imagePath, File.separator, "_");//替换所有出现过的字符

                        //创建删除失败文件
                        fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+imagePath);

                    }
                }

            }
        }
    }

    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param questionId 问题Id
     * @param userName 用户名称
     * @param isStaff 是否是员工   true:员工   false:会员
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir,Long questionId, String userName,Boolean isStaff, String fileName, String fileServerAddress, MultipartFile file) {

        String number = questionComponent.generateFileNumber(userName, isStaff);

        Map<String,Object> returnValue = new HashMap<String,Object>();
        if (dir == null || number == null || number.trim().isEmpty() || questionId == null || questionId<0L) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }

        int fileSystem = fileComponent.getFileSystem();
        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, questionId, number, returnValue);
        } else { // 本地系统
            return handleLocalUpload(dir, file, questionId, number, fileServerAddress, returnValue);
        }
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param questionId 问题Id
     * @param number 上传文件编号
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, Long questionId, String number, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }
        AnswerServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
        if (allowFormat.formatList == null || allowFormat.formatList.isEmpty()) {
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),allowFormat.formatList)){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,questionId +"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/answer/"+questionId+"/"+newFileName,allowFormat.fileSize);//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }

    /**
     * 根据文件类型获取允许上传的格式列表。
     */
    private AnswerServiceImpl.AllowFormat getAllowFormats(String dir,Map<String, Object> result) {
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
        return new AnswerServiceImpl.AllowFormat(formatList,fileSize);
    }

    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param questionId 问题Id
     * @param number  上传文件编号
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, Long questionId, String number, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();
        AnswerServiceImpl.AllowFormat allowFormat = getAllowFormats(dir,result);
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
        String pathDir = "file"+File.separator+"answer"+File.separator + questionId +File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"answer"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,questionId +"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/answer/"+questionId+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }

    /**
     * 删除答案
     * @param answerId 答案Id集合
     */
    public void deleteAnswer(Long[] answerId){
        if(answerId == null || answerId.length ==0){
            throw new BusinessException(Map.of("answerId", "请选择答案"));
        }

        List<Long> answerIdList = Arrays.stream(answerId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (answerIdList.isEmpty()) {
            throw new BusinessException(Map.of("answerId", "答案Id不能为空"));
        }

        List<Answer> answerList = answerRepository.findByAnswerIdList(answerIdList);
        if(answerList == null || answerList.isEmpty()) {
            throw new BusinessException(Map.of("answerId", "答案不存在"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }

        for(Answer answer : answerList){
            if(answer.getStatus() <100){//标记删除
                Integer constant = 100000;
                int i = answerRepository.markDeleteAnswer(answer.getId(),constant);

                if(i >0){
                    User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
                    if(user != null){
                        //修改答案状态
                        userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),answer.getStatus()+constant);
                    }
                    //删除缓存
                    answerCacheManager.delete_cache_findByAnswerId(answer.getId());
                }

            }else{//物理删除
                Question question = questionCacheManager.query_cache_findById(answer.getQuestionId());
                if(question == null) {
                    throw new BusinessException(Map.of("question", "问题不存在"));

                }
                LocalDateTime time = LocalDateTime.now();

                User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());

                //取消采纳用户名称
                String cancelAdoptionUserName = null;
                //取消采纳用户退还悬赏积分日志
                Object cancelAdoptionPointLogObject = null;
                //取消采纳用户退还分成金额
                BigDecimal cancelAdoptionUserNameShareAmount = new BigDecimal("0");
                //取消采纳用户退还悬赏金额日志
                Object cancelAdoptionPaymentLogObject = null;

                if(question.getAdoptionAnswerId() >0L){//已悬赏
                    cancelAdoptionUserName = answer.getUserName();

                    if(user != null && !answer.getIsStaff()){
                        if(question.getPoint() != null && question.getPoint() >0L){
                            PointLog reward_pointLog = new PointLog();
                            reward_pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
                            reward_pointLog.setModule(1100);//1100.采纳答案
                            reward_pointLog.setParameterId(answer.getId());//参数Id
                            reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                            reward_pointLog.setOperationUserName(username);//操作用户名称
                            reward_pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出
                            reward_pointLog.setPoint(question.getPoint());//积分
                            reward_pointLog.setUserName(answer.getUserName());//用户名称
                            reward_pointLog.setRemark("");
                            reward_pointLog.setTimes(time);
                            cancelAdoptionPointLogObject = pointComponent.createPointLogObject(reward_pointLog);
                        }
                        if(question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
                            QuestionRewardPlatformShare questionRewardPlatformShare = platformShareRepository.findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(question.getId(),answer.getUserName());
                            if(questionRewardPlatformShare != null){
                                //用户分成金额
                                BigDecimal userNameShareAmount = questionRewardPlatformShare.getTotalAmount().subtract(questionRewardPlatformShare.getShareAmount());
                                cancelAdoptionUserNameShareAmount = userNameShareAmount;

                                PaymentLog reward_paymentLog = new PaymentLog();
                                String paymentRunningNumber = paymentComponent.createRunningNumber(user.getId());
                                reward_paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                                reward_paymentLog.setPaymentModule(100);//支付模块 100.采纳答案
                                reward_paymentLog.setSourceParameterId(String.valueOf(answer.getId()));//参数Id
                                reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                                reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
                                reward_paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
                                reward_paymentLog.setAmount(userNameShareAmount);//金额
                                reward_paymentLog.setInterfaceProduct(0);//接口产品
                                reward_paymentLog.setUserName(answer.getUserName());//用户名称
                                reward_paymentLog.setTimes(time);
                                cancelAdoptionPaymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);

                            }
                        }
                    }
                    //员工回答
                    if(answer.getIsStaff() && question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
                        cancelAdoptionUserNameShareAmount = question.getAmount();

                    }

                }

                try {
                    int i = answerRepository.deleteAnswer(answer.getQuestionId(),answer.getId(),cancelAdoptionUserName,cancelAdoptionPointLogObject,cancelAdoptionUserNameShareAmount,cancelAdoptionPaymentLogObject,question.getPoint());
                    if(i >0){
                        //根据答案Id删除用户动态(答案下的回复也同时删除)
                        userRepository.deleteUserDynamicByAnswerId(answer.getQuestionId(),answer.getId());

                        //删除缓存
                        answerCacheManager.delete_cache_findByAnswerId(answer.getId());
                        answerCacheManager.delete_cache_answerCount(answer.getUserName());
                        if(user != null){
                            userCacheManager.delete_cache_findUserById(user.getId());
                            userCacheManager.delete_cache_findUserByUserName(user.getUserName());
                        }

                        String fileNumber = questionComponent.generateFileNumber(answer.getUserName(), answer.getIsStaff());



                        //删除图片
                        List<String> imageNameList = textFilterComponent.readImageName(answer.getContent(),"answer");
                        if(imageNameList != null && imageNameList.size() >0){
                            for(String imagePath : imageNameList){
                                //如果验证不是当前用户上传的文件，则不删除锁
                                if(!questionComponent.getFileNumber(FileUtil.getBaseName(imagePath.trim())).equals(fileNumber)){
                                    continue;
                                }


                                //替换路径中的..号
                                imagePath = FileUtil.toRelativePath(imagePath);
                                //替换路径分割符
                                imagePath = Strings.CS.replace(imagePath, "/", File.separator);

                                Boolean state = fileComponent.deleteFile(imagePath);

                                if(state != null && !state){
                                    //替换指定的字符，只替换第一次出现的
                                    imagePath = Strings.CS.replaceOnce(imagePath, "file"+File.separator+"answer"+File.separator, "");
                                    imagePath = Strings.CS.replace(imagePath, File.separator, "_");//替换所有出现过的字符

                                    //创建删除失败文件
                                    fileComponent.failedStateFile("file"+File.separator+"answer"+File.separator+"lock"+File.separator+imagePath);

                                }
                            }
                        }

                    }
                } catch (Exception e) {
                    throw new BusinessException(Map.of("answer", e.getMessage()));
                }
            }
        }

    }


    /**
     * 审核答案
     * @param answerId 评论Id
     */
    public void moderateAnswer(Long answerId){
        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answerId", "答案Id不能为空"));
        }
        int i = answerRepository.updateAnswerStatus(answerId, 20);

        Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);
        if(i ==0 || answer == null) {
            throw new BusinessException(Map.of("answerId", "答案不存在或修改答案状态失败"));
        }

        User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
        if(user != null){
            //修改答案状态
            userRepository.updateUserDynamicAnswerStatus(user.getId(),answer.getUserName(),answer.getQuestionId(),answer.getId(),20);
        }
        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answerId);
        answerCacheManager.delete_cache_answerCount(answer.getUserName());

    }
    /**
     * 获取添加答案回复界面信息
     * @return
     */
    public Map<String, Object> getAddAnswerReplyViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        returnValue.put("availableTag",answerComponent.availableTag());

        String username = "";
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        return returnValue;
    }

    /**
     * 添加答案回复
     * @param answerReplyRequest 答案回复表单
     * @param request        请求信息
     * @return
     */
    public void addAnswerReply(AnswerReplyRequest answerReplyRequest, HttpServletRequest request){
        if(answerReplyRequest.getAnswerId() == null || answerReplyRequest.getAnswerId() <=0){
            throw new BusinessException(Map.of("answerId", "答案Id不能为空"));
        }
        Answer answer = answerRepository.findByAnswerId(answerReplyRequest.getAnswerId());
        if(answer == null){
            throw new BusinessException(Map.of("answerId", "答案不存在"));
        }
        AnswerReply friendReply = null;
        if(answerReplyRequest.getFriendReplyId() != null && answerReplyRequest.getFriendReplyId() >0){
            friendReply = answerRepository.findReplyByReplyId(answerReplyRequest.getFriendReplyId());
            if(friendReply != null){
                if(!friendReply.getAnswerId().equals(answerReplyRequest.getAnswerId())){
                    throw new BusinessException(Map.of("friendReplyId", "对方回复Id和答案Id不对应"));
                }
            }else{
                throw new BusinessException(Map.of("friendReplyId", "对方回复Id不存在"));
            }
        }
        if(answerReplyRequest.getContent() == null || answerReplyRequest.getContent().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        String content = textFilterComponent.filterReplyTag(request,answerReplyRequest.getContent().trim(),settingComponent.readAnswerEditorTag());
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


        AnswerReply answerReply = new AnswerReply();//回复
        answerReply.setAnswerId(answer.getId());
        answerReply.setIsStaff(true);
        answerReply.setUserName(username);
        answerReply.setContent(correctionResult.getCorrectedHtml());
        answerReply.setQuestionId(answer.getQuestionId());
        answerReply.setStatus(20);
        answerReply.setIp(IpAddress.getClientIpAddress(request));
        if(friendReply != null){
            answerReply.setFriendReplyId(answerReplyRequest.getFriendReplyId());
            answerReply.setFriendReplyIdGroup(friendReply.getFriendReplyIdGroup()+answerReplyRequest.getFriendReplyId()+",");
            answerReply.setIsFriendStaff(friendReply.getIsStaff());
            answerReply.setFriendUserName(friendReply.getUserName());
        }

        //保存答案回复
        answerRepository.saveReply(answerReply);


        //修改问题最后回答时间
        questionRepository.updateQuestionAnswerTime(answer.getQuestionId(), LocalDateTime.now());
    }

    /**
     * 获取修改答案回复界面信息
     * @param answerReplyId 答案回复Id
     * @return
     */
    public Map<String, Object> getEditAnswerReplyViewModel(Long answerReplyId){
        if(answerReplyId == null || answerReplyId <=0L){
            throw new BusinessException(Map.of("answerReplyId", "回复Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        AnswerReply answerReply = answerRepository.findReplyByReplyId(answerReplyId);
        if(answerReply != null){
            if(answerReply.getIp() != null && !answerReply.getIp().trim().isEmpty()){

                answerReply.setIpAddress(IpAddress.queryAddress(answerReply.getIp()));
            }
            returnValue.put("answerReply", answerReply);
        }
        returnValue.put("availableTag",answerComponent.availableTag());

        String username = "";
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        returnValue.put("userName", username);
        return returnValue;

    }

    /**
     * 修改答案回复
     * @param answerReplyRequest 答案回复表单
     * @param request        请求信息
     * @return
     */
    public void editAnswerReply(AnswerReplyRequest answerReplyRequest, HttpServletRequest request){
        if(answerReplyRequest.getAnswerReplyId() == null || answerReplyRequest.getAnswerReplyId() <=0){
            throw new BusinessException(Map.of("answerReplyId", "回复Id不能为空"));
        }
        AnswerReply answerReply = answerRepository.findReplyByReplyId(answerReplyRequest.getAnswerReplyId());
        if(answerReply == null){
            throw new BusinessException(Map.of("answerReplyId", "回复不存在"));
        }

        Integer old_status = answerReply.getStatus();
        String old_content = answerReply.getContent();
        answerReply.setStatus(answerReplyRequest.getStatus());

        if(answerReplyRequest.getContent() == null || answerReplyRequest.getContent().trim().isEmpty()){
            throw new BusinessException(Map.of("content", "内容不能为空"));
        }
        String content = textFilterComponent.filterReplyTag(request,answerReplyRequest.getContent().trim(),settingComponent.readAnswerEditorTag());
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
        String username = answerReply.getUserName();//用户名称
        //修改回复
        int i = answerRepository.updateReply(answerReplyRequest.getAnswerReplyId(),correctionResult.getCorrectedHtml(),username,answerReplyRequest.getStatus());

        if(i >0 && !old_status.equals(answerReplyRequest.getStatus())){
            User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
            if(user != null){
                //修改答案回复状态
                userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus());
            }

        }


        //删除缓存
        answerCacheManager.delete_cache_findReplyByReplyId(answerReplyRequest.getAnswerReplyId());
    }

    /**
     * 删除答案回复
     * @param answerReplyId 答案回复Id集合
     */
    public void deleteAnswerReply(Long[] answerReplyId){
        if(answerReplyId == null || answerReplyId.length ==0){
            throw new BusinessException(Map.of("answerReplyId", "请选择答案回复"));
        }

        List<Long> answerReplyIdList = Arrays.stream(answerReplyId)
                .filter(Objects::nonNull) // 过滤掉所有 null 元素
                .toList();

        if (answerReplyIdList.isEmpty()) {
            throw new BusinessException(Map.of("answerReplyId", "答案回复Id不能为空"));
        }

        List<AnswerReply> answerReplyList = answerRepository.findByAnswerReplyIdList(answerReplyIdList);
        if(answerReplyList == null || answerReplyList.isEmpty()) {
            throw new BusinessException(Map.of("answerReplyId", "回复不存在"));
        }
        for(AnswerReply answerReply : answerReplyList){
            if(answerReply.getStatus() <100){//标记删除
                Integer constant = 100000;
                int i = answerRepository.markDeleteReply(answerReply.getId(),constant);


                if(i >0){
                    User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                    if(user != null){
                        //修改回复状态
                        userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),answerReply.getStatus()+constant);
                    }
                    //删除缓存
                    answerCacheManager.delete_cache_findReplyByReplyId(answerReply.getId());
                }
            }else{//物理删除
                int i = answerRepository.deleteReply(answerReply .getId());
                if(i >0){
                    User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
                    if(user != null){
                        userRepository.deleteUserDynamicByAnswerReplyId(user.getId(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply .getId());
                    }
                }

                //删除缓存
                answerCacheManager.delete_cache_findReplyByReplyId(answerReply .getId());
            }

        }

    }

    /**
     * 审核答案回复
     * @param answerReplyId 答案回复Id
     */
    public void moderateAnswerReply(Long answerReplyId){
        if(answerReplyId == null || answerReplyId <=0L){
            throw new BusinessException(Map.of("answerReplyId", "回复Id不能为空"));
        }
        int i = answerRepository.updateReplyStatus(answerReplyId, 20);

        AnswerReply answerReply = answerCacheManager.query_cache_findReplyByReplyId(answerReplyId);
        if(answerReply != null){
            User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
            if(i >0 && user != null){
                //修改答案回复状态
                userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),20);
            }
        }

        //删除缓存
        answerCacheManager.delete_cache_findReplyByReplyId(answerReplyId);
    }

    /**
     * 恢复答案回复
     * @param answerReplyId 答案回复Id
     */
    public void recoveryAnswerReply(Long answerReplyId){
        if(answerReplyId == null || answerReplyId <=0L){
            throw new BusinessException(Map.of("replyId", "回复Id不能为空"));
        }
        AnswerReply answerReply = answerRepository.findReplyByReplyId(answerReplyId);
        if(answerReply == null || answerReply.getStatus() <=100){
            throw new BusinessException(Map.of("replyId", "回复不存在或未标记删除"));
        }
        int originalState = this.parseInitialValue(answerReply.getStatus());
        int i = answerRepository.updateReplyStatus(answerReplyId, originalState);

        User user = userCacheManager.query_cache_findUserByUserName(answerReply.getUserName());
        if(i >0 && user != null){
            //修改回复状态
            userRepository.updateUserDynamicAnswerReplyStatus(user.getId(),answerReply.getUserName(),answerReply.getQuestionId(),answerReply.getAnswerId(),answerReply.getId(),originalState);
        }
        //删除缓存
        answerCacheManager.delete_cache_findReplyByReplyId(answerReplyId);
    }

    /**
     * 采纳答案
     * @param answerId 答案Id
     */
    public void adoptionAnswer(Long answerId){
        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answerId", "答案Id不能为空"));
        }
        Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);
        if(answer == null){
            throw new BusinessException(Map.of("answer", "答案不存在"));
        }
        Question question = questionCacheManager.query_cache_findById(answer.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("question", "问题不存在"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }
        LocalDateTime time = LocalDateTime.now();

        User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());
        User old_user = null;
        //取消采纳用户名称
        String cancelAdoptionUserName = null;
        //取消采纳用户退还悬赏积分日志
        Object cancelAdoptionPointLogObject = null;
        //取消采纳用户退还分成金额
        BigDecimal cancelAdoptionUserNameShareAmount = new BigDecimal("0");
        //取消采纳用户退还悬赏金额日志
        Object cancelAdoptionPaymentLogObject = null;

        //是否更改采纳答案
        boolean changeAdoption = false;
        if(question.getAdoptionAnswerId() >0L){
            changeAdoption = true;


            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();
            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

            jpql.append(" o.questionId=?"+ (params.size()+1));
            params.add(question.getId());//设置参数


            orderby.put("adoption", "desc");//采纳答案时间排序   新-->旧
            //根据sort字段降序排序
            QueryResult<Answer> qr = answerRepository.getScrollData(Answer.class,0, 1,jpql.toString(),params.toArray(),orderby);

            List<Answer> answerList = qr.getResultlist();
            if(answerList != null && answerList.size() >0){
                //上一个答案被采纳用户退还积分和金额到问题
                for(Answer old_answer : answerList){
                    old_user = userCacheManager.query_cache_findUserByUserName(old_answer.getUserName());
                    cancelAdoptionUserName = old_answer.getUserName();


                    if(old_user != null && old_answer.getIsStaff() ==false){
                        if(question.getPoint() != null && question.getPoint() >0L){
                            PointLog reward_pointLog = new PointLog();
                            reward_pointLog.setId(pointLogConfig.createPointLogId(old_user.getId()));
                            reward_pointLog.setModule(1100);//1100.采纳答案
                            reward_pointLog.setParameterId(old_answer.getId());//参数Id
                            reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                            reward_pointLog.setOperationUserName(username);//操作用户名称
                            reward_pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出
                            reward_pointLog.setPoint(question.getPoint());//积分
                            reward_pointLog.setUserName(old_answer.getUserName());//用户名称
                            reward_pointLog.setRemark("");
                            reward_pointLog.setTimes(time);
                            cancelAdoptionPointLogObject = pointComponent.createPointLogObject(reward_pointLog);
                        }
                        if(question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
                            QuestionRewardPlatformShare questionRewardPlatformShare = platformShareRepository.findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(question.getId(),old_answer.getUserName());
                            if(questionRewardPlatformShare != null){
                                //用户分成金额
                                BigDecimal userNameShareAmount = questionRewardPlatformShare.getTotalAmount().subtract(questionRewardPlatformShare.getShareAmount());
                                cancelAdoptionUserNameShareAmount = userNameShareAmount;

                                PaymentLog reward_paymentLog = new PaymentLog();
                                String paymentRunningNumber = paymentComponent.createRunningNumber(old_user.getId());
                                reward_paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                                reward_paymentLog.setPaymentModule(100);//支付模块 100.采纳答案
                                reward_paymentLog.setSourceParameterId(String.valueOf(old_answer.getId()));//参数Id
                                reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                                reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
                                reward_paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
                                reward_paymentLog.setAmount(userNameShareAmount);//金额
                                reward_paymentLog.setInterfaceProduct(0);//接口产品
                                reward_paymentLog.setUserName(old_answer.getUserName());//用户名称
                                reward_paymentLog.setTimes(time);
                                cancelAdoptionPaymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);

                            }


                        }
                    }

                    //员工回答
                    if(old_answer.getIsStaff() && question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
                        cancelAdoptionUserNameShareAmount = question.getAmount();

                    }
                }
            }
        }

        //回答用户获得积分
        Long point = 0L;


        //用户悬赏积分日志
        Object pointLogObject = null;
        if(user != null && answer.getIsStaff() ==false  && question.getPoint() != null && question.getPoint() >0L){
            point = question.getPoint();

            PointLog reward_pointLog = new PointLog();
            reward_pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
            reward_pointLog.setModule(1100);//1100.采纳答案
            reward_pointLog.setParameterId(answer.getId());//参数Id
            reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
            reward_pointLog.setOperationUserName(username);//操作用户名称
            reward_pointLog.setPointState(1);//积分状态  1:账户存入  2:账户支出
            reward_pointLog.setPoint(question.getPoint());//积分
            reward_pointLog.setUserName(answer.getUserName());//用户名称
            reward_pointLog.setRemark("");
            reward_pointLog.setTimes(time);
            pointLogObject = pointComponent.createPointLogObject(reward_pointLog);
        }

        //用户悬赏金额日志
        Object paymentLogObject = null;
        //平台分成
        QuestionRewardPlatformShare questionRewardPlatformShare = null;
        //回答用户分成金额
        BigDecimal userNameShareAmount = new BigDecimal("0");
        //平台分成金额
        BigDecimal platformShareAmount = new BigDecimal("0");

        if(question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){

            if(answer.getIsStaff() ==false){//会员回答
                if(user != null){
                    Integer questionRewardPlatformShareProportion = settingRepository.findSystemSetting().getQuestionRewardPlatformShareProportion();//平台分成比例

                    if(questionRewardPlatformShareProportion >0){
                        //平台分成金额 = 总金额 * (平台分成比例 /100)
                        platformShareAmount = question.getAmount().multiply(new BigDecimal(String.valueOf(questionRewardPlatformShareProportion)).divide(new BigDecimal("100")));

                        userNameShareAmount = question.getAmount().subtract(platformShareAmount);
                    }else{
                        userNameShareAmount = question.getAmount();
                    }




                    PaymentLog reward_paymentLog = new PaymentLog();
                    String paymentRunningNumber = paymentComponent.createRunningNumber(user.getId());
                    reward_paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                    reward_paymentLog.setPaymentModule(100);//支付模块 100.采纳答案
                    reward_paymentLog.setSourceParameterId(String.valueOf(answer.getId()));//参数Id
                    reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                    reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
                    reward_paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                    reward_paymentLog.setAmount(userNameShareAmount);//金额
                    reward_paymentLog.setInterfaceProduct(0);//接口产品
                    reward_paymentLog.setUserName(answer.getUserName());//用户名称
                    reward_paymentLog.setTimes(time);
                    paymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);

                    if(questionRewardPlatformShareProportion >0){
                        //平台分成
                        questionRewardPlatformShare = new QuestionRewardPlatformShare();
                        questionRewardPlatformShare.setQuestionId(question.getId());//问题Id
                        questionRewardPlatformShare.setStaff(answer.getIsStaff());//分成用户是否为员工
                        questionRewardPlatformShare.setPostUserName(question.getUserName());//提问题的用户名称
                        questionRewardPlatformShare.setAnswerUserName(answer.getUserName());//回答问题的用户名称
                        questionRewardPlatformShare.setPlatformShareProportion(questionRewardPlatformShareProportion);//平台分成比例
                        questionRewardPlatformShare.setAnswerUserShareRunningNumber(paymentRunningNumber);//回答问题的用户分成流水号
                        questionRewardPlatformShare.setTotalAmount(question.getAmount());//总金额
                        questionRewardPlatformShare.setShareAmount(platformShareAmount);//平台分成金额
                        questionRewardPlatformShare.setAdoptionTime(time);//采纳时间
                    }
                }
            }else{//员工回答
                //收益归平台
                platformShareAmount = question.getAmount();

                //平台分成
                questionRewardPlatformShare = new QuestionRewardPlatformShare();
                questionRewardPlatformShare.setQuestionId(question.getId());//问题Id
                questionRewardPlatformShare.setStaff(answer.getIsStaff());//分成用户是否为员工
                questionRewardPlatformShare.setPostUserName(question.getUserName());//提问题的用户名称
                questionRewardPlatformShare.setAnswerUserName(answer.getUserName());//回答问题的用户名称
                questionRewardPlatformShare.setPlatformShareProportion(100);//平台分成比例
                //	questionRewardPlatformShare.setAnswerUserShareRunningNumber();//回答问题的用户分成流水号
                questionRewardPlatformShare.setTotalAmount(question.getAmount());//总金额
                questionRewardPlatformShare.setShareAmount(platformShareAmount);//平台分成金额
                questionRewardPlatformShare.setAdoptionTime(time);//采纳时间
            }

        }




        int i = answerRepository.updateAdoptionAnswer(answer.getQuestionId(), answerId,changeAdoption,cancelAdoptionUserName,cancelAdoptionPointLogObject,cancelAdoptionUserNameShareAmount,cancelAdoptionPaymentLogObject,
                    answer.getUserName(),point,pointLogObject,userNameShareAmount,paymentLogObject,questionRewardPlatformShare);

        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answerId);
        questionCacheManager.delete_cache_findById(answer.getQuestionId());
        answerCacheManager.delete_cache_answerCount(answer.getUserName());
        if(user != null){
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());
        }
        if(old_user != null){
            userCacheManager.delete_cache_findUserById(old_user.getId());
            userCacheManager.delete_cache_findUserByUserName(old_user.getUserName());
        }

    }
    /**
     * 取消采纳答案
     * @param answerId 答案Id
     */
    public void cancelAdoptionAnswer(Long answerId){
        if(answerId == null || answerId <=0L){
            throw new BusinessException(Map.of("answerId", "答案Id不能为空"));
        }
        Answer answer = answerCacheManager.query_cache_findByAnswerId(answerId);
        if(answer == null){
            throw new BusinessException(Map.of("answer", "答案不存在"));
        }
        Question question = questionCacheManager.query_cache_findById(answer.getQuestionId());
        if(question == null){
            throw new BusinessException(Map.of("question", "问题不存在"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            userId =((SysUsers)obj).getUserId();
            username =((SysUsers)obj).getUserAccount();
        }

        LocalDateTime time = LocalDateTime.now();

        User user = userCacheManager.query_cache_findUserByUserName(answer.getUserName());

        //取消采纳用户名称
        String cancelAdoptionUserName = answer.getUserName();
        //取消采纳用户退还悬赏积分日志
        Object cancelAdoptionPointLogObject = null;
        //取消采纳用户退还分成金额
        BigDecimal cancelAdoptionUserNameShareAmount = new BigDecimal("0");
        //取消采纳用户退还悬赏金额日志
        Object cancelAdoptionPaymentLogObject = null;




        if(user != null && !answer.getIsStaff()){
            if(question.getPoint() != null && question.getPoint() >0L){
                PointLog reward_pointLog = new PointLog();
                reward_pointLog.setId(pointLogConfig.createPointLogId(user.getId()));
                reward_pointLog.setModule(1100);//1100.采纳答案
                reward_pointLog.setParameterId(answer.getId());//参数Id
                reward_pointLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                reward_pointLog.setOperationUserName(username);//操作用户名称
                reward_pointLog.setPointState(2);//积分状态  1:账户存入  2:账户支出
                reward_pointLog.setPoint(question.getPoint());//积分
                reward_pointLog.setUserName(answer.getUserName());//用户名称
                reward_pointLog.setRemark("");
                reward_pointLog.setTimes(time);
                cancelAdoptionPointLogObject = pointComponent.createPointLogObject(reward_pointLog);
            }
            if(question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
                QuestionRewardPlatformShare questionRewardPlatformShare = platformShareRepository.findQuestionRewardPlatformShareByQuestionIdAndAnswerUserName(question.getId(),answer.getUserName());
                if(questionRewardPlatformShare != null){
                    //用户分成金额
                    BigDecimal userNameShareAmount = questionRewardPlatformShare.getTotalAmount().subtract(questionRewardPlatformShare.getShareAmount());
                    cancelAdoptionUserNameShareAmount = userNameShareAmount;

                    PaymentLog reward_paymentLog = new PaymentLog();
                    String paymentRunningNumber = paymentComponent.createRunningNumber(user.getId());
                    reward_paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                    reward_paymentLog.setPaymentModule(100);//支付模块 100.采纳答案
                    reward_paymentLog.setSourceParameterId(String.valueOf(answer.getId()));//参数Id
                    reward_paymentLog.setOperationUserType(1);//操作用户类型  0:系统  1: 员工  2:会员
                    reward_paymentLog.setOperationUserName(username);//操作用户名称  0:系统  1: 员工  2:会员
                    reward_paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
                    reward_paymentLog.setAmount(userNameShareAmount);//金额
                    reward_paymentLog.setInterfaceProduct(0);//接口产品
                    reward_paymentLog.setUserName(answer.getUserName());//用户名称
                    reward_paymentLog.setTimes(time);
                    cancelAdoptionPaymentLogObject = paymentComponent.createPaymentLogObject(reward_paymentLog);

                }
            }
        }
        //员工回答
        if(answer.getIsStaff() && question.getAmount() != null && question.getAmount().compareTo(new BigDecimal("0")) >0){
            cancelAdoptionUserNameShareAmount = question.getAmount();

        }



        int i = answerRepository.updateCancelAdoptionAnswer(answer.getQuestionId(),cancelAdoptionUserName,cancelAdoptionPointLogObject,cancelAdoptionUserNameShareAmount,cancelAdoptionPaymentLogObject,question.getPoint());

        //删除缓存
        answerCacheManager.delete_cache_findByAnswerId(answerId);
        questionCacheManager.delete_cache_findById(answer.getQuestionId());
        answerCacheManager.delete_cache_answerCount(answer.getUserName());

        if(user != null){
            userCacheManager.delete_cache_findUserById(user.getId());
            userCacheManager.delete_cache_findUserByUserName(user.getUserName());
        }
    }


    /**
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @return HtmlFilterResult Html过滤结果
     */
    private AnswerServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content,settingComponent.readAnswerEditorTag());
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"answer",settingComponent.readAnswerEditorTag());
        return new AnswerServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);

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