package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-10
 */
@Getter
@Setter
@TableName("tb_subject")
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 课题名称
     */
    private String subjectName;

    /**
     * 所属学院
     */
    private String faculty;

    /**
     * 负责老师id
     */
    private String teacher;

    /**
     * 教学秘书id
     */
    private String secretary;

    /**
     * 是否选中
     */
    private Boolean state;


    //查询课程对应的学生
    @TableField(exist = false)
    private List<Student> studentList;

    /**
     * 选中的哪位学生
     */
    private String student;

    @Override
    public String toString() {
        return "Subject{" +
                "subjectName='" + subjectName + '\'' +
                ", faculty='" + faculty + '\'' +
                ", teacher=" + teacher +
                '}';
    }
}
