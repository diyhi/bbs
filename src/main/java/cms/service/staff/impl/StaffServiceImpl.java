package cms.service.staff.impl;

import cms.component.fileSystem.FileComponent;
import cms.component.staff.StaffCacheManager;
import cms.config.BusinessException;
import cms.dto.PageView;
import cms.dto.QueryResult;
import cms.model.staff.SysRoles;
import cms.model.staff.SysUsers;
import cms.model.staff.SysUsersRoles;
import cms.repository.setting.SettingRepository;
import cms.repository.staff.ACLRepository;
import cms.repository.staff.StaffRepository;
import cms.service.staff.StaffService;
import cms.service.user.impl.UserServiceImpl;
import cms.utils.FileUtil;
import cms.utils.SHA;
import cms.utils.UUIDUtil;
import jakarta.annotation.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 员工服务
 */
@Service
public class StaffServiceImpl implements StaffService {
    private static final Logger logger = LogManager.getLogger(StaffServiceImpl.class);


    @Resource
    StaffRepository staffRepository;
    @Resource
    FileComponent fileComponent;
    @Resource
    StaffCacheManager staffCacheManager;
    @Resource
    SettingRepository settingRepository;
    @Resource ACLRepository aclRepository;
    @Resource PasswordEncoder passwordEncoder;

    /**
     * 获取员工列表
     *
     * @param page 页码
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String, Object> getStaffList(int page, String fileServerAddress) {
        Map<String, Object> returnValue = new HashMap<String, Object>();

        //调用分页算法代码
        PageView<SysUsers> pageView = new PageView<SysUsers>(settingRepository.findSystemSetting_cache().getBackstagePageNumber(), page, 10);
        //当前页
        int firstIndex = (page - 1) * pageView.getMaxresult();

        QueryResult<SysUsers> qr = staffRepository.getScrollData(SysUsers.class, firstIndex, pageView.getMaxresult());
        //仅显示指定字段
        if (qr != null && qr.getResultlist() != null && qr.getResultlist().size() > 0) {
            List<SysUsers> sysUserViewList = new ArrayList<SysUsers>();
            for (SysUsers sysUsers : qr.getResultlist()) {
                SysUsers sysUserView = new SysUsers();
                sysUserView.setUserId(sysUsers.getUserId());//用户id
                sysUserView.setUserAccount(sysUsers.getUserAccount());//用户账号
                sysUserView.setFullName(sysUsers.getFullName());//姓名
                sysUserView.setUserDuty(sysUsers.getUserDuty());//用户的职位
                sysUserView.setIssys(sysUsers.isIssys());//是否是超级用户
                sysUserView.setNickname(sysUsers.getNickname());
                sysUserView.setAvatarPath(fileServerAddress + sysUsers.getAvatarPath());
                sysUserView.setAvatarName(sysUsers.getAvatarName());
                sysUserViewList.add(sysUserView);
            }
            qr.setResultlist(sysUserViewList);
        }
        //将查询结果集传给分页List
        pageView.setQueryResult(qr);

        List<SysUsers> sysUsersList = qr.getResultlist();
        if (sysUsersList != null && sysUsersList.size() > 0) {
            List<String> userAccountList = new ArrayList<String>();
            for (SysUsers sysUsers : sysUsersList) {
                userAccountList.add(sysUsers.getUserAccount());
            }

            Map<String, List<String>> rolesNameMap = aclRepository.findRolesByUserAccount(userAccountList);
            returnValue.put("rolesNameMap", rolesNameMap);
        }
        returnValue.put("pageView", pageView);


        return returnValue;
    }


    /**
     * 获取添加员工界面信息
     *
     * @param username   员工账号
     * @param isSysAdmin 是否为超级用户
     * @return
     */
    public Map<String, Object> getAddStaffViewModel(String username, boolean isSysAdmin) {
        Map<String, Object> returnValue = new LinkedHashMap<String, Object>();


        List<String> roleIdList = staffRepository.findRoleIdByUserAccount(username);

        List<SysRoles> sysRolesList = aclRepository.findRolesList();
        if (sysRolesList != null && !sysRolesList.isEmpty()) {
            for (SysRoles sr : sysRolesList) {
                if (roleIdList != null && roleIdList.contains(sr.getId())) {
                    sr.setLogonUserPermission(true);
                }
                if (isSysAdmin) {//如果登录用户是超级管理员
                    sr.setLogonUserPermission(true);
                }
            }
        }


        returnValue.put("sysRolesList", sysRolesList);
        returnValue.put("isSysAdmin", isSysAdmin);

        return returnValue;
    }

