package cms.controller.thumbnail;

import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.dto.thumbnail.ThumbnailRequest;
import cms.service.thumbnail.ThumbnailService;
import cms.validator.thumbnail.ThumbnailValidator;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 缩略图管理控制器
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/control/thumbnail/manage")
public class ThumbnailManageController {
    @Resource
    ThumbnailService thumbnailService;
    @Resource
    MessageSource messageSource;
    @Resource
    ThumbnailValidator thumbnailValidator;
    /**
     * 缩略图 添加界面显示
     */
    @RequestMapping(params="method=add",method= RequestMethod.GET)
    public RequestResult addUI(){
        return new RequestResult(ResultCode.SUCCESS, null);
    }

    /**
     * 缩略图 添加
     * @param thumbnailRequest 短信接口表单
     * @param result 存储校验信息
     * @return
     */
    @RequestMapping(params="method=add",method=RequestMethod.POST)
    public RequestResult add(@ModelAttribute ThumbnailRequest thumbnailRequest, BindingResult result) {
        Map<String, Object> errors = new HashMap<>();
        //数据校验
        thumbnailValidator.validate(thumbnailRequest, result);
        if (result.hasErrors()) {

            for (FieldError fieldError : result.getFieldErrors()) {
                errors.put(fieldError.getField(),  messageSource.getMessage(fieldError, null));
            }
            return new RequestResult(ResultCode.FAILURE, errors);
        }
        thumbnailService.addThumbnail(thumbnailRequest);
        return new RequestResult(ResultCode.SUCCESS, null);
    }


    /**
     * 缩略图 删除
     * @param thumbnailId 缩略图Id
     * @return
     */
    @RequestMapping(params="method=delete",method=RequestMethod.POST)
    public RequestResult delete(Integer thumbnailId) {
        thumbnailService.deleteThumbnail(thumbnailId);
        return new RequestResult(ResultCode.SUCCESS, null);
    }



}
