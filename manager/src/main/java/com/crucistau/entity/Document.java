package com.crucistau.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_file")
public class Document {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;//类型
    private String md5;//标识 判断是否有相同文件
    private Long size;
    private String url;//地址
    private Boolean isDelete;//假删除
    private Boolean enable;//
    private String studentName;//学生姓名
    private String studentId;//学生Id

}
