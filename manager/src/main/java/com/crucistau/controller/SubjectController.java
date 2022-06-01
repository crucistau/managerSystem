package com.crucistau.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crucistau.commons.Result;
import com.crucistau.entity.Student;
import com.crucistau.entity.StudentSubject;
import com.crucistau.entity.Subject;
import com.crucistau.service.ISubjectService;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-10
 */
@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Resource
    private ISubjectService subjectService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Subject subject) {
        subjectService.changeSubject(subject);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        subjectService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        subjectService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(subjectService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(subjectService.getById(id));
    }

    @GetMapping("{pageNum}/{pageSize}")
    public Result findPage(@PathVariable Integer pageNum, @PathVariable Integer pageSize,Subject subject) {
        IPage<Subject> page = subjectService.getPage(pageNum, pageSize, subject);
        return Result.success(page);
    }


    //设置学生课题关联表
    @GetMapping("/select/{subjectNum}/{stuId}")
    public Result selectSubject(@PathVariable String subjectNum, @PathVariable String stuId){
        //先查看学生是否选中了三个课题,如果不够则继续选，反之提示
        return subjectService.setSub(subjectNum, stuId);
    }

    //查看学生所选课程信息
    @GetMapping("/sub/{studentId}")
    public Result selectSub(@PathVariable String studentId){
        return Result.success(subjectService.findSub(studentId));
    }

    //查询课程对应的学生信息
    @GetMapping("/stu/{subjectId}")
    public Result selectStu(@PathVariable String subjectId){
        return Result.success(subjectService.findStu(subjectId));
    }

    //选中学生
    @PutMapping("/setStuAndSub")
    public Result setStuAndSub(@RequestBody StudentSubject ss){
        //先删除该课题的所有学生，然后设置只有一个学生。同时设置此课题状态为已选中
        int i = subjectService.setSubByStu(ss);
        return Result.success(i);
    }



    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws  Exception{
        List<Subject> list = subjectService.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
//        writer.addHeaderAlias("id", "id");
//        writer.addHeaderAlias("subjectName", "课程名");
//        writer.addHeaderAlias("faculty", "所属学院");
//        writer.addHeaderAlias("teacherId", "教师Id");
//        writer.addHeaderAlias("secretaryId", "课程秘书Id");
//        writer.addHeaderAlias("state", "状态");
        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("课题信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }

    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 1、 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        //List<Student> list = reader.readAll(Student.class);
        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<Subject> subs = CollUtil.newArrayList();
        for (List<Object> row : list) {
            Subject sub = new Subject();
            sub.setSubjectName(row.get(0).toString());
            sub.setFaculty(SecureUtil.md5(row.get(1).toString()));
            sub.setTeacher((String)row.get(2));
            sub.setSecretary((String)row.get(3));
            sub.setFaculty(row.get(4).toString());
            subs.add(sub);
        }
        subjectService.saveBatch(subs);
        return true;
    }


}

