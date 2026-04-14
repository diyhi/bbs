package cms.service.membershipCard;


import cms.dto.PageView;
import cms.dto.membershipCard.MembershipCardGiftTaskRequest;
import cms.model.membershipCard.MembershipCardGiftItem;
import cms.model.membershipCard.MembershipCardGiftTask;

import java.util.Map;

/**
 * 会员卡赠送任务服务
 */
public interface MembershipCardGiftTaskService {

    /**
     * 获取会员卡赠送任务列表
     * @param page 页码
     */
    public PageView<MembershipCardGiftTask> getMembershipCardGiftTaskList(int page);
    /**
     * 获取会员卡赠送项(获赠用户)
     * @param page 页码
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public PageView<MembershipCardGiftItem> getMembershipCardGiftItem(int page, Long membershipCardGiftTaskId, String fileServerAddress);
    /**
     * 获取会员卡赠送任务
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    public MembershipCardGiftTask getMembershipCardGiftTask(Long membershipCardGiftTaskId);
    /**
     * 获取添加会员卡赠送任务界面信息
     * @return
     */
    public Map<String,Object> getAddMembershipCardGiftTaskViewModel();
    /**
     * 添加会员卡赠送任务
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     */
    public void addMembershipCardGiftTask(MembershipCardGiftTaskRequest membershipCardGiftTaskRequest);
    /**
     * 获取修改会员卡赠送任务界面信息
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     * @return
     */
    public Map<String,Object> getEditMembershipCardGiftTaskViewModel(Long membershipCardGiftTaskId);
    /**
     * 修改会员卡赠送任务
     * @param membershipCardGiftTaskRequest 会员卡赠送任务表单
     */
    public void editMembershipCardGiftTask(MembershipCardGiftTaskRequest membershipCardGiftTaskRequest);
    /**
     * 删除会员卡赠送任务
     * @param membershipCardGiftTaskId 会员卡赠送任务Id
     */
    public void deleteMembershipCardGiftTask(Long membershipCardGiftTaskId);
}
