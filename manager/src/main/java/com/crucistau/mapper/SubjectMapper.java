package com.crucistau.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crucistau.entity.Student;
import com.crucistau.entity.Subject;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crucisTau'''
 * @since 2022-04-10
 */
@Mapper
public interface SubjectMapper extends BaseMapper<Subject> {

    @Select("SELECT a.* FROM tb_stu a ,tb_student_subject b \n" +
            "WHERE b.student_id = a.stu_id AND b.subject_id=#{subjectId} ")
    List<Student> findStu(@Param("subjectId")String subjectId);

    @Update("update tb_subject Set state = 1 where id = #{id}")
    int setState(@Param("id") String subjectId);

    @Update("update tb_subject Set student = #{studentName} where id = #{id}")
    void setStudent(@Param("id")String subjectId, @Param("studentName")String  stuName);

    @Select("SELECT a.* from tb_subject a, tb_student_subject b \n" +
    "WHERE b.student_id = #{studentId} AND b.subject_id = a.id")
    List<Subject> findSub(@Param("studentId") String studentId);
}
