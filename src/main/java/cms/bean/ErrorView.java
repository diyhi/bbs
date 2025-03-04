package cms.bean;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台错误提示
 *
 */
public enum ErrorView {
	
	_1("路径来路错误"),
	_11("令牌为空"),
	_12("令牌过期"),
	_13("令牌错误"),
	_14("需要输入验证码"),//验证码参数错误
	_15("验证码错误"),
	_16("请输入验证码"),
	_17("验证码过期"),
	_18("密钥错误"),
	_20("字符超长"),
	_21("只读模式不允许提交数据"),
	_22("系统繁忙"),
	_23("模块错误"),
	_24("令牌检测未开启"),
	_25("刷新令牌不能为空"),
	/**-------------------------------- 话题 -----------------------------------**/	
	_101("内容不能为空"),
	_103("话题Id不能为空"),
	_104("引用评论不能为空"),
	_105("评论不存在"),
	_106("禁止评论"),
	_107("话题不存在"),
	_108("实名用户才允许评论"),
	_109("实名用户才允许提交话题"),
	_110("不允许提交话题"),
	_111("已发布话题才允许评论"),
	_112("话题不存在"),
	_113("只允许修改自己发布的话题"),
	_114("话题已删除"),
	_115("修改话题失败"),
	_116("评论不存在"),
	_117("只允许修改自己发布的评论"),
	_118("评论已删除"),
	_119("修改评论失败"),
	_120("评论Id不能为空"),
	_121("修改回复失败"),
	_122("只允许修改自己发布的回复"),
	_123("禁止回复"),
	_124("实名用户才允许提交回复"),
	_125("回复不存在"),
	_126("回复Id不能为空"),
	_127("请填写总金额"),
	_128("不能超过12位数字"),
	_129("不能超过3位数字"),
	_130("请填写正整数"),
	_131("不允许发红包"),
	_132("不能小于发红包总金额下限"),
	_133("不能大于发红包总金额上限"),
	_134("总金额不能为空"),
	_135("拆分后最低单个红包金额不足0.01元"),
	_136("金额不能为空"),
	_137("不能小于0.01元"),
	_138("请填写货币格式"),
	_139("不能为空"),
	_140("提交话题错误"),
	_141("删除评论失败"),
	_142("删除回复失败"),
	_143("只允许删除自己发布的评论"),
	_144("只允许删除自己发布的回复"),
	_145("对方回复Id和评论Id不对应"),
	_146("对方回复Id不存在"),
	
	/**-------------------------------- 问答 -----------------------------------**/	
	_201("内容不能为空"),
	_203("问题Id不能为空"),
	_205("答案不存在"),
	_206("禁止回答"),
	_207("问题不存在"),
	_208("实名用户才允许回答"),
	_209("实名用户才允许提交问题"),
	_210("不允许提交问题"),
	_211("已提交问题才允许评论"),
	_212("问题不存在"),
	_214("问题已删除"),
	_215("答案Id不能为空"),
	_216("该问题已经采纳答案"),
	_217("不是提交该问题的用户不允许采纳答案"),
	_218("追加内容不能为空"),
	_219("不是提交该问题的用户不允许追加提问"),
	_220("不能超过12位数字"),
	_221("请填写金额"),
	_222("不能超过8位数字"),
	_223("请填写正整数或0"),
	
	_224("不能大于账户预存款"),
	_225("不能小于0"),
	_226("不能大于账户积分"),
	_227("提交问题错误"),
	_228("该问题不允许采纳答案"),
	_229("不允许悬赏金额"),
	_230("不能小于悬赏金额下限"),
	_231("不能大于悬赏金额上限"),
	_232("不允许悬赏积分"),
	_233("不能小于悬赏积分下限"),
	_234("不能大于悬赏积分上限"),
	
	_240("只允许修改自己发布的答案"),
	_241("答案已删除"),
	_242("修改答案失败"),
	_243("答案Id不能为空"),
	_244("不允许修改已采纳的答案"),
	_245("删除答案失败"),
	_246("对方回复Id和答案Id不对应"),
	_247("对方回复Id不存在"),
	/**-------------------------------- 在线留言 -----------------------------------**/	
	_301("不能超过100个字符"),
	_302("名称不能为空"),
	_303("联系方式不能为空"),
	_304("内容不能为空"),
	_305("在线留言已关闭"),
	_306("字符超长"),
	
