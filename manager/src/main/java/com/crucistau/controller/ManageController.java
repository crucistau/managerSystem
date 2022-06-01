package com.crucistau.controller;

import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Manage;
import com.crucistau.service.IManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-17
 */
@RestController
@RequestMapping("/manage")
public class ManageController {

    @Resource
    private IManageService manageService;


    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto){
        return manageService.login(userDto);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        manageService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        manageService.removeByIds(ids);
        return Result.success();
    }
    //修改
    @PutMapping
    public Result update(@RequestBody Manage man){
        boolean flag = manageService.updateById(man);
        return Result.info(flag,manageService.getById(man.getId()),flag ? "修改成功":"修改失败");
    }
    //新增
    @PostMapping
    public Result addMan(@RequestBody Manage man){
        boolean flag = manageService.addMan(man);
        return Result.info(flag,manageService.getById(man.getId()),flag ? "新增成功":"新增失败");
    }
    @GetMapping
    public Result findAll() {
        return Result.success(manageService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(manageService.getById(id));
    }

    @GetMapping("{currentPage}/{pageSize}")
    public Result findPage(@PathVariable Integer currentPage, @PathVariable Integer pageSize, Manage man) {
        return Result.success(manageService.getPag(currentPage, pageSize, man));
    }

    //更改密码
    @PutMapping("/changePwd")
    public Result changePwd(@RequestBody UserDto userDto){
        manageService.changePwd(userDto.getUserId(), userDto.getPassword());
        return Result.success();
    }

}

