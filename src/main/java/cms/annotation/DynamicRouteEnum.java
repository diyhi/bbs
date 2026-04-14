package cms.annotation;

import lombok.Getter;

import java.util.Optional;

/**
 * 动态路由枚举
 */
public enum DynamicRouteEnum {

    /** 添加话题 **/
    DEFAULT_1010100("添加话题", 1),
    /** 修改话题 **/
    DEFAULT_1010200("修改话题", 1),
    /** 话题文件上传 **/
    DEFAULT_1010300("话题文件上传", 1),
    /** 话题解锁 **/
    DEFAULT_1010400("话题解锁", 1),
    /** 添加评论 **/
    DEFAULT_1010500("添加评论", 1),
    /** 引用评论 **/
    DEFAULT_1010600("引用评论", 1),
    /** 修改评论 **/
    DEFAULT_1010700("修改评论", 1),
    /** 删除评论 **/
    DEFAULT_1010800("删除评论", 1),
    /** 评论图片上传 **/
    DEFAULT_1010900("评论图片上传", 1),
    /** 添加评论回复 **/
    DEFAULT_1011000("添加评论回复", 1),
    /** 修改评论回复 **/
    DEFAULT_1011100("修改评论回复", 1),
    /** 删除评论回复 **/
    DEFAULT_1011200("删除评论回复", 1),
    /** 我的话题 **/
    DEFAULT_1011300("我的话题", 1),
    /** 我的评论 **/
    DEFAULT_1011400("我的评论", 1),
    /** 我的评论回复 **/
    DEFAULT_1011500("我的评论回复", 1),
    /** 话题解锁用户列表 **/
    DEFAULT_1011600("话题解锁用户列表", 1),

    /** 添加问题 **/
    DEFAULT_1020100("添加问题", 1),
    /** 追加问题 **/
    DEFAULT_1020200("追加问题", 1),
    /** 问题文件上传 **/
    DEFAULT_1020300("问题文件上传", 1),
    /** 采纳答案 **/
    DEFAULT_1020400("采纳答案", 1),
    /** 添加答案 **/
    DEFAULT_1020500("添加答案", 1),
    /** 修改答案 **/
    DEFAULT_1020600("修改答案", 1),
    /** 删除答案 **/
    DEFAULT_1020700("删除答案", 1),
    /** 答案图片上传 **/
    DEFAULT_1020800("答案图片上传", 1),
    /** 添加答案回复 **/
    DEFAULT_1020900("添加答案回复", 1),
    /** 修改答案回复 **/
    DEFAULT_1021000("修改答案回复", 1),
    /** 删除答案回复 **/
    DEFAULT_1021100("删除答案回复", 1),
    /** 我的问题 **/
    DEFAULT_1021200("我的问题", 1),
    /** 我的答案 **/
    DEFAULT_1021300("我的答案", 1),
    /** 我的答案回复 **/
    DEFAULT_1021400("我的答案回复", 1),


    /** 添加收藏夹 **/
    DEFAULT_1030100("添加收藏夹", 1),
    /** 话题收藏列表 **/
    DEFAULT_1030200("话题收藏列表", 1),
    /** 问题收藏列表 **/
    DEFAULT_1030300("问题收藏列表", 1),
    /** 收藏夹列表 **/
    DEFAULT_1030400("收藏夹列表", 1),
    /** 删除收藏 **/
    DEFAULT_1030500("删除收藏", 1),

    /** 添加点赞 **/
    DEFAULT_1040100("添加点赞", 1),
    /** 话题点赞列表 **/
    DEFAULT_1040200("话题点赞列表", 1),
    /** 点赞列表 **/
    DEFAULT_1040300("点赞列表", 1),
    /** 删除点赞 **/
    DEFAULT_1040400("删除点赞", 1),

    /** 添加关注 **/
    DEFAULT_1050100("添加关注", 1),
    /** 关注列表 **/
    DEFAULT_1050200("关注列表", 1),
    /** 粉丝列表 **/
    DEFAULT_1050300("粉丝列表", 1),
    /** 删除关注 **/
    DEFAULT_1050400("删除关注", 1),

