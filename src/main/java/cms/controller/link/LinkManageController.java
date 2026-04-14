package cms.controller.link;

import cms.component.fileSystem.FileComponent;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.link.LinkRequest;
import cms.service.link.LinkService;
import cms.validator.link.LinkValidator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 友情链接管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/links/manage")
public class LinkManageController {
    @Resource
    LinkService linkService;
    @Resource
    MessageSource messageSource;
    @Resource
    LinkValidator linkValidator;
    @Resource
    FileComponent fileComponent;


    /**
     * 友情链接 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 友情链接 添加
     * @param linkRequest 友情链接表单
     * @param result 存储校验信息
     * @param images 图片
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute LinkRequest
                                         linkRequest, BindingResult result,
                             MultipartFile images) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        linkValidator.validate(linkRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        linkService.addLink(linkRequest,images);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 友情链接 显示修改
     * @param linksId 友情链接Id
     * @param request 请求信息
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.GET)
    public RequestResult editUI(Integer linksId, HttpServletRequest request) {
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = linkService.getEditLinkViewModel(linksId,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 友情链接 修改
     * @param linkRequest 友情链接表单
     * @param result 存储校验信息
     * @param images 图片
     * @return
     */
    @RequestMapping(params="method=edit",method=RequestMethod.POST)
    public RequestResult edit(@ModelAttribute LinkRequest linkRequest, BindingResult result,
                              MultipartFile images) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        linkValidator.validate(linkRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        linkService.editLink(linkRequest,images);
        return new RequestResult(ResultCode.SUCCESS, null);

    }

    /**
     * 友情链接 删除
     * @param linksId 友情链接Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer linksId) {
        linkService.deleteLink(linksId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
