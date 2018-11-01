package cn.mirror.mirrorming_mybatisdemo.framework.aop;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.mirror.mirrorming_mybatisdemo.entity.User;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorDelete;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorInsert;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorParam;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorSelect;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorUpdate;
import cn.mirror.mirrorming_mybatisdemo.framework.utils.JDBCUtil;
import cn.mirror.mirrorming_mybatisdemo.framework.utils.SQLUtil;

/**
 * @author mirror
 */
public class MyBatisInvocationHandler implements InvocationHandler {
	private Object object;

	public MyBatisInvocationHandler(Object object) {
		this.object = object;
	}

	// proxy 代理对象,method拦截方法 ,args方法上的参数值
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("使用动态代理拦截接口开始");
		// 1. 判断方法上是否存在@MirrorInsert
		MirrorInsert mirrorInsert = method.getDeclaredAnnotation(MirrorInsert.class);
		if (mirrorInsert != null) {
			return mirrorInsert(mirrorInsert, proxy, method, args);
		}
		// 2. 判断方法上是否存在@MirrorDelete
		MirrorDelete mirrorDelete = method.getDeclaredAnnotation(MirrorDelete.class);
		if (mirrorDelete != null) {
			return mirrorDelete(mirrorDelete, proxy, method, args);
		}
		// 3. 判断方法上是否存在@MirrorDelete
		MirrorUpdate mirrorUpdate = method.getDeclaredAnnotation(MirrorUpdate.class);
		if (mirrorUpdate != null) {
			return mirrorUpdate(mirrorUpdate, proxy, method, args);
		}

