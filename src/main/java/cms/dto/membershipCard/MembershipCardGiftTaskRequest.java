package cms.dto.membershipCard;


import cms.model.membershipCard.RestrictionGroup;
import cms.model.user.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员卡赠送任务表单
 */
@Getter
@Setter
public class MembershipCardGiftTaskRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 7752927569116649927L;

    /** Id  **/
    private Long membershipCardGiftTaskId;

    /** 名称**/
    private String name;
    /** 角色Id **/
    private String userRoleId;
    /** 角色(内部传递参数用) **/
    private UserRole userRole;
    /** 用户角色名称 **/
    private String userRoleName;
    /** 任务类型 10:长期  20:一次性 **/
    private Integer type = 10;

    /** 任务有效期范围起始(本参数只在'长期任务'中有效)  默认：1970-1-1 00:00:00 **/
    private String expirationDate_start;
    /** 任务有效期范围结束(本参数只在'长期任务'中有效)  默认：2999-1-1 00:00:00 **/
    private String expirationDate_end;

    /** 任务有效期范围起始(内部传递参数用) **/
    private LocalDateTime parsedExpirationDateStart;
    /** 任务有效期范围结束(内部传递参数用) **/
    private LocalDateTime parsedExpirationDateEnd;

    /** 用户注册时间范围起始 **/
    private String registrationTime_start;
    /** 用户注册时间范围结束 **/
    private String registrationTime_end;
    /** 用户注册时间范围起始(内部传递参数用) **/
    private LocalDateTime parsedRegistrationTimeStart;
    /** 用户注册时间范围结束(内部传递参数用) **/
    private LocalDateTime parsedRegistrationTimeEnd;

    /** 是否启用(本参数只在'长期任务'中有效)  true:启用 false:禁用 **/
    private boolean enable = true;
    /** 赠送时长 **/
    private Integer duration;
    /** 赠送时长单位 10.小时 20.日 30.月 40.年 **/
    private Integer unit;

    /** 限制条件组 **/
    private RestrictionGroup restrictionGroup = new RestrictionGroup();

}
