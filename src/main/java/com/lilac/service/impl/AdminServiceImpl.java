package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.entity.Admin;
import com.lilac.entity.DTO.AdminPageDTO;
import com.lilac.entity.Result;
import com.lilac.entity.VO.PageVO;
import com.lilac.mapper.AdminMapper;
import com.lilac.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
     * 添加管理员
     * @param admin 管理员信息
     */
    @Override
    public void save(Admin admin) {
        adminMapper.save(admin);
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
