package com.keung.spring4hibernate3.common.persistence.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryCacheKeysDefine
{
	/**
	 * 缓存关键字定义
	 * 例如：{"id", "birthday,lastUpdateTime"}
	 * @return
	 */
	String[] def() default { "id" };

	/**
	 * 缓存超时时间（毫秒）
	 * @return
	 */
	long timeout() default 0;
}
