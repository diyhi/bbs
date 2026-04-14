package cms.service.user.impl;

import cms.component.JsonComponent;
import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.user.DisableUserNameRequest;
import cms.model.user.DisableUserName;
import cms.repository.setting.SettingRepository;
import cms.repository.user.UserRepository;
import cms.service.user.DisableUserNameService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 禁止的用户名称服务
 */
@Service
public class DisableUserNameServiceImpl implements DisableUserNameService {


    @Resource
    UserRepository userRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    JsonComponent jsonComponent;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FileComponent fileComponent;

    /**
     * 获取禁止的用户名称列表
     *
     * @param page 页码
     */
    public PageView<DisableUserName> getDisableUserNameList(int page){
        //调用分页算法代码
        PageView<DisableUserName> pageView = new PageView<DisableUserName>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("id", "desc");//降序排序
        QueryResult<DisableUserName> qr = userRepository.getScrollData(DisableUserName.class,firstindex, pageView.getMaxresult(),orderby);
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);
        return pageView;
    }

    /**
     * 添加禁止的用户名称
     * @param disableUserNameRequest 禁止的用户名称表单
     */
    public void addDisableUserName(DisableUserNameRequest disableUserNameRequest){
        Map<String, String> errors = new HashMap<>();
        DisableUserName disableUserName = new DisableUserName();
        processAndValidateDisableUserNameFields(disableUserNameRequest, errors);
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        disableUserName.setName(disableUserNameRequest.getName().trim());
        userRepository.saveDisableUserName(disableUserName);
    }

    /**
     * 获取修改禁止的用户名称界面信息
     * @param disableUserNameId 禁止的用户名称Id
     * @return
     */
    public Map<String,Object> getEditDisableUserNameViewModel(Integer disableUserNameId) {
        if (disableUserNameId == null) {
            throw new BusinessException(Map.of("disableUserNameId", "禁止的用户名称Id不能为空"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        DisableUserName disableUserName = userRepository.findDisableUserNameById(disableUserNameId);

        if(disableUserName == null){
            throw new BusinessException(Map.of("disableUserName", "禁止的用户名称不存在"));
        }
        returnValue.put("disableUserName", disableUserName);
        return returnValue;
    }

    /**
     * 修改禁止的用户名称
     * @param disableUserNameRequest 禁止的用户名称表单
     */
    public void editDisableUserName(DisableUserNameRequest disableUserNameRequest){

        if (disableUserNameRequest.getDisableUserNameId() == null) {
            throw new BusinessException(Map.of("disableUserNameId", "禁止的用户名称Id不能为空"));
        }
        DisableUserName oldDisableUserName = userRepository.findDisableUserNameById(disableUserNameRequest.getDisableUserNameId());
        if(oldDisableUserName == null){
            throw new BusinessException(Map.of("disableUserName", "禁止的用户名称不存在"));
        }


        Map<String, String> errors = new HashMap<>();
        DisableUserName disableUserName = new DisableUserName();
        processAndValidateDisableUserNameFields(disableUserNameRequest, errors);
        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }
        disableUserName.setId(disableUserNameRequest.getDisableUserNameId());
        disableUserName.setName(disableUserNameRequest.getName().trim());
        userRepository.updateDisableUserName(disableUserName);
    }

    /**
     * 删除禁止的用户名称
     * @param disableUserNameId 禁止的用户名称Id
     */
    public void deleteUDisableUserName(Integer disableUserNameId){
        if (disableUserNameId == null) {
            throw new BusinessException(Map.of("disableUserNameId", "禁止的用户名称Id不存在"));
        }
        userRepository.deleteDisableUserName(disableUserNameId);
    }


    /**
     * 处理禁止的用户名称字段的解析和校验
     * @param disableUserNameRequest 禁止的用户名称表单
     * @param errors         错误集合
     */
    private void processAndValidateDisableUserNameFields(DisableUserNameRequest disableUserNameRequest, Map<String, String> errors) {

        if(disableUserNameRequest.getName() != null && !disableUserNameRequest.getName().isEmpty()){
            if("*".equals(disableUserNameRequest.getName().trim())){
                errors.put("name", "不能仅一个*号");
            }
            if(disableUserNameRequest.getName().trim().indexOf("**")!=-1){
                errors.put("name", "不能连着*号");
            }
            if("?".equals(disableUserNameRequest.getName().trim())){
                errors.put("name", "不能仅一个?号");
            }
            //移除所有相同的部分
            String _name = Strings.CS.remove(disableUserNameRequest.getName().trim(), "?");


            if(_name == null || _name.trim().isEmpty()){
                errors.put("name", "不能仅用?号");
            }
            //if(Verification.isString_1(formbean.getName().trim()) == false){
            //	error.put("name", "只能输入由数字、26个英文字母、星号、问号或者下划线组成");//只能输入由数字、26个英文字母、星号或者下划线组成
            //}
        }else{
            errors.put("name", "不能为空");
        }
    }

}