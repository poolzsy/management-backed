package com.lilac.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.entity.Admin;
import com.lilac.entity.DTO.AdminPageDTO;
import com.lilac.entity.DTO.LoginDTO;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    @Transactional
    public void deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return; // 避免空的IN查询导致SQL错误
        }
        adminMapper.deleteBatch(ids);
    }

    /**
     * 导出所有管理员信息
     * @param response HttpServletResponse对象
     */
    @Override
    public void export(HttpServletResponse response) throws IOException {
        // 查询所有管理员
        List<Admin> admins = adminMapper.selectAll();
        exportAdminsToExcel(admins, response);
    }

    /**
     * 导出选中管理员信息
     * @param ids ID列表
     * @param response HttpServletResponse对象
     */
    @Override
    public void exportSelected(List<Integer> ids, HttpServletResponse response) throws IOException {
        if (ids == null || ids.isEmpty()) {
            throw new SystemException(HttpsCodeEnum.BAD_REQUEST, "请选择要导出的记录");
        }
        List<Admin> admins = adminMapper.selectByIds(ids);
        exportAdminsToExcel(admins, response);
    }

    /**
     * 抽取出的通用方法，用于将Admin列表写入Excel并响应
     * @param admins 管理员列表
     * @param response HttpServletResponse对象
     */
    private void exportAdminsToExcel(List<Admin> admins, HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 定义表头别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("phone", "手机号");
        writer.addHeaderAlias("email", "邮箱");
        writer.setOnlyAlias(true);
        // 写入数据
        writer.write(admins, true);
        // 设置响应头
        String filename = URLEncoder.encode("管理员信息", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        // 将Excel内容写入输出流
        try (ServletOutputStream os = response.getOutputStream()) {
            writer.flush(os, true);
        } finally {
            writer.close();
        }
    }

    /**
     * 批量导入管理员数据
     * @param file 上传的Excel文件
     * @return 导入结果
     */
    @Override
    @Transactional
    public Result importData(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 定义Excel表头与Java实体类的字段映射
        reader.addHeaderAlias("用户名", "username");
        reader.addHeaderAlias("姓名", "name");
        reader.addHeaderAlias("手机号", "phone");
        reader.addHeaderAlias("邮箱", "email");
        // 读取Excel数据到List
        List<Admin> adminsToImport = reader.readAll(Admin.class);
        if (adminsToImport.isEmpty()) {
            return Result.error(HttpsCodeEnum.BAD_REQUEST, "请上传Excel文件");
        }
        // 数据校验
        List<String> errorMessages = new ArrayList<>();
        for (int i = 0; i < adminsToImport.size(); i++) {
            Admin admin = adminsToImport.get(i);
            // 校验逻辑
            Admin dbAdminByUsername = adminMapper.findByUsername(admin.getUsername());
            if (dbAdminByUsername != null) {
                errorMessages.add("第 " + (i + 1) + " 行，用户名 '" + admin.getUsername() + "' 已存在");
            }
            Admin dbAdminByPhone = adminMapper.findByPhone(admin.getPhone());
            if (dbAdminByPhone != null) {
                errorMessages.add("第 " + (i + 1) + " 行，手机号 '" + admin.getPhone() + "' 已存在");
            }
        }
        // 如果有校验失败的记录，则终止导入，并返回所有错误信息
        if (!errorMessages.isEmpty()) {
            throw new SystemException(HttpsCodeEnum.BAD_REQUEST, String.join("<br>", errorMessages));
        }
        // 批量插入数据库
        try {
            adminMapper.saveBatch(adminsToImport);
        } catch (Exception e) {
            // 捕获数据库层面的异常，例如唯一索引冲突等
            throw new SystemException(HttpsCodeEnum.SYSTEM_ERROR,"批量插入失败，请检查数据");
        }

        return Result.success("成功导入 " + adminsToImport.size() + " 条数据");
    }

    /**
     * 管理员登录
     * @param loginDTO 登录信息
     */
    @Override
    public Result login(LoginDTO loginDTO) {
        Admin admin = adminMapper.findByUsername(loginDTO.getUsername());
        if (admin == null) {
            throw new SystemException(HttpsCodeEnum.USER_NOT_EXIST);
        }
        if (!admin.getPassword().equals(loginDTO.getPassword())) {
            throw new SystemException(HttpsCodeEnum.USER_PASSWORD_ERROR);
        }
        return Result.success(admin);
    }
}
