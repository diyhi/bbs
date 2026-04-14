package cms.annotation;

import lombok.Getter;

/**
 * 动态路由组枚举
 *
 */
public enum DynamicRouteGroupEnum {
    TOPIC("话题", 6010000),
    QNA("问答", 6020000),
    FAVORITES("收藏夹",  6030000),
    LIKES("点赞", 6040000),
    FOLLOWS("关注", 6050000),
    MEMBERSHIP_CARD("会员卡", 6060000),
    RED_ENVELOPE("红包", 6070000),
    FEEDBACK("在线留言", 6080000),
    LINKS("友情链接", 6090000),
    ADVERTISEMENT("广告", 6100000),
    HELP("在线帮助", 6110000),
    SECTION("站点栏目", 6120000),
    REPORT("举报", 6130000),
    VOTE("投票", 6140000),
    OTHER("其它", 6150000);

    private DynamicRouteGroupEnum(String name, Integer code) {
        this.name = name;
        this.code = code;
    }

    /** 资源组名称 **/
    @Getter
    private final String name;
    /** 编号 **/
    @Getter
    private final Integer code;
    

}
