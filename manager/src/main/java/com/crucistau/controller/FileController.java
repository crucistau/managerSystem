package com.crucistau.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crucistau.commons.Constants;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.FileDto;
import com.crucistau.entity.Document;
import com.crucistau.entity.Student;
import com.crucistau.service.impl.DocServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 文件上传相关接口
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    //将配置文件中的文件路径注入
    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Autowired
    private DocServiceImpl dsI;

    /**
     * 文件存储上传接口
     * @param file   前端传来的文件
     * @return url  下载地址
     * @throws IOException
     */
    @PostMapping("/upload")
    public Result upload(@RequestBody MultipartFile file, FileDto fileDto) throws IOException {

        String originalFileName = file.getOriginalFilename();
        String type = FileUtil.extName(originalFileName);//文件类型 hutools
        long size = file.getSize();;

        //存储到磁盘
        File upload = new File(fileUploadPath);
        //判断配置的文件目录是否存在，如果不存在则创建一个新的文件目录里
        if (!upload.exists()){
            upload.mkdirs();
        }
        //定义一个文件唯一的标识位
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUid = uuid +StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath + fileUUid);//最终要保存的文件路径

        String md5 = SecureUtil.md5(file.getInputStream());//获取前端文件上传来的md5

        //通过md5在数据库中查询文件
        QueryWrapper<Document> qwe = new QueryWrapper<>();
        qwe.eq("md5", md5);
        Document one = dsI.getOne(qwe);

        //获取文件的url
        String url;
        if (one != null){//文件存在
            url =one.getUrl();
            uploadFile.delete();//删除上传的文件信息
            log.info("文件存在");
            //return Result.error();
        }else {
            // 文件不存在 把获取到的文件存储到磁盘目录中去
            file.transferTo(uploadFile);
            url = "http://localhost:8081/file/" +fileUUid;
            //存储到数据库

            Document document = new Document();
            BeanUtils.copyProperties(fileDto, document);
            document.setName(originalFileName);
            //document.setType(type);
            document.setSize(size/1024);
            document.setUrl(url);
            document.setMd5(md5);
            dsI.save(document);
        }

        return Result.success(url);
    }

    /**
     * 文件下载接口  http://localhost:8081/file/{fileUUID}
     * @param fileUUID
     */
    @GetMapping("/{fileUUID}/{name}")
    public void download(@PathVariable String fileUUID, @PathVariable String name, HttpServletResponse response ) throws IOException {

        // 从本地找根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + name); //使用url做名称 URLEncoder.encode(fileUUID, "UTF-8")
        response.setContentType("application/octet-stream");

        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }


    /**
     * 根据课程Id找到学生Id 找到该学生所上传的文件
     * @return
     */
    @GetMapping("/subId/{subId}")
    public Result getDocWithSubId(@PathVariable String subId){
        return dsI.getDocWithSubId(subId);
    }

    @DeleteMapping("/del/{id}")
    public Result del(@PathVariable Integer id){
        dsI.removeById(id);
        return Result.success();
    }
}
