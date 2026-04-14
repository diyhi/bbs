package cms.controller.favorite;

import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.RequestResult;
import cms.dto.ResultCode;
import cms.service.favorite.FavoriteService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 收藏夹列表控制器
 *
 */
@RestController
public class FavoriteController {
    @Resource
    FavoriteService favoriteService;
    @Resource
    FileComponent fileComponent;

    /**
     * 收藏夹列表
     * @param pageForm 页码
     * @param id 用户Id
     * @param userName 用户名称
     * @param request 请求信息
     * @return
     */
    @RequestMapping("/control/favorite/list")
    public RequestResult favoriteList(PageForm pageForm, Long id, String userName, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        Map<String,Object> returnValue = favoriteService.getFavoriteList(pageForm.getPage(),id,userName,fileServerAddress);
        return new RequestResult(ResultCode.SUCCESS, returnValue);
    }

    /**
     * 话题收藏列表
     * @param pageForm 页码
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
     @RequestMapping("/control/topicFavorite/list")
     public RequestResult topicFavoriteList(PageForm pageForm,Long topicId,HttpServletRequest request){
         String fileServerAddress = fileComponent.fileServerAddress(request);
         Map<String,Object> returnValue = favoriteService.getTopicFavorites(pageForm.getPage(),topicId,fileServerAddress);
         return new RequestResult(ResultCode.SUCCESS, returnValue);
     }

    /**
     * 问题收藏列表
     * @param pageForm 页码
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
     @RequestMapping("/control/questionFavorite/list")
     public RequestResult questionFavoriteList(PageForm pageForm,Long questionId,HttpServletRequest request){
         String fileServerAddress = fileComponent.fileServerAddress(request);
         Map<String,Object> returnValue  = favoriteService.getQuestionFavorites(pageForm.getPage(),questionId,fileServerAddress);
         return new RequestResult(ResultCode.SUCCESS, returnValue);
     }
}
