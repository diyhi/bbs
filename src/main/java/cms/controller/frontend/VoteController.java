package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.annotation.RoleAnnotation;
import cms.dto.user.ResourceEnum;
import cms.model.vote.VoteTheme;
import cms.utils.IpAddress;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 前台投票控制器(未开源)
 */
@RestController("frontendVoteController")
public class VoteController {

    /**
     * 投票主题明细
     * @param voteThemeId 投票主题Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6140100)
    public VoteTheme voteThemeDetail(String voteThemeId){
        return null;
    }

    /**
     * 参与投票
     * @param voteOptionId 投票选项Id
     * @param request 请求信息
     * @return
     */
    @RoleAnnotation(resourceCode= ResourceEnum._8001000)
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1140100)
    @RequestMapping(value="/user/control/vote/addVoteRecord", method= RequestMethod.POST)
    public Map<String,Object> add(String voteOptionId,
                                  HttpServletRequest request) {
        return null;
    }


}