    /** 添加会员卡 **/
    DEFAULT_1060100("添加会员卡", 1),
    /** 会员卡订单列表 **/
    DEFAULT_1060200("会员卡订单列表", 1),

    /** 收红包 **/
    DEFAULT_1070100("收红包", 1),
    /** 发红包列表 **/
    DEFAULT_1070200("发红包列表", 1),
    /** 发红包金额分配列表 **/
    DEFAULT_1070300("发红包金额分配列表", 1),
    /** 收红包列表 **/
    DEFAULT_1070400("收红包列表", 1),

    /** 添加在线留言 **/
    DEFAULT_1080100("添加在线留言", 1),

    /** 添加举报 **/
    DEFAULT_1130100("添加举报", 1),
    /** 举报列表 **/
    DEFAULT_1130200("举报列表", 1),

    /** 参与投票 **/
    DEFAULT_1140100("参与投票", 1),

    /** 生成验证码 **/
    DEFAULT_1150100("生成验证码", 1),
    /** 校验验证码 **/
    DEFAULT_1150200("校验验证码", 1),
    /** AI助手 **/
    DEFAULT_1150300("AI助手", 1),
    /** 基本信息 **/
    DEFAULT_1150400("基本信息", 1),
    /** 默认消息 **/
    DEFAULT_1150450("默认消息", 1),
    /** 获取邮箱验证码 **/
    DEFAULT_1150500("获取邮箱验证码", 1),
    /** 文件下载 **/
    DEFAULT_1150600("文件下载", 1),
    /** 搜索 **/
    DEFAULT_1150800("搜索", 1),
    /** 获取短信验证码 **/
    DEFAULT_1150900("获取短信验证码", 1),
    /** 获取绑定短信验证码 **/
    DEFAULT_1151000("获取绑定短信验证码", 1),
    /** 添加统计数据 **/
    DEFAULT_1151100("添加统计数据", 1),
    /** 视频重定向 **/
    DEFAULT_1151200("视频重定向", 1),
    /** 动态播放列表 **/
    DEFAULT_1151300("动态播放列表", 1),

    /** 付款表单 **/
    DEFAULT_1160100("付款表单", 1),
    /** 支付校验 **/
    DEFAULT_1160200("支付校验", 1),
    /** 付款 **/
    DEFAULT_1160300("付款", 1),
    /** 支付回调通知 **/
    DEFAULT_1160400("支付回调通知", 1),
    /** 支付回调通知 **/
    DEFAULT_1160500("支付完成通知", 1),

    /** 查询微信openid **/
    DEFAULT_1170100("查询微信openid", 1),
    /** 第三方登录链接 **/
    DEFAULT_1170200("第三方登录链接", 1),
    /** 第三方登录重定向 **/
    DEFAULT_1170300("第三方登录重定向", 1),

