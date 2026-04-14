package cms.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Jackson Mixin 用于 SysUsers 类，以确保“authorities”字段在反序列化期间得到正确处理，防止将“null”传递给构造函数。
 * @JsonSetter(nulls = Nulls.AS_EMPTY) 确保 null 被视为空列表。
 */
public interface SysUsersMixin {

    // 如果 JSON 中该字段为 null，强制将其作为空列表处理
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    List<GrantedAuthority> getAuthorities();

}
