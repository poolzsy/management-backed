package com.lilac.controller;

import com.lilac.entity.Admin;
import com.lilac.entity.DTO.AdminPageDTO;
import com.lilac.entity.Result;
import com.lilac.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 删除管理员
     * @param id 管理员ID
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        adminService.delete(id);
        return Result.success();
    }

    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        adminService.deleteBatch(ids);
        return Result.success();
    }
}
