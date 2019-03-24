package cms.bean;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 前台错误提示
 *
 */
public enum ErrorView {
	
	yes{
		public String getContent(){
			return "yes";
		}
	},
	no{
		public String getContent(){
			return "no";
		}
	},
	_1{
		public String getContent(){
			return "路径来路错误";
		}
	},
	_11{
		public String getContent(){
			return "令牌为空";
		}
	},
	_12{
		public String getContent(){
			return "令牌过期";
		}
	},
	_13{
		public String getContent(){
			return "令牌错误";
		}
	},
	
	_14{
		public String getContent(){
			return "验证码参数错误";
		}
	},
	_15{
		public String getContent(){
			return "验证码错误";
		}
	},
	_16{
		public String getContent(){
			return "请输入验证码";
		}
	},
	_17{
		public String getContent(){
			return "验证码过期";
		}
	},
	_18{
		public String getContent(){
			return "密钥错误";
		}
	},
	_20{
		public String getContent(){
			return "字符超长";
		}
	},
	/**-------------------------------- 评论 -----------------------------------**/	
	_101{
		public String getContent(){
			return "内容不能为空";
		}
	},
	_103{
		public String getContent(){
			return "话题Id不能为空";
		}
	},
	_104{
		public String getContent(){
			return "引用评论不能为空";
		}
	},
	_105{
		public String getContent(){
			return "评论不存在";
		}
	},
	_106{
		public String getContent(){
			return "禁止评论";
		}
	},
	_107{
		public String getContent(){
			return "话题不存在";
		}
	},
	_108{
		public String getContent(){
			return "实名用户才允许评论";
		}
	},
	_109{
		public String getContent(){
			return "实名用户才允许提交话题";
		}
	},
	_110{
		public String getContent(){
			return "不允许提交话题";
		}
	},
	_111{
		public String getContent(){
			return "已发布话题才允许评论";
		}
	},
	
	/**-------------------------------- 在线留言 -----------------------------------**/	
	_301{
		public String getContent(){
			return "不能超过100个字符";
		}
	},
	_302{
		public String getContent(){
			return "名称不能为空";
		}
	},
	_303{
		public String getContent(){
			return "联系方式不能为空";
		}
	},
	_304{
		public String getContent(){
			return "内容不能为空";
		}
	},
	_305{
		public String getContent(){
			return "在线留言已关闭";
		}
	},
	_306{
		public String getContent(){
			return "字符超长";
		}
	},
	
