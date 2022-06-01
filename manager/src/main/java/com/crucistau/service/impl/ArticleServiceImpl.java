package com.crucistau.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.entity.Article;
import com.crucistau.mapper.ArticleMapper;
import com.crucistau.service.IArticleService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-20
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    @Autowired
    private ArticleServiceImpl articleService;
    @Override
    public IPage<Article> getPage(Integer currentPage, Integer pageSize, Article article) {
        Page<Article> articlePage = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(article.getName()), Article::getName, article.getName());
        lqw.like(Strings.isNotEmpty(article.getTime()), Article::getTime, article.getTime());
        return articleService.page(articlePage,lqw);
    }
}
