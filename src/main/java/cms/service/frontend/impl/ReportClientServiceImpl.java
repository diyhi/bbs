package cms.service.frontend.impl;

import cms.component.CaptchaComponent;
import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.report.ReportTypeComponent;
import cms.component.setting.SettingCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.frontendModule.ReportDTO;
import cms.dto.topic.ImageInfo;
import cms.dto.user.AccessUser;
import cms.model.question.Answer;
import cms.model.question.AnswerReply;
import cms.model.question.Question;
import cms.model.report.Report;
import cms.model.report.ReportType;
import cms.model.setting.SystemSetting;
import cms.model.topic.Comment;
import cms.model.topic.Reply;
import cms.model.topic.Topic;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.report.ReportRepository;
import cms.repository.report.ReportTypeRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import cms.service.frontend.ReportClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.FileUtil;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 前台举报服务
 */
@Service
public class ReportClientServiceImpl implements ReportClientService {
    private static final Logger logger = LogManager.getLogger(ReportClientServiceImpl.class);

    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    FileComponent fileComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    ReportTypeRepository reportTypeRepository;
    @Resource
    ReportTypeComponent reportTypeComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    TopicRepository topicRepository;
    @Resource
    CommentRepository commentRepository;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    AnswerRepository answerRepository;
    @Resource
    ReportRepository reportRepository;



    //模块参数
    private final List<Integer> modules = Arrays.asList(10,20,30,40,50,60);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    private SettingCacheManager settingCacheManager;


