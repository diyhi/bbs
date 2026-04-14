package cms.service.user.impl;

import cms.component.JsonComponent;
import cms.config.BusinessException;
import cms.dto.user.UserCustomRequest;
import cms.model.user.UserCustom;
import cms.repository.user.UserCustomRepository;
import cms.service.user.UserCustomService;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.*;

/**
 * 用户自定义注册功能项服务
 */
@Service
public class UserCustomServiceImpl implements UserCustomService {

    @Resource
    JsonComponent jsonComponent;
    @Resource
    UserCustomRepository userCustomRepository;


    /**
     * 获取所有用户自定义注册功能项
     */
    public List<UserCustom> getAllUserCustom(){
        return userCustomRepository.findAllUserCustom();
    }

    /**
     * 添加用户自定义注册功能项
     * @param userCustomRequest 用户自定义注册功能项表单
     */
    public void addUserCustom(UserCustomRequest userCustomRequest){
        Map<String, String> errors = new HashMap<>();

        processAndValidateUserCustomFields(userCustomRequest,  errors);

        UserCustom userCustom = new UserCustom();
        LinkedHashMap<String,String> newItemValueMap = new LinkedHashMap<String,String>();//key:选项值 value:选项文本

        validateAndFillCustomFields(userCustom, userCustomRequest,errors, null,new LinkedHashMap<String,String>(),newItemValueMap);


        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }


