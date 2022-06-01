package com.crucistau.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.commons.Constants;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.*;
import com.crucistau.mapper.ManageMapper;
import com.crucistau.mapper.RoleAndMenuMapper;
import com.crucistau.mapper.RoleMapper;
import com.crucistau.service.IManageService;
import com.crucistau.utils.TokenUtils;
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
 * @since 2022-04-17
 */
@Service
public class ManageServiceImpl extends ServiceImpl<ManageMapper, Manage> implements IManageService {

    @Resource
    private ManageMapper manageMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private MenuServiceImpl menuService;

    @Resource
    private RoleAndMenuMapper ramMapper;
    @Override
    public Result login(UserDto userDto) {

        QueryWrapper<Manage> queryWrapper = new QueryWrapper<Manage>();
        queryWrapper.eq("man_id",userDto.getUserId());
        queryWrapper.eq("man_pwd",userDto.getPassword());
        Manage one = getOne(queryWrapper);
        if (one != null){
            //在数据库中查找成功
            userDto.setMail(one.getEmail());
            userDto.setIdentity(one.getIdentify());
            userDto.setUserName(one.getManName());
            userDto.setFaculty(one.getFaculty());
            //设置Token
            String token = TokenUtils.getToken(one.getId().toString(), one.getIdentify(), userDto.getPassword());//将ID与密码设置为token
            userDto.setToken(token);

            //获取到响应用户获得的菜单
            String flag = one.getIdentify();//ROLE_STUDENT
            //获取当前菜单列表
            List<Menu> roleMenus = getRoleMenus(flag);

            userDto.setMenus(roleMenus);
            return Result.success(userDto, "登录成功");
        }else{
            //查找失败
            return Result.error(Constants.CODE_400,"用户名或密码错误");
        }
    }

    @Override
    public boolean addMan(Manage man) {
            //新增  设置密码
            String pwd = man.getManId();
            pwd = SecureUtil.md5(pwd.substring(pwd.length()-6));
            man.setManPwd(pwd);
            manageMapper.insert(man);
        return true;
    }

    @Override
    public IPage<Manage> getPag(Integer currentPage, Integer pageSize, Manage man) {
        Page<Manage> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Manage> lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(man.getFaculty()),Manage::getFaculty,man.getFaculty());
        lqw.like(Strings.isNotEmpty(man.getManName()),Manage::getManName,man.getManName());
        return manageMapper.selectPage(page, lqw);
    }

    @Override
    public void changePwd(String userId, String password) {
        LambdaQueryWrapper<Manage> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Manage::getManId,userId);
        Manage manage = manageMapper.selectOne(lqw);
        password = SecureUtil.md5(password);
        manage.setManPwd(password);
        manageMapper.updateById(manage);
    }

    /**
     * 获取当前角色的菜单列表
     * @param flag
     * @return
     */
    private List<Menu> getRoleMenus(String flag){

        Integer roleId = roleMapper.selectByFlag(flag);
        List<Integer> menuIds = ramMapper.selectByRoleId(roleId);//得到结果

        //查出系统所有菜单(树形)
        List<Menu> menus = menuService.findMenus("");
        //new出一个最后筛选完成后所拥有的菜单
        List<Menu> roleMenus = new ArrayList<>();
        //筛选出当前用户角色的菜单
        for (Menu menu: menus) {
            if (menuIds.contains(menu.getId())){
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            //移除children里面不在menIds集合中的元素
            children.removeIf(child-> !menuIds.contains(child.getId()));
        }
        return  roleMenus;
    }
}