    /** 会员注册表单 **/
    DEFAULT_1180100("会员注册表单", 1),
    /** 会员注册 **/
    DEFAULT_1180200("会员注册", 1),
    /** 会员注册校验 **/
    //DEFAULT_1180300("会员注册校验", 1),
    /** 会员登录表单 **/
    DEFAULT_1180400("会员登录表单", 1),
    /** 会员登录 **/
    DEFAULT_1180500("会员登录", 1),
    /** 会员退出 **/
    DEFAULT_1180600("会员退出", 1),
    /** 找回密码第一步表单 **/
    DEFAULT_1180700("找回密码第一步表单", 1),
    /** 找回密码第一步 **/
    DEFAULT_1180800("找回密码第一步", 1),
    /** 找回密码第二步表单 **/
    DEFAULT_1180900("找回密码第二步表单", 1),
    /** 找回密码第二步 **/
    DEFAULT_1181000("找回密码第二步", 1),
    /** 会话续期 **/
    DEFAULT_1181100("会话续期", 1),
    /** 恢复微信浏览器会话 **/
    DEFAULT_1181200("恢复微信浏览器会话", 1),
    /** 恢复微信浏览器会话 **/
    DEFAULT_1181300("根据账号或呢称查询用户", 1),
    /** 积分 **/
    DEFAULT_1181400("积分", 1),
    /** 会员修改表单 **/
    DEFAULT_1181500("会员修改表单", 1),
    /** 会员修改 **/
    DEFAULT_1181600("会员修改", 1),
    /** 更新头像 **/
    DEFAULT_1181700("更新头像", 1),
    /** 实名认证 **/
    DEFAULT_1181800("实名认证", 1),
    /** 手机绑定表单 **/
    DEFAULT_1181900("手机绑定表单", 1),
    /** 手机绑定 **/
    DEFAULT_1182000("手机绑定", 1),
    /** 修改手机绑定第一步表单  **/
    DEFAULT_1182100("修改手机绑定第一步表单", 1),
    /** 修改手机绑定第一步 **/
    DEFAULT_1182200("修改手机绑定第一步", 1),
    /** 修改手机绑定第二步表单  **/
    DEFAULT_1182300("修改手机绑定第二步表单", 1),
    /** 修改手机绑定第二步 **/
    DEFAULT_1182400("修改手机绑定第二步", 1),
    /** 用户登录日志 **/
    DEFAULT_1182500("用户登录日志", 1),
    /** 账户余额 **/
    DEFAULT_1182600("账户余额", 1),

    /** 私信列表 **/
    DEFAULT_1190100("私信列表", 1),
    /** 私信对话列表 **/
    DEFAULT_1190200("私信对话列表", 1),
    /** 添加私信表单 **/
    DEFAULT_1190300("添加私信表单", 1),
    /** 添加私信 **/
    DEFAULT_1190400("添加私信", 1),
    /** 删除私信 **/
    DEFAULT_1190500("删除私信", 1),
    /** 系统通知列表 **/
    DEFAULT_1190600("系统通知列表", 1),
    /** 全部系统通知标记为已读 **/
    DEFAULT_1190700("全部系统通知标记为已读", 1),
    /** 删除系统通知 **/
    DEFAULT_1190800("删除系统通知", 1),
    /** 未读消息数量 **/
    DEFAULT_1190900("未读消息数量", 1),
    /** 提醒列表 **/
    DEFAULT_1191000("提醒列表", 1),
    /** 全部提醒状态标记为已读 **/
    DEFAULT_1191100("全部提醒状态标记为已读", 1),
    /** 删除提醒 **/
    DEFAULT_1191200("删除提醒", 1),

    /** 用户中心 **/
    DEFAULT_1200100("用户中心", 1),
    /** 用户动态列表 **/
    DEFAULT_1200200("用户动态列表", 1),

