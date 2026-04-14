package cms.dto.user;


import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户自定义注册功能项表单
 */
@Getter
@Setter
public class UserCustomRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -9146423177028430699L;

    /** 用户自定义注册功能项Id **/
    private Integer id;
    /** 注册项名称 **/
    private String name;
    /** 是否必填 **/
    private boolean required = false;
    /** 后台可搜索 **/
    private boolean search = false;
    /** 是否显示 **/
    private boolean visible = true;
    /** 选框类型  1.输入框 2.单选按钮  3.多选按钮 4.下拉列表  5.文本域textarea   **/
    private Integer chooseType = 1;
    /** 字段过滤方式  0.无  1.只允许输入数字  2.只允许输入字母  3.只允许输入数字和字母  4.只允许输入汉字  5.正则表达式过滤 **/
    private Integer fieldFilter = 0;

    /** 输入框的宽度 **/
    private Integer size;
    /** 输入框中字符的最大长度 **/
    private Integer maxlength;


    /** 是否可选择多个选项 true: multiple='multiple'  false: **/
    private boolean multiple;
    /** 下拉列表中可见选项的数目 **/
    private Integer selete_size;
    /** 单选按钮键,多选按钮键,下拉列表键 **/
    private String[] itemKey;
    /** 单选按钮值,多选按钮值,下拉列表值 **/
    private String[] itemValue;

    /** 文本域内的可见行数 **/
    private Integer rows;
    /** 文本域内的可见宽度 **/
    private Integer cols;

    /** 过滤正则表达式 **/
    private String regular;
    /** 提示 Tip cue**/
    private String tip;
    /** 排序 **/
    private Integer sort;

}
