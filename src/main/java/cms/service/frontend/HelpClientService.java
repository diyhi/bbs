package cms.service.frontend;


import cms.dto.PageView;
import cms.model.help.Help;
import cms.model.help.HelpType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * 前台帮助服务接口
 */
public interface HelpClientService {
    /**
     * 获取在线帮助分页
     *
     * @param page 页码
     * @param helpTypeId 分类Id
     * @param request 请求信息
     * @return
     */
    public PageView<Help> getHelpPage(int page, Long helpTypeId, HttpServletRequest request);
    /**
     * 获取在线帮助列表
     * @param helpTypeId 分类Id
     * @return
     */
    public List<Help> getHelpList(Long helpTypeId);
    /**
     * 获取推荐在线帮助
     * @param request 请求信息
     * @return
     */
    public List<Help> getRecommendHelp( HttpServletRequest request);
    /**
     * 获取在线帮助分类
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public List<HelpType> getType(String fileServerAddress);
    /**
     * 获取在线帮助导航
     * @param helpTypeId 分类Id
     * @return
     */
    public Map<Long,String> getNavigation(Long helpTypeId);
    /**
     * 获取在线帮助明细
     * @param helpId 在线帮助Id
     * @param request 请求信息
     * @return
     */
    public Help getHelpDetail(Long helpId,HttpServletRequest request);
}
