package com.crucistau.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.entity.Menu;

import com.crucistau.mapper.MenuMapper;
import com.crucistau.service.IMenuService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Resource
    private MenuMapper menuMapper;

    @Override
    public IPage<Menu> getPage(int currentPage, int pageSize, Menu menu) {
        Page<Menu> menuPage = new Page<>(currentPage, pageSize);
        //menuMapper.selectPage()
        return null;
    }

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> mqw = new QueryWrapper<>();
        mqw.like("name",name);
        mqw.orderByAsc("id");//升序
        //查询所有数据
        List<Menu> list = list(mqw);
        //找出pid为null（一级菜单）
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        //找出一级菜单的子菜单
        for (Menu menu : parentNodes) {
            // 筛选所有数据中pid=父级id的数据就是二级菜单
            menu.setChildren(list.stream().filter(m -> menu.getId().equals(m.getPid())).collect(Collectors.toList()));
        }
        return parentNodes;
    }
}

