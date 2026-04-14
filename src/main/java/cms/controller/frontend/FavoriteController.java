package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.FavoritedStatusDTO;
import cms.dto.frontendModule.FavoriteCountDTO;
import cms.dto.user.ResourceEnum;
import cms.model.favorite.Favorites;
import cms.service.frontend.FavoriteClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台收藏夹控制器
 */
@RestController("frontendFavoriteController")
public class FavoriteController {

    @Resource FavoriteClientService favoriteClientService;
    @Resource
    FileComponent fileComponent;

    /**
     * 话题收藏总数
     * @param topicId 话题Id
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6030100)
    public FavoriteCountDTO topicFavoriteCount(Long topicId){
        return favoriteClientService.getTopicFavoriteCount(topicId);
    }

    /**
     * 用户是否已经收藏该话题
     * @param topicId 话题Id
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6030200)
    public FavoritedStatusDTO isTopicFavorited(Long topicId){
        return favoriteClientService.isTopicFavorited(topicId);
    }

    /**
     * 问题收藏总数
     * @param questionId 问题Id
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6030300)
    public FavoriteCountDTO questionFavoriteCount(Long questionId){
        return favoriteClientService.getQuestionFavoriteCount(questionId);
    }
    /**
     * 用户是否已经收藏该问题
     * @param questionId 问题Id
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6030400)
    public FavoritedStatusDTO isQuestionFavorited(Long questionId){
        return favoriteClientService.isQuestionFavorited(questionId);
    }


    /**
     * 收藏夹   添加
     * @param topicId 话题Id
     * @param questionId 问题Id
     * @param request 请求信息
     * @param response 响应信息
     * @return
     */
    @RoleAnnotation(resourceCode= ResourceEnum._3001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1030100)
    @RequestMapping(value="/user/control/favorite/add", method= RequestMethod.POST)
    public Map<String,Object> add(Long topicId, Long questionId,
                                  HttpServletRequest request, HttpServletResponse response){
        return favoriteClientService.addFavorite(topicId,questionId);
    }

    /**
     * 话题收藏列表
     * @param pageForm 页码
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1030200)
    @RequestMapping(value="/user/control/topicFavoriteList",method=RequestMethod.GET)
    public PageView<Favorites> topicFavoriteList(PageForm pageForm, Long topicId, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return favoriteClientService.getTopicFavoriteList(pageForm.getPage(),topicId,fileServerAddress);
    }

    /**
     * 问题收藏列表
     * @param pageForm 页码
     * @param questionId 问题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1030300)
    @RequestMapping(value="/user/control/questionFavoriteList",method=RequestMethod.GET)
    public PageView<Favorites> questionFavoriteList(PageForm pageForm,Long questionId, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return favoriteClientService.getQuestionFavoriteList(pageForm.getPage(),questionId,fileServerAddress);
    }
    /**
     * 收藏夹列表
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1030400)
    @RequestMapping(value="/user/control/favoriteList",method=RequestMethod.GET)
    public PageView<Favorites> favoriteList(PageForm pageForm){
        return favoriteClientService.getFavoriteList(pageForm.getPage());
    }


    /**
     * 删除收藏
     * @param favoriteId 收藏Id  favoriteId、topicId、questionId这3个参数任选一个
     * @param topicId 话题Id  favoriteId、topicId、questionId这3个参数任选一个
     * @param questionId 问题Id  favoriteId、topicId、questionId这3个参数任选一个
     * @return
     */
    @RoleAnnotation(resourceCode=ResourceEnum._3002000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1030500)
    @RequestMapping(value="/user/control/deleteFavorite", method=RequestMethod.POST)
    public Map<String,Object> deleteFavorite(String favoriteId,Long topicId,Long questionId){
        return favoriteClientService.deleteFavorite(favoriteId,topicId,questionId);
    }

}