    /**
     * 添加员工
     * @param sysUsersForm   员工表单
     * @param repeatPassword 重复密码
     * @param sysRolesId     角色Id集合
     * @param width          图片宽
     * @param height         图片高
     * @param x              X轴
     * @param y              Y轴
     * @param file           头像文件
     */
    public void addStaff(SysUsers sysUsersForm, String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y, MultipartFile file) {
        Map<String, String> errors = new HashMap<>();

        //校验用户账号和昵称
        validateUserAccountAndNickname(sysUsersForm, errors);

        //验证密码
        validatePassword(sysUsersForm, repeatPassword, errors);

        //处理头像剪裁
        String newFileName = handleFile(file, width, height, x, y, errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        String username = "";//用户名称
        boolean issys = false;//是否是超级用户
        Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(obj instanceof UserDetails){
            username =((UserDetails)obj).getUsername();
        }
        if(obj instanceof SysUsers){
            issys = ((SysUsers)obj).isIssys();
        }


        // 获取有效的角色
        Set<SysUsersRoles> userRoles = getValidRoles(sysRolesId,sysUsersForm,username,issys);

        SysUsers sysUsers = new SysUsers();
        sysUsers.setUserId(UUIDUtil.getUUID32());//Id
        if(issys){//如果登录用户是超级管理员才接收设置超级管理员值
            sysUsers.setIssys(sysUsersForm.isIssys());//是否是超级用户
        }else{
            sysUsers.setIssys(false);
        }
        sysUsers.setUserAccount(sysUsersForm.getUserAccount() != null ? sysUsersForm.getUserAccount().trim() : "");//用户账号
        sysUsers.setFullName(sysUsersForm.getFullName());//姓名

        sysUsers.setNickname(sysUsersForm.getNickname() != null ? sysUsersForm.getNickname().trim() : sysUsersForm.getNickname());
        sysUsers.setUserPassword(passwordEncoder.encode(SHA.sha256Hex(sysUsersForm.getUserPassword().trim())));
        sysUsers.setUserDesc(sysUsersForm.getUserDesc());//用户备注
        sysUsers.setUserDuty(sysUsersForm.getUserDuty());//职位
        sysUsers.setEnabled(sysUsersForm.isEnabled());//是否使用
        sysUsers.setUsername(sysUsers.getUserAccount());//账号
        sysUsers.setSecurityDigest(UUIDUtil.getUUID32());

        sysUsers.setAvatarName(newFileName);

        staffRepository.saveUser(sysUsers, userRoles);
        if(sysUsers.getNickname() != null && !sysUsers.getNickname().trim().isEmpty()){
            staffCacheManager.delete_cache_findByNickname(sysUsers.getNickname());
        }

    }

    /**
     * 获取修改员工界面信息
     * @param userId 员工Id
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditStaffViewModel(String userId,String fileServerAddress){
        Map<String, Object> returnValue = new LinkedHashMap<String, Object>();
        Map<String, String> errors = new HashMap<>();


        if (userId == null || userId.trim().isEmpty()) {
            errors.put("userId", "员工Id不能为空");
            throw new BusinessException(errors);
        }

        // 查询员工信息
        SysUsers targetUser = (SysUsers)staffRepository.find(SysUsers.class, userId);
        if (targetUser == null) {
            errors.put("userId", "员工不存在");
            throw new BusinessException(errors);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUsers logonUser = (SysUsers) auth.getPrincipal();
        boolean issys = logonUser.isIssys();
        String logonUserAccount = logonUser.getUserAccount();

        // 权限判断
        targetUser.setLogonUserPermission(validateEditPermissions(logonUser, targetUser));

        // 获取所有角色
        List<SysRoles> sysRolesList = aclRepository.findRolesList();
        //当前用户角色Id
        List<String> roleIdList = staffRepository.findRoleIdByUserAccount(logonUser.getUserAccount());
        //选中用户角色Id
        List<String> selectedRolesIds = staffRepository.findRoleIdByUserAccount(targetUser.getUserAccount());


        if(sysRolesList != null && sysRolesList.size() >0){
            for(SysRoles sr : sysRolesList){
                if(roleIdList != null && roleIdList.size() >0 && roleIdList.contains(sr.getId())){
                    sr.setLogonUserPermission(true);//当前登录用户权限是否拥有本权限
                }
                if(selectedRolesIds != null && selectedRolesIds.size() >0 && selectedRolesIds.contains(sr.getId())){
                    sr.setSelected(true);//是否选中
                }

                if(issys){//如果是超级用户,则有修改权限
                    sr.setLogonUserPermission(true);

                }else{
                    //不能修改自已的权限
                    if(targetUser.getUserAccount().equals(logonUser.getUserAccount())){
                        sr.setLogonUserPermission(false);
                    }
                    //用户如果没有修改权限
                    if(!targetUser.isLogonUserPermission()){
                        sr.setLogonUserPermission(false);
                    }
                }

                //如果要修改的用户是超级用户则显示全部选中
                if(targetUser.isIssys()){//非超级用户不能修改超级用户的资料
                    sr.setSelected(true);//是否选中
                }

            }
        }

        targetUser.setPassword("");//密码不显示
        targetUser.setUserPassword("");
        targetUser.setAvatarPath(fileServerAddress+targetUser.getAvatarPath());
        returnValue.put("sysRolesList",sysRolesList);
        returnValue.put("sysUsers",targetUser);
        returnValue.put("isSysAdmin",(issys && !logonUserAccount.equals(targetUser.getUserAccount())));


        return returnValue;
    }

    /**
     * 修改员工
     * @param sysUsersForm   员工表单
     * @param userId   员工Id
     * @param repeatPassword 重复密码
     * @param sysRolesId     角色Id集合
     * @param width          图片宽
     * @param height         图片高
     * @param x              X轴
     * @param y              Y轴
     * @param file           头像文件
     */
    public void editStaff(SysUsers sysUsersForm,String userId, String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y, MultipartFile file) {
        Map<String, String> errors = new HashMap<>();


        if (userId == null || userId.trim().isEmpty()) {
            errors.put("userId", "员工Id不能为空");
            throw new BusinessException(errors);
        }

        // 查询员工信息
        SysUsers targetUser = (SysUsers)staffRepository.find(SysUsers.class, userId);
        if(targetUser == null){
            errors.put("userId", "员工不存在");
            throw new BusinessException(errors);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers logonUser = (SysUsers) authentication.getPrincipal();
        String old_nickname = targetUser.getNickname();
        String old_avatarName = targetUser.getAvatarName();

        // 判断当前登录员工是否可以编辑目标员工
        if (!validateEditPermissions(logonUser, targetUser)) {
            errors.put("permission", "账号权限不足或无法修改此用户");
            throw new BusinessException(errors);
        }
        //登录用户不能修改自己的超级管理员权限
        if(logonUser.getUserId().equals(userId) && logonUser.isIssys() != sysUsersForm.isIssys()){
            errors.put("permission", "登录用户不能修改自己的超级管理员权限");
        }
        //登录用户不能停用自己账号
        if(logonUser.getUserId().equals(userId) && logonUser.isEnabled() != sysUsersForm.isEnabled()){
            errors.put("permission", "登录用户不能停用自己账号");
        }

        //呢称处理
        if(sysUsersForm.getNickname() != null && !sysUsersForm.getNickname().trim().isEmpty()){

            if(targetUser.getNickname() == null || !targetUser.getNickname().equals(sysUsersForm.getNickname().trim())){
                SysUsers s = staffCacheManager.query_cache_findByNickname(sysUsersForm.getNickname().trim());
                if(s != null){
                    errors.put("nickname", "呢称不能和员工呢称相同");
                }
            }
            if(!targetUser.getUserAccount().equals(sysUsersForm.getNickname().trim())){
                SysUsers s = staffCacheManager.query_cache_findByUserAccount(sysUsersForm.getNickname().trim());
                if(s != null){
                    errors.put("nickname", "呢称不能和员工账号相同");
                }

            }
        }


        //密码处理
        if (sysUsersForm.getUserPassword() != null && !sysUsersForm.getUserPassword().trim().isEmpty()) {
            validatePassword(sysUsersForm, repeatPassword, errors);
        }


        //处理头像剪裁
        String newFileName = handleFile(file, width, height, x, y, errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }


        //可用的角色ID
        List<String> select_rolesIdList  = assignRolesToUser(logonUser, targetUser, sysRolesId);

        Set<SysUsersRoles> userRoles = new HashSet<SysUsersRoles>();
        for (String rolesId : select_rolesIdList) {
            userRoles.add(new SysUsersRoles(targetUser.getUserAccount(),rolesId));
        }

        if(sysUsersForm.getUserPassword() != null && !sysUsersForm.getUserPassword().trim().isEmpty()){
            // 密码通过盐值加密以备存储入数据库
            String newPassword = passwordEncoder.encode(SHA.sha256Hex(sysUsersForm.getUserPassword().trim()));
            targetUser.setUserPassword(newPassword);
            targetUser.setSecurityDigest(UUIDUtil.getUUID32());
        }

        if(targetUser.isEnabled() != sysUsersForm.isEnabled()){//如果修改是否允许使用
            targetUser.setSecurityDigest(UUIDUtil.getUUID32());
        }


        targetUser.setFullName(sysUsersForm.getFullName());
        targetUser.setNickname(sysUsersForm.getNickname() != null ? sysUsersForm.getNickname().trim() : sysUsersForm.getNickname());
        targetUser.setUserDesc(sysUsersForm.getUserDesc());
        targetUser.setUserDuty(sysUsersForm.getUserDuty());
        targetUser.setEnabled(sysUsersForm.isEnabled());

        if (logonUser.isIssys()) {
            targetUser.setIssys(sysUsersForm.isIssys());
        }
        if(newFileName != null && !newFileName.trim().isEmpty()){
            targetUser.setAvatarName(newFileName);
        }

        staffRepository.updateUser(targetUser, userRoles);
        staffCacheManager.delete_staffSecurityDigest(targetUser.getUserAccount());
        staffCacheManager.delete_staffPermissionMenu(targetUser.getUserAccount());
        staffCacheManager.delete_userAuthoritiesByName(targetUser.getUserAccount());
        staffCacheManager.delete_cache_findByUserAccount(targetUser.getUserAccount());
        if(old_nickname != null && !old_nickname.trim().isEmpty()){
            staffCacheManager.delete_cache_findByNickname(old_nickname);
        }

        if(old_avatarName != null && !old_avatarName.trim().isEmpty() && newFileName != null && !newFileName.trim().isEmpty()){
            String pathFile = "file"+File.separator+"staffAvatar"+File.separator +old_avatarName;
            //删除头像
            fileComponent.deleteFile(pathFile);

            String pathFile_100 = "file"+File.separator+"staffAvatar"+File.separator +"100x100" +File.separator+old_avatarName;
            //删除头像100*100
            fileComponent.deleteFile(pathFile_100);
        }

    }


    /**
     * 获取员工修改自身界面信息
     * @param fileServerAddress 文件服务器随机地址
     * @return
     */
    public Map<String,Object> getEditStaffSelfViewModel(String fileServerAddress) {
        Map<String, Object> returnValue = new LinkedHashMap<String, Object>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers logonUser = (SysUsers) authentication.getPrincipal();

        SysUsers targetUser = (SysUsers)staffRepository.find(SysUsers.class, logonUser.getUserId());
        if(targetUser == null){
            throw new BusinessException(Map.of("userId", "员工不存在"));
        }
        List<SysRoles> sysRolesList = aclRepository.findRolesList();
        //选中用户角色Id
        List<String> select_roleIdList = staffRepository.findRoleIdByUserAccount(targetUser.getUserAccount());
        if(sysRolesList != null && sysRolesList.size() >0){
            for(SysRoles sr : sysRolesList){

                if(select_roleIdList != null && select_roleIdList.size() >0 && select_roleIdList.contains(sr.getId())){
                    sr.setSelected(true);//是否选中
                }

                if(targetUser.isIssys()){
                    sr.setSelected(true);//是否选中
                }

            }
        }
        targetUser.setPassword("");//密码不显示
        targetUser.setUserPassword("");
        targetUser.setAvatarPath(fileServerAddress+targetUser.getAvatarPath());
        returnValue.put("sysRolesList",sysRolesList);
        returnValue.put("sysUsers",targetUser);
        return returnValue;
    }

    /**
     * 修改员工自身信息
     * @param sysUsersForm   员工表单
     * @param repeatPassword 重复密码
     * @param sysRolesId     角色Id集合
     * @param width          图片宽
     * @param height         图片高
     * @param x              X轴
     * @param y              Y轴
     * @param file           头像文件
     */
    public void editSelfInfo(SysUsers sysUsersForm,String repeatPassword, String[] sysRolesId, Integer width, Integer height, Integer x, Integer y, MultipartFile file) {
        Map<String, String> errors = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers logonUser = (SysUsers) authentication.getPrincipal();


        // 查询员工信息
        SysUsers targetUser = (SysUsers)staffRepository.find(SysUsers.class, logonUser.getUserId());
        if(targetUser == null){
            errors.put("userId", "员工不存在");
            throw new BusinessException(errors);
        }
        String old_nickname = targetUser.getNickname();
        String old_avatarName = targetUser.getAvatarName();


        //呢称处理
        if(sysUsersForm.getNickname() != null && !sysUsersForm.getNickname().trim().isEmpty()){

            if(targetUser.getNickname() == null || !targetUser.getNickname().equals(sysUsersForm.getNickname().trim())){
                SysUsers s = staffCacheManager.query_cache_findByNickname(sysUsersForm.getNickname().trim());
                if(s != null){
                    errors.put("nickname", "呢称不能和员工呢称相同");
                }
            }
            if(!targetUser.getUserAccount().equals(sysUsersForm.getNickname().trim())){
                SysUsers s = staffCacheManager.query_cache_findByUserAccount(sysUsersForm.getNickname().trim());
                if(s != null){
                    errors.put("nickname", "呢称不能和员工账号相同");
                }

            }
        }


        //密码处理
        if (sysUsersForm.getUserPassword() != null && !sysUsersForm.getUserPassword().trim().isEmpty()) {
            validatePassword(sysUsersForm, repeatPassword, errors);
        }


        //处理头像剪裁
        String newFileName = handleFile(file, width, height, x, y, errors);

        if (!errors.isEmpty()) {
            throw new BusinessException(errors);
        }

        if(sysUsersForm.getUserPassword() != null && !sysUsersForm.getUserPassword().trim().isEmpty()){
            // 密码通过盐值加密以备存储入数据库
            String newPassword = passwordEncoder.encode(SHA.sha256Hex(sysUsersForm.getUserPassword().trim()));
            targetUser.setUserPassword(newPassword);
            targetUser.setSecurityDigest(UUIDUtil.getUUID32());
        }

        targetUser.setFullName(sysUsersForm.getFullName());
        targetUser.setNickname(sysUsersForm.getNickname() != null ? sysUsersForm.getNickname().trim() : sysUsersForm.getNickname());
        targetUser.setUserDuty(sysUsersForm.getUserDuty());

        if(newFileName != null && !newFileName.trim().isEmpty()){
            targetUser.setAvatarName(newFileName);
        }

        staffRepository.updateUser(targetUser);
        staffCacheManager.delete_staffSecurityDigest(targetUser.getUserAccount());
        staffCacheManager.delete_staffPermissionMenu(targetUser.getUserAccount());
        staffCacheManager.delete_userAuthoritiesByName(targetUser.getUserAccount());
        staffCacheManager.delete_cache_findByUserAccount(targetUser.getUserAccount());
        if(old_nickname != null && !old_nickname.trim().isEmpty()){
            staffCacheManager.delete_cache_findByNickname(old_nickname);
        }

        if(old_avatarName != null && !old_avatarName.trim().isEmpty() && newFileName != null && !newFileName.trim().isEmpty()){
            String pathFile = "file"+File.separator+"staffAvatar"+File.separator +old_avatarName;
            //删除头像
            fileComponent.deleteFile(pathFile);

            String pathFile_100 = "file"+File.separator+"staffAvatar"+File.separator +"100x100" +File.separator+old_avatarName;
            //删除头像100*100
            fileComponent.deleteFile(pathFile_100);
        }


    }


    /**
     * 删除员工
     * @param userId   员工Id
     */
    public void deleteStaff(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException(Map.of("userId", "员工Id不能为空"));
        }

        SysUsers targetUser = (SysUsers)staffRepository.find(SysUsers.class, userId);//要删除的用户

        //权限验证
        validateDeletePermissions(targetUser);


        staffRepository.deleteUser(targetUser.getUserId(),targetUser.getUserAccount());
        staffCacheManager.delete_staffSecurityDigest(targetUser.getUserAccount());
        staffCacheManager.delete_staffPermissionMenu(targetUser.getUserAccount());
        staffCacheManager.delete_userAuthoritiesByName(targetUser.getUserAccount());
        staffCacheManager.delete_cache_findByUserAccount(targetUser.getUserAccount());
        if(targetUser.getNickname() != null && !targetUser.getNickname().trim().isEmpty()){
            staffCacheManager.delete_cache_findByNickname(targetUser.getNickname());
        }

        if(targetUser.getAvatarName() != null && !targetUser.getAvatarName().trim().isEmpty()){


            String pathFile = "file"+File.separator+"staffAvatar"+File.separator +targetUser.getAvatarName();
            //删除头像
            fileComponent.deleteFile(pathFile);

            String pathFile_100 = "file"+File.separator+"staffAvatar"+File.separator +"100x100" +File.separator+targetUser.getAvatarName();
            //删除头像100*100
            fileComponent.deleteFile(pathFile_100);
        }

    }



    /**
     * 验证用户账号和昵称
     *
     * @param sysUsersForm 员工表单
     * @param errors       错误信息
     */
    private void validateUserAccountAndNickname(SysUsers sysUsersForm, Map<String, String> errors) {
        //账号
        if (sysUsersForm.getUserAccount() == null || "".equals(sysUsersForm.getUserAccount())) {
            errors.put("userAccount", "账号不能为空");
        } else {
            if (sysUsersForm.getUserAccount().trim().length() > 30) {
                errors.put("userAccount", "不能超过30个字符");
            }
            SysUsers s1 = (SysUsers) staffRepository.findByUserAccount(sysUsersForm.getUserAccount().trim());
            if (s1 != null) {
                errors.put("userAccount", "该账号已存在");
            }

            SysUsers s2 = staffCacheManager.query_cache_findByNickname(sysUsersForm.getUserAccount().trim());
            if (s2 != null) {
                errors.put("nickname", "账号不能和员工呢称相同");
            }
        }

        if (sysUsersForm.getNickname() != null && !sysUsersForm.getNickname().trim().isEmpty()) {
            SysUsers s1 = staffCacheManager.query_cache_findByUserAccount(sysUsersForm.getNickname().trim());
            if (s1 != null) {
                errors.put("nickname", "呢称不能和员工账号相同");
            }
            SysUsers s2 = staffCacheManager.query_cache_findByNickname(sysUsersForm.getNickname().trim());
            if (s2 != null) {
                errors.put("nickname", "呢称不能和员工呢称相同");
            }
        }
    }

    /**
     * 验证密码
     *
     * @param sysUsersForm   员工表单
     * @param repeatPassword 重复密码
     * @param errors         错误信息
     */
    private void validatePassword(SysUsers sysUsersForm, String repeatPassword, Map<String, String> errors) {
        if (repeatPassword == null || repeatPassword.trim().isEmpty()) {
            errors.put("repeatPassword", "重复密码不能为空");
        }
        if (sysUsersForm.getUserPassword() == null || sysUsersForm.getUserPassword().trim().isEmpty()) {
            errors.put("userPassword", "密码不能为空");
        }
        if (sysUsersForm.getUserPassword() != null && repeatPassword != null && !sysUsersForm.getUserPassword().equals(repeatPassword.trim())) {
            errors.put("userPassword", "两次输入密码不相同");
        }
    }

    /**
     * 处理头像文件
     *
     * @param file   图片文件
     * @param width  图片宽
     * @param height 图片高
     * @param x      X轴
     * @param y      Y轴
     * @param errors 错误信息
     * @return 新的文件名
     */
    private String handleFile(MultipartFile file, Integer width, Integer height, Integer x, Integer y, Map<String, String> errors) {
        String newFileName = "";

        if (file == null || file.isEmpty()) {
            return newFileName;
        }


        // 文件大小和类型验证
        long imageSize = 5 * 1024L; // 5MB
        if (file.getSize() / 1024 > imageSize) {
            errors.put("file", "文件超出允许上传大小");
            return newFileName;
        }

        List<String> allowedFormats = Arrays.asList("gif", "jpg", "jpeg", "bmp", "png","webp");
        String originalFileName = file.getOriginalFilename();
        if (!FileUtil.validateFileSuffix(originalFileName, allowedFormats)) {
            errors.put("file", "当前文件类型不允许上传");
            return newFileName;
        }


        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("读取上传文件字节失败", e);
            }
            errors.put("file", "文件读取失败");
            return newFileName;
        }