	/**-------------------------------- 修改用户 -----------------------------------**/	
	_801{
		public String getContent(){
			return "密码长度错误";
		}
	},
	_802{
		public String getContent(){
			return "旧密码错误";
		}
	},
	_803{
		public String getContent(){
			return "旧密码不能为空";
		}
	},
	_804{
		public String getContent(){
			return "只允许输入数字";
		}
	},
	_805{
		public String getContent(){
			return "只允许输入字母";
		}
	},
	_806{
		public String getContent(){
			return "只允许输入数字和字母";
		}
	},
	_807{
		public String getContent(){
			return "只允许输入汉字";
		}
	},
	_808{
		public String getContent(){
			return "输入错误";
		}
	},
	_809{
		public String getContent(){
			return "必填项";
		}
	},
	_810{
		public String getContent(){
			return "修改用户失败";
		}
	},
	_811{
		public String getContent(){
			return "用户名称小于3个字符";
		}
	},
	_812{
		public String getContent(){
			return "用户名称大于25个字符";
		}
	},
	_813{
		public String getContent(){
			return "用户名只能输入由数字、26个英文字母或者下划线组成";
		}
	},
	_814{
		public String getContent(){
			return "该用户名已注册";
		}
	},
	_815{
		public String getContent(){
			return "用户名称不能为空";
		}
	},
	_816{
		public String getContent(){
			return "密码不能为空";
		}
	},
	_817{
		public String getContent(){
			return "密码提示问题不能超过50个字符";
		}
	},
	_818{
		public String getContent(){
			return "密码提示问题不能为空";
		}
	},
	_819{
		public String getContent(){
			return "密码提示答案长度错误";
		}
	},
	_820{
		public String getContent(){
			return "密码提示答案不能为空";
		}
	},
	_821{
		public String getContent(){
			return "Email地址不正确";
		}
	},
	_822{
		public String getContent(){
			return "Email地址不能超过60个字符";
		}
	},
	_823{
		public String getContent(){
			return "注册会员出错";
		}
	},
	_824{
		public String getContent(){
			return "禁止用户";
		}
	},
	_825{
		public String getContent(){
			return "用户名错误";
		}
	},
	_826{
		public String getContent(){
			return "密码错误";
		}
	},
	_827{
		public String getContent(){
			return "密码提示答案错误";
		}
	},
	_828{
		public String getContent(){
			return "找回密码错误";
		}
	},
	_850{
		public String getContent(){
			return "手机验证码错误";
		}
	},
	_851{
		public String getContent(){
			return "手机号不能为空";
		}
	},
	_852{
		public String getContent(){
			return "手机验证码不能为空";
		}
	},
	_853{
		public String getContent(){
			return "手机号码不正确";
		}
	},
	_854{
		public String getContent(){
			return "手机号码超长";
		}
	},
	_855{
		public String getContent(){
			return "手机验证码超长";
		}
	},
	_856{
		public String getContent(){
			return "手机验证码不存在或已过期";
		}
	},
	_857{
		public String getContent(){
			return "手机号码不能重复绑定";
		}
	},
	_858{
		public String getContent(){
			return "你还没有绑定手机";
		}
	},
	_859{
		public String getContent(){
			return "用户不存在";
		}
	},
	_860{
		public String getContent(){
			return "新手机号码不能和旧用机号码相同";
		}
	},
	_861{
		public String getContent(){
			return "旧手机号码校验失败";
		}
	},
	_862{
		public String getContent(){
			return "不允许注册";
		}
	},
	_863{
		public String getContent(){
			return "该用户名不允许注册";
		}
	},
	_910{
		public String getContent(){
			return "用户不存在";
		}
	},
	_1000{
		public String getContent(){
			return "不允许给当前用户发私信";
		}
	},
	_1010{
		public String getContent(){
			return "不允许给自己发私信";
		}
	},
	_1020{
		public String getContent(){
			return "对方用户名称不能为空";
		}
	},
	_1030{
		public String getContent(){
			return "私信内容不能超过1000个字符";
		}
	},
	_1040{
		public String getContent(){
			return "私信内容不能为空";
		}
	},
	_1050{
		public String getContent(){
			return "删除私信失败";
		}
	},
	_1100{
		public String getContent(){
			return "订阅系统通知Id不能为空";
		}
	},
	_1110{
		public String getContent(){
			return "删除系统通知失败";
		}
	},
	_1200{
		public String getContent(){
			return "不能超过8位数字";
		}
	},
	_1210{
		public String getContent(){
			return "宽度必须大于0";
		}
	},
	_1230{
		public String getContent(){
			return "高度必须大于0";
		}
	},
	_1250{
		public String getContent(){
			return "X轴必须大于或等于0";
		}
	},
	_1270{
		public String getContent(){
			return "Y轴必须大于或等于0";
		}
	},
	_1290{
		public String getContent(){
			return "超出最大宽度";
		}
	},
	_1300{
		public String getContent(){
			return "超出最大高度";
		}
	},
	_1310{
		public String getContent(){
			return "当前文件类型不允许上传";
		}
	},
	_1320{
		public String getContent(){
			return "文件超出允许上传大小";
		}
	},
	_1330{
		public String getContent(){
			return "文件不能为空";
		}
	},
	_1400{
		public String getContent(){
			return "提醒不存在";
		}
	},
	_1500{
		public String getContent(){
			return "重复收藏";
		}
	}
	,
	_1510{
		public String getContent(){
			return "话题收藏Id不能为空";
		}
	},
	_1520{
		public String getContent(){
			return "当前话题已经收藏";
		}
	},
	_1530{
		public String getContent(){
			return "收藏Id不存在";
		}
	},
	_1540{
		public String getContent(){
			return "删除收藏失败";
		}
	},
	_1550{
		public String getContent(){
			return "收藏不存在";
		}
	},
	_1560{
		public String getContent(){
			return "本收藏不属于当前用户";
		}
	};
	
	
	
	public abstract String getContent();
	
	
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
