package com.crucistau.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.commons.Constants;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Manage;
import com.crucistau.entity.Menu;
import com.crucistau.entity.Subject;
import com.crucistau.mapper.RoleAndMenuMapper;
import com.crucistau.mapper.RoleMapper;
import com.crucistau.utils.CacheClient;
import com.crucistau.utils.RedisData;
import com.crucistau.utils.TokenUtils;
import com.crucistau.entity.Student;
import com.crucistau.mapper.StudentMapper;
import com.crucistau.service.IStuService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.crucistau.commons.Constants.CODE_400;
import static com.crucistau.commons.RedisConstants.*;

@Service
public class StuServiceImpl extends ServiceImpl<StudentMapper, Student> implements IStuService {
    @Autowired
    private StudentMapper studentMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private MenuServiceImpl menuService;

    @Resource
    private RoleAndMenuMapper ramMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;

    //新增学生类 密码为学号后六位
    @Override
    @Transactional
    public Boolean addStu(Student student) {
        Student stu = studentMapper.selectById(student.getId());//判断是更新还是新增
        if (stu != null) {
            // 1、更新数据库
            updateById(student);
            //2、删除缓存
            //删除缓存
            stringRedisTemplate.delete(CACHE_STUDENT_KEY + student.getId());
        } else {
            //新增  设置密码
            String pwd = student.getStuId();
            pwd = SecureUtil.md5(pwd.substring(pwd.length() - 6));
            student.setStuPwd(pwd);
            studentMapper.insert(student);
        }
        Set<String> keys = stringRedisTemplate.keys(CACHE_STUDENTLIST_KEY + "*");
        if ( !CollectionUtils.isEmpty(keys) ){
            stringRedisTemplate.delete(keys);
        }
        return true;
    }

    @Override
    public IPage<Student> getPage(int currentPage, int pageSize, Student student) {
        Page<Student> studentPage = new Page<>(currentPage, pageSize);
        //模糊查找
        LambdaQueryWrapper<Student> lqw = new LambdaQueryWrapper();
        lqw.like(Strings.isNotEmpty(student.getStuName()), Student::getStuName, student.getStuName());
        lqw.like(Strings.isNotEmpty(student.getStuId()), Student::getStuId, student.getStuId());
        lqw.like(Strings.isNotEmpty(student.getFaculty()), Student::getFaculty, student.getFaculty());
        lqw.orderByDesc(Student::getCreatTime);

        IPage page = cacheClient.queryListWithPassThrough(
                CACHE_STUDENTLIST_KEY, studentPage, student, lqw, Page.class, this::page, CACHE_STUDENT_TTL, TimeUnit.MINUTES);
        return page;
        //return studentMapper.selectPage(studentPage, lqw);
    }

    //登录验证
    @Override
    public Result login(UserDto userDto) {

        QueryWrapper<Student> queryWrapper = new QueryWrapper<Student>();
        queryWrapper.eq("stu_id", userDto.getUserId());

        queryWrapper.eq("stu_pwd", userDto.getPassword());
        Student one = getOne(queryWrapper);
        userDto.setStudent(one);//将从数据库查到的用户设置到userDto中
        if (one != null) {
            //在数据库中查找成功
            userDto.setMail(one.getEmail());
            userDto.setIdentity(one.getIdentity());
            userDto.setUserName(one.getStuName());
            userDto.setFaculty(one.getFaculty());
            //设置Token
            String token = TokenUtils.getToken(one.getId().toString(), one.getIdentity(), userDto.getPassword());//将ID与密码设置为token
            userDto.setToken(token);

            //获取到响应用户获得的菜单
            String flag = one.getIdentity();//ROLE_STUDENT
            //获取当前菜单列表
            List<Menu> roleMenus = getRoleMenus(flag);

            userDto.setMenus(roleMenus);
            return Result.success(userDto, "登录成功");
        } else {
            //查找失败
            return Result.error(CODE_400, "用户名或密码错误");
        }
    }


    //通过学生ID
    @Override
    public Student getByStuId(String stuId) {
        QueryWrapper<Student> qw = new QueryWrapper<>();
        qw.eq("stu_id", stuId);
        return getOne(qw);
    }

    /**
     * 修改密码
     *
     * @param userId
     * @param password
     */
    @Override
    public void changePwd(String userId, String password) {
        LambdaQueryWrapper<Student> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Student::getStuId, userId);
        Student manage = studentMapper.selectOne(lqw);
        password = SecureUtil.md5(password);
        manage.setStuPwd(password);
        studentMapper.updateById(manage);
    }


    @Override
    public Result queryById(Integer id) {
        //解决缓存穿透
        Student student = cacheClient.queryWithPassThrough(
                CACHE_STUDENT_KEY, id, Student.class, this::getById, CACHE_STUDENT_TTL, TimeUnit.MINUTES);
        if (student == null){
            return Result.error( CODE_400,"数据为空");
        }
        return Result.success(student);
    }


    /**
     * 获取当前角色的菜单列表
     *
     * @param flag
     * @return
     */
    private List<Menu> getRoleMenus(String flag) {

        Integer roleId = roleMapper.selectByFlag(flag);
        List<Integer> menuIds = ramMapper.selectByRoleId(roleId);//得到结果

        //查出系统所有菜单(树形)
        List<Menu> menus = menuService.findMenus("");
        //new出一个最后筛选完成后所拥有的菜单
        List<Menu> roleMenus = new ArrayList<>();
        //筛选出当前用户角色的菜单
        for (Menu menu : menus) {
            if (menuIds.contains(menu.getId())) {
                roleMenus.add(menu);
            }
            List<Menu> children = menu.getChildren();
            //移除children里面不在menIds集合中的元素
            children.removeIf(child -> !menuIds.contains(child.getId()));
        }
        return roleMenus;
    }

}
