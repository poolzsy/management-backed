package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.entity.Admin;
import com.lilac.entity.DTO.AdminPageDTO;
import com.lilac.entity.Result;
import com.lilac.entity.VO.PageVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.SystemException;
import com.lilac.mapper.AdminMapper;
import com.lilac.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    /**
     * 查询所有管理员
     * @return 管理员列表
     */
    @Override
    public List<Admin> selectAll() {
        return adminMapper.selectAll();
    }

    /**
     * 分页查询管理员
     * @param adminPageDTO 分页参数
     * @return 分页结果
     */
    @Override
    public Result selectPage(AdminPageDTO adminPageDTO) {
        PageHelper.startPage(adminPageDTO.getPageNum(), adminPageDTO.getPageSize());
        List<Admin> admins = adminMapper.selectPage(adminPageDTO.getName());
        PageInfo<Admin> pageInfo = new PageInfo<>(admins);
        PageVO pageVO = new PageVO(pageInfo.getList(), pageInfo.getTotal());
        return Result.success(pageVO);
    }

    /**
     * 添加或更新管理员，并进行唯一性校验
     * @param admin 管理员信息
     */
    @Override
    public void save(Admin admin) {
        checkUniqueness(admin);
        if (admin.getId() == null) {
            adminMapper.save(admin);
        } else {
            adminMapper.update(admin);
        }
    }

    /**
     * 抽取出的私有方法，用于校验用户名和手机号的唯一性
     * @param admin 待校验的管理员对象
     */
    private void checkUniqueness(Admin admin) {
        // 校验用户名
        Admin dbAdminByUsername = adminMapper.findByUsername(admin.getUsername());
        // 如果查到了记录，并且(这是新增操作 || 查到的记录ID和当前操作的记录ID不一致)，则说明冲突
        if (dbAdminByUsername != null && (admin.getId() == null || !dbAdminByUsername.getId().equals(admin.getId()))) {
            throw new SystemException(HttpsCodeEnum.USER_NAME_EXIST);
        }
        // 校验手机号
        Admin dbAdminByPhone = adminMapper.findByPhone(admin.getPhone());
        if (dbAdminByPhone != null && (admin.getId() == null || !dbAdminByPhone.getId().equals(admin.getId()))) {
            throw new SystemException(HttpsCodeEnum.USER_PHONE_EXIST);
        }
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     */
    @Override
    public void delete(Integer id) {
        adminMapper.delete(id);
    }

    /**
     * 批量删除管理员
     * @param ids 管理员ID列表
     */
    @Override
    public void deleteBatch(List<Integer> ids) {
        for(Integer id : ids){
            adminMapper.delete(id);
        }
    }
}
