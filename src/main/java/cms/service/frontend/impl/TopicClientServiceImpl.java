package cms.service.frontend.impl;

import cms.component.*;
import cms.component.fileSystem.FileComponent;
import cms.component.filterWord.SensitiveWordFilterComponent;
import cms.component.follow.FollowCacheManager;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.lucene.TopicIndexComponent;
import cms.component.lucene.TopicLuceneComponent;
import cms.component.mediaProcess.MediaProcessQueueCacheManager;
import cms.component.mediaProcess.MediaProcessQueueComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.message.RemindCacheManager;
import cms.component.message.RemindComponent;
import cms.component.message.RemindConfig;
import cms.component.payment.PaymentComponent;
import cms.component.redEnvelope.RedEnvelopeComponent;
import cms.component.setting.SettingCacheManager;
import cms.component.setting.SettingComponent;
import cms.component.staff.StaffCacheManager;
import cms.component.topic.HotTopicComponent;
import cms.component.topic.TopicCacheManager;
import cms.component.topic.TopicComponent;
import cms.component.topic.TopicUnhideConfig;
import cms.component.user.*;
import cms.config.BusinessException;
import cms.dto.*;
import cms.dto.frontendModule.TopicDTO;
import cms.dto.riskControl.BlockedFeaturesEnum;
import cms.dto.topic.HideTagName;
import cms.dto.topic.HideTagType;
import cms.dto.topic.ImageInfo;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.frontendModule.*;
import cms.model.message.Remind;
import cms.model.payment.PaymentLog;
import cms.model.platformShare.TopicUnhidePlatformShare;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.setting.EditorTag;
import cms.model.setting.SystemSetting;
import cms.model.staff.SysUsers;
import cms.model.thumbnail.Thumbnail;
import cms.model.topic.Tag;
import cms.model.topic.Topic;
import cms.model.topic.TopicIndex;
import cms.model.topic.TopicUnhide;
import cms.model.user.PointLog;
import cms.model.user.User;
import cms.model.user.UserDynamic;
import cms.model.user.UserGrade;
import cms.model.vote.VoteOption;
import cms.model.vote.VoteTheme;
import cms.repository.message.RemindRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.thumbnail.ThumbnailRepository;
import cms.repository.topic.TagRepository;
import cms.repository.topic.TopicIndexRepository;
import cms.repository.topic.TopicRepository;
import cms.repository.user.UserGradeRepository;
import cms.repository.user.UserRepository;
import cms.service.frontend.TopicClientService;
import cms.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 前台话题服务
 */
@Service
public class TopicClientServiceImpl implements TopicClientService {
    private static final Logger logger = LogManager.getLogger(TopicClientServiceImpl.class);


    @Resource
    SettingRepository settingRepository;
    @Resource SettingCacheManager settingCacheManager;
    @Resource TopicCacheManager topicCacheManager;
    @Resource
    TopicComponent topicComponent;
    @Resource
    TopicUnhideConfig topicUnhideConfig;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    UserRepository userRepository;
    @Resource UserCacheManager userCacheManager;
    @Resource
    PointLogConfig pointLogConfig;
    @Resource
    PointComponent pointComponent;
    @Resource
    PaymentComponent paymentComponent;
    @Resource
    RemindComponent remindComponent;
    @Resource
    TopicRepository topicRepository;
    @Resource
    RemindConfig remindConfig;
    @Resource
    RemindRepository remindRepository;
    @Resource RemindCacheManager remindCacheManager;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    TagRepository tagRepository;
    @Resource MediaProcessQueueCacheManager mediaProcessQueueCacheManager;
    @Resource
    MediaProcessQueueComponent mediaProcessQueueComponent;
    @Resource
    FileComponent fileComponent;
    @Resource StaffCacheManager staffCacheManager;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserGradeRepository userGradeRepository;
    @Resource
    TopicLuceneComponent topicLuceneComponent;
    @Resource
    HotTopicComponent hotTopicComponent;
    @Resource
    CaptchaComponent captchaComponent;
    @Resource
    SensitiveWordFilterComponent sensitiveWordFilterComponent;
    @Resource
    SettingComponent settingComponent;
    @Resource
    TopicIndexComponent topicIndexComponent;
    @Resource
    RedEnvelopeComponent redEnvelopeComponent;
    @Resource
    UserDynamicComponent userDynamicComponent;
    @Resource
    TopicIndexRepository topicIndexRepository;
    @Resource UserDynamicConfig userDynamicConfig;
    @Resource
    ThumbnailRepository thumbnailRepository;
    @Resource FollowCacheManager followCacheManager;


    //Html过滤结果
    private record HtmlFilterResult(HtmlProcessingResult htmlProcessingResult, String formatterMarkdown, String content) {}


    /**
     * 文件上传配置
     * @param success 是否校验通过
     * @param message 错误消息
     * @param formats 允许的后缀列表
     * @param maxSize 最大限制 (KB)
     */
    private record FileUploadConfig(
            boolean success,
            String message,
            List<String> formats,
            long maxSize
    ) {
        public static FileUploadConfig ok(List<String> formats, long maxSize) {
            return new FileUploadConfig(true, null, formats, maxSize);
        }
        public static FileUploadConfig fail(String message) {
            return new FileUploadConfig(false, message, null, 0);
        }
    }



