package com.crucistau.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crucistau.entity.Document;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocMapper extends BaseMapper<Document> {
}
