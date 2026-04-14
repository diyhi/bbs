package cms.service.staff.impl;

import cms.component.CaptchaCacheManager;
import cms.component.admin.AdminCacheManager;
import cms.component.fileSystem.FileComponent;
import cms.component.setting.SettingCacheManager;
import cms.component.staff.StaffCacheManager;
import cms.component.staff.StaffLoginLogComponent;
import cms.component.staff.StaffLoginLogShardingConfig;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.staff.StaffLoginLog;
import cms.model.staff.SysRoles;
import cms.model.staff.SysUsers;
import cms.model.staff.SysUsersRoles;
import cms.repository.feedback.FeedbackRepository;
import cms.repository.question.AnswerRepository;
import cms.repository.question.QuestionRepository;
import cms.repository.report.ReportRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.staff.ACLRepository;
import cms.repository.staff.StaffRepository;
import cms.repository.topic.CommentRepository;
import cms.repository.topic.TopicRepository;
import cms.service.staff.StaffLoginLogService;
import cms.service.staff.StaffService;
import cms.utils.FileUtil;
import cms.utils.IpAddress;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 员工服务
 */
@Service
public class StaffLoginLogServiceImpl implements StaffLoginLogService {
    @Resource
    StaffRepository staffRepository;

    @Resource
    SettingRepository settingRepository;

    /**
     * 获取员工登录日志列表
     * @param userId 员工Id
     * @param page 页码
     * @return
     */
    public Map<String, Object> getStaffLoginLogList(String userId, int page) {
        if(userId == null || userId.trim().isEmpty()){
            throw new BusinessException(Map.of("userId", "员工Id不能为空"));
        }

        Map<String, Object> returnValue = new HashMap<String, Object>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers loginUser = (SysUsers) authentication.getPrincipal();


        if(!loginUser.isIssys() && !loginUser.getUserId().equals(userId)){
            throw new BusinessException(Map.of("userId", "非超级管理员不允许查看其他成员登录记录"));
        }

        //调用分页算法代码
        PageView<StaffLoginLog> pageView = new PageView<StaffLoginLog>(settingRepository.findSystemSetting().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();;
        QueryResult<StaffLoginLog> qr = staffRepository.findStaffLoginLogPage(userId, firstIndex, pageView.getMaxresult());
        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(StaffLoginLog staffLoginLog : qr.getResultlist()){
                if(staffLoginLog.getIp() != null && !staffLoginLog.getIp().trim().isEmpty()){
                    staffLoginLog.setIpAddress(IpAddress.queryAddress(staffLoginLog.getIp()));
                }
            }
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        returnValue.put("pageView", pageView);
        SysUsers sysUsers = (SysUsers)staffRepository.find(SysUsers.class, userId);
        if(sysUsers != null){
            returnValue.put("currentStaff", sysUsers);
        }

        return returnValue;
    }

}