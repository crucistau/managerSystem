package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Manage;
import com.crucistau.entity.Student;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-17
 */
public interface IManageService extends IService<Manage> {

    Result login(UserDto userDto);

    boolean addMan(Manage man);

    IPage<Manage> getPag(Integer currentPage, Integer pageSize, Manage man);

    void changePwd(String userId, String password);
}
