package com.crucistau.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.commons.Constants;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Menu;
import com.crucistau.entity.Student;
import com.crucistau.entity.Teacher;
import com.crucistau.mapper.RoleAndMenuMapper;
import com.crucistau.mapper.RoleMapper;
import com.crucistau.mapper.TeacherMapper;
import com.crucistau.service.ITeacherService;
import com.crucistau.utils.TokenUtils;
import org.apache.logging.log4j.util.Strings;
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
 * @since 2022-04-11
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private MenuServiceImpl menuService;

    @Resource
    private RoleAndMenuMapper ramMapper;

    @Override
    public IPage<Teacher> getPage(Integer pageNum, Integer pageSize, Teacher teacher) {
        Page<Teacher> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Teacher>  lqw = new LambdaQueryWrapper<>();
        lqw.like(Strings.isNotEmpty(teacher.getTeaName()),Teacher::getTeaName,teacher.getTeaName());
        return teacherMapper.selectPage(page, lqw);
    }

    //用户登录
    @Override
    public Result login(UserDto userDto) {
        LambdaQueryWrapper<Teacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teacher::getTeaId,userDto.getUserId());
        lqw.eq(Teacher::getTeaPwd,userDto.getPassword());
        Teacher teacher = teacherMapper.selectOne(lqw);
        if (teacher!=null){
            userDto.setMail(teacher.getEmail());
            userDto.setUserName(teacher.getTeaName());
            userDto.setFaculty(teacher.getFaculty());
            userDto.setIdentity(teacher.getIdentity());
            //查找成功 返回token
            String token = TokenUtils.getToken(teacher.getId().toString(), teacher.getIdentity(), teacher.getTeaPwd());
            userDto.setToken(token);

            //获取到菜单列表
            String identity = teacher.getIdentity();
            List<Menu> roleMenus = getRoleMenus(identity);
            userDto.setMenus(roleMenus);
            return Result.success(userDto, "登录成功");
        }else {
            //查找失败
            return Result.error(Constants.CODE_400,"用户名或密码错误");
        }
    }


    @Override
    public boolean addTea(Teacher tea) {
        //新增设置后六位为密码
        String pwd = tea.getTeaId();
        pwd = SecureUtil.md5(pwd.substring(pwd.length()-6));
        tea.setTeaPwd(pwd);
        teacherMapper.insert(tea);
        return true;
    }

    @Override
    public void changePwd(String userId, String password) {
        LambdaQueryWrapper<Teacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teacher::getTeaId,userId);
        Teacher manage = teacherMapper.selectOne(lqw);
        password = SecureUtil.md5(password);
        manage.setTeaPwd(password);
        teacherMapper.updateById(manage);
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