        int srcWidth;
        int srcHeight;
        int maxWidth = 200;
        int maxHeight = 200;
        try (InputStream is = new ByteArrayInputStream(fileBytes)) {
            BufferedImage bufferImage = ImageIO.read(is);
            if (bufferImage == null) {
                errors.put("file", "无法读取图片文件");
                return newFileName;
            }

            srcWidth = bufferImage.getWidth();
            srcHeight = bufferImage.getHeight();


            // 图像尺寸验证
            if ("blob".equalsIgnoreCase(originalFileName)) {
                if (srcWidth > maxWidth || srcHeight > maxHeight) {
                    errors.put("file", "超出最大尺寸 (" + maxWidth + "x" + maxHeight + ")");
                    return newFileName;
                }
            }
        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("解析图片尺寸失败", e);
            }
            errors.put("file", "图片处理失败");
            return newFileName;
        }

        // 裁剪坐标验证
        if (width != null && (width <= 0 || width.toString().length() >= 8))
            errors.put("width", "宽度必须大于0且少于8位");
        if (height != null && (height <= 0 || height.toString().length() >= 8))
            errors.put("height", "高度必须大于0且少于8位");
        if (x != null && (x < 0 || x.toString().length() >= 8)) errors.put("x", "X轴必须大于或等于0且少于8位");
        if (y != null && (y < 0 || y.toString().length() >= 8)) errors.put("y", "Y轴必须大于或等于0且少于8位");

        if (!errors.isEmpty()) {
            return newFileName;
        }

        //保存文件并生成缩略图
        String suffix = FileUtil.getExtension(originalFileName).toLowerCase();
        // 如果是 blob，默认使用 png
        if (suffix.isEmpty() || "blob".equalsIgnoreCase(suffix)) {
            suffix = "png";
        }
        newFileName = UUIDUtil.getUUID32() + "." + suffix;
        String pathDir = "file" + File.separator + "staffAvatar" + File.separator;
        String pathDir_100 = pathDir + "100x100" + File.separator;

        fileComponent.createFolder(pathDir);
        fileComponent.createFolder(pathDir_100);

        // 原图在 200x200 范围内
        if (srcWidth <= 200 && srcHeight <= 200) {
            // 保存原图到 pathDir (作为 200x200 级别的备份)
            fileComponent.writeFile(pathDir, newFileName, fileBytes);

            //判断是否需要缩小到 100x100
            if (srcWidth <= 100 && srcHeight <= 100) {
                // 图片很小，直接拷贝字节
                fileComponent.writeFile(pathDir_100, newFileName, fileBytes);
            } else {
                // 图片在 100-200 之间，等比例缩放生成 100x100 缩略图
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, 100, 100);
            }
        }else {//原图大于 200x200，需要裁剪并生成两套缩略图

            if (x != null && y != null && width != null && height != null) {
                // 根据裁剪坐标生成 200x200 缩略图
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir + newFileName, suffix, x, y, width, height, 200, 200);
                // 根据裁剪坐标生成 100x100 缩略图
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, x, y, width, height, 100, 100);
            } else {
                // 如果没有裁剪参数
                fileComponent.writeFile(pathDir, newFileName, fileBytes);// 保存原始文件
                fileComponent.createImage(new ByteArrayInputStream(fileBytes), pathDir_100 + newFileName, suffix, 100, 100);
            }
        }

        return newFileName;
    }

    /**
     * 获取有效角色
     * @param sysRolesId 角色Id集合
     * @param sysUsersForm 员工表单
     * @param username 员工名称
     * @param issys 是否为超级管理员
     * @return
     */
    private Set<SysUsersRoles> getValidRoles(String[] sysRolesId,SysUsers sysUsersForm,String username,boolean issys) {
        List<SysRoles> sysRolesList = aclRepository.findRolesList();

        //当前用户角色Id
        List<String> roleIdList = staffRepository.findRoleIdByUserAccount(username);

        List<String> select_rolesIdList = new ArrayList<String>();//选中可用的角色ID

        if(issys){//如果是超级用户,则可添加系统所有角色
            //如果输入用户不是超级管理员
            if(!sysUsersForm.isIssys()){
                List<String> allSysRolesId  = new ArrayList<String>();//系统所有的角色ID
                if(sysRolesList != null && sysRolesList.size() >0){
                    for(SysRoles sr : sysRolesList){
                        allSysRolesId.add(sr.getId());
                    }
                }
                if(sysRolesId != null && sysRolesId.length >0){
                    for(String s : sysRolesId){
                        if(allSysRolesId.contains(s)){//如果输入的角色ID属于系统的角色ID
                            select_rolesIdList.add(s);
                        }
                    }
                }
            }

        }else{
            if(roleIdList != null && sysRolesId != null && sysRolesId.length >0){
                for(String s : sysRolesId){
                    if(roleIdList.contains(s)){
                        select_rolesIdList.add(s);
                    }
                }
            }
        }

        Set<SysUsersRoles> userRoles = new HashSet<SysUsersRoles>();
        for (String rolesId : select_rolesIdList) {
            userRoles.add(new SysUsersRoles(sysUsersForm.getUserAccount(),rolesId));
        }
        return userRoles;
    }

    /**
     * 判断当前登录员工是否可以编辑目标员工
     * @param logonUser 当前登录员工
     * @param targetUser 目标员工
     * @return
     */
    private boolean validateEditPermissions(SysUsers logonUser, SysUsers targetUser) {
        // 如果登录用户是超级管理员，可以直接编辑
        if (logonUser.isIssys()) {
            return true;
        }

        // 非超级用户不能编辑超级用户
        if (targetUser.isIssys()) {
            return false;
        }


        // 非超级用户只能编辑自己的或权限小于自己的用户
        List<String> logonUserPermissionIds = staffRepository.findPermissionIdByUserAccount(logonUser.getUserAccount());
        List<String> targetUserPermissionIds = staffRepository.findPermissionIdByUserAccount(targetUser.getUserAccount());

        if(logonUserPermissionIds != null && targetUserPermissionIds != null){
            // 判断登录用户权限是否包含要修改用户的权限
            boolean canEdit = logonUserPermissionIds.containsAll(targetUserPermissionIds);

            // 如果权限相同且不是编辑自己，则不能修改
            if (canEdit && logonUserPermissionIds.size() == targetUserPermissionIds.size() && !logonUser.getUserAccount().equals(targetUser.getUserAccount())) {
                return false;
            }
            return canEdit;
        }


        return false;
    }

    /**
     * 根据权限为目标用户分配角色。
     * @param logonUser 当前登录用户
     * @param targetUser 待修改的用户
     * @param sysRolesId 表单提交的角色ID数组
     * @return 最终分配的角色ID列表
     */
    private List<String> assignRolesToUser(SysUsers logonUser, SysUsers targetUser, String[] sysRolesId) {
        List<String> selectedRoles = new ArrayList<>();

        // 如果是超级用户，可以分配系统内的所有角色
        if (logonUser.isIssys()) {
            List<SysRoles> rolesList = aclRepository.findRolesList();

            //系统所有的角色ID
            List<String> allSysRolesId = Optional.ofNullable(rolesList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(SysRoles::getId)
                    .toList();
            if (sysRolesId != null) {
                for (String s : sysRolesId) {
                    if (allSysRolesId.contains(s)) {//如果输入的角色ID属于系统的角色ID
                        selectedRoles.add(s);
                    }
                }
            }
        } else { // 如果不是超级用户
            List<String> currentUserRoleIds = staffRepository.findRoleIdByUserAccount(logonUser.getUserAccount());

            // 不能修改自己的权限，直接使用自己的角色
            if (targetUser.getUserAccount().equals(logonUser.getUserAccount())) {
                if(currentUserRoleIds != null){
                    selectedRoles.addAll(currentUserRoleIds);
                }

            } else { // 修改其他用户
                if (sysRolesId != null && currentUserRoleIds != null) {
                    for (String s : sysRolesId) {
                        if (currentUserRoleIds.contains(s)) { // 只能分配自己拥有的角色
                            selectedRoles.add(s);
                        }
                    }
                }
            }
        }

        return selectedRoles;
    }

    /**
     * 验证删除权限
     * @param targetUser 待删除的用户
     */
    private void validateDeletePermissions(SysUsers targetUser) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SysUsers logonUser = (SysUsers) authentication.getPrincipal();

        //登录用户不能删除自己
        if (targetUser.getUserAccount().equals(logonUser.getUserAccount())) {
            throw new BusinessException(Map.of("staff", "不能删除自己"));
        }

        // 2. 非超级用户不能删除超级用户
        if (targetUser.isIssys() && !logonUser.isIssys()) {
            throw new BusinessException(Map.of("staff", "账号权限不足，不能删除超级管理员"));
        }

        // 非超级用户不能删除同级或权限高于自己的用户
        if (!logonUser.isIssys()) {
            //要修改的用户的权限ID
            List<String> userPermission = staffRepository.findPermissionIdByUserAccount(targetUser.getUserAccount());
            //登录用户权限ID
            List<String> logonUserPermissionId = staffRepository.findPermissionIdByUserAccount(logonUser.getUserAccount());
            if (logonUserPermissionId != null && userPermission != null) {
                // 判断登录用户权限是否包含要删除用户的权限
                if (!logonUserPermissionId.containsAll(userPermission)) {
                    throw new BusinessException(Map.of("staff", "账号权限不足"));
                }

                // 如果权限相同，不能删除
                if (logonUserPermissionId.size() == userPermission.size()) {
                    throw new BusinessException(Map.of("staff", "不能删除相同权限的用户"));
                }
            }
        }

    }
}