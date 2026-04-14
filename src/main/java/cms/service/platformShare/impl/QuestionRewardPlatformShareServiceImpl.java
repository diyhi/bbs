package cms.service.platformShare.impl;


import cms.component.JsonComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.question.QuestionCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.platformShare.QuestionRewardPlatformShare;
import cms.model.question.Question;
import cms.model.user.User;
import cms.repository.platformShare.PlatformShareRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.platformShare.QuestionRewardPlatformShareService;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 问答悬赏平台分成服务
 */
@Service
public class QuestionRewardPlatformShareServiceImpl implements QuestionRewardPlatformShareService {

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
    @Resource
    QuestionCacheManager questionCacheManager;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    /**
     * 获取问答悬赏平台分成列表
     * @param page 页码
     * @param start_times 超始时间
     * @param end_times 结束时间
     * @param request 请求信息
     */
    public PageView<QuestionRewardPlatformShare> getQuestionRewardPlatformShareList(int page, String start_times, String end_times, HttpServletRequest request){
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
            jpql.append(" and o.adoptionTime >= ?"+ (params.size()+1));
            params.add(_start_times);
        }
        if(_end_times != null){//结束时间
            jpql.append(" and o.adoptionTime <= ?"+ (params.size()+1));
            params.add(_end_times);
        }
        //调用分页算法代码
        PageView<QuestionRewardPlatformShare> pageView = new PageView<QuestionRewardPlatformShare>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序

        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        QueryResult<QuestionRewardPlatformShare> qr = platformShareRepository.getScrollData(QuestionRewardPlatformShare.class,firstindex, pageView.getMaxresult(),jpql_str, params.toArray(),orderby);

        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(QuestionRewardPlatformShare questionRewardPlatformShare : qr.getResultlist()){


                if(questionRewardPlatformShare.getQuestionId() != null && questionRewardPlatformShare.getQuestionId() >0L){
                    Question question = questionCacheManager.query_cache_findById(questionRewardPlatformShare.getQuestionId());//查询缓存
                    if(question != null){
                        questionRewardPlatformShare.setQuestionTitle(question.getTitle());
                    }

                }

                User post_user = userRepository.findUserByUserName(questionRewardPlatformShare.getPostUserName());
                if(post_user != null){
                    questionRewardPlatformShare.setPostAccount(post_user.getAccount());
                    questionRewardPlatformShare.setPostNickname(post_user.getNickname());
                    if(post_user.getAvatarName() != null && !post_user.getAvatarName().trim().isEmpty()){
                        questionRewardPlatformShare.setPostAvatarPath(fileComponent.fileServerAddress(request)+post_user.getAvatarPath());
                        questionRewardPlatformShare.setPostAvatarName(post_user.getAvatarName());
                    }
                }
                User answer_user = userRepository.findUserByUserName(questionRewardPlatformShare.getAnswerUserName());
                if(answer_user != null){
                    questionRewardPlatformShare.setAnswerAccount(answer_user.getAccount());
                    questionRewardPlatformShare.setAnswerNickname(answer_user.getNickname());
                    if(answer_user.getAvatarName() != null && !answer_user.getAvatarName().trim().isEmpty()){
                        questionRewardPlatformShare.setAnswerAvatarPath(fileComponent.fileServerAddress(request)+answer_user.getAvatarPath());
                        questionRewardPlatformShare.setAnswerAvatarName(answer_user.getAvatarName());
                    }
                }

                if(questionRewardPlatformShare.isStaff()){//如果为员工
                    questionRewardPlatformShare.setPostAccount(questionRewardPlatformShare.getPostUserName());//员工用户名和账号是同一个
                    questionRewardPlatformShare.setAnswerAccount(questionRewardPlatformShare.getAnswerUserName());//员工用户名和账号是同一个
                }
            }
        }

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }


}
