package cms.service.frontend.impl;

import cms.component.JsonComponent;
import cms.component.frontendModule.FrontendApiComponent;
import cms.component.redEnvelope.ReceiveRedEnvelopeConfig;
import cms.component.redEnvelope.RedEnvelopeCacheManager;
import cms.component.redEnvelope.RedEnvelopeComponent;
import cms.component.topic.TopicCacheManager;
import cms.component.user.UserCacheManager;
import cms.component.user.UserRoleComponent;
import cms.config.BusinessException;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.user.AccessUser;
import cms.dto.user.ResourceEnum;
import cms.model.frontendModule.ConfigRedEnvelopeUserPage;
import cms.model.frontendModule.FrontendApi;
import cms.model.redEnvelope.GiveRedEnvelope;
import cms.model.redEnvelope.ReceiveRedEnvelope;
import cms.model.setting.SystemSetting;
import cms.model.topic.Topic;
import cms.model.user.User;
import cms.repository.redEnvelope.RedEnvelopeRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.RedEnvelopeClientService;
import cms.utils.AccessUserThreadLocal;
import cms.utils.RateLimiterUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 前台红包服务
 */
@Service
public class RedEnvelopeClientServiceImpl implements RedEnvelopeClientService {
    @Resource
    SettingRepository settingRepository;
    @Resource
    UserRoleComponent userRoleComponent;
    @Resource RedEnvelopeCacheManager redEnvelopeCacheManager;
    @Resource UserCacheManager userCacheManager;
    @Resource TopicCacheManager topicCacheManager;
    @Resource
    ReceiveRedEnvelopeConfig receiveRedEnvelopeConfig;
    @Resource
    FrontendApiComponent frontendApiComponent;
    @Resource
    RedEnvelopeComponent redEnvelopeComponent;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    RedEnvelopeRepository redEnvelopeRepository;


