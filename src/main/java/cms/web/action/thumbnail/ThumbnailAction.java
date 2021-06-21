package cms.web.action.thumbnail;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.RequestResult;
import cms.bean.ResultCode;
import cms.bean.thumbnail.Thumbnail;
import cms.service.thumbnail.ThumbnailService;
import cms.utils.JsonUtils;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 缩略图列表
 *
 */
@Controller
public class ThumbnailAction {
	@Resource ThumbnailService thumbnailService;
	
	/**
	 * 
	 * @param model
	 * @param pageForm
	 * @param processStatus 流程状态
	 * @param start_createDate 起始时间
	 * @param end_createDate 结束时间
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/control/thumbnail/list")  
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		
		List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail();
		return JsonUtils.toJSONString(new RequestResult(ResultCode.SUCCESS,thumbnailList));
	}
}
