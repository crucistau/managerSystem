package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.commons.R;
import com.crucistau.entity.Student;

public interface IStuService extends IService<Student> {
    Boolean addStu(Student student);
    IPage<Student> getPage(int currentPage, int pageSize, Student student);
    Result login(UserDto userDto);

    Student getByStuId(String stuId);

    void changePwd(String userId, String password);

    Result queryById(Integer id);
}
