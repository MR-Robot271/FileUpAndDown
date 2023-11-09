package com.kb.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("file")
public class FileController {
    @Value("D:/GitProjects/FileUpAndDown/upload")
    private String uploadFilePath;
    @Value("D:/GitProjects/FileUpAndDown/download")
    private String downloadFilePath;

    // 其他类可以访问，用于传递上传文件的文件名
    public static String fileName;

    /**
     * @Description: 上传文件的接口
     * @Param: [MultipartFile：files]
     * @return: java.lang.String
     * @Date: 2023/11/9
     */
    @PostMapping("/upload")
    public String fileUpload(@RequestParam(value = "files",required = true) MultipartFile files[]) {
        // 遍历接收的文件
        for (int i = 0; i < files.length; i++) {
            // 获取文件名
            String originalFilename = files[i].getOriginalFilename();
            // 创建文件对象
            fileName=originalFilename;
            File uploadFile = new File(uploadFilePath + "/" + originalFilename);
            // 判断文件夹是否存在
            if (!uploadFile.getParentFile().exists()){
                // 不存在则创建文件夹
                uploadFile.getParentFile().mkdirs();
            }
            // 将上传的文件导入本地
            try {
                // 上传重复的文件不会报错，后上传的文件会直接覆盖已经上传的文件
                files[i].transferTo(uploadFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "成功";
    }

    /**
     * @Description: 下载文件的接口
     * @Param: [response]
     * @return: java.lang.String
     * @Date: 2023/11/9
     */
    @RequestMapping("/download")
    public String download(HttpServletResponse response){
        // 判断文件是否存在
        String path=uploadFilePath+"/"+fileName;
        File file = new File(path);
        if (!file.exists()){
            return "文件不存在";
        }
        // 用response设置返回文件的格式 以文件流的方式返回
        response.setContentType("application/octet-stream");
        // 设置编码方式为utf-8
        response.setCharacterEncoding("utf-8");
        // 设置文件流长度
        response.setContentLength((int) file.length());
        // 设置返回头
        response.setHeader("Content-Disposition","attachment;filename="+fileName);
        // 设置下载后的文件名

        // 将文件转化为文件输出流
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));) {
            byte[] bytes = new byte[1024];
            OutputStream os=response.getOutputStream();
            int i=0;
            while ((i=bufferedInputStream.read(bytes))!=-1){
                os.write(bytes,0,i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "下载失败";
        }
        return "下载成功";
    }
}
