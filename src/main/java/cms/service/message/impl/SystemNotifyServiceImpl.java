package cms.service.message.impl;


import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.message.SystemNotifyCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.message.SystemNotifyRequest;
import cms.model.message.SubscriptionSystemNotify;
import cms.model.message.SystemNotify;
import cms.model.staff.SysUsers;
import cms.model.user.User;
import cms.repository.message.SystemNotifyRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.message.SystemNotifyService;
import cms.utils.HtmlEscape;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 系统通知服务
 */
@Service
public class SystemNotifyServiceImpl implements SystemNotifyService {

    @Resource
    SystemNotifyRepository systemNotifyRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    SystemNotifyCacheManager systemNotifyCacheManager;
    @Resource
    FileComponent fileComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    UserRepository userRepository;


    /**
     * 获取系统通知列表
     * @param page 页码
     */
    public PageView<SystemNotify> getSystemNotifyList(int page){
        PageView<SystemNotify> pageView = new PageView<SystemNotify>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sendTime", "desc");//根据sendTime字段降序排序


        //调用分页算法类
        QueryResult<SystemNotify> qr = systemNotifyRepository.getScrollData(SystemNotify.class, firstindex, pageView.getMaxresult(), orderby);

        pageView.setQueryResult(qr);
        return pageView;
    }




    /**
     * 添加系统通知
     * @param systemNotifyRequest 系统通知表单
     */
    public void addSystemNotify(SystemNotifyRequest systemNotifyRequest){

        String staffName = "";//员工名称
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            staffName =((SysUsers)obj).getUserAccount();
        }

        SystemNotify systemNotify = new SystemNotify();
        String content = WebUtil.urlToHyperlink(HtmlEscape.escape(systemNotifyRequest.getContent().trim()));

        systemNotify.setContent(content);
        systemNotify.setSendTime(LocalDateTime.now());
        systemNotify.setStaffName(staffName);
        systemNotifyRepository.saveSystemNotify(systemNotify);


        //删除缓存
        systemNotifyCacheManager.delete_cache_findSystemNotifyCountBySystemNotifyId();
        systemNotifyCacheManager.delete_cache_findSystemNotifyCountBySendTime();

    }


    /**
     * 获取修改系统通知界面信息
     * @param systemNotifyId 系统通知Id
     * @return
     */
    public SystemNotify getEditSystemNotifyViewModel(Long systemNotifyId){
        if(systemNotifyId == null || systemNotifyId <=0){
            throw new BusinessException(Map.of("systemNotifyId", "系统通知Id不能为空"));
        }
        SystemNotify systemNotify = systemNotifyRepository.findById(systemNotifyId);

        if(systemNotify == null){
            throw new BusinessException(Map.of("systemNotifyId", "系统通知不存在"));
        }

        systemNotify.setContent(textFilterComponent.filterText(systemNotify.getContent()));

        return systemNotify;
    }
    /**
     * 修改系统通知
     * @param systemNotifyRequest 系统通知表单
     */
    public void editSystemNotify(SystemNotifyRequest systemNotifyRequest){
        if(systemNotifyRequest.getSystemNotifyId() == null || systemNotifyRequest.getSystemNotifyId() <=0){
            throw new BusinessException(Map.of("systemNotifyId", "系统通知Id不能为空"));
        }
        SystemNotify old_systemNotify = systemNotifyRepository.findById(systemNotifyRequest.getSystemNotifyId());
        if(old_systemNotify == null){
            throw new BusinessException(Map.of("systemNotifyId", "系统通知不存在"));
        }

        String staffName = "";//员工名称
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof SysUsers){
            staffName =((SysUsers)obj).getUserAccount();
        }

        SystemNotify systemNotify = new SystemNotify();
        systemNotify.setId(old_systemNotify.getId());
        String content = WebUtil.urlToHyperlink(HtmlEscape.escape(systemNotifyRequest.getContent().trim()));

        systemNotify.setContent(content);
        systemNotify.setStaffName(staffName);
        systemNotifyRepository.updateSystemNotify(systemNotify);


        //删除缓存
        systemNotifyCacheManager.delete_cache_findSystemNotifyCountBySystemNotifyId();
        systemNotifyCacheManager.delete_cache_findSystemNotifyCountBySendTime();
        systemNotifyCacheManager.delete_cache_findById(systemNotifyRequest.getSystemNotifyId());
    }

    /**
     * 删除系统通知
     * @param systemNotifyId 系统通知Id
     */
    public void deleteSystemNotify(Long systemNotifyId){
        if(systemNotifyId == null || systemNotifyId <=0L){
            throw new BusinessException(Map.of("systemNotifyId", "系统通知Id不能为空"));
        }
        int i = systemNotifyRepository.deleteSystemNotify(systemNotifyId);

        //删除缓存
        systemNotifyCacheManager.delete_cache_findSystemNotifyCountBySystemNotifyId();
        systemNotifyCacheManager.delete_cache_findSystemNotifyCountBySendTime();
        systemNotifyCacheManager.delete_cache_findById(systemNotifyId);
    }

    /**
     * 查询订阅系统通知列表
     * @param page 页码
     * @param id 用户Id
     * @param request 请求信息
     * @return
     */
    public Map<String,Object> getSubscriptionSystemNotifyList(int page,Long id, HttpServletRequest request){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(id == null || id <=0L){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        //调用分页算法代码
        PageView<SubscriptionSystemNotify> pageView = new PageView<SubscriptionSystemNotify>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);


        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        //系统通知Id集合
        Set<Long> systemNotifyIdList = new HashSet<Long>();
        //系统通知内容集合
        Map<Long,String> systemNotifyMap = new HashMap<Long,String>();

        QueryResult<SubscriptionSystemNotify> qr = systemNotifyRepository.findSubscriptionSystemNotifyByUserId(id,null,firstIndex,pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
                systemNotifyIdList.add(subscriptionSystemNotify.getSystemNotifyId());
            }
        }
        if(systemNotifyIdList.size() >0){
            for(Long systemNotifyId : systemNotifyIdList){
                SystemNotify systemNotify = systemNotifyRepository.findById(systemNotifyId);
                if(systemNotify != null){
                    systemNotifyMap.put(systemNotifyId, systemNotify.getContent());
                }
            }
        }
        if(systemNotifyIdList.size() >0){
            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(SubscriptionSystemNotify subscriptionSystemNotify : qr.getResultlist()){
                    String content = systemNotifyMap.get(subscriptionSystemNotify.getSystemNotifyId());
                    if(content != null){
                        subscriptionSystemNotify.setContent(content);
                    }

                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);



        User user = userRepository.findUserById(id);
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
     * 还原订阅系统通知
     * @param userId 用户Id
     * @param subscriptionSystemNotifyId 订阅系统通知Id
     */
    public void reductionSubscriptionSystemNotify(Long userId,String subscriptionSystemNotifyId){
        if(userId == null || userId <=0L){
            throw new BusinessException(Map.of("userId", "用户Id不能为空"));
        }
        if(subscriptionSystemNotifyId == null || subscriptionSystemNotifyId.trim().isEmpty()){
            throw new BusinessException(Map.of("subscriptionSystemNotifyId", "系统通知Id不能为空"));
        }
        int i = systemNotifyRepository.reductionSubscriptionSystemNotify(subscriptionSystemNotifyId);

        //删除缓存
        systemNotifyCacheManager.delete_cache_findMinUnreadSystemNotifyIdByUserId(userId);
        systemNotifyCacheManager.delete_cache_findMaxReadSystemNotifyIdByUserId(userId);
    }

}
