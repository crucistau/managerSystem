package com.crucistau.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crucistau.entity.Menu;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
public interface IMenuService extends IService<Menu> {
    IPage<Menu> getPage(int currentPage, int pageSize, Menu menu);

    List<Menu> findMenus(String name);

}
