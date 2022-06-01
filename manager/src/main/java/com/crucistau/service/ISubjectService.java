package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.commons.Result;
import com.crucistau.entity.Student;
import com.crucistau.entity.StudentSubject;
import com.crucistau.entity.Subject;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-10
 */
public interface ISubjectService extends IService<Subject> {

    IPage<Subject> getPage(Integer pageNum, Integer pageSize, Subject subject);

    Result setSub(String subjectNum, String stuId);


    List<Student> findStu(String subjectId);

    //设置该课题为选中，且为传过来的那名学生
    int setSubByStu(StudentSubject ss);

    List<Subject> findSub(String studentId);

    void changeSubject(Subject subject);
}