    /**
     * 获取举报界面信息
     * @return
     */
    public Map<String,Object> getAddReportViewModel(){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(accessUser != null){
            boolean captchaKey = captchaComponent.report_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());//是否有验证码
            }
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //如果全局不允许提交问题
        if(!systemSetting.isAllowReport()){
            returnValue.put("allowReport",false);//不允许提交问题
        }else{
            returnValue.put("allowReport",true);//允许提交问题
        }
        returnValue.put("reportTypeList", reportTypeList());
        returnValue.put("reportMaxImageUpload", systemSetting.getReportMaxImageUpload());//图片允许最大上传数量
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        return returnValue;
    }

    /**
     * 添加举报
     * @param reportDTO 举报表单
     * @param ip IP地址
     * @param imageFile 图片文件
     * @return
     */
    public Map<String,Object> addReport(ReportDTO reportDTO,String ip, MultipartFile[] imageFile){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("report", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //验证码
        boolean isCaptcha = captchaComponent.report_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(reportDTO.getCaptchaKey(), reportDTO.getCaptchaValue(), errors);
        }

        if(reportDTO.getModule() == null || reportDTO.getModule() <=0){
            errors.put("report", "模块不能为空");

        }else{
            if(!modules.contains(reportDTO.getModule())){
                errors.put("report", "状态参数错误");
            }
        }
        Report report = new Report();

        if(errors.size()==0 && reportDTO.getParameterId() != null && !reportDTO.getParameterId().trim().isEmpty()){
            if(reportDTO.getModule().equals(10)){//话题
                Topic topic = topicRepository.findById(Long.parseLong(reportDTO.getParameterId().trim()));
                if(topic != null && topic.getStatus().equals(20)){
                    report.setParameterId(reportDTO.getParameterId().trim());
                }else{
                    errors.put("report", "话题不存在");
                }
            }else if(reportDTO.getModule().equals(20)){//评论
                Comment comment = commentRepository.findByCommentId(Long.parseLong(reportDTO.getParameterId().trim()));
                if(comment != null && comment.getStatus().equals(20)){
                    report.setParameterId(reportDTO.getParameterId().trim());
                    report.setExtraParameterId(comment.getTopicId().toString());
                }else{
                    errors.put("report", "评论不存在");
                }
            }else if(reportDTO.getModule().equals(30)){//评论回复
                Reply reply = commentRepository.findReplyByReplyId(Long.parseLong(reportDTO.getParameterId().trim()));
                if(reply != null && reply.getStatus().equals(20)){
                    report.setParameterId(reportDTO.getParameterId().trim());
                    report.setExtraParameterId(reply.getTopicId()+"-"+reply.getCommentId());
                }else{
                    errors.put("report", "评论回复不存在");
                }
            }else if(reportDTO.getModule().equals(40)){//问题
                Question question = questionRepository.findById(Long.parseLong(reportDTO.getParameterId().trim()));
                if(question != null && question.getStatus().equals(20)){
                    report.setParameterId(reportDTO.getParameterId().trim());
                }else{
                    errors.put("report", "问题不存在");
                }
            }else if(reportDTO.getModule().equals(50)){//答案
                Answer answer = answerRepository.findByAnswerId(Long.parseLong(reportDTO.getParameterId().trim()));
                if(answer != null && answer.getStatus().equals(20)){
                    report.setParameterId(reportDTO.getParameterId().trim());
                    report.setExtraParameterId(answer.getQuestionId().toString());
                }else{
                    errors.put("report", "答案不存在");
                }
            }else if(reportDTO.getModule().equals(60)){//答案回复
                AnswerReply answerReply = answerRepository.findReplyByReplyId(Long.parseLong(reportDTO.getParameterId().trim()));
                if(answerReply != null && answerReply.getStatus().equals(20)){
                    report.setParameterId(reportDTO.getParameterId().trim());
                    report.setExtraParameterId(answerReply.getQuestionId()+"-"+answerReply.getAnswerId());
                }else{
                    errors.put("report", "答案回复不存在");
                }
            }
            //查询用户是否有本模块的待处理状态举报
            boolean isReport = reportRepository.findByCondition(accessUser.getUserName(),reportDTO.getParameterId().trim(), reportDTO.getModule(), 10);
            if(isReport){
                errors.put("report", "您之前对此内容的举报还在处理中，请不要重复举报");
            }

        }else{
            errors.put("report", "参数Id不能为空");
        }


        String format_reason = "";

        if(reportDTO.getReportTypeId() != null && !reportDTO.getReportTypeId().trim().isEmpty()){
            ReportType reportType = reportTypeRepository.findById(reportDTO.getReportTypeId().trim());
            if(reportType != null){
                if(reportType.getChildNodeNumber() == 0 && reportType.getGiveReason()){
                    if(reportDTO.getReason() != null && !reportDTO.getReason().trim().isEmpty()){
                        format_reason = textFilterComponent.filterText(reportDTO.getReason().trim());
                    }else{
                        errors.put("reason", "请填写理由");
                    }
                }

            }else{
                errors.put("reportTypeId", "举报分类不存在");
            }
        }else{
            errors.put("reportTypeId", "请选择举报分类");
        }


        //如果全局不允许提交问题
        if(!systemSetting.isAllowReport()){
            errors.put("report", "不允许提交举报");
        }


        LocalDateTime dateTime = LocalDateTime.now();
        String date = dateTime.format(formatter);
        List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();

        if(errors.size() == 0 && format_reason != null && !format_reason.trim().isEmpty() && systemSetting.getReportMaxImageUpload() >0 && imageFile != null && imageFile.length <= systemSetting.getReportMaxImageUpload()){
            for(MultipartFile image : imageFile) {
                if(!image.isEmpty()){
                    //验证文件类型
                    List<String> formatList = new ArrayList<String>();
                    formatList.add("gif");
                    formatList.add("jpg");
                    formatList.add("jpeg");
                    formatList.add("bmp");
                    formatList.add("png");
                    formatList.add("webp");
                    boolean authentication = FileUtil.validateFileSuffix(image.getOriginalFilename(),formatList);
                    if(authentication){
                        //取得文件后缀
                        String ext = FileUtil.getExtension(image.getOriginalFilename());
                        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
                        String pathDir = "file"+ File.separator+"report"+File.separator + date +File.separator;;
                        //构建文件名称
                        String fileName = UUIDUtil.getUUID32()+ "." + ext;

                        imageInfoList.add(new ImageInfo("file/report/"+ date +"/",fileName));

                        //生成文件保存目录
                        FileUtil.createFolder(pathDir);

                        //生成锁文件名称
                        String lockFileName = date +"_"+fileName;
                        //添加文件锁
                        try {
                            //添加文件锁
                            fileComponent.addLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,lockFileName);
                            //保存文件
                            fileComponent.writeFile(pathDir, fileName,image.getBytes());
                        } catch (IOException e) {
                            if (logger.isErrorEnabled()) {
                                logger.error("上传图片错误",e);
                            }
                            throw new BusinessException(Map.of("imageFile", "上传图片错误"));
                        }


                    }else{
                        errors.put("imageFile", "图片格式错误");
                    }
                }
            }

        }

        report.setUserName(accessUser.getUserName());

        report.setModule(reportDTO.getModule());
        report.setReportTypeId(reportDTO.getReportTypeId());
        report.setReason(format_reason);
        report.setPostTime(dateTime);
        report.setStatus(10);
        report.setIp(ip);
        if(imageInfoList.size() >0){
            report.setImageData(jsonComponent.toJSONString(imageInfoList));
        }



        if(errors.size() == 0){
            //保存举报
            reportRepository.saveReport(report);

            //统计每分钟原来提交次数
            Integer original = settingCacheManager.getSubmitQuantity("report", accessUser.getUserName());
            if(original != null){
                settingCacheManager.addSubmitQuantity("report", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
            }else{
                settingCacheManager.addSubmitQuantity("report", accessUser.getUserName(),1);//刷新每分钟原来提交次数
            }

            //删除图片锁
            if(imageInfoList.size() >0){
                for(ImageInfo imageInfo : imageInfoList){
                    fileComponent.deleteLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,date +"_"+imageInfo.getName());
                }
            }
        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
            if(isCaptcha){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());
            }

        }else{
            returnValue.put("success", true);
        }
        return returnValue;
    }






    /**
     * 分类列表
     * @return
     */
    private List<ReportType> reportTypeList(){
        List<ReportType> reportTypeList =  reportTypeRepository.findAllReportType_cache();
        List<ReportType> new_reportTypeList = new ArrayList<ReportType>();//排序后标签

        if(reportTypeList != null && reportTypeList.size() >0){

            //组成排序数据
            Iterator<ReportType> reportType_iter = reportTypeList.iterator();
            while(reportType_iter.hasNext()){
                ReportType reportType = reportType_iter.next();

                //如果是根节点
                if(reportType.getParentId().equals("0")){

                    new_reportTypeList.add(reportType);
                    reportType_iter.remove();
                }
            }
            //组合子分类
            for(ReportType reportType :new_reportTypeList){
                reportTypeComponent.childReportType(reportType,reportTypeList);
            }
            //排序
            reportTypeComponent.reportTypeSort(new_reportTypeList);

        }
        return new_reportTypeList;
    }

    /**
     * 获取举报列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<Report> getReportList(int page, String fileServerAddress){
        //调用分页算法代码
        PageView<Report> pageView = new PageView<Report>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(accessUser.getUserName());

        jpql.append(" and o.status<?"+ (params.size()+1));
        params.add(1000);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
        orderby.put("id", "desc");//根据id字段降序排序

        QueryResult<Report> qr = answerRepository.getScrollData(Report.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            List<ReportType> reportTypeList = reportTypeRepository.findAllReportType();
            if(reportTypeList != null && reportTypeList.size() >0){
                for(Report report : qr.getResultlist()){
                    for(ReportType reportType : reportTypeList){
                        if(report.getReportTypeId().equals(reportType.getId())){
                            report.setReportTypeName(reportType.getName());
                            break;
                        }
                    }
                }
            }
            for(Report report : qr.getResultlist()){
                if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
                    List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
                    if(imageInfoList != null && imageInfoList.size() >0){
                        for(ImageInfo imageInfo : imageInfoList){
                            imageInfo.setPath(fileServerAddress+imageInfo.getPath());
                        }
                        report.setImageInfoList(imageInfoList);
                    }
                }
                //不显示的信息
                report.setStaffAccount(null);
                report.setRemark(null);
                report.setIp(null);
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }
}