    /**
     * 获取发红包明细
     * @param giveRedEnvelopeId 发红包Id
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public GiveRedEnvelope getSentRedEnvelopeDetail(String giveRedEnvelopeId, String fileServerAddress){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        if(giveRedEnvelopeId != null && !giveRedEnvelopeId.trim().isEmpty()){
            GiveRedEnvelope giveRedEnvelope = redEnvelopeCacheManager.query_cache_findById(giveRedEnvelopeId);//查询缓存

            if(giveRedEnvelope != null){
                if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
                    Topic topic = topicCacheManager.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
                    if(topic != null){
                        //判断权限
                        boolean permission = userRoleComponent.isPermission(ResourceEnum._1001000,topic.getTagId());
                        if(!permission){
                            return null;
                        }
                    }
                }

                User user = userCacheManager.query_cache_findUserById(giveRedEnvelope.getUserId());
                if(user != null){
                    giveRedEnvelope.setUserName(user.getUserName());
                    if(user.getCancelAccountTime().equals(-1L)){
                        giveRedEnvelope.setAccount(user.getAccount());
                        giveRedEnvelope.setNickname(user.getNickname());
                        giveRedEnvelope.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        giveRedEnvelope.setAvatarName(user.getAvatarName());
                    }
                }

                if(accessUser != null){
                    ReceiveRedEnvelope receiveRedEnvelope = redEnvelopeCacheManager.query_cache_findByReceiveRedEnvelopeId(receiveRedEnvelopeConfig.createReceiveRedEnvelopeId(giveRedEnvelopeId, accessUser.getUserId()));
                    if(receiveRedEnvelope != null){
                        giveRedEnvelope.setAccessUserUnwrap(true);//当前用户已拆开红包
                    }

                }
                return giveRedEnvelope;
            }
        }
        return null;
    }

    /**
     * 领取红包用户分页
     * @param page 页码
     * @param giveRedEnvelopeId 发红包Id
     * @param request 请求信息
     * @return
     */
    public PageView<ReceiveRedEnvelope> getReceivedRedEnvelopePage(int page, String giveRedEnvelopeId,HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigRedEnvelopeUserPage configRedEnvelopeUserPage) {
            int pageCount = 10;// 页码显示总数
            boolean sort = false;//true:正序 false:倒序
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if (configRedEnvelopeUserPage.getMaxResult() != null && configRedEnvelopeUserPage.getMaxResult() > 0) {
                maxResult = configRedEnvelopeUserPage.getMaxResult();
            }
            //排序
            if(configRedEnvelopeUserPage.getSort() != null && configRedEnvelopeUserPage.getSort() >0){
                if(configRedEnvelopeUserPage.getSort().equals(10)){
                    sort = true;
                }else if(configRedEnvelopeUserPage.getSort().equals(20)){
                    sort = false;
                }
            }
            if(giveRedEnvelopeId != null && !giveRedEnvelopeId.trim().isEmpty()){
                GiveRedEnvelope giveRedEnvelope = redEnvelopeCacheManager.query_cache_findById(giveRedEnvelopeId);//查询缓存
                if(giveRedEnvelope != null && giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
                    PageForm pageForm = new PageForm();
                    pageForm.setPage(page);

                    //调用分页算法代码
                    PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(maxResult,pageForm.getPage(),pageCount);
                    //当前页
                    int firstIndex = (pageForm.getPage()-1)*pageView.getMaxresult();

                    QueryResult<ReceiveRedEnvelope> qr = redEnvelopeComponent.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,false,firstIndex, pageView.getMaxresult(),sort,false);

                    //将查询结果集传给分页List
                    pageView.setQueryResult(qr);

                    return pageView;
                }
            }
        }
        return null;
    }

    /**
     * 收红包
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    public Map<String,Object> addReceiveRedEnvelope(String giveRedEnvelopeId){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值


        Map<String,String> errors = new HashMap<String,String>();
        SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
        if(systemSetting.getCloseSite().equals(2)){
            throw new BusinessException(Map.of("receiveRedEnvelope", "只读模式不允许提交数据"));
        }

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //拆红包获得金额
        BigDecimal receiveRedEnvelopeAmount = null;

        if(RateLimiterUtil.apply_minutes("giveRedEnvelope-"+giveRedEnvelopeId, 300,null)){//限流  每秒钟可以领5个

            GiveRedEnvelope giveRedEnvelope = null;
            if(giveRedEnvelopeId != null){
                giveRedEnvelope = redEnvelopeRepository.findById(giveRedEnvelopeId);
                if(giveRedEnvelope != null){

                    if(giveRedEnvelope.getRemainingQuantity() <=0){
                        errors.put("receiveRedEnvelope", "红包已被抢光");

                    }else{
                        ReceiveRedEnvelope receiveRedEnvelope = redEnvelopeRepository.findByReceiveRedEnvelopeId(receiveRedEnvelopeConfig.createReceiveRedEnvelopeId(giveRedEnvelopeId, accessUser.getUserId()));
                        if(receiveRedEnvelope != null){
                            errors.put("receiveRedEnvelope", "每个红包只能领取一次");
                        }
                    }

                    Topic topic = topicCacheManager.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
                    if(topic != null){
                        if(!topic.getStatus().equals(20)){//20.已发布
                            errors.put("receiveRedEnvelope", "话题未发布不允许领取红包");
                        }else{
                            //检查权限
                            boolean isPermission = userRoleComponent.checkPermission(ResourceEnum._1001000,topic.getTagId());
                            if(!isPermission){
                                errors.put("receiveRedEnvelope", "没有领取红包权限");
                            }
                        }

                    }else{
                        errors.put("receiveRedEnvelope", "话题不存在不允许领取红包");
                    }

                    if(giveRedEnvelope.getRefundAmount() != null && giveRedEnvelope.getRefundAmount().compareTo(new BigDecimal("0")) >0){

                        errors.put("receiveRedEnvelope", "红包已原路返还用户");
                    }

                }else{
                    errors.put("receiveRedEnvelope", "没有发红包");
                }
            }else{
                errors.put("receiveRedEnvelope", "发红包Id不能为空");
            }

            if(errors.size() == 0){
                //收红包
                ReceiveRedEnvelope receiveRedEnvelope = new ReceiveRedEnvelope();
                receiveRedEnvelope.setId(receiveRedEnvelopeConfig.createReceiveRedEnvelopeId(giveRedEnvelopeId, accessUser.getUserId()));
                receiveRedEnvelope.setReceiveUserId(accessUser.getUserId());//收红包的用户Id

                receiveRedEnvelope.setGiveRedEnvelopeId(giveRedEnvelopeId);//发红包Id
                receiveRedEnvelope.setGiveUserId(giveRedEnvelope.getUserId());
                receiveRedEnvelope.setReceiveTime(LocalDateTime.now());

                Object receiveRedEnvelope_obj = redEnvelopeComponent.createReceiveRedEnvelopeObject(receiveRedEnvelope);
                String grabRedEnvelopeUserId = jsonComponent.toJSONString(accessUser.getUserId())+",";
                try {
                    int i = redEnvelopeRepository.saveReceiveRedEnvelope(receiveRedEnvelope_obj, giveRedEnvelopeId, grabRedEnvelopeUserId);

                    if(i >0){
                        giveRedEnvelope = redEnvelopeRepository.findById(giveRedEnvelopeId);
                        //查询用户领取到的红包金额
                        BigDecimal amount = redEnvelopeComponent.queryReceiveRedEnvelopeAmount(giveRedEnvelope,accessUser.getUserId());
                        if(amount != null && amount.compareTo(new BigDecimal("0")) >0){
                            ReceiveRedEnvelope new_receiveRedEnvelope =  redEnvelopeRepository.findByReceiveRedEnvelopeId(receiveRedEnvelope.getId());
                            if(new_receiveRedEnvelope != null && new_receiveRedEnvelope.getAmount() != null && new_receiveRedEnvelope.getAmount().compareTo(new BigDecimal("0")) ==0 ){
                                //拆红包--调用的方法包含删除收红包缓存
                                Integer j = redEnvelopeComponent.unwrapRedEnvelope(new_receiveRedEnvelope,amount,accessUser.getUserId(),accessUser.getUserName());
                                if(j >0){//如果拆红包成功
                                    receiveRedEnvelopeAmount = amount;
                                }
                            }
                        }

                    }else{
                        //删除缓存
                        redEnvelopeCacheManager.delete_cache_findByReceiveRedEnvelopeId(receiveRedEnvelope.getId());
                    }
                    //删除缓存
                    redEnvelopeCacheManager.delete_cache_findById(giveRedEnvelopeId);

                } catch (org.springframework.orm.jpa.JpaSystemException e) {
                    errors.put("receiveRedEnvelope", "收红包错误");
                }

            }



        }else{
            errors.put("systemInfo", "系统繁忙");
        }
        if(errors.size() >0){
            returnValue.put("success", false);
            returnValue.put("error", errors);
        }else{
            returnValue.put("success", true);
            if(receiveRedEnvelopeAmount != null){
                returnValue.put("receiveRedEnvelopeAmount", receiveRedEnvelopeAmount);
            }
        }
        return returnValue;
    }

    /**
     * 获取发红包列表
     * @param page 页码
     * @return
     */
    public PageView<GiveRedEnvelope> getGiveRedEnvelopeList(int page){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //调用分页算法代码
        PageView<GiveRedEnvelope> pageView = new PageView<GiveRedEnvelope>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();

        jpql.append(" and o.userId=?"+ (params.size()+1));
        params.add(accessUser.getUserId());

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("giveTime", "desc");//根据giveTime字段降序排序
        QueryResult<GiveRedEnvelope> qr = redEnvelopeRepository.getScrollData(GiveRedEnvelope.class,firstIndex, pageView.getMaxresult(),_jpql, params.toArray(),orderby);

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

        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取发红包金额分配列表
     * @param page 页码
     * @param giveRedEnvelopeId 发红包Id
     * @return
     */
    public Map<String,Object> getRedEnvelopeAmountDistributionList(int page, String giveRedEnvelopeId){
        Map<String,Object> returnValue = new HashMap<String,Object>();//返回值

        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //调用分页算法代码
        PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();
        if(giveRedEnvelopeId != null && !giveRedEnvelopeId.trim().isEmpty()){
            GiveRedEnvelope giveRedEnvelope = redEnvelopeRepository.findById(giveRedEnvelopeId);
            if(giveRedEnvelope != null && giveRedEnvelope.getUserId().equals(accessUser.getUserId())){
                //排序
                boolean sort = false;//true:正序 false:倒序

                QueryResult<ReceiveRedEnvelope> qr = redEnvelopeComponent.queryReceiveRedEnvelopeByCondition(giveRedEnvelope,false,firstindex, pageView.getMaxresult(),sort,false);

                if(giveRedEnvelope.getBindTopicId() != null && giveRedEnvelope.getBindTopicId() >0L){
                    Topic topic = topicCacheManager.queryTopicCache(giveRedEnvelope.getBindTopicId());//查询缓存
                    if(topic != null){
                        giveRedEnvelope.setBindTopicTitle(topic.getTitle());
                    }

                }

                //将查询结果集传给分页List
                pageView.setQueryResult(qr);

                returnValue.put("pageView", pageView);
                returnValue.put("giveRedEnvelope", giveRedEnvelope);
            }
        }
        return returnValue;
    }

    /**
     * 获取收红包列表
     * @param page 页码
     * @param fileServerAddress 文件服务器地址
     * @return
     */
    public PageView<ReceiveRedEnvelope> getReceiveRedEnvelopeList(int page, String fileServerAddress){
        //获取登录用户
        AccessUser accessUser = AccessUserThreadLocal.get();

        //调用分页算法代码
        PageView<ReceiveRedEnvelope> pageView = new PageView<ReceiveRedEnvelope>(settingRepository.findSystemSetting_cache().getForestagePageNumber(),page,10);
        //当前页
        int firstIndex = (page-1)*pageView.getMaxresult();


        QueryResult<ReceiveRedEnvelope> qr = redEnvelopeRepository.findReceiveRedEnvelopeByUserId(accessUser.getUserId(), firstIndex, pageView.getMaxresult());
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        if(qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(ReceiveRedEnvelope receiveRedEnvelope : qr.getResultlist()){
                User user = userCacheManager.query_cache_findUserById(receiveRedEnvelope.getGiveUserId());
                if(user != null && user.getCancelAccountTime().equals(-1L)){
                    receiveRedEnvelope.setGiveNickname(user.getNickname());
                    receiveRedEnvelope.setGiveUserName(user.getUserName());
                    receiveRedEnvelope.setGiveAccount(user.getAccount());
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
        return pageView;
    }
}
