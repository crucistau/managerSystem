package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.entity.Role;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
public interface IRoleService extends IService<Role> {
    IPage<Role> getPage(int currentPage, int pageSize, Role role);
    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getMenuIdsByRoleId(Integer roleId);


}
