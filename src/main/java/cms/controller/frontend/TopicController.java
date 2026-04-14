package cms.controller.frontend;

import cms.annotation.DynamicRouteEnum;
import cms.annotation.DynamicRouteTarget;
import cms.component.fileSystem.FileComponent;
import cms.dto.PageForm;
import cms.dto.PageView;
import cms.dto.frontendModule.TopicDTO;
import cms.model.topic.Topic;
import cms.model.topic.TopicUnhide;
import cms.service.frontend.TopicClientService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 前台话题控制器
 */
@RestController("frontendTopicController")
public class TopicController {
    @Resource
    TopicClientService topicClientService;
    @Resource
    FileComponent fileComponent;
    /**
     * 话题分页
     * @param pageForm 页码
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010100)
    public PageView<Topic> topicPage(PageForm pageForm,Long tagId,HttpServletRequest request){
        return topicClientService.getTopicPage(pageForm.getPage(),tagId,request);
    }

    /**
     * 相似话题
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6011300)
    public List<Topic> similarTopic(Long topicId,HttpServletRequest request){
        return topicClientService.getSimilarTopic(topicId,request);
    }

    /**
     * 热门话题
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6011400)
    public List<Topic> hotTopic(HttpServletRequest request){
        return topicClientService.getHotTopic(request);
    }

    /**
     * 话题明细
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010200)
    public Topic topicDetail(Long topicId,HttpServletRequest request){
         return topicClientService.getTopicDetail(topicId,request);
    }


    /**
     * 精华话题分页
     * @param pageForm 页码
     * @param tagId 标签Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6011500)
    public PageView<Topic> featuredTopicPage(PageForm pageForm,Long tagId,HttpServletRequest request){
        return topicClientService.getFeaturedTopicPage(pageForm.getPage(),tagId,request);
    }

    /**
     * 添加话题表单
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010500)
    public Map<String,Object> addTopicUI(){
        return topicClientService.getAddTopicViewModel();
    }


    /**
     * 话题  添加
     * @param topicDTO    用户表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010100)
    @RequestMapping(value="/user/control/topic/add", method= RequestMethod.POST)
    public Map<String,Object> add(@ModelAttribute TopicDTO topicDTO, BindingResult result,
                      HttpServletRequest request) {
        return topicClientService.addTopic(topicDTO,request);
    }

    /**
     * 修改话题表单
     * @param topicId 话题Id
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.CUSTOM_6010900)
    public Map<String,Object> editTopicUI(Long topicId){
        return topicClientService.getEditTopicViewModel(topicId);
    }
    /**
     * 话题  修改
     * @param topicDTO    用户表单
     * @param result      存储校验信息
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010200)
    @RequestMapping(value="/user/control/topic/edit", method=RequestMethod.POST)
    public Map<String,Object> edit(@ModelAttribute TopicDTO topicDTO, BindingResult result,
                       HttpServletRequest request){
        return topicClientService.editTopic(topicDTO,request);
    }

    /**
     * 文件上传
     * 员工发话题 上传文件名为UUID + a + 员工Id
     * 用户发话题 上传文件名为UUID + b + 用户Id
     * @param dir: 上传类型，分别为image、file、media
     * @param fileName 文件名称 预签名时有值
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010300)
    @RequestMapping(value="/user/control/topic/upload", method=RequestMethod.POST)
    public Map<String,Object> upload(String dir, String fileName,
                         MultipartFile file, HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return topicClientService.uploadFile(dir, fileName,file, fileServerAddress);
    }

    /**
     * 话题  解锁
     * @param topicId 话题Id
     * @param hideType 隐藏类型
     * @param password 密码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1010400)
    @RequestMapping(value="/user/control/topic/unhide", method=RequestMethod.POST)
    public Map<String,Object> topicUnhide(Long topicId,Integer hideType, String password) {
        return topicClientService.topicUnhide(topicId, hideType,password);
    }

    /**
     * 我的话题
     * @param pageForm 页码
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011300)
    @RequestMapping(value="/user/control/topicList",method=RequestMethod.GET)
    public PageView<Topic> topicListUI(PageForm pageForm) {
        return topicClientService.getTopicList(pageForm.getPage());
    }

    /**
     * 话题解锁用户列表(只记录'输入密码可见','积分购买可见','余额购买可见'的用户)
     * @param pageForm 页码
     * @param topicId 话题Id
     * @param request 请求信息
     * @return
     */
    @DynamicRouteTarget(route = DynamicRouteEnum.DEFAULT_1011600)
    @RequestMapping(value="/user/control/topicUnhideList",method=RequestMethod.GET)
    public PageView<TopicUnhide> topicUnhideList(PageForm pageForm, Long topicId,
                                                 HttpServletRequest request){
        String fileServerAddress = fileComponent.fileServerAddress(request);
        return topicClientService.getTopicUnhideList(pageForm.getPage(),topicId,fileServerAddress);
    }
}
