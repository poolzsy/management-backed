package com.lilac.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.lilac.entity.DTO.UserPageDTO;
import com.lilac.entity.Result;
import com.lilac.entity.User;
import com.lilac.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     */
    @GetMapping("/selectAll")
    public Result selectAll(){
        List<User> userList = userService.selectAll();
        return Result.success(userList);
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/selectPage")
    public Result selectPage(UserPageDTO userPageDTO){
        return userService.selectPage(userPageDTO);
    }

    /**
     * 保存用户
     */
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        userService.save(user);
        return Result.success();
    }

    /**
     * 更新用户
     */
    @PutMapping("/update")
    public Result update(@RequestBody User user){
        userService.save(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        userService.delete(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     */
    @PostMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        userService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 导出所有用户
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        userService.export(response);
    }

    /**
     * 导出选中用户
     */
    @PostMapping("/export/selected")
    public void exportSelected(@RequestBody List<Integer> ids, HttpServletResponse response) throws Exception {
        userService.exportSelected(ids, response);
    }

    /**
     * 导入用户数据
     */
    @PostMapping("/import")
    public Result importData(MultipartFile file) throws Exception {
        return userService.importData(file);
    }

    /**
     * 提供Excel模板下载
     */
    @GetMapping("/import/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 创建一个内存中的Excel
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 定义表头
        writer.writeRow(Arrays.asList("用户名", "姓名", "手机号", "邮箱"));

        // 设置响应头
        String filename = URLEncoder.encode("用户导入模板", StandardCharsets.UTF_8);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename + ".xlsx");
        try (ServletOutputStream os = response.getOutputStream()) {
            writer.flush(os, true);
        } finally {
            writer.close();
        }
    }
}
