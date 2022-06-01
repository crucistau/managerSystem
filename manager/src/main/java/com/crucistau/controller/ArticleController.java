package com.crucistau.controller;


import cn.hutool.core.date.DateUtil;

import com.crucistau.commons.Result;
import com.crucistau.entity.Article;
import com.crucistau.service.IArticleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-20
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;

    // 新增或者更新
    @PostMapping
    public Result save(@RequestBody Article article) {
        if (article.getId()== null){
            article.setTime(DateUtil.now());
        }
        articleService.saveOrUpdate(article);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        articleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        articleService.removeByIds(ids);
        return Result.success();
    }

    @GetMapping
    public Result findAll() {
        return Result.success(articleService.list());
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(articleService.getById(id));
    }

    @GetMapping("{currentPage}/{pageSize}")
    public Result findPage(@PathVariable Integer currentPage,
                                @PathVariable Integer pageSize,Article article) {
        return Result.success(articleService.getPage(currentPage, pageSize, article));
    }

}

