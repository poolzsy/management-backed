package com.lilac.service;

import com.lilac.entity.Account;
import com.lilac.entity.DTO.RegisterDTO;
import com.lilac.entity.DTO.UserPageDTO;
import com.lilac.entity.Result;
import com.lilac.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    /**
     * 查询所有用户
     */
    List<User> selectAll();

    /**
     * 分页查询用户
     */
    Result selectPage(UserPageDTO userPageDTO);

    /**
     * 保存用户
     */
    void save(User user);

    /**
     * 删除用户
     */
    void delete(Integer id);

    /**
     * 批量删除用户
     */
    void deleteBatch(List<Integer> ids);

    /**
     * 导出用户数据
     */
    void export(HttpServletResponse response) throws IOException;

    /**
     * 导出选中的用户数据
     */
    void exportSelected(List<Integer> ids, HttpServletResponse response) throws IOException;

    /**
     * 导入用户数据
     */
    Result importData(MultipartFile file) throws IOException;

    /**
     * 登录
     */
    Account login(Account account);

    /**
     * 注册
     */
    Result register(RegisterDTO registerDTO);

    /**
     * 根据id查询用户
     */
    Account selectById(String userId);
}
