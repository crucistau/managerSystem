package com.crucistau.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crucistau.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

}
