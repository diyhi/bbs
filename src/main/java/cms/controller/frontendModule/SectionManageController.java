package cms.controller.frontendModule;

import cms.component.frontendModule.SectionComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.model.frontendModule.Section;
import cms.service.frontendModule.SectionService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点栏目管理控制器
 */
@RestController
@RequestMapping("/control/section/manage")
public class SectionManageController {
    @Resource SectionService sectionService;
    @Resource
    SectionComponent sectionComponent;
    /**
     * 栏目列表
     * @return
     */
    @RequestMapping(params="method=list",method= RequestMethod.GET)
    public RequestResult sectionList(){
        Map<String,Object> returnValue = new HashMap<String,Object>();
        List<Section> sectionList = sectionComponent.getSectionList();
        returnValue.put("sectionList",sectionList);

        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }


    /**
     * 栏目管理 添加
     * @param sectionForm 栏目表单
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(Section sectionForm, HttpServletRequest request){
        sectionService.addSection(sectionForm,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 栏目管理 修改
     * @param sectionForm 栏目表单
     * @param sectionId 栏目Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(Section sectionForm,Integer sectionId, HttpServletRequest request){
        sectionForm.setId(sectionId);
        sectionService.editSection(sectionForm,request);
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 栏目管理  删除
     * @param sectionId 栏目Id
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer sectionId){
        sectionService.deleteSection(sectionId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }
}
