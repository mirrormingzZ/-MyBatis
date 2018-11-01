package cn.mirror.mirrorming_mybatisdemo.framework.sql;

import java.lang.reflect.Proxy;

import cn.mirror.mirrorming_mybatisdemo.framework.aop.MyBatisInvocationHandler;

/**
 * @author mirror
 */
public class SqlSession {
	// 加载Mapper接口
	@SuppressWarnings("unchecked")
	public static <T> T getMapper(Class clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz },
				new MyBatisInvocationHandler(clazz));

	}
}
