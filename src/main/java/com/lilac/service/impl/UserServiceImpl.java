package com.lilac.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.entity.Account;
import com.lilac.entity.DTO.RegisterDTO;
import com.lilac.entity.DTO.UserPageDTO;
import com.lilac.entity.Result;
import com.lilac.entity.User;
import com.lilac.entity.VO.PageVO;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.SystemException;
import com.lilac.mapper.UserMapper;
import com.lilac.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询所有用户
     * @return 用户列表
     */
    @Override
    public List<User> selectAll() {
        return userMapper.selectAll();
    }

    /**
     * 分页查询用户
     * @param userPageDTO 分页参数
     * @return 用户列表
     */
    @Override
    public Result selectPage(UserPageDTO userPageDTO) {
        PageHelper.startPage(userPageDTO.getPageNum(), userPageDTO.getPageSize());
        List<User> users = userMapper.selectPage(userPageDTO.getName());
        PageInfo<User> pageInfo = new PageInfo<>(users);
        PageVO pageVO = new PageVO(pageInfo.getList(), pageInfo.getTotal());
        return Result.success(pageVO);
    }

    /**
     * 保存或更新用户信息
     * @param user 用户信息
     */
    @Override
    public void save(User user) {
        checkUniqueness(user);
        user.setRole("user");
        if(!StringUtils.hasText(user.getPassword())){
            user.setPassword("123456");
        }
        if(!StringUtils.hasText(user.getName())){
            user.setName(user.getUsername());
        }
        if (user.getId() == null) {
            userMapper.save(user);
        } else {
            userMapper.update(user);
        }
    }

    /**
     * 校验用户名、手机号是否唯一
     * @param user 用户信息
     */
    private void checkUniqueness(User user) {
        // 校验用户名
        User dbUserByUsername = userMapper.findByUsername(user.getUsername());
        // 如果查到了记录，并且(这是新增操作 || 查到的记录ID和当前操作的记录ID不一致)，则说明冲突
        if (dbUserByUsername != null && (user.getId() == null || !dbUserByUsername.getId().equals(user.getId()))) {
            throw new SystemException(HttpsCodeEnum.USER_NAME_EXIST);
        }
        // 校验手机号
        User dbUserByPhone = userMapper.findByPhone(user.getPhone());
        if (dbUserByPhone != null && (user.getId() == null || !dbUserByPhone.getId().equals(user.getId()))) {
            throw new SystemException(HttpsCodeEnum.USER_PHONE_EXIST);
        }
    }

    /**
     * 删除用户
     * @param id 用户ID
     */
    @Override
    public void delete(Integer id) {
        userMapper.delete(id);
    }

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     */
    @Override
    @Transactional
    public void deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        userMapper.deleteBatch(ids);
    }

    /**
     * 导出所有用户数据
     * @param response HttpServletResponse对象
     * @throws IOException IO异常
     */
    @Override
    public void export(HttpServletResponse response) throws IOException {
        // 查询所有管理员
        List<User> users = userMapper.selectAll();
        exportAdminsToExcel(users, response);
    }

    /**
     * 导出选中的用户数据
     * @param ids 用户ID列表
     * @param response HttpServletResponse对象
     * @throws IOException IO异常
     */
    @Override
    public void exportSelected(List<Integer> ids, HttpServletResponse response) throws IOException {
        if (ids == null || ids.isEmpty()) {
            throw new SystemException(HttpsCodeEnum.BAD_REQUEST, "请选择要导出的记录");
        }
        List<User> users = userMapper.selectByIds(ids);
        exportAdminsToExcel(users, response);
    }

    /**
     * 导出用户数据到Excel文件
     * @param users 用户列表
     * @param response HttpServletResponse对象
     * @throws IOException IO异常
     */
    private void exportAdminsToExcel(List<User> users, HttpServletResponse response) throws IOException {
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 定义表头别名
        writer.addHeaderAlias("username", "用户名");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("phone", "手机号");
        writer.addHeaderAlias("email", "邮箱");
        writer.setOnlyAlias(true);
        // 写入数据
        writer.write(users, true);
        // 设置响应头
        String filename = URLEncoder.encode("用户信息", StandardCharsets.UTF_8);
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
     * 批量导入用户数据
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
        List<User> usersToImport = reader.readAll(User.class);
        if (usersToImport.isEmpty()) {
            return Result.error(HttpsCodeEnum.BAD_REQUEST, "请上传Excel文件");
        }
        // 数据校验
        List<String> errorMessages = new ArrayList<>();
        for (int i = 0; i < usersToImport.size(); i++) {
            User user = usersToImport.get(i);
            // 校验逻辑
            User dbUserByUsername = userMapper.findByUsername(user.getUsername());
            if (dbUserByUsername != null) {
                errorMessages.add("第 " + (i + 1) + " 行，用户名 '" + user.getUsername() + "' 已存在");
            }
            User dbUserByPhone = userMapper.findByPhone(user.getPhone());
            if (dbUserByPhone != null) {
                errorMessages.add("第 " + (i + 1) + " 行，手机号 '" + user.getPhone() + "' 已存在");
            }
        }
        // 如果有校验失败的记录，则终止导入，并返回所有错误信息
        if (!errorMessages.isEmpty()) {
            throw new SystemException(HttpsCodeEnum.BAD_REQUEST, String.join("<br>", errorMessages));
        }
        // 批量插入数据库
        try {
            userMapper.saveBatch(usersToImport);
        } catch (Exception e) {
            // 捕获数据库层面的异常，例如唯一索引冲突等
            throw new SystemException(HttpsCodeEnum.SYSTEM_ERROR,"批量插入失败，请检查数据");
        }

        return Result.success("成功导入 " + usersToImport.size() + " 条数据");
    }

    /**
     * 登录
     * @param account 登录信息
     */
    @Override
    public Account login(Account account) {
        User user = userMapper.findByUsername(account.getUsername());
        if (user == null) {
            throw new SystemException(HttpsCodeEnum.USER_NOT_EXIST);
        }
        if (!user.getPassword().equals(account.getPassword())) {
            throw new SystemException(HttpsCodeEnum.USER_PASSWORD_ERROR);
        }
        return user;
    }

    /**
     * 注册
     * @param registerDTO 注册信息
     */
    @Override
    public Result register(RegisterDTO registerDTO) {
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(registerDTO.getPassword());
        save(user);
        return Result.success();
    }
}
