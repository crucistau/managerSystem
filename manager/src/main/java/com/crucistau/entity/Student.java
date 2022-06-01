package com.crucistau.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor//无参与有参构造
@AllArgsConstructor
@TableName("tb_stu")//指定实体类与数据库表明一致
@ToString
public class Student {
    @TableId
    private Integer id;
    private String stuName;
    @JsonIgnore//忽略展示给前端的数据
    private String stuPwd;
    @TableField("identity")//解决数据库字段名与实体类属性名不同
    private String identity;
    private String email;
    private String stuId;
    private String faculty;
    private String creatTime;

    @Override
    public String toString() {
        return "Student{" +
                "stuName='" + stuName + '\'' +
                ", stuId='" + stuId + '\'' +
                ", faculty='" + faculty + '\'' +
                '}';
    }
}
