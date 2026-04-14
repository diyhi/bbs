package cms.service.report.impl;


import cms.component.report.ReportTypeComponent;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.dto.report.ReportTypeRequest;
import cms.model.report.ReportType;
import cms.repository.report.ReportTypeRepository;
import cms.repository.setting.SettingRepository;
import cms.service.report.ReportTypeService;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 举报分类服务
 */
@Service
public class ReportTypeServiceImpl implements ReportTypeService {

    @Resource
    ReportTypeRepository reportTypeRepository;
    @Resource
    SettingRepository settingRepository;
    @Resource
    ReportTypeComponent reportTypeComponent;


    /**
     * 获取举报分类列表
     * @param page 页码
     * @param parentId 父Id
     */
    public Map<String,Object> getReportTypeList(int page,String parentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();

        StringBuffer jpql = new StringBuffer("");
        //存放参数值
        List<Object> params = new ArrayList<Object>();


        //如果所属父类有值
        if(parentId != "" && !"0".equals(parentId.trim())){
            jpql.append(" and o.parentId=?"+ (params.size()+1));//所属父类的ID;(params.size()+1)是为了和下面的条件参数兼容
            params.add(parentId);//设置o.parentId=?2参数
        }else{//如果没有父类
            jpql.append(" and o.parentId=?"+ (params.size()+1));
            params.add("0");
        }

        PageView<ReportType> pageView = new PageView<ReportType>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(),page,10);
        //当前页
        int firstindex = (page-1)*pageView.getMaxresult();;
        //排序
        LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();

        orderby.put("sort", "desc");//根据sort字段降序排序
        orderby.put("id", "desc");

        //删除第一个and
        String _jpql = org.apache.commons.lang3.StringUtils.difference(" and", jpql.toString());

        //调用分页算法类
        QueryResult<ReportType> qr = reportTypeRepository.getScrollData(ReportType.class, firstindex, pageView.getMaxresult(), _jpql, params.toArray(),orderby);


        pageView.setQueryResult(qr);
        returnValue.put("pageView", pageView);

