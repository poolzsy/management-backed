package com.lilac.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.lilac.entity.Admin;
import com.lilac.entity.DTO.AdminPageDTO;
import com.lilac.entity.Result;
import com.lilac.service.AdminService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    /**
     * 查询所有管理员
     * @return 管理员列表
     */
    @GetMapping("/selectAll")
    public Result selectAll(){
        List<Admin> adminList = adminService.selectAll();
        return Result.success(adminList);
    }

    /**
     * 分页查询管理员
     * @return 管理员列表
     */
    @GetMapping("/selectPage")
    public Result selectPage(AdminPageDTO adminPageDTO){
        return adminService.selectPage(adminPageDTO);
    }

    /**
     * 保存管理员
     * @param admin 管理员信息
     */
    @PostMapping("/save")
    public Result save(@RequestBody Admin admin){
        adminService.save(admin);
        return Result.success();
    }

    /**
     * 更新管理员
     * @param admin 管理员信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Admin admin){
        adminService.save(admin);
        return Result.success();
    }

    /**
     * 删除管理员
     * @param id 管理员ID
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        adminService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除管理员
     * @param ids 管理员ID列表
     */
    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        adminService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 导出管理员信息
     * @param response HttpServletResponse
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        adminService.export(response);
    }

    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("用户名","username");
        reader.addHeaderAlias("姓名", "name");
        reader.addHeaderAlias("手机号","phone");
        reader.addHeaderAlias( "邮箱","email");
        List<Admin> admins = reader.readAll(Admin.class);
        for (Admin admin : admins){
            adminService.save(admin);
        }
        return Result.success();
    }
}
