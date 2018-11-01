package cn.mirror.mirrorming_mybatisdemo.framework.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义删除注解
 * 
 * @author mirror
 */

@Documented // 注解包含在Javadoc位置 没什么作用
@Retention(RetentionPolicy.RUNTIME) // 什么时候生效
@Target(ElementType.METHOD) // 注解使用的位置method->方法上 ;type->Java类上面;
							// @Target({ElementType.METHOD,ElementType.METHOD})可以在方法和类上面
public @interface MirrorDelete {
	String value();
	// String value() default "";代表我们的注解可以跟值 @MirrorDelete("")
}