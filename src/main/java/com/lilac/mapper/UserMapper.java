package com.lilac.mapper;

import com.lilac.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 查询所有用户
     */
    @Select("select * from user")
    List<User> selectAll();

    /**
     * 分页查询用户
     */
    List<User> selectPage(String name);

    /**
     * 保存用户
     */
    void save(User user);

    /**
     * 批量保存用户
     */
    void saveBatch(@Param("users") List<User> users);

    /**
     * 更新用户
     */
    void update(User user);

    /**
     * 删除用户
     */
    @Delete("delete from user where id = #{id}")
    void delete(Integer id);

    /**
     * 批量删除用户
     */
    void deleteBatch(@Param("ids") List<Integer> ids);

    /**
     * 根据id批量查询用户
     */
    List<User> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据用户名查询用户
     */
    @Select("select * from User where username = #{username}")
    User findByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    @Select("select * from user where phone = #{phone}")
    User findByPhone(String phone);
}
