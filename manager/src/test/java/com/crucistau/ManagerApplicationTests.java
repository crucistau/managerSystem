package com.crucistau;


import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crucistau.entity.Student;
import com.crucistau.service.impl.StuServiceImpl;
import com.crucistau.utils.CacheClient;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.crucistau.commons.RedisConstants.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class ManagerApplicationTests {

    @Resource
    private JavaMailSenderImpl mailSender;

    @Resource
    private StuServiceImpl stuService;
    @Resource
    private CacheClient cacheClient;
    @Test
    public void contextLoads() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("重置密码验证码");
        String s = RandomUtil.randomString(6);
        message.setText("您的验证码为："+ s);
        message.setTo("crucistau@gmail.com");
        message.setFrom("1350932647@qq.com");

        mailSender.send(message);
    }

    @Test
    public void random(){
        System.out.println(RandomUtil.randomString(6));
    }

    @Test
    public void page(){
        Student student = new Student();
        Page<Student> studentPage = new Page<>(1, 10);
        //模糊查找
        LambdaQueryWrapper<Student> lqw = new LambdaQueryWrapper();
        lqw.like(Strings.isNotEmpty(student.getStuName()), Student::getStuName, student.getStuName());
        lqw.like(Strings.isNotEmpty(student.getStuId()), Student::getStuId, student.getStuId());
        lqw.like(Strings.isNotEmpty(student.getFaculty()), Student::getFaculty, student.getFaculty());
        lqw.orderByDesc(Student::getCreatTime);
        //Page<Student> page = stuService.page(studentPage, lqw);
        //System.out.println(page);


        //使用缓存，先存储在
//        IPage page1 = cacheClient.queryListWithPassThrough(
//                CACHE_STUDENTLIST_KEY, studentPage, lqw, Page.class, (studentPage1, lqw2) -> stuService.page(studentPage1, lqw2), CACHE_STUDENT_TTL, TimeUnit.MINUTES);

        //System.out.println(page1);
    }

    @Test
    public void getPage(){
        Student student = new Student();
        Student student2 = new Student();
        student.setStuName("zhangsan");
        LambdaQueryWrapper<Student> lqw = new LambdaQueryWrapper();
        lqw.like(Strings.isNotEmpty(student.getStuName()), Student::getStuName, student.getStuName());
        lqw.like(Strings.isNotEmpty(student.getStuId()), Student::getStuId, student.getStuId());
        lqw.like(Strings.isNotEmpty(student.getFaculty()), Student::getFaculty, student.getFaculty());
        int i = lqw.hashCode();
        String s = student.toString();
        int i2 = student2.toString().hashCode();
        int i1 = s.hashCode(); //1082446593
        System.out.println(i1);
        System.out.println(i2);
        System.out.println(s);//1330912871 1527684620

    }
}