		// 4. 判断方法上是否存在@MirrorSelect
		MirrorSelect mirrorSelect = method.getDeclaredAnnotation(MirrorSelect.class);
		if (mirrorSelect != null) {
			return mirrorSelect(mirrorSelect, proxy, method, args);
		}
		return null;
	}

	// 查
	private Object mirrorSelect(MirrorSelect mirrorSelect, Object proxy, Method method, Object[] args)
			throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, InvocationTargetException {
		// 1.查询的思路
		// 2. 获取注解上查询的SQL语句
		String selectSQL = mirrorSelect.value();
		// 3. 获取方法上的参数,绑定在一起
		ConcurrentHashMap<Object, Object> paramsMap = paramsMap(proxy, method, args);
		// 4. 参数替换？传递方式
		List<String> sqlSelectParameter = SQLUtil.sqlSelectParameter(selectSQL);
		// 5.传递参数
		List<Object> sqlParams = new ArrayList<>();
		for (String parameterName : sqlSelectParameter) {
			Object parameterValue = paramsMap.get(parameterName);
			sqlParams.add(parameterValue);
		}
		// 6.将sql语句替换成?
		String newSql = SQLUtil.parameQuestion(selectSQL, sqlSelectParameter);
		System.out.println("newSQL:" + newSql + ",sqlParams:" + sqlParams.toString());

		// 5.调用jdbc代码底层执行sql语句
		// 6.使用反射机制实例对象### 获取方法返回的类型，进行实例化
		// 思路:
		// 1.使用反射机制获取方法的类型
		// 2.判断是否有结果集,如果有结果集，在进行初始化
		// 3.使用反射机制,给对象赋值

		ResultSet res = JDBCUtil.query(newSql, sqlParams);
		while (res.next()) {
			System.out.println(res.getString("name") + " " + res.getInt("age"));

			// 判断是否存在值
			// if (!res.next()) {
			// return null;
			// }
			// 下标往上移动移位
			// res.previous();
			// 使用反射机制获取方法的类型
			Class<?> returnType = method.getReturnType();
			// getGenericReturnType返回表示由此 Method 对象所表示方法的正式返回类型的 Type 对象。
			// Type genericReturnType = method.getGenericReturnType();
			// if (genericReturnType instanceof ParameterizedType) {
			// ParameterizedType pType = (ParameterizedType) genericReturnType;
			// Type rType = pType.getRawType();
			// Type[] tArgs = pType.getActualTypeArguments();
			// for (int i = 0; i < tArgs.length; i++) {
			// System.out.println("第" + i + "泛型类型是：" + tArgs[i]);
			// }
			// }

			Object object = returnType.newInstance();
			// 获取当前所有的属性
			// 注:Field.getGenericType()：如果当前属性有签名属性类型就返回，否则就返回 Field.getType()
			Field[] declaredFields = returnType.getDeclaredFields();
			for (Field field : declaredFields) {
				String fieldName = field.getName();
				Object fieldValue = res.getObject(fieldName);
				field.setAccessible(true);
				field.set(object, fieldValue);
			}

			// for (String parameteName : sqlSelectParameter) {
			// // 获取参数值
			// Object resultValue = res.getObject(parameteName);
			// // 使用java的反射值赋值
			// Field field = returnType.getDeclaredField(parameteName);
			// // 私有方法允许访问
			// field.setAccessible(true);
			// field.set(object, resultValue);
			// }
			return object;
		}
		return null;
	}

	// 改
	private Object mirrorUpdate(MirrorUpdate mirrorUpdate, Object proxy, Method method, Object[] args) {
		// 方法上存在@mirrorUpdate,获取他的SQL语句
		// 2. 获取SQL语句,获取注解mirrorUpdate语句
		String updateSql = mirrorUpdate.value();
		System.out.println("updateSql:" + updateSql);
		// 3. 获取方法的参数和SQL参数进行匹配
		// 定一个一个Map集合 KEY为@MirrorParamValue,Value 结果为参数值
		ConcurrentHashMap<Object, Object> paramsMap = paramsMap(proxy, method, args);
		// 存放sql执行的参数---参数绑定过程
		List sqlInsertParameter = SQLUtil.sqlUpdateParameter(updateSql);
		List<Object> sqlParams = sqlParams(sqlInsertParameter, paramsMap);
		// 4. 根据参数替换参数变为?
		String newSQL = SQLUtil.parameQuestion(updateSql, sqlInsertParameter);
		System.out.println("newSQL:" + newSQL + ",sqlParams:" + sqlParams.toString());
		// 5. 调用jdbc底层代码执行语句
		return JDBCUtil.update(newSQL, false, sqlParams);
	}

	// 增
	private Object mirrorInsert(MirrorInsert mirrorInsert, Object proxy, Method method, Object[] args) {
		// 方法上存在@MirrorInsert,获取他的SQL语句
		// 2. 获取SQL语句,获取注解Insert语句
		String insertSql = mirrorInsert.value();
		// System.out.println("insertSql:" + insertSql);
		// 3. 获取方法的参数和SQL参数进行匹配
		// 定一个一个Map集合 KEY为@MirrorParamValue,Value 结果为参数值
		ConcurrentHashMap<Object, Object> paramsMap = paramsMap(proxy, method, args);
		// 存放sql执行的参数---参数绑定过程
		String[] sqlInsertParameter = SQLUtil.sqlInsertParameter(insertSql);
		List<Object> sqlParams = sqlParams(sqlInsertParameter, paramsMap);
		// 4. 根据参数替换参数变为?
		String newSQL = SQLUtil.parameQuestion(insertSql, sqlInsertParameter);
		System.out.println("newSQL:" + newSQL + ",sqlParams:" + sqlParams.toString());
		// 5. 调用jdbc底层代码执行语句
		return JDBCUtil.insert(newSQL, false, sqlParams);
	}

	// 删
	private Object mirrorDelete(MirrorDelete mirrorDelete, Object proxy, Method method, Object[] args) {
		// 方法上存在@MirrorDelete,获取他的SQL语句
		// 2. 获取SQL语句,获取注解Delete语句
		String deleteSql = mirrorDelete.value();
		System.out.println("f(mirrorDelete)------deleteSql:" + deleteSql);
		// 3. 获取方法的参数和SQL参数进行匹配
		// 定一个一个Map集合 KEY为@MirrorParamValue,Value 结果为参数值
		ConcurrentHashMap<Object, Object> paramsMap = paramsMap(proxy, method, args);
		// 存放sql执行的参数---参数绑定过程
		String sqlDeleteParameter = SQLUtil.sqlDeleteParameter(deleteSql);
		String sqlParam = singleSqlParams(sqlDeleteParameter, paramsMap);
		// 4. 根据参数替换参数变为?
		String newSQL = SQLUtil.parameQuestion(deleteSql, sqlDeleteParameter);
		System.out.println("newSQL:" + newSQL + ",sqlParams:" + sqlParam.toString());
		// 5. 调用jdbc底层代码执行语句
		return JDBCUtil.delete(newSQL, false, sqlParam);
	}

	private List<Object> sqlParams(String[] sqlInsertParameter, ConcurrentHashMap<Object, Object> paramsMap) {
		List<Object> sqlParams = new ArrayList<>();
		for (String paramName : sqlInsertParameter) {
			Object paramValue = paramsMap.get(paramName);
			sqlParams.add(paramValue);
		}
		return sqlParams;
	}

	private List<Object> sqlParams(List<String> sqlInsertParameter, ConcurrentHashMap<Object, Object> paramsMap) {
		List<Object> sqlParams = new ArrayList<>();
		for (String paramName : sqlInsertParameter) {
			Object paramValue = paramsMap.get(paramName);
			sqlParams.add(paramValue);
		}
		return sqlParams;
	}

	private String singleSqlParams(String sqlDeleteParameter, ConcurrentHashMap<Object, Object> paramsMap) {
		return paramsMap.get(sqlDeleteParameter).toString();
	}

	// 将方法注解上的参数和定义的局部变量绑定并且放置到map中
	private ConcurrentHashMap<Object, Object> paramsMap(Object proxy, Method method, Object[] args) {
		ConcurrentHashMap<Object, Object> paramsMap = new ConcurrentHashMap<>();
		// 获取方法上的参数
		Parameter[] parameters = method.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			MirrorParam mirrorParam = parameter.getDeclaredAnnotation(MirrorParam.class);
			if (mirrorParam != null) {
				// 参数名称
				String paramName = mirrorParam.value();
				Object paramValue = args[i];
				// System.out.println(paramName + "," + paramValue);
				paramsMap.put(paramName, paramValue);
			}
		}
		return paramsMap;
	}

	// public Object mirrorInsertSQL() {
	// return object;
	// }
}
