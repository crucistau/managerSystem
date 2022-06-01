package com.crucistau.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crucistau.entity.StudentSubject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-14
 */
@Mapper
public interface StudentSubjectMapper extends BaseMapper<StudentSubject> {

    @Select("select subject_id from tb_student_subject where student_id = #{stuId}")
    List<Integer> getNum(@Param("stuId") String stuId);

    @Select("select student_id from tb_student_subject where subject_id =  #{subId} ")
    String getStudentId(@Param("subId") String subId);

    @Insert("insert into  tb_student_subject values (#{stuId},#{subjectNum})")
    int saveSub(@Param("subjectNum") String subjectNum, @Param("stuId") String stuId);

}
