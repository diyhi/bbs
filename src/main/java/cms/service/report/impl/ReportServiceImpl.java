package cms.service.report.impl;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.report.ReportTypeComponent;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.report.ReportRequest;
import cms.dto.topic.ImageInfo;
import cms.model.question.Question;
import cms.model.report.Report;
import cms.model.report.ReportType;
import cms.model.staff.SysUsers;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.question.QuestionRepository;
import cms.repository.report.ReportRepository;
import cms.repository.report.ReportTypeRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserRepository;
import cms.service.report.ReportService;
import cms.utils.FileUtil;
import cms.utils.IpAddress;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 举报服务
 */
@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger logger = LogManager.getLogger(ReportServiceImpl.class);


    @Resource
    ReportTypeRepository reportTypeRepository;
    @Resource
    ReportRepository reportRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    ReportTypeComponent reportTypeComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource
    FileComponent fileComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    TopicRepository topicRepository;
    @Resource
    QuestionRepository questionRepository;
    @Resource
    UserRepository userRepository;

    //模块参数
    private final List<Integer> statusList = Arrays.asList(40,50);

    /**
     * 获取举报列表
     * @param page 页码
     * @param visible 是否可见
     * @param request 请求信息
     */
    public PageView<Report> getReportList(int page, Boolean visible, HttpServletRequest request){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        if(visible != null && !visible){
            jpql.append(" and o.status>?"+ (params.size()+1));
            params.add(1000);
        }else{
            jpql.append(" and o.status<?"+ (params.size()+1));
            params.add(1000);
        }
        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        PageView<Report> pageView = new PageView<Report>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Report> qr = reportRepository.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);


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
                //	User user = userService.findUserByUserName(report.getUserName());
                User user = userCacheManager.query_cache_findUserByUserName(report.getUserName());
                if(user != null){
                    report.setUserId(user.getId());
                    report.setAccount(user.getAccount());
                    report.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        report.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                        report.setAvatarName(user.getAvatarName());
                    }
                }

                if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
                    List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
                    if(imageInfoList != null && imageInfoList.size() >0){
                        for(ImageInfo imageInfo : imageInfoList){
                            imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                        }
                        report.setImageInfoList(imageInfoList);
                    }
                }

                if(report.getIp() != null && !report.getIp().trim().isEmpty()){
                    report.setIpAddress(IpAddress.queryAddress(report.getIp()));
                }
            }


        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取话题举报列表
     * @param page 页码
     * @param parameterId 参数Id
     * @param module 模块
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getTopicReportList(int page,String parameterId,Integer module,Long topicId, HttpServletRequest request){
        if(topicId == null || topicId <=0L) {
            throw new BusinessException(Map.of("topicId", "话题Id不能为空"));
        }
        if(parameterId == null || parameterId.trim().isEmpty()){
            throw new BusinessException(Map.of("parameterId", "参数Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Topic topic = topicRepository.findById(topicId);
        if(topic != null){
            returnValue.put("currentTopic", topic);
        }
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        PageView<Report> pageView = new PageView<Report>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);


        jpql.append(" and o.parameterId=?"+ (params.size()+1));
        params.add(parameterId);

        jpql.append(" and o.module=?"+ (params.size()+1));
        params.add(module);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Report> qr = reportRepository.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);


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
                //	User user = userService.findUserByUserName(report.getUserName());
                User user = userCacheManager.query_cache_findUserByUserName(report.getUserName());
                if(user != null){
                    report.setUserId(user.getId());
                    report.setAccount(user.getAccount());
                    report.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        report.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                        report.setAvatarName(user.getAvatarName());
                    }
                }

                if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
                    List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
                    if(imageInfoList != null && imageInfoList.size() >0){
                        for(ImageInfo imageInfo : imageInfoList){
                            imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                        }
                        report.setImageInfoList(imageInfoList);
                    }
                }

                if(report.getIp() != null && !report.getIp().trim().isEmpty()){
                    report.setIpAddress(IpAddress.queryAddress(report.getIp()));
                }
            }


        }

        pageView.setQueryResult(qr);

        returnValue.put("pageView", pageView);

        return returnValue;
    }

    /**
     * 获取问题举报列表
     * @param page 页码
     * @param parameterId 参数Id
     * @param module 模块
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getQuestionReportList(int page,String parameterId,Integer module,Long questionId, HttpServletRequest request) {
        if (questionId == null || questionId <= 0L) {
            throw new BusinessException(Map.of("questionId", "问题Id不能为空"));
        }
        if (parameterId == null || parameterId.trim().isEmpty()) {
            throw new BusinessException(Map.of("parameterId", "参数Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Question question= questionRepository.findById(questionId);
        if(question != null){
            returnValue.put("currentQuestion", question);
        }
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        PageView<Report> pageView = new PageView<Report>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);


        jpql.append(" and o.parameterId=?"+ (params.size()+1));
        params.add(parameterId);

        jpql.append(" and o.module=?"+ (params.size()+1));
        params.add(module);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Report> qr = reportRepository.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);


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
                //	User user = userService.findUserByUserName(report.getUserName());
                User user = userCacheManager.query_cache_findUserByUserName(report.getUserName());
                if(user != null){
                    report.setUserId(user.getId());
                    report.setAccount(user.getAccount());
                    report.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        report.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                        report.setAvatarName(user.getAvatarName());
                    }
                }

                if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
                    List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
                    if(imageInfoList != null && imageInfoList.size() >0){
                        for(ImageInfo imageInfo : imageInfoList){
                            imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                        }
                        report.setImageInfoList(imageInfoList);
                    }
                }

                if(report.getIp() != null && !report.getIp().trim().isEmpty()){
                    report.setIpAddress(IpAddress.queryAddress(report.getIp()));
                }
            }


        }

        pageView.setQueryResult(qr);

        returnValue.put("pageView", pageView);
        return returnValue;
    }


    /**
     * 获取用户举报列表
     * @param page 页码
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getUserReportList(int page,String userName, HttpServletRequest request) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new BusinessException(Map.of("userName", "用户名称不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        PageView<Report> pageView = new PageView<Report>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);


        jpql.append(" and o.userName=?"+ (params.size()+1));
        params.add(userName);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //调用分页算法类
        QueryResult<Report> qr = reportRepository.getScrollData(Report.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);


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
                //	User user = userService.findUserByUserName(report.getUserName());
                User user = userCacheManager.query_cache_findUserByUserName(report.getUserName());
                if(user != null){
                    report.setUserId(user.getId());
                    report.setAccount(user.getAccount());
                    report.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        report.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                        report.setAvatarName(user.getAvatarName());
                    }
                }

                if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
                    List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
                    if(imageInfoList != null && imageInfoList.size() >0){
                        for(ImageInfo imageInfo : imageInfoList){
                            imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                        }
                        report.setImageInfoList(imageInfoList);
                    }
                }

                if(report.getIp() != null && !"".equals(report.getIp().trim())){
                    report.setIpAddress(IpAddress.queryAddress(report.getIp()));
                }
            }


        }

        pageView.setQueryResult(qr);

        User user = userRepository.findUserByUserName(userName);
        if(user != null){
            User currentUser = new User();
            currentUser.setId(user.getId());
            currentUser.setAccount(user.getAccount());
            currentUser.setNickname(user.getNickname());
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                currentUser.setAvatarName(user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);
        }


        returnValue.put("pageView", pageView);
        return returnValue;
    }

    /**
     * 举报处理
     * @param reportRequest 举报表单
     */
    public void reportHandle(ReportRequest reportRequest){
        if(reportRequest.getReportId() == null || reportRequest.getReportId() <=0L) {
            throw new BusinessException(Map.of("reportId", "举报Id不能为空"));
        }

        Report report = reportRepository.findById(reportRequest.getReportId());
        if(report == null) {
            throw new BusinessException(Map.of("reportId", "举报不存在"));
        }

        if(!report.getStatus().equals(10)){
            throw new BusinessException(Map.of("report", "待处理状态才能执行举报流程"));
        }
        if(!report.getVersion().equals(reportRequest.getVersion())){
            throw new BusinessException(Map.of("report", "举报内容不是最新"));
        }
        if(reportRequest.getStatus() == null || reportRequest.getStatus() <=0){
            throw new BusinessException(Map.of("status", "状态不能为空"));
        }
        if(!statusList.contains(reportRequest.getStatus())){
            throw new BusinessException(Map.of("status", "状态参数错误"));
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof SysUsers){
            userId =((SysUsers)principal).getUserId();
            username =((SysUsers)principal).getUserAccount();
        }

        int i = reportRepository.updateReportInfo(reportRequest.getReportId(), reportRequest.getStatus(), reportRequest.getProcessResult(), reportRequest.getRemark(), LocalDateTime.now(),username, reportRequest.getVersion());
        if(i ==0){
            throw new BusinessException(Map.of("report", "修改失败"));
        }
    }


    /**
     * 获取修改举报界面信息
     * @param reportId 举报Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getEditReportViewModel(Long reportId, HttpServletRequest request){
        if(reportId == null || reportId <=0L){
            throw new BusinessException(Map.of("reportId", "举报Id不能为空"));
        }
        Report report = reportRepository.findById(reportId);

        if(report == null){
            throw new BusinessException(Map.of("reportId", "举报不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        ReportType reportType = reportTypeRepository.findById(report.getReportTypeId());
        if(reportType != null){
            report.setReportTypeName(reportType.getName());
        }

        if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
            List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
            if(imageInfoList != null && imageInfoList.size() >0){
                for(ImageInfo imageInfo : imageInfoList){
                    imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                }
                report.setImageInfoList(imageInfoList);
            }
        }
        returnValue.put("report",report);//返回消息
        return returnValue;
    }
    /**
     * 修改举报
     * @param reportRequest 举报表单
     * @param imageFile 图片
     * @param request 请求信息
     */
    public void editReport(ReportRequest reportRequest, MultipartFile[] imageFile, HttpServletRequest request){
        if(reportRequest.getReportId() == null || reportRequest.getReportId() <=0L){
            throw new BusinessException(Map.of("reportId", "举报Id不能为空"));
        }

        Report report = reportRepository.findById(reportRequest.getReportId());
        if(report == null){
            throw new BusinessException(Map.of("reportId", "举报不存在"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        if(reportRequest.getStatus() != null && !statusList.contains(reportRequest.getStatus())){
            errors.put("status", "状态参数错误");
        }
        if(!report.getVersion().equals(reportRequest.getVersion())){
            errors.put("report", "举报内容不是最新");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = report.getPostTime().format(formatter);

        List<ImageInfo> new_imageInfoList = handleFileUpload(reportRequest.getImagePath(), imageFile,date,request,errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        String username = "";//用户名称
        String userId = "";//用户Id
        Object principal  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof SysUsers){
            userId =((SysUsers)principal).getUserId();
            username =((SysUsers)principal).getUserAccount();
        }

        Integer old_status = report.getStatus();

        if(reportRequest.getStatus() != null){
            report.setStatus(reportRequest.getStatus());
            if(old_status.equals(10)){
                report.setProcessCompleteTime(LocalDateTime.now());
                report.setStaffAccount(username);
            }
        }

        report.setReportTypeId(reportRequest.getReportTypeId());
        report.setReason(reportRequest.getReason());
        report.setProcessResult(reportRequest.getProcessResult());
        report.setRemark(reportRequest.getRemark());

        List<ImageInfo> old_imageInfoList = new ArrayList<ImageInfo>();
        if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
            old_imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
        }



        String imageData = jsonComponent.toJSONString(new_imageInfoList);


        int i = reportRepository.updateReportInfo(report.getId(), report.getReportTypeId(),report.getStatus(), report.getProcessResult(), report.getReason(),report.getRemark(), report.getProcessCompleteTime(),report.getStaffAccount(),imageData, reportRequest.getVersion());
        if(i ==0){
            throw new BusinessException(Map.of("report", "修改失败"));
        }
        //删除旧文件
        A:for(ImageInfo old_imageInfo : old_imageInfoList){
            for(ImageInfo new_imageInfo : new_imageInfoList){
                if(old_imageInfo.getPath().equals(new_imageInfo.getPath()) && old_imageInfo.getName().equals(new_imageInfo.getName())){
                    continue A;
                }
            }
            //删除旧图片
            //替换路径中的..号
            String oldPathFile = FileUtil.toRelativePath(old_imageInfo.getPath()+old_imageInfo.getName());
            //删除旧文件
            fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile));
        }

        //删除图片锁
        if(new_imageInfoList.size() >0){
            for(ImageInfo imageInfo : new_imageInfoList){
                fileComponent.deleteLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,date +"_"+imageInfo.getName());
            }
        }


    }

    /**
     * 删除举报
     * @param reportId 举报Id集合
     */
    public void deleteReport(Long[] reportId){
        if(reportId == null || reportId.length == 0){
            throw new BusinessException(Map.of("reportId", "举报Id不存在"));
        }

        List<Long> reportIdList = new ArrayList<Long>();
        for(Long l :reportId){
            if(l != null && l >0L){
                reportIdList .add(l);
            }
        }
        if(reportIdList.size() ==0){
            throw new BusinessException(Map.of("reportId", "举报Id不能为空"));
        }

        List<Report> reportList = reportRepository.findByIdList(reportIdList);
        if(reportList == null || reportList.size() ==0) {
            throw new BusinessException(Map.of("report", "举报不存在"));
        }
        for(Report report : reportList){
            if(report.getStatus() < 1000){//标记删除
                int i = reportRepository.markDelete(report.getId(),100000);
            }else{//物理删除
                int i = reportRepository.deleteReport(report.getId());
                if(i>0){
                    //删除图片
                    if(report.getImageData() != null && !report.getImageData().trim().isEmpty()){
                        List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(report.getImageData().trim(),new TypeReference< List<ImageInfo> >(){});
                        if(imageInfoList != null && imageInfoList.size() >0){
                            for(ImageInfo imageInfo : imageInfoList){
                                //删除图片
                                //替换路径中的..号
                                String oldPathFile = FileUtil.toRelativePath(imageInfo.getPath());
                                //删除文件
                                fileComponent.deleteFile(FileUtil.toSystemPath(oldPathFile)+imageInfo.getName());
                            }
                        }
                    }
                }
            }


        }
    }

    /**
     * 还原举报
     * @param reportId 举报Id集合
     */
    public void reductionReport(Long[] reportId){
        if(reportId == null || reportId.length == 0){
            throw new BusinessException(Map.of("reportId", "举报Id不存在"));
        }

        List<Long> reportIdList = new ArrayList<Long>();
        for(Long l :reportId){
            if(l != null && l >0L){
                reportIdList .add(l);
            }
        }
        if(reportIdList.size() ==0){
            throw new BusinessException(Map.of("reportId", "举报Id不能为空"));
        }
        List<Report> reportList = reportRepository.findByIdList(reportIdList);
        if(reportList == null || reportList.size() ==0) {
            throw new BusinessException(Map.of("report", "举报不存在"));
        }
        for(Report report : reportList){
            if(report.getStatus() >1000){
                int originalState = this.parseInitialValue(report.getStatus());

                reportRepository.reductionReport(report.getId(), originalState);
            }
        }
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

    /**
     * 处理文件上传
     * @param imagePath 图片路径
     * @param imageFile    图片
     * @param date 日期
     * @param request 请求信息
     */
    private List<ImageInfo> handleFileUpload(String[] imagePath, MultipartFile[] imageFile,String date, HttpServletRequest request,Map<String, String> errors) {
        List<ImageInfo> new_imageInfoList = new ArrayList<ImageInfo>();
        if(imageFile == null){
            return new_imageInfoList;
        }

        int imagePathCount = 0;//已上传图片文件上传总数
        for(MultipartFile file : imageFile) {
            if(file.isEmpty()){//如果图片已上传
                String image = imagePath[imagePathCount];
                if(image != null && !image.trim().isEmpty()){
                    image = textFilterComponent.deleteBindURL(request, image);

                }
                //取得文件名称
                String fileName = FileUtil.getName(image);

                //取得路径名称
                String pathName = FileUtil.getFullPath(image);



                new_imageInfoList.add(new ImageInfo(pathName,fileName));

                imagePathCount++;
            }else{

                //验证文件类型
                List<String> formatList = new ArrayList<String>();
                formatList.add("gif");
                formatList.add("jpg");
                formatList.add("jpeg");
                formatList.add("bmp");
                formatList.add("png");
                formatList.add("webp");
                boolean authentication = FileUtil.validateFileSuffix(file.getOriginalFilename(),formatList);
                if(authentication){
                    //取得文件后缀
                    String ext = FileUtil.getExtension(file.getOriginalFilename());
                    //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
                    String pathDir = "file"+File.separator+"report"+File.separator + date +File.separator;;
                    //构建文件名称
                    String fileName = UUIDUtil.getUUID32()+ "." + ext;

                    new_imageInfoList.add(new ImageInfo("file/report/"+ date +"/",fileName));

                    //生成文件保存目录
                    FileUtil.createFolder(pathDir);

                    //生成锁文件名称
                    String lockFileName = date +"_"+fileName;
                    //添加文件锁
                    try {
                        fileComponent.addLock("file"+File.separator+"report"+File.separator+"lock"+File.separator,lockFileName);
                        //保存文件
                        fileComponent.writeFile(pathDir, fileName,file.getBytes());
                    } catch (IOException e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("上传图片错误", e);
                        }
                        throw new BusinessException(Map.of("images", "上传图片错误"));
                    }



                }else{
                    errors.put("image", "图片格式错误");
                }
            }
        }
        return new_imageInfoList;
    }


}
