package com.crucistau.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crucistau.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-20
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
