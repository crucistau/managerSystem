package com.crucistau.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crucistau.commons.Constants;
import com.crucistau.commons.Result;
import com.crucistau.entity.Dict;
import com.crucistau.service.IDictService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-05
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    @Resource
    private IDictService dictService;

    @GetMapping("/icons")
    public Result findAll() {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", Constants.DICT_TYPE_ICON);
        return Result.success(dictService.list(queryWrapper));
    }


}

