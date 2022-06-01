package com.crucistau.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crucistau.commons.Result;
import com.crucistau.entity.Role;

import com.crucistau.service.IRoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private IRoleService roleService;


    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Role role) {
        roleService.saveOrUpdate(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        roleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        roleService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {

        return Result.success(roleService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(roleService.getById(id));
    }

    @GetMapping("/{currentPage}/{pageSize}")
    public Result getPage(@PathVariable Integer currentPage, @PathVariable Integer pageSize, Role role){
        IPage<Role> page = roleService.getPage(currentPage, pageSize, role);
        return Result.success(page);
    }


    /**
     * 绑定用户与菜单的关系
     * @param roleId 用户ID
     * @param menuIds  菜单IDs
     * @return
     */
    @PostMapping("/roleMenu/{roleId}")
    public Result roleMenu(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds){
        roleService.setRoleMenu(roleId, menuIds);
        return Result.success(null,"修改成功");
    }

    /**
     * 查询对应角色所绑定的菜单
     * @param roleId
     * @return
     */
    @GetMapping("/roleMenu/{roleId}")
    public Result roleMenu(@PathVariable Integer roleId){
        return Result.success(roleService.getMenuIdsByRoleId(roleId));
    }

}
