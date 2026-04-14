package cms.service.platformShare.impl;


import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.sms.SmsComponent;
import cms.component.topic.TopicCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.sms.SmsInterfaceRequest;
import cms.model.platformShare.TopicUnhidePlatformShare;
import cms.model.sms.Alidayu;
import cms.model.sms.SendService;
import cms.model.sms.SendSmsLog;
import cms.model.sms.SmsInterface;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.platformShare.PlatformShareRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.sms.SmsRepository;
import cms.repository.user.UserRepository;
import cms.service.platformShare.TopicUnhidePlatformShareService;
import cms.service.sms.SmsInterfaceService;
import cms.utils.Verification;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 解锁话题隐藏内容平台分成服务
 */
@Service
public class TopicUnhidePlatformShareServiceImpl implements TopicUnhidePlatformShareService {

    @Resource
    PlatformShareRepository platformShareRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserRepository userRepository;
    @Resource TopicCacheManager topicCacheManager;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    /**
     * 获取解锁话题隐藏内容平台分成列表
     * @param page 页码
     * @param start_times 超始时间
     * @param end_times 结束时间
     * @param request 请求信息
     */
    public PageView<TopicUnhidePlatformShare> getTopicUnhidePlatformShareList(int page, String start_times, String end_times, HttpServletRequest request){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        //错误
        Map<String,String> errors = new HashMap<String,String>();
        LocalDateTime _start_times = null;
        LocalDateTime _end_times= null;
        if(start_times != null && !start_times.trim().isEmpty()){
            boolean start_createDateVerification = Verification.isTime_minute(start_times.trim());
            if(start_createDateVerification){
                _start_times = LocalDateTime.parse(start_times.trim(), FORMATTER);
            }else{
                errors.put("start_times", "请填写正确的日期");
            }
        }
        if(end_times != null && !end_times.trim().isEmpty()){
            boolean end_createDateVerification = Verification.isTime_minute(end_times.trim());
            if(end_createDateVerification){
                _end_times = LocalDateTime.parse(end_times.trim(), FORMATTER);
            }else{
                errors.put("end_times", "请填写正确的日期");
            }
        }

        if(_start_times != null && _end_times != null){
            if(_start_times.isAfter(_end_times)){
                // 起始时间比结束时间大
                errors.put("start_times", "起始时间不能比结束时间大");
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        if(_start_times != null){//起始时间
            jpql.append(" and o.unlockTime >= ?"+ (params.size()+1));
            params.add(_start_times);
        }
        if(_end_times != null){//结束时间
            jpql.append(" and o.unlockTime <= ?"+ (params.size()+1));
            params.add(_end_times);
        }




        //调用分页算法代码
        PageView<TopicUnhidePlatformShare> pageView = new PageView<TopicUnhidePlatformShare>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序

        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        QueryResult<TopicUnhidePlatformShare> qr = platformShareRepository.getScrollData(TopicUnhidePlatformShare.class,firstindex, pageView.getMaxresult(),jpql_str, params.toArray(),orderby);

        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(TopicUnhidePlatformShare topicUnhidePlatformShare : qr.getResultlist()){


                if(topicUnhidePlatformShare.getTopicId() != null && topicUnhidePlatformShare.getTopicId() >0L){
                    Topic topic = topicCacheManager.queryTopicCache(topicUnhidePlatformShare.getTopicId());//查询缓存
                    if(topic != null){
                        topicUnhidePlatformShare.setTopicTitle(topic.getTitle());
                    }

                }

                User post_user = userRepository.findUserByUserName(topicUnhidePlatformShare.getPostUserName());
                if(post_user != null){
                    topicUnhidePlatformShare.setPostAccount(post_user.getAccount());
                    topicUnhidePlatformShare.setPostNickname(post_user.getNickname());
                    if(post_user.getAvatarName() != null && !post_user.getAvatarName().trim().isEmpty()){
                        topicUnhidePlatformShare.setPostAvatarPath(fileComponent.fileServerAddress(request)+post_user.getAvatarPath());
                        topicUnhidePlatformShare.setPostAvatarName(post_user.getAvatarName());
                    }
                }
                User unlock_user = userRepository.findUserByUserName(topicUnhidePlatformShare.getUnlockUserName());
                if(unlock_user != null){
                    topicUnhidePlatformShare.setUnlockAccount(unlock_user.getAccount());
                    topicUnhidePlatformShare.setUnlockNickname(unlock_user.getNickname());
                    if(unlock_user.getAvatarName() != null && !unlock_user.getAvatarName().trim().isEmpty()){
                        topicUnhidePlatformShare.setUnlockAvatarPath(fileComponent.fileServerAddress(request)+unlock_user.getAvatarPath());
                        topicUnhidePlatformShare.setUnlockAvatarName(unlock_user.getAvatarName());
                    }
                }

                if(topicUnhidePlatformShare.isStaff()){//如果为员工
                    topicUnhidePlatformShare.setPostAccount(topicUnhidePlatformShare.getPostUserName());//员工用户名和账号是同一个
                    topicUnhidePlatformShare.setUnlockAccount(topicUnhidePlatformShare.getUnlockUserName());//员工用户名和账号是同一个
                }
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }


}
