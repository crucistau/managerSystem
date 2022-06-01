package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.entity.Article;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-20
 */
public interface IArticleService extends IService<Article> {

    IPage<Article> getPage(Integer currentPage, Integer pageSize, Article article);
}
