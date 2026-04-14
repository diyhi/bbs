package cms.service.membershipCard.impl;


import cms.component.JsonComponent;
import cms.component.membershipCard.MembershipCardGiftTaskComponent;
import cms.component.user.UserCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.membershipCard.MembershipCardGiftTaskRequest;
import cms.model.membershipCard.MembershipCardGiftItem;
import cms.model.membershipCard.MembershipCardGiftTask;
import cms.model.membershipCard.RestrictionGroup;
import cms.model.user.User;
import cms.model.user.UserRole;
import cms.repository.membershipCard.MembershipCardGiftTaskRepository;
import cms.repository.membershipCard.MembershipCardRepository;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRoleRepository;
import cms.service.membershipCard.MembershipCardGiftTaskService;
import cms.utils.Verification;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 会员卡赠送任务服务
 */
@Service
public class MembershipCardGiftTaskServiceImpl implements MembershipCardGiftTaskService {

    @Resource
    MembershipCardRepository membershipCardRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource UserCacheManager userCacheManager;
    @Resource
    MembershipCardGiftTaskRepository membershipCardGiftTaskRepository;
    @Resource
    MembershipCardGiftTaskComponent membershipCardGiftTaskComponent;
    @Resource
    UserRoleRepository userRoleRepository;

    private final List<Integer> taskType = Arrays.asList(10,20);//任务类型

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取会员卡赠送任务列表
     * @param page 页码
     */
    public PageView<MembershipCardGiftTask> getMembershipCardGiftTaskList(int page){
        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();


        PageView<MembershipCardGiftTask> pageView = new PageView<MembershipCardGiftTask>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//根据id字段降序排序


        //删除第一个and
        String jpql_str = StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<MembershipCardGiftTask> qr = membershipCardRepository.getScrollData(MembershipCardGiftTask.class, firstindex, pageView.getMaxresult(), jpql_str, params.toArray(),orderby);


        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(MembershipCardGiftTask membershipCardGiftTask : qr.getResultlist()){
                if(membershipCardGiftTask.getExpirationDate_start() != null){//默认初始时间则设为空
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒

                    if(defaultTime.isEqual(membershipCardGiftTask.getExpirationDate_start())){
                        membershipCardGiftTask.setExpirationDate_start(null);
                    }
                }
                if(membershipCardGiftTask.getExpirationDate_end() != null){//默认初始时间则设为空
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒

                    if(defaultTime.isEqual(membershipCardGiftTask.getExpirationDate_end())){
                        membershipCardGiftTask.setExpirationDate_end(null);
                    }
                }


                if(membershipCardGiftTask.getRestriction() != null && !membershipCardGiftTask.getRestriction().trim().isEmpty()){
                    RestrictionGroup restrictionGroup= jsonComponent.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
                    if(restrictionGroup != null){
                        membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
                    }
                }
            }
        }

        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取会员卡赠送项(获赠用户)
     * @param page 页码
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<MembershipCardGiftItem> getMembershipCardGiftItem(int page,Long membershipCardGiftTaskId,String fileServerAddress){
        if(membershipCardGiftTaskId == null || membershipCardGiftTaskId <=0){
            throw new BusinessException(Map.of("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空"));
        }
        PageView<MembershipCardGiftItem> pageView = new PageView<MembershipCardGiftItem>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;

        //调用分页算法类
        QueryResult<MembershipCardGiftItem> qr = membershipCardGiftTaskRepository.findMembershipCardGiftItemPage(membershipCardGiftTaskId, firstindex, pageView.getMaxresult());

        if(qr != null && qr.getResultlist() != null && qr.getResultlist().size() >0){
            for(MembershipCardGiftItem membershipCardGiftItem :qr.getResultlist()){
                if(membershipCardGiftItem.getRestriction() != null && !membershipCardGiftItem.getRestriction().trim().isEmpty()){
                    RestrictionGroup restrictionGroup= jsonComponent.toObject(membershipCardGiftItem.getRestriction(), RestrictionGroup.class);
                    if(restrictionGroup != null){
                        membershipCardGiftItem.setRestrictionGroup(restrictionGroup);
                    }
                }
                User user = userCacheManager.query_cache_findUserByUserName(membershipCardGiftItem.getUserName());
                if(user != null){
                    membershipCardGiftItem.setAccount(user.getAccount());
                    membershipCardGiftItem.setNickname(user.getNickname());
                    if(user.getAvatarName() != null && !user.getAvatarName().trim().isEmpty()){
                        membershipCardGiftItem.setAvatarPath(fileServerAddress+user.getAvatarPath());
                        membershipCardGiftItem.setAvatarName(user.getAvatarName());
                    }
                }
            }
        }
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 获取会员卡赠送任务
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    public MembershipCardGiftTask getMembershipCardGiftTask(Long membershipCardGiftTaskId){
        if(membershipCardGiftTaskId == null || membershipCardGiftTaskId <=0){
            throw new BusinessException(Map.of("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空"));
        }
        MembershipCardGiftTask membershipCardGiftTask = membershipCardGiftTaskRepository.findById(membershipCardGiftTaskId);
        if(membershipCardGiftTask == null){
            throw new BusinessException(Map.of("membershipCardGiftTaskId", "会员卡赠送任务不存在"));
        }
        return membershipCardGiftTask;
    }

    /**
     * 获取添加会员卡赠送任务界面信息
     * @return
     */
    public Map<String,Object> getAddMembershipCardGiftTaskViewModel(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if(userRoleList != null && userRoleList.size() >0){
            Iterator<UserRole> iterator = userRoleList.iterator();
            while (iterator.hasNext()) {
                UserRole userRole = iterator.next();
                if(userRole.getDefaultRole()){//如果是默认角色
                    iterator.remove();
                }
            }
            for(UserRole userRole : userRoleList){
                userRole.setSelected(true);//默认选中第一个
                break;
            }
        }

        returnValue.put("userRoleList", userRoleList);
        return returnValue;
    }

    /**
     * 添加会员卡赠送任务
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     */
    public void addMembershipCardGiftTask(MembershipCardGiftTaskRequest membershipCardGiftTaskRequest){
        if(membershipCardGiftTaskRequest.getType() == null){
            throw new BusinessException(Map.of("type", "任务类型不能为空"));
        }else if(!taskType.contains(membershipCardGiftTaskRequest.getType())){
            throw new BusinessException(Map.of("type", "任务类型参数错误或不支持"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        validateInput(membershipCardGiftTaskRequest,errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        MembershipCardGiftTask membershipCardGiftTask = new MembershipCardGiftTask();
        RestrictionGroup restrictionGroup = new RestrictionGroup();

        membershipCardGiftTask.setType(membershipCardGiftTaskRequest.getType());
        membershipCardGiftTask.setName(membershipCardGiftTaskRequest.getName().trim());//名称
        if(membershipCardGiftTaskRequest.getUserRole() != null){
            membershipCardGiftTask.setUserRoleId(membershipCardGiftTaskRequest.getUserRole().getId());
            membershipCardGiftTask.setUserRoleName(membershipCardGiftTaskRequest.getUserRole().getName());
        }
        if(membershipCardGiftTaskRequest.getType().equals(10)) {//长期任务
            if(membershipCardGiftTaskRequest.getParsedExpirationDateStart() != null){
                membershipCardGiftTask.setExpirationDate_start(membershipCardGiftTaskRequest.getParsedExpirationDateStart());
            }else{//空时间则填充默认值
                //默认时间  年,月,日,时,分,秒,毫秒
                LocalDateTime defaultTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒
                membershipCardGiftTask.setExpirationDate_start(defaultTime);
            }
            if(membershipCardGiftTaskRequest.getParsedExpirationDateEnd() != null) {
                membershipCardGiftTask.setExpirationDate_end(membershipCardGiftTaskRequest.getParsedExpirationDateEnd());
            }else{//空时间则填充默认值
                //默认时间  年,月,日,时,分,秒,毫秒
                LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒
                membershipCardGiftTask.setExpirationDate_end(defaultTime);
            }
        }

        restrictionGroup.setRegistrationTime_start(membershipCardGiftTaskRequest.getParsedRegistrationTimeStart());
        restrictionGroup.setRegistrationTime_end(membershipCardGiftTaskRequest.getParsedRegistrationTimeEnd());
        if(membershipCardGiftTaskRequest.getRestrictionGroup().getTotalPoint() != null && membershipCardGiftTaskRequest.getRestrictionGroup().getTotalPoint() >0L){
            restrictionGroup.setTotalPoint(membershipCardGiftTaskRequest.getRestrictionGroup().getTotalPoint());
        }

        membershipCardGiftTask.setRestriction(jsonComponent.toJSONString(restrictionGroup));

        membershipCardGiftTask.setDuration(membershipCardGiftTaskRequest.getDuration());
        membershipCardGiftTask.setUnit(membershipCardGiftTaskRequest.getUnit());

        membershipCardGiftTask.setCreateDate(LocalDateTime.now());//创建时间
        membershipCardGiftTask.setEnable(membershipCardGiftTaskRequest.isEnable());

        membershipCardGiftTaskRepository.saveMembershipCardGiftTask(membershipCardGiftTask);

        if(membershipCardGiftTask.getType().equals(20)){//一次性任务
            membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
            membershipCardGiftTaskComponent.async_executionMembershipCardGiftTask(membershipCardGiftTask);
        }
    }


    /**
     * 获取修改会员卡赠送任务界面信息
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    public Map<String,Object> getEditMembershipCardGiftTaskViewModel(Long membershipCardGiftTaskId){
        if(membershipCardGiftTaskId == null || membershipCardGiftTaskId <=0L){
            throw new BusinessException(Map.of("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空"));
        }
        MembershipCardGiftTask membershipCardGiftTask = membershipCardGiftTaskRepository.findById(membershipCardGiftTaskId);
        if(membershipCardGiftTask == null){
            throw new BusinessException(Map.of("membershipCardGiftTask", "会员卡赠送任务不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(membershipCardGiftTask.getExpirationDate_start() != null){//默认初始时间则设为空
            //默认时间  年,月,日,时,分,秒,毫秒
            LocalDateTime defaultTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒

            if(defaultTime.isEqual(membershipCardGiftTask.getExpirationDate_start())){
                membershipCardGiftTask.setExpirationDate_start(null);
            }
        }
        if(membershipCardGiftTask.getExpirationDate_end() != null){//默认初始时间则设为空
            //默认时间  年,月,日,时,分,秒,毫秒
            LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒

            if(defaultTime.isEqual(membershipCardGiftTask.getExpirationDate_end())){
                membershipCardGiftTask.setExpirationDate_end(null);
            }
        }


        if(membershipCardGiftTask.getRestriction() != null && !membershipCardGiftTask.getRestriction().trim().isEmpty()){
            RestrictionGroup restrictionGroup= jsonComponent.toObject(membershipCardGiftTask.getRestriction(), RestrictionGroup.class);
            if(restrictionGroup != null){
                membershipCardGiftTask.setRestrictionGroup(restrictionGroup);
            }
        }
        returnValue.put("membershipCardGiftTask",membershipCardGiftTask);

        //查询所有角色
        List<UserRole> userRoleList = userRoleRepository.findAllRole();
        if(userRoleList != null && userRoleList.size() >0){
            Iterator<UserRole> iterator = userRoleList.iterator();
            while (iterator.hasNext()) {
                UserRole userRole = iterator.next();
                if(membershipCardGiftTask.getUserRoleId() != null && userRole.getId().equals(membershipCardGiftTask.getUserRoleId())){
                    userRole.setSelected(true);
                }
                if(userRole.getDefaultRole()){//如果是默认角色
                    iterator.remove();
                }
            }

        }
        returnValue.put("userRoleList", userRoleList);

        return returnValue;
    }
    /**
     * 修改会员卡赠送任务
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     */
    public void editMembershipCardGiftTask(MembershipCardGiftTaskRequest membershipCardGiftTaskRequest){
        if(membershipCardGiftTaskRequest.getMembershipCardGiftTaskId() == null || membershipCardGiftTaskRequest.getMembershipCardGiftTaskId() <=0){
            throw new BusinessException(Map.of("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空"));
        }
        MembershipCardGiftTask old_membershipCardGiftTask = membershipCardGiftTaskRepository.findById(membershipCardGiftTaskRequest.getMembershipCardGiftTaskId());
        if(old_membershipCardGiftTask == null){
            throw new BusinessException(Map.of("membershipCardGiftTask", "会员卡赠送任务不存在"));
        }
        if(old_membershipCardGiftTask.getType().equals(10)) {//10:长期
            Map<String, String> errors = new HashMap<String, String>();

            validateInput(membershipCardGiftTaskRequest,errors);

            if (!errors.isEmpty()) {
                throw new BusinessException(errors);
            }


            MembershipCardGiftTask membershipCardGiftTask = new MembershipCardGiftTask();
            RestrictionGroup restrictionGroup = new RestrictionGroup();

            membershipCardGiftTask.setType(membershipCardGiftTaskRequest.getType());
            membershipCardGiftTask.setName(membershipCardGiftTaskRequest.getName().trim());//名称
            if(membershipCardGiftTaskRequest.getUserRole() != null){
                membershipCardGiftTask.setUserRoleId(membershipCardGiftTaskRequest.getUserRole().getId());
                membershipCardGiftTask.setUserRoleName(membershipCardGiftTaskRequest.getUserRole().getName());
            }
            if(membershipCardGiftTaskRequest.getType().equals(10)) {//长期任务
                if(membershipCardGiftTaskRequest.getParsedExpirationDateStart() != null){
                    membershipCardGiftTask.setExpirationDate_start(membershipCardGiftTaskRequest.getParsedExpirationDateStart());
                }else{//空时间则填充默认值
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(1970, 1, 1, 0, 0, 0);// 1970年1月1日0点0分0秒
                    membershipCardGiftTask.setExpirationDate_start(defaultTime);
                }
                if(membershipCardGiftTaskRequest.getParsedExpirationDateEnd() != null) {
                    membershipCardGiftTask.setExpirationDate_end(membershipCardGiftTaskRequest.getParsedExpirationDateEnd());
                }else{//空时间则填充默认值
                    //默认时间  年,月,日,时,分,秒,毫秒
                    LocalDateTime defaultTime = LocalDateTime.of(2999, 1, 1, 0, 0, 0);// 2999年1月1日0点0分0秒
                    membershipCardGiftTask.setExpirationDate_end(defaultTime);
                }
            }

            restrictionGroup.setRegistrationTime_start(membershipCardGiftTaskRequest.getParsedRegistrationTimeStart());
            restrictionGroup.setRegistrationTime_end(membershipCardGiftTaskRequest.getParsedRegistrationTimeEnd());
            if(membershipCardGiftTaskRequest.getRestrictionGroup().getTotalPoint() != null && membershipCardGiftTaskRequest.getRestrictionGroup().getTotalPoint() >0L){
                restrictionGroup.setTotalPoint(membershipCardGiftTaskRequest.getRestrictionGroup().getTotalPoint());
            }

            membershipCardGiftTask.setRestriction(jsonComponent.toJSONString(restrictionGroup));

            membershipCardGiftTask.setDuration(membershipCardGiftTaskRequest.getDuration());
            membershipCardGiftTask.setUnit(membershipCardGiftTaskRequest.getUnit());


            membershipCardGiftTask.setId(membershipCardGiftTaskRequest.getMembershipCardGiftTaskId());
            membershipCardGiftTask.setEnable(membershipCardGiftTaskRequest.isEnable());

            membershipCardGiftTaskRepository.updateMembershipCardGiftTask(membershipCardGiftTask);
        }else{
            throw new BusinessException(Map.of("membershipCardGiftTask", "一次性类型赠送任务不允许修改"));
        }
    }

    /**
     * 删除会员卡赠送任务
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     */
    public void deleteMembershipCardGiftTask(Long membershipCardGiftTaskId){
        if(membershipCardGiftTaskId == null || membershipCardGiftTaskId <=0){
            throw new BusinessException(Map.of("membershipCardGiftTaskId", "会员卡赠送任务Id不能为空"));
        }
        int i = membershipCardGiftTaskRepository.deleteMembershipCardGiftTask(membershipCardGiftTaskId);
    }


    /**
     * 校验表单字段
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     * @param errors 错误信息
     */
    private void validateInput(MembershipCardGiftTaskRequest membershipCardGiftTaskRequest,Map<String, String> errors) {
        if(membershipCardGiftTaskRequest.getName() != null && !membershipCardGiftTaskRequest.getName().trim().isEmpty()){
            if(membershipCardGiftTaskRequest.getName().trim().length() >150){
                errors.put("name", "不能大于150个字符");
            }
        }else{
            errors.put("name", "名称不能为空");
        }

        if(membershipCardGiftTaskRequest.getUserRoleId() != null && !membershipCardGiftTaskRequest.getUserRoleId().trim().isEmpty()){
            //查询角色
            UserRole userRole = userRoleRepository.findRoleById(membershipCardGiftTaskRequest.getUserRoleId().trim());
            if(userRole == null){
                errors.put("userRoleId", "角色不存在");
            }else{
                membershipCardGiftTaskRequest.setUserRole(userRole);
            }
        }else{
            errors.put("userRoleId", "角色不能为空");
        }


        if(membershipCardGiftTaskRequest.getType().equals(10)){//长期任务
            //任务有效期范围起始
            if(membershipCardGiftTaskRequest.getExpirationDate_start() != null && !membershipCardGiftTaskRequest.getExpirationDate_start().trim().isEmpty()){
                boolean start_verification = Verification.isTime_second(membershipCardGiftTaskRequest.getExpirationDate_start().trim());
                if(start_verification){
                    LocalDateTime startDate = LocalDateTime.parse(membershipCardGiftTaskRequest.getExpirationDate_start(), FORMATTER);
                    membershipCardGiftTaskRequest.setParsedExpirationDateStart(startDate);
                }else{
                    errors.put("expirationDate_start", "请填写正确的日期");
                }
            }
            //任务有效期范围结束
            if(membershipCardGiftTaskRequest.getExpirationDate_end() != null && !membershipCardGiftTaskRequest.getExpirationDate_end().trim().isEmpty()){
                boolean end_verification = Verification.isTime_second(membershipCardGiftTaskRequest.getExpirationDate_end().trim());
                if(end_verification){
                    LocalDateTime endDate = LocalDateTime.parse(membershipCardGiftTaskRequest.getExpirationDate_end(), FORMATTER);
                    membershipCardGiftTaskRequest.setParsedExpirationDateEnd(endDate);
                }else{
                    errors.put("expirationDate_end", "请填写正确的日期");
                }
            }
            if (membershipCardGiftTaskRequest.getParsedExpirationDateStart() != null && membershipCardGiftTaskRequest.getParsedExpirationDateEnd() != null) {
                if (membershipCardGiftTaskRequest.getParsedExpirationDateStart().isAfter(membershipCardGiftTaskRequest.getParsedExpirationDateEnd())) {
                    errors.put("expirationDate_start", "起始时间不能比结束时间大");
                }
            }


        }

        //用户注册时间范围起始
        if(membershipCardGiftTaskRequest.getRegistrationTime_start() != null && !membershipCardGiftTaskRequest.getRegistrationTime_start().trim().isEmpty()){
            boolean start_verification = Verification.isTime_second(membershipCardGiftTaskRequest.getRegistrationTime_start().trim());
            if(start_verification){
                LocalDateTime startDate = LocalDateTime.parse(membershipCardGiftTaskRequest.getRegistrationTime_start(), FORMATTER);
                membershipCardGiftTaskRequest.setParsedRegistrationTimeStart(startDate);
            }else{
                errors.put("restrictionGroup.registrationTime_start", "请填写正确的日期");
            }
        }
        //用户注册时间范围结束
        if(membershipCardGiftTaskRequest.getRegistrationTime_end() != null && !membershipCardGiftTaskRequest.getRegistrationTime_end().trim().isEmpty()){
            boolean end_verification = Verification.isTime_second(membershipCardGiftTaskRequest.getRegistrationTime_end().trim());
            if(end_verification){
                LocalDateTime endDate = LocalDateTime.parse(membershipCardGiftTaskRequest.getRegistrationTime_end(), FORMATTER);
                membershipCardGiftTaskRequest.setParsedRegistrationTimeEnd(endDate);
            }else{
                errors.put("restrictionGroup.registrationTime_end", "请填写正确的日期");
            }
        }
        if (membershipCardGiftTaskRequest.getParsedRegistrationTimeStart() != null && membershipCardGiftTaskRequest.getParsedRegistrationTimeEnd() != null) {
            if (membershipCardGiftTaskRequest.getParsedRegistrationTimeStart().isAfter(membershipCardGiftTaskRequest.getParsedRegistrationTimeEnd())) {
                errors.put("restrictionGroup.registrationTime_start", "起始时间不能比结束时间大");
            }
        }

        if(membershipCardGiftTaskRequest.getDuration() != null){
            if(membershipCardGiftTaskRequest.getDuration() <=0){
                errors.put("duration", "时长必须大于0");
            }else{
                if(membershipCardGiftTaskRequest.getDuration() >99999999){
                    errors.put("duration", "数字最大为8位");
                }
            }
        }else{
            errors.put("duration", "时长不能为空");
        }

        //时长单位
        if(membershipCardGiftTaskRequest.getUnit() == null){
            errors.put("unit", "时长单位不能为空");
        }

    }


}
