package com.crucistau.controller;


import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Teacher;
import com.crucistau.service.ITeacherService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author crucisTau
 * @since 2022-04-11
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private ITeacherService teacherService;

    //登录验证（token、menus）
    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto){
        return teacherService.login(userDto);
    }

    //修改
    @PutMapping
    public Result update(@RequestBody Teacher tea){
        boolean flag = teacherService.updateById(tea);
        return Result.info(flag,teacherService.getById(tea.getId()), flag ? "修改成功":"修改失败");
    }

    //新增
    @PostMapping
    public Result add(@RequestBody Teacher tea){
        boolean flag = teacherService.addTea(tea);
        return Result.info(flag,teacherService.getById(tea.getId()), flag ? "添加成功":"添加失败");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        teacherService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        teacherService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(teacherService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(teacherService.getById(id));
    }

    @GetMapping("/{currentPage}/{pageSize}")
    public Result findPage(@PathVariable Integer currentPage, @PathVariable Integer pageSize,Teacher teacher) {
        return Result.success(teacherService.getPage(currentPage, pageSize, teacher));
    }

    //更改密码
    @PutMapping("/changePwd")
    public Result changePwd(@RequestBody UserDto userDto){
        teacherService.changePwd(userDto.getUserId(), userDto.getPassword());
        return Result.success();
    }
}

