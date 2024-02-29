package cms.web.action.staff;


import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;

import cms.bean.staff.SysUsers;
import cms.service.staff.StaffService;

import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



/**
 * 用户验证信息获取
 * 该类的主要作用是为Spring Security提供一个经过用户认证后的UserDetails。
 * 该UserDetails包括用户名、密码、是否可用、是否过期等信息。
 * 
 * 获取员工信息方法
 * Object obj  =  SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
 * if(obj instanceof SysUsers){
 *		SysUsers sysUsers = (SysUsers)obj;			
 * }
 * 
 *
 */
@Component("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Resource StaffService staffService;//通过接口引用代理返回的对象
	@Resource StaffManage staffManage;
	
	@Resource @Lazy
	private PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		
		Collection<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();

		//根据用户名取得一个SysUsers对象，以获取该用户的其他信息。
		SysUsers user = staffService.findByUserAccount(username);
		if(user == null){
			throw new UsernameNotFoundException("用户不存在"); 
		}
		if(!user.isEnabled()){
			throw new UsernameNotFoundException("用户已被禁用"); 
		}
		/**
		//如果是管理员则有所有权限
		if(user.isIssys()){
			auths = staffService.loadAllAuthorities();
		}else{
			//得到用户的权限
			auths = staffService.loadUserAuthoritiesByName(username);
		}**/
		
		return new SysUsers(user.getUserId(), user.getUserAccount(), user.getFullName(),user.getNickname(),user.getAvatarName(),
			//	 user.getUserPassword(),user.getUserDesc(), user.getEnabled(),user.isIssys(), 
				user.getUserPassword(),user.getUserDesc(), user.isEnabled(),user.isIssys(), user.getSecurityDigest(),
				 user.getUserDuty(), true, true, true, auths);
	}
		
	
}