    /**
     * 获取话题分页
     * @param page 页码
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    public PageView<Topic> getTopicPage(int page, Long tagId, HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigTopicPage configTopicPage) {
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if(configTopicPage.getMaxResult() != null && configTopicPage.getMaxResult() >0){
                maxResult = configTopicPage.getMaxResult();
            }
            int pageCount=10;// 页码显示总数
            int sort = 1;//排序


            //标签Id
            if(!configTopicPage.isTag_transferPrameter()){
                tagId = configTopicPage.getTagId();
            }

            //排序
            if(configTopicPage.getSort() != null && configTopicPage.getSort() >0){
                sort = configTopicPage.getSort();
            }

            //页码显示总数
            if(configTopicPage.getPageCount() != null && configTopicPage.getPageCount() >0){
                pageCount = configTopicPage.getPageCount();
            }

            AccessUser accessUser = AccessUserThreadLocal.get();

            PageForm pageForm = new PageForm();
            pageForm.setPage(page);


            //调用分页算法代码
            PageView<Topic> pageView = new PageView<Topic>(maxResult,pageForm.getPage(),pageCount);
            //当前页
            int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

            //执行查询
            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();




            //标签Id
            if(tagId != null){
                Tag tag = tagRepository.findById_cache(tagId);
                if(tag != null){
                    jpql.append(" and o.tagIdGroup like ?"+ (params.size()+1)+" escape '/' ");//escpae '/'表示 对'/'后面的字符进行转义
                    params.add(cms.utils.StringUtil.escapeSQLLike(tag.getParentIdGroup()+tagId)+",%");//加上查询参数
                }
                //jpql.append(" and o.tagId=?"+ (params.size()+1));
                //params.add(tagId);//加上查询参数
            }

            jpql.append(" and o.status=?"+ (params.size()+1));
            params.add(20);//设置o.visible=?1是否可见


            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

            orderby.put("sort", "desc");//优先级   大-->小
            //排行依据
            if(sort == 1){
                orderby.put("id", "desc");//发布时间排序   新-->旧
            }else if(sort == 2){
                orderby.put("id", "asc");//发布时间排序  旧-->新
            }else if(sort == 3){
                orderby.put("lastReplyTime", "desc");//回复时间排序   新-->旧
            }else if(sort == 4){
                orderby.put("lastReplyTime", "asc");//回复时间排序  旧-->新
            }



            //删除第一个and
            String jpql_str = StringUtils.difference(" and", jpql.toString());
            QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);


            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

                for(Topic topic : qr.getResultlist()){

                    //话题内容隐藏标签MD5
                    String topicContentDigest_link = "";
                    String topicContentDigest_video = "";
                    String topicContentDigest_hide = "";

                    String processContent = topic.getContent();


                    //处理隐藏标签
                    if(processContent != null && !processContent.trim().isEmpty()){
                        List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);


                        Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topic.getId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

                        //生成处理'隐藏标签'Id
                        String processHideTagId = topicComponent.createProcessHideTagId(topic.getId(),topicContentUpdateMark, visibleTagList);

                        //处理隐藏标签
                        processContent = topicCacheManager.query_cache_processHiddenTag(processContent,visibleTagList,processHideTagId+"|"+topicContentDigest_link+"|"+ topicContentDigest_video);

                        topicContentDigest_hide = cms.utils.MD5.getMD5(processHideTagId);
                    }

                    //处理视频播放器标签
                    if(processContent != null && !processContent.trim().isEmpty()){
                        //处理富文本路径
                        processContent = fileComponent.processRichTextFilePath(processContent,"topic");

                        Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topic.getId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

                        //生成处理'视频播放器'Id
                        String processVideoPlayerId = mediaProcessQueueComponent.createProcessVideoPlayerId(topic.getId(),topicContentUpdateMark);


                        //处理视频信息
                        List<MediaInfo> mediaInfoList = mediaProcessQueueCacheManager.query_cache_processVideoInfo(WebUtil.getUrl(request),processContent,processVideoPlayerId+"|"+topicContentDigest_hide,topic.getTagId(),systemSetting.getFileSecureLinkSecret());
                        topic.setMediaInfoList(mediaInfoList);

                    }
                }


                //查询标签名称
                List<Tag> tagList = tagRepository.findAllTag_cache();
                Map<Long,List<String>> tagRoleNameMap = new HashMap<Long,List<String>>();//标签角色名称 key:标签Id value:角色名称集合
                Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id value:角色名称集合
                Map<Long,Boolean> userViewPermissionMap = new HashMap<Long,Boolean>();//用户如果对话题项是否有查看权限  key:标签Id value:是否有查看权限

                if(tagList != null && tagList.size() >0){
                    for(Topic topic : qr.getResultlist()){

                        //解析隐藏标签
                        Map<Integer,Object> analysisHiddenTagMap = topicCacheManager.query_cache_analysisHiddenTag(topic.getContent(),topic.getId());
                        if(analysisHiddenTagMap != null && analysisHiddenTagMap.size() >0){

                            //内容含有隐藏标签类型
                            LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();//key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁

                            //允许可见的隐藏标签
                            List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);


                            for (HideTagType hideTagType : HideTagType.values()) {//按枚举类的顺序排序
                                for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                                    if(entry.getKey().equals(hideTagType.getName())){
                                        if(visibleTagList.contains(entry.getKey())){
                                            hideTagTypeMap.put(entry.getKey(), true);

                                        }else{
                                            hideTagTypeMap.put(entry.getKey(), false);
                                        }
                                        break;
                                    }
                                }
                            }
                            topic.setHideTagTypeMap(hideTagTypeMap);

                        }

                        topic.setIp(null);//IP地址不显示
                        topic.setContent(null);//话题内容不显示
                        topic.setMarkdownContent(null);//话题Markdown内容不显示
                        if(topic.getPostTime().equals(topic.getLastReplyTime())){//如果发贴时间等于回复时间，则不显示回复时间
                            topic.setLastReplyTime(null);
                        }

                        topic.setViewTotal(topic.getViewTotal()+topicComponent.readLocalView(topic.getId()));

                        if(!topic.getIsStaff()){//会员
                            userRoleNameMap.put(topic.getUserName(), null);
                        }
                        for(Tag tag: tagList){
                            if(topic.getTagId().equals(tag.getId())){
                                topic.setTagName(tag.getName());
                                tagRoleNameMap.put(tag.getId(), null);
                                userViewPermissionMap.put(tag.getId(), null);
                                break;
                            }
                        }

                        if(topic.getImage() != null && !topic.getImage().trim().isEmpty()){
                            List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
                            if(imageInfoList != null && imageInfoList.size() >0){
                                for(ImageInfo imageInfo : imageInfoList){
                                    imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                                }
                                topic.setImageInfoList(imageInfoList);
                            }
                        }

                    }
                }

                if(tagRoleNameMap.size() >0){
                    for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                        List<String> roleNameList = userRoleComponent.queryAllowViewTopicRoleName(entry.getKey(),request);
                        entry.setValue(roleNameList);
                    }
                }

                if(userRoleNameMap.size() >0){
                    for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                        List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey(),request);
                        entry.setValue(roleNameList);
                    }
                }
                if(userViewPermissionMap.size()>0){
                    for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                        //是否有当前功能操作权限
                        boolean flag = userRoleComponent.isPermission(ResourceEnum._1001000,entry.getKey());
                        entry.setValue(flag);
                    }
                }

                for(Topic topic : qr.getResultlist()){
                    if(!topic.getIsStaff()){//会员
                        User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                        if(user != null){
                            topic.setAccount(user.getAccount());
                            topic.setNickname(user.getNickname());
                            topic.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                            topic.setAvatarName(user.getAvatarName());

                            if(user.getCancelAccountTime() != -1L){//账号已注销
                                topic.setUserInfoStatus(-30);
                            }
                        }

                    }else{
                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(topic.getUserName());
                        if(sysUsers != null){
                            topic.setNickname(sysUsers.getNickname());
                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                topic.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                topic.setAvatarName(sysUsers.getAvatarName());
                            }
                        }
                        topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
                    }
                    //话题允许查看的角色名称集合
                    for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                        if(entry.getKey().equals(topic.getTagId())){
                            List<String> roleNameList = entry.getValue();
                            if(roleNameList != null && roleNameList.size() >0){
                                topic.setAllowRoleViewList(roleNameList);
                            }
                            break;
                        }

                    }
                    if(!topic.getIsStaff()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(topic.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && roleNameList.size() >0){
                                    topic.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }

                    //用户如果对话题项无查看权限，则不显示摘要、图片、视频
                    for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                        if(entry.getKey().equals(topic.getTagId())){
                            if(entry.getValue() != null && !entry.getValue()){
                                topic.setImage(null);
                                topic.setImageInfoList(new ArrayList<ImageInfo>());
                                topic.setSummary("");
                                topic.setMediaInfoList(new ArrayList<MediaInfo>());
                            }
                            break;
                        }

                    }


                }


                //非正常状态用户清除显示数据
                for(Topic topic : qr.getResultlist()){
                    if(topic.getUserInfoStatus() <0){
                        topic.setUserName(null);
                        topic.setAccount(null);
                        topic.setNickname(null);
                        topic.setAvatarPath(null);
                        topic.setAvatarName(null);
                        topic.setUserRoleNameList(new ArrayList<String>());
                        topic.setTitle("");
                        topic.setContent("");
                        topic.setMarkdownContent("");
                        topic.setSummary("");
                        topic.setVoteThemeId("");
                    }
                }

            }


            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
            return pageView;

        }
        return null;
    }

    /**
     * 获取精华话题分页
     * @param page 页码
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    public PageView<Topic> getFeaturedTopicPage(int page, Long tagId, HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigTopicFeaturedPage configTopicFeaturedPage) {
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if (configTopicFeaturedPage.getMaxResult() != null)
                if (configTopicFeaturedPage.getMaxResult() > 0) {
                    maxResult = configTopicFeaturedPage.getMaxResult();
                }
            int pageCount = 10;// 页码显示总数
            int sort = 1;//排序


            //排序
            if (configTopicFeaturedPage.getSort() != null && configTopicFeaturedPage.getSort() > 0) {
                sort = configTopicFeaturedPage.getSort();
            }

            //页码显示总数
            if (configTopicFeaturedPage.getPageCount() != null && configTopicFeaturedPage.getPageCount() > 0) {
                pageCount = configTopicFeaturedPage.getPageCount();
            }

            AccessUser accessUser = AccessUserThreadLocal.get();

            PageForm pageForm = new PageForm();
            pageForm.setPage(page);


            //调用分页算法代码
            PageView<Topic> pageView = new PageView<Topic>(maxResult,pageForm.getPage(),pageCount);
            //当前页
            int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

            //执行查询
            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();



            jpql.append(" and o.essence=?"+ (params.size()+1));
            params.add(true);

            jpql.append(" and o.status=?"+ (params.size()+1));
            params.add(20);

            //标签Id
            if(tagId != null){
                Tag tag = tagRepository.findById_cache(tagId);
                if(tag != null){
                    jpql.append(" and o.tagIdGroup like ?"+ (params.size()+1)+" escape '/' ");//escpae '/'表示 对'/'后面的字符进行转义
                    params.add(cms.utils.StringUtil.escapeSQLLike(tag.getParentIdGroup()+tagId)+",%");//加上查询参数
                }
                //jpql.append(" and o.tagId=?"+ (params.size()+1));
                //params.add(tagId);//加上查询参数
            }

            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

            orderby.put("sort", "desc");//优先级   大-->小
            //排行依据
            if(sort == 1){
                orderby.put("id", "desc");//发布时间排序   新-->旧
            }else if(sort == 2){
                orderby.put("id", "asc");//发布时间排序  旧-->新
            }



            //删除第一个and
            String jpql_str = StringUtils.difference(" and", jpql.toString());
            QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);


            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

                for(Topic topic : qr.getResultlist()){

                    //话题内容隐藏标签MD5
                    String topicContentDigest_link = "";
                    String topicContentDigest_video = "";
                    String topicContentDigest_hide = "";

                    String processContent = topic.getContent();


                    //处理隐藏标签
                    if(processContent != null && !processContent.trim().isEmpty()){
                        List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);


                        Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topic.getId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

                        //生成处理'隐藏标签'Id
                        String processHideTagId = topicComponent.createProcessHideTagId(topic.getId(),topicContentUpdateMark, visibleTagList);

                        //处理隐藏标签
                        processContent = topicCacheManager.query_cache_processHiddenTag(processContent,visibleTagList,processHideTagId+"|"+topicContentDigest_link+"|"+ topicContentDigest_video);

                        topicContentDigest_hide = cms.utils.MD5.getMD5(processHideTagId);
                    }

                    //处理视频播放器标签
                    if(processContent != null && !processContent.trim().isEmpty()){
                        //处理富文本路径
                        processContent = fileComponent.processRichTextFilePath(processContent,"topic");

                        Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topic.getId(), ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

                        //生成处理'视频播放器'Id
                        String processVideoPlayerId = mediaProcessQueueComponent.createProcessVideoPlayerId(topic.getId(),topicContentUpdateMark);


                        //处理视频信息
                        List<MediaInfo> mediaInfoList = mediaProcessQueueCacheManager.query_cache_processVideoInfo(WebUtil.getUrl(request),processContent,processVideoPlayerId+"|"+topicContentDigest_hide,topic.getTagId(),systemSetting.getFileSecureLinkSecret());
                        topic.setMediaInfoList(mediaInfoList);

                    }
                }


                //查询标签名称
                List<Tag> tagList = tagRepository.findAllTag_cache();
                Map<Long,List<String>> tagRoleNameMap = new HashMap<Long,List<String>>();//标签角色名称 key:标签Id value:角色名称集合
                Map<String,List<String>> userRoleNameMap = new HashMap<String,List<String>>();//用户角色名称 key:用户名称Id value:角色名称集合
                Map<Long,Boolean> userViewPermissionMap = new HashMap<Long,Boolean>();//用户如果对话题项是否有查看权限  key:标签Id value:是否有查看权限

                if(tagList != null && tagList.size() >0){
                    for(Topic topic : qr.getResultlist()){

                        //解析隐藏标签
                        Map<Integer,Object> analysisHiddenTagMap = topicCacheManager.query_cache_analysisHiddenTag(topic.getContent(),topic.getId());
                        if(analysisHiddenTagMap != null && analysisHiddenTagMap.size() >0){

                            //内容含有隐藏标签类型
                            LinkedHashMap<Integer,Boolean> hideTagTypeMap = new LinkedHashMap<Integer,Boolean>();//key:内容含有隐藏标签类型  10.输入密码可见  20.评论话题可见  30.达到等级可见 40.积分购买可见 50.余额购买可见  value:当前用户是否已对隐藏内容解锁

                            //允许可见的隐藏标签
                            List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);


                            for (HideTagType hideTagType : HideTagType.values()) {//按枚举类的顺序排序
                                for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                                    if(entry.getKey().equals(hideTagType.getName())){
                                        if(visibleTagList.contains(entry.getKey())){
                                            hideTagTypeMap.put(entry.getKey(), true);

                                        }else{
                                            hideTagTypeMap.put(entry.getKey(), false);
                                        }
                                        break;
                                    }
                                }
                            }
                            topic.setHideTagTypeMap(hideTagTypeMap);

                        }

                        topic.setIp(null);//IP地址不显示
                        topic.setContent(null);//话题内容不显示
                        topic.setMarkdownContent(null);//话题Markdown内容不显示
                        if(topic.getPostTime().equals(topic.getLastReplyTime())){//如果发贴时间等于回复时间，则不显示回复时间
                            topic.setLastReplyTime(null);
                        }

                        topic.setViewTotal(topic.getViewTotal()+topicComponent.readLocalView(topic.getId()));

                        if(!topic.getIsStaff()){//会员
                            userRoleNameMap.put(topic.getUserName(), null);
                        }
                        for(Tag tag: tagList){
                            if(topic.getTagId().equals(tag.getId())){
                                topic.setTagName(tag.getName());
                                tagRoleNameMap.put(tag.getId(), null);
                                userViewPermissionMap.put(tag.getId(), null);

                                break;
                            }
                        }

                        if(topic.getImage() != null && !topic.getImage().trim().isEmpty()){
                            List<ImageInfo> imageInfoList = jsonComponent.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
                            if(imageInfoList != null && imageInfoList.size() >0){
                                for(ImageInfo imageInfo : imageInfoList){
                                    imageInfo.setPath(fileComponent.fileServerAddress(request)+imageInfo.getPath());
                                }
                                topic.setImageInfoList(imageInfoList);
                            }
                        }

                    }
                }

                if(tagRoleNameMap.size() >0){
                    for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                        List<String> roleNameList = userRoleComponent.queryAllowViewTopicRoleName(entry.getKey(),request);
                        entry.setValue(roleNameList);
                    }
                }

                if(userRoleNameMap.size() >0){
                    for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                        List<String> roleNameList = userRoleComponent.queryUserRoleName(entry.getKey(),request);
                        entry.setValue(roleNameList);
                    }
                }
                if(userViewPermissionMap.size()>0){
                    for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                        //是否有当前功能操作权限
                        boolean flag = userRoleComponent.isPermission(ResourceEnum._1001000,entry.getKey());
                        entry.setValue(flag);
                    }
                }

                for(Topic topic : qr.getResultlist()){
                    if(!topic.getIsStaff()){//会员
                        User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                        if(user != null){
                            topic.setAccount(user.getAccount());
                            topic.setNickname(user.getNickname());
                            topic.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                            topic.setAvatarName(user.getAvatarName());

                            if(user.getCancelAccountTime() != -1L){//账号已注销
                                topic.setUserInfoStatus(-30);
                            }
                        }

                    }else{
                        SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(topic.getUserName());
                        if(sysUsers != null){
                            topic.setNickname(sysUsers.getNickname());
                            if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                                topic.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                                topic.setAvatarName(sysUsers.getAvatarName());
                            }
                        }
                        topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
                    }
                    //话题允许查看的角色名称集合
                    for (Map.Entry<Long, List<String>> entry : tagRoleNameMap.entrySet()) {
                        if(entry.getKey().equals(topic.getTagId())){
                            List<String> roleNameList = entry.getValue();
                            if(roleNameList != null && roleNameList.size() >0){
                                topic.setAllowRoleViewList(roleNameList);
                            }
                            break;
                        }

                    }
                    if(!topic.getIsStaff()){
                        //用户角色名称集合
                        for (Map.Entry<String, List<String>> entry : userRoleNameMap.entrySet()) {
                            if(entry.getKey().equals(topic.getUserName())){
                                List<String> roleNameList = entry.getValue();
                                if(roleNameList != null && roleNameList.size() >0){
                                    topic.setUserRoleNameList(roleNameList);
                                }
                                break;
                            }
                        }
                    }

                    //用户如果对话题项无查看权限，则不显示摘要、图片、视频
                    for (Map.Entry<Long,Boolean> entry : userViewPermissionMap.entrySet()) {
                        if(entry.getKey().equals(topic.getTagId())){
                            if(entry.getValue() != null && !entry.getValue()){
                                topic.setImage(null);
                                topic.setImageInfoList(new ArrayList<ImageInfo>());
                                topic.setSummary("");
                                topic.setMediaInfoList(new ArrayList<MediaInfo>());
                            }
                            break;
                        }

                    }


                }


                //非正常状态用户清除显示数据
                for(Topic topic : qr.getResultlist()){
                    if(topic.getUserInfoStatus() <0){
                        topic.setUserName(null);
                        topic.setAccount(null);
                        topic.setNickname(null);
                        topic.setAvatarPath(null);
                        topic.setAvatarName(null);
                        topic.setUserRoleNameList(new ArrayList<String>());
                        topic.setTitle("");
                        topic.setContent("");
                        topic.setMarkdownContent("");
                        topic.setSummary("");
                    }
                }

            }


            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
            return pageView;
        }
        return null;
    }

    /**
     * 获取相似话题
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    public List<Topic> getSimilarTopic(Long topicId,HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigSimilarTopic configSimilarTopic) {
            int maxResult = 10;
            //每页显示记录数
            if(configSimilarTopic.getMaxResult() != null && configSimilarTopic.getMaxResult() >0){
                maxResult = configSimilarTopic.getMaxResult();
            }

            if(topicId != null && topicId > 0L){
                return topicLuceneComponent.findLikeTopic(maxResult,topicId,20);
            }
        }
        return null;
    }
    /**
     * 获取热门话题
     * @param request 请求信息
     * @return
     */
    public List<Topic> getHotTopic(HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigHotTopic configHotTopic) {
            int maxResult = 10;
            //每页显示记录数
            if(configHotTopic.getMaxResult() != null && configHotTopic.getMaxResult() >0){
                maxResult = configHotTopic.getMaxResult();
            }

            return topicRepository.findHotTopic_cache(maxResult);
        }
        return null;
    }

    /**
     * 获取话题明细
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    public Topic getTopicDetail(Long topicId, HttpServletRequest request){
        if(topicId == null || topicId <= 0L){
            return null;
        }
        Topic topic = topicCacheManager.queryTopicCache(topicId);//查询缓存
        if(topic == null){
            return null;
        }

        String ip = IpAddress.getClientIpAddress(request);

        AccessUser accessUser = AccessUserThreadLocal.get();

        //检查权限
        userRoleComponent.checkPermission(ResourceEnum._1001000,topic.getTagId());
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        if(ip != null){
            topicComponent.addView(topicId, ip);
        }
        topic.setViewTotal(topic.getViewTotal()+topicComponent.readLocalView(topicId));



        //添加热门话题
        hotTopicComponent.addHotTopic(topic);

        if(topic.getStatus() >20){//20:已发布
            return null;

        }else{
            if(topic.getStatus().equals(10) ){
                if(accessUser == null){
                    return null;
                }else{
                    if(!accessUser.getUserName().equals(topic.getUserName())){
                        return null;
                    }
                }
            }
        }

        //只有中国地区简体中文语言才显示IP归属地
        if(accessUser != null && systemSetting.isShowIpAddress() && IpAddress.isChinaRegion(IpAddress.getClientIpAddress(request))){
            topic.setIpAddress(IpAddress.queryProvinceAddress(topic.getIp()));
        }
        topic.setIp(null);//IP地址不显示
        topic.setMarkdownContent(null);//话题Markdown内容不显示
        List<Tag> tagList = tagRepository.findAllTag_cache();
        if(tagList != null && tagList.size() >0){
            for(Tag tag :tagList){
                if(topic.getTagId().equals(tag.getId())){
                    topic.setTagName(tag.getName());

                    break;
                }

            }
        }
        User user = null;
        if(!topic.getIsStaff()){//会员
            user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
            if(user != null){
                topic.setAccount(user.getAccount());
                topic.setNickname(user.getNickname());
                topic.setAvatarPath(fileComponent.fileServerAddress(request)+user.getAvatarPath());
                topic.setAvatarName(user.getAvatarName());

                List<String> userRoleNameList = userRoleComponent.queryUserRoleName(user.getUserName(),request);
                if(userRoleNameList != null && userRoleNameList.size() >0){
                    topic.setUserRoleNameList(userRoleNameList);//用户角色名称集合
                }
                if(user.getCancelAccountTime() != -1L){//账号已注销
                    topic.setUserInfoStatus(-30);
                }
            }

        }else{
            SysUsers sysUsers = staffCacheManager.query_cache_findByUserAccount(topic.getUserName());
            if(sysUsers != null){
                topic.setNickname(sysUsers.getNickname());
                if(sysUsers.getAvatarName() != null && !sysUsers.getAvatarName().trim().isEmpty()){
                    topic.setAvatarPath(fileComponent.fileServerAddress(request)+sysUsers.getAvatarPath());
                    topic.setAvatarName(sysUsers.getAvatarName());
                }
            }
            topic.setAccount(topic.getUserName());//员工用户名和账号是同一个
        }

        List<String> topicRoleNameList = userRoleComponent.queryAllowViewTopicRoleName(topic.getTagId(),request);
        if(topicRoleNameList != null && topicRoleNameList.size() >0){
            topic.setAllowRoleViewList(topicRoleNameList);//话题允许查看的角色名称集合
        }




        //话题内容摘要MD5
        String topicContentDigest_link = "";
        String topicContentDigest_video = "";
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty()){
            //处理富文本路径
            topic.setContent(fileComponent.processRichTextFilePath(topic.getContent(),"topic"));
        }

        //处理文件防盗链
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty() && systemSetting.getFileSecureLinkSecret() != null && !"".equals(systemSetting.getFileSecureLinkSecret().trim())){
            List<String> serverAddressList = fileComponent.fileServerAllAddress(request);

            //前后端分离时，后端绑定多个主机名（域名或IP地址），前端请求不同主机名时，本模块会出现<a href >的情况；后端必须只绑定一个主机名才能避免错误，或者前端请求统一设置后端的主机名。例如Nuxt3的【headers.set('host' ,domain)】
            //解析上传的文件完整路径名称
            Map<String,String> analysisFullFileNameMap = topicCacheManager.query_cache_analysisFullFileName(topic.getContent(),topic.getId(),serverAddressList);

            if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){


                Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
                for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

                    newFullFileNameMap.put(entry.getKey(), WebUtil.getUrl(request)+ SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),topic.getTagId(),systemSetting.getFileSecureLinkSecret()));
                }

                Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topicId, ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数
                //生成处理'上传的文件完整路径名称'Id
                String processFullFileNameId = topicComponent.createProcessFullFileNameId(topicId,topicContentUpdateMark,newFullFileNameMap);


                topic.setContent(topicCacheManager.query_cache_processFullFileName(topic.getContent(),"topic",newFullFileNameMap,processFullFileNameId,serverAddressList));

                topicContentDigest_link = cms.utils.MD5.getMD5(processFullFileNameId);
            }


        }

        //处理视频播放器标签
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty()){
            Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topicId, ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

            //生成处理'视频播放器'Id
            String processVideoPlayerId = mediaProcessQueueComponent.createProcessVideoPlayerId(topicId,topicContentUpdateMark);

            //处理视频播放器标签
            String content = mediaProcessQueueCacheManager.query_cache_processVideoPlayer(WebUtil.getUrl(request),topic.getContent(),processVideoPlayerId+"|"+topicContentDigest_link,topic.getTagId(),systemSetting.getFileSecureLinkSecret());
            topic.setContent(content);

            topicContentDigest_video = cms.utils.MD5.getMD5(processVideoPlayerId);
        }

        //处理隐藏标签
        if(topic.getContent() != null && !topic.getContent().trim().isEmpty()){
            List<Integer> visibleTagList = this.getVisibleTagList(accessUser,topic);


            Integer topicContentUpdateMark = topicCacheManager.query_cache_markUpdateTopicStatus(topicId, ThreadLocalRandom.current().nextInt(10000000, 100000000));//8位随机数

            //生成处理'隐藏标签'Id
            String processHideTagId = topicComponent.createProcessHideTagId(topicId,topicContentUpdateMark, visibleTagList);

            //处理隐藏标签
            String content = topicCacheManager.query_cache_processHiddenTag(topic.getContent(),visibleTagList,processHideTagId+"|"+topicContentDigest_link+"|"+ topicContentDigest_video);

            //String content = textFilterManage.processHiddenTag(topic.getContent(),visibleTagList);
            topic.setContent(content);


        }
        //非正常状态用户清除显示数据
        if(topic.getUserInfoStatus() <0){
            topic.setUserName(null);
            topic.setAccount(null);
            topic.setNickname(null);
            topic.setAvatarPath(null);
            topic.setAvatarName(null);
            topic.setUserRoleNameList(new ArrayList<String>());
            topic.setTitle(null);
            topic.setContent(null);
            topic.setMarkdownContent(null);
            topic.setSummary(null);
        }

        return topic;
    }

    /**
     * 话题解锁
     * @param topicId 话题Id
     * @param hideType 隐藏类型
     * @param password 密码
     * @return
     */
    public Map<String,Object> topicUnhide(Long topicId,Integer hideType, String password){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("topicUnhide", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //统计每分钟原来提交次数
        Integer quantity = settingCacheManager.getSubmitQuantity("topicUnhide", accessUser.getUserName());
        if(quantity != null && quantity >30){//如果每分钟提交超过30次，则一分钟内不允许'取消隐藏'
            throw new BusinessException(Map.of("topicUnhide", "提交过于频繁，请稍后再提交"));
        }


        if(topicId == null || topicId <=0L){
            throw new BusinessException(Map.of("topicUnhide", "话题Id不能为空"));
        }
        Topic topic = topicCacheManager.queryTopicCache(topicId);//查询缓存
        if(topic == null){
            throw new BusinessException(Map.of("topicUnhide", "话题不存在"));
        }

        if(topic.getUserName().equals(accessUser.getUserName())){
            errors.put("topicUnhide", "不允许解锁自已发表的话题");
        }

        //话题取消隐藏Id
        String currentTopicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), hideType, topicId);

        TopicUnhide currentTopicUnhide = topicCacheManager.query_cache_findTopicUnhideById(currentTopicUnhideId);

        if(currentTopicUnhide != null){
            errors.put("topicUnhide", "当前话题已经取消隐藏");
        }

        //消费积分
        Long point = null;
        //消费金额
        BigDecimal amount = null;

        List<Integer> hideTypeList = new ArrayList<Integer>();
        hideTypeList.add(HideTagType.PASSWORD.getName());
        hideTypeList.add(HideTagType.POINT.getName());
        hideTypeList.add(HideTagType.AMOUNT.getName());

        if(!hideTypeList.contains(hideType)){
            errors.put("topicUnhide", "隐藏标签不存在");
        }


        if(errors.size() == 0){
            //解析隐藏标签
            Map<Integer,Object> analysisHiddenTagMap = textFilterComponent.analysisHiddenTag(topic.getContent());
            if(!analysisHiddenTagMap.containsKey(hideType)){
                errors.put("topicUnhide", "话题内容不含当前标签");
            }

            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                if(entry.getKey().equals(HideTagType.PASSWORD.getName()) && HideTagType.PASSWORD.getName().equals(hideType)){//输入密码可见
                    if(password == null || password.trim().isEmpty()){
                        errors.put("password", "密码不能为空");
                        break;
                    }


                    if(!entry.getValue().equals(password)){
                        errors.put("topicUnhide", "密码错误");
                    }
                    break;
                }

                if(entry.getKey().equals(HideTagType.POINT.getName()) && HideTagType.POINT.getName().equals(hideType)){//积分购买可见
                    //获取登录用户
                    User _user = userRepository.findUserByUserName(accessUser.getUserName());
                    if(_user != null){
                        if(_user.getPoint() < (Long)entry.getValue()){
                            errors.put("topicUnhide", "用户积分不足");
                        }else{
                            point = (Long)entry.getValue();
                        }
                    }else{
                        errors.put("topicUnhide", "用户不存在");
                    }

                }

                if(entry.getKey().equals(HideTagType.AMOUNT.getName()) && HideTagType.AMOUNT.getName().equals(hideType)){//余额购买可见
                    //获取登录用户
                    User _user = userRepository.findUserByUserName(accessUser.getUserName());
                    if(_user != null){
                        if(_user.getDeposit().compareTo((BigDecimal)entry.getValue()) <0){
                            errors.put("topicUnhide", "用户余额不足");
                        }else{
                            amount = (BigDecimal)entry.getValue();
                        }
                    }else{
                        errors.put("topicUnhide", "用户不存在");
                    }


                }

            }
        }

        //统计每分钟原来提交次数
        Integer original = settingCacheManager.getSubmitQuantity("topicUnhide", accessUser.getUserName());
        if(original != null){
            settingCacheManager.addSubmitQuantity("topicUnhide", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
        }else{
            settingCacheManager.addSubmitQuantity("topicUnhide", accessUser.getUserName(),1);//刷新每分钟原来提交次数
        }

        if(errors.size() == 0){
            LocalDateTime time = LocalDateTime.now();
            TopicUnhide topicUnhide = new TopicUnhide();
            String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), hideType, topicId);
            topicUnhide.setId(topicUnhideId);
            topicUnhide.setUserName(accessUser.getUserName());
            topicUnhide.setCancelTime(time);
            topicUnhide.setHideTagType(hideType);
            topicUnhide.setPostUserName(topic.getUserName());//发布话题的用户名称
            topicUnhide.setTopicId(topicId);

            //用户消费积分日志
            Object consumption_pointLogObject = null;
            //用户收入积分日志
            Object income_pointLogObject = null;
            if(point != null){
                topicUnhide.setPoint(point);

                PointLog pointLog = new PointLog();
                pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
                pointLog.setModule(400);//400.积分购买隐藏话题
                pointLog.setParameterId(topic.getId());//参数Id
                pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
                pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
                pointLog.setPointState(2);//2:账户支出
                pointLog.setPoint(point);//积分
                pointLog.setUserName(accessUser.getUserName());//用户名称
                pointLog.setRemark("");
                pointLog.setTimes(time);
                consumption_pointLogObject = pointComponent.createPointLogObject(pointLog);

                if(!topic.getIsStaff()){//如果是用户
                    User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                    PointLog income_pointLog = new PointLog();
                    income_pointLog.setId(pointLogConfig.createPointLogId(_user.getId()));
                    income_pointLog.setModule(400);//400.积分购买隐藏话题
                    income_pointLog.setParameterId(topic.getId());//参数Id
                    income_pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
                    income_pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称
                    income_pointLog.setPointState(1);//1:账户存入
                    income_pointLog.setPoint(point);//积分
                    income_pointLog.setUserName(topic.getUserName());//用户名称
                    income_pointLog.setRemark("");
                    income_pointLog.setTimes(time);
                    income_pointLogObject = pointComponent.createPointLogObject(income_pointLog);

                    //删除用户缓存
                    userCacheManager.delete_cache_findUserById(_user.getId());
                    userCacheManager.delete_cache_findUserByUserName(topic.getUserName());

                }

            }

            //用户消费金额日志
            Object consumption_paymentLogObject = null;
            //用户收入金额日志
            Object income_paymentLogObject = null;
            //平台分成
            TopicUnhidePlatformShare topicUnhidePlatformShare = null;

            //发布话题用户分成金额
            BigDecimal postUserNameShareAmount = new BigDecimal("0");
            //平台分成金额
            BigDecimal platformShareAmount = new BigDecimal("0");
            if(amount != null){
                topicUnhide.setAmount(amount);



                Integer topicUnhidePlatformShareProportion = settingRepository.findSystemSetting().getTopicUnhidePlatformShareProportion();//平台分成比例

                if(topicUnhidePlatformShareProportion >0){
                    //平台分成金额 = 总金额 * (平台分成比例 /100)
                    platformShareAmount = amount.multiply(new BigDecimal(String.valueOf(topicUnhidePlatformShareProportion)).divide(new BigDecimal("100")));

                    postUserNameShareAmount = amount.subtract(platformShareAmount);
                }else{
                    postUserNameShareAmount = amount;
                }


                PaymentLog paymentLog = new PaymentLog();
                paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(accessUser.getUserId()));//支付流水号
                paymentLog.setPaymentModule(70);//支付模块 7.余额购买话题隐藏内容
                paymentLog.setSourceParameterId(String.valueOf(topic.getId()));//参数Id
                paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
                paymentLog.setOperationUserName(accessUser.getUserName());//操作用户名称  0:系统  1: 员工  2:会员
                paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
                paymentLog.setAmount(amount);//金额
                paymentLog.setInterfaceProduct(0);//接口产品
                paymentLog.setUserName(accessUser.getUserName());//用户名称
                paymentLog.setTimes(time);
                consumption_paymentLogObject = paymentComponent.createPaymentLogObject(paymentLog);

                if(!topic.getIsStaff()){//如果是用户
                    User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                    String paymentRunningNumber = paymentComponent.createRunningNumber(_user.getId());//支付流水号
                    PaymentLog income_paymentLog = new PaymentLog();
                    income_paymentLog.setPaymentRunningNumber(paymentRunningNumber);//支付流水号
                    income_paymentLog.setPaymentModule(80);//支付模块 80.余额购买话题隐藏内容分成收入
                    income_paymentLog.setSourceParameterId(String.valueOf(topic.getId()));//参数Id
                    income_paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
                    income_paymentLog.setOperationUserName(accessUser.getUserName());//操作用户名称  0:系统  1: 员工  2:会员
                    income_paymentLog.setAmountState(1);//金额状态  1:账户存入  2:账户支出
                    income_paymentLog.setAmount(postUserNameShareAmount);//金额
                    income_paymentLog.setInterfaceProduct(0);//接口产品
                    income_paymentLog.setUserName(topic.getUserName());//用户名称
                    income_paymentLog.setTimes(time);
                    income_paymentLogObject = paymentComponent.createPaymentLogObject(income_paymentLog);

                    if(topicUnhidePlatformShareProportion >0){
                        //平台分成
                        topicUnhidePlatformShare = new TopicUnhidePlatformShare();
                        topicUnhidePlatformShare.setTopicId(topic.getId());//话题Id
                        topicUnhidePlatformShare.setStaff(topic.getIsStaff());//分成用户是否为员工
                        topicUnhidePlatformShare.setPostUserName(topic.getUserName());//发布话题的用户名称
                        topicUnhidePlatformShare.setUnlockUserName(accessUser.getUserName());//解锁话题的用户名称
                        topicUnhidePlatformShare.setPlatformShareProportion(topicUnhidePlatformShareProportion);//平台分成比例
                        topicUnhidePlatformShare.setPostUserShareRunningNumber(paymentRunningNumber);//发布话题的用户分成流水号
                        topicUnhidePlatformShare.setTotalAmount(amount);//总金额
                        topicUnhidePlatformShare.setShareAmount(platformShareAmount);//平台分成金额
                        topicUnhidePlatformShare.setUnlockTime(time);//解锁时间
                    }



                    //删除用户缓存
                    userCacheManager.delete_cache_findUserById(_user.getId());
                    userCacheManager.delete_cache_findUserByUserName(topic.getUserName());
                }
            }

            try {
                //保存'话题取消隐藏'
                topicRepository.saveTopicUnhide(topicComponent.createTopicUnhideObject(topicUnhide),hideType,
                        point,accessUser.getUserName(),consumption_pointLogObject,topic.getUserName(),income_pointLogObject,
                        amount,postUserNameShareAmount,consumption_paymentLogObject,income_paymentLogObject,topicUnhidePlatformShare);

                //删除'话题取消隐藏'缓存;
                topicCacheManager.delete_cache_findTopicUnhideById(topicUnhideId);

                //删除用户缓存
                userCacheManager.delete_cache_findUserById(accessUser.getUserId());
                userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());



                User _user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());

                //别人解锁了我的话题  只对(隐藏标签类型 10:输入密码可见  40:积分购买可见  50:余额购买可见)发提醒
                if(!topic.getIsStaff() && _user != null && !topic.getUserName().equals(accessUser.getUserName())){//不发提醒给自己

                    //提醒楼主
                    Remind remind = new Remind();
                    remind.setId(remindConfig.createRemindId(_user.getId()));
                    remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                    remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                    remind.setTypeCode(60);//60:别人解锁了我的话题
                    remind.setSendTimeFormat(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());//发送时间格式化
                    remind.setTopicId(topic.getId());//话题Id

                    Object remind_object = remindComponent.createRemindObject(remind);
                    remindRepository.saveRemind(remind_object);

                    //删除提醒缓存
                    remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());


                }


                //异步执行会员卡赠送任务(长期任务类型)
                membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(accessUser.getUserName());





            } catch (org.springframework.orm.jpa.JpaSystemException e) {
                errors.put("topicUnhide", "话题重复取消隐藏");

            }

        }

        if(!errors.isEmpty()){
            returnValue.put("success", false);
            returnValue.put("error", errors);

        }else{
            returnValue.put("success", true);
        }

        return returnValue;
    }


    /**
     * 允许可见的隐藏标签
     * @param accessUser 访问用户
     * @param topic 话题
     * @return
     */
    private List<Integer> getVisibleTagList(AccessUser accessUser,Topic topic){
        //允许可见的隐藏标签
        List<Integer> visibleTagList = new ArrayList<Integer>();
        if(accessUser != null){
            //如果话题由当前用户发表，则显示全部隐藏内容
            if(!topic.getIsStaff() && topic.getUserName().equals(accessUser.getUserName())){
                visibleTagList.add(HideTagType.PASSWORD.getName());
                visibleTagList.add(HideTagType.COMMENT.getName());
                visibleTagList.add(HideTagType.GRADE.getName());
                visibleTagList.add(HideTagType.POINT.getName());
                visibleTagList.add(HideTagType.AMOUNT.getName());
            }else{
                //解析隐藏标签
                Map<Integer,Object> analysisHiddenTagMap = topicCacheManager.query_cache_analysisHiddenTag(topic.getContent(),topic.getId());
                for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {

                    if(entry.getKey().equals(HideTagType.PASSWORD.getName())){//输入密码可见
                        //话题取消隐藏Id
                        String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), HideTagType.PASSWORD.getName(), topic.getId());

                        TopicUnhide topicUnhide = topicCacheManager.query_cache_findTopicUnhideById(topicUnhideId);

                        if(topicUnhide != null){
                            visibleTagList.add(HideTagType.PASSWORD.getName());//当前话题已经取消隐藏
                        }
                    }else if(entry.getKey().equals(HideTagType.COMMENT.getName())){//评论话题可见
                        Boolean isUnhide = topicCacheManager.query_cache_findWhetherCommentTopic(topic.getId(),accessUser.getUserName());
                        if(isUnhide){
                            visibleTagList.add(HideTagType.COMMENT.getName());//当前话题已经取消隐藏
                        }
                    }else if(entry.getKey().equals(HideTagType.GRADE.getName())){//超过等级可见
                        User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
                        if(_user != null){
                            List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();//取得用户所有等级
                            if(userGradeList != null && userGradeList.size() >0){
                                for(UserGrade userGrade : userGradeList){
                                    if(_user.getPoint() >= userGrade.getNeedPoint() && (Long)entry.getValue() <=userGrade.getNeedPoint()){
                                        visibleTagList.add(HideTagType.GRADE.getName());//当前话题已经取消隐藏

                                        break;
                                    }
                                }


                            }
                        }

                    }else if(entry.getKey().equals(HideTagType.POINT.getName())){//积分购买可见
                        //话题取消隐藏Id
                        String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), HideTagType.POINT.getName(), topic.getId());

                        TopicUnhide topicUnhide = topicCacheManager.query_cache_findTopicUnhideById(topicUnhideId);

                        if(topicUnhide != null){
                            visibleTagList.add(HideTagType.POINT.getName());//当前话题已经取消隐藏
                        }
                    }else if(entry.getKey().equals(HideTagType.AMOUNT.getName())){//余额购买可见
                        //话题取消隐藏Id
                        String topicUnhideId = topicUnhideConfig.createTopicUnhideId(accessUser.getUserId(), HideTagType.AMOUNT.getName(), topic.getId());
                        TopicUnhide topicUnhide = topicCacheManager.query_cache_findTopicUnhideById(topicUnhideId);

                        if(topicUnhide != null){
                            visibleTagList.add(HideTagType.AMOUNT.getName());//当前话题已经取消隐藏
                        }
                    }

                }
            }
        }
        return visibleTagList;
    }

    /**
     * 获取添加话题界面信息
     * @return
     */
    public Map<String,Object> getAddTopicViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.topic_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());//是否有验证码
            }

            User user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(user != null){
                returnValue.put("deposit",user.getDeposit());//用户共有预存款
            }
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        //如果全局不允许提交话题
        if(!systemSetting.isAllowTopic()){
            returnValue.put("allowTopic",false);//不允许提交话题
        }else{
            returnValue.put("allowTopic",true);//允许提交话题
        }

        //有提交权限的标签Id
        List<Long> allowTagIdList = new ArrayList<Long>();

        List<Tag> tagList = tagRepository.findAllTag_cache();
        if(tagList != null && tagList.size() >0){
            for(Tag tag : tagList){
                boolean flag = userRoleComponent.isPermission(ResourceEnum._1002000, tag.getId());
                if(flag){
                    allowTagIdList.add(tag.getId());
                }
            }
        }

        returnValue.put("maxVoteOptions", systemSetting.getTopicMaxVoteOptions());//发起投票选项数量

        returnValue.put("allowTagIdList", allowTagIdList);//有提交权限的标签Id
        returnValue.put("giveRedEnvelopeAmountMin",systemSetting.getGiveRedEnvelopeAmountMin());
        returnValue.put("giveRedEnvelopeAmountMax",systemSetting.getGiveRedEnvelopeAmountMax());

        returnValue.put("availableTag", topicComponent.availableTag());//话题编辑器允许使用标签
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();
        returnValue.put("userGradeList", jsonComponent.toJSONString(userGradeList));

        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }


    /**
     * 添加话题
     * @param topicDTO 话题表单
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> addTopic(TopicDTO topicDTO, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();


        if(topicDTO.getTagId() != null){
            //是否有当前功能操作权限
            userRoleComponent.checkPermission(ResourceEnum._1002000,topicDTO.getTagId());

        }

        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("topic", "只读模式不允许提交数据"));
        }



        //验证码
        boolean isCaptcha = captchaComponent.topic_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(topicDTO.getCaptchaKey(), topicDTO.getCaptchaValue(), errors);
        }

        //如果全局不允许提交话题
        if(!systemSetting.isAllowTopic()){
            throw new BusinessException(Map.of("topic", "不允许提交话题"));
        }

        //如果实名用户才允许提交话题
        if(systemSetting.isRealNameUserAllowTopic()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                throw new BusinessException(Map.of("topic", "实名用户才允许提交话题"));
            }

        }
        //红包总金额
        BigDecimal redEnvelope_totalAmount = new BigDecimal("0.00");
        //单个红包金额
        BigDecimal redEnvelope_singleAmount = new BigDecimal("0.00");
        //红包发放数量
        Integer redEnvelope_giveQuantity = 0;


        User user = userRepository.findUserByUserName(accessUser.getUserName());//查询用户数据
        //红包
        if(topicDTO.getType() != null && topicDTO.getType() >0 && user != null){
            if(topicDTO.getType().equals(20)){//红包类型 随机金额
                if(topicDTO.getTotalAmount() != null && !topicDTO.getTotalAmount().trim().isEmpty()){

                    if(topicDTO.getTotalAmount().trim().length()>12){
                        errors.put("totalAmount", "不能超过12位数字");
                    }else{
                        boolean amountVerification = topicDTO.getTotalAmount().trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
                        if(amountVerification){
                            BigDecimal _totalAmount = new BigDecimal(topicDTO.getTotalAmount().trim());
                            if(_totalAmount.compareTo(user.getDeposit()) >0){
                                errors.put("totalAmount", "不能大于账户预存款");

                            }
                            if(_totalAmount.compareTo(new BigDecimal("0")) <0){
                                errors.put("totalAmount","不能小于0");
                            }
                            if(systemSetting.getGiveRedEnvelopeAmountMax() != null ){
                                if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("0")) ==0){
                                    errors.put("redEnvelopeLimit","不允许发红包");

                                }else if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("0")) >0){
                                    if(_totalAmount.compareTo(systemSetting.getGiveRedEnvelopeAmountMin()) <0){
                                        errors.put("redEnvelopeLimit","不能小于发红包总金额下限");
                                    }
                                    if(_totalAmount.compareTo(systemSetting.getGiveRedEnvelopeAmountMax()) >0){
                                        errors.put("redEnvelopeLimit","不能大于发红包总金额上限");
                                    }
                                }
                            }else{
                                if(_totalAmount.compareTo(systemSetting.getGiveRedEnvelopeAmountMin()) <0){
                                    errors.put("redEnvelopeLimit","不能小于发红包总金额下限");
                                }
                            }



                            if(errors.size() ==0){
                                redEnvelope_totalAmount = _totalAmount;
                            }
                        }else{
                            errors.put("totalAmount", "请填写总金额");
                        }
                    }
                }else{
                    errors.put("totalAmount","总金额不能为空");
                }
                if(topicDTO.getGiveQuantity() != null && !topicDTO.getGiveQuantity().trim().isEmpty()){
                    if(topicDTO.getGiveQuantity().trim().length()>3){
                        errors.put("giveQuantity", "不能超过3位数字");
                    }else{
                        boolean verification = Verification.isPositiveInteger(topicDTO.getGiveQuantity().trim());//正整数
                        if(verification){
                            Integer _giveQuantity =Integer.parseInt(topicDTO.getGiveQuantity().trim());


                            if(errors.size() ==0){
                                redEnvelope_giveQuantity = _giveQuantity;
                            }
                        }else{
                            errors.put("giveQuantity","请填写正整数");
                        }
                    }
                }else{
                    errors.put("giveQuantity","不能为空");
                }

                if(errors.size() ==0){
                    //按单个红包金额为0.01计算总金额
                    BigDecimal total = new BigDecimal("0.01").multiply(new BigDecimal(String.valueOf(redEnvelope_giveQuantity)));//最低单个红包金额为0.01元

                    if(total.compareTo(redEnvelope_totalAmount) >0){
                        errors.put("giveQuantity","拆分后最低单个红包金额不足0.01元");
                    }
                }


            }else if(topicDTO.getType().equals(30)){//红包类型 固定金额
                if(topicDTO.getSingleAmount() != null && !topicDTO.getSingleAmount().trim().isEmpty()){

                    if(topicDTO.getSingleAmount().trim().length()>12){
                        errors.put("singleAmount", "不能超过12位数字");
                    }else{
                        boolean amountVerification = topicDTO.getSingleAmount().trim().matches("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");//金额
                        if(amountVerification){
                            BigDecimal _singleAmount = new BigDecimal(topicDTO.getSingleAmount().trim());
                            if(_singleAmount.compareTo(new BigDecimal("0.01")) <0){
                                errors.put("singleAmount","不能小于0.01元");
                            }
                            if(errors.size() ==0){
                                redEnvelope_singleAmount = _singleAmount;
                            }
                        }else{
                            errors.put("singleAmount", "请填写货币格式");
                        }
                    }
                }else{
                    errors.put("singleAmount", "金额不能为空");
                }

                if(topicDTO.getGiveQuantity() != null && !topicDTO.getGiveQuantity().trim().isEmpty()){
                    if(topicDTO.getGiveQuantity().trim().length()>3){
                        errors.put("giveQuantity", "不能超过3位数字");
                    }else{
                        boolean verification = Verification.isPositiveInteger(topicDTO.getGiveQuantity().trim());//正整数
                        if(verification){
                            Integer _giveQuantity =Integer.parseInt(topicDTO.getGiveQuantity().trim());
                            //计算总金额
                            BigDecimal total = redEnvelope_singleAmount.multiply(new BigDecimal(String.valueOf(_giveQuantity)));



                            if(systemSetting.getGiveRedEnvelopeAmountMax() != null ){
                                if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("0")) ==0){
                                    errors.put("redEnvelopeLimit","不允许发红包");

                                }else if(systemSetting.getGiveRedEnvelopeAmountMax().compareTo(new BigDecimal("0")) >0){
                                    if(total.compareTo(systemSetting.getGiveRedEnvelopeAmountMin()) <0){
                                        errors.put("redEnvelopeLimit","不能小于发红包总金额下限");
                                    }
                                    if(total.compareTo(systemSetting.getGiveRedEnvelopeAmountMax()) >0){
                                        errors.put("redEnvelopeLimit","不能大于发红包总金额上限");
                                    }
                                }
                            }else{
                                if(total.compareTo(systemSetting.getGiveRedEnvelopeAmountMin()) <0){
                                    errors.put("redEnvelopeLimit","不能小于发红包总金额下限");
                                }
                            }

                            if(errors.size() ==0){
                                redEnvelope_giveQuantity = _giveQuantity;
                                redEnvelope_totalAmount = total;
                            }
                        }else{
                            errors.put("giveQuantity","请填写正整数");
                        }
                    }

                }else{
                    errors.put("giveQuantity","不能为空");

                }
            }
        }


        //图片地址
        List<ImageInfo> imageList = new ArrayList<ImageInfo>();
        Topic topic = new Topic();
        VoteTheme voteTheme = null;
        List<String> optionTextList = new ArrayList<String>();
        LocalDateTime voteEndDate = null;
        LocalDateTime currentTime = LocalDateTime.now();//当前时间

        //前台发表话题审核状态
        if(systemSetting.getTopic_review().equals(10)){//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
            topic.setStatus(10);//10.待审核
        }else if(systemSetting.getTopic_review().equals(30)){
            if(topicDTO.getTagId() != null && topicDTO.getTagId() >0L){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1006000,topicDTO.getTagId());
                if(flag_permission){
                    topic.setStatus(20);//20.已发布
                }else{
                    topic.setStatus(10);//10.待审核
                }
            }
        }else{
            topic.setStatus(20);//20.已发布
        }


        if(topicDTO.getTagId() == null || topicDTO.getTagId() <=0L){
            errors.put("tagId", "标签不能为空");
        }else{
            Tag tag = tagRepository.findById_cache(topicDTO.getTagId());
            if(tag != null && tag.getChildNodeNumber()==0){
                topic.setTagId(tag.getId());
                topic.setTagName(tag.getName());
                topic.setTagIdGroup(tag.getParentIdGroup()+tag.getId()+",");
            }else{
                errors.put("tagId", "标签不存在");
            }
        }
        if(topicDTO.getTitle() != null && !topicDTO.getTitle().trim().isEmpty()){
            if(systemSetting.isAllowFilterWord()){
                String wordReplace = "";
                if(systemSetting.getFilterWordReplace() != null){
                    wordReplace = systemSetting.getFilterWordReplace();
                }
                topic.setTitle(sensitiveWordFilterComponent.filterSensitiveWord(topicDTO.getTitle(), wordReplace));
            }

            topic.setTitle(topicDTO.getTitle());
            if(topicDTO.getTitle().length() >150){
                errors.put("title", "不能大于150个字符");
            }
        }else{
            errors.put("title", "标题不能为空");
        }

        if (topicDTO.getIsMarkdown() != null && topicDTO.getIsMarkdown()) {
            if(systemSetting.getSupportEditor().equals(10)){
                errors.put("topic", "不支持此编辑器");
            }
        } else {
            if(systemSetting.getSupportEditor().equals(20)){
                errors.put("topic", "不支持此编辑器");
            }
        }



        EditorTag editorTag = settingComponent.readTopicEditorTag();
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();

        // 处理内容过滤和文件路径
        TopicClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, topicDTO.getContent(), topicDTO.getIsMarkdown(), topicDTO.getMarkdownContent(),editorTag);

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            errors.put("content", "内容不能为空");
        }

        //风控文本
        String riskControlText = "";
        if(errors.isEmpty()){


            String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();


            //校正隐藏标签
            String validValue =  textFilterComponent.correctionHiddenTag(value,userGradeList);
            riskControlText = validValue;
            //允许使用的隐藏标签
            List<Integer> allowHiddenTagList = new ArrayList<Integer>();
            if(editorTag.isHidePassword()){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1020000,topic.getTagId());
                if(flag_permission){
                    //输入密码可见
                    allowHiddenTagList.add(HideTagType.PASSWORD.getName());
                }
            }
            if(editorTag.isHideComment()){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1021000,topic.getTagId());
                if(flag_permission){
                    //评论话题可见
                    allowHiddenTagList.add(HideTagType.COMMENT.getName());
                }
            }
            if(editorTag.isHideGrade()){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1022000,topic.getTagId());
                if(flag_permission){
                    //达到等级可见
                    allowHiddenTagList.add(HideTagType.GRADE.getName());
                }
            }
            if(editorTag.isHidePoint()){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1023000,topic.getTagId());
                if(flag_permission){
                    //积分购买可见
                    allowHiddenTagList.add(HideTagType.POINT.getName());
                }
            }
            if(editorTag.isHideAmount()){
                //是否有当前功能操作权限
                boolean flag_permission = userRoleComponent.isPermission(ResourceEnum._1024000,topic.getTagId());
                if(flag_permission){
                    //余额购买可见
                    allowHiddenTagList.add(HideTagType.AMOUNT.getName());
                }


            }

            //解析隐藏标签
            Map<Integer,Object> analysisHiddenTagMap = textFilterComponent.analysisHiddenTag(validValue);
            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                if(!allowHiddenTagList.contains(entry.getKey())){
                    errors.put("content", "发表话题不允许使用 '"+ HideTagName.getKey(entry.getKey())+"' 隐藏标签");//隐藏标签
                    break;
                }
            }



            //删除隐藏标签
            String new_content = textFilterComponent.deleteHiddenTag(value);

            //不含标签内容
            String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(new_content));

            //清除空格&nbsp;
            String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

            //摘要
            if(!trimSpace.isEmpty()){
                if(systemSetting.isAllowFilterWord()){
                    String wordReplace = "";
                    if(systemSetting.getFilterWordReplace() != null){
                        wordReplace = systemSetting.getFilterWordReplace();
                    }
                    trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                }
                if(trimSpace.length() >180){
                    topic.setSummary(trimSpace.substring(0, 180)+"..");
                }else{
                    topic.setSummary(trimSpace+"..");
                }
            }

            //不含标签内容
            String source_text = textFilterComponent.filterText(htmlFilterResult.content);
            //清除空格&nbsp;
            String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();

            if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                    && !htmlFilterResult.htmlProcessingResult.isHasFile()
                    && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                    && !htmlFilterResult.htmlProcessingResult.isHasMap()
                    && (source_text.trim().isEmpty() || source_trimSpace.isEmpty())){
                errors.put("content", "内容不能为空");
            }else{
                if(systemSetting.isAllowFilterWord()){
                    String wordReplace = "";
                    if(systemSetting.getFilterWordReplace() != null){
                        wordReplace = systemSetting.getFilterWordReplace();
                    }
                    validValue = sensitiveWordFilterComponent.filterSensitiveWord(validValue, wordReplace);
                }

                topic.setContent(validValue);
            }


            //非隐藏标签内图片
            List<String> other_imageNameList = textFilterComponent.readImageName(new_content,"topic");


            if(other_imageNameList != null && other_imageNameList.size() >0){
                for(int i=0; i<other_imageNameList.size(); i++){
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setName(FileUtil.getName(other_imageNameList.get(i)));
                    imageInfo.setPath(FileUtil.getFullPath(other_imageNameList.get(i)));

                    imageList.add(imageInfo);

                }
            }

        }



        if(errors.isEmpty()){


            topic.setPostTime(currentTime);
            topic.setLastReplyTime(currentTime);
            topic.setIp(IpAddress.getClientIpAddress(request));
            topic.setUserName(accessUser.getUserName());
            topic.setIsStaff(false);
            topic.setIsMarkdown(topicDTO.getIsMarkdown() != null && topicDTO.getIsMarkdown());
            topic.setMarkdownContent(htmlFilterResult.formatterMarkdown);
            topic.setImage(jsonComponent.toJSONString(imageList));



            //发红包金额日志
            PaymentLog giveRedEnvelope_paymentLog = null;
            GiveRedEnvelope giveRedEnvelope = null;
            if(redEnvelope_totalAmount.compareTo(new BigDecimal("0")) >0 && redEnvelope_giveQuantity >0){
                if(topicDTO.getType().equals(20)){//红包类型 20.公共随机红包(随机金额)
                    giveRedEnvelope = new GiveRedEnvelope();

                    giveRedEnvelope.setId(UUIDUtil.getUUID32());
                    giveRedEnvelope.setUserId(user.getId());
                    giveRedEnvelope.setType(topicDTO.getType());//类型
                    giveRedEnvelope.setTotalAmount(redEnvelope_totalAmount);//红包总金额

                    giveRedEnvelope.setGiveQuantity(redEnvelope_giveQuantity);//红包发放数量
                    giveRedEnvelope.setRemainingQuantity(redEnvelope_giveQuantity);//剩余数量
                    //	giveRedEnvelope.setBindTopicId(topic.getId());/插入话题表后设置
                    giveRedEnvelope.setGiveTime(currentTime);
                    giveRedEnvelope.setDistributionAmountGroup(jsonComponent.toJSONString( redEnvelopeComponent.createRedEnvelopeAmount(redEnvelope_totalAmount,redEnvelope_giveQuantity)));


                }else if(topicDTO.getType().equals(30)){//红包类型 30.公共定额红包(固定金额)
                    giveRedEnvelope = new GiveRedEnvelope();
                    giveRedEnvelope.setId(UUIDUtil.getUUID32());
                    giveRedEnvelope.setUserId(user.getId());
                    giveRedEnvelope.setType(topicDTO.getType());//类型
                    giveRedEnvelope.setTotalAmount(redEnvelope_totalAmount);//红包总金额
                    giveRedEnvelope.setSingleAmount(redEnvelope_singleAmount);//单个红包金额
                    giveRedEnvelope.setGiveQuantity(redEnvelope_giveQuantity);//红包发放数量
                    giveRedEnvelope.setRemainingQuantity(redEnvelope_giveQuantity);//剩余数量
                    //	giveRedEnvelope.setBindTopicId(topic.getId());//插入话题表后设置
                    giveRedEnvelope.setGiveTime(currentTime);
                }
            }
            if(giveRedEnvelope != null){
                giveRedEnvelope_paymentLog = new PaymentLog();
                giveRedEnvelope_paymentLog.setPaymentRunningNumber(paymentComponent.createRunningNumber(accessUser.getUserId()));//支付流水号
                giveRedEnvelope_paymentLog.setPaymentModule(120);//支付模块 120.发红包
                giveRedEnvelope_paymentLog.setSourceParameterId(giveRedEnvelope.getId());//参数Id
                giveRedEnvelope_paymentLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
                giveRedEnvelope_paymentLog.setOperationUserName(accessUser.getUserName());//操作用户名称  0:系统  1: 员工  2:会员
                giveRedEnvelope_paymentLog.setAmountState(2);//金额状态  1:账户存入  2:账户支出
                giveRedEnvelope_paymentLog.setAmount(redEnvelope_totalAmount);//金额
                giveRedEnvelope_paymentLog.setInterfaceProduct(0);//接口产品
                giveRedEnvelope_paymentLog.setUserName(accessUser.getUserName());//用户名称
                giveRedEnvelope_paymentLog.setTimes(currentTime);

                topic.setGiveRedEnvelopeId(giveRedEnvelope.getId());//发红包Id
            }


            //保存话题
            topicRepository.saveTopic(topic,giveRedEnvelope, user.getUserName(), redEnvelope_totalAmount, giveRedEnvelope_paymentLog,voteTheme);



            //更新索引
            topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),1));



            PointLog pointLog = new PointLog();
            pointLog.setId(pointLogConfig.createPointLogId(accessUser.getUserId()));
            pointLog.setModule(100);//100.话题
            pointLog.setParameterId(topic.getId());//参数Id
            pointLog.setOperationUserType(2);//操作用户类型  0:系统  1: 员工  2:会员
            pointLog.setOperationUserName(accessUser.getUserName());//操作用户名称

            pointLog.setPoint(systemSetting.getTopic_rewardPoint());//积分
            pointLog.setUserName(accessUser.getUserName());//用户名称
            pointLog.setRemark("");
            //增加用户积分
            userRepository.addUserPoint(accessUser.getUserName(),systemSetting.getTopic_rewardPoint(), pointComponent.createPointLogObject(pointLog));


            //用户动态
            UserDynamic userDynamic = new UserDynamic();
            userDynamic.setId(userDynamicConfig.createUserDynamicId(accessUser.getUserId()));
            userDynamic.setUserName(accessUser.getUserName());
            userDynamic.setModule(100);//模块 100.话题
            userDynamic.setTopicId(topic.getId());
            userDynamic.setPostTime(topic.getPostTime());
            userDynamic.setStatus(topic.getStatus());
            userDynamic.setFunctionIdGroup(","+topic.getId()+",");
            Object new_userDynamic = userDynamicComponent.createUserDynamicObject(userDynamic);
            userRepository.saveUserDynamic(new_userDynamic);

            if(htmlFilterResult.htmlProcessingResult.getMentionUserNameList() != null && !htmlFilterResult.htmlProcessingResult.getMentionUserNameList().isEmpty()){
                int count = 0;
                for(String mentionUserName : htmlFilterResult.htmlProcessingResult.getMentionUserNameList()){
                    count++;
                    if(systemSetting.getAllowMentionMaxNumber() != null && count > systemSetting.getAllowMentionMaxNumber()){
                        break;
                    }

                    User _user = userCacheManager.query_cache_findUserByUserName(mentionUserName);
                    if(_user != null && !mentionUserName.equals(accessUser.getUserName())){//不发提醒给自己
                        //提醒楼主
                        Remind remind = new Remind();
                        remind.setId(remindConfig.createRemindId(_user.getId()));
                        remind.setReceiverUserId(_user.getId());//接收提醒用户Id
                        remind.setSenderUserId(accessUser.getUserId());//发送用户Id
                        remind.setTypeCode(200);// 200:别人在话题提到我
                        remind.setSendTimeFormat(new Date().getTime());//发送时间格式化
                        remind.setTopicId(topic.getId());//话题Id

                        Object remind_object = remindComponent.createRemindObject(remind);
                        remindRepository.saveRemind(remind_object);

                        //删除提醒缓存
                        remindCacheManager.delete_cache_findUnreadRemindByUserId(_user.getId());


                    }
                }
            }

            //删除缓存
            userCacheManager.delete_cache_findUserById(accessUser.getUserId());
            userCacheManager.delete_cache_findUserByUserName(accessUser.getUserName());
            followCacheManager.delete_cache_userUpdateFlag(accessUser.getUserName());
            topicCacheManager.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态
            String fileNumber = "b"+ accessUser.getUserId();





            //异步执行会员卡赠送任务(长期任务类型)
            membershipCardGiftTaskComponent.async_triggerMembershipCardGiftTask(user.getUserName());


            //删除图片锁
            if(htmlFilterResult.htmlProcessingResult().getImageNameList() != null && !htmlFilterResult.htmlProcessingResult().getImageNameList().isEmpty()){
                for(String imageName :htmlFilterResult.htmlProcessingResult().getImageNameList()){

                    if(imageName != null && !imageName.trim().isEmpty()){

                        //如果验证不是当前用户上传的文件，则不删除锁
                        if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                            continue;
                        }

                        fileComponent.deleteLock("file"+ File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                    }
                }
            }
            //音视频
            if(htmlFilterResult.htmlProcessingResult().getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult().getMediaNameList().isEmpty()){
                for(String mediaName :htmlFilterResult.htmlProcessingResult().getMediaNameList()){
                    if(mediaName != null && !mediaName.trim().isEmpty()){
                        //如果验证不是当前用户上传的文件，则不删除锁
                        if(!topicComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                            continue;
                        }
                        fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                    }
                }
            }
            //文件
            if(htmlFilterResult.htmlProcessingResult().getFileNameList() != null && !htmlFilterResult.htmlProcessingResult().getFileNameList().isEmpty()){
                for(String fileName :htmlFilterResult.htmlProcessingResult().getFileNameList()){
                    if(fileName != null && !fileName.trim().isEmpty()){
                        //如果验证不是当前用户上传的文件，则不删除锁
                        if(!topicComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                            continue;
                        }
                        fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                    }
                }
            }

            List<Thumbnail> thumbnailList = thumbnailRepository.findAllThumbnail_cache();
            if(thumbnailList != null && !thumbnailList.isEmpty()){
                //异步增加缩略图
                if(!imageList.isEmpty()){
                    fileComponent.addThumbnail(thumbnailList,imageList);
                }
            }

            //统计每分钟原来提交次数
            Integer original = settingCacheManager.getSubmitQuantity("topic", accessUser.getUserName());
            if(original != null){
                settingCacheManager.addSubmitQuantity("topic", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
            }else{
                settingCacheManager.addSubmitQuantity("topic", accessUser.getUserName(),1);//刷新每分钟原来提交次数
            }

        }



        if(!errors.isEmpty()){
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
     * 获取修改话题界面信息
     * @param topicId 话题Id
     * @return
     */
    public Map<String,Object> getEditTopicViewModel(Long topicId){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        AccessUser accessUser = AccessUserThreadLocal.get();
        if(accessUser != null){
            boolean captchaKey = captchaComponent.topic_isCaptcha(accessUser.getUserName());//验证码标记
            if(captchaKey){
                returnValue.put("captchaKey", UUIDUtil.getUUID32());//是否有验证码
            }

            if(topicId != null && topicId >0L){
                Topic topic = topicCacheManager.queryTopicCache(topicId);//查询缓存
                if(topic != null && topic.getStatus() <100 && topic.getUserName().equals(accessUser.getUserName())) {
                    topic.setIp(null);//IP地址不显示


                    if (topic.getContent() != null && !topic.getContent().trim().isEmpty()) {
                        //处理富文本路径
                        topic.setContent(fileComponent.processRichTextFilePath(topic.getContent(), "topic"));
                    }

                    List<Tag> tagList = tagRepository.findAllTag_cache();
                    if (tagList != null && tagList.size() > 0) {
                        for (Tag tag : tagList) {
                            if (topic.getTagId().equals(tag.getId())) {
                                topic.setTagName(tag.getName());
                                break;
                            }

                        }
                    }

                    List<String> topicRoleNameList = userRoleComponent.queryAllowViewTopicRoleName(topic.getTagId());
                    if (topicRoleNameList != null && topicRoleNameList.size() > 0) {
                        topic.setAllowRoleViewList(topicRoleNameList);//话题允许查看的角色名称集合
                    }



                    returnValue.put("topic", topic);
                }
            }
        }
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();

        returnValue.put("maxVoteOptions", systemSetting.getTopicMaxVoteOptions());//发起投票选项数量


        //如果全局不允许提交话题
        if(!systemSetting.isAllowTopic()){
            returnValue.put("allowTopic",false);//不允许提交话题
        }else{
            returnValue.put("allowTopic",true);//允许提交话题
        }
        returnValue.put("availableTag", topicComponent.availableTag());//话题编辑器允许使用标签
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();
        returnValue.put("userGradeList", jsonComponent.toJSONString(userGradeList));
        returnValue.put("fileSystem", fileComponent.getFileSystem());
        returnValue.put("supportEditor", systemSetting.getSupportEditor());
        return returnValue;
    }

    /**
     * 修改话题
     * @param topicDTO 话题表单
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> editTopic(TopicDTO topicDTO, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        Map<String,String> errors = new HashMap<String,String>();

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        if(topicDTO.getTopicId() == null || topicDTO.getTopicId() <=0L){
            throw new BusinessException(Map.of("topic", "话题Id不能为空"));

        }



        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("topic", "只读模式不允许提交数据"));
        }



        //验证码
        boolean isCaptcha = captchaComponent.topic_isCaptcha(accessUser.getUserName());
        if(isCaptcha) {//如果需要验证码
            //校验验证码
            captchaComponent.validateCaptcha(topicDTO.getCaptchaKey(), topicDTO.getCaptchaValue(), errors);
        }


        Topic topic = topicRepository.findById(topicDTO.getTopicId());
        if(topic == null){
            throw new BusinessException(Map.of("topic", "话题不存在"));
        }



        //是否有当前功能操作权限
        userRoleComponent.checkPermission(ResourceEnum._1003000,topic.getTagId());

        if(!topic.getUserName().equals(accessUser.getUserName())){
            throw new BusinessException(Map.of("topic", "只允许修改自己发布的话题"));
        }
        if(topic.getStatus() > 100){
            throw new BusinessException(Map.of("topic", "话题已删除"));
        }

        //如果全局不允许提交话题
        if(!systemSetting.isAllowTopic()){
            throw new BusinessException(Map.of("topic", "不允许提交话题"));
        }

        //如果实名用户才允许提交话题
        if(systemSetting.isRealNameUserAllowTopic()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                throw new BusinessException(Map.of("topic", "实名用户才允许提交话题"));
            }

        }

        //旧图片地址
        List<ImageInfo> oldImageList = new ArrayList<ImageInfo>();
        //图片地址
        List<ImageInfo> imageList = new ArrayList<ImageInfo>();

        List<String> oldPathFileList = new ArrayList<String>();//旧路径文件

        //旧状态
        Integer old_status = topic.getStatus();
        String old_content = topic.getContent();
        String old_voteThemeId = topic.getVoteThemeId();
        if(topic.getImage() != null && !topic.getImage().trim().isEmpty()){
            oldImageList = jsonComponent.toGenericObject(topic.getImage().trim(),new TypeReference< List<ImageInfo> >(){});
        }

        VoteTheme voteTheme = null;
        List<VoteOption> voteOptionList = null;
        VoteTheme new_voteTheme = null;//新增投票主题
        VoteTheme update_voteTheme = null;//修改投票主题
        String deleteVoteThemeId = null;//删除投票
        List<VoteOption> add_voteOptionList = new ArrayList<VoteOption>();
        List<VoteOption> edit_voteOptionList = new ArrayList<VoteOption>();
        List<String> delete_voteOptionIdList = new ArrayList<String>();

        //风控文本
        String riskControlText = "";

        if (topic.getStatus().equals(20)) {//如果已发布，则重新执行发贴审核逻辑
            //前台发表话题审核状态
            if (systemSetting.getTopic_review().equals(10)) {//10.全部审核 20.特权会员未触发敏感词免审核(未实现) 30.特权会员免审核 40.触发敏感词需审核(未实现) 50.无需审核
                topic.setStatus(10);//10.待审核
            } else if (systemSetting.getTopic_review().equals(30)) {
                if (topic.getTagId() != null && topic.getTagId() > 0L) {
                    //是否有当前功能操作权限
                    boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1006000, topic.getTagId());
                    if (_flag_permission) {
                        topic.setStatus(20);//20.已发布
                    } else {
                        topic.setStatus(10);//10.待审核
                    }
                }
            } else {
                topic.setStatus(20);//20.已发布
            }
        }

        if(topicDTO.getTitle() != null && !topicDTO.getTitle().trim().isEmpty()){
            if(systemSetting.isAllowFilterWord()){
                String wordReplace = "";
                if(systemSetting.getFilterWordReplace() != null){
                    wordReplace = systemSetting.getFilterWordReplace();
                }
                topic.setTitle(sensitiveWordFilterComponent.filterSensitiveWord(topicDTO.getTitle(), wordReplace));
            }

            topic.setTitle(topicDTO.getTitle());
            if(topicDTO.getTitle().length() >150){
                errors.put("title", "不能大于150个字符");
            }
        }else{
            errors.put("title", "标题不能为空");
        }


        LocalDateTime voteCreateDate = topic.getPostTime();//投票主题创建时间
        LocalDateTime voteEndDate= null;//截止日期
        List<VoteOption> temp_voteOptionList = new ArrayList<VoteOption>();



        EditorTag editorTag = settingComponent.readTopicEditorTag();
        List<UserGrade> userGradeList = userGradeRepository.findAllGrade_cache();

        // 处理内容过滤和文件路径
        TopicClientServiceImpl.HtmlFilterResult htmlFilterResult = processAndFilterContent(request, topicDTO.getContent(), topic.getIsMarkdown(), topicDTO.getMarkdownContent(),editorTag);

        if(htmlFilterResult == null || htmlFilterResult.content == null || htmlFilterResult.content.trim().isEmpty()){
            errors.put("content", "内容不能为空");
        }

        if(errors.isEmpty()) {

            String value = htmlFilterResult.htmlProcessingResult.getHtmlContent();

            //校正隐藏标签
            String validValue =  textFilterComponent.correctionHiddenTag(value,userGradeList);

            //允许使用的隐藏标签
            List<Integer> allowHiddenTagList = new ArrayList<Integer>();
            if(editorTag.isHidePassword()){
                //是否有当前功能操作权限
                boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1020000,topic.getTagId());
                if(_flag_permission){
                    //输入密码可见
                    allowHiddenTagList.add(HideTagType.PASSWORD.getName());
                }
            }
            if(editorTag.isHideComment()){
                //是否有当前功能操作权限
                boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1021000,topic.getTagId());
                if(_flag_permission){
                    //评论话题可见
                    allowHiddenTagList.add(HideTagType.COMMENT.getName());
                }
            }
            if(editorTag.isHideGrade()){
                //是否有当前功能操作权限
                boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1022000,topic.getTagId());
                if(_flag_permission){
                    //达到等级可见
                    allowHiddenTagList.add(HideTagType.GRADE.getName());
                }
            }
            if(editorTag.isHidePoint()){
                //是否有当前功能操作权限
                boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1023000,topic.getTagId());
                if(_flag_permission){
                    //积分购买可见
                    allowHiddenTagList.add(HideTagType.POINT.getName());
                }
            }
            if(editorTag.isHideAmount()){
                //是否有当前功能操作权限
                boolean _flag_permission = userRoleComponent.isPermission(ResourceEnum._1024000,topic.getTagId());
                if(_flag_permission){
                    //余额购买可见
                    allowHiddenTagList.add(HideTagType.AMOUNT.getName());
                }


            }

            //解析隐藏标签
            Map<Integer,Object> analysisHiddenTagMap = textFilterComponent.analysisHiddenTag(validValue);
            for (Map.Entry<Integer,Object> entry : analysisHiddenTagMap.entrySet()) {
                if(!allowHiddenTagList.contains(entry.getKey())){
                    errors.put("content", "发表话题不允许使用 '"+HideTagName.getKey(entry.getKey())+"' 隐藏标签");//隐藏标签
                    break;
                }
            }

            //删除隐藏标签
            String new_content = textFilterComponent.deleteHiddenTag(value);


            //不含标签内容
            String text = textFilterComponent.filterText(textFilterComponent.specifyHtmlTagToText(new_content));

            //清除空格&nbsp;
            String trimSpace = cms.utils.StringUtil.replaceSpace(text).trim();

            //摘要
            if(!trimSpace.isEmpty()){
                if(systemSetting.isAllowFilterWord()){
                    String wordReplace = "";
                    if(systemSetting.getFilterWordReplace() != null){
                        wordReplace = systemSetting.getFilterWordReplace();
                    }
                    trimSpace = sensitiveWordFilterComponent.filterSensitiveWord(trimSpace, wordReplace);
                }
                if(trimSpace.length() >180){
                    topic.setSummary(trimSpace.substring(0, 180)+"..");
                }else{
                    topic.setSummary(trimSpace+"..");
                }
            }

            riskControlText = htmlFilterResult.content;

            //不含标签内容
            String source_text = textFilterComponent.filterText(htmlFilterResult.content);
            //清除空格&nbsp;
            String source_trimSpace = cms.utils.StringUtil.replaceSpace(source_text).trim();

            if(!htmlFilterResult.htmlProcessingResult.isHasImage()
                    && !htmlFilterResult.htmlProcessingResult.isHasFile()
                    && !htmlFilterResult.htmlProcessingResult.isHasMedia()
                    && !htmlFilterResult.htmlProcessingResult.isHasMap()
                    && (source_text.trim().isEmpty() || source_trimSpace.isEmpty())){
                errors.put("content", "内容不能为空");
            }else{
                if(systemSetting.isAllowFilterWord()){
                    String wordReplace = "";
                    if(systemSetting.getFilterWordReplace() != null){
                        wordReplace = systemSetting.getFilterWordReplace();
                    }
                    validValue = sensitiveWordFilterComponent.filterSensitiveWord(validValue, wordReplace);
                }

                topic.setContent(validValue);
            }


            //非隐藏标签内图片
            List<String> other_imageNameList = textFilterComponent.readImageName(new_content,"topic");


            if(other_imageNameList != null && other_imageNameList.size() >0){
                for(int i=0; i<other_imageNameList.size(); i++){
                    ImageInfo imageInfo = new ImageInfo();
                    imageInfo.setName(FileUtil.getName(other_imageNameList.get(i)));
                    imageInfo.setPath(FileUtil.getFullPath(other_imageNameList.get(i)));

                    imageList.add(imageInfo);

                }
            }

        }



        if(errors.isEmpty()){



            topic.setLastUpdateTime(LocalDateTime.now());//最后修改时间
            topic.setMarkdownContent(htmlFilterResult.formatterMarkdown);
            topic.setImage(jsonComponent.toJSONString(imageList));

            int i = topicRepository.updateTopic2(topic,new_voteTheme,update_voteTheme,deleteVoteThemeId,add_voteOptionList,edit_voteOptionList,delete_voteOptionIdList);
            //更新索引
            topicIndexRepository.addTopicIndex(new TopicIndex(String.valueOf(topic.getId()),2));

            if(i >0 && topic.getStatus() < 100 && !old_status.equals(topic.getStatus())){
                User user = userCacheManager.query_cache_findUserByUserName(topic.getUserName());
                if(user != null){
                    //修改用户动态话题状态
                    userRepository.updateUserDynamicTopicStatus(user.getId(),topic.getUserName(),topic.getId(),topic.getStatus());
                }

            }

            if(i >0) {

                //删除缓存
                topicCacheManager.deleteTopicCache(topic.getId());//删除话题缓存
                topicCacheManager.delete_cache_analysisHiddenTag(topic.getId());//删除解析隐藏标签缓存
                topicCacheManager.delete_cache_analysisFullFileName(topic.getId());//删除 解析上传的文件完整路径名称缓存
                topicCacheManager.delete_cache_markUpdateTopicStatus(topic.getId());//删除 标记修改话题状态


                ReadPathResult readPathResult = textFilterComponent.readPathName(old_content,"topic");



                //旧图片
                List<String> old_imageNameList = readPathResult.getImageNameList();

                if(old_imageNameList != null && !old_imageNameList.isEmpty()){

                    Iterator<String> iter = old_imageNameList.iterator();
                    while (iter.hasNext()) {
                        String imageName = iter.next();

                        for(String new_imageName : htmlFilterResult.htmlProcessingResult.getImageNameList()){
                            if(imageName.equals("file/topic/"+new_imageName)){
                                iter.remove();
                                break;
                            }
                        }
                    }
                    if(!old_imageNameList.isEmpty()){
                        for(String imageName : old_imageNameList){

                            oldPathFileList.add(FileUtil.toSystemPath(imageName));

                        }

                    }
                }


                //旧影音
                List<String> old_mediaNameList = readPathResult.getMediaNameList();
                if(old_mediaNameList != null && !old_mediaNameList.isEmpty()){
                    Iterator<String> iter = old_mediaNameList.iterator();
                    while (iter.hasNext()) {
                        String mediaName = iter.next();
                        for(String new_mediaName : htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                            if(mediaName.equals("file/topic/"+new_mediaName)){
                                iter.remove();
                                break;
                            }
                        }
                    }
                    if(!old_mediaNameList.isEmpty()){
                        for(String mediaName : old_mediaNameList){
                            oldPathFileList.add(FileUtil.toSystemPath(mediaName));

                        }

                    }
                }

                //旧文件
                List<String> old_fileNameList = readPathResult.getFileNameList();
                if(old_fileNameList != null && !old_fileNameList.isEmpty()){
                    Iterator<String> iter = old_fileNameList.iterator();
                    while (iter.hasNext()) {
                        String fileName = iter.next();
                        for(String new_fileName : htmlFilterResult.htmlProcessingResult.getFileNameList()){
                            if(fileName.equals("file/topic/"+new_fileName)){
                                iter.remove();
                                break;
                            }
                        }
                    }
                    if(!old_fileNameList.isEmpty()){
                        for(String fileName : old_fileNameList){
                            oldPathFileList.add(FileUtil.toSystemPath(fileName));

                        }

                    }
                }



                List<Thumbnail> thumbnailList = thumbnailRepository.findAllThumbnail_cache();
                if(thumbnailList != null && !thumbnailList.isEmpty()){


                    if(oldImageList != null && !oldImageList.isEmpty()){
                        List<ImageInfo> deleteBeforeImageList = new ArrayList<ImageInfo>();
                        A:for(ImageInfo oldImageInfo : oldImageList){
                            if(!imageList.isEmpty()){
                                for(ImageInfo imageInfo :imageList){
                                    if(oldImageInfo.getName().equals(imageInfo.getName())){
                                        continue A;
                                    }
                                }
                            }
                            deleteBeforeImageList.add(oldImageInfo);
                        }
                        if(!deleteBeforeImageList.isEmpty()){
                            //删除旧缩略图
                            fileComponent.deleteThumbnail(thumbnailList,oldImageList);
                        }
                    }

                    //异步增加缩略图
                    if(!imageList.isEmpty()){
                        fileComponent.addThumbnail(thumbnailList,imageList);
                    }
                }

                //上传文件编号
                String fileNumber = topicComponent.generateFileNumber(topic.getUserName(), topic.getIsStaff());

                //删除图片锁
                if(htmlFilterResult.htmlProcessingResult.getImageNameList() != null && !htmlFilterResult.htmlProcessingResult.getImageNameList().isEmpty()){
                    for(String imageName :htmlFilterResult.htmlProcessingResult.getImageNameList()){

                        if(imageName != null && !imageName.trim().isEmpty()){
                            //如果验证不是当前用户上传的文件，则不删除
                            if(!topicComponent.getFileNumber(FileUtil.getBaseName(imageName.trim())).equals(fileNumber)){
                                continue;
                            }
                            fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,imageName.replaceAll("/","_"));

                        }
                    }
                }
                //删除音视频锁
                if(htmlFilterResult.htmlProcessingResult.getMediaNameList() != null && !htmlFilterResult.htmlProcessingResult.getMediaNameList().isEmpty()){
                    for(String mediaName :htmlFilterResult.htmlProcessingResult.getMediaNameList()){
                        if(mediaName != null && !mediaName.trim().isEmpty()){
                            //如果验证不是当前用户上传的文件，则不删除
                            if(!topicComponent.getFileNumber(FileUtil.getBaseName(mediaName.trim())).equals(fileNumber)){
                                continue;
                            }
                            fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,mediaName.replaceAll("/","_"));

                        }
                    }
                }
                //删除文件锁
                if(htmlFilterResult.htmlProcessingResult.getFileNameList() != null && !htmlFilterResult.htmlProcessingResult.getFileNameList().isEmpty()){
                    for(String fileName :htmlFilterResult.htmlProcessingResult.getFileNameList()){
                        if(fileName != null && !fileName.trim().isEmpty()){
                            //如果验证不是当前用户上传的文件，则不删除
                            if(!topicComponent.getFileNumber(FileUtil.getBaseName(fileName.trim())).equals(fileNumber)){
                                continue;
                            }
                            fileComponent.deleteLock("file"+File.separator+"topic"+File.separator+"lock"+File.separator,fileName.replaceAll("/","_"));

                        }
                    }
                }

                //删除旧路径文件
                if(!oldPathFileList.isEmpty()){
                    for(String oldPathFile :oldPathFileList){
                        //如果验证不是当前用户上传的文件，则不删除
                        if(!topicComponent.getFileNumber(FileUtil.getBaseName(oldPathFile.trim())).equals(fileNumber)){
                            continue;
                        }


                        //替换路径中的..号
                        oldPathFile = FileUtil.toRelativePath(oldPathFile);

                        //删除旧路径文件
                        Boolean state = fileComponent.deleteFile(oldPathFile);
                        if(state != null && !state){

                            //替换指定的字符，只替换第一次出现的
                            oldPathFile = Strings.CS.replaceOnce(oldPathFile, "file"+File.separator+"topic"+File.separator, "");

                            //创建删除失败文件
                            fileComponent.failedStateFile("file"+File.separator+"topic"+File.separator+"lock"+File.separator+FileUtil.toUnderline(oldPathFile));
                        }
                    }
                }
            }else{
                errors.put("topic", "修改话题失败");
            }

            //统计每分钟原来提交次数
            Integer original = settingCacheManager.getSubmitQuantity("topic", accessUser.getUserName());
            if(original != null){
                settingCacheManager.addSubmitQuantity("topic", accessUser.getUserName(),original+1);//刷新每分钟原来提交次数
            }else{
                settingCacheManager.addSubmitQuantity("topic", accessUser.getUserName(),1);//刷新每分钟原来提交次数
            }
        }




        if(!errors.isEmpty()){

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
     * 文件上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param dir: 上传类型，分别为image、file、media
     * @param fileName 文件名称 预签名时有值
     * @param file 文件
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> uploadFile(String dir, String fileName, MultipartFile file, String fileServerAddress){
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        Map<String,Object> returnValue = new HashMap<String,Object>();

        if(systemSetting.getCloseSite().equals(2)){
            returnValue.put("error", 1);
            returnValue.put("message", "只读模式不允许提交数据");
            return returnValue;
        }

        //如果全局不允许提交话题
        if(!systemSetting.isAllowTopic()){
            returnValue.put("error", 1);
            returnValue.put("message", "不允许提交话题");
            return returnValue;
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        //如果实名用户才允许提交话题
        if(systemSetting.isRealNameUserAllowTopic()){
            User _user = userCacheManager.query_cache_findUserByUserName(accessUser.getUserName());
            if(!_user.isRealNameAuthentication()){
                returnValue.put("error", 1);
                returnValue.put("message","实名用户才允许提交话题");
                return returnValue;
            }
        }
        //上传文件编号
        String number = "b"+accessUser.getUserId();


        if (dir == null || number.trim().isEmpty()) {
            returnValue.put("error", 1);
            returnValue.put("message", "参数不能为空");
            return returnValue;
        }
        FileUploadConfig config = validateUpload(dir, accessUser.getUserName());
        if (!config.success()) {
            returnValue.put("error", 1);
            returnValue.put("message", config.message());
            return returnValue;
        }

        DateTime dateTime = new DateTime();
        String date = dateTime.toString("yyyy-MM-dd");
        int fileSystem = fileComponent.getFileSystem();

        if (fileSystem == 10 || fileSystem == 20 || fileSystem == 30) { // SeaweedFS, MinIO, 阿里云OSS
            return handleCloudUpload(dir, fileName, date, number,config, returnValue);
        } else { // 本地系统
            // 本地系统增加大小校验
            if (file != null && file.getSize() / 1024 > config.maxSize()) {
                returnValue.put("error", 1);
                returnValue.put("message", "文件超出允许上传大小");
                return returnValue;
            }
            return handleLocalUpload(dir, file, date, number, config,fileServerAddress, returnValue);
        }

    }


    /**
     * 获取我的话题
     * @param page 页码
     * @return
     */
    public PageView<Topic> getTopicList(int page){
        //调用分页算法代码
        PageView<Topic> pageView = new PageView<Topic>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
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
        params.add(100);

        jpql.append(" and o.isStaff=?"+ (params.size()+1));
        params.add(false);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        QueryResult<Topic> qr = topicRepository.getScrollData(Topic.class, firstindex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(Topic o :qr.getResultlist()){
                o.setIp(null);//IP地址不显示
            }
            List<Tag> tagList = tagRepository.findAllTag_cache();
            if(tagList != null && tagList.size() >0){
                for(Topic topic : qr.getResultlist()){
                    for(Tag tag : tagList){
                        if(topic.getTagId().equals(tag.getId())){
                            topic.setTagName(tag.getName());
                            break;
                        }
                    }

                }
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取话题解锁用户列表
     * @param page 页码
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<TopicUnhide> getTopicUnhideList(int page,Long topicId,String fileServerAddress){
        //调用分页算法代码
        PageView<TopicUnhide> pageView = new PageView<TopicUnhide>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();
        if(topicId != null && topicId > 0L){
            Topic topicInfo = topicCacheManager.queryTopicCache(topicId);//查询缓存
            if(topicInfo != null && topicInfo.getUserName().equals(accessUser.getUserName())){
                //当前页
                int firstIndex = (page-1)*pageView.getMaxresult();


                QueryResult<TopicUnhide> qr = topicRepository.findTopicUnhidePageByTopicId(firstIndex,pageView.getMaxresult(),topicId);
                if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
                    for(TopicUnhide topicUnhide : qr.getResultlist()){
                        User user = userCacheManager.query_cache_findUserByUserName(topicUnhide.getUserName());
                        if(user != null && user.getCancelAccountTime().equals(-1L)){
                            topicUnhide.setAccount(user.getAccount());
                            topicUnhide.setNickname(user.getNickname());
                            topicUnhide.setAvatarPath(fileServerAddress+user.getAvatarPath());
                            topicUnhide.setAvatarName(user.getAvatarName());
                        }else{
                            topicUnhide.setUserName(null);
                        }
                    }
                }
                //将查询结果集传给分页List
                pageView.setQueryResult(qr);
            }
        }
        return pageView;
    }


    /**
     * 校验上传参数
     * @param dir 目录
     * @param userName 用户名称
     * @return
     */
    private FileUploadConfig validateUpload(String dir, String userName) {
        EditorTag editor = settingComponent.readTopicEditorTag();
        if (editor == null) return FileUploadConfig.fail("读取配置失败");

        // 根据 dir 定义不同的规则
        return switch (dir) {
            case "image" -> {
                if (!editor.isImage()) yield FileUploadConfig.fail("当前图片类型不允许上传");
                if (!userRoleComponent.isPermission(ResourceEnum._2002000, null))
                    yield FileUploadConfig.fail("权限不足");
                yield FileUploadConfig.ok(editor.getImageFormat(), editor.getImageSize());
            }
            case "media" -> {
                if (!editor.isUploadVideo()) yield FileUploadConfig.fail("当前视频类型不允许上传");
                if (!userRoleComponent.isPermission(ResourceEnum._2004000, null))
                    yield FileUploadConfig.fail("权限不足");
                yield FileUploadConfig.ok(editor.getVideoFormat(), editor.getVideoSize());
            }
            case "file" -> {
                if (!editor.isFile()) yield FileUploadConfig.fail("当前文件类型不允许上传");
                if (!userRoleComponent.isPermission(ResourceEnum._2003000, null))
                    yield FileUploadConfig.fail("权限不足");
                yield FileUploadConfig.ok(editor.getFileFormat(), editor.getFileSize());
            }
            default -> FileUploadConfig.fail("缺少dir参数");
        };
    }

    /**
     * 处理云存储系统（如SeaweedFS, MinIO, OSS）的文件上传
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param date 日期
     * @param number 上传文件编号
     * @param fileUploadConfig 文件上传配置
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleCloudUpload(String dir, String fileName, String date, String number,FileUploadConfig fileUploadConfig, Map<String, Object> result){
        if (fileName == null || fileName.trim().isEmpty()) {
            result.put("error", 1);
            result.put("message", "名称不能为空");
            return result;
        }

        if(!FileUtil.validateFileSuffix(fileName.trim(),fileUploadConfig.formats())){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }


        //文件锁目录
        String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;
        //取得文件后缀
        String suffix = FileUtil.getExtension(fileName.trim()).toLowerCase();
        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;


        //生成锁文件
        try {
            fileComponent.addLock(lockPathDir,date +"_"+dir+"_"+newFileName);
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }
        String presigne = fileComponent.createPresigned("file/topic/"+date+"/"+dir+"/"+newFileName, fileUploadConfig.maxSize());//创建预签名


        //返回预签名URL
        result.put("error", 0);//0成功  1错误
        result.put("url", presigne);
        result.put("title", HtmlEscape.escape(fileName));//旧文件名称
        return result;
    }




    /**
     * 处理本地文件系统上传
     * @param dir dir 上传类型，分别为image、media、file
     * @param file 文件
     * @param date 日期
     * @param number  上传文件编号
     * @param fileUploadConfig 文件上传配置
     * @param fileServerAddress 文件服务器地址
     * @param result 返回内容
     * @return
     */
    private Map<String, Object> handleLocalUpload(String dir, MultipartFile file, String date, String number,FileUploadConfig fileUploadConfig, String fileServerAddress, Map<String, Object> result){
        if (file == null || file.isEmpty()) {
            result.put("error", 1);
            result.put("message", "文件不能为空");
            return result;
        }

        //当前文件名称
        String sourceFileName = file.getOriginalFilename();
        String suffix = FileUtil.getExtension(sourceFileName).toLowerCase();

        //验证文件类型
        if(!FileUtil.validateFileSuffix(sourceFileName.trim(),fileUploadConfig.formats())){
            result.put("error", 1);
            result.put("message", "该文件格式不允许上传");
            return result;
        }

        //文件保存目录;分多目录主要是为了分散图片目录,提高检索速度
        String pathDir = "file"+File.separator+"topic"+File.separator + date +File.separator +dir+ File.separator;
        //文件锁目录
        String lockPathDir = "file"+File.separator+"topic"+File.separator+"lock"+File.separator;

        //构建文件名称
        String newFileName = UUIDUtil.getUUID32()+ number+"." + suffix;

        //生成文件保存目录
        fileComponent.createFolder(pathDir);
        //生成锁文件保存目录
        fileComponent.createFolder(lockPathDir);

        try {
            //生成锁文件
            fileComponent.addLock(lockPathDir,date +"_"+dir+"_"+newFileName);
            //保存文件
            fileComponent.writeFile(pathDir, newFileName,file.getBytes());
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("生成锁文件错误",e);
            }
        }

        //上传成功
        result.put("error", 0);//0成功  1错误
        result.put("url", fileServerAddress+"file/topic/"+date+"/"+dir+"/"+newFileName);
        result.put("title", HtmlEscape.escape(file.getOriginalFilename()));//旧文件名称
        return result;
    }



    /**
     * 处理并过滤内容
     * @param request 请求信息
     * @param content 富文本内容
     * @param isMarkdown 是否为markdown格式
     * @param markdownContent markdown格式内容
     * @param editorTag 编辑器标签
     * @return HtmlFilterResult Html过滤结果
     */
    private TopicClientServiceImpl.HtmlFilterResult processAndFilterContent(HttpServletRequest request, String content, Boolean isMarkdown, String markdownContent,EditorTag editorTag) {

        //过滤标签
        content = textFilterComponent.filterTag(request,content,editorTag);
        HtmlProcessingResult htmlProcessingResult = textFilterComponent.filterHtml(request,content,"topic",editorTag);
        return new TopicClientServiceImpl.HtmlFilterResult(htmlProcessingResult,null,content);

    }

}
