package com.lilac.mapper;

import com.lilac.entity.Admin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper {

    /**
     * 查询所有管理员
     * @return 管理员列表
     */
    @Select("select * from admin")
    List<Admin> selectAll();

    /**
     * 分页查询管理员
     * @param name 管理员名称
     * @return 管理员列表
     */
    List<Admin> selectPage(String name);

    /**
     * 新增或更新管理员
     * @param admin 管理员
     */
    void save(Admin admin);

    /**
     * 更新管理员
     * @param admin 管理员
     */
    void update(Admin admin);

    /**
     * 删除管理员
     * @param id 管理员ID
     */
    @Delete("delete from admin where id = #{id}")
    void delete(Integer id);

    /**
     * 新增：根据用户名查询管理员
     * @param username 用户名
     * @return Admin
     */
    @Select("select * from admin where username = #{username}")
    Admin findByUsername(String username);

    /**
     * 新增：根据手机号查询管理员
     * @param phone 手机号
     * @return Admin
     */
    @Select("select * from admin where phone = #{phone}")
    Admin findByPhone(String phone);
}
