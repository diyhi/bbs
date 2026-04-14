package cms.service.frontend.impl;

import cms.component.TextFilterComponent;
import cms.component.fileSystem.FileComponent;
import cms.component.frontendModule.FrontendApiComponent;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.frontendModule.ConfigHelpPage;
import cms.model.frontendModule.ConfigRecommendedHelp;
import cms.model.frontendModule.FrontendApi;
import cms.model.help.Help;
import cms.model.help.HelpType;
import cms.model.setting.SystemSetting;
import cms.repository.help.HelpRepository;
import cms.repository.help.HelpTypeRepository;
import cms.repository.setting.SettingRepository;
import cms.service.frontend.HelpClientService;
import cms.utils.SecureLink;
import cms.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 前台帮助服务
 */
@Service
public class HelpClientServiceImpl implements HelpClientService {

    @Resource
    SettingRepository settingRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    HelpTypeRepository helpTypeRepository;
    @Resource
    HelpRepository helpRepository;
    @Resource
    TextFilterComponent textFilterComponent;
    @Resource
    FrontendApiComponent frontendApiComponent;




    /**
     * 获取在线帮助分页
     *
     * @param page 页码
     * @param helpTypeId 分类Id
     * @param request 请求信息
     * @return
     */
    public PageView<Help> getHelpPage(int page, Long helpTypeId, HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigHelpPage configHelpPage) {
            int maxResult = settingRepository.findSystemSetting_cache().getForestagePageNumber();
            //每页显示记录数
            if (configHelpPage.getMaxResult() != null && configHelpPage.getMaxResult() > 0) {
                maxResult = configHelpPage.getMaxResult();
            }
            int pageCount = 10;// 页码显示总数
            int sort = 1;//排序


            //分类Id
            if (!configHelpPage.isHelpType_transferPrameter()) {
                helpTypeId = configHelpPage.getHelpTypeId();
            }

            //排序
            if (configHelpPage.getSort() != null && configHelpPage.getSort() > 0) {
                sort = configHelpPage.getSort();
            }

            //页码显示总数
            if (configHelpPage.getPageCount() != null && configHelpPage.getPageCount() > 0) {
                pageCount = configHelpPage.getPageCount();
            }

            //调用分页算法代码
            PageView<Help> pageView = new PageView<Help>(maxResult,page,pageCount);
            //当前页
            int firstIndex = (page-1)*pageView.getMaxresult();

            //执行查询
            StringBuffer jpql = new StringBuffer("");
            //存放参数值
            List<Object> params = new ArrayList<Object>();

            //帮助分类Id
            if(helpTypeId != null){
                jpql.append(" and o.helpTypeId=?"+ (params.size()+1));
                params.add(helpTypeId);//加上查询参数

            }
            jpql.append(" and o.visible=?"+ (params.size()+1));
            params.add(true);//设置o.visible=?1是否可见


            //排序
            LinkedHashMap<String,String> orderby = new LinkedHashMap<String,String>();
            //排行依据
            if(sort == 1){
                orderby.put("id", "desc");//发布时间排序   新-->旧
            }else if(sort == 2){
                orderby.put("id", "asc");//发布时间排序  旧-->新
            }
            //删除第一个and
            String jpql_str = StringUtils.difference(" and", jpql.toString());
            QueryResult<Help> qr = helpRepository.getScrollData(Help.class,firstIndex, maxResult, jpql_str, params.toArray(),orderby);

            if(qr.getResultlist() != null && qr.getResultlist().size() >0){
                for(Help help: qr.getResultlist()){
                    HelpType helpType = helpTypeRepository.findById(help.getHelpTypeId());
                    if(helpType != null){
                        help.setHelpTypeName(helpType.getName());
                    }
                    //处理富文本路径
                    help.setContent(fileComponent.processRichTextFilePath(help.getContent(),"help"));
                }
            }


            //将查询结果集传给分页List
            pageView.setQueryResult(qr);
            return pageView;
        }
        return null;
    }
    /**
     * 获取在线帮助列表
     * @param helpTypeId 分类Id
     * @return
     */
    public List<Help> getHelpList(Long helpTypeId){
        List<Help> helpList = new ArrayList<Help>();

        if(helpTypeId != null && helpTypeId >0L){
            List<Help> _helpList = helpRepository.findByTypeId(helpTypeId);
            if(_helpList != null && _helpList.size() >0){
                helpList = _helpList;
            }
        }
        return helpList;
    }
    /**
     * 获取推荐在线帮助
     * @param request 请求信息
     * @return
     */
    public List<Help> getRecommendHelp(HttpServletRequest request){
        //获取当前请求的前台API
        FrontendApi frontendApi = frontendApiComponent.getRequestFrontendApi(request);
        if(frontendApi == null){
            return null;
        }
        if (frontendApi.getConfigObject() instanceof ConfigRecommendedHelp configRecommendedHelp) {
            List<Help> helpList = new ArrayList<Help>();
            List<Help> recommendHelpList = configRecommendedHelp.getRecommendHelpList();
            if(recommendHelpList != null && recommendHelpList.size() >0){
                for(int i = 0; i< recommendHelpList.size(); i++){
                    Help help = helpRepository.findById(recommendHelpList.get(i).getId());
                    if(help != null){
                        helpList.add(help);
                    }
                }
                return helpList;
            }

        }
        return null;
    }
    /**
     * 获取在线帮助分类
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public List<HelpType> getType(String fileServerAddress){
        List<HelpType> helpTypeList = new ArrayList<HelpType>();

        List<HelpType> allHelpType = helpTypeRepository.findAllHelpType();
        for(HelpType helpType : allHelpType){
            if(helpType.getImage() != null && !helpType.getImage().trim().isEmpty()){
                helpType.setImage(fileServerAddress+helpType.getImage());
            }
            if(helpType.getChildNodeNumber() >0){//有子节点
                List<HelpType> childHelpType = this.queryType(allHelpType,helpType.getId());
                if(childHelpType.size() >0){
                    helpType.setChildHelpType(childHelpType);
                }
            }
        }
        for(HelpType helpType : allHelpType){
            if(helpType.getParentId().equals(0L)){//加入所有父节点
                helpTypeList.add(helpType);
            }
            //排序
            this.helpTypeSort(helpTypeList);
        }
        return helpTypeList;
    }

    /**
     * 获取在线帮助导航
     * @param helpTypeId 分类Id
     * @return
     */
    public Map<Long,String> getNavigation(Long helpTypeId){
        Map<Long,String> navigation = new LinkedHashMap<Long,String>();
        if(helpTypeId != null && helpTypeId > 0){
            HelpType helpType = helpTypeRepository.findById(helpTypeId);
            if(helpType != null){
                List<HelpType> parentHelpTypeList = helpTypeRepository.findAllParentById(helpType);
                for(HelpType p : parentHelpTypeList){
                    navigation.put(p.getId(), p.getName());
                }
                navigation.put(helpType.getId(), helpType.getName());
            }

        }

        return navigation;
    }

    /**
     * 获取在线帮助明细
     * @param helpId 在线帮助Id
     * @param request 请求信息
     * @return
     */
    public Help getHelpDetail(Long helpId,HttpServletRequest request){
        if(helpId != null && helpId > 0){
            Help help = helpRepository.findById(helpId);
            if(help != null && help.isVisible()){
                HelpType helpType = helpTypeRepository.findById(help.getHelpTypeId());
                if(helpType != null){
                    help.setHelpTypeName(helpType.getName());
                }
                //处理富文本路径
                help.setContent(fileComponent.processRichTextFilePath(help.getContent(),"help"));

                SystemSetting systemSetting = settingRepository.findSystemSetting_cache();
                if(help.getContent() != null && !help.getContent().trim().isEmpty() && systemSetting.getFileSecureLinkSecret() != null && !systemSetting.getFileSecureLinkSecret().trim().isEmpty()){
                    List<String> serverAddressList = fileComponent.fileServerAllAddress(request);
                    //解析上传的文件完整路径名称
                    Map<String,String> analysisFullFileNameMap = textFilterComponent.analysisFullFileName(help.getContent(),"help",serverAddressList);
                    if(analysisFullFileNameMap != null && analysisFullFileNameMap.size() >0){


                        Map<String,String> newFullFileNameMap = new HashMap<String,String>();//新的完整路径名称 key: 完整路径名称 value: 重定向接口
                        for (Map.Entry<String,String> entry : analysisFullFileNameMap.entrySet()) {

                            newFullFileNameMap.put(entry.getKey(), WebUtil.getUrl(request)+ SecureLink.createDownloadRedirectLink(entry.getKey(),entry.getValue(),-1L,systemSetting.getFileSecureLinkSecret()));
                        }

                        help.setContent(textFilterComponent.processFullFileName(help.getContent(),"help",newFullFileNameMap,serverAddressList));

                    }
                }
                if(help.getContent() != null && !help.getContent().trim().isEmpty()){

                    //处理视频播放器标签
                    String content = textFilterComponent.processVideoPlayer(WebUtil.getUrl(request),help.getContent(),-1L,systemSetting.getFileSecureLinkSecret());
                    help.setContent(content);
                }
                return help;
            }

        }
        return null;
    }

    /**
     * 根据父Id查询分类
     * @param allHelpType 所有在线帮助分类
     * @param parentId 父Id
     * @return
     */
    private List<HelpType> queryType(List<HelpType> allHelpType,Long parentId){
        List<HelpType> helpTypeList = new ArrayList<HelpType>();
        for(HelpType helpType : allHelpType){
            if(helpType.getParentId().equals(parentId)){//加入所有父节点
                helpTypeList.add(helpType);
            }
            //排序
            this.helpTypeSort(helpTypeList);
        }
        return helpTypeList;
    }
    /**
     * 在线帮助分类排序
     * @param helpTypeList 在线帮助分类集合
     */
    private void helpTypeSort(List<HelpType> helpTypeList){
        //排序，防止更新时数据死锁，从小到大排序
        Collections.sort(helpTypeList, new Comparator<HelpType>(){
            @Override
            public int compare(HelpType o1,
                               HelpType o2) {
                return o2.getSort().compareTo(o1.getSort());
            }
        });
    }
}