	/**-------------------------------- 修改用户 -----------------------------------**/	
	_801("密码长度错误"),
	_802("旧密码错误"),
	_803("旧密码不能为空"),
	_804("只允许输入数字"),
	_805("只允许输入字母"),
	_806("只允许输入数字和字母"),
	_807("只允许输入汉字"),
	_808("输入错误"),
	_809("必填项"),
	_810("修改用户失败"),
	_811("账号不能小于3个字符"),
	_812("账号不能大于25个字符"),
	_813("账号只能输入由数字、26个英文字母或者下划线组成"),
	_814("该账号已注册"),
	_815("账号不能为空"),
	_816("密码不能为空"),
	_817("密码提示问题不能超过50个字符"),
	_818("密码提示问题不能为空"),
	_819("密码提示答案长度错误"),
	_820("密码提示答案不能为空"),
	_821("邮箱地址不正确"),
	_822("邮箱地址不能超过60个字符"),
	_823("注册会员出错"),
	_824("禁止账号"),
	_825("账号错误"),
	_826("密码错误"),
	_827("密码提示答案错误"),
	_828("找回密码错误"),
	_829("呢称不能超过15个字符"),
	_830("该呢称已存在"),
	_831("不允许修改呢称"),
	_832("该呢称不允许使用"),
	_833("呢称不能和其他用户名相同"),
	_834("用户类型不能为空"),
	_835("用户类型错误"),
	_836("呢称不能和其他账号相同"),
	_837("呢称不能和员工账号相同"),
	_838("呢称不能和员工呢称相同"),
	_839("该账号不能和其他用户呢称相同"),
	_850("手机验证码错误"),
	_851("手机号不能为空"),
	_852("手机验证码不能为空"),
	_853("手机号码不正确"),
	_854("手机号码超长"),
	_855("手机验证码超长"),
	_856("手机验证码不存在或已过期"),
	_857("手机号码不能重复绑定"),
	_858("你还没有绑定手机"),
	_859("用户不存在"),
	_860("新手机号码不能和旧用机号码相同"),
	_861("旧手机号码校验失败"),
	_862("不允许注册"),
	_863("该用户名不允许注册"),
	_864("手机号码已注册"),
	_865("用户类型不能为空"),
	_866("手机号不能为空"),
	_867("手机号错误"),
	_868("不允许发短信"),
	_869("手机用户不存在"),
	_870("手机号不是手机账户"),
	_910("用户不存在"),
	_920("用户不是本地密码账户"),
	_1000("不允许给当前用户发私信"),
	_1010("不允许给自己发私信"),
	_1020("对方用户名称不能为空"),
	_1030("私信内容不能超过1000个字符"),
	_1040("私信内容不能为空"),
	_1050("删除私信失败"),
	_1100("订阅系统通知Id不能为空"),
	_1110("删除系统通知失败"),
	_1200("不能超过8位数字"),
	_1210("宽度必须大于0"),
	_1230("高度必须大于0"),
	_1250("X轴必须大于或等于0"),
	_1270("Y轴必须大于或等于0"),
	_1290("超出最大宽度"),
	_1300("超出最大高度"),
	_1310("当前文件类型不允许上传"),
	_1320("文件超出允许上传大小"),
	_1330("文件不能为空"),
	_1400("提醒不存在"),
	_1500("重复收藏"),
	_1510("话题收藏Id不能为空"),
	_1520("当前话题已经收藏"),
	_1521("当前问题已经收藏"),
	_1530("收藏Id不存在"),
	_1540("删除收藏失败"),
	_1550("收藏不存在"),
	_1560("本收藏不属于当前用户"),
	_1570("问题收藏Id不能为空"),
	_1580("待收藏数据不存在"),
	_1590("不允许同时收藏多项数据"),
	
	
	_1600("话题重复取消隐藏"),
	_1610("当前话题已经取消隐藏"),
	_1620("隐藏标签不存在"),
	_1630("密码错误"),
	_1640("提交过于频繁，请稍后再提交"),
	_1650("密码不能为空"),
	_1660("话题内容不含当前标签"),
	_1670("用户不存在"),
	_1680("用户积分不足"),
	_1685("用户余额不足"),
	_1690("不允许解锁自已发表的话题"),
	_1700("重复点赞"),
	_1710("项目点赞Id不能为空"),
	_1720("当前项目已经点赞"),
	_1725("点赞模块不能为空"),
	_1726("点赞模块不存在"),
	_1730("点赞Id不存在"),
	_1740("删除点赞失败"),
	_1750("点赞不存在"),
	_1760("本点赞不属于当前用户"),
	_1800("重复关注"),
	_1810("关注Id不能为空"),
	_1820("当前用户已关注对方"),
	_1830("关注Id不存在"),
	_1840("删除关注失败"),
	_1850("关注不存在"),
	_1860("本关注不属于当前用户"),
	_1870("不能关注自身"),
	_1900("规格Id不能为空"),
	_1910("规格不存在"),
	_1920("会员卡不存在"),
	_1930("会员卡已下架"),
	_1940("创建会员卡订单错误"),
	_1950("角色不存在"),
	_1960("库存不足"),
	_1970("积分不足"),
	_1980("预存款不足"),
	_1990("此规格已下架"),
	_2000("此会员卡已下架"),
	
	
	_3010("没有发红包"),
	_3020("发红包Id不能为空"),
	_3030("收红包错误"),
	_3040("每个红包只能领取一次"),
	_3050("红包已被抢光"),
	_3060("话题未发布不允许领取红包"),
	_3070("话题不存在不允许领取红包"),
	_3080("红包已原路返还用户"),
	_3090("没有领取红包权限"),
	
	_4010("请选择举报分类"),
	_4020("举报分类不存在"),
	_4030("请填写理由"),
	_4040("不允许提交举报"),
	_4050("图片格式错误"),
	_4060("参数Id不能为空"),
	_4070("模块不能为空"),
	_4080("状态参数错误"),
	_4090("话题不存在"),
	_4100("评论不存在"),
	_4110("评论回复不存在"),
	_4120("问题不存在"),
	_4130("答案不存在"),
	_4140("答案回复不存在"),
	_4150("您之前对此内容的举报还在处理中，请不要重复举报");
	
	
	private ErrorView(String content) {
        this.content = content;
    }
    /** 内容 **/
    private String content;

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	private static final Map<String,String> lookup = new HashMap<String,String>();
	static {
	    for(ErrorView s : EnumSet.allOf(ErrorView.class)){
	
	         lookup.put(s.name(), s.getContent());
	
	    }

	}
	public static String get(String code) { 
	
	    return lookup.get(code); 
	
	}

	
}
