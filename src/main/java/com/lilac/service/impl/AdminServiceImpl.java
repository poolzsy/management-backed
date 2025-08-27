package com.lilac.service.impl;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
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
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    /**
     * 导出管理员信息
     * @param response HttpServletResponse对象
     */
    @Override
    public void export(HttpServletResponse response) throws IOException {
        // 查询所有管理员
        List<Admin> admins = adminMapper.selectAll();
        // 创建ExcelWriter对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 写入数据
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("phone", "手机号");
        writer.addHeaderAlias("email", "邮箱");
        writer.setOnlyAlias(true);
        writer.write(admins, true);
        // 设置响应头
        String filename = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
        // 设置响应类型
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        // 获取输出流
        ServletOutputStream os = response.getOutputStream();
        writer.flush(os);
        writer.close();
        os.close();
    }
}
