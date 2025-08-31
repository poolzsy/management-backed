package com.lilac.controller;

import cn.hutool.core.io.FileUtil;
import com.lilac.entity.Result;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.SystemException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * 文件下载
     * @param fileName 文件名
     */
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        String filePath = System.getProperty("user.dir") + "/files/";
        String realFile = filePath + fileName;
        boolean  exist = FileUtil.exist(realFile);
        if (!exist){
            throw new SystemException(HttpsCodeEnum.BAD_REQUEST, "文件不存在");
        }
        byte[] bytes = FileUtil.readBytes(realFile);
        ServletOutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }

    /**
     * 文件上传
     * @param file 文件
     */
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile file) throws IOException {
        String filePath = System.getProperty("user.dir") + "/files/";
        if (!FileUtil.exist(filePath)){
            FileUtil.mkdir(filePath);
        }
        String fileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String newFileName = uuid + fileName;
        File dest = new File(filePath + newFileName);
        file.transferTo(dest);
        String url = "http://localhost:9090/file/download/" + newFileName;
        return Result.success(url);
    }
}
