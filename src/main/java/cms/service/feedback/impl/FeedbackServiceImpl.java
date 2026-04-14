package cms.service.feedback.impl;


import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.feedback.Feedback;
import cms.repository.feedback.FeedbackRepository;
import cms.repository.setting.SettingRepository;
import cms.service.feedback.FeedbackService;
import cms.utils.IpAddress;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 在线留言服务
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Resource
    FeedbackRepository feedbackRepository;
    @Resource
    SettingRepository settingRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * 获取在线留言列表
     *
     * @param page             页码
     * @param start_createDate 起始时间
     * @param end_createDate   结束时间
     * @return
     */
    public PageView<Feedback> getFeedbackList(int page, String start_createDate, String end_createDate) {
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();
        //错误
        Map<String, String> errors = new HashMap<String, String>();
        LocalDateTime _start_createDate = null;
        LocalDateTime _end_createDate = null;
        if (start_createDate != null && !start_createDate.trim().isEmpty()) {
            boolean start_createDateVerification = Verification.isTime_minute(start_createDate.trim());
            if (start_createDateVerification) {
                _start_createDate = LocalDateTime.parse(start_createDate.trim(), FORMATTER);
            } else {
                errors.put("start_createDate", "请填写正确的日期");
            }
        }
        if (end_createDate != null && !end_createDate.trim().isEmpty()) {
            boolean end_createDateVerification = Verification.isTime_minute(end_createDate.trim());
            if (end_createDateVerification) {
                _end_createDate = LocalDateTime.parse(end_createDate.trim(), FORMATTER);
            } else {
                errors.put("end_createDate", "请填写正确的日期");
            }
        }

        if (_start_createDate != null && _end_createDate != null) {
            if (_start_createDate.isAfter(_end_createDate)) {
                // 起始时间比结束时间大
                errors.put("start_createDate", "起始时间不能比结束时间大");
            }
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        if (_start_createDate != null) {//起始时间
            jpql.append(" and o.createDate >= ?" + (params.size() + 1));
            params.add(_start_createDate);
        }
        if (_end_createDate != null) {//结束时间
            jpql.append(" and o.createDate <= ?" + (params.size() + 1));
            params.add(_end_createDate);
        }

        //调用分页算法代码
        PageView<Feedback> pageView = new PageView<Feedback>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);
        //当前页
        int firstindex = (page - 1) * pageView.getMaxresult();
        ;
        //排序
        LinkedHashMap<String, String> orderby = new LinkedHashMap<String, String>();
        orderby.put("createDate", "desc");//根据createDate字段降序排序

        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        QueryResult<Feedback> qr = feedbackRepository.getScrollData(Feedback.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(), orderby);
        /**
        for(Feedback feedback :qr.getResultlist()){
            if(feedback.getIp() != null && !feedback.getIp().trim().isEmpty()){
                feedback.setIpAddress(IpAddress.queryAddress(feedback.getIp().trim()));
            }
        }**/


        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        return pageView;
    }


    /**
     * 获取在线留言明细
     * @param feedbackId 在线留言Id
     * @return
     */
    public Feedback getFeedbackDetail(Long feedbackId) {
        if(feedbackId == null || feedbackId <=0L){
            throw new BusinessException(Map.of("feedbackId", "在线留言Id不能为空"));
        }
        Feedback feedback = feedbackRepository.findById(feedbackId);
        if(feedback == null){
            throw new BusinessException(Map.of("feedbackId", "在线留言不存在"));
        }
        if(feedback.getIp() != null && !feedback.getIp().trim().isEmpty()){
            feedback.setIpAddress(IpAddress.queryAddress(feedback.getIp().trim()));
        }
        return  feedback;
    }

    /**
     * 删除在线留言
     * @param feedbackId 在线留言Id
     */
    public void deleteFeedback(Long feedbackId){
        if(feedbackId == null || feedbackId <=0L){
            throw new BusinessException(Map.of("feedbackId", "在线留言Id不能为空"));
        }
        feedbackRepository.deleteFeedback(feedbackId);
    }
}