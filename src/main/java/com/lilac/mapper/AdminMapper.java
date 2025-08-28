package com.lilac.mapper;

import com.lilac.entity.Admin;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
     * 批量保存管理员
     * @param admins 管理员列表
     */
    void saveBatch(@Param("admins") List<Admin> admins);

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
     * 批量删除管理员
     * @param ids ID列表
     */
    void deleteBatch(@Param("ids") List<Integer> ids);

    /**
     * 根据ID列表查询管理员
     * @param ids ID列表
     * @return 管理员列表
     */
    List<Admin> selectByIds(@Param("ids") List<Integer> ids);

    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return Admin
     */
    @Select("select * from admin where username = #{username}")
    Admin findByUsername(String username);

    /**
     * 根据手机号查询管理员
     * @param phone 手机号
     * @return Admin
     */
    @Select("select * from admin where phone = #{phone}")
    Admin findByPhone(String phone);
}
