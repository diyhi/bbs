package cms.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 校验类型
 *
 */
public class Verification {
	/** Integer **/
	private static final Pattern INTEGER = Pattern.compile("^-?(([1-9]\\d*$)|0)");
	/** 正整数 不能为0开头**/
	private static final Pattern INTEGER_POSITIVE = Pattern.compile("^[1-9]{1}[\\d]*$");//^[1-9]\\d*$
	/** 正整数和0 **/
	private static final Pattern INTEGER_POSITIVE_ZERO = Pattern.compile("^[0-9]{1}[\\d]*$");//^\\d+$   ^[0-9]\\d*$  [0-9]+    ^[0-9]*$
	/** 负整数 <=0 **/
	private static final Pattern INTEGER_NEGATIVE  = Pattern.compile("^-[1-9]\\d*|0$");
	/** Double **/
	private static final Pattern DOUBLE = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$");
	/** 正Double正则表达式 >=0 **/
	private static final Pattern DOUBLE_NEGATIVE  = Pattern.compile("^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0$");
	/** 负Double **/
	private static final Pattern DOUBLE_POSITIVE  = Pattern.compile("^(-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*))|0?\\.0+|0$");
	/** 国内6位邮编 **/
	private static final Pattern CODE = Pattern.compile("[0-9]\\d{5}(?!\\d)");

	/** 匹配由数字和26个英文字母组成的字符串 **/
	private static final Pattern STR_ENG_NUM = Pattern.compile("^[A-Za-z0-9]+");

	/** 匹配数字组成的字符串 **/
	private static final Pattern STR_NUM = Pattern.compile("^[0-9]+$");
	
	/** 匹配由数字和字母和下划线和星号组成的字符串 **/
	private static final Pattern STRING_1 = Pattern.compile("^[A-Za-z0-9_\\*\\?]+$");
	
	
	/** 邮编 **/
	private static final Pattern ZIP = Pattern.compile("^[0-9]{6}$");

	/** 邮箱 **/
	private static final Pattern EMAIL = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
	
	/** 座机 **/
	private static final Pattern PHONE = Pattern.compile("1([\\d]{10})|((\\+[0-9]{2,4})?\\(?[0-9]+\\)?-?)?[0-9]{7,8}");
	
	
	
	/** 金额类型 **/
	private static final Pattern AMOUNT = Pattern.compile("(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1,2})?$");
	/** 折扣类型 0.1~9.9之间的数 **/
	private static final Pattern DISCOUNT = Pattern.compile("^((0\\.[1-9]?)|([1-9](\\.\\d)?)|(9.9))$");//[1-9](\\.[1-9])?|0\\.[1-9]

	/** 时间 yyyy-MM-dd HH:mm **/
	private static final Pattern TIME_MINUTE = Pattern.compile("((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9]))|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9]))|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9]))|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29) ))\\s((20|21|22|23|[0-1]?\\d):[0-5]?\\d)$");
	
	/** 时间 yyyy-MM-dd HH:mm:ss **/
	private static final Pattern TIME_SECOND = Pattern.compile("((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9]))|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9]))|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9]))|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29))|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29) ))\\s((20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d)$");
	 
	/** 匹配由26个英文字母组成的字符串 **/
	private static final Pattern LETTER = Pattern.compile("^[A-Za-z]+$");
	

	/** 匹配由数字和字母组成的字符串 **/
	private static final Pattern NUMERIC_LETTERS = Pattern.compile("^[A-Za-z0-9]+$");
	
	/** 匹配由数字、26个英文字母或者下划线组成的字符串 **/
	private static final Pattern NUMERIC_LETTERS_UNDERSCORE = Pattern.compile("^\\w+$");//^[a-zA-Z0-9_]{1,}$
	
	/** 匹配汉字 **/
	private static final Pattern CHINESECHARACTER = Pattern.compile("^[\u4e00-\u9fa5]{0,}$");
	
	/** 匹配汉字,字母或数字 **/
	private static final Pattern CHINESECHARACTER_NUMERIC_LETTERS = Pattern.compile("^(\\w|[\u4E00-\u9FA5])*$");
	
	/** 匹配RGB颜色 **/
	private static final Pattern RGB = Pattern.compile("^[rR][gG][Bb][\\(]([\\s]*(2[0-4][0-9]|25[0-5]|[01]?[0-9][0-9]?)[\\s]*,){2}[\\s]*(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)[\\s]*[\\)]{1}$");
	
	
	
