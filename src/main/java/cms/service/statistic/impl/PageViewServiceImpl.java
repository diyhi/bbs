package cms.service.statistic.impl;


import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.statistic.PV;
import cms.repository.setting.SettingRepository;
import cms.repository.statistic.PageViewRepository;
import cms.service.statistic.PageViewService;
import cms.utils.IpAddress;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 页面浏览量服务
 */
@Service
public class PageViewServiceImpl implements PageViewService {

    @Resource
    PageViewRepository pageViewRepository;
    @Resource
    SettingRepository settingRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    /**
     * 获取页面浏览量列表
     * @param page 页码
     * @param start_times 起始时间
     * @param end_times 结束时间
     */
    public PageView<PV> getPageViewList(int page, String start_times, String end_times){
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
            jpql.append(" and o.times >= ?"+ (params.size()+1));
            params.add(_start_times);
        }
        if(_end_times != null){//结束时间
            jpql.append(" and o.times <= ?"+ (params.size()+1));
            params.add(_end_times);
        }

        //调用分页算法代码
        PageView<PV> pageView = new PageView<PV>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
        orderby.put("times", "desc");//根据sort字段降序排序

        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        QueryResult<PV> qr = pageViewRepository.getScrollData(PV.class,firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(PV pv : qr.getResultlist()){
                if(pv.getIp() != null && !pv.getIp().trim().isEmpty()){
                    pv.setIpAddress(IpAddress.queryAddress(pv.getIp()));
                }

                if(pv.getUrl() != null && !pv.getUrl().trim().isEmpty()){
                    pv.setUrl(org.springframework.web.util.UriUtils.decode(pv.getUrl().trim(), "utf-8"));
                }
                if(pv.getReferrer() != null && !pv.getReferrer().trim().isEmpty()){
                    pv.setReferrer(org.springframework.web.util.UriUtils.decode(pv.getReferrer().trim(), "utf-8"));
                }
            }
        }
        return pageView;
    }

}
