package com.crucistau.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crucistau.commons.Result;
import com.crucistau.controller.dto.FileDto;
import com.crucistau.entity.Document;
import com.crucistau.entity.StudentSubject;
import com.crucistau.mapper.DocMapper;
import com.crucistau.mapper.StudentSubjectMapper;
import com.crucistau.service.IDocService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Document> implements IDocService {

    @Resource
    private StudentSubjectMapper studentSubjectMapper;


    /**
     * 从课题学生表中，找到学生id， 然后通过学生id查找文件表找到学生上传的文件
     * @param subId
     * @return
     */
    @Override
    public Result getDocWithSubId(String subId) {
        //获得学生Id
        String studentId = studentSubjectMapper.getStudentId(subId);
        LambdaQueryWrapper<Document> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Document::getStudentId,studentId);
        //查找该学生所上传的文件
        List<Document> list = list(lqw);

        return Result.success(list);
    }
}