	/**
	 * 校验数值类型
	 * @param param 参数
	 * @return
	 */
	public static boolean isNumeric(String param){
		if (INTEGER.matcher(param).matches()) {
			return true;
	    } 
		return false;
	}
	/**
	 * 校验浮点类型
	 * @param param 参数
	 * @return
	 */
	public static boolean isDouble(String param){
		if (DOUBLE.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	/**
	 * 校验正浮点类型   >=0
	 * @param param 参数
	 * @return
	 */
	public static boolean isPositiveDouble(String param){
		if (DOUBLE_NEGATIVE.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	/**
	 * 校验数字类型
	 * @param param 参数
	 * @return
	 */
	public static boolean isNumber(String param){
		if (STR_NUM.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	
	
	/**
	 * 校验正整数
	 * @param param 参数
	 * @return
	 */
	public static boolean isPositiveInteger(String param){
		if (INTEGER_POSITIVE.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 校验正整数+0
	 * @param param 参数
	 * @return
	 */
	public static boolean isPositiveIntegerZero(String param){
		if (INTEGER_POSITIVE_ZERO.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	
	
	/**
	 * 校验金额类型
	 * @param param 参数
	 * @return
	 */
	public static boolean isAmount(String param){
		if (AMOUNT.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 校验折扣类型
	 * @param param 参数
	 * @return
	 */
	public static boolean isDiscount(String param){
		if (DISCOUNT.matcher(param).matches()) {
			return true;
	    }
		return false;
	}

    /**
     * 校验 BigDecimal 值最多只有两位小数。
     * @param value 待校验的 BigDecimal 值
     * @return 如果小数位数小于等于 2，返回 true；否则返回 false。
     */
    public static boolean hasAtMostTwoDecimalPlaces(BigDecimal value) {
        if (value == null) {
            return false;
        }
        // 使用 stripTrailingZeros() 移除所有尾随零。
        //    例如: 1.2300 -> 1.23; 10.00 -> 10.
        BigDecimal normalizedValue = value.stripTrailingZeros();

        // 获取规范化后的 scale (小数位数)。
        // 如果 scale <= 2，则该数字最多只有两位小数。
        // 注意：对于整数 (如 10.00 规范化为 10)，scale() 返回 0。
        return normalizedValue.scale() <= 2;
    }
	
	/**
	 * 校验时间类型(yyyy-MM-dd HH:mm)
	 * @param param 参数
	 * @return
	 */
	public static boolean isTime_minute(String param){
		if (TIME_MINUTE.matcher(param).matches()) {
			return true;
	    } 
		return false;
	}
	/**
	 * 校验时间类型(yyyy-MM-dd HH:mm:ss)
	 * @param param 参数
	 * @return
	 */
	public static boolean isTime_second(String param){
		if (TIME_SECOND.matcher(param).matches()) {
			return true;
	    } 
		return false;
	}
	
	/**
	 * 只能输入由数字、26个英文字母或者下划线组成
	 * @param param 参数
	 * @return
	 */
	public static boolean isUserName(String param){
		return false;
		
	}
	
	/**
	 * 匹配由数字和字母和下划线和星号组成的字符串
	 * @param param 参数
	 * @return
	 */
	public static boolean isString_1(String param){
		if (STRING_1.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	
	/**
	 * 只能输入26个英文字母组成
	 * @param param 参数
	 * @return
	 */
	public static boolean isLetter(String param){
		if (LETTER.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 只能输入由数字、26个英文字母组成
	 * @param param 参数
	 * @return
	 */
	public static boolean isNumericLetters(String param){
		if (NUMERIC_LETTERS.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 只能输入由数字、26个英文字母或者下划线组成
	 * @param param 参数
	 * @return
	 */
	public static boolean isNumericLettersUnderscore(String param){
		if (NUMERIC_LETTERS_UNDERSCORE.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 验证邮编
	 * @param param 参数
	 * @return
	 */
	public static boolean isZip(String param){
		if (ZIP.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 验证邮箱
	 * @param param 参数
	 * @return
	 */
	public static boolean isEmail(String param){
		if (EMAIL.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	/**
	 * 验证座机
	 * @param param 参数
	 * @return
	 */
	public static boolean isPhone(String param){
		if (PHONE.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	
	/**
	 * 验证汉字
	 * @param param 参数
	 * @return
	 */
	public static boolean isChineseCharacter(String param){
		if (CHINESECHARACTER.matcher(param).matches()) {
			return true;
	    }
		return false;
	}
	/**
	 * 校验RGB颜色
	 * @param param 参数
	 * @return
	 */
	public static boolean isRGB(String param){
		if (RGB.matcher(param).matches()) {
			return true;
	    } 
		return false;
	}
	
}