        userCustom.setName(userCustomRequest.getName());
        userCustom.setSort(userCustomRequest.getSort());
        userCustom.setRequired(userCustomRequest.isRequired());//是否必填
        userCustom.setSearch(userCustomRequest.isSearch());//后台可搜索
        userCustom.setVisible(userCustomRequest.isVisible());//是否显示
        userCustom.setChooseType(userCustomRequest.getChooseType());//选框类型
        userCustom.setTip(userCustomRequest.getTip());
        userCustomRepository.saveUserCustom(userCustom);
    }

    /**
     * 获取修改用户自定义注册功能项界面信息
     * @param userCustomId 用户自定义注册功能项Id
     * @return
     */
    public Map<String,Object> getEditUserCustomViewModel(Integer userCustomId){
        if (userCustomId == null) {
            throw new BusinessException(Map.of("id", "用户自定义注册功能项Id不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        UserCustom userCustom = userCustomRepository.findUserCustomById(userCustomId);
        if (userCustom == null) {
            throw new BusinessException(Map.of("id", "用户自定义注册功能项不存在"));
        }
        returnValue.put("userCustom", userCustom);
        if(userCustom.getValue() != null && !userCustom.getValue().isEmpty()){

            LinkedHashMap<String,String> itemValue_map = jsonComponent.toGenericObject(userCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){});//key:选项值 value:选项文本
            returnValue.put("itemValue_map", itemValue_map);
        }

        return returnValue;
    }

    /**
     * 修改用户自定义注册功能项
     * @param userCustomRequest 用户自定义注册功能项表单
     */
    public void editUserCustom(UserCustomRequest userCustomRequest){
        if (userCustomRequest.getId() == null) {
            throw new BusinessException(Map.of("id", "用户自定义注册功能项Id不存在"));
        }
        UserCustom oldUserCustom = userCustomRepository.findUserCustomById(userCustomRequest.getId());
        if (oldUserCustom == null) {
            throw new BusinessException(Map.of("id", "用户自定义注册功能项不存在"));
        }

        if(oldUserCustom.getValue() != null && !oldUserCustom.getValue().isEmpty()){
            oldUserCustom.setItemValue(jsonComponent.toGenericObject(oldUserCustom.getValue(), new TypeReference<LinkedHashMap<String,String>>(){}));//key:选项值 value:选项文本
        }

        Map<String, String> errors = new HashMap<>();
        UserCustom userCustom = new UserCustom();
        LinkedHashMap<String,String> newItemValueMap = new LinkedHashMap<String,String>();//key:选项值 value:选项文本

        validateAndFillCustomFields(userCustom, userCustomRequest,errors, userCustomRequest.getItemKey(),oldUserCustom.getItemValue(),newItemValueMap);


        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }


        userCustom.setId(userCustomRequest.getId());
        userCustom.setName(userCustomRequest.getName());
        userCustom.setSort(userCustomRequest.getSort());
        userCustom.setRequired(userCustomRequest.isRequired());//是否必填
        userCustom.setSearch(userCustomRequest.isSearch());//后台可搜索
        userCustom.setVisible(userCustomRequest.isVisible());//是否显示

        userCustom.setChooseType(oldUserCustom.getChooseType());//选框类型不能改
        userCustom.setTip(userCustomRequest.getTip());

        //删除自定义单选按钮.多选按钮.下拉列表
        List<String> deleteItem = new ArrayList<String>();
        if(userCustom.getChooseType().equals(2) || userCustom.getChooseType().equals(3) || userCustom.getChooseType().equals(4)){
            LinkedHashMap<String,String> oldItemValue_map = oldUserCustom.getItemValue();

            for(Map.Entry<String, String> itemValue : newItemValueMap.entrySet()){
                oldItemValue_map.remove(itemValue.getKey());
            }

            deleteItem.addAll(oldItemValue_map.keySet());
        }
        userCustomRepository.updateUserCustom(userCustom, deleteItem);
    }




    /**
     * 删除用户自定义注册功能项
     * @param id 用户自定义注册功能项Id
     */
    public void deleteUserCustom(Integer id){
        if (id == null) {
            throw new BusinessException(Map.of("id", "用户自定义注册功能项Id不存在"));
        }
        userCustomRepository.deleteUserCustom(id);
    }

    /**
     * 处理用户自定义注册功能项字段的解析和校验
     * @param userCustomRequest       用户自定义注册功能项表单
     * @param errors         错误集合
     */
    private void processAndValidateUserCustomFields(UserCustomRequest userCustomRequest, Map<String, String> errors) {

        if(userCustomRequest.getName() != null && !userCustomRequest.getName().isEmpty()){//验证注册项名称
            if(userCustomRequest.getName().length() >50){
                errors.put("name", "注册项名称不能超过50个字符");
            }
        }else{
            errors.put("name", "请填写注册项名称");
        }

        if(userCustomRequest.getSort() != null && userCustomRequest.getSort() <=0){
            errors.put("sort", "请填写大于0的整数");
        }

        if(userCustomRequest.getTip() != null && !userCustomRequest.getTip().isEmpty()){//提示
            if(userCustomRequest.getTip().length() >250){
                errors.put("tip", "提示不能超过50个字符");
            }
        }
    }

    /**
     * 校验并填充 UserCustom 对象的动态字段
     * @param userCustom 用户自定义注册功能项
     * @param userCustomRequest 用户自定义注册功能项请求参数
     * @param errors 错误收集 Map
     * @param itemKeys (可选) 修改操作时传入的旧选项Key（单选按钮Key,多选按钮Key,下拉列表Key）
     * @param oldItemValue (可选) 修改操作时传入的旧选项值（单选按钮值,多选按钮值,下拉列表值）
     * @param newItemValueMap 新的选项值
     */
    private void validateAndFillCustomFields(UserCustom userCustom, UserCustomRequest userCustomRequest,
                                             Map<String, String> errors, String[] itemKeys,LinkedHashMap<String,String> oldItemValue,LinkedHashMap<String,String> newItemValueMap) {

        if(userCustomRequest.getChooseType().equals(1)){//输入框
            if(userCustomRequest.getSize() != null){//输入框的宽度
                if(userCustomRequest.getSize() <=0){
                    errors.put("size", "请填写正整数");
                }
                userCustom.setSize(userCustomRequest.getSize());
            }
            if(userCustomRequest.getMaxlength() != null){//输入框中字符的最大长度
                if(userCustomRequest.getMaxlength() <=0){
                    errors.put("maxlength", "请填写正整数");
                }
                userCustom.setMaxlength(userCustomRequest.getMaxlength());
            }
            userCustom.setFieldFilter(userCustomRequest.getFieldFilter());
            if(userCustomRequest.getFieldFilter().equals(5)){//5.正则表达式
                if(userCustomRequest.getRegular() == null || userCustomRequest.getRegular().trim().isEmpty()){
                    errors.put("regular", "请填写正则表达式");
                }else{
                    userCustom.setRegular(userCustomRequest.getRegular().trim());
                }
            }

        }

        if(userCustomRequest.getChooseType().equals(2) || userCustomRequest.getChooseType().equals(3) || userCustomRequest.getChooseType().equals(4)){
            if(userCustomRequest.getItemValue() != null && userCustomRequest.getItemValue().length >0){
                for(int i = 0; i< userCustomRequest.getItemValue().length; i++){
                    String item = userCustomRequest.getItemValue()[i];
                    if(item != null && !item.trim().isEmpty()){
                        if(itemKeys != null && itemKeys.length >0){
                            String oldItemKey = itemKeys[i];//旧key
                            if(oldItemKey != null && !oldItemKey.trim().isEmpty()){
                                if(oldItemValue.get(oldItemKey) != null){//已存在key
                                    newItemValueMap.put(oldItemKey, item);
                                    continue;
                                }
                            }
                        }
                        newItemValueMap.put(UUIDUtil.getUUID32(), item);
                    }
                }
            }else{
                errors.put("itemValue", "请填写选项值");
            }
            if(newItemValueMap.isEmpty()){
                errors.put("itemValue", "请填写选项值");
            }
            userCustom.setValue(jsonComponent.toJSONString(newItemValueMap));
        }
        if(userCustomRequest.getChooseType().equals(4)){//4.下拉列表
            userCustom.setMultiple(userCustomRequest.isMultiple());//是否可选择多个选项

            if(userCustomRequest.getSelete_size() != null){//下拉列表中可见选项的数目
                if(userCustomRequest.getSelete_size() <=0){
                    errors.put("selete_size", "请填写正整数");
                }else{
                    userCustom.setSelete_size(userCustomRequest.getSelete_size());
                }
            }

        }
        if(userCustomRequest.getChooseType().equals(5)){//5.文本域
            if(userCustomRequest.getRows() != null){//文本域内的可见行数
                if(userCustomRequest.getRows() <=0){
                    errors.put("rows", "请填写正整数");
                }else{
                    userCustom.setRows(userCustomRequest.getRows());
                }
            }
            if(userCustomRequest.getCols() != null){//文本域内的可见宽度
                if(userCustomRequest.getCols() <=0){
                    errors.put("cols", "请填写正整数");
                }else{
                    userCustom.setCols(userCustomRequest.getCols());
                }
            }
        }

    }
}