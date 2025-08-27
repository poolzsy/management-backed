package com.lilac.service;

import com.lilac.entity.Admin;
import com.lilac.entity.DTO.AdminPageDTO;
import com.lilac.entity.Result;

import java.util.List;

public interface AdminService {
    /**
     * 查询所有管理员
     * @return 管理员列表
     */
    List<Admin> selectAll();

    /**
     * 分页查询管理员
     * @param adminPageDTO 分页参数
     * @return 分页结果
     */
    Result selectPage(AdminPageDTO adminPageDTO);

    /**
     * 保存管理员
     * @param admin 管理员信息
     * @return 保存结果
     */
    void save(Admin admin);

    /**
     * 删除管理员
     * @param id 管理员ID
     * @return 删除结果
     */
    void delete(Integer id);

    /**
     * 批量删除管理员
     * @param ids 管理员ID列表
     * @return 删除结果
     */
    void deleteBatch(List<Integer> ids);
}
