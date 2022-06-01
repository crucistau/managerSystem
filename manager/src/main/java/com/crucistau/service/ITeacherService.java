package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Student;
import com.crucistau.entity.Teacher;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-11
 */
public interface ITeacherService extends IService<Teacher> {

    IPage<Teacher> getPage(Integer pageNum, Integer pageSize, Teacher teacher);

    Result login(UserDto userDto);

    boolean addTea(Teacher tea);

    void changePwd(String userId, String password);
}