    /** 话题分页 **/
    CUSTOM_6010100("话题分页", 2, DynamicRouteGroupEnum.TOPIC),
    /** 话题明细 **/
    CUSTOM_6010200("话题明细", 2, DynamicRouteGroupEnum.TOPIC),
    /** 评论分页 **/
    CUSTOM_6010300("评论分页", 2, DynamicRouteGroupEnum.TOPIC),
    /** 话题标签列表 **/
    CUSTOM_6010400("话题标签列表", 2, DynamicRouteGroupEnum.TOPIC),
    /** 添加话题表单 **/
    CUSTOM_6010500("添加话题表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 添加评论表单 **/
    CUSTOM_6010600("添加评论表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 引用评论表单 **/
    CUSTOM_6010700("引用评论表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 添加评论回复表单 **/
    CUSTOM_6010800("添加评论回复表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 修改话题表单 **/
    CUSTOM_6010900("修改话题表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 修改评论表单 **/
    CUSTOM_6011000("修改评论表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 修改评论回复表单 **/
    CUSTOM_6011100("修改评论回复表单", 2, DynamicRouteGroupEnum.TOPIC),
    /** 话题内容解锁 **/
    //CUSTOM_6011200("话题内容解锁", 2, DynamicRouteGroupEnum.TOPIC),
    /** 相似话题 **/
    CUSTOM_6011300("相似话题", 2, DynamicRouteGroupEnum.TOPIC),
    /** 热门话题 **/
    CUSTOM_6011400("热门话题", 2, DynamicRouteGroupEnum.TOPIC),
    /** 精华话题分页 **/
    CUSTOM_6011500("精华话题分页", 2, DynamicRouteGroupEnum.TOPIC),

    /** 问题分页 **/
    CUSTOM_6020100("问题分页", 2, DynamicRouteGroupEnum.QNA),
    /** 问题明细 **/
    CUSTOM_6020200("问题明细", 2, DynamicRouteGroupEnum.QNA),
    /** 答案分页 **/
    CUSTOM_6020300("答案分页", 2, DynamicRouteGroupEnum.QNA),
    /** 问答标签列表 **/
    CUSTOM_6020400("问答标签列表", 2, DynamicRouteGroupEnum.QNA),
    /** 添加问题表单 **/
    CUSTOM_6020500("添加问题表单", 2, DynamicRouteGroupEnum.QNA),
    /** 追加问题表单 **/
    CUSTOM_6020600("追加问题表单", 2, DynamicRouteGroupEnum.QNA),
    /** 添加答案表单 **/
    CUSTOM_6020700("添加答案表单", 2, DynamicRouteGroupEnum.QNA),
    /** 添加答案回复表单 **/
    CUSTOM_6020800("添加答案回复表单", 2, DynamicRouteGroupEnum.QNA),
    /** 修改答案 **/
    CUSTOM_6020900("修改答案表单", 2, DynamicRouteGroupEnum.QNA),
    /** 修改答案回复表单 **/
    CUSTOM_6021000("修改答案回复表单", 2, DynamicRouteGroupEnum.QNA),
    /** 相似问题 **/
    CUSTOM_6021100("相似问题", 2, DynamicRouteGroupEnum.QNA),
    /** 用户回答总数 **/
    CUSTOM_6021200("用户回答总数", 2, DynamicRouteGroupEnum.QNA),

    /** 话题收藏总数 **/
    CUSTOM_6030100("话题收藏总数", 2, DynamicRouteGroupEnum.FAVORITES),
    /** 用户是否已经收藏该话题 **/
    CUSTOM_6030200("用户是否已经收藏该话题", 2, DynamicRouteGroupEnum.FAVORITES),
    /** 问题收藏总数 **/
    CUSTOM_6030300("问题收藏总数", 2, DynamicRouteGroupEnum.FAVORITES),
    /** 用户是否已经收藏该问题 **/
    CUSTOM_6030400("用户是否已经收藏该问题", 2, DynamicRouteGroupEnum.FAVORITES),

    /** 点赞总数 **/
    CUSTOM_6040100("点赞总数", 2, DynamicRouteGroupEnum.LIKES),
    /** 用户是否已经点赞该项 **/
    CUSTOM_6040200("用户是否已经点赞该项", 2, DynamicRouteGroupEnum.LIKES),

    /** 关注总数 **/
    CUSTOM_6050100("关注总数", 2, DynamicRouteGroupEnum.FOLLOWS),
    /** 粉丝总数 **/
    CUSTOM_6050200("粉丝总数", 2, DynamicRouteGroupEnum.FOLLOWS),
    /** 是否已经关注该用户 **/
    CUSTOM_6050300("是否已经关注该用户", 2, DynamicRouteGroupEnum.FOLLOWS),

    /** 会员卡列表 **/
    CUSTOM_6060100("会员卡列表", 2, DynamicRouteGroupEnum.MEMBERSHIP_CARD),
    /** 会员卡明细 **/
    CUSTOM_6060200("会员卡明细", 2, DynamicRouteGroupEnum.MEMBERSHIP_CARD),
    /** 购买会员卡表单 **/
    //CUSTOM_6060300("购买会员卡表单", 2, DynamicRouteGroupEnum.MEMBERSHIP_CARD),

    /** 发红包明细 **/
    CUSTOM_6070100("发红包明细", 2, DynamicRouteGroupEnum.RED_ENVELOPE),
    /** 领取红包用户分页 **/
    CUSTOM_6070200("领取红包用户分页", 2, DynamicRouteGroupEnum.RED_ENVELOPE),
    /** 抢红包表单 **/
    //CUSTOM_6070300("抢红包表单", 2, DynamicRouteGroupEnum.RED_ENVELOPE),

    /** 添加在线留言表单 **/
    CUSTOM_6080100("添加在线留言表单", 2, DynamicRouteGroupEnum.FEEDBACK),

    /** 友情链接 **/
    CUSTOM_6090100("友情链接列表", 2, DynamicRouteGroupEnum.LINKS),

    /** 图片广告 **/
    CUSTOM_6100100("图片广告", 2, DynamicRouteGroupEnum.ADVERTISEMENT),

    /** 在线帮助分页 **/
    CUSTOM_6110100("在线帮助分页", 2, DynamicRouteGroupEnum.HELP),
    /** 推荐在线帮助 **/
    CUSTOM_6110200("推荐在线帮助", 2, DynamicRouteGroupEnum.HELP),
    /** 在线帮助分类 **/
    CUSTOM_6110300("在线帮助分类", 2, DynamicRouteGroupEnum.HELP),
    /** 在线帮助导航 **/
    CUSTOM_6110400("在线帮助导航", 2, DynamicRouteGroupEnum.HELP),
    /** 在线帮助明细 **/
    CUSTOM_6110500("在线帮助明细", 2, DynamicRouteGroupEnum.HELP),
    /** 在线帮助列表 **/
    CUSTOM_6110600("在线帮助列表", 2, DynamicRouteGroupEnum.HELP),

    /** 站点栏目 **/
    CUSTOM_6120100("站点栏目", 2, DynamicRouteGroupEnum.SECTION),

    /** 添加举报表单 **/
    CUSTOM_6130100("添加举报表单", 2, DynamicRouteGroupEnum.REPORT),

    /** 投票主题明细 **/
    CUSTOM_6140100("投票主题明细", 2, DynamicRouteGroupEnum.VOTE),

    /** 自定义HTML **/
    CUSTOM_6150100("自定义HTML", 2, DynamicRouteGroupEnum.OTHER),
    /** 热门搜索词 **/
    CUSTOM_6150200("热门搜索词", 2, DynamicRouteGroupEnum.OTHER),
    /** 第三方登录 **/
    CUSTOM_6150300("第三方登录", 2, DynamicRouteGroupEnum.OTHER),
    /** AI助手表单 **/
    CUSTOM_6150400("AI助手表单", 2, DynamicRouteGroupEnum.OTHER);



    /**
     *
     * @param name 名称
     * @param type 类型
     */
    private DynamicRouteEnum(String name, Integer type) {
        this(name, type, null);
    }

    /**
     *
     * @param name 名称
     * @param type 类型
     * @param group 分组
     */
    private DynamicRouteEnum(String name, Integer type, DynamicRouteGroupEnum group) {
        this.name = name;
        this.type = type;
        this.group = group;
    }

    /**
     * 路由名称
     **/
    @Getter
    private final String name;
    /**
     * 类型 1：默认  2.自定义
     * 由Controller类上注解指定映射路径的为默认；由数据库配置映射路径的为自定义
     **/
    @Getter
    private final Integer type;
    /**
     * 路由分组
     **/
    @Getter
    private final DynamicRouteGroupEnum group;

    /**
     * 【根据字符串名称查找 DynamicRouteEnum 常量。
     * @param name 要查找的枚举名称字符串 (e.g., "DEFAULT_1000100")
     * @return 包含找到的常量的 Optional 对象，如果找不到或输入为 null/空，则返回 Optional.empty()
     */
    public static Optional<DynamicRouteEnum> getByNameSafe(String name) {
        // 1. 处理 null 或空字符串输入
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }

        for (DynamicRouteEnum constant : DynamicRouteEnum.values()) {
            // 使用 equals() 进行精确匹配
            if (constant.name().equals(name)) {
                return Optional.of(constant);
            }
        }
        return Optional.empty();
    }
}
