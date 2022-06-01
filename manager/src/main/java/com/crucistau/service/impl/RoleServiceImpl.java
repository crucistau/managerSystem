package com.crucistau.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.entity.Menu;
import com.crucistau.entity.Role;
import com.crucistau.entity.RoleMenu;
import com.crucistau.mapper.RoleAndMenuMapper;
import com.crucistau.mapper.RoleMapper;
import com.crucistau.service.IMenuService;
import com.crucistau.service.IRoleService;
import com.crucistau.utils.CacheClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Resource
    private RoleAndMenuMapper roleAndMenuMapper;
    @Resource
    private IMenuService menuService;
    @Resource
    private CacheClient cacheClient;

    @Override
    public IPage<Role> getPage(int currentPage, int pageSize, Role role) {
        Page<Role> rolePage = new Page<>(currentPage, pageSize);
        //模糊查找
        LambdaQueryWrapper<Role> lqw = new LambdaQueryWrapper();
        lqw.like(Strings.isNotEmpty(role.getName()), Role::getName, role.getName());
        lqw.like(Strings.isNotEmpty(role.getDescription()), Role::getDescription, role.getDescription());
        lqw.orderByAsc(Role::getId);//
        return roleMapper.selectPage(rolePage, lqw);
    }

    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        //先删除角色绑定的菜单
        roleAndMenuMapper.deleteByRoleId(roleId);

        //在将前端传来的菜单Id数组绑定到当前的角色上
        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);
        for (Integer menuId: menuIds) {
            Menu menu = menuService.getById(menuId);
            //查到的menu为二级菜单
            if ( menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())){//二级菜单&&传过来的menuId数组中没有它的父级id
                System.out.println("==============================================");
                System.out.println(menu.getPid());
                //那就要为其补上父级Id
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menu.getPid());
                menuIdsCopy.add(menu.getPid());
                roleAndMenuMapper.insert(roleMenu);
            }
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleAndMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getMenuIdsByRoleId(Integer roleId) {
      return roleAndMenuMapper.selectByRoleId(roleId);
    }




}
