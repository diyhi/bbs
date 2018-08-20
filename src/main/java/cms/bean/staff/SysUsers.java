package cms.bean.staff;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * 用户表
 * @author Administrator
 *
 */
@Entity
@Table(name="sysusers",uniqueConstraints = {
		@UniqueConstraint(columnNames={"userAccount"}
   )}
)
public class SysUsers implements UserDetails ,java.io.Serializable{	
	private static final long serialVersionUID = -7103976496088705053L;

	//用户id
	@Id @Column(length=32)
	private String userId;
	
	//用户账号 ，具有唯一性
	@Column(length=30)
	private String userAccount;
	
	//姓名
	private String fullName;
	
	//密码 BCrypt(sha256(密码原文))
	private String userPassword;
	
	//用户备注
	private String userDesc;
	
	//是否能用。
	private boolean enabled = true;
	//用户的职位：比如主任、经理等。
	private String userDuty;
	
	
	//是否是超级用户。
	private boolean issys = false;
	
	/** 安全摘要 **/
	@Column(length=32)
	private String securityDigest;
	
	/** 当前登录用户权限是否拥有本权限  **/
	@Transient
	private boolean logonUserPermission = false;
	//用户所在的单位。
//	private String userDept;
	
	
	
	//该用户所负责的子系统
//	private String subSystem;
	

	
	//实现了UserDetails之后的相关变量
	@Transient
    private  String password;
    @Transient
    private  String username;
    @Transient
    private  Set<GrantedAuthority> authorities;
    @Transient
    private  boolean accountNonExpired;//账号过期
    @Transient
    private  boolean accountNonLocked;//账号是否锁定
    @Transient
    private  boolean credentialsNonExpired;//凭证是否过期
   
   
    
    public SysUsers(){}
    public SysUsers(String userId, String userAccount, String fullName,
			String userPassword, String userDesc, boolean enabled,boolean issys,String securityDigest,
			 String userDuty,  boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities) {
        if (((userAccount == null) || "".equals(userAccount)) || (userPassword == null)) {
            throw new IllegalArgumentException("不能传递null或空值的构造函数");
        }

        this.userId = userId;
        this.userAccount = userAccount;
        this.fullName = fullName;
        this.userPassword = userPassword;
        this.userDesc = userDesc;
        this.issys = issys;
        this.securityDigest = securityDigest;
        this.userDuty = userDuty;
   //     this.userDept = userDept;
 //       this.subSystem = subSystem;
     //   this.sysUsersRoleses = sysUsersRoleses;
   //     this.usersRolesSet = usersRoles;
        this.username = userAccount;
        this.password = userPassword;//加密所用的字段
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }
    /**
	public SysUsers(String userId, String userAccount, String userName,
			String userPassword, String userDesc, boolean enabled,boolean issys,
			 String userDuty,  Set<SysUsersRoles> usersRoles,boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities) {
        if (((userAccount == null) || "".equals(userAccount)) || (userPassword == null)) {
            throw new IllegalArgumentException("Cannot pass null or empty values to constructor");
        }

        this.userId = userId;
        this.userAccount = userAccount;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userDesc = userDesc;
        this.issys = issys;
        this.userDuty = userDuty;
   //     this.userDept = userDept;
 //       this.subSystem = subSystem;
     //   this.sysUsersRoleses = sysUsersRoleses;
        this.usersRolesSet = usersRoles;
        this.username = userAccount;//加密所用的字段
        this.password = userPassword;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }**/
    


	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserAccount() {
		return this.userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserDesc() {
		return this.userDesc;
	}

	public void setUserDesc(String userDesc) {
		this.userDesc = userDesc;
	}

	
/** 多了这个 **/

    public boolean isEnabled() {
        return enabled;
    }
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	public Boolean getEnabled() {
		return this.enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}**/
	
	public String getUserDuty() {
		return this.userDuty;
	}

	public void setUserDuty(String userDuty) {
		this.userDuty = userDuty;
	}	
	
	public boolean isIssys() {
		return issys;
	}

	public void setIssys(boolean issys) {
		this.issys = issys;
	}

    //~ Methods ========================================================================================================

    
/**
	public Set<SysUsersRoles> getUsersRolesSet() {
		return usersRolesSet;
	}

    public void setUsersRolesSet(Set<SysUsersRoles> usersRolesSet) {
		this.usersRolesSet = usersRolesSet;
	}**/

	

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    public void setAuthorities( Collection<GrantedAuthority> authorities ){
    	this.authorities = (Set<GrantedAuthority>) authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SysUsers other = (SysUsers) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    public boolean isLogonUserPermission() {
		return logonUserPermission;
	}

	public void setLogonUserPermission(boolean logonUserPermission) {
		this.logonUserPermission = logonUserPermission;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAuthorities(Set<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public String getSecurityDigest() {
		return securityDigest;
	}
	public void setSecurityDigest(String securityDigest) {
		this.securityDigest = securityDigest;
	}
	private static SortedSet<GrantedAuthority> sortAuthorities(Collection<GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
        // Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-717)
        SortedSet<GrantedAuthority> sortedAuthorities =
            new TreeSet<GrantedAuthority>(new AuthorityComparator());

        for (GrantedAuthority grantedAuthority : authorities) {
            Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
            sortedAuthorities.add(grantedAuthority);
        }

        return sortedAuthorities;
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            // Neither should ever be null as each entry is checked before adding it to the set.
            // If the authority is null, it is a custom authority and should precede others.
            if (g2.getAuthority() == null) {
                return -1;
            }

            if (g1.getAuthority() == null) {
                return 1;
            }

            return g1.getAuthority().compareTo(g2.getAuthority());
        }
    }
	
	
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("UserAccount: ").append(this.userAccount).append("; ");
   //     sb.append("UserDept: ").append(this.userDept).append("; ");
        sb.append("UserDuty: ").append(this.userDuty).append("; ");
        sb.append("UserDesc: ").append(this.userDesc).append("; ");
   //     sb.append("UserSubSystem: ").append(this.subSystem).append("; ");
    //    sb.append("UserIsSys: ").append(this.issys).append("; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        if ( null !=authorities  && !authorities.isEmpty()) {
            sb.append("Granted Authorities: ");

            boolean first = true;
            for (GrantedAuthority auth : authorities) {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
    
  //  public static void main(String[] args){

    	
  //  }
}
