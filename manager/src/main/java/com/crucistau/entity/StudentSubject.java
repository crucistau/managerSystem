package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2022-04-14
 */
@Getter
@Setter
@TableName("tb_student_subject")
public class StudentSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    private String studentId;

    private String subjectId;





}
