package com.crucistau.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.UserDto;
import com.crucistau.entity.Student;
import com.crucistau.service.impl.MailServiceImpl;
import com.crucistau.service.impl.StuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import static com.crucistau.commons.RedisConstants.CACHE_STUDENTLIST_KEY;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StuServiceImpl ssI;
    @Resource
    private MailServiceImpl mailService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //登录验证（token、menus）
    @PostMapping("/login")
    public Result login(@RequestBody UserDto userDto){
        return ssI.login(userDto);
    }

    //新增或修改
    @PutMapping
    public Result update(@RequestBody Student stu){
        boolean flag = ssI.addStu(stu);
        return Result.info(flag,ssI.getById(stu.getId()),flag ? "修改成功":"修改失败");
    }

    //分页查找(可以添加条件 stuId faculty stuName)
    @GetMapping("/{currentPage}/{pageSize}")
    public Result getPage(@PathVariable Integer currentPage, @PathVariable Integer pageSize, Student student){
        IPage<Student> page = ssI.getPage(currentPage, pageSize, student);
        return Result.success(page);
    }


    //查询所有数据
    @GetMapping
    public Result getAll(){
        List<Student> list = ssI.list();
        return Result.success(list);
    }

    //通过指定id查询数据
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id){
        return ssI.queryById(id);
    }

    //批量删除
    @PostMapping("/batch")
    public Result deleteByIds(@RequestBody List<Integer> ids){
        boolean flag = ssI.removeByIds(ids);
        Set<String> keys = stringRedisTemplate.keys(CACHE_STUDENTLIST_KEY + "*");
        if ( !CollectionUtils.isEmpty(keys) ){
            stringRedisTemplate.delete(keys);
        }
        return Result.info(flag, "删除成功");
    }

    //根据id删除指定数据
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable  Integer id){
        boolean flag = ssI.removeById(id);
        Set<String> keys = stringRedisTemplate.keys(CACHE_STUDENTLIST_KEY + "*");
        if ( !CollectionUtils.isEmpty(keys) ){
            stringRedisTemplate.delete(keys);
        }
        return Result.info(flag, "删除成功");
    }


    /**
     * 导出接口
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws  Exception{
        List<Student> list = ssI.list();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        //自定义标题别名
        writer.addHeaderAlias("id", "id");
        writer.addHeaderAlias("stuName", "用户名");
        writer.addHeaderAlias("stuPwd", "密码");
        writer.addHeaderAlias("identity", "身份");
        writer.addHeaderAlias("stuId", "学号");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("faculty", "学院");
        writer.addHeaderAlias("creatTime", "时间");
        // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
        writer.write(list, true);

        // 设置浏览器响应的格式
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("学生信息", "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        out.close();
        writer.close();
    }



    /**
     * excel 导入
     * @param file
     * @throws Exception
     */
    @PostMapping("/import")
    public Boolean imp(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        // 1、 通过 javabean的方式读取Excel内的对象，但是要求表头必须是英文，跟javabean的属性要对应起来
        //List<Student> list = reader.readAll(Student.class);
        // 方式2：忽略表头的中文，直接读取表的内容
        List<List<Object>> list = reader.read(1);
        List<Student> users = CollUtil.newArrayList();
        for (List<Object> row : list) {
            Student user = new Student();
            user.setStuName(row.get(0).toString());
            user.setStuPwd(SecureUtil.md5(row.get(1).toString()));
            user.setStuId(row.get(2).toString());
            user.setEmail(row.get(3).toString());
            user.setFaculty(row.get(4).toString());
            users.add(user);
        }
        ssI.saveBatch(users);
        Set<String> keys = stringRedisTemplate.keys(CACHE_STUDENTLIST_KEY + "*");
        if ( !CollectionUtils.isEmpty(keys) ){
            stringRedisTemplate.delete(keys);
        }
        return true;
    }

    //发送邮件
    @PutMapping("rePassword")
    public Result resetPwd(@RequestBody UserDto userDto){
        String code = RandomUtil.randomString(6);
        mailService.sendMail(userDto.getMail(), code);
        return  Result.success(code);
    }


    //更改密码
    @PutMapping("/changePwd")
    public Result changePwd(@RequestBody UserDto userDto){
        ssI.changePwd(userDto.getUserId(), userDto.getPassword());
        return Result.success();
    }
}
