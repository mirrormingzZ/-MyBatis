package cn.mirror.mirrorming_mybatisdemo.mapper;

import java.util.List;

import cn.mirror.mirrorming_mybatisdemo.entity.User;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorDelete;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorInsert;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorParam;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorSelect;
import cn.mirror.mirrorming_mybatisdemo.framework.annotation.MirrorUpdate;

/**
 * @author mirror
 */
public interface UserMapper {
	@MirrorInsert("insert into user(name,age) values(#{name},#{age})")
	public int insertUser(@MirrorParam("name") String name, @MirrorParam("age") Integer age);

	@MirrorSelect("select * from User where id=#{id}")
	User selectUser(@MirrorParam("id") int id);

	@MirrorDelete("delete from user where id=#{id}")
	public int deleteUser(@MirrorParam("id") int id);

	@MirrorUpdate("update user set name =#{name}, age =#{age} where id =#{id}")
	public int updateUser(@MirrorParam("name") String name, @MirrorParam("age") Integer age, @MirrorParam("id") int id);
}
