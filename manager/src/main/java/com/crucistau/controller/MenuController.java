package com.crucistau.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crucistau.commons.Result;
import com.crucistau.entity.Menu;
import com.crucistau.service.IMenuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Resource
    private IMenuService menuService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Menu menu) {
        menuService.saveOrUpdate(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        menuService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        menuService.removeByIds(ids);
        return Result.success();
    }

    //找出所有菜单项
    @GetMapping
    public Result findAll(@RequestParam String name) {

        List<Menu> menus = menuService.findMenus(name);

        return Result.success(menus);
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(menuService.getById(id));
    }

    /**
     * 查询所有菜单Id
     * @return
     */
    @GetMapping("/ids")
    public Result findAllIds(){
        return Result.success(menuService.list().stream().map(Menu::getId));
    }

    @GetMapping("{currentPage}/{pageSize}")
    public Result findPage(@PathVariable Integer currentPage,
                                @PathVariable Integer pageSize) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        Page<Menu> menuPage = new Page<>(currentPage, pageSize);
        return Result.success(menuService.page(menuPage, queryWrapper));
    }

}

