package cms.service.membershipCard;

import cms.dto.PageView;
import cms.dto.membershipCard.MembershipCardRequest;
import cms.model.membershipCard.MembershipCard;
import cms.model.membershipCard.MembershipCardOrder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 会员卡服务
 */
public interface MembershipCardService {

    /**
     * 获取会员卡列表
     * @param page 页码
     */
    public PageView<MembershipCard> getMembershipCardList(int page);
    /**
     * 获取会员卡订单列表
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     */
    public PageView<MembershipCardOrder> getMembershipCardOrderList(int page, String fileServerAddress);
    /**
     * 获取用户会员卡订单列表
     * @param page 页码
     * @param userName 用户名称
     */
    public Map<String,Object> getUserMembershipCardOrderList(int page, String userName);
    /**
     * 获取添加会员卡界面信息
     * @return
     */
    public Map<String,Object> getAddMembershipCardViewModel();
    /**
     * 添加会员卡
     * @param membershipCardRequest 会员卡表单
     * @param request 请求信息
     */
    public void addMembershipCard(MembershipCardRequest membershipCardRequest, HttpServletRequest request);
    /**
     * 获取修改会员卡界面信息
     * @param membershipCardId 会员卡Id
     * @return
     */
    public Map<String,Object> getEditMembershipCardViewModel(Long membershipCardId);
    /**
     * 修改会员卡
     * @param membershipCardRequest 会员卡表单
     * @param request 请求信息
     */
    public void editMembershipCard(MembershipCardRequest membershipCardRequest, HttpServletRequest request);
    /**
     * 上传文件
     * @param dir 上传类型，分别为image、media、file
     * @param fileName 文件名称 预签名时有值
     * @param fileServerAddress 文件服务器地址
     * @param file 文件
     * @return
     */
    public Map<String, Object> uploadFile(String dir, String fileName, String fileServerAddress, MultipartFile file);
    /**
     * 删除会员卡
     * @param membershipCardId 会员卡Id
     */
    public void deleteMembershipCard(Long membershipCardId);
}
