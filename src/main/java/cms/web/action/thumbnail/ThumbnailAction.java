package cms.web.action.thumbnail;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cms.bean.thumbnail.Thumbnail;
import cms.service.thumbnail.ThumbnailService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

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
	@RequestMapping("/control/thumbnail/list")  
	public String execute(ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {		
		
		
		List<Thumbnail> thumbnailList = thumbnailService.findAllThumbnail();
		
		request.setAttribute("thumbnailList", thumbnailList);

		return "jsp/thumbnail/thumbnailList";
	}
}