        //分类路径
        if(parentId != "" && !"".equals(parentId.trim()) && !"0".equals(parentId.trim())){
            Map<String,String> navigation = new LinkedHashMap<String,String>();
            ReportType t = reportTypeRepository.findById(parentId);
            if(t != null){
                List<ReportType> parentTagList = reportTypeRepository.findAllParentById(t);
                for(ReportType p : parentTagList){
                    navigation.put(p.getId(), p.getName());
                }
                navigation.put(t.getId(), t.getName());
            }

            returnValue.put("navigation", navigation);//标签导航
        }
        return returnValue;
    }



    /**
     * 获取添加举报分类界面信息
     * @param parentId 父Id
     * @return
     */
    public Map<String,Object> getAddReportTypeViewModel(String parentId){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        if(parentId != null && !parentId.trim().isEmpty()){//判断父类ID是否存在;
            ReportType _type = reportTypeRepository.findById(parentId);
            if(_type == null) {
                throw new BusinessException(Map.of("parentId", "父类不存在"));
            }
            returnValue.put("parentType", _type);//返回消息


            Map<String,String> navigation = new LinkedHashMap<String,String>();

            List<ReportType> parentProductTypeList = reportTypeRepository.findAllParentById(_type);
            for(ReportType p : parentProductTypeList){
                navigation.put(p.getId(), p.getName());
            }
            navigation.put(_type.getId(),_type.getName());
            returnValue.put("navigation", navigation);//分类导航
        }
        return returnValue;
    }

    /**
     * 添加举报分类
     * @param reportTypeRequest 举报分类表单
     */
    public void addReportType(ReportTypeRequest reportTypeRequest){


        Map<String, String> errors = new HashMap<String, String>();

        validateInput(reportTypeRequest,errors);


        String parentIdGroup = ",0,";
        if(reportTypeRequest.getParentId() != null && !reportTypeRequest.getParentId().trim().isEmpty()){//判断父类ID是否存在;
            if(!"0".equals(reportTypeRequest.getParentId().trim())){
                //取得父对象
                ReportType t = reportTypeRepository.findById(reportTypeRequest.getParentId().trim());
                if(t != null){
                    //根据分类查询所有父分类
                    List<ReportType> allParentType = reportTypeRepository.findAllParentById(t);
                    if(allParentType.size() >=1){
                        errors.put("type", "分类已达到最大层数,添加失败");
                    }
                    parentIdGroup = t.getParentIdGroup()+reportTypeRequest.getParentId().trim()+",";
                }else{
                    errors.put("type", "父分类不存在");
                }
            }
        }else{
            reportTypeRequest.setParentId("0");
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        ReportType reportType = new ReportType();
        reportType.setId(UUIDUtil.getUUID32());
        reportType.setName(reportTypeRequest.getName().trim());
        reportType.setGiveReason(reportTypeRequest.getGiveReason() == null ? false:reportTypeRequest.getGiveReason());
        reportType.setParentId(reportTypeRequest.getParentId().trim());
        reportType.setParentIdGroup(parentIdGroup);
        reportType.setSort(reportTypeRequest.getSort());
        reportTypeRepository.saveReportType(reportType);

    }


    /**
     * 获取修改举报分类界面信息
     * @param reportTypeId 举报分类Id
     * @return
     */
    public Map<String,Object> getEditReportTypeViewModel(String reportTypeId){
        if(reportTypeId == null || reportTypeId.trim().isEmpty()){
            throw new BusinessException(Map.of("reportTypeId", "举报分类Id不能为空"));
        }
        ReportType reportType = reportTypeRepository.findById(reportTypeId);

        if(reportType == null){
            throw new BusinessException(Map.of("reportTypeId", "举报分类不存在"));
        }
        Map<String,Object> returnValue = new HashMap<String,Object>();
        returnValue.put("reportType",reportType);//返回消息

        int i=0;
        Map<String,String> navigation = new LinkedHashMap<String,String>();
        List<ReportType> parentTypeList = reportTypeRepository.findAllParentById(reportType);
        for(ReportType r : parentTypeList){
            navigation.put(r.getId(), r.getName());
            i++;
            if(i ==1){
                returnValue.put("parentType", r);

            }

        }
        returnValue.put("navigation", navigation);//分类导航

        return returnValue;
    }
    /**
     * 修改举报分类
     * @param reportTypeRequest 举报分类表单
     */
    public void editReportType(ReportTypeRequest reportTypeRequest){
        if(reportTypeRequest.getReportTypeId() == null || reportTypeRequest.getReportTypeId().trim().isEmpty()){
            throw new BusinessException(Map.of("reportTypeId", "举报分类Id不能为空"));
        }

        ReportType type = reportTypeRepository.findById(reportTypeRequest.getReportTypeId());
        if(type == null){
            throw new BusinessException(Map.of("reportTypeId", "举报分类不存在"));
        }

        Map<String, String> errors = new HashMap<String, String>();

        validateInput(reportTypeRequest,errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        ReportType reportType = new ReportType();
        reportType.setId(reportTypeRequest.getReportTypeId());
        reportType.setName(reportTypeRequest.getName().trim());
        reportType.setSort(reportTypeRequest.getSort());
        reportType.setGiveReason(reportTypeRequest.getGiveReason() == null ? false:reportTypeRequest.getGiveReason());

        reportTypeRepository.updateReportType(reportType);
    }

    /**
     * 删除举报分类
     * @param reportTypeId 举报分类Id
     */
    public void deleteReportType(String reportTypeId){
        if(reportTypeId == null || reportTypeId.trim().isEmpty()){
            throw new BusinessException(Map.of("reportTypeId", "举报分类Id不能为空"));
        }
        ReportType type = reportTypeRepository.findById(reportTypeId);

        //根据分类Id查询子分类(下一节点)
        List<ReportType> childTypeList = reportTypeRepository.findChildTypeById(reportTypeId);

        List<String> typeIdList = new ArrayList<String>();
        typeIdList.add(type.getId());
        if(childTypeList != null && childTypeList.size() >0){
            for(ReportType t : childTypeList){
                typeIdList.add(t.getId());
            }
        }

        reportTypeRepository.deleteReportType(type);
    }

    /**
     * 查询所有举报分类
     * @return
     */
    public List<ReportType> getAllType(){
        List<ReportType> reportTypeList =  reportTypeRepository.findAllReportType();
        List<ReportType> new_reportTypeList = new ArrayList<ReportType>();//排序后分类

        if(reportTypeList != null && reportTypeList.size() >0){

            //组成排序数据
            Iterator<ReportType> reportType_iter = reportTypeList.iterator();
            while(reportType_iter.hasNext()){
                ReportType reportType = reportType_iter.next();

                //如果是根节点
                if(reportType.getParentId().equals("0")){

                    new_reportTypeList.add(reportType);
                    reportType_iter.remove();
                }
            }
            //组合子分类
            for(ReportType reportType :new_reportTypeList){
                reportTypeComponent.childReportType(reportType,reportTypeList);
            }
            //排序
            reportTypeComponent.reportTypeSort(new_reportTypeList);
        }
        return new_reportTypeList;
    }

    /**
     * 校验表单字段
     * @param reportTypeRequest 举报分类表单
     * @param errors 错误信息
     */
    private void validateInput(ReportTypeRequest reportTypeRequest,Map<String, String> errors) {
        if(reportTypeRequest.getName() == null || reportTypeRequest.getName().trim().isEmpty()){
            errors.put("name", "分类名称不能为空");
        }else{
            if(reportTypeRequest.getName().length() >190){
                errors.put("name", "不能大于190个字符");
            }
        }
        if(reportTypeRequest.getSort() != null){
            if(reportTypeRequest.getSort() < 0){
                errors.put("sort", "排序必须为大于或等于0的数字");
            }
            if(reportTypeRequest.getSort() > 99999999){
                errors.put("sort", "不能大于8位数字");
            }
        }else{
            errors.put("sort", "排序不能为空");
        }

    }


}
