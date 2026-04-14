package cms.service.redEnvelope.impl;


import cms.component.JsonComponent;
import cms.component.redEnvelope.RedEnvelopeComponent;
import cms.component.topic.TopicCacheManager;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.redEnvelope.ReceiveRedEnvelope;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.redEnvelope.RedEnvelopeRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.redEnvelope.RedEnvelopeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 红包服务
 */
@Service
public class RedEnvelopeServiceImpl implements RedEnvelopeService {

    @Resource
    SettingRepository settingRepository;
    @Resource
    RedEnvelopeComponent redEnvelopeComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    RedEnvelopeRepository redEnvelopeRepository;
    @Resource
    UserRepository userRepository;
    @Resource TopicCacheManager topicCacheManager;
    @Autowired
    private UserCacheManager userCacheManager;


    /**
     * 获取发红包列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getGiveRedEnvelopeList(int page,Long id, String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<GiveRedEnvelope> pageView = new PageView<GiveRedEnvelope>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();


        jpql.append(" and o.userId=?"+ (params.size()+1));
        params.add(id);

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());


        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("giveTime", "desc");//根据giveTime字段降序排序
        QueryResult<GiveRedEnvelope> qr = redEnvelopeRepository.getScrollData(GiveRedEnvelope.class,firstIndex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);


        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(GiveRedEnvelope giveRedEnvelope : qr.getResultlist()){
                if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
                    Topic topic = topicCacheManager.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
                    if(topic != null){
                        giveRedEnvelope.setBindTopicTitle(topic.getTitle());
                    }
                }
            }
        }

        User user = userRepository.findUserById(id);
        if(user != null){
            User currentUser = new User();
            currentUser.setId(user.getId());
            currentUser.setAccount(user.getAccount());
            currentUser.setNickname(user.getNickname());
            if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
                currentUser.setAvatarName(user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);
        }
        returnValue.put("pageView", pageView);
        return returnValue;
    }

    /**
     * 获取发红包金额分配列表
     * @param page 页码
     * @param giveRedEnvelopeId 发红包Id
     * @param id 用户Id
     * @param topicId 话题Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getRedEnvelopeAmountDistributionList(int page,String giveRedEnvelopeId,Long id,Long topicId, String fileServerAddress){
        if(giveRedEnvelopeId == null || giveRedEnvelopeId.trim().isEmpty()){
            throw new BusinessException(Map.of("giveRedEnvelopeId", "发红包Id不能为空"));
        }
        GiveRedEnvelope giveRedEnvelope = redEnvelopeRepository.findById(giveRedEnvelopeId);
        if(giveRedEnvelope == null){
            throw new BusinessException(Map.of("giveRedEnvelopeId", "发红包不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //调用分页算法代码
        PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);

        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        //排序
        boolean sort = true;//true:正序 false:倒序

        QueryResult<ReceiveRedEnvelope> qr = redEnvelopeComponent.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,true,firstindex, pageView.getMaxresult(),sort,true);

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        if(id != null){
            User user = userRepository.findUserById(id);
            if(user != null){
                User currentUser = new User();
                currentUser.setId(user.getId());
                currentUser.setAccount(user.getAccount());
                currentUser.setNickname(user.getNickname());
                if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                    currentUser.setAvatarPath(fileServerAddress+user.getAvatarPath());
                    currentUser.setAvatarName(user.getAvatarName());
                }
                returnValue.put("currentUser", currentUser);
            }
        }
        if(topicId != null){
            Topic topic = topicCacheManager.queryTopicCache(topicId);
            if(topic != null){
                returnValue.put("currentTopic", topic);
            }
        }
        returnValue.put("pageView", pageView);

        returnValue.put("giveRedEnvelope", giveRedEnvelope);
        return returnValue;
    }

    /**
     * 获取收红包列表
     * @param page 页码
     * @param id 用户Id
     * @param fileServerAddress 文件服务器随机地址
     */
    public Map<String,Object> getReceiveRedEnvelopeList(int page,Long id, String fileServerAddress){
        if(id == null || id <=0){
            throw new BusinessException(Map.of("id", "用户Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();

        //调用分页算法代码
        PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();
        QueryResult<ReceiveRedEnvelope> qr = redEnvelopeRepository.findReceiveRedEnvelopeByUserId(id, firstIndex, pageView.getMaxresult());
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        User _user = userRepository.findUserById(id);
        if(_user != null){
            User currentUser = new User();
            currentUser.setId(_user.getId());
            currentUser.setAccount(_user.getAccount());
            currentUser.setNickname(_user.getNickname());
            if(_user.getAvatarName() != null && !_user.getAvatarName().trim().isEmpty()){
                currentUser.setAvatarPath(fileServerAddress+_user.getAvatarPath());
                currentUser.setAvatarName(_user.getAvatarName());
            }
            returnValue.put("currentUser", currentUser);
        }

        returnValue.put("pageView", pageView);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(ReceiveRedEnvelope receiveRedEnvelope : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserById(receiveRedEnvelope.getGiveUserId());
                if(user != null){
                    receiveRedEnvelope.setGiveAccount(user.getAccount());
                    receiveRedEnvelope.setGiveNickname(user.getNickname());
                    receiveRedEnvelope.setGiveUserName(user.getUserName());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        receiveRedEnvelope.setGiveAvatarPath(fileServerAddress+user.getAvatarPath());
                        receiveRedEnvelope.setGiveAvatarName(user.getAvatarName());
                    }
                }

                //如果红包还没拆，则执行拆红包
                if(receiveRedEnvelope.getAmount() != null && receiveRedEnvelope.getAmount().compareTo(new BigDecimal("0")) ==0 && user != null){
                    GiveRedEnvelope giveRedEnvelope = redEnvelopeRepository.findById(receiveRedEnvelope.getGiveRedEnvelopeId());
                    if(giveRedEnvelope != null){
                        //查询用户领取到的红包金额
                        BigDecimal amount = redEnvelopeComponent.queryReceiveRedEnvelopeAmount(giveRedEnvelope,user.getId());
                        if(amount != null && amount.compareTo(new BigDecimal("0")) >0 ){
                            redEnvelopeComponent.unwrapRedEnvelope(receiveRedEnvelope,amount,user.getId(),user.getUserName());
                        }
                    }

                }

            }
        }
        return returnValue;
    }

}
