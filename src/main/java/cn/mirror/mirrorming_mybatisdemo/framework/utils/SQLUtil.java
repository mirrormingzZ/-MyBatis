package cn.mirror.mirrorming_mybatisdemo.framework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mirror
 */
public class SQLUtil {
	// 测试用
	public static void main(String[] args) {
		String sql = "select * from User where name=#{name} and age=#{age}";
		List<String> sqlParameter = sqlSelectParameter(sql);
		for (String string : sqlParameter) {
			System.out.println(string);
		}

	}

	/**
	 * 获取Insert语句后面values 参数信息<br>
	 * sql:insert into user(name,age) values(#{name},#{age})
	 * 
	 * @param sql
	 * @return
	 */
	public static String[] sqlInsertParameter(String sql) {
		int startIndex = sql.indexOf("values");// 从values开始找
		int endIndex = sql.length();// 到sql末尾
		String sqlSubString = sql.substring(startIndex + 6, endIndex).replace("(", "").replace(")", "")
				.replace("#{", "").replace("}", "");// 从values后面开始找替换掉括号
		System.out.println(sqlSubString);// 变成name,age
		String[] split = sqlSubString.split(",");// 将name,age转为数组{name,age}
		return split;
	}

	/**
	 * 使用正则表达式获取Update语句后面 参数信息<br>
	 * sql:update user set name =#{name}, age =#{age} where id =#{id}
	 * 
	 * @param sql
	 * @return List
	 */
	public static List<String> sqlUpdateParameter(String sql) {

		Pattern pattern = Pattern.compile("\\{+\\w+\\}");
		Matcher matcher = pattern.matcher(sql);
		List<String> list = new ArrayList<>();
		while (matcher.find()) {
			String temp = matcher.group();
			String param = temp.substring(1, temp.length() - 1);
			list.add(param);
		}
		System.out.println(list);
		return list;
	}

	/**
	 * 获取Delete语句后面 参数信息<br>
	 * sql:delete from user where id=#{id}
	 * 
	 * @param sql
	 * @return
	 */
	public static String sqlDeleteParameter(String sql) {
		int startIndex = sql.indexOf("id");// 从values开始找
		int endIndex = sql.length();// 到sql末尾
		String sqlSubString = sql.substring(startIndex + 2, endIndex).replace("=#{", "").replace("}", "");// 从values后面开始找替换掉括号
		System.out.println(sqlSubString);// 变成name,age
		return sqlSubString;
	}

	/**
	 * 使用正则表达式获取Select语句后面 参数信息<br>
	 * sql:select * from User where name=#{name} and age=#{name}
	 * 
	 * @param sql
	 * @return List
	 */
	public static List<String> sqlSelectParameter(String sql) {
		Pattern pattern = Pattern.compile("\\{+\\w+\\}");
		Matcher matcher = pattern.matcher(sql);
		List<String> list = new ArrayList<>();
		while (matcher.find()) {
			String temp = matcher.group();
			String param = temp.substring(1, temp.length() - 1);
			list.add(param);
		}
		System.out.println(list);
		return list;
	}

	/**
	 * 将SQL语句的参数替换变为?
	 * 
	 * @param sql
	 * @param parameterName
	 * @return
	 */
	public static String parameQuestion(String sql, String[] parameterName) {
		for (int i = 0; i < parameterName.length; i++) {
			String str = parameterName[i];
			sql = sql.replace("#{" + str + "}", "?");
		}
		return sql;
	}

	public static String parameQuestion(String sql, String parameterName) {
		String str = parameterName;
		sql = sql.replace("#{" + str + "}", "?");
		return sql;
	}

	public static String parameQuestion(String sql, List<String> parameterName) {
		for (int i = 0; i < parameterName.size(); i++) {
			String str = parameterName.get(i);
			sql = sql.replace("#{" + str + "}", "?");
		}
		return sql;
	}

}
