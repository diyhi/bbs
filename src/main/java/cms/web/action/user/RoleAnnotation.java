package cms.web.action.user;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cms.bean.user.ResourceEnum;

/**
 * 自定义角色注解
 * 注解参考： @RoleAnnotation(resourceCode=ResourceEnum._2001000)
 * 具体由cms.web.filter.TempletesInterceptor的方法处理
 */

@Documented //注解是否将包含在JavaDoc中
@Target(ElementType.METHOD)//注解声明在方法上
@Retention(RetentionPolicy.RUNTIME) //注解的生命周期: 始终不会丢弃，运行期也保留该注解，因此可以使用反射机制读取该注解的信息
public @interface RoleAnnotation {
	//资源代码
	ResourceEnum resourceCode();
	//ResourceEnum code() default ResourceEnum.no;
}
