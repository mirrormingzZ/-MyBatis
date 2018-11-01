package cn.mirror.mirrorming_mybatisdemo;

import java.util.List;

import cn.mirror.mirrorming_mybatisdemo.entity.User;
import cn.mirror.mirrorming_mybatisdemo.framework.sql.SqlSession;
import cn.mirror.mirrorming_mybatisdemo.mapper.UserMapper;
import junit.framework.TestCase;

/**
 * 测试
 */
public class AppTest extends TestCase {
	public void test() {
		UserMapper userMapper = SqlSession.getMapper(UserMapper.class);
		// int row = userMapper.insertUser("mirror", 18);
		// int row = userMapper.deleteUser(2);
		// int row = userMapper.updateUser("mirr1r", 11, 6);
		User user = (User) userMapper.selectUser(11);
		System.out.println(user.toString());
		System.out.println("1");
	}
}
