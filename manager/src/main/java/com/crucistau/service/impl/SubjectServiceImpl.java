package com.crucistau.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.commons.Constants;
import com.crucistau.commons.Result;
import com.crucistau.entity.Student;
import com.crucistau.entity.StudentSubject;
import com.crucistau.entity.Subject;
import com.crucistau.mapper.StudentMapper;
import com.crucistau.mapper.StudentSubjectMapper;
import com.crucistau.mapper.SubjectMapper;
import com.crucistau.service.ISubjectService;
import com.crucistau.utils.CacheClient;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.crucistau.commons.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-10
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements ISubjectService {

    @Resource
    private SubjectMapper subjectMapper;

    @Resource
    private StudentSubjectMapper ssm;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private CacheClient cacheClient;
    @Resource
    private StringRedisTemplate  stringRedisTemplate;

    @Override
    public IPage<Subject> getPage(Integer pageNum, Integer pageSize, Subject subject) {
        Page<Subject> subjectPage = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Subject> lqw = new LambdaQueryWrapper<>();
        //条件查询 课题名称 学院 老师ID
        lqw.like(Strings.isNotEmpty(subject.getSubjectName()), Subject::getSubjectName, subject.getSubjectName());
        lqw.like(Strings.isNotEmpty(subject.getFaculty()), Subject::getFaculty, subject.getFaculty());
        lqw.like(false, Subject::getTeacher, subject.getTeacher());

        IPage page = cacheClient.queryListWithPassThrough(
                CACHE_SUBJECT_KEY, subjectPage, subject, lqw, Page.class, this::page, CACHE_KEY_TTL, TimeUnit.HOURS);
        return page;

    }

    @Override
    public Result setSub(String subjectNum, String stuId) {
        //设置查询条件
        List<Integer> num = ssm.getNum(stuId);
        if (num.size() == 3){
            return Result.error(Constants.CODE_400,"你的三门课已经选完了");
        }
        //否则将这一门课题添加到学生课题表中
        ssm.saveSub(subjectNum, stuId);
        return Result.success(null,"成功选中");
    }


    /**
     * 通过课题号找到所选该课题的学生
     * @param subjectId
     * @return
     */
    @Override
    public List<Student> findStu(String subjectId) {
        List<Student> stu = subjectMapper.findStu(subjectId);
        return stu;
    }

    @Override
    public int setSubByStu(StudentSubject ss) {
        //先删除，再添加，然后设置状态
        LambdaQueryWrapper<StudentSubject> lqw = new LambdaQueryWrapper<>();
        //删除该门课和学生在关系表中的相关数据
        lqw.eq(StudentSubject::getSubjectId, ss.getSubjectId());
        lqw.eq(StudentSubject::getStudentId,ss.getStudentId());
        ssm.delete(lqw);
        //然后设置只有一个（即学生与课题一对一）
        ssm.insert(ss);
        //设置状态为选中
        int i = subjectMapper.setState(ss.getSubjectId());
        //设置该门课程属于该学生
        //查询到学生
        LambdaQueryWrapper<Student> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Student::getStuId, ss.getStudentId());
        Student student = studentMapper.selectOne(lqw2);
        //然后设置学生姓名
        subjectMapper.setStudent(ss.getSubjectId(),student.getStuName());
        return i;
    }

    //获取学生所选的课题
    @Override
    public List<Subject> findSub(String studentId) {
       return subjectMapper.findSub(studentId);
    }

    @Override
    public void changeSubject(Subject subject) {

        //删除缓存
        Set<String> keys = stringRedisTemplate.keys(CACHE_SUBJECT_KEY + "*");
        if ( !CollectionUtils.isEmpty(keys) ){
            stringRedisTemplate.delete(keys);
        }
        saveOrUpdate(subject);

    }


}
