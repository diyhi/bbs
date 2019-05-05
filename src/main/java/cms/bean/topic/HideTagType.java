package cms.bean.topic;

/**
 * 隐藏标签类型
 *
 */
public enum HideTagType {
	//输入密码可见
	PASSWORD{
		public Integer getName(){
			return 10;
		}
	},
	//评论话题可见
	COMMENT{
		public Integer getName(){
			return 20;
		}
	},
	//达到等级可见
	GRADE{
		public Integer getName(){
			return 30;
		}
	},
	//积分购买可见
	POINT{
		public Integer getName(){
			return 40;
		}
	},
	//余额购买可见
	AMOUNT{
		public Integer getName(){
			return 50;
		}
	};
	public abstract Integer getName();
}
